package com.hangu.task.controller;

import com.hangu.task.core.TaskMemoryStore;
import com.hangu.task.model.ApiResult;
import com.hangu.task.model.TimerTaskRequest;
import com.hangu.task.service.TaskSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * curl 'http://127.0.0.1/task-scheduler/task/getExecTaskList'
     * @return
     */
    @GetMapping("/getExecTaskList")
    @ResponseBody
    public ApiResult getExecTaskList() {

        return ApiResult.success(TaskMemoryStore.getDefIdMapScheduleTask());
    }

    /**
     * curl 'http://127.0.0.1/task-scheduler/task/getTaskLock'
     * @return
     */
    @GetMapping("/getTaskLock")
    @ResponseBody
    public ApiResult getTaskLock() {

        return ApiResult.success(TaskMemoryStore.getLockMap());
    }

    /**
     * curl 'http://127.0.0.1/task-scheduler/task/cancelInvaildTask'
     * @return
     */
    @GetMapping("/cancelInvaildTask")
    @ResponseBody
    public ApiResult cancelInvaildTask() {

        TaskMemoryStore.cancelInvaildTask();

        return ApiResult.success(null);
    }


}
