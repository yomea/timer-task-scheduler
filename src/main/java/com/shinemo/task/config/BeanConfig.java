package com.shinemo.task.config;

import com.shinemo.common.tools.redis.RedisLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.*;

/**
 * Created by wuzhenhong on 10/7/21 11:01 AM
 */
@Configuration
public class BeanConfig {

    private static final int NCPUS = Runtime.getRuntime().availableProcessors();

    @Bean
    public TaskScheduler taskScheduler() {

        //设置任务调度器
        return new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(NCPUS << 3));
    }

    @Bean
    public RedisLock redisLock() {

        return new RedisLock();
    }

    @Bean
    public ExecutorService retryExecutor() {

        return new ThreadPoolExecutor(NCPUS, NCPUS,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000));
    }

}
