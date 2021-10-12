package com.shinemo.task.dal.wrapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.perform.common.mybatis.PageHelper;
import com.shinemo.task.dal.mapper.SmtTsConsumeProgressMapper;
import com.shinemo.task.dal.model.SmtTsConsumeProgress;
import com.shinemo.task.dal.model.SmtTsConsumeProgressQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsConsumeProgressWrapper {
    @Resource
    private SmtTsConsumeProgressMapper smtTsConsumeProgressMapper;

    public Page<SmtTsConsumeProgress> pageBy(SmtTsConsumeProgressQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null){
    PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
}else{
    PageHelper.startPage(0, 999999999, query.getOrderByStr());}

        return smtTsConsumeProgressMapper.pageBy(query);
    }

    public Integer countBy(SmtTsConsumeProgressQuery query) {
        return smtTsConsumeProgressMapper.countBy(query);
    }

    public Integer deleteById(Long id) {
        return smtTsConsumeProgressMapper.deleteById(id);
    }

    public SmtTsConsumeProgress getBy(SmtTsConsumeProgressQuery query) {
        return smtTsConsumeProgressMapper.getBy(query);
    }

    public int insertSelective(SmtTsConsumeProgress domain) {
        return smtTsConsumeProgressMapper.insertSelective(domain);
    }

    public List<SmtTsConsumeProgress> selectBy(SmtTsConsumeProgressQuery query) {
        return smtTsConsumeProgressMapper.selectBy(query);
    }

    public Integer updateById(SmtTsConsumeProgress domain) {
        return smtTsConsumeProgressMapper.updateById(domain);
    }
}