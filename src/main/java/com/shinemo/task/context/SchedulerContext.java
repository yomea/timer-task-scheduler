package com.shinemo.task.context;

import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskRecordWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by wuzhenhong on 10/11/21 7:40 PM
 */
@Data
@NoArgsConstructor
@Builder
public class SchedulerContext {

    private SmtTsTaskDef smtTsTaskDef;

    private SmtTsTaskLockWrapper smtTsTaskLockWrapper;

    private SmtTsTaskRecordWrapper smtTsTaskRecordWrapper;

    private Map<String, Object> extParams;
}
