package com.shinemo.task.service;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.model.CronTaskRequest;

/**
 * Created by wuzhenhong on 10/12/21 5:37 PM
 */
public interface TaskSchedulerService {

    /**
     * 返回任务ID
     * @param cronTaskRequest
     * @return
     */
    ApiResult<Long> submitCronTask(CronTaskRequest cronTaskRequest);

    /**
     * 删除任务
     * @param taskId
     * @return
     */
    ApiResult<Void> cronTaskDel(Long taskId);

    /**
     * 禁止某定时任务
     * @param taskId
     * @return
     */
    ApiResult<Void> disableTask(Long taskId);

    /**
     * 启动某定时任务
     * @param taskId
     * @return
     */
    ApiResult<Void> enableTask(Long taskId);

    /**
     * 立即调用某任务
     * @param taskId
     * @return
     */
    ApiResult<Void> execTaskImmediately(Long taskId);

    void taskRetry();

    void dealDownLineTask();

    void taskUpdateMsg();

    void cleanOldTaskUpdateMsg();
}
