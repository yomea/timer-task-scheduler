package com.shinemo.task.service.impl;

import com.google.common.collect.Lists;
import com.shinemo.ace4j.Ace;
import com.shinemo.ace4j.common.service.dto.ServerInfoDTO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.task.constant.TaskScheduleConstant;
import com.shinemo.task.context.SchedulerContext;
import com.shinemo.task.core.CommonTraceTask;
import com.shinemo.task.core.ScheduledTaskRegistrarHolder;
import com.shinemo.task.core.TaskMemoryStore;
import com.shinemo.task.dal.model.*;
import com.shinemo.task.dal.wrapper.*;
import com.shinemo.task.enums.TaskActionEnum;
import com.shinemo.task.enums.TaskExecEnum;
import com.shinemo.task.enums.TaskStatusEnum;
import com.shinemo.task.model.TimerTask;
import com.shinemo.task.model.*;
import com.shinemo.task.service.TaskSchedulerService;
import com.shinemo.task.utils.AceServiceUtils;
import com.shinemo.task.utils.SchedulerContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by wuzhenhong on 10/12/21 5:37 PM
 */
@Service
@Slf4j
public class TaskSchedulerServiceImpl implements TaskSchedulerService, ApplicationRunner {

    @Value("${app.name}")
    private String appName;

    @Resource
    private SmtTsTaskMsgWrapper smtTsTaskMsgWrapper;

    @Resource
    private SmtTsTaskDefWrapper smtTsTaskDefWrapper;

    @Resource
    private SmtTsTaskTimerWrapper smtTsTaskTimerWrapper;

    @Resource
    private SmtTsTaskLockWrapper smtTsTaskLockWrapper;

    @Resource
    private SmtTsTaskRecordWrapper smtTsTaskRecordWrapper;

    @Resource
    private SmtTsConsumeProgressWrapper smtTsConsumeProgressWrapper;

    @Resource
    private ExecutorService retryExecutor;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Long> submitCronTask(CronTaskRequest cronTaskRequest) {

        ApiResult<Void> apiResult = cronSubmitArgsCheck(cronTaskRequest);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }

        TaskInfoConf taskInfoConf = cronTaskRequest.getTaskInfoConf();
        TaskScheduleConf taskScheduleConf = cronTaskRequest.getTaskScheduleConf();

        Long taskId = taskInfoConf.getTaskId();

        if(taskId != null && taskId > 0) {
            SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
            query.setId(taskId);
            SmtTsTaskDef smtTsTaskDef = smtTsTaskDefWrapper.getBy(query);
            if(smtTsTaskDef == null) {
                return ApiResult.fail("要修改的任务不存在！", 404);
            }
            Integer action = TaskActionEnum.MODIFY.getType();
            copyProperty(smtTsTaskDef, taskInfoConf);
            int row = smtTsTaskDefWrapper.updateById(smtTsTaskDef);
            if(row > 0) {
                smtTsTaskTimerWrapper.deleteByTaskId(taskId);
                List<SmtTsTaskTimer> taskTimerList = instanceByTaskScheduleConf(taskScheduleConf, smtTsTaskDef);
                row = smtTsTaskTimerWrapper.batchSave(taskTimerList);
                if(row > 0) {

                    SmtTsTaskMsg domain = new SmtTsTaskMsg();
                    domain.setSmcDefId(smtTsTaskDef.getId());
                    domain.setSmcAction(action);

                    row = smtTsTaskMsgWrapper.insertSelective(domain);
                    if(row > 0) {
                        return ApiResult.success(smtTsTaskDef.getId());
                    }
                }
            }



        } else {
            Integer action = TaskActionEnum.NEW.getType();

            //构建任务定义
            SmtTsTaskDef smtTsTaskDef = instanceByTaskInfoConf(taskInfoConf);

            int row = smtTsTaskDefWrapper.insertSelective(smtTsTaskDef);
            if(row > 0) {
                List<SmtTsTaskTimer> taskTimerList = instanceByTaskScheduleConf(taskScheduleConf, smtTsTaskDef);
                row = smtTsTaskTimerWrapper.batchSave(taskTimerList);
                if(row > 0) {

                    SmtTsTaskMsg domain = new SmtTsTaskMsg();
                    domain.setSmcAction(action);
                    domain.setSmcDefId(smtTsTaskDef.getId());

                    row = smtTsTaskMsgWrapper.insertSelective(domain);
                    if(row > 0) {
                        return ApiResult.success(smtTsTaskDef.getId());
                    }
                }
            }
        }

