package com.shinemo.task.callback;

import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.utils.SchedulerContextUtils;
import com.shinemo.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wuzhenhong on 10/12/21 4:29 PM
 */
@Slf4j
public class TaskSchedulerCallbackImpl implements TaskSchedulerCallback {

    private SchedulerContext schedulerContext;

    public TaskSchedulerCallbackImpl(SchedulerContext schedulerContext) {
        this.schedulerContext = schedulerContext;
    }

    @Override
    public void failure(int retCode) {
        printErrMsg(retCode);
        SchedulerContextUtils.failure(schedulerContext);
    }

    @Override
    public void exception(int retCode, Throwable e) {
        printErrMsg(retCode, e);
        SchedulerContextUtils.exception(schedulerContext, e);
    }

    @Override
    public void success(int retCode) {
        SchedulerContextUtils.success(schedulerContext);
    }

    @Override
    public void timeout(int retCode) {
        printErrMsg(retCode);
        SchedulerContextUtils.timeout(schedulerContext);
    }

    private void printErrMsg(int retCode) {
        printErrMsg(retCode, null);
    }
    private void printErrMsg(int retCode, Throwable e) {
        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();
        if(e != null) {
            log.error(String.format("定时任务 =》%s 执行失败，ace返回编码为 %s", GsonUtil.toJson(smtTsTaskDef), retCode), e);
        } else {
            log.error("定时任务 =》{} 执行失败，ace返回编码为 {}", GsonUtil.toJson(smtTsTaskDef), retCode);
        }
    }
}
