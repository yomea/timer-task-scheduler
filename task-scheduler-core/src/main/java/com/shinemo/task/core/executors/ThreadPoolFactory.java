package com.shinemo.task.core.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.util.Assert;

/**
 * @author zhaoyn
 * @Date 2019/5/9
 */
public class ThreadPoolFactory {

    /**
     * 创建线程池
     */
    public static ExecutorService create(Settings settings) {
        Assert.hasText(settings.getThreadNamePrefix(), "参数threadNamePrefix不能为空");
        Assert.isTrue(settings.getCorePoolSize() > 0, "参数corePoolSize必须大于0");
        Assert.isTrue(settings.getMaximumPoolSize() > 0, "参数maximumPoolSize必须大于0");
        return new ThreadPoolExecutor(settings.getCorePoolSize(), settings.getMaximumPoolSize(),
                settings.getKeepAliveTime(), settings.getUnit(), settings.getWorkQueue(),
                new ExecutorThreadFactory(settings.getThreadNamePrefix()),
                settings.getDefaultHandler());
    }


    /**
     * 创建默认数量的线程池
     */
    public static ExecutorService createFixedThreadPool(String threadPrefixName, int count) {
        Settings settings = new Settings();
        settings.setThreadNamePrefix(threadPrefixName);
        settings.setCorePoolSize(count);
        settings.setMaximumPoolSize(count);
        return create(settings);
    }

    /**
     * 创建cache线程池
     */
    public static ExecutorService createCachedThreadPool(String threadPrefixName) {
        Settings settings = new Settings();
        settings.setThreadNamePrefix(threadPrefixName);
        settings.setCorePoolSize(0);
        settings.setMaximumPoolSize(10000);
        settings.setKeepAliveTime(60);
        settings.setUnit(TimeUnit.SECONDS);
        settings.setWorkQueue(new SynchronousQueue<Runnable>());
        return create(settings);
    }


    /**
     * 创建scheduled线程池
     */
    public static ExecutorService createScheduledThreadPool(String threadPrefixName, int corePoolSize) {
        return Executors
                .newScheduledThreadPool(corePoolSize, new ExecutorThreadFactory(threadPrefixName));
    }

}
