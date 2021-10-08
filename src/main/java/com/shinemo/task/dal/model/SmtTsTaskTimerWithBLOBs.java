package com.shinemo.task.dal.model;

import lombok.Data;
import lombok.ToString;

/**
 * SmtTsTaskTimerWithBLOBs
 */
@Data
@ToString(callSuper = true)
public class SmtTsTaskTimerWithBLOBs extends SmtTsTaskTimer {
    /**
     * 用于可视化翻译的字段
     */
    private String smcViewExtend;

    /**
     * 扩展字段
     */
    private String smcCommonExtend;
}