package com.shinemo.task.utils;

import com.google.common.collect.Maps;
import com.shinemo.ace4j.Ace;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.constant.TaskSchedulerCons;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.core.*;
import com.shinemo.task.dal.model.*;
import com.shinemo.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.shinemo.task.enums.TaskExecEnum;
import com.shinemo.task.enums.TimerTypeEnum;
import com.shinemo.task.listener.AceTaskSchedulerListener;
import com.shinemo.task.model.TaskContext;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzhenhong on 10/12/21 4:55 PM
 */
public class SchedulerContextUtils {

    public static void failure(SchedulerContext schedulerContext, ApiResult<Long> apiResult) {

        SmtTsTaskRecord smtTsTaskRecord = new SmtTsTaskRecord();

        smtTsTaskRecord.setSmcStatus(TaskExecEnum.FAILURE.getStatus());
        smtTsTaskRecord.setSmcError(apiResult.getMsg());
        smtTsTaskRecord.setSmcEtime(new Date());

        doDealData(schedulerContext, smtTsTaskRecord, TaskExecEnum.FAILURE.getStatus());
    }

    public static void exception(SchedulerContext schedulerContext, ApiResult<Long> apiResult) {

        SmtTsTaskRecord smtTsTaskRecord = new SmtTsTaskRecord();

        smtTsTaskRecord.setSmcStatus(TaskExecEnum.FAILURE.getStatus());
        smtTsTaskRecord.setSmcEtime(new Date());
        smtTsTaskRecord.setSmcError(apiResult.getMsg());

        doDealData(schedulerContext, smtTsTaskRecord, TaskExecEnum.FAILURE.getStatus());
    }

    public static void success(SchedulerContext schedulerContext, ApiResult<Long> apiResult) {

        SmtTsTaskRecord smtTsTaskRecord = new SmtTsTaskRecord();

        smtTsTaskRecord.setSmcStatus(TaskExecEnum.FINISHED.getStatus());
        smtTsTaskRecord.setSmcEtime(new Date());

        doDealData(schedulerContext, smtTsTaskRecord, TaskExecEnum.FINISHED.getStatus());
    }

    public static void timeout(SchedulerContext schedulerContext, ApiResult<Long> apiResult) {

        SmtTsTaskRecord smtTsTaskRecord = new SmtTsTaskRecord();

        smtTsTaskRecord.setSmcStatus(TaskExecEnum.EXEC_TIMEOUT.getStatus());
        smtTsTaskRecord.setSmcEtime(new Date());
        smtTsTaskRecord.setSmcError(apiResult.getMsg());

        doDealData(schedulerContext, smtTsTaskRecord, TaskExecEnum.EXEC_TIMEOUT.getStatus());
    }

    private static void doDealData(SchedulerContext schedulerContext, SmtTsTaskRecord taskRecordParam, Integer lockStatus) {

        SmtTsTaskLockWrapper smtTsTaskLockWrapper = schedulerContext.getSmtTsTaskLockWrapper();

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();

        SmtTsTaskLockQuery query = new SmtTsTaskLockQuery();
        query.setSmcDefId(smtTsTaskDef.getId());
        SmtTsTaskLock smtTsTaskLock = smtTsTaskLockWrapper.getBy(query);
        SmtTsTaskRecord smtTsTaskRecord = SchedulerContextUtils.getRecord(schedulerContext);

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        TransactionTemplate transactionTemplate = schedulerContext.getTransactionTemplate();

        transactionTemplate.execute(status -> {

            if(smtTsTaskRecord != null) {

                smtTsTaskRecord.setSmcStatus(taskRecordParam.getSmcStatus());
                smtTsTaskRecord.setSmcError(taskRecordParam.getSmcError());
                smtTsTaskRecord.setSmcEtime(taskRecordParam.getSmcEtime());
                smtTsTaskRecord.setSmcDesc(taskRecordParam.getSmcDesc());

                //更新状态
                smtTsTaskRecordWrapper.updateById(smtTsTaskRecord);
            }

            if(smtTsTaskLock != null) {
                smtTsTaskLock.setSmcStatus(lockStatus);
                smtTsTaskLockWrapper.updateById(smtTsTaskLock);
            }

            return null;
        });
    }

    private static SmtTsTaskRecord getRecord(SchedulerContext schedulerContext) {

        Map<String, Object> extParams = schedulerContext.getExtParams();
        SmtTsTaskRecord record = (SmtTsTaskRecord)extParams.get(TaskSchedulerCons.TASK_RECORD);
        if(record == null || record.getId() == null) {
            return null;
        }
        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        SmtTsTaskRecordQuery query = new SmtTsTaskRecordQuery();
        query.setId(record.getId());
        query.setSplitKey(record.getSplitKey());

        SmtTsTaskRecord smtTsTaskRecord = smtTsTaskRecordWrapper.getBy(query);
        return smtTsTaskRecord;
    }

