package org.springframework.scheduling.config;

/**
 * Created by wuzhenhong on 10/18/21 2:35 PM
 */
public class CancelTaskWithoutInterruptUtils {

    public static boolean cancel(ScheduledTask scheduledTask, boolean mayInterruptIfRunning) {

        return scheduledTask.future.cancel(mayInterruptIfRunning);
    }
}
