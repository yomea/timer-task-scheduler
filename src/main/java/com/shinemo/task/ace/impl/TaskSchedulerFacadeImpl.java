package com.shinemo.task.ace.impl;

import com.shinemo.ace4j.spring.AceProvider;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.ace.TaskSchedulerFacade;
import com.shinemo.task.model.TimerTaskRequest;
import com.shinemo.task.service.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wuzhenhong on 10/12/21 5:34 PM
 */
@AceProvider
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
    public ApiResult<Void> disableTask(Long taskId) {
        return taskSchedulerService.disableTask(taskId);
    }

    @Override
    public ApiResult<Void> enableTask(Long taskId) {
        return taskSchedulerService.enableTask(taskId);
    }

    @Override
    public ApiResult<Void> execTaskImmediately(Long taskId) {
        return taskSchedulerService.execTaskImmediately(taskId);
    }
}
