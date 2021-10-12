package com.shinemo.task.dal.model;

import lombok.Data;

/**
 * SmtTsTaskMsgQuery
 */
@Data
public class SmtTsTaskMsgQuery {
    private Long id;

    /**
     * 任务定义ID
     */
    private Long smcDefId;

    /**
     * 操作类型，0:新增，1:修改，2:删除
     */
    private Integer smcAction;

    private Integer pageIndex;

    private Integer pageSize;

    private String orderByStr;
}