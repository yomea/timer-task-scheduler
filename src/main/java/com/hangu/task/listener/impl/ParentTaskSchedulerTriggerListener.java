package com.hangu.task.listener.impl;

import com.hangu.task.context.SchedulerContext;
import com.hangu.task.core.CommonTraceTask;
import com.hangu.task.dal.model.SmtTsTaskDef;
import com.hangu.task.dal.model.SmtTsTaskDefQuery;
import com.hangu.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.hangu.task.enums.TaskStatusEnum;
import com.hangu.task.listener.HanguTaskSchedulerListener;
import com.hangu.task.model.ApiResult;
import com.hangu.task.model.TaskContext;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wuzhenhong on 10/14/21 8:47 PM
 */
@Component
@Slf4j
public class ParentTaskSchedulerTriggerListener implements HanguTaskSchedulerListener {

    @Resource
    private SmtTsTaskDefWrapper smtTsTaskDefWrapper;

    @Resource
    private ExecutorService retryExecutor;

    @Autowired
    private List<HanguTaskSchedulerListener> listenerList;

    @Override
    public void failure(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

    }

    @Override
    public void exception(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

    }

    @Override
    public void success(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();
        Boolean hasChild = smtTsTaskDef.getSmcHasChild();
        //如果存在子任务，那么执行
        if(hasChild != null && hasChild) {
            SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
            query.setSmcDefPid(smtTsTaskDef.getId());
            query.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());
            List<SmtTsTaskDef> taskDefList = smtTsTaskDefWrapper.selectBy(query);
            taskDefList.stream().forEach(taskDef -> {

                TaskContext tc = TaskContext.builder().appServiceName(taskDef.getAppServiceName()).apiServiceName(taskDef.getApiServiceName())
                        .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).contextParams(null).retry(false).customerExtParams(taskDef.getSmcExt()).customerId(taskDef.getSmcCustomerId()).build();

                SchedulerContext sc = SchedulerContext.builder().listenerList(listenerList).smtTsTaskDef(taskDef).smtTsTaskRecordWrapper(schedulerContext.getSmtTsTaskRecordWrapper())
                        .smtTsTaskLockWrapper(schedulerContext.getSmtTsTaskLockWrapper()).smtTsTaskDefWrapper(smtTsTaskDefWrapper).transactionTemplate(schedulerContext.getTransactionTemplate()).build();


                CommonTraceTask task = new CommonTraceTask(tc, sc);

                //立即执行子任务
                retryExecutor.submit(task);

            });
        }
    }

    @Override
    public void timeout(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

    }
}
