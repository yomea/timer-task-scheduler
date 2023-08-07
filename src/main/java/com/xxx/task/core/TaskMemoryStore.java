package com.xxx.task.core;

import com.google.common.collect.Maps;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.CancelTaskWithoutInterruptUtils;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by wuzhenhong on 10/7/21 2:05 PM
 */
public class TaskMemoryStore {

    private static final Object OBJECT = new Object();
    private static final Map<Long, Object> LOCK_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, List<ScheduledTask>> DEF_ID_MAP_SCHEDULE_TASK = new ConcurrentHashMap<>(16);

    public static void putScheduledTask(Long defId, ScheduledTask task) {

        dealWithLocalLock(defId, () -> {

            List<ScheduledTask> taskList = DEF_ID_MAP_SCHEDULE_TASK.get(defId);
            if(taskList == null) {
                taskList = new ArrayList<>();
                DEF_ID_MAP_SCHEDULE_TASK.put(defId, taskList);
            }
            taskList.add(task);
        });
    }

    public static void cancelByTaskDefId(Long defId) {

        List<ScheduledTask> scheduledTaskList = DEF_ID_MAP_SCHEDULE_TASK.remove(defId);

        if (CollectionUtils.isEmpty(scheduledTaskList)) {
            return;
        }
        scheduledTaskList.stream().forEach(scheduledTask -> {

            CancelTaskWithoutInterruptUtils.cancel(scheduledTask, false);
            //spring提供的这个取消方法会中断线程，导致后续出现一些不可预知的错误，比如进行数据库操作时，会导致异常
//        scheduledTask.cancel();
        });
    }

    public static void cancelInvaildTask() {

        Date date = new Date();
        Map<Long, List<ScheduledTask>> defIdMapNeedRmTask = Maps.newHashMap();


        DEF_ID_MAP_SCHEDULE_TASK.forEach((defId, scheduledTaskList) -> {

            if(!CollectionUtils.isEmpty(scheduledTaskList)) {

                scheduledTaskList.stream().forEach(scheduledTask -> {
                    if (scheduledTask != null && (scheduledTask.getTask() instanceof TriggerTask)) {

                        TriggerTask triggerTask = (TriggerTask) scheduledTask.getTask();
                        Trigger trigger = triggerTask.getTrigger();
                        if(trigger instanceof LimitCronTrigger) {
                            LimitCronTrigger limitCronTrigger = (LimitCronTrigger) trigger;
                            Date limitEnd = limitCronTrigger.getLimitEnd();
                            if (limitEnd != null && date.after(limitEnd)) {
                                recordNeedRmTaskMap(defIdMapNeedRmTask, defId, scheduledTask);
                            }
                        } else if(trigger instanceof LimitPeriodicTrigger) {
                            LimitPeriodicTrigger limitPeriodicTrigger = (LimitPeriodicTrigger) trigger;
                            Date limitEnd = limitPeriodicTrigger.getLimitEnd();
                            if (limitEnd != null && date.after(limitEnd)) {
                                recordNeedRmTaskMap(defIdMapNeedRmTask, defId, scheduledTask);
                            }
                        } else if(trigger instanceof DelayTrigger) {
                            DelayTrigger delayTrigger = (DelayTrigger) trigger;
                            if(delayTrigger.isHasExec()) {
                                recordNeedRmTaskMap(defIdMapNeedRmTask, defId, scheduledTask);
                            }
                        }
                    }
                });
            }
        });

        if (defIdMapNeedRmTask.size() > 0) {
            defIdMapNeedRmTask.forEach((defId, taskList) -> {

                dealWithLocalLock(defId, () -> {
                    List<ScheduledTask> scheduledTaskList = DEF_ID_MAP_SCHEDULE_TASK.get(defId);
                    if(scheduledTaskList != null && scheduledTaskList.size() > 0) {
                        scheduledTaskList.removeAll(taskList);
                    }
                });
            });
        }
    }

    private static void dealWithLocalLock(Long defId, Runnable runnable) {

        while (LOCK_MAP.putIfAbsent(defId, OBJECT) != null) {
            Thread.yield();
        }
        try {
            runnable.run();
        } finally {
            LOCK_MAP.remove(defId);
        }
    }

    private static void recordNeedRmTaskMap(Map<Long, List<ScheduledTask>> defIdMapNeedRmTask, Long defId, ScheduledTask scheduledTask) {

        List<ScheduledTask> taskList = defIdMapNeedRmTask.get(defId);
        if(taskList == null) {
            taskList = new ArrayList<>();
            defIdMapNeedRmTask.put(defId, taskList);
        }

        taskList.add(scheduledTask);
    }

    public static Map<Long, Object> getLockMap() {
        return LOCK_MAP;
    }

    public static Map<Long, List<ScheduledTask>> getDefIdMapScheduleTask() {
        return DEF_ID_MAP_SCHEDULE_TASK;
    }
}
