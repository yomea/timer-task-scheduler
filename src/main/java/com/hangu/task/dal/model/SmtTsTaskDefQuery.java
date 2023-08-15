package com.hangu.task.dal.model;

import lombok.Data;

import java.util.List;

/**
 * SmtTsTaskDefQuery
 */
@Data
public class SmtTsTaskDefQuery {
    private Long id;

    private Long smcDefPid;

    private List<Long> idList;

    /**
     * 任务名称
     */
    private String smcTaskName;

    /**
     * 注册到注册中心的服务名
     */
    private String appServiceName;

    /**
     * 接口服务名
     */
    private String apiServiceName;

    /**
     * 接口方法名，为了简单期间，方法不要重载
     */
    private String apiMethodName;

    private String smcCustomerId;

    /**
     * 配置标记,预留字段
     */
    private Integer smcConfFlag;

    /**
     * 任务执行超时时间，单位ms，超时将视为执行失败，会重跑，-1表示永不超时
     */
    private Long smcTimeout;

    /**
     * 任务是否启动，1:启动，-1:禁用
     */
    private Integer smcStatus;

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;

    private boolean exculeSubTask;

    private Boolean smcHasChild;

    private String smcExt;
}