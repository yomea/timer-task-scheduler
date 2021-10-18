package com.shinemo.task.core;

import com.shinemo.ace4j.Ace;
import com.shinemo.ace4j.client.AaceClientConfig;
import com.shinemo.ace4j.protocol.Codec;
import com.shinemo.ace4j.spring.AceProperties;
import com.shinemo.task.ace.TaskSchedulerWorker;
import com.shinemo.task.constant.TaskScheduleConstant;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuzhenhong on 10/11/21 3:47 PM
 */
@Component
public class TaskHandlerBeanFactory implements EnvironmentAware {

    private static String ACE_URI;
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


            String proxy = TaskScheduleConstant.WORKER_PROXY_NAME + appServiceName;

            AaceClientConfig<TaskSchedulerWorker> config = new AaceClientConfig();
            config.setInterfaceClass(TaskSchedulerWorker.class);
            config.setUri(ACE_URI);
            config.setCodec(Codec.ACE_PLUS);
            config.setInterfaceName(TaskScheduleConstant.WORKER_INTERFACE_NAME);
            config.setProxy(proxy);

            worker = Ace.getAndStart().serviceLookup().lookup(config);

            registryWorker(appServiceName, worker);
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

        ACE_URI = AceProperties.getCenterUri(environment);
    }
}
