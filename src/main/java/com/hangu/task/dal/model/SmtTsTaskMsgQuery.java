package com.hangu.task.dal.model;

import lombok.Data;

import java.util.Date;

/**
 * SmtTsTaskMsgQuery
 */
@Data
public class SmtTsTaskMsgQuery {

    private Long id;

    private Long gtId;

    private Date ltDate;

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

    private Date appStartTime;
}