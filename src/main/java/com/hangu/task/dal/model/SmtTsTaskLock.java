package com.hangu.task.dal.model;

import lombok.Data;

import java.util.Date;

/**
 * SmtTsTaskLock
 */
@Data
public class SmtTsTaskLock {
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
     * 任务定义id
     */
    private Long smcDefId;

    private Long smcDefPid;

    /**
     * 任务执行超时时间，超时时需要释放锁
     */
    private Long smcTimeOut;

    /**
     * 调度的机器ip
     */
    private String smcIp;

    /**
     * 执行状态，-1:失败，1:执行中，2:执行超时，3:执行完成
     */
    private Integer smcStatus;

    private Long smcStartTime;
}