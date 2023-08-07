package com.xxx.task.listener;

import com.xxx.task.context.SchedulerContext;
import com.xxx.task.model.ApiResult;

/**
 * Created by wuzhenhong on 10/14/21 8:40 PM
 */
public interface HanguTaskSchedulerListener {

    void failure(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);

    void exception(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);

    void success(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);

    void timeout(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);
}
