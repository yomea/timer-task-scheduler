package com.xxx.task.dal.model;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import lombok.Data;

/**
 * SmtTsTaskRecord
 */
@Data
public class SmtTsTaskRecord {
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
    private Long smcDefId;

    /**
     * 任务名称
     */
    private String smcTaskName;

    /**
     * 任务执行超时时间，超时将视为执行失败，会重跑
     */
    private Long smcTimeout;

    /**
     * 任务状态，-1:失败，1:执行中，2:执行超时，3:执行完成
     */
    private Integer smcStatus;

    /**
     * 失败原因
     */
    private String smcError;

    /**
     * 描述
     */
    private String smcDesc;

    /**
     * 调度的机器ip
     */
    private String smcIp;

    private int splitKey = Integer.valueOf(DateUtil.format(new Date(), "yyyyMM"));
}