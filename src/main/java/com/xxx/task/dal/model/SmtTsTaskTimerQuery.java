package com.xxx.task.dal.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * SmtTsTaskTimerQuery
 */
@Data
public class SmtTsTaskTimerQuery {
    private Long id;

    private Integer smcStatus;
    /**
     * 任务定义ID
     */
    private Long smcDefId;

    private List<Long> smcDefIdList;

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

    private Date triggerDate;

    /**
     * 定时周期
     */
    private Long smcPeriod;

    /**
     * cron表达式
     */
    private String smcCron;

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;
}