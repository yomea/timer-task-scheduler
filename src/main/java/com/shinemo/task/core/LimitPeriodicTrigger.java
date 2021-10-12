package com.shinemo.task.core;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;

/**
 * Created by wuzhenhong on 10/7/21 4:12 PM
 */
public class LimitPeriodicTrigger extends PeriodicTrigger {


    private Date limitStart;
    private Date limitEnd;

    public LimitPeriodicTrigger(long period, Date limitStart, Date limitEnd) {
        super(period);
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
