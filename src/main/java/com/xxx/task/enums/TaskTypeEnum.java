package com.xxx.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by wuzhenhong on 10/9/21 10:26 AM
 */
@Getter
@AllArgsConstructor
public enum TaskTypeEnum {

    HTTP_INVOKE(1, "http调用类型任务");

    private Integer type;

    private String desc;
}
