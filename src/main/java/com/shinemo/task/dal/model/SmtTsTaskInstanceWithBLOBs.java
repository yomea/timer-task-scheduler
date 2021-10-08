package com.shinemo.task.dal.model;

import lombok.Data;
import lombok.ToString;

/**
 * SmtTsTaskInstanceWithBLOBs
 */
@Data
@ToString(callSuper = true)
public class SmtTsTaskInstanceWithBLOBs extends SmtTsTaskInstance {
    /**
     * 扩展字段，用于保存执行上下文参数
     */
    private String smcExtend;
}