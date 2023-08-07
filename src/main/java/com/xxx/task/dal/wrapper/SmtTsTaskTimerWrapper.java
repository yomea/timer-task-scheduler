package com.xxx.task.dal.wrapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.task.dal.mapper.SmtTsTaskTimerMapper;
import com.xxx.task.dal.model.SmtTsTaskTimer;
import com.xxx.task.dal.model.SmtTsTaskTimerQuery;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SmtTsTaskTimerWrapper {

    @Resource
    private SmtTsTaskTimerMapper smtTsTaskTimerMapper;

    public Page<SmtTsTaskTimer> pageBy(SmtTsTaskTimerQuery query) {
        if (query.getPageIndex() != null && query.getPageSize() != null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        }

        return smtTsTaskTimerMapper.pageBy(query);
    }

    public Integer countBy(SmtTsTaskTimerQuery query) {
        return smtTsTaskTimerMapper.countBy(query);
    }

    public Integer deleteById(Long id) {
        return smtTsTaskTimerMapper.deleteById(id);
    }

    public SmtTsTaskTimer getBy(SmtTsTaskTimerQuery query) {
        return smtTsTaskTimerMapper.getBy(query);
    }

    public int insertSelective(SmtTsTaskTimer domain) {
        return smtTsTaskTimerMapper.insertSelective(domain);
    }

    public List<SmtTsTaskTimer> selectBy(SmtTsTaskTimerQuery query) {
        return smtTsTaskTimerMapper.selectBy(query);
    }

    public Integer updateById(SmtTsTaskTimer domain) {
        return smtTsTaskTimerMapper.updateById(domain);
    }

    public Integer batchSave(List<SmtTsTaskTimer> taskTimerList) {
        return smtTsTaskTimerMapper.batchSave(taskTimerList);
    }

    public Integer deleteByTaskId(Long taskId) {
        return smtTsTaskTimerMapper.deleteByTaskId(taskId);
    }

    public Integer deleteByIdList(List<Long> delayTaskTimerIdList) {
        return smtTsTaskTimerMapper.deleteByIdList(delayTaskTimerIdList);
    }

    public Integer modifyStatusByIdList(Integer status, List<Long> delayTaskTimerIdList) {
        return smtTsTaskTimerMapper.modifyStatusByIdList(status, delayTaskTimerIdList);
    }
}