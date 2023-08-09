package com.xxx.task.config;

import com.xxx.task.constant.GlobalConfig;
import com.xxx.task.utils.RedisSearchServiceUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import redis.clients.jedis.JedisSentinelPool;

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
    public RedisSearchServiceUtils redisSearchServiceUtils(JedisSentinelPool jedisSentinelPool) {
        return new RedisSearchServiceUtils(jedisSentinelPool);
    }

    @Bean
    public ExecutorService retryExecutor() {

        return GlobalConfig.getGlobalExecutor();
    }

}
