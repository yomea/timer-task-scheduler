package com.xxx.task.config;

import com.xxx.task.core.ScheduledTaskRegistrarHolder;
import com.xxx.task.core.TaskHandlerBeanFactory;
import com.xxx.task.dal.model.SmtTsTaskDef;
import com.xxx.task.dal.model.SmtTsTaskDefQuery;
import com.xxx.task.dal.model.SmtTsTaskTimer;
import com.xxx.task.dal.model.SmtTsTaskTimerQuery;
import com.xxx.task.dal.wrapper.*;
import com.xxx.task.enums.TaskStatusEnum;
import com.xxx.task.listener.HanguTaskSchedulerListener;
import com.xxx.task.processor.TaskConfigurePostProcessor;
import com.xxx.task.utils.SchedulerContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
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

    @Resource
    private SmtTsTaskMsgWrapper smtTsTaskMsgWrapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private List<TaskConfigurePostProcessor> processorList;

    @Autowired
    private List<HanguTaskSchedulerListener> listenerList;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        beforeConfigure(taskRegistrar);
        //使用数据库的时间
        Date date = smtTsTaskMsgWrapper.getCurDbDate();
        ScheduledTaskRegistrarHolder.setAppStartTime(date);
        //设置调度任务注册器
        ScheduledTaskRegistrarHolder.setScheduledTaskRegistrar(taskRegistrar);

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setOrderByStr(" id asc ");
        query.setPageIndex(1);
        query.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());
        //排除子任务，子任务没有定时器，它与父任务绑定
        query.setExculeSubTask(true);

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
            taskTimerQuery.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());
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

                    SchedulerContextUtils.schedulerTask(listenerList, taskRegistrar, transactionTemplate, smtTsTaskLockWrapper, smtTsTaskRecordWrapper, smtTsTaskDefWrapper, taskDef, timer);
                });
            });

            if (list.size() < query.getPageSize()) {
                break;
            }

            query.setPageIndex(query.getPageIndex() + 1);
        }

        afterConfigure(taskRegistrar);
    }

    private void beforeConfigure(ScheduledTaskRegistrar taskRegistrar) {

        if(processorList != null) {
            processorList.stream().forEach(processor -> {
                processor.beforeConfigure(taskRegistrar);
            });
        }
    }

    private void afterConfigure(ScheduledTaskRegistrar taskRegistrar) {

        if(processorList != null) {
            processorList.stream().forEach(processor -> {
                processor.afterConfigure(taskRegistrar);
            });
        }
    }
}
