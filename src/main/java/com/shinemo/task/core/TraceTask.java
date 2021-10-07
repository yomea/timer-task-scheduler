package com.shinemo.task.core;

/**
 * Created by wuzhenhong on 10/7/21 11:31 AM
 */
public abstract class TraceTask implements Runnable {

    private Runnable runnable;

    public TraceTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {



        try {
            if(!this.beforeStart(this.runnable)) {
                return;
            }

            this.runnable.run();

            complete(this.runnable);

        } catch (Exception e) {
            exception(this.runnable, e);
        }
    }

    protected abstract boolean beforeStart(Runnable runnable);

    protected abstract void complete(Runnable runnable);

    protected abstract void exception(Runnable runnable, Exception e);
}
