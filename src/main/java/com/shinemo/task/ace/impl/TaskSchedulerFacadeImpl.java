package com.shinemo.task.ace.impl;

import com.shinemo.ace4j.spring.AceProvider;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.ace.TaskSchedulerFacade;
import com.shinemo.task.model.CronTaskRequest;
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
    public ApiResult<Long> submitCronTask(CronTaskRequest cronTaskRequest) {
        return taskSchedulerService.submitCronTask(cronTaskRequest);
    }

    @Override
    public ApiResult cronTaskDel(Long taskId) {
        return taskSchedulerService.cronTaskDel(taskId);
    }
}
