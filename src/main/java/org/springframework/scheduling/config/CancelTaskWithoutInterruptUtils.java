package org.springframework.scheduling.config;

import java.util.concurrent.ScheduledFuture;

/**
 * spring 提供的 ScheduledTask#cancel 方法默认指定了会中断线程
 * 但是实际代码中是不需要中断的，为了不中断取消任务，可以使用反射或者以下方法去处理，
 *
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
