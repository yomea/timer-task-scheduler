package com.shinemo.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by wuzhenhong on 10/8/21 1:45 PM
 */
@Getter
@AllArgsConstructor
public enum TaskInstanceEnum {

    //任务状态，-1:失败，0: 待执行，1:执行中，2:执行即将完成，3:执行完成
    FAILURE(-1, "失败！"),
    WAIT_EXEC(0, "待执行"),
    EXEC_ING(1, "执行中"),
    COMPLETING(2, "执行即将完成"),
    FINISHED(3, "执行完成");

    private Integer status;
    private String desc;

}
