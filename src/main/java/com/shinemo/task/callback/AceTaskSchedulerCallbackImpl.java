package com.shinemo.task.callback;

import com.shinemo.Aace.RetCode;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.utils.SchedulerContextUtils;
import com.shinemo.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wuzhenhong on 10/13/21 5:40 PM
 */
@Slf4j
public class AceTaskSchedulerCallbackImpl implements AceTaskSchedulerCallback {

    private SchedulerContext schedulerContext;

    public AceTaskSchedulerCallbackImpl(SchedulerContext schedulerContext) {
        this.schedulerContext = schedulerContext;
    }

    @Override
    public void onResponse(int retCode, Long taskId, AaceContext aaceContext) {

        String msg = aaceContext.getError();

        if(RetCode.RET_SUCCESS == retCode) {
            success(retCode, ApiResult.success(taskId));
        } else if(RetCode.RET_TIMEOUT == retCode) {
            timeout(retCode, ApiResult.fail("请求超时！", retCode));
        } else if(RetCode.RET_FAILURE == retCode) {
            exception(retCode, ApiResult.fail(msg, retCode));
        } else {
            failure(retCode, ApiResult.fail(msg, retCode));
        }

    }

    private void failure(int retCode, ApiResult<Long> apiResult) {
        printErrMsg(retCode, apiResult);
        SchedulerContextUtils.failure(schedulerContext, apiResult);
    }

    private void exception(int retCode, ApiResult<Long> apiResult) {
        printErrMsg(retCode, apiResult);
        SchedulerContextUtils.exception(schedulerContext, apiResult);
    }

    private void success(int retCode, ApiResult<Long> apiResult) {
        SchedulerContextUtils.success(schedulerContext, apiResult);
    }

    private void timeout(int retCode, ApiResult<Long> apiResult) {
        printErrMsg(retCode, apiResult);
        SchedulerContextUtils.timeout(schedulerContext, apiResult);
    }

    private void printErrMsg(int retCode, ApiResult<Long> apiResult) {
        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();
        log.error("定时任务 =》{} 执行失败，ace返回编码为 {}, 业务编码为 {}", GsonUtil.toJson(smtTsTaskDef), retCode, apiResult.getCode());
    }
}
