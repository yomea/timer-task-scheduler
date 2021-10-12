package com.shinemo.task.utils;

import com.google.common.collect.Maps;
import com.shinemo.ace4j.Ace;
import com.shinemo.task.constant.TaskSchedulerCons;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.model.SmtTsTaskRecord;
import com.shinemo.task.dal.model.SmtTsTaskRecordQuery;
import com.shinemo.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.shinemo.task.enums.TaskExecEnum;

import java.util.Date;
import java.util.Map;

/**
 * Created by wuzhenhong on 10/12/21 4:55 PM
 */
public class SchedulerContextUtils {

    public static void failure(SchedulerContext schedulerContext) {

        SmtTsTaskRecord smtTsTaskRecord = SchedulerContextUtils.getRecord(schedulerContext);
        if(smtTsTaskRecord == null) {
            return;
        }

        smtTsTaskRecord.setSmcStatus(TaskExecEnum.FAILURE.getStatus());
        smtTsTaskRecord.setSmcError("执行失败！");
        smtTsTaskRecord.setSmcEtime(new Date());

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        smtTsTaskRecordWrapper.updateById(smtTsTaskRecord);
    }

    public static void exception(SchedulerContext schedulerContext, Throwable e) {

        SmtTsTaskRecord smtTsTaskRecord = SchedulerContextUtils.getRecord(schedulerContext);
        if(smtTsTaskRecord == null) {
            return;
        }
        smtTsTaskRecord.setSmcError(e.getMessage());
        smtTsTaskRecord.setSmcStatus(TaskExecEnum.FAILURE.getStatus());
        smtTsTaskRecord.setSmcEtime(new Date());

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        smtTsTaskRecordWrapper.updateById(smtTsTaskRecord);
    }

    public static void success(SchedulerContext schedulerContext) {

        SmtTsTaskRecord smtTsTaskRecord = SchedulerContextUtils.getRecord(schedulerContext);
        if(smtTsTaskRecord == null) {
            return;
        }
        smtTsTaskRecord.setSmcStatus(TaskExecEnum.FINISHED.getStatus());
        smtTsTaskRecord.setSmcEtime(new Date());

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        smtTsTaskRecordWrapper.updateById(smtTsTaskRecord);
    }

    public static void timeout(SchedulerContext schedulerContext) {

        SmtTsTaskRecord smtTsTaskRecord = SchedulerContextUtils.getRecord(schedulerContext);
        if(smtTsTaskRecord == null) {
            return;
        }
        smtTsTaskRecord.setSmcStatus(TaskExecEnum.EXEC_TIMEOUT.getStatus());
        smtTsTaskRecord.setSmcEtime(new Date());
        smtTsTaskRecord.setSmcError("执行任务超时！");

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        smtTsTaskRecordWrapper.updateById(smtTsTaskRecord);
    }

    private static SmtTsTaskRecord getRecord(SchedulerContext schedulerContext) {

        Map<String, Object> extParams = schedulerContext.getExtParams();
        Long recordId = (Long)extParams.get(TaskSchedulerCons.TASK_RECORD_ID);
        if(recordId == null) {
            return null;
        }
        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        SmtTsTaskRecordQuery query = new SmtTsTaskRecordQuery();
        query.setId(recordId);

        SmtTsTaskRecord smtTsTaskRecord = smtTsTaskRecordWrapper.getBy(query);
        return smtTsTaskRecord;
    }

    public static void newRecord(SchedulerContext schedulerContext) {

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();

        SmtTsTaskRecord smtTsTaskRecord = new SmtTsTaskRecord();
        smtTsTaskRecord.setSmcDefId(smtTsTaskDef.getId());
        smtTsTaskRecord.setSmcTimeout(smtTsTaskDef.getSmcTimeout());
        smtTsTaskRecord.setSmcTaskName(smtTsTaskDef.getSmcTaskName());
        smtTsTaskRecord.setSmcStime(new Date());
        smtTsTaskRecord.setSmcStatus(TaskExecEnum.EXEC_ING.getStatus());
        smtTsTaskRecord.setSmcIp(Ace.get().getLocalHost());

        SmtTsTaskRecordWrapper smtTsTaskRecordWrapper = schedulerContext.getSmtTsTaskRecordWrapper();

        int row = smtTsTaskRecordWrapper.insertSelective(smtTsTaskRecord);

        if(row > 0) {
            Map<String, Object> extParams = schedulerContext.getExtParams();
            if(extParams == null) {
                extParams = Maps.newHashMap();
                schedulerContext.setExtParams(extParams);
            }

            extParams.put(TaskSchedulerCons.TASK_RECORD_ID, smtTsTaskRecord.getId());
        }

    }
}
