package com.xxx.task.core;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Instant;
import java.util.Date;

/**
 * 延时触发器
 * Created by wuzhenhong on 10/15/21 10:13 AM
 */
public class DelayTrigger implements Trigger {

    private long delay;
    private volatile boolean hasExec = false;
    public DelayTrigger(long delay) {
        this.delay = delay;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {

        //如果已经执行过了，那么不再执行
        if(hasExec) {
            return null;
        }
        hasExec = true;
        return Date.from(Instant.ofEpochMilli(System.currentTimeMillis() + delay));
    }

    public boolean isHasExec() {
        return hasExec;
    }
}
