package com.hangu.task.dal.model;

import lombok.Data;

import java.util.Date;

/**
 * SmtTsTaskTimer
 */
@Data
public class SmtTsTaskTimer {
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
     * 任务定义ID
     */
    private Long smcDefId;

    /**
     * 保留字段，目前就支持 cron，1:cron
     */
    private Integer smcTimerType;

    /**
     * 初始延时时间
     */
    private Long smcInitDelay;

    /**
     * 延时时间
     */
    private Long smcOnceDelay;

    /**
     * 定时器有效开启时间
     */
    private Date smcStartDay;

    /**
     * 定时器有效结束时间
     */
    private Date smcEndDay;

    /**
     * 定时周期
     */
    private Long smcPeriod;

    /**
     * cron表达式
     */
    private String smcCron;

    private Integer smcStatus;
}