package com.shinemo.task.config;

import com.shinemo.task.core.LimitCronTask;
import com.shinemo.task.core.LimitCronTrigger;
import com.shinemo.task.core.ScheduledTaskRegistrarHolder;
import com.shinemo.task.core.TaskMemoryStore;
import com.shinemo.task.dal.mapper.SmtTsTaskDefMapper;
import com.shinemo.task.dal.mapper.SmtTsTaskInstanceMapper;
import com.shinemo.task.dal.model.SmtTsTaskDefQuery;
import com.shinemo.task.dal.model.SmtTsTaskDefWithBLOBs;
import com.shinemo.task.dal.model.SmtTsTaskInstanceWithBLOBs;
import com.shinemo.task.enums.TaskInstanceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wuzhenhong on 10/7/21 11:04 AM
 */
@Component
@Slf4j
public class TaskSchedulingConfigurer implements SchedulingConfigurer {

    @Resource
    private SmtTsTaskDefMapper smtTsTaskDefMapper;

    @Resource
    private SmtTsTaskInstanceMapper smtTsTaskInstanceMapper;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        // TODO: 10/7/21 从数据库中获取任务并创建一个任务实例，将任务修改为待调度

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setOrderByStr(" id asc ");
        query.setPageIndex(1);

        List<SmtTsTaskInstanceWithBLOBs> taskInstanceList = new ArrayList<>();

        while(true) {

            query.setPageSize(10000);
            //分页查询数据
            List<SmtTsTaskDefWithBLOBs> list = smtTsTaskDefMapper.pageBy(query);
            if(CollectionUtils.isEmpty(list)) {
                break;
            }


            list.stream().map(taskDef -> {
                SmtTsTaskInstanceWithBLOBs taskInstance = new  SmtTsTaskInstanceWithBLOBs();
                taskInstance.setSmcTimeout(taskDef.getSmcTimeout());
                taskInstance.setSmcStatus(TaskInstanceEnum.WAIT_EXEC.getStatus());
                taskInstance.setSmcDefId(taskDef.getId());
                taskInstance.set

            })


            query.setPageIndex(query.getPageIndex() + 1);
        }






        LimitCronTrigger cronTrigger = new LimitCronTrigger("*/1 * * * * *", Date.from(Instant.now()), Date.from(Instant.now()));

        CronTask task = new LimitCronTask(null, cronTrigger);

        //设置调度任务注册器
        ScheduledTaskRegistrarHolder.setScheduledTaskRegistrar(taskRegistrar);

        ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask(task);

        TaskMemoryStore.putScheduledTask("", "", scheduledTask);

    }
}
