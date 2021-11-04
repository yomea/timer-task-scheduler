package com.xxx.task.callback;

import com.xxx.Aace.RetCode;
import com.xxx.Aace.context.AaceContext;
import com.xxx.common.tools.result.ApiResult;
import com.xxx.task.callback.AceTaskSchedulerCallback;
import com.xxx.task.constant.GlobalConfig;
import com.xxx.task.context.SchedulerContext;
import com.xxx.task.listener.AceTaskSchedulerListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by wuzhenhong on 10/13/21 5:40 PM
 */
@Slf4j
public class AceTaskSchedulerCallbackImpl implements AceTaskSchedulerCallback {

    private SchedulerContext schedulerContext;
    private List<AceTaskSchedulerListener> listenerList;

    public AceTaskSchedulerCallbackImpl(SchedulerContext schedulerContext) {
        this.listenerList = schedulerContext.getListenerList();
        this.schedulerContext = schedulerContext;
    }

    @Override
    public void onResponse(int retCode, Long taskId, AaceContext aaceContext) {

        try {
            String msg = aaceContext.getError();

            if (RetCode.RET_SUCCESS == retCode) {
                success(retCode, ApiResult.success(taskId));
            } else if (RetCode.RET_TIMEOUT == retCode) {
                timeout(retCode, ApiResult.fail(String.format("请求超时！retCode: %s", retCode), retCode));
            } else if (RetCode.RET_FAILURE == retCode) {
                exception(retCode, ApiResult.fail(String.format("调度任务失败，失败原因：%s, retCode: %s", msg, retCode), retCode));
            } else {
                failure(retCode, ApiResult.fail(String.format("调度任务失败，失败原因：%s, retCode: %s", msg, retCode), retCode));
            }
        } catch (Exception e) {
            log.error("任务回调失败！", e);
            throw e;
        }

    }

    @Override
    public Executor getExecutor() {
        return GlobalConfig.getGlobalExecutor();
    }

    private void failure(int retCode, ApiResult<Long> apiResult) {
        listenerList.stream().forEach(listener -> {
            listener.failure(schedulerContext, retCode, apiResult);
        });
    }

    private void exception(int retCode, ApiResult<Long> apiResult) {
        listenerList.stream().forEach(listener -> {
            listener.exception(schedulerContext, retCode, apiResult);
        });
    }

    private void success(int retCode, ApiResult<Long> apiResult) {
        listenerList.stream().forEach(listener -> {
            listener.success(schedulerContext, retCode, apiResult);
        });
    }

    private void timeout(int retCode, ApiResult<Long> apiResult) {
        listenerList.stream().forEach(listener -> {
            listener.timeout(schedulerContext, retCode, apiResult);
        });
    }
}
