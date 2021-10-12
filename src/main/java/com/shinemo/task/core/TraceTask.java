package com.shinemo.task.core;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.model.TaskContext;

/**
 * Created by wuzhenhong on 10/7/21 11:31 AM
 */
public abstract class TraceTask implements Runnable {

    private TaskContext taskContext;

    public TraceTask(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public void run() {

        try {
            if(!this.allowExec(taskContext)) {
                return;
            }
            
            startExec(taskContext);
            
            ApiResult<Long> apiResult = doRun(taskContext);

            complete(apiResult, taskContext);

        } catch (Exception e) {
            exception(taskContext, e);
        }
    }

    protected abstract void startExec(TaskContext taskContext);

    protected abstract ApiResult<Long> doRun(TaskContext taskContext);

    protected abstract boolean allowExec(TaskContext taskContext);

    protected abstract void complete(ApiResult<Long> apiResult, TaskContext taskContext);

    protected abstract void exception(TaskContext taskContext, Exception e);
}
