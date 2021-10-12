package com.shinemo.task.core;

import org.springframework.scheduling.config.ScheduledTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuzhenhong on 10/7/21 2:05 PM
 */
public class TaskMemoryStore {

    private static final Map<Long, ScheduledTask> DEF_ID_MAP_SCHEDULE_TASK = new ConcurrentHashMap<>(16);

    public static void putScheduledTask(Long defId, ScheduledTask task) {

        DEF_ID_MAP_SCHEDULE_TASK.put(defId, task);
    }

    public static void cancelByTaskDefId(String defId) {

        ScheduledTask scheduledTask = DEF_ID_MAP_SCHEDULE_TASK.remove(defId);

        if (scheduledTask == null) {
            return;
        }

        scheduledTask.cancel();
    }

    public static void cancelInvaildTask() {

        Date date = new Date();
        List<Long> needRmDefIdList = new ArrayList<>();

        DEF_ID_MAP_SCHEDULE_TASK.forEach((defId, scheduledTask) -> {

            if (scheduledTask != null && (scheduledTask.getTask() instanceof LimitCronTask)) {

                LimitCronTask limitCronTask = (LimitCronTask) scheduledTask.getTask();
                Date limitEnd = limitCronTask.getLimitEnd();
                if (date.after(limitEnd)) {
                    needRmDefIdList.add(defId);
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
