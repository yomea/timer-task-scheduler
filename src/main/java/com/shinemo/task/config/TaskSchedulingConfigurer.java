package com.shinemo.task.config;

import com.shinemo.task.core.LimitCronTask;
import com.shinemo.task.core.LimitCronTrigger;
import com.shinemo.task.core.ScheduledTaskRegistrarHolder;
import com.shinemo.task.core.TaskMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

/**
 * Created by wuzhenhong on 10/7/21 11:04 AM
 */
@Component
@Slf4j
public class TaskSchedulingConfigurer implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        // TODO: 10/7/21 从数据库中获取任务并创建一个任务实例，将任务修改为待调度


        LimitCronTrigger cronTrigger = new LimitCronTrigger("*/1 * * * * *", Date.from(Instant.now()), Date.from(Instant.now()));

        CronTask task = new LimitCronTask(null, cronTrigger);

        //设置调度任务注册器
        ScheduledTaskRegistrarHolder.setScheduledTaskRegistrar(taskRegistrar);

        ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask(task);

        TaskMemoryStore.putScheduledTask("", "", scheduledTask);

    }
}
