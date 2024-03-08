package com.hangu.task.callback;

import com.hangu.rpc.common.callback.RpcResponseCallback;
import com.hangu.rpc.common.entity.RpcResult;
import com.hangu.rpc.common.enums.ErrorCodeEnum;
import com.hangu.task.context.SchedulerContext;
import com.hangu.task.listener.HanguTaskSchedulerListener;
import com.hangu.task.model.ApiResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wuzhenhong on 10/13/21 5:40 PM
 */
@Slf4j
public class HanguTaskSchedulerCallbackImpl implements RpcResponseCallback {

    private SchedulerContext schedulerContext;
    private List<HanguTaskSchedulerListener> listenerList;

    public HanguTaskSchedulerCallbackImpl(SchedulerContext schedulerContext) {
        this.listenerList = schedulerContext.getListenerList();
        this.schedulerContext = schedulerContext;
    }

    @Override
    public void callback(RpcResult rpcResult) {

        try {
            int retCode = rpcResult.getCode();

            if (ErrorCodeEnum.SUCCESS.getCode() == retCode) {
                Long taskId = (Long) rpcResult.getResult();
                success(retCode, ApiResult.success(taskId));
            } else if (ErrorCodeEnum.TIME_OUT.getCode() == retCode) {
                timeout(retCode, ApiResult.fail(String.format("请求超时！retCode: %s", retCode), retCode));
            } else if (ErrorCodeEnum.FAILURE.getCode() == retCode) {
                exception(retCode, ApiResult.fail(String.format("调度任务失败，失败原因：%s, retCode: %s",
                    this.dealExceptionToMsg(rpcResult.getResult()), retCode), retCode));
            } else {
                failure(retCode, ApiResult.fail(String.format("调度任务失败，失败原因：%s, retCode: %s",
                    this.dealExceptionToMsg(rpcResult.getResult()), retCode), retCode));
            }
        } catch (Exception e) {
            log.error("任务回调失败！", e);
            throw e;
        }

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

    private String dealExceptionToMsg(Object result) {
        if (result instanceof Throwable) {
            return ((Throwable) result).getMessage();
        }
        return "";
    }
}
