package com.shinemo.task.config;

import com.shinemo.task.core.LimitCronTask;
import com.shinemo.task.core.LimitCronTrigger;
import com.shinemo.task.core.ScheduledTaskRegistrarHolder;
import com.shinemo.task.core.TaskMemoryStore;
import com.shinemo.task.dal.mapper.SmtTsTaskDefMapper;
import com.shinemo.task.dal.mapper.SmtTsTaskInstanceMapper;
import com.shinemo.task.dal.mapper.SmtTsTaskTimerMapper;
import com.shinemo.task.dal.model.*;
import com.shinemo.task.enums.TaskInstanceEnum;
import com.shinemo.task.enums.TimerTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private SmtTsTaskTimerMapper smtTsTaskTimerMapper;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        // TODO: 10/7/21 从数据库中获取任务并创建一个任务实例，将任务修改为待调度

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setOrderByStr(" id asc ");
        query.setPageIndex(1);

        while(true) {

            query.setPageSize(500);
            //分页查询数据
            List<SmtTsTaskDefWithBLOBs> list = smtTsTaskDefMapper.pageBy(query);
            if(CollectionUtils.isEmpty(list)) {
                break;
            }

            List<Long> taskDefIdList = new ArrayList<>();

            List<SmtTsTaskInstanceWithBLOBs> taskInstanceList = list.stream().map(taskDef -> {
                SmtTsTaskInstanceWithBLOBs taskInstance = new  SmtTsTaskInstanceWithBLOBs();
                taskInstance.setSmcTimeout(taskDef.getSmcTimeout());
                taskInstance.setSmcStatus(TaskInstanceEnum.WAIT_EXEC.getStatus());
                taskInstance.setSmcDefId(taskDef.getId());
                taskInstance.setSmcTaskName(taskDef.getSmcTaskName());

                taskDefIdList.add(taskDef.getId());

                return taskInstance;
            }).collect(Collectors.toList());

            SmtTsTaskTimerQuery taskTimerQuery = new SmtTsTaskTimerQuery();
            taskTimerQuery.setSmcDefIdList(taskDefIdList);
            taskTimerQuery.setTriggerDate(new Date());
            //获取定时器
            List<SmtTsTaskTimerWithBLOBs> taskTimerList = smtTsTaskTimerMapper.selectBy(taskTimerQuery);
            Map<Long, List<SmtTsTaskTimerWithBLOBs>> taskDefIdMapTimers = taskTimerList.stream().collect(Collectors.groupingBy(SmtTsTaskTimerWithBLOBs::getSmcDefId));

            smtTsTaskInstanceMapper.batchSave(taskInstanceList);

            list.stream().filter(taskDef -> taskDefIdMapTimers.containsKey(taskDef.getId())).forEach(taskDef -> {

                List<SmtTsTaskTimerWithBLOBs> timerWithBLOBsList = taskDefIdMapTimers.get(taskDef.getId());
                timerWithBLOBsList.stream().forEach(timer -> {

                    //定时任务，cron，延时
                    Integer timerType = timer.getSmcTimerType();

                    if(TimerTypeEnum.CRON.getType().equals(timerType)) {

                        LimitCronTrigger cronTrigger = new LimitCronTrigger(timer.getSmcCron(), timer.getSmcStartDay(), timer.getSmcEndDay());

                        // TODO: 10/8/21 创建Task工厂，更具任务定义类型
                        Integer taskType = taskDef.getSmcTaskType();

                        CronTask task = new LimitCronTask(null, cronTrigger);

                        //设置调度任务注册器
                        ScheduledTaskRegistrarHolder.setScheduledTaskRegistrar(taskRegistrar);

                        ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask(task);

                        TaskMemoryStore.putScheduledTask("", "", scheduledTask);
                    }
                });
            });

            if(list.size() < query.getPageSize()) {
                break;
            }

            query.setPageIndex(query.getPageIndex() + 1);
        }

    }
}
