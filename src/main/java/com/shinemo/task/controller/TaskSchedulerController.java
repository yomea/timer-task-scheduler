package com.shinemo.task.controller;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.model.CronTaskRequest;
import com.shinemo.task.service.TaskSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/task")
public class TaskSchedulerController {

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @PostMapping("/submitCronTask")
    @ResponseBody
    public ApiResult<Long> submitCronTask(@RequestBody CronTaskRequest cronTaskRequest) {
        return taskSchedulerService.submitCronTask(cronTaskRequest);
    }

    @PostMapping("/cronTaskDel")
    @ResponseBody
    public ApiResult<Void> cronTaskDel(Long taskId) {
        return taskSchedulerService.cronTaskDel(taskId);
    }

    @PostMapping("/disableTask")
    @ResponseBody
    public ApiResult<Void> disableTask(Long taskId) {
        return taskSchedulerService.disableTask(taskId);
    }

    @PostMapping("/enableTask")
    @ResponseBody
    public ApiResult<Void> enableTask(Long taskId) {
        return taskSchedulerService.enableTask(taskId);
    }

    @PostMapping("/execTaskImmediately")
    @ResponseBody
    public ApiResult<Void> execTaskImmediately(Long taskId) {
        return taskSchedulerService.execTaskImmediately(taskId);
    }
}
