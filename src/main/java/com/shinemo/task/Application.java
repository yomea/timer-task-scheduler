package com.shinemo.task;

import com.shinemo.ace4j.Ace;
import com.shinemo.ace4j.srd.AaceServiceNode;
import com.shinemo.ace4j.srd.ServiceNode;
import com.shinemo.task.constant.TaskScheduleConstant;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
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
@MapperScan(basePackages = {"com.shinemo.task.dal.mapper"})
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
        //注册到ace
        registerAce();
    }

    private void registerAce() {

        AaceServiceNode serviceNode = new AaceServiceNode();
        serviceNode.setHost(Ace.get().getLocalHost());
        serviceNode.setPort(serverPort);
        serviceNode.setProxy(TaskScheduleConstant.WORKER_PROXY_NAME + appName + "$center");
        serviceNode.setInterfaceName(appName);
        serviceNode.setRegisterMode(ServiceNode.MODE_REMOTE);
        serviceNode.setStatus(Ace.get().getServerStatus());
        Ace.get().aaceCenterClient().init();
        Ace.get().serviceRegistry().register(serviceNode);
    }
}



