package com.hangu.task.core;

import com.hangu.task.model.TaskContext;

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
            
            doRun(taskContext);

            complete(taskContext);

        } catch (Exception e) {
            exception(taskContext, e);
        }
    }

    protected abstract void startExec(TaskContext taskContext);

    protected abstract void doRun(TaskContext taskContext);

    protected abstract boolean allowExec(TaskContext taskContext);

    protected abstract void complete(TaskContext taskContext);

    protected abstract void exception(TaskContext taskContext, Exception e);
}
