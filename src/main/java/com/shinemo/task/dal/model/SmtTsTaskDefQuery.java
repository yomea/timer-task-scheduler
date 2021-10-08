package com.shinemo.task.dal.model;

import lombok.Data;

/**
 * SmtTsTaskDefQuery
 */
@Data
public class SmtTsTaskDefQuery {
    private Long id;

    /**
     * 任务类型，1: 数据源抽取，1000: other
     */
    private Integer smcTaskType;

    /**
     * 调度类型，1: 定时任务，2:一次性任务
     */
    private Integer smcScheduleType;

    /**
     * 配置标记，第1位表示是否异步任务
     */
    private Integer smcConfFlag;

    /**
     * 任务执行超时时间，单位s，超时将视为执行失败，会重跑，-1表示永不超时
     */
    private Integer smcTimeout;

    /**
     * 任务是否启动，1:启动，-1:禁用
     */
    private Integer smcStatus;

    /**
     * 删除标志，0:未删除，1:已删除
     */
    private Byte smcDelFlag;

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;
}