package com.xxx.task.core;

import com.hanggu.common.manager.HanguRpcManager;
import com.hanggu.consumer.callback.RpcResponseCallback;
import com.xxx.Aace.context.AaceContext;
import com.xxx.task.callback.HanguTaskSchedulerCallbackImpl;
import com.xxx.task.constant.TaskSchedulerCons;
import com.xxx.task.context.SchedulerContext;
import com.xxx.task.dal.model.SmtTsTaskDef;
import com.xxx.task.dal.model.SmtTsTaskLock;
import com.xxx.task.dal.model.SmtTsTaskLockQuery;
import com.xxx.task.dal.model.SmtTsTaskRecord;
import com.xxx.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.xxx.task.enums.TaskExecEnum;
import com.xxx.task.hangu.TaskSchedulerWorker;
import com.xxx.task.model.ApiResult;
import com.xxx.task.model.TaskContext;
import com.xxx.task.utils.SchedulerContextUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wuzhenhong on 10/7/21 11:41 AM
 */
@Slf4j
public class CommonTraceTask extends TraceTask {

    private SchedulerContext schedulerContext;

    public CommonTraceTask(TaskContext taskContext, SchedulerContext schedulerContext) {
        super(taskContext);
        this.schedulerContext = schedulerContext;
    }

    @Override
    protected void doRun(TaskContext taskContext) {

        //如果被中断,那么不执行下面的逻辑并清除中断标志位
        if(Thread.interrupted()) {
            return;
        }

        Map<String, Object> extParams = taskContext.getContextParams();
        if(extParams != null) {
            SmtTsTaskRecord record = (SmtTsTaskRecord)extParams.get(TaskSchedulerCons.TASK_RECORD);
            if(record != null) {
                taskContext.setTaskExecInsId(record.getId());
            }
        }

        String appServiceName = taskContext.getAppServiceName();

        TaskSchedulerWorker worker = TaskHandlerBeanFactory.getWorker(appServiceName);
        if(worker == null) {
            log.error("应用名（appServiceName）为 {} 的服务不在线！", appServiceName);
            return;
        }

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();

        AaceContext aaceContext = new AaceContext();
        Long timeout = smtTsTaskDef.getSmcTimeout();
        timeout = timeout == null || timeout == -1L ? 5000 : timeout;
        //设置超时时间
        aaceContext.setTimeout(timeout.intValue());

        RpcResponseCallback callback = new HanguTaskSchedulerCallbackImpl(schedulerContext);
        //透传调度该任务的机器 IP
        taskContext.setScheduleIp(HanguRpcManager.getLocalHost().getHost());
        worker.asyncDealTask(taskContext, callback, aaceContext);
    }

    @Override
    protected boolean allowExec(TaskContext taskContext) {

        //如果被中断,那么不执行下面的逻辑并清除中断标志位
        if(Thread.interrupted()) {
            return false;
        }

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();

        //获取分布式锁
        SmtTsTaskLockWrapper smtTsTaskLockWrapper = schedulerContext.getSmtTsTaskLockWrapper();

        SmtTsTaskLockQuery query = new SmtTsTaskLockQuery();
        query.setSmcDefId(smtTsTaskDef.getId());
        SmtTsTaskLock smtTsTaskLock = smtTsTaskLockWrapper.getBy(query);

        if(smtTsTaskLock == null) {
            SmtTsTaskLock lock = new SmtTsTaskLock();
            lock.setSmcDefId(smtTsTaskDef.getId());
            lock.setSmcStatus(TaskExecEnum.EXEC_ING.getStatus());
            lock.setSmcIp(HanguRpcManager.getLocalHost().getHost());
            lock.setSmcTimeOut(smtTsTaskDef.getSmcTimeout());
            lock.setSmcStartTime(System.currentTimeMillis());

            Integer row = smtTsTaskLockWrapper.lock(lock);
            if(row > 0) {
                return true;
            }
            smtTsTaskLock = smtTsTaskLockWrapper.getBy(query);
            if(smtTsTaskLock == null) {
                return false;
            }
        }

        Integer status = smtTsTaskLock.getSmcStatus();
        //还在执行中，不做处理
        if(TaskExecEnum.EXEC_ING.getStatus().equals(status)) {
            return false;
        }

        int row = smtTsTaskLockWrapper.updateStatusByOldStatus(System.currentTimeMillis(), smtTsTaskLock.getId(), HanguRpcManager.getLocalHost().getHost(), TaskExecEnum.EXEC_ING.getStatus(), status);
        if(row > 0) {
            return true;
        }

        return false;
    }

    @Override
    protected void startExec(TaskContext taskContext) {

        SchedulerContextUtils.newRecord(taskContext, schedulerContext);
    }

    @Override
    protected void complete(TaskContext taskContext) {

//        SchedulerContextUtils.success(schedulerContext);
    }

    @Override
    protected void exception(TaskContext taskContext, Exception e) {
        log.error("调度任务失败！", e);
        SchedulerContextUtils.exception(schedulerContext, ApiResult.fail(e.getMessage(), 500));
    }


}