        throw new RuntimeException("提交任务失败！");
    }

    private List<SmtTsTaskTimer> instanceByTaskScheduleConf(TaskScheduleConf taskScheduleConf, SmtTsTaskDef smtTsTaskDef) {

        List<TimerTask> timerList = taskScheduleConf.getTimerList();
        return timerList.stream().map(timerTask -> {
            SmtTsTaskTimer smtTsTaskTimer = new SmtTsTaskTimer();
            smtTsTaskTimer.setSmcCron(timerTask.getCron());
            smtTsTaskTimer.setSmcDefId(smtTsTaskDef.getId());
            smtTsTaskTimer.setSmcStartDay(timerTask.getStartDateTime());
            smtTsTaskTimer.setSmcEndDay(timerTask.getEndDateTime());

            return smtTsTaskTimer;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult cronTaskDel(Long taskId) {

        SmtTsTaskMsg domain = new SmtTsTaskMsg();
        domain.setSmcDefId(taskId);
        domain.setSmcAction(TaskActionEnum.DELETE.getType());

        Integer rows = smtTsTaskDefWrapper.deleteById(taskId);
        if(rows > 0) {
            rows = smtTsTaskTimerWrapper.deleteByTaskId(taskId);
            if(rows > 0) {
                rows = smtTsTaskMsgWrapper.insertSelective(domain);
            }
        }

        if(rows < 1) {
            throw new RuntimeException("删除失败！");
        }

        return ApiResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> disableTask(Long taskId) {

        return modifyTaskStatus(taskId, TaskStatusEnum.DISABLE.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> enableTask(Long taskId) {
        return modifyTaskStatus(taskId, TaskStatusEnum.ENABLE.getStatus());
    }

    private ApiResult<Void> modifyTaskStatus(Long taskId, Integer status) {

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setId(taskId);

        SmtTsTaskDef taskDef = smtTsTaskDefWrapper.getBy(query);
        if(taskDef == null) {
            return ApiResult.fail("该任务不存在！", 404);
        }

        taskDef.setSmcStatus(status);

        smtTsTaskDefWrapper.updateById(taskDef);
        SmtTsTaskMsg domain = new SmtTsTaskMsg();
        domain.setSmcAction(TaskActionEnum.MODIFY.getType());
        domain.setSmcDefId(taskId);
        smtTsTaskMsgWrapper.insertSelective(domain);

        return ApiResult.success();
    }

    @Override
    public ApiResult<Void> execTaskImmediately(Long taskId) {

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setId(taskId);

        SmtTsTaskDef taskDef = smtTsTaskDefWrapper.getBy(query);
        if(taskDef == null) {
            return ApiResult.fail("要执行的任务不存在！", 404);
        }

        SchedulerContext schedulerContext = SchedulerContext.builder().smtTsTaskDef(taskDef).smtTsTaskRecordWrapper(smtTsTaskRecordWrapper)
                .smtTsTaskLockWrapper(smtTsTaskLockWrapper).transactionTemplate(transactionTemplate).build();

        TaskContext taskContext = TaskContext.builder().appServiceName(taskDef.getAppServiceName()).apiServiceName(taskDef.getApiServiceName())
                .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).extParams(null).retry(false).build();

        CommonTraceTask task = new CommonTraceTask(taskContext, schedulerContext);

        try {
            retryExecutor.submit(task);
        } catch (Exception e) {
            log.error("提交任务到线程池执行失败！", e);
            return ApiResult.fail("任务执行失败！", 500);
        }

        return ApiResult.success();
    }


    @Override
    public void taskRetry() {


        SmtTsTaskLockQuery query = new SmtTsTaskLockQuery();

        query.setStatusList(Arrays.asList(TaskExecEnum.EXEC_TIMEOUT.getStatus(), TaskExecEnum.FAILURE.getStatus()));
        query.setOrderByStr(" id asc ");
        query.setLtStartTime(System.currentTimeMillis());


        SmtTsTaskDefQuery taskDefQuery = new SmtTsTaskDefQuery();
        taskDefQuery.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());

        query.setPageSize(500);
        int pageIndex = 1;

        while (true) {

            query.setPageIndex(pageIndex);

            Page<SmtTsTaskLock> page = smtTsTaskLockWrapper.pageBy(query);
            if(CollectionUtils.isEmpty(page)) {
                break;
            }

            taskDefQuery.setIdList(page.stream().map(SmtTsTaskLock::getSmcDefId).collect(Collectors.toList()));

            List<SmtTsTaskDef> taskDefList = smtTsTaskDefWrapper.selectBy(taskDefQuery);

            taskDefList.stream().forEach(taskDef -> {

                SchedulerContext schedulerContext = SchedulerContext.builder().smtTsTaskDef(taskDef).smtTsTaskRecordWrapper(smtTsTaskRecordWrapper)
                        .smtTsTaskLockWrapper(smtTsTaskLockWrapper).transactionTemplate(transactionTemplate).build();

                TaskContext taskContext = TaskContext.builder().appServiceName(taskDef.getAppServiceName()).apiServiceName(taskDef.getApiServiceName())
                        .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).extParams(null).retry(true).build();

                CommonTraceTask task = new CommonTraceTask(taskContext, schedulerContext);

                retryExecutor.submit(task);
            });


            if(page.size() < query.getPageSize()) {
                break;
            }

            pageIndex += 1;
        }

    }

    @Override
    public void dealDownLineTask() {

        List<ServerInfoDTO> list = AceServiceUtils.getServerInfoList(TaskScheduleConstant.WORKER_PROXY_NAME + appName + "$center", appName);

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<String> ipList = list.stream().map(ServerInfoDTO::getHostIp).collect(Collectors.toList());

        appDownLineDealBeforeShutDownTask(ipList);
    }

    @Override
    public void taskUpdateMsg() {

        SmtTsConsumeProgressQuery progressQuery = new SmtTsConsumeProgressQuery();
        progressQuery.setSmcIp(Ace.get().getLocalHost());

        //获取该机器的消费进度
        SmtTsConsumeProgress smtTsConsumeProgress = smtTsConsumeProgressWrapper.getBy(progressQuery);

        if(smtTsConsumeProgress == null) {
            smtTsConsumeProgress = new SmtTsConsumeProgress();
            smtTsConsumeProgress.setSmcIp(Ace.get().getLocalHost());
            smtTsConsumeProgress.setSmcMsgId(0L);
            smtTsConsumeProgressWrapper.insertSelective(smtTsConsumeProgress);
        }

        SmtTsTaskMsgQuery query = new SmtTsTaskMsgQuery();
        query.setOrderByStr(" id asc ");
        query.setPageSize(1000);
        //设置当前服务启动时间
        query.setAppStartTime(ScheduledTaskRegistrarHolder.getAppStartTime());
        query.setGtId(smtTsConsumeProgress.getSmcMsgId());

        int pageIndex = 1;

        ScheduledTaskRegistrar scheduledTaskRegistrar = ScheduledTaskRegistrarHolder.getScheduledTaskRegistrar();

        SmtTsTaskDefQuery taskDefQuery = new SmtTsTaskDefQuery();
        SmtTsTaskTimerQuery timerQuery = new SmtTsTaskTimerQuery();

        while(true) {

            query.setPageIndex(pageIndex);

            Page<SmtTsTaskMsg> page = smtTsTaskMsgWrapper.pageBy(query);
            if(CollectionUtils.isEmpty(page)) {
                break;
            }

            List<SmtTsTaskMsg> taskMsgList = page.stream().filter(taskMsg -> {
                Integer action = taskMsg.getSmcAction();
                if(TaskActionEnum.DELETE.getType().equals(action)) {
                    TaskMemoryStore.cancelByTaskDefId(taskMsg.getSmcDefId());
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            if(taskMsgList.size() > 0) {

                List<Long> defIdList = taskMsgList.stream().map(SmtTsTaskMsg::getSmcDefId).collect(Collectors.toList());
                taskDefQuery.setIdList(defIdList);

                List<SmtTsTaskDef> tsTaskDefList = smtTsTaskDefWrapper.selectBy(taskDefQuery);
                Map<Long, SmtTsTaskDef> taskIdMapTaskDef = tsTaskDefList.stream().collect(Collectors.toMap(SmtTsTaskDef::getId, Function.identity()));

                timerQuery.setSmcDefIdList(defIdList);
                List<SmtTsTaskTimer> taskTimerList = smtTsTaskTimerWrapper.selectBy(timerQuery);
                Map<Long, List<SmtTsTaskTimer>> defIdMapTaskTimerList = taskTimerList.stream().collect(Collectors.groupingBy(SmtTsTaskTimer::getSmcDefId));

                taskMsgList.stream().forEach(smtTsTaskMsg -> {

                    Long defId = smtTsTaskMsg.getSmcDefId();
                    SmtTsTaskDef smtTsTaskDef = taskIdMapTaskDef.get(defId);
                    if(smtTsTaskDef == null) {
                        return;
                    }

                    List<SmtTsTaskTimer> timerList = defIdMapTaskTimerList.get(defId);
                    if(CollectionUtils.isEmpty(timerList)) {
                        return;
                    }

                    Integer action = smtTsTaskMsg.getSmcAction();
                    //新增
                    if(TaskActionEnum.NEW.getType().equals(action)) {
                        timerList.stream().forEach(timer -> {
                            SchedulerContextUtils.schedulerTask(scheduledTaskRegistrar, transactionTemplate, smtTsTaskLockWrapper, smtTsTaskRecordWrapper, smtTsTaskDef, timer);
                        });
                    } else if(TaskActionEnum.MODIFY.getType().equals(action)) {
                        TaskMemoryStore.cancelByTaskDefId(defId);
                        if(TaskStatusEnum.ENABLE.getStatus().equals(smtTsTaskDef.getSmcStatus())) {
                            timerList.stream().forEach(timer -> {
                                SchedulerContextUtils.schedulerTask(scheduledTaskRegistrar, transactionTemplate, smtTsTaskLockWrapper, smtTsTaskRecordWrapper, smtTsTaskDef, timer);
                            });
                        }
                    } else {
                        TaskMemoryStore.cancelByTaskDefId(defId);
                    }
                });
            }

            //消费成功
            Long maxId = page.get(page.size() - 1).getId();
            query.setGtId(maxId);

            if(page.size() < query.getPageSize()) {
                break;
            }

            pageIndex += 1;
        }

        //TODO 更新消费进度，这里的消息可以重复消费，为了简单起见，这里就不做幂等性检查了
        smtTsConsumeProgress.setSmcMsgId(query.getGtId());
        smtTsConsumeProgressWrapper.updateById(smtTsConsumeProgress);
    }

    @Override
    public void cleanOldTaskUpdateMsg() {


        LocalDateTime localDateTime = LocalDateTime.now().minusDays(3);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        SmtTsTaskMsgQuery query = new SmtTsTaskMsgQuery();
        query.setLtDate(date);

        smtTsTaskMsgWrapper.cleanOldTaskUpdateMsg(query);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(this::appStartDealBeforeShutDownTask).start();
    }

    private void appDownLineDealBeforeShutDownTask(List<String> notInIps) {

        SmtTsTaskLockQuery query = new SmtTsTaskLockQuery();
        query.setNotInIps(notInIps);
    }

    private void appStartDealBeforeShutDownTask() {

        SmtTsTaskLockQuery query = new SmtTsTaskLockQuery();
        query.setSmcIp(Ace.get().getLocalHost());

        doDealBeforeShutDownTask(query);
    }

    /**
     * 检查该机器下线之前正在执行的任务
     */
    private void doDealBeforeShutDownTask(SmtTsTaskLockQuery query) {

        query.setSmcStatus(TaskExecEnum.EXEC_ING.getStatus());
        query.setOrderByStr(" id asc ");

        int pageIndex = 1;
        query.setPageSize(1000);

        while (true) {

            query.setPageIndex(pageIndex);

            Page<SmtTsTaskLock> page = smtTsTaskLockWrapper.pageBy(query);
            if(CollectionUtils.isEmpty(page)) {
                break;
            }

            Lists.partition(page.stream().map(SmtTsTaskLock::getId).collect(Collectors.toList()), 500).stream().forEach(list -> {

                smtTsTaskLockWrapper.updateStatusByIdList(list, TaskExecEnum.FAILURE.getStatus(), TaskExecEnum.EXEC_ING.getStatus());

            });

            if(page.size() < query.getPageSize()) {
                break;
            }

            pageIndex += 1;
        }

    }

    private ApiResult<Void> cronSubmitArgsCheck(CronTaskRequest cronTaskRequest) {

        TaskInfoConf taskInfoConf = cronTaskRequest.getTaskInfoConf();
        if(taskInfoConf == null) {
            return ApiResult.fail("任务配置信息不能为空！", 500);
        }
        TaskScheduleConf taskScheduleConf = cronTaskRequest.getTaskScheduleConf();
        if(taskScheduleConf == null || CollectionUtils.isEmpty(taskScheduleConf.getTimerList())) {
            return ApiResult.fail("任务调度配置不能为空！", 500);
        }

        String appServiceName = taskInfoConf.getAppServiceName();
        String apiServiceName = taskInfoConf.getApiServiceName();
        String apiMethodName = taskInfoConf.getApiMethodName();
        String taskName = taskInfoConf.getTaskName();
        Long timeout = taskInfoConf.getTimeout();
        if(StringUtils.isEmpty(appServiceName)) {
            return ApiResult.fail("应用服务名 appServiceName 不能为空！", 500);
        }
        if(StringUtils.isEmpty(apiServiceName)) {
            return ApiResult.fail("任务处理接口服务名 apiServiceName 不能为空！", 500);
        }
        if(StringUtils.isEmpty(apiMethodName)) {
            return ApiResult.fail("任务处理方法名 apiMethodName 不能为空！", 500);
        }
        if(StringUtils.isEmpty(taskName)) {
            return ApiResult.fail("任务名 taskName 不能为空！", 500);
        }
        if(timeout == null || timeout <= 0L) {
            return ApiResult.fail("任务 taskName 不能为空！", 500);
        }

        Integer status = taskInfoConf.getStatus();
        if(status == null) {
            taskInfoConf.setStatus(TaskStatusEnum.ENABLE.getStatus());
        }

        List<TimerTask> timerList = taskScheduleConf.getTimerList();
        for(TimerTask timerTask : timerList) {

            String cron = timerTask.getCron();
            if(StringUtils.isEmpty(cron)) {
                return ApiResult.fail("定时任务表达式 cron 不能为空！", 500);
            }
        }

        return ApiResult.success();
    }

    private SmtTsTaskDef instanceByTaskInfoConf(TaskInfoConf taskInfoConf) {

        SmtTsTaskDef smtTsTaskDef = new SmtTsTaskDef();
        copyProperty(smtTsTaskDef, taskInfoConf);
        return smtTsTaskDef;
    }

    private void copyProperty(SmtTsTaskDef smtTsTaskDef, TaskInfoConf taskInfoConf) {

        smtTsTaskDef.setSmcStatus(taskInfoConf.getStatus());
        smtTsTaskDef.setSmcTimeout(taskInfoConf.getTimeout());
        smtTsTaskDef.setSmcTaskName(taskInfoConf.getTaskName());
        smtTsTaskDef.setAppServiceName(taskInfoConf.getAppServiceName());
        smtTsTaskDef.setApiServiceName(taskInfoConf.getApiServiceName());
        smtTsTaskDef.setApiMethodName(taskInfoConf.getApiMethodName());
    }
}
