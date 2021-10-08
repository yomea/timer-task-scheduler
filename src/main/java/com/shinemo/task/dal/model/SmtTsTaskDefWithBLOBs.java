package com.shinemo.task.dal.model;

import lombok.Data;
import lombok.ToString;

/**
 * SmtTsTaskDefWithBLOBs
 */
@Data
@ToString(callSuper = true)
public class SmtTsTaskDefWithBLOBs extends SmtTsTaskDef {
    /**
     * 扩展字段
     */
    private String smcExtend;
}