package com.hangu.task.processor.impl;

import cn.hutool.core.date.DateUtil;
import com.hangu.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.hangu.task.processor.TaskConfigurePostProcessor;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * Created by wuzhenhong on 10/14/21 12:07 PM
 */
@Component
@Slf4j
public class SplitTableGenPostProcessor implements TaskConfigurePostProcessor {

    private static final String TABLE_NAME = "smt_ts_task_record";
    @Resource
    private SmtTsTaskRecordWrapper smtTsTaskRecordWrapper;

    @Override
    public void beforeConfigure(ScheduledTaskRegistrar taskRegistrar) {

        splitTableMothGen(TABLE_NAME);

        CronTask task = new CronTask(() -> {

            splitTableMothGen(TABLE_NAME);
        }, "00 00 00 0/10 * ?");

        taskRegistrar.scheduleCronTask(task);
    }

    @Override
    public void afterConfigure(ScheduledTaskRegistrar taskRegistrar) {

        //nothing to do
    }

    public void splitTableMothGen(String tableName) {

        LocalDateTime curDate = LocalDateTime.now();

        //当月分表
        String newTableName = tableName + "_" + DateUtil.format(curDate, "yyyyMM");

        smtTsTaskRecordWrapper.createMontTable(newTableName);

        //下月分表，避免时间接近导致表不存在的错误
        LocalDateTime nextMonthLdt = curDate.plusMonths(1);
        String nextNewTableName = tableName + "_" + DateUtil.format(nextMonthLdt, "yyyyMM");

        smtTsTaskRecordWrapper.createMontTable(nextNewTableName);
    }
}
