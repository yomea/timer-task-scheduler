package com.shinemo.task.core.config;

import com.shinemo.common.tools.redis.RedisLock;
import com.shinemo.task.core.schedule.handler.ScheduleErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author zhaoyn
 * @Date 2019/3/20
 */
@Configuration
public class CoreConfig {

    /**
     * springboot task线程池 配置
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setErrorHandler(new ScheduleErrorHandler());
        return taskScheduler;
    }

    /**
     * redis分布式锁，redis其他操作直接使用redisTemplate
     */
    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }

    /**
     * redis模版, 注入参考这个, Object换成具体类型, Serializer自己选择合适的, 注意RedisTemplate<String, String> spring已经帮我们自动注入了，不需要重复注入
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "objectRedisTemplate",initMethod = "afterPropertiesSet")
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer=new StringRedisSerializer();
        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(redisConnectionFactory);
        objectRedisTemplate.setKeySerializer(stringRedisSerializer);
        objectRedisTemplate.setValueSerializer(jsonRedisSerializer);
        objectRedisTemplate.setEnableDefaultSerializer(true);
        objectRedisTemplate.setDefaultSerializer(jsonRedisSerializer);
        objectRedisTemplate.setHashKeySerializer(stringRedisSerializer);
        objectRedisTemplate.setHashValueSerializer(stringRedisSerializer);
        objectRedisTemplate.setStringSerializer(stringRedisSerializer);
        return objectRedisTemplate;
    }



}
