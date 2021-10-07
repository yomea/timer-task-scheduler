package com.shinemo.task.core;

import org.springframework.scheduling.config.ScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuzhenhong on 10/7/21 2:05 PM
 */
public class TaskMemoryStore {

    private static final Object OBJECT = new Object();
    private static final Map<String, Object> LOCK_MAP = new ConcurrentHashMap<>(16);
    private static final Map<String, List<String>> DEF_ID_MAP_SCHEDULE_TASK_LIST = new HashMap<>(16);
    private static final Map<String, ScheduledTask> ID_MAP_SCHEDULE_TASK = new ConcurrentHashMap<>(16);

    public static void putScheduledTask(String defId, String id, ScheduledTask task) {

        //加锁
        while (LOCK_MAP.putIfAbsent(defId, OBJECT) != null) {
            Thread.yield();
        }

        try {
            List<String> list = DEF_ID_MAP_SCHEDULE_TASK_LIST.get(defId);
            if (list == null) {
                list = new ArrayList<>();
                DEF_ID_MAP_SCHEDULE_TASK_LIST.put(defId, list);
            }
            list.add(id);
        } finally {
            LOCK_MAP.remove(defId);
        }

        ID_MAP_SCHEDULE_TASK.put(id, task);
    }

    public static void cancelByTaskId(String id) {

        ScheduledTask scheduledTask = ID_MAP_SCHEDULE_TASK.get(id);
        if (scheduledTask != null) {
            scheduledTask.cancel();
            ID_MAP_SCHEDULE_TASK.remove(id);
        }
    }

    public static void cancelByTaskDefId(String defId) {

        List<String> list = DEF_ID_MAP_SCHEDULE_TASK_LIST.get(defId);
        if (list != null) {
            list.stream().forEach(id -> {
                cancelByTaskId(id);
            });

        }
    }
}
