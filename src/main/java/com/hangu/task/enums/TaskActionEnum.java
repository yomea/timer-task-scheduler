package com.hangu.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by wuzhenhong on 10/12/21 8:15 PM
 */
@Getter
@AllArgsConstructor
public enum TaskActionEnum {

    //操作类型，0:新增，1:修改，2:删除
    NEW(0, "新增"),
    MODIFY(1, "修改"),
    DELETE(2, "删除");

    private Integer type;
    private String desc;
}
