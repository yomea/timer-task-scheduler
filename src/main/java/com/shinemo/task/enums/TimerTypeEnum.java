package com.shinemo.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时器类型
 * Created by wuzhenhong on 10/9/21 10:22 AM
 */
@Getter
@AllArgsConstructor
public enum TimerTypeEnum {

    //1: 固定定时，2:固定延时，3:一次延时，4:cron
    FIX_RATE(1, "固定定时"),
    FIX_DELAY(2, "固定延时"),
    DELAY_TRIGGER(3, "一次延时"),
    CRON(4, "cron");

    private Integer type;

    private String desc;
}
