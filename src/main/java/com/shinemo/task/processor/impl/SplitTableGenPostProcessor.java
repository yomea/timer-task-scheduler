package com.shinemo.task.processor.impl;

import com.shinemo.mybatis.common.interceptor.enums.DateSplitType;
import com.shinemo.mybatis.common.interceptor.util.TableUtils;
import com.shinemo.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.shinemo.task.processor.TaskConfigurePostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

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

        Date curDate = new Date();

        //当月分表
        String newTableName = tableName + "_" + DateFormatUtils.format(curDate, "yyyyMM");

        smtTsTaskRecordWrapper.createMontTable(newTableName);

        //下月分表，避免时间接近导致表不存在的错误
        Date nextMonth = DateUtils.addMonths(curDate, 1);
        String nextNewTableName = tableName + "_" + DateFormatUtils.format(nextMonth, "yyyyMM");

        smtTsTaskRecordWrapper.createMontTable(nextNewTableName);
    }
}
