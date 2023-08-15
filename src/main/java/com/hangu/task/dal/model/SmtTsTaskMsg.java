package com.hangu.task.dal.model;

import lombok.Data;

import java.util.Date;

/**
 * SmtTsTaskMsg
 */
@Data
public class SmtTsTaskMsg {
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
     * 操作类型，0:新增，1:修改，2:删除
     */
    private Integer smcAction;
}