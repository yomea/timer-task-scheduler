package com.xxx.task.listener.impl;

import cn.hutool.json.JSONUtil;
import com.xxx.task.context.SchedulerContext;
import com.xxx.task.dal.model.SmtTsTaskDef;
import com.xxx.task.listener.HanguTaskSchedulerListener;
import com.xxx.task.model.ApiResult;
import com.xxx.task.utils.SchedulerContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by wuzhenhong on 10/14/21 8:43 PM
 */
@Component
@Slf4j
public class TaskSchedulerRecordListener implements HanguTaskSchedulerListener {

    public void failure(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {
        printErrMsg(schedulerContext, retCode, apiResult);
        SchedulerContextUtils.failure(schedulerContext, apiResult);
    }

    public void exception(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {
        printErrMsg(schedulerContext, retCode, apiResult);
        SchedulerContextUtils.exception(schedulerContext, apiResult);
    }

    public void success(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {
        SchedulerContextUtils.success(schedulerContext, apiResult);
    }

    public void timeout(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {
        printErrMsg(schedulerContext, retCode, apiResult);
        SchedulerContextUtils.timeout(schedulerContext, apiResult);
    }

    private void printErrMsg(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {
        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();
        log.error("定时任务 =》{} 执行失败，ace返回编码为 {}, 业务编码为 {}", JSONUtil.toJsonStr(smtTsTaskDef), retCode, apiResult.getCode());
    }
}
