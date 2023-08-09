package com.xxx.task.job;

import com.xxx.task.core.TaskMemoryStore;
import com.xxx.task.service.TaskSchedulerService;
import com.xxx.task.utils.RedissonLock;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * Created by wuzhenhong on 10/7/21 5:21 PM
 */
@Component
@Slf4j
public class AfterMathJob {

    @Autowired
    private RedissonLock redisLock;

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    // 善后处理 任务
    @Scheduled(cron = "00 0/5 * * * ?")
    public void checkNeedRemoveTask() {

        TaskMemoryStore.cancelInvaildTask();
    }

    /**
     * 检查需要重试的任务，重试的任务包括，执行失败，执行超时的任务
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void checkNeedRetryTask() {

        String lock = "com.xxx.task.job.AfterMathJob.checkNeedRetryTask";
        redisLock.tryLock(lock, 0L, 60L, TimeUnit.SECONDS, () -> {
            taskSchedulerService.taskRetry();
        });
    }

    /**
     * 检查已下线机器所执行的任务
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void checkDownLineTask() {

        String lock = "com.xxx.task.job.AfterMathJob.checkDownLineTask";

        redisLock.tryLock(lock, 0L, 60L, TimeUnit.SECONDS, () -> {
            taskSchedulerService.dealDownLineTask();
        });
    }

    /**
     * 检查任务更新消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void checkTaskUpdateMsg() {

        taskSchedulerService.taskUpdateMsg();
    }

    /**
     * 用于清理历史消息，消息只保存3天
     */
    @Scheduled(cron = "00 00 00 0/2 * ?")
    public void cleanOldTaskUpdateMsg() {

        String lock = "com.xxx.task.job.AfterMathJob.cleanOldTaskUpdateMsg";

        redisLock.tryLock(lock, 0L, 60L, TimeUnit.SECONDS, () -> {
            taskSchedulerService.cleanOldTaskUpdateMsg();
        });
    }
}
