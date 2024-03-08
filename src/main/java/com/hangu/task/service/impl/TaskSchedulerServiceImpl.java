package com.hangu.task.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.hangu.rpc.common.manager.HanguRpcManager;
import com.hangu.rpc.provider.manager.NettyServerSingleManager;
import com.hangu.task.constant.TaskScheduleConstant;
import com.hangu.task.context.SchedulerContext;
import com.hangu.task.core.CommonTraceTask;
import com.hangu.task.core.ScheduledTaskRegistrarHolder;
import com.hangu.task.core.TaskMemoryStore;
import com.hangu.task.dal.model.SmtTsConsumeProgress;
import com.hangu.task.dal.model.SmtTsConsumeProgressQuery;
import com.hangu.task.dal.model.SmtTsTaskDef;
import com.hangu.task.dal.model.SmtTsTaskDefQuery;
import com.hangu.task.dal.model.SmtTsTaskLock;
import com.hangu.task.dal.model.SmtTsTaskLockQuery;
import com.hangu.task.dal.model.SmtTsTaskMsg;
import com.hangu.task.dal.model.SmtTsTaskMsgQuery;
import com.hangu.task.dal.model.SmtTsTaskTimer;
import com.hangu.task.dal.model.SmtTsTaskTimerQuery;
import com.hangu.task.dal.wrapper.SmtTsConsumeProgressWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskMsgWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskTimerWrapper;
import com.hangu.task.enums.TaskActionEnum;
import com.hangu.task.enums.TaskExecEnum;
import com.hangu.task.enums.TaskStatusEnum;
import com.hangu.task.enums.TimerTypeEnum;
import com.hangu.task.listener.HanguTaskSchedulerListener;
import com.hangu.task.model.ApiResult;
import com.hangu.task.model.TaskContext;
import com.hangu.task.model.TaskInfoConf;
import com.hangu.task.model.TaskScheduleConf;
import com.hangu.task.model.TimerTask;
import com.hangu.task.model.TimerTaskRequest;
import com.hangu.task.service.TaskSchedulerService;
import com.hangu.task.utils.RedisSearchServiceUtils;
import com.hangu.task.utils.SchedulerContextUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    @Autowired
    private List<HanguTaskSchedulerListener> listenerList;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Long> submitTimerTask(TimerTaskRequest timerTaskRequest) {

        // TODO: 10/28/21 一般情况下每个服务每个job不太会出现同时提交相同id的任务，这里就不做分布式锁增加复杂性了，如果出现了，数据库报唯一键错误即可
        // TODO: 10/28/21 目前用户自定义任务ID是必填的
        ApiResult<Void> apiResult = cronSubmitArgsCheck(timerTaskRequest);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }

        TaskInfoConf taskInfoConf = timerTaskRequest.getTaskInfoConf();
        TaskScheduleConf taskScheduleConf = timerTaskRequest.getTaskScheduleConf();

        String customerId = taskInfoConf.getCustomerId();

        //获取已经存在的进行更新
        SmtTsTaskDef smtTsTaskDef = getDefByUnique(taskInfoConf.getTaskId(), taskInfoConf.getAppServiceName(), taskInfoConf.getApiServiceName(), taskInfoConf.getApiMethodName(), customerId);

        if(smtTsTaskDef != null) {

            Integer action = TaskActionEnum.MODIFY.getType();
            copyProperty(smtTsTaskDef, taskInfoConf);
            int row = smtTsTaskDefWrapper.updateById(smtTsTaskDef);
            if(row > 0) {
                Long taskId = smtTsTaskDef.getId();
                if(smtTsTaskDef.getSmcHasChild() != null || smtTsTaskDef.getSmcHasChild()) {
                    //如果当前任务是存在子任务的，那么通过topId全部清除
                    smtTsTaskDefWrapper.deleteByTopId(taskId);
                }
                smtTsTaskTimerWrapper.deleteByTaskId(taskId);
                List<SmtTsTaskTimer> taskTimerList = instanceByTaskScheduleConf(taskScheduleConf, smtTsTaskDef);
                row = smtTsTaskTimerWrapper.batchSave(taskTimerList);
                addSubTask(taskId, taskId, taskInfoConf.getSubTaskList());
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
            smtTsTaskDef = instanceByTaskInfoConf(taskInfoConf);
            int row = smtTsTaskDefWrapper.insertSelective(smtTsTaskDef);
            Long taskId = smtTsTaskDef.getId();
            addSubTask(taskId, taskId, taskInfoConf.getSubTaskList());
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

    /**
     * 暂时先不弄子任务
     * @param topTaskId
     * @param parentTaskId
     * @param list
     */
    @Deprecated
    private void addSubTask(Long topTaskId, Long parentTaskId, List<TaskInfoConf> list) {

        list.stream().forEach(taskInfoConf -> {

            //构建任务定义
            SmtTsTaskDef smtTsTaskDef = instanceByTaskInfoConf(taskInfoConf);
            smtTsTaskDef.setSmcDefPid(parentTaskId);
            smtTsTaskDef.setSmcTopPid(topTaskId);

            //todo 一般任务子任务并不会特别多，这里就直接使用 smtTsTaskDefWrapper.insertSelective(smtTsTaskDef);
            smtTsTaskDefWrapper.insertSelective(smtTsTaskDef);

            addSubTask(topTaskId, smtTsTaskDef.getId(), taskInfoConf.getSubTaskList());
        });

    }

    private List<SmtTsTaskTimer> instanceByTaskScheduleConf(TaskScheduleConf taskScheduleConf, SmtTsTaskDef smtTsTaskDef) {

        List<TimerTask> timerList = taskScheduleConf.getTimerList();
        return timerList.stream().map(timerTask -> {

            SmtTsTaskTimer smtTsTaskTimer = new SmtTsTaskTimer();
            smtTsTaskTimer.setSmcDefId(smtTsTaskDef.getId());
            smtTsTaskTimer.setSmcStartDay(timerTask.getStartDateTime());
            smtTsTaskTimer.setSmcEndDay(timerTask.getEndDateTime());
            smtTsTaskTimer.setSmcStatus(timerTask.getStatus() == null ? TaskStatusEnum.ENABLE.getStatus() : TaskStatusEnum.DISABLE.getStatus());
            Integer timerType = timerTask.getTimerType();
            if(TimerTypeEnum.CRON.getType().equals(timerType)) {
                smtTsTaskTimer.setSmcCron(timerTask.getCron());
                smtTsTaskTimer.setSmcTimerType(TimerTypeEnum.CRON.getType());

            } else if(TimerTypeEnum.DELAY_TRIGGER.getType().equals(timerType)) {
                smtTsTaskTimer.setSmcOnceDelay(timerTask.getDelay());
                smtTsTaskTimer.setSmcTimerType(TimerTypeEnum.DELAY_TRIGGER.getType());
            } else if(TimerTypeEnum.FIX_DELAY.getType().equals(timerType)) {
                smtTsTaskTimer.setSmcInitDelay(timerTask.getInitDelay());
                smtTsTaskTimer.setSmcPeriod(timerTask.getPeriodic());
                smtTsTaskTimer.setSmcTimerType(TimerTypeEnum.FIX_DELAY.getType());
            } else if(TimerTypeEnum.FIX_RATE.getType().equals(timerType)){
                smtTsTaskTimer.setSmcInitDelay(timerTask.getInitDelay());
                smtTsTaskTimer.setSmcPeriod(timerTask.getPeriodic());
                smtTsTaskTimer.setSmcTimerType(TimerTypeEnum.FIX_RATE.getType());
            } else {
                throw new RuntimeException(String.format("不支持的定时任务类型！timerType = %s", timerType));
            }

            return smtTsTaskTimer;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult timerTaskDel(Long taskId) {

        SmtTsTaskMsg domain = new SmtTsTaskMsg();
        domain.setSmcDefId(taskId);
        domain.setSmcAction(TaskActionEnum.DELETE.getType());

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        query.setId(taskId);
        SmtTsTaskDef smtTsTaskDef = smtTsTaskDefWrapper.getBy(query);
        if(smtTsTaskDef == null) {
            return ApiResult.fail("要删除的任务不存在！", 500);
        }
        Boolean hashChild = smtTsTaskDef.getSmcHasChild();
        if(hashChild != null && hashChild) {
            smtTsTaskDefWrapper.deleteByTopId(smtTsTaskDef.getId());
        }

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
    public ApiResult<Void> timerTaskDel(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {

        ApiResult<SmtTsTaskDef> apiResult = getDefByUniqueWithCheck(appServiceName, apiServiceName, apiMethodName, customerId);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }
        SmtTsTaskDef smtTsTaskDef = apiResult.getData();

        return timerTaskDel(smtTsTaskDef.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> disableTask(Long taskId) {

        return modifyTaskStatus(taskId, TaskStatusEnum.DISABLE.getStatus());
    }

    @Override
    public ApiResult<Void> disableTask(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        ApiResult<SmtTsTaskDef> apiResult = getDefByUniqueWithCheck(appServiceName, apiServiceName, apiMethodName, customerId);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }
        SmtTsTaskDef smtTsTaskDef = apiResult.getData();

        return disableTask(smtTsTaskDef.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> enableTask(Long taskId) {
        return modifyTaskStatus(taskId, TaskStatusEnum.ENABLE.getStatus());
    }

    @Override
    public ApiResult<Void> enableTask(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        ApiResult<SmtTsTaskDef> apiResult = getDefByUniqueWithCheck(appServiceName, apiServiceName, apiMethodName, customerId);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }
        SmtTsTaskDef smtTsTaskDef = apiResult.getData();

        return enableTask(smtTsTaskDef.getId());
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

        SchedulerContext schedulerContext = SchedulerContext.builder().listenerList(listenerList).smtTsTaskDef(taskDef).smtTsTaskRecordWrapper(smtTsTaskRecordWrapper)
                .smtTsTaskLockWrapper(smtTsTaskLockWrapper).smtTsTaskDefWrapper(smtTsTaskDefWrapper).transactionTemplate(transactionTemplate).build();

        TaskContext taskContext = TaskContext.builder().appServiceName(taskDef.getAppServiceName()).apiServiceName(taskDef.getApiServiceName())
                .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).contextParams(null).retry(false).customerExtParams(taskDef.getSmcExt()).customerId(taskDef.getSmcCustomerId()).build();

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
    public ApiResult<Void> execTaskImmediately(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {
        ApiResult<SmtTsTaskDef> apiResult = getDefByUniqueWithCheck(appServiceName, apiServiceName, apiMethodName, customerId);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }
        SmtTsTaskDef smtTsTaskDef = apiResult.getData();

        return execTaskImmediately(smtTsTaskDef.getId());
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

            List<Long> defIdList = page.stream().map(SmtTsTaskLock::getSmcDefId).collect(Collectors.toList());
            taskDefQuery.setIdList(defIdList);

            List<SmtTsTaskDef> taskDefList = smtTsTaskDefWrapper.selectBy(taskDefQuery);

            List<Long> factExistsDefIdList = new ArrayList<>(taskDefList.size());
            taskDefList.stream().forEach(taskDef -> {

                factExistsDefIdList.add(taskDef.getId());
                SchedulerContext schedulerContext = SchedulerContext.builder().listenerList(listenerList).smtTsTaskDef(taskDef).smtTsTaskRecordWrapper(smtTsTaskRecordWrapper)
                        .smtTsTaskLockWrapper(smtTsTaskLockWrapper).smtTsTaskDefWrapper(smtTsTaskDefWrapper).transactionTemplate(transactionTemplate).build();

                TaskContext taskContext = TaskContext.builder().appServiceName(taskDef.getAppServiceName()).apiServiceName(taskDef.getApiServiceName())
                        .methodName(taskDef.getApiMethodName()).taskId(taskDef.getId()).contextParams(null).retry(true).customerExtParams(taskDef.getSmcExt()).customerId(taskDef.getSmcCustomerId()).build();

                CommonTraceTask task = new CommonTraceTask(taskContext, schedulerContext);

                retryExecutor.submit(task);
            });

            defIdList.removeAll(factExistsDefIdList);
            if(!CollectionUtils.isEmpty(defIdList)) {
                smtTsTaskLockWrapper.deleteByDefIdList(defIdList);
            }

            if(page.size() < query.getPageSize()) {
                break;
            }

            pageIndex += 1;
        }

    }

    @Override
    public void dealDownLineTask() {

        List<String> list = RedisSearchServiceUtils.getServerInfoList(TaskScheduleConstant.WORKER_PROXY_NAME + appName + "$center");

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        appDownLineDealBeforeShutDownTask(list);
    }

    @Override
    public void taskUpdateMsg() {

        SmtTsConsumeProgressQuery progressQuery = new SmtTsConsumeProgressQuery();
        progressQuery.setSmcIp(NettyServerSingleManager.getLocalHost().getHost());

        //获取该机器的消费进度
        SmtTsConsumeProgress smtTsConsumeProgress = smtTsConsumeProgressWrapper.getBy(progressQuery);

        if(smtTsConsumeProgress == null) {
            smtTsConsumeProgress = new SmtTsConsumeProgress();
            smtTsConsumeProgress.setSmcIp(NettyServerSingleManager.getLocalHost().getHost());
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
                timerQuery.setSmcStatus(TaskStatusEnum.ENABLE.getStatus());
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
                        //避免重复消费导致的bug
                        TaskMemoryStore.cancelByTaskDefId(defId);
                        timerList.stream().forEach(timer -> {
                            SchedulerContextUtils.schedulerTask(listenerList, scheduledTaskRegistrar, transactionTemplate, smtTsTaskLockWrapper, smtTsTaskRecordWrapper, smtTsTaskDefWrapper, smtTsTaskDef, timer);
                        });
                    } else if(TaskActionEnum.MODIFY.getType().equals(action)) {
                        TaskMemoryStore.cancelByTaskDefId(defId);
                        if(TaskStatusEnum.ENABLE.getStatus().equals(smtTsTaskDef.getSmcStatus())) {
                            timerList.stream().forEach(timer -> {
                                SchedulerContextUtils.schedulerTask(listenerList, scheduledTaskRegistrar, transactionTemplate, smtTsTaskLockWrapper, smtTsTaskRecordWrapper, smtTsTaskDefWrapper, smtTsTaskDef, timer);
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
        query.setSmcIp(NettyServerSingleManager.getLocalHost().getHost());

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

    private ApiResult<Void> cronSubmitArgsCheck(TimerTaskRequest cronTaskRequest) {

        TaskInfoConf taskInfoConf = cronTaskRequest.getTaskInfoConf();
        if(taskInfoConf == null) {
            return ApiResult.fail("任务配置信息不能为空！", 500);
        }
        TaskScheduleConf taskScheduleConf = cronTaskRequest.getTaskScheduleConf();
        if(taskScheduleConf == null || CollectionUtils.isEmpty(taskScheduleConf.getTimerList())) {
            return ApiResult.fail("任务调度配置不能为空！", 500);
        }

        ApiResult<Void> apiResult = taskInfoConfCheck(taskInfoConf);
        if(!apiResult.isSuccess()) {
            return apiResult;
        }

        //子任务检查
        apiResult = subTaskConfigCheck(taskInfoConf.getSubTaskList());
        if(!apiResult.isSuccess()) {
            return apiResult;
        }

        List<TimerTask> timerList = taskScheduleConf.getTimerList();
        for(TimerTask timerTask : timerList) {
            Integer timerType = timerTask.getTimerType();
            if(TimerTypeEnum.CRON.getType().equals(timerType)) {
                String cron = timerTask.getCron();
                if(StringUtils.isEmpty(cron)) {
                    return ApiResult.fail("定时任务表达式 cron 不能为空！", 500);
                }
                if(!CronSequenceGenerator.isValidExpression(cron)) {
                    return ApiResult.fail("定时任务表达式 cron 不合法！", 500);
                }
            } else if(TimerTypeEnum.FIX_RATE.getType().equals(timerType) || TimerTypeEnum.FIX_DELAY.getType().equals(timerType)) {
                long initDelay = timerTask.getInitDelay();
                long periodic = timerTask.getPeriodic();
                if(initDelay < 0) {
                    return ApiResult.fail("固定频率定时任务初始延时时间 initDelay 不能小于零！", 500);
                }
                if(periodic <= 0) {
                    return ApiResult.fail("固定频率定时任务的频率 periodic 必须大于零！", 500);
                }
            } else if(TimerTypeEnum.DELAY_TRIGGER.getType().equals(timerType)) {
                long delay = timerTask.getDelay();
                if(delay < 0) {
                    return ApiResult.fail("延时定时任务的延时时间 delay 不能小于零！", 500);
                }
            } else {
                throw new RuntimeException(String.format("不支持的定时任务类型！class = [%s]", timerTask.getClass().getName()));
            }

        }

        return ApiResult.success();
    }

    private ApiResult<Void> subTaskConfigCheck(List<TaskInfoConf> taskInfoConfList) {

        taskInfoConfList = Optional.ofNullable(taskInfoConfList).orElse(Collections.emptyList());
        for(TaskInfoConf tackConf : taskInfoConfList) {
            ApiResult apiResult = taskInfoConfCheck(tackConf);
            if(!apiResult.isSuccess()) {
                return apiResult;
            }
        }

        return ApiResult.success();
    }

    private ApiResult<Void> taskInfoConfCheck(TaskInfoConf taskInfoConf) {

        String appServiceName = taskInfoConf.getAppServiceName();
        String apiServiceName = taskInfoConf.getApiServiceName();
        String apiMethodName = taskInfoConf.getApiMethodName();
        String taskName = taskInfoConf.getTaskName();
        Long timeout = taskInfoConf.getTimeout();
        String customerId = taskInfoConf.getCustomerId();
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
        if(StringUtils.isEmpty(customerId)) {
            return ApiResult.fail("任务 customerId  不能为空！", 500);
        }

        Integer status = taskInfoConf.getStatus();
        if(status == null) {
            taskInfoConf.setStatus(TaskStatusEnum.ENABLE.getStatus());
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
        smtTsTaskDef.setSmcHasChild(!CollectionUtils.isEmpty(taskInfoConf.getSubTaskList()));
        smtTsTaskDef.setSmcExt(taskInfoConf.getCustomerExtParams());
        smtTsTaskDef.setSmcCustomerId(taskInfoConf.getCustomerId());
    }

    private SmtTsTaskDef getDefByUnique(Long taskId, String appServiceName, String apiServiceName, String apiMethodName, String customerId) {

        if((taskId == null || taskId <= 0) && StringUtils.isEmpty(customerId)) {
            return null;
        }

        SmtTsTaskDefQuery query = new SmtTsTaskDefQuery();
        if(taskId != null && taskId > 0) {
            query.setId(taskId);
        } else {
            query.setSmcCustomerId(customerId);
            query.setAppServiceName(appServiceName);
            query.setApiServiceName(apiServiceName);
            query.setApiMethodName(apiMethodName);
        }

        //获取已经存在的进行更新
        SmtTsTaskDef smtTsTaskDef = smtTsTaskDefWrapper.getBy(query);

        return smtTsTaskDef;
    }

    private ApiResult<SmtTsTaskDef> getDefByUniqueWithCheck(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {

        ApiResult<Void> apiResult = uniqueCheck(appServiceName, apiServiceName, apiMethodName, customerId);
        if(!apiResult.isSuccess()) {
            return ApiResult.fail(apiResult.getMsg(), apiResult.getCode());
        }
        SmtTsTaskDef smtTsTaskDef = getDefByUnique(null, appServiceName, apiServiceName, apiMethodName, customerId);
        if(smtTsTaskDef == null) {
            return ApiResult.fail("该任务不存在！", 404);
        }

        return ApiResult.success(smtTsTaskDef);
    }

    private ApiResult<Void> uniqueCheck(String appServiceName, String apiServiceName, String apiMethodName, String customerId) {

        if(StringUtils.isEmpty(appServiceName)) {
            return ApiResult.fail("应用服务名  appServiceName 不能为空！", 500);
        }
        if(StringUtils.isEmpty(apiServiceName)) {
            return ApiResult.fail("任务处理接口服务名  apiServiceName 不能为空！", 500);
        }
        if(StringUtils.isEmpty(apiMethodName)) {
            return ApiResult.fail("任务处理方法名  apiMethodName 不能为空！", 500);
        }
        if(StringUtils.isEmpty(customerId)) {
            return ApiResult.fail("任务 customerId  不能为空！", 500);
        }

        return ApiResult.success();
    }
}
