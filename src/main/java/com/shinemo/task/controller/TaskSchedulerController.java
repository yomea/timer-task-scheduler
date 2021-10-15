package com.shinemo.task.controller;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.model.TimerTaskRequest;
import com.shinemo.task.service.TaskSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequestMapping("/task")
public class TaskSchedulerController {

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @PostMapping("/submitTimerTask")
    @ResponseBody
    public ApiResult<Long> submitTimerTask(@RequestBody TimerTaskRequest timerTaskRequest) {
        return taskSchedulerService.submitTimerTask(timerTaskRequest);
    }

    @PostMapping("/timerTaskDel")
    @ResponseBody
    public ApiResult<Void> timerTaskDel(Long taskId) {
        return taskSchedulerService.timerTaskDel(taskId);
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
