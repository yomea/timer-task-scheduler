package com.hangu.task.core;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by wuzhenhong on 10/7/21 4:12 PM
 */
public class LimitCronTrigger extends CronTrigger {


    private Date limitStart;
    private Date limitEnd;

    public LimitCronTrigger(String expression, Date limitStart, Date limitEnd) {
        this(expression, TimeZone.getDefault(), limitStart, limitEnd);
    }

    public LimitCronTrigger(String expression, TimeZone timeZone, Date limitStart, Date limitEnd) {
        super(expression, timeZone);
        this.limitStart = limitStart;
        this.limitEnd = limitEnd;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {

        Date nextDate = super.nextExecutionTime(triggerContext);

        if(limitStart != null && nextDate.compareTo(limitStart) < 0) {
            return null;
        }

        if(limitEnd != null && nextDate.compareTo(limitEnd) > 0) {
            return null;
        }

        return nextDate;
    }

    public Date getLimitStart() {
        return limitStart;
    }

    public Date getLimitEnd() {
        return limitEnd;
    }
}
