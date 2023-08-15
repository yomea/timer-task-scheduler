package com.hangu.task.core;

import org.springframework.scheduling.config.CronTask;

import java.util.Date;

/**
 * Created by wuzhenhong on 10/7/21 4:10 PM
 */
public class LimitCronTask extends CronTask {

    private LimitCronTrigger cronTrigger;

    public LimitCronTask(Runnable runnable, LimitCronTrigger cronTrigger) {
        super(runnable, cronTrigger);
        this.cronTrigger = cronTrigger;
    }

    public Date getLimitStart() {
        return cronTrigger.getLimitStart();
    }

    public Date getLimitEnd() {
        return cronTrigger.getLimitEnd();
    }
}
