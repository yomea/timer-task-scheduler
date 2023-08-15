package com.hangu.task.service;

import com.hangu.task.model.ApiResult;
import com.hangu.task.model.TimerTaskRequest;

/**
 * Created by wuzhenhong on 10/12/21 5:37 PM
 */
public interface TaskSchedulerService {

    /**
     * 返回任务ID
     * @param timerTaskRequest
     * @return
     */
    ApiResult<Long> submitTimerTask(TimerTaskRequest timerTaskRequest);

    /**
     * 删除任务
     * @param taskId
     * @return
     */
    ApiResult<Void> timerTaskDel(Long taskId);

    /**
     *
     * @param appServiceName
     * @param apiServiceName
     * @param apiMethodName
     * @param customerId
     * @return
     */
    ApiResult<Void> timerTaskDel(String appServiceName, String apiServiceName, String apiMethodName, String customerId);

    /**
     * 禁止某定时任务
     * @param taskId
     * @return
     */
    ApiResult<Void> disableTask(Long taskId);

    /**
     *
     * @param appServiceName
     * @param apiServiceName
     * @param apiMethodName
     * @param customerId
     * @return
     */
    ApiResult<Void> disableTask(String appServiceName, String apiServiceName, String apiMethodName, String customerId);

    /**
     * 启动某定时任务
     * @param taskId
     * @return
     */
    ApiResult<Void> enableTask(Long taskId);

    ApiResult<Void> enableTask(String appServiceName, String apiServiceName, String apiMethodName, String customerId);

    /**
     * 立即调用某任务
     * @param taskId
     * @return
     */
    ApiResult<Void> execTaskImmediately(Long taskId);

    ApiResult<Void> execTaskImmediately(String appServiceName, String apiServiceName, String apiMethodName, String customerId);

    void taskRetry();

    void dealDownLineTask();

    void taskUpdateMsg();

    void cleanOldTaskUpdateMsg();
}
