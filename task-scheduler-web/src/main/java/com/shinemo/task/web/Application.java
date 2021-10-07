package com.shinemo.task.web;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhaoyn
 * @Date 2019/9/20
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.shinemo.task"})
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(basePackages = {"com.shinemo.task.core.dal.mapper"})
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = "com.shinemo.task.core")
@Slf4j
public class Application {

    public static void main(String[] args) {
        try {
            log.info("========task-scheduler start init========");
            ApplicationContext app = SpringApplication.run(Application.class, args);
            log.info("========task-scheduler start success========");
        } catch (Exception e) {
            log.error("task-scheduler start exception", e);
        }

    }
}



