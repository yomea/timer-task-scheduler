package com.hangu.task.context;

import com.hangu.task.dal.model.SmtTsTaskDef;
import com.hangu.task.dal.wrapper.SmtTsTaskDefWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskLockWrapper;
import com.hangu.task.dal.wrapper.SmtTsTaskRecordWrapper;
import com.hangu.task.listener.HanguTaskSchedulerListener;
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

    private List<HanguTaskSchedulerListener> listenerList;

    private Map<String, Object> extParams;
}
