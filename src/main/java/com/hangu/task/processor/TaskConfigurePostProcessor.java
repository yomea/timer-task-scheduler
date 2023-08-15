package com.hangu.task.processor;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Created by wuzhenhong on 10/14/21 12:06 PM
 */
public interface TaskConfigurePostProcessor {

    void beforeConfigure(ScheduledTaskRegistrar taskRegistrar);

    void afterConfigure(ScheduledTaskRegistrar taskRegistrar);
}