    public static void newRecord(TaskContext taskContext, SchedulerContext schedulerContext) {

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();

        SmtTsTaskRecord smtTsTaskRecord = new SmtTsTaskRecord();
        smtTsTaskRecord.setSmcDefId(smtTsTaskDef.getId());
        smtTsTaskRecord.setSmcTimeout(smtTsTaskDef.getSmcTimeout());
        smtTsTaskRecord.setSmcTaskName(smtTsTaskDef.getSmcTaskName());
        smtTsTaskRecord.setSmcStime(new Date());
        smtTsTaskRecord.setSmcStatus(TaskExecEnum.EXEC_ING.getStatus());
        smtTsTaskRecord.setSmcIp(Ace.get().getLocalHost());
        smtTsTaskRecord.setSmcDesc(taskContext.isRetry() ? "该任务为重试调度线程调度" : null);

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        int row = smtTsTaskRecordWrapper.insertSelective(smtTsTaskRecord);

        if(row > 0) {
            Map<String, Object> extParams = schedulerContext.getExtParams();
            if(extParams == null) {
                extParams = Maps.newHashMap();
                schedulerContext.setExtParams(extParams);
            }

            extParams.put(TaskSchedulerCons.TASK_RECORD, smtTsTaskRecord);
        }

    }

    public static void schedulerTask(List<AceTaskSchedulerListener> listenerList, ScheduledTaskRegistrar taskRegistrar, TransactionTemplate transactionTemplate, SmtTsTaskLockWrapper smtTsTaskLockWrapper, SmtTsTaskRecordWrapper smtTsTaskRecordWrapper, SmtTsTaskDefWrapper smtTsTaskDefWrapper, SmtTsTaskDef taskDef, SmtTsTaskTimer timer) {

        TaskContext taskContext = TaskContext.builder().appServiceName(taskDef.getAppServiceName()).apiServiceName(taskDef.getApiServiceName())
                .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).extParams(null).customerExtParams(taskDef.getSmcExt()).build();


        SchedulerContext schedulerContext = SchedulerContext.builder().listenerList(listenerList).smtTsTaskDef(taskDef).smtTsTaskRecordWrapper(smtTsTaskRecordWrapper)
                .smtTsTaskLockWrapper(smtTsTaskLockWrapper).smtTsTaskDefWrapper(smtTsTaskDefWrapper).transactionTemplate(transactionTemplate).build();

        CommonTraceTask task = new CommonTraceTask(taskContext, schedulerContext);

        Integer timerType = timer.getSmcTimerType();
        ScheduledTask scheduledTask;
        if(TimerTypeEnum.CRON.getType().equals(timerType)) {
            scheduledTask = scheduleCronTask(taskRegistrar, timer, task);
        } else if(TimerTypeEnum.DELAY_TRIGGER.getType().equals(timerType)) {
            scheduledTask = scheduleDelayTriggerTask(taskRegistrar, timer, task);
        } else if(TimerTypeEnum.FIX_DELAY.getType().equals(timerType)) {
            scheduledTask = scheduleFixDelayTriggerTask(taskRegistrar, timer, task);
        } else if(TimerTypeEnum.FIX_RATE.getType().equals(timerType)) {
            scheduledTask = scheduleFixRateTriggerTask(taskRegistrar, timer, task);
        } else {
            throw new ApiException(String.format("不支持的定时器类型 %s", timerType), 500);
        }

        TaskMemoryStore.putScheduledTask(taskDef.getId(), scheduledTask);
    }

    private static ScheduledTask scheduleFixRateTriggerTask(ScheduledTaskRegistrar taskRegistrar, SmtTsTaskTimer timer, CommonTraceTask task) {

        LimitPeriodicTrigger fixDelayTrigger = new LimitPeriodicTrigger(timer.getSmcPeriod(), timer.getSmcStartDay(), timer.getSmcEndDay());
        fixDelayTrigger.setFixedRate(true);
        TriggerTask triggerTask = new TriggerTask(task, fixDelayTrigger);

        ScheduledTask scheduledTask = taskRegistrar.scheduleTriggerTask(triggerTask);

        return scheduledTask;
    }

    private static ScheduledTask scheduleFixDelayTriggerTask(ScheduledTaskRegistrar taskRegistrar, SmtTsTaskTimer timer, CommonTraceTask task) {

        LimitPeriodicTrigger fixDelayTrigger = new LimitPeriodicTrigger(timer.getSmcPeriod(), timer.getSmcStartDay(), timer.getSmcEndDay());
        fixDelayTrigger.setFixedRate(false);
        TriggerTask triggerTask = new TriggerTask(task, fixDelayTrigger);

        ScheduledTask scheduledTask = taskRegistrar.scheduleTriggerTask(triggerTask);

        return scheduledTask;
    }

    private static ScheduledTask scheduleDelayTriggerTask(ScheduledTaskRegistrar taskRegistrar, SmtTsTaskTimer timer, CommonTraceTask task) {

        DelayTrigger delayTrigger = new DelayTrigger(timer.getSmcOnceDelay());

        TriggerTask triggerTask = new TriggerTask(task, delayTrigger);

        ScheduledTask scheduledTask = taskRegistrar.scheduleTriggerTask(triggerTask);

        return scheduledTask;
    }

    private static ScheduledTask scheduleCronTask(ScheduledTaskRegistrar taskRegistrar, SmtTsTaskTimer timer, CommonTraceTask task) {

        LimitCronTrigger cronTrigger = new LimitCronTrigger(timer.getSmcCron(), timer.getSmcStartDay(), timer.getSmcEndDay());
        CronTask cronTask = new LimitCronTask(task, cronTrigger);

        ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask(cronTask);

        return scheduledTask;
    }
}
