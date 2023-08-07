package com.xxx.task.dal.model;

import lombok.Data;

/**
 * SmtTsConsumeProgressQuery
 */
@Data
public class SmtTsConsumeProgressQuery {
    private Long id;

    /**
     * 消费机器ip
     */
    private String smcIp;

    /**
     * 消费进度id
     */
    private Long smcMsgId;

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;
}