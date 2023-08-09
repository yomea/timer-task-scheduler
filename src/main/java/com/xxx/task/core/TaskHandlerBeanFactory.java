package com.xxx.task.core;

import com.hanggu.common.registry.RegistryService;
import com.hanggu.consumer.factory.ReferenceFactoryBean;
import com.xxx.task.constant.TaskScheduleConstant;
import com.xxx.task.hangu.TaskSchedulerWorker;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by wuzhenhong on 10/11/21 3:47 PM
 */
@Component
public class TaskHandlerBeanFactory implements EnvironmentAware {

    private static RegistryService registryService;
    public TaskHandlerBeanFactory(RegistryService registryService) {
        this.registryService = registryService;
    }

    private static final Object OBJECT = new Object();
    private static final Map<String, Object> LOCK_MAP = new ConcurrentHashMap<>();
    private static final Map<String, TaskSchedulerWorker> APP_SERVICE_NAME_MAP_WORKER = new ConcurrentHashMap<>(16);

    public static void registryWorker(String appServiceName, TaskSchedulerWorker worker) {

        APP_SERVICE_NAME_MAP_WORKER.put(appServiceName, worker);
    }

    public static void registryWorker(String appServiceName) {

        while (LOCK_MAP.putIfAbsent(appServiceName, OBJECT) != null) {
            Thread.yield();
        }

        try {

            TaskSchedulerWorker worker = APP_SERVICE_NAME_MAP_WORKER.get(appServiceName);
            if(worker != null) {
                return;
            }

            String groupName = TaskScheduleConstant.WORKER_PROXY_NAME + appServiceName;

            ReferenceFactoryBean<TaskSchedulerWorker> config =
                new ReferenceFactoryBean<>(groupName,
                    TaskScheduleConstant.WORKER_INTERFACE_NAME,
                    "", TaskSchedulerWorker.class, registryService);

            // 初始化
            config.afterPropertiesSet();

            worker = config.getObject();

            registryWorker(appServiceName, worker);
        } catch (Exception e) {
            throw new RuntimeException("注册worker失败！", e);
        } finally {
            LOCK_MAP.remove(appServiceName);
        }
    }

    public static TaskSchedulerWorker getWorker(String appServiceName) {

        TaskSchedulerWorker worker = APP_SERVICE_NAME_MAP_WORKER.get(appServiceName);
        if(worker == null) {
            registryWorker(appServiceName);
        }
        return APP_SERVICE_NAME_MAP_WORKER.get(appServiceName);
    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
