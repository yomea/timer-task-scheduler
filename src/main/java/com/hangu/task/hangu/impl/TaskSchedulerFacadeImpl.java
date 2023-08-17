package com.hangu.task.hangu.impl;

import com.hangu.provider.annotation.HanguService;
import com.hangu.task.hangu.TaskSchedulerFacade;
import com.hangu.task.model.ApiResult;
import com.hangu.task.model.TimerTaskRequest;
import com.hangu.task.service.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wuzhenhong on 10/12/21 5:34 PM
 */
@HanguService
public class TaskSchedulerFacadeImpl implements TaskSchedulerFacade {

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Override
    public ApiResult<Long> submitTimerTask(TimerTaskRequest timerTaskRequest) {
        return taskSchedulerService.submitTimerTask(timerTaskRequest);
    }

    @Override
    public ApiResult timerTaskDel(Long taskId) {
        return taskSchedulerService.timerTaskDel(taskId);
    }

    @Override
    public ApiResult<Void> timerTaskDel(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        return taskSchedulerService.timerTaskDel(appServiceName, apiServiceName, apiMethodName, customerId);
    }

    @Override
    public ApiResult<Void> disableTask(Long taskId) {
        return taskSchedulerService.disableTask(taskId);
    }

    @Override
    public ApiResult<Void> disableTask(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        return null;
    }

    @Override
    public ApiResult<Void> enableTask(Long taskId) {
        return taskSchedulerService.enableTask(taskId);
    }

    @Override
    public ApiResult<Void> enableTask(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        return null;
    }

    @Override
    public ApiResult<Void> execTaskImmediately(Long taskId) {
        return taskSchedulerService.execTaskImmediately(taskId);
    }

    @Override
    public ApiResult<Void> execTaskImmediately(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        return null;
    }
}
