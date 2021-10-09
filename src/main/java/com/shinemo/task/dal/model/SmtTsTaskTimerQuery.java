package com.shinemo.task.dal.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * SmtTsTaskTimerQuery
 */
@Data
public class SmtTsTaskTimerQuery {
    private Long id;

    /**
     * 任务定义ID
     */
    private Long smcDefId;

    private List<Long> smcDefIdList;

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
     * 出发时间
     */
    private Date triggerDate;

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

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;
}