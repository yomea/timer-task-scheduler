package com.shinemo.task.core;

import org.springframework.scheduling.config.CronTask;

/**
 * Created by wuzhenhong on 10/7/21 4:10 PM
 */
public class LimitCronTask extends CronTask {

    public LimitCronTask(Runnable runnable, LimitCronTrigger cronTrigger) {
        super(runnable, cronTrigger);
    }
}
