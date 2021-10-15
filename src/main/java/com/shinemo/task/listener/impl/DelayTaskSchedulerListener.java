package com.shinemo.task.listener.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.model.SmtTsTaskTimer;
import com.shinemo.task.dal.model.SmtTsTaskTimerQuery;
import com.shinemo.task.dal.wrapper.SmtTsTaskTimerWrapper;
import com.shinemo.task.enums.TaskStatusEnum;
import com.shinemo.task.enums.TimerTypeEnum;
import com.shinemo.task.listener.AceTaskSchedulerListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wuzhenhong on 10/15/21 2:22 PM
 */
@Component
@Slf4j
public class DelayTaskSchedulerListener implements AceTaskSchedulerListener {

    @Resource
    private SmtTsTaskTimerWrapper smtTsTaskTimerWrapper;

    @Override
    public void failure(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

    }

    @Override
    public void exception(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

    }

    @Override
    public void success(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

        SmtTsTaskDef smtTsTaskDef = schedulerContext.getSmtTsTaskDef();
        SmtTsTaskTimerQuery query = new SmtTsTaskTimerQuery();
        query.setSmcDefId(smtTsTaskDef.getId());
        query.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());
        List<SmtTsTaskTimer> taskTimerList =  smtTsTaskTimerWrapper.selectBy(query);
        if(!CollectionUtils.isEmpty(taskTimerList)) {
            List<Long> delayTaskTimerIdList = taskTimerList.stream().filter(tsTaskTimer -> TimerTypeEnum.DELAY_TRIGGER.getType().equals(tsTaskTimer.getSmcTimerType())).map(SmtTsTaskTimer::getId).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(delayTaskTimerIdList)) {
                smtTsTaskTimerWrapper.modifyStatusByIdList(TaskStatusEnum.DISABLE.getStatus(), delayTaskTimerIdList);
            }

        }
    }

    @Override
    public void timeout(SchedulerContext schedulerContext, int retCode, ApiResult<Long> apiResult) {

    }
}
