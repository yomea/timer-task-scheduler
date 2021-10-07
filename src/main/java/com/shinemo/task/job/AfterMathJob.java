package com.shinemo.task.job;

import com.shinemo.task.core.TaskMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by wuzhenhong on 10/7/21 5:21 PM
 */
@Component
@Slf4j
public class AfterMathJob {

    @Scheduled(cron = "")
    public void checkNeedRemoveTask() {

        TaskMemoryStore.cancelInvaildTask();
    }
}
