package com.shinemo.task.core;

import com.shinemo.Aace.context.AaceContext;
import com.shinemo.ace4j.Ace;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.ace.TaskSchedulerWorker;
import com.shinemo.task.callback.TaskSchedulerCallback;
import com.shinemo.task.callback.TaskSchedulerCallbackImpl;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.model.SmtTsTaskLock;
import com.shinemo.task.dal.model.SmtTsTaskLockQuery;
import com.shinemo.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.shinemo.task.enums.TaskExecEnum;
import com.shinemo.task.model.TaskContext;
import com.shinemo.task.utils.SchedulerContextUtils;
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
    protected ApiResult<Long> doRun(TaskContext taskContext) {

        String appServiceName = taskContext.getAppServiceName();

        TaskSchedulerWorker worker = TaskHandlerBeanFactory.getWorker(appServiceName);
        if(worker == null) {
            log.error("应用名（appServiceName）为 {} 的服务不在线！", appServiceName);
            return ApiResult.fail(String.format("应用名（appServiceName）为 %s 的服务不在线！", appServiceName), 500);
        }

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();

        AaceContext aaceContext = new AaceContext();
        Long timeout = smtTsTaskDef.getSmcTimeout();
        timeout = timeout == null || timeout == -1L ? 5000 : timeout;
        //设置超时时间
        aaceContext.setTimeout(timeout.intValue());

        TaskSchedulerCallback callback = new TaskSchedulerCallbackImpl(schedulerContext);

        return worker.asyncDealTask(taskContext, callback, aaceContext);
    }

    @Override
    protected boolean allowExec(TaskContext taskContext) {

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
            lock.setSmcIp(Ace.get().getLocalHost());
            lock.setSmcTimeOut(smtTsTaskDef.getSmcTimeout());

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


        int row = smtTsTaskLockWrapper.updateStatusByOldStatus(smtTsTaskLock.getId(), TaskExecEnum.EXEC_ING.getStatus(), status);
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
    protected void complete(ApiResult<Long> apiResult, TaskContext taskContext) {

        SchedulerContextUtils.success(schedulerContext);
    }

    @Override
    protected void exception(TaskContext taskContext, Exception e) {

        SchedulerContextUtils.exception(schedulerContext, e);
    }


}
