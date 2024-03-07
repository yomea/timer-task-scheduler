package com.hangu.task.core;

import com.hangu.common.entity.RequestHandlerInfo;
import com.hangu.common.entity.ServerInfo;
import com.hangu.common.properties.ExecutorProperties;
import com.hangu.common.properties.HanguProperties;
import com.hangu.common.registry.RegistryService;
import com.hangu.consumer.reference.ReferenceBean;
import com.hangu.consumer.reference.ServiceReference;
import com.hangu.task.constant.TaskScheduleConstant;
import com.hangu.task.hangu.TaskSchedulerWorker;
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
    private static HanguProperties hanguProperties;

    /**
     * 从 hanggu-rpc 自动装配
     *
     * @param registryService
     * @param hanguProperties
     */
    public TaskHandlerBeanFactory(RegistryService registryService, HanguProperties hanguProperties) {
        TaskHandlerBeanFactory.registryService = registryService;
        TaskHandlerBeanFactory.hanguProperties = hanguProperties;
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
            if (worker != null) {
                return;
            }

            String groupName = TaskScheduleConstant.WORKER_PROXY_NAME + appServiceName;

            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setGroupName(groupName);
            serverInfo.setInterfaceName(TaskScheduleConstant.WORKER_INTERFACE_NAME);
            serverInfo.setVersion("");

            ExecutorProperties executorProperties = new ExecutorProperties();
            executorProperties.setMaxNum(hanguProperties.getMaxNum());
            executorProperties.setCoreNum(hanguProperties.getCoreNum());
            RequestHandlerInfo requestHandlerInfo = new RequestHandlerInfo();
            requestHandlerInfo.setServerInfo(serverInfo);
            ReferenceBean<TaskSchedulerWorker> referenceBean = new ReferenceBean<>(requestHandlerInfo,
                TaskSchedulerWorker.class, registryService, executorProperties);
            worker = ServiceReference.reference(referenceBean);

            registryWorker(appServiceName, worker);
        } catch (Exception e) {
            throw new RuntimeException("注册worker失败！", e);
        } finally {
            LOCK_MAP.remove(appServiceName);
        }
    }

    public static TaskSchedulerWorker getWorker(String appServiceName) {

        TaskSchedulerWorker worker = APP_SERVICE_NAME_MAP_WORKER.get(appServiceName);
        if (worker == null) {
            registryWorker(appServiceName);
        }
        return APP_SERVICE_NAME_MAP_WORKER.get(appServiceName);
    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
