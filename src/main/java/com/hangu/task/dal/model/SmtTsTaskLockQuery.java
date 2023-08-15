package com.hangu.task.dal.model;

import lombok.Data;

import java.util.List;

/**
 * SmtTsTaskLockQuery
 */
@Data
public class SmtTsTaskLockQuery {
    private Long id;

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

    private List<String> notInIps;

    /**
     * 执行状态，-1:失败，1:执行中，2:执行超时，3:执行完成
     */
    private Integer smcStatus;

    private List<Integer> statusList;

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;

    private Long smcStartTime;

    private Long ltStartTime;
}