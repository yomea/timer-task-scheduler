package org.springframework.scheduling.config;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by wuzhenhong on 10/18/21 2:35 PM
 */
public class CancelTaskWithoutInterruptUtils {

    public static void cancel(ScheduledTask scheduledTask, boolean mayInterruptIfRunning) {

        ScheduledFuture<?> future = scheduledTask.future;
        if(future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }
}
