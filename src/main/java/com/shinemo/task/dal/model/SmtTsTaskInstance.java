package com.shinemo.task.dal.model;

import lombok.Data;

import java.util.Date;

/**
 * SmtTsTaskInstance
 */
@Data
public class SmtTsTaskInstance {
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 执行开始时间
     */
    private Date smcStime;

    /**
     * 执行结束时间
     */
    private Date smcEtime;

    /**
     * 任务定义ID
     */
    private Integer smcDefId;

    /**
     * 任务执行超时时间，单位s，超时将视为执行失败，会重跑
     */
    private Integer smcTimeout;

    /**
     * 任务状态，-1:失败，0: 待执行，1:执行中，2:执行即将完成，3:执行完成
     */
    private Integer smcStatus;

    /**
     * 失败原因
     */
    private String smcError;
}