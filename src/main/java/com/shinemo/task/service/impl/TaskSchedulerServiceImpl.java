package com.shinemo.task.service.impl;

import com.google.common.collect.Lists;
import com.shinemo.ace4j.Ace;
import com.shinemo.ace4j.common.service.dto.ServerInfoDTO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.task.constant.TaskScheduleConstant;
import com.shinemo.task.dal.model.SmtTsTaskLock;
import com.shinemo.task.dal.model.SmtTsTaskLockQuery;
import com.shinemo.task.dal.model.SmtTsTaskMsg;
import com.shinemo.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskMsgWrapper;
import com.shinemo.task.enums.TaskActionEnum;
import com.shinemo.task.enums.TaskExecEnum;
import com.shinemo.task.model.CronTaskRequest;
import com.shinemo.task.service.TaskSchedulerService;
import com.shinemo.task.utils.AceServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
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
    private SmtTsTaskLockWrapper smtTsTaskLockWrapper;

    @Resource
    private ExecutorService retryExecutor;

    @Override
    public ApiResult<Long> submitCronTask(CronTaskRequest cronTaskRequest) {
        return null;
    }

    @Override
    public ApiResult cronTaskDel(Long taskId) {

        SmtTsTaskMsg domain = new SmtTsTaskMsg();
        domain.setSmcDefId(taskId);
        domain.setSmcAction(TaskActionEnum.DELETE.getType());

        int row = smtTsTaskMsgWrapper.insertSelective(domain);

        return row > 0 ? ApiResult.success() : ApiResult.fail("删除失败！", 500);
    }


    @Override
    public void taskRetry() {


        SmtTsTaskLockQuery query = new SmtTsTaskLockQuery();

        query.setStatusList(Arrays.asList(TaskExecEnum.EXEC_TIMEOUT.getStatus(), TaskExecEnum.FAILURE.getStatus()));
        query.setOrderByStr(" id asc ");
// TODO: 10/12/21 设置超时时间 
        query.setSmcTimeOut();


        query.setPageSize(1000);
        int pageIndex = 1;

        while (true) {

            query.setPageIndex(pageIndex);

            Page<SmtTsTaskLock> page = smtTsTaskLockWrapper.pageBy(query);
            if(CollectionUtils.isEmpty(page)) {
                break;
            }

            Lists.partition(page.stream().map(SmtTsTaskLock::getId).collect(Collectors.toList()), 500).stream().forEach(list -> {

                smtTsTaskLockWrapper.updateStatusByIdList(list, TaskExecEnum.RETRY.getStatus(), TaskExecEnum.EXEC_ING.getStatus());

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
        // TODO: 10/12/21 更新任务与机器进度 
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        appStartDealBeforeShutDownTask();
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
}
