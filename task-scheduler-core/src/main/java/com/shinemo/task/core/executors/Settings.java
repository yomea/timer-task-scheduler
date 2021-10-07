package com.shinemo.task.core.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoyn
 * @Date 2019/5/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    /**
     * 线程名字前缀
     */
    private String threadNamePrefix;
    /**
     * 线程池核心线程数
     */
    private int corePoolSize;
    /**
     * 线程池最大线程数量
     */
    private int maximumPoolSize;
    /**
     * 线程空闲时间(当线程池数量大于cpu核心数后，超过此空闲时间线程会被回收)
     */
    private long keepAliveTime = 0;
    /**
     * 线程空闲时间单位
     */
    private TimeUnit unit = TimeUnit.MILLISECONDS;
    /**
     * 缓冲队列,默认设为10000
     */
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10000);

    /**
     * 线程池对拒绝任务的策略
     */
    private RejectedExecutionHandler defaultHandler =
            new ThreadPoolExecutor.AbortPolicy();
}
