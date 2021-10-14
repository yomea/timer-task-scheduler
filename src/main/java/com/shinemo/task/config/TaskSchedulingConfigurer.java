package com.shinemo.task.config;

import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.core.*;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.model.SmtTsTaskDefQuery;
import com.shinemo.task.dal.model.SmtTsTaskTimer;
import com.shinemo.task.dal.model.SmtTsTaskTimerQuery;
import com.shinemo.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskTimerWrapper;
import com.shinemo.task.enums.TaskStatusEnum;
import com.shinemo.task.model.TaskContext;
import com.shinemo.task.utils.SchedulerContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
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
    private SmtTsTaskDefWrapper smtTsTaskDefWrapper;

    @Resource
    private SmtTsTaskTimerWrapper smtTsTaskTimerWrapper;

    @Resource
    private SmtTsTaskLockWrapper smtTsTaskLockWrapper;

    @Resource
    private SmtTsTaskRecordWrapper smtTsTaskRecordWrapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ScheduledTaskRegistrarHolder.setAppStartTime(new Date());

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setOrderByStr(" id asc ");
        query.setPageIndex(1);
        query.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());

        while (true) {

            query.setPageSize(500);
            //分页查询数据
            List<SmtTsTaskDef> list = smtTsTaskDefWrapper.pageBy(query);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }

            List<Long> taskDefIdList = new ArrayList<>();

            SmtTsTaskTimerQuery taskTimerQuery = new SmtTsTaskTimerQuery();
            taskTimerQuery.setSmcDefIdList(taskDefIdList);
            taskTimerQuery.setTriggerDate(new Date());
            //获取定时器
            List<SmtTsTaskTimer> taskTimerList = smtTsTaskTimerWrapper.selectBy(taskTimerQuery);
            Map<Long, List<SmtTsTaskTimer>> taskDefIdMapTimers = taskTimerList.stream().collect(Collectors.groupingBy(SmtTsTaskTimer::getSmcDefId));

            list.stream().filter(taskDef -> taskDefIdMapTimers.containsKey(taskDef.getId())).forEach(taskDef -> {

                //应用服务名
                String appServiceName = taskDef.getAppServiceName();

                //注册 worker
                TaskHandlerBeanFactory.registryWorker(appServiceName);

                List<SmtTsTaskTimer> timerWithBLOBsList = taskDefIdMapTimers.get(taskDef.getId());
                timerWithBLOBsList.stream().forEach(timer -> {

                    SchedulerContextUtils.schedulerTask(taskRegistrar, transactionTemplate, smtTsTaskLockWrapper, smtTsTaskRecordWrapper, taskDef, timer);
                });
            });

            if (list.size() < query.getPageSize()) {
                break;
            }

            query.setPageIndex(query.getPageIndex() + 1);
        }

    }
}
