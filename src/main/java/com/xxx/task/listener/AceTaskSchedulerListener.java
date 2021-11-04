package com.xxx.task.listener;

import com.xxx.common.tools.result.ApiResult;
import com.xxx.task.context.SchedulerContext;

/**
 * Created by wuzhenhong on 10/14/21 8:40 PM
 */
public interface AceTaskSchedulerListener {

    void failure(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);

    void exception(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);

    void success(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);

    void timeout(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult);
}
