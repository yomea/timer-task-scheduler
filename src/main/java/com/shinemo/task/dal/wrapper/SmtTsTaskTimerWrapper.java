package com.shinemo.task.dal.wrapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.perform.common.mybatis.PageHelper;
import com.shinemo.task.dal.mapper.SmtTsTaskTimerMapper;
import com.shinemo.task.dal.model.SmtTsTaskTimer;
import com.shinemo.task.dal.model.SmtTsTaskTimerQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsTaskTimerWrapper {
    @Resource
    private SmtTsTaskTimerMapper smtTsTaskTimerMapper;

    public Page<SmtTsTaskTimer> pageBy(SmtTsTaskTimerQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null){
    PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
}else{
    PageHelper.startPage(0, 999999999, query.getOrderByStr());}

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
}