package com.shinemo.task.dal.model;

import lombok.Data;
import lombok.ToString;

/**
 * SmtTsTaskHistoryWithBLOBs
 */
@Data
@ToString(callSuper = true)
public class SmtTsTaskHistoryWithBLOBs extends SmtTsTaskHistory {
    /**
     * 扩展字段，用于保存执行上下文参数
     */
    private String smcExtend;
}