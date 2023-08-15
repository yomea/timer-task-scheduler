package com.hangu.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by wuzhenhong on 10/8/21 1:45 PM
 */
@Getter
@AllArgsConstructor
public enum TaskExecEnum {

    //-1:失败，1:执行中，2:执行超时，3:执行完成
    FAILURE(-1, "失败！"),
    EXEC_ING(1, "执行中"),
    EXEC_TIMEOUT(2, "执行超时"),
    FINISHED(3, "执行完成"),
    RETRY(4, "重试");

    private Integer status;
    private String desc;

}
