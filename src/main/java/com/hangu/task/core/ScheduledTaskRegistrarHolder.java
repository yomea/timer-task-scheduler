package com.hangu.task.core;

import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by wuzhenhong on 10/7/21 2:18 PM
 */
public class ScheduledTaskRegistrarHolder {

    private static Date APP_START_TIME;
    private static ScheduledTaskRegistrar scheduledTaskRegistrar;

    public static ScheduledTaskRegistrar getScheduledTaskRegistrar() {
        return scheduledTaskRegistrar;
    }

    public static void setScheduledTaskRegistrar(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ScheduledTaskRegistrarHolder.scheduledTaskRegistrar = scheduledTaskRegistrar;
    }

    public static Map<Task, ScheduledTask> getTaskMap() {
        Set<ScheduledTask> set = scheduledTaskRegistrar.getScheduledTasks();
        return set.stream().collect(Collectors.toMap(ScheduledTask::getTask, Function.identity()));
    }

    public static Date getAppStartTime() {
        return APP_START_TIME;
    }

    public static void setAppStartTime(Date appStartTime) {
        APP_START_TIME = appStartTime;
    }
}
