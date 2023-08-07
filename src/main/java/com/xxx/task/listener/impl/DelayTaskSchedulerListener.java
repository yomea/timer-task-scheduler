package com.xxx.task.listener.impl;

import com.xxx.task.context.SchedulerContext;
import com.xxx.task.dal.model.SmtTsTaskDef;
import com.xxx.task.dal.model.SmtTsTaskTimer;
import com.xxx.task.dal.model.SmtTsTaskTimerQuery;
import com.xxx.task.dal.wrapper.SmtTsTaskTimerWrapper;
import com.xxx.task.enums.TaskStatusEnum;
import com.xxx.task.enums.TimerTypeEnum;
import com.xxx.task.listener.HanguTaskSchedulerListener;
import com.xxx.task.model.ApiResult;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Created by wuzhenhong on 10/15/21 2:22 PM
 */
@Component
@Slf4j
public class DelayTaskSchedulerListener implements HanguTaskSchedulerListener {

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
