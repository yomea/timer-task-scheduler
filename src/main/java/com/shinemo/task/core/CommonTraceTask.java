package com.shinemo.task.core;

/**
 * Created by wuzhenhong on 10/7/21 11:41 AM
 */
public class CommonTraceTask extends TraceTask {

    // TODO: 10/7/21 引用对应的实体类

    public CommonTraceTask(Runnable runnable) {
        super(runnable);
    }

    @Override
    protected boolean beforeStart(Runnable runnable) {

        // TODO: 10/7/21 检查该任务实例是否已经存在，已存在将状态为正在执行中，如果该任务是需要依赖上一个任务完成的，那么当前任务出发之后不会执行具体的逻辑

        return true;
    }

    @Override
    protected void complete(Runnable runnable) {

        // TODO: 10/7/21 执行完成，将任务状态设置为已完成，并记录执行耗时

    }

    @Override
    protected void exception(Runnable runnable, Exception e) {
        // TODO: 10/7/21 将任务设置为执行失败，记录原因
    }
}
