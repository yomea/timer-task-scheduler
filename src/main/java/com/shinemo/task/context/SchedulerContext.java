package com.shinemo.task.context;

import com.shinemo.task.dal.model.SmtTsTaskDef;
import com.shinemo.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.shinemo.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.shinemo.task.listener.AceTaskSchedulerListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzhenhong on 10/11/21 7:40 PM
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SchedulerContext {

    private TransactionTemplate transactionTemplate;

    private SmtTsTaskDef smtTsTaskDef;

    private SmtTsTaskLockWrapper smtTsTaskLockWrapper;

    private SmtTsTaskRecordWrapper smtTsTaskRecordWrapper;

    private SmtTsTaskDefWrapper smtTsTaskDefWrapper;

    private List<AceTaskSchedulerListener> listenerList;

    private Map<String, Object> extParams;
}
