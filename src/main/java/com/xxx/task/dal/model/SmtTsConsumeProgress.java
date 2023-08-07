package com.xxx.task.dal.model;

import lombok.Data;

import java.util.Date;

/**
 * SmtTsConsumeProgress
 */
@Data
public class SmtTsConsumeProgress {
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
     * 消费机器ip
     */
    private String smcIp;

    /**
     * 消费进度id
     */
    private Long smcMsgId;
}