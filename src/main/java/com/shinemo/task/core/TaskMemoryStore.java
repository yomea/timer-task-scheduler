package com.shinemo.task.core;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.CancelTaskWithoutInterruptUtils;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuzhenhong on 10/7/21 2:05 PM
 */
public class TaskMemoryStore {

    private static final Map<Long, ScheduledTask> DEF_ID_MAP_SCHEDULE_TASK = new ConcurrentHashMap<>(16);

    public static void putScheduledTask(Long defId, ScheduledTask task) {

        DEF_ID_MAP_SCHEDULE_TASK.put(defId, task);
    }

    public static void cancelByTaskDefId(Long defId) {

        ScheduledTask scheduledTask = DEF_ID_MAP_SCHEDULE_TASK.remove(defId);

        if (scheduledTask == null) {
            return;
        }

        CancelTaskWithoutInterruptUtils.cancel(scheduledTask, false);
        //spring提供的这个取消方法会中断线程，导致后续出现一些不可预知的错误，比如进行数据库操作时，会导致异常
//        scheduledTask.cancel();
    }

    public static void cancelInvaildTask() {

        Date date = new Date();
        List<Long> needRmDefIdList = new ArrayList<>();

        DEF_ID_MAP_SCHEDULE_TASK.forEach((defId, scheduledTask) -> {

            if (scheduledTask != null && (scheduledTask.getTask() instanceof TriggerTask)) {

                TriggerTask triggerTask = (TriggerTask) scheduledTask.getTask();
                Trigger trigger = triggerTask.getTrigger();
                if(trigger instanceof LimitCronTrigger) {
                    LimitCronTrigger limitCronTrigger = (LimitCronTrigger) trigger;
                    Date limitEnd = limitCronTrigger.getLimitEnd();
                    if (limitEnd != null && date.after(limitEnd)) {
                        needRmDefIdList.add(defId);
                    }
                } else if(trigger instanceof LimitPeriodicTrigger) {
                    LimitPeriodicTrigger limitPeriodicTrigger = (LimitPeriodicTrigger) trigger;
                    Date limitEnd = limitPeriodicTrigger.getLimitEnd();
                    if (limitEnd != null && date.after(limitEnd)) {
                        needRmDefIdList.add(defId);
                    }
                } else if(trigger instanceof DelayTrigger) {
                    DelayTrigger delayTrigger = (DelayTrigger) trigger;
                    if(delayTrigger.isHasExec()) {
                        needRmDefIdList.add(defId);
                    }
                }
            }
        });

        if (needRmDefIdList.size() > 0) {
            needRmDefIdList.stream().forEach(defId -> {
                DEF_ID_MAP_SCHEDULE_TASK.remove(defId);
            });
        }
    }
}
