package com.shinemo.task.listener.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.core.CommonTraceTask;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.model.SmtTsTaskDefQuery;
import com.shinemo.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.shinemo.task.enums.TaskStatusEnum;
import com.shinemo.task.listener.AceTaskSchedulerListener;
import com.shinemo.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by wuzhenhong on 10/14/21 8:47 PM
 */
@Component
@Slf4j
public class ParentTaskSchedulerTriggerListener implements AceTaskSchedulerListener {

    @Resource
    private SmtTsTaskDefWrapper smtTsTaskDefWrapper;

    @Resource
    private ExecutorService retryExecutor;

    @Autowired
    private List<AceTaskSchedulerListener> listenerList;

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
                        .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).extParams(null).retry(false).build();

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
