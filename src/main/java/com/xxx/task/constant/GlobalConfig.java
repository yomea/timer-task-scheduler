package com.xxx.task.constant;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuzhenhong on 10/15/21 4:12 PM
 */
public class GlobalConfig {

    private static final int NCPUS = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService GLOBAL_EXECUTOR = new ThreadPoolExecutor(NCPUS << 3, NCPUS << 3,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000));

    public static int getNCPUS() {
        return NCPUS;
    }

    public static ExecutorService getGlobalExecutor() {
        return GLOBAL_EXECUTOR;
    }
}
