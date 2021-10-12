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

    ApiResult<Void> cronTaskDel(Long taskId);

    void taskRetry();

    void dealDownLineTask();

    void taskUpdateMsg();
}
