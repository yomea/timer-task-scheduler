package com.shinemo.task.dal.model;

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
     * 1: 固定定时，2:固定延时，3:一次延时，4:cron
     */
    private Integer smcTimerType;

    /**
     * 初始延时时间
     */
    private Long smcInitDelay;

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

    /**
     * 可视化类型，用于实现翻译策略，一般和任务类型一样1:API抽取定时任务
     */
    private Integer smcViewType;
}