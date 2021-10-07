package com.shinemo.task.core.schedule.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

/**
 * @author zhaoyn
 * @Date 2019/3/20
 */
@Slf4j
public class ScheduleErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable throwable) {
        log.error("schedule handle error: ", throwable);
    }
}
