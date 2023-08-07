package com.xxx.task;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(basePackages = {"com.xxx.task.dal.mapper"})
@Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    private int serverPort;

    @Value("${app.name}")
    private String appName;

    public static void main(String[] args) {
        try {
            log.info("========task-scheduler start init========");
            ApplicationContext app = SpringApplication.run(Application.class, args);
            log.info("========task-scheduler start success========");
        } catch (Exception e) {
            log.error("task-scheduler start exception", e);
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }
}



