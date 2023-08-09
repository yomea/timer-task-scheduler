package com.xxx.task;

import ch.qos.logback.classic.Logger;
import cn.hutool.core.net.NetUtil;
import com.hanggu.common.manager.HanguRpcManager;
import com.xxx.task.constant.TaskScheduleConstant;
import com.xxx.task.utils.LogUtils;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(basePackages = {"com.xxx.task.dal.mapper"})
@Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    // // 启动时spring还未加载日志，这里先加载下，就设置一个输出到控制台的日志
//    private static Logger LOGGER = LogUtils.getConsoleLogger(Application.class.getName());
    private static final Long HEART_TIME_OUT = 6L;
    private static final int SELF_EXPORT_TIME_OUT = 24 * 60 * 60;

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
        new ScheduledThreadPoolExecutor(1);

    @Value("${app.name}")
    private String appName;

    @Autowired
    private JedisSentinelPool jedisSentinelPool;

    public static void main(String[] args) {
        try {
            ApplicationContext app = SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            log.error("task-scheduler start exception", e);
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 获取本机可用ip
        String ip = NetUtil.getLocalhost().getHostAddress();
        // 向redis暴露自身
        Jedis jedis = jedisSentinelPool.getResource();
        String groupName = TaskScheduleConstant.WORKER_PROXY_NAME + appName + "$center";
        // 6s 超时
        String expire = String.valueOf(System.currentTimeMillis() + HEART_TIME_OUT);
        try {
            jedis.hset(groupName, ip, expire);
            jedis.expire(groupName, SELF_EXPORT_TIME_OUT);
        } catch (Exception e) {
            log.error("将自身暴露到redis失败！");
        } finally {
            jedis.close();
        }
        // 每隔2s刷新一次，模拟心跳
        SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            Jedis j = jedisSentinelPool.getResource();
            try {
                j.hset(groupName, ip, String.valueOf(System.currentTimeMillis() + HEART_TIME_OUT));
                j.expire(groupName, SELF_EXPORT_TIME_OUT);
            } finally {
                j.close();
            }
        }, 2, TimeUnit.SECONDS);
    }
}



