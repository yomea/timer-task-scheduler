package com.shinemo.task.dal.wrapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.perform.common.mybatis.PageHelper;
import com.shinemo.task.dal.mapper.SmtTsTaskMsgMapper;
import com.shinemo.task.dal.model.SmtTsTaskMsg;
import com.shinemo.task.dal.model.SmtTsTaskMsgQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsTaskMsgWrapper {
    @Resource
    private SmtTsTaskMsgMapper smtTsTaskMsgMapper;

    public Page<SmtTsTaskMsg> pageBy(SmtTsTaskMsgQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null){
    PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
}else{
    PageHelper.startPage(0, 999999999, query.getOrderByStr());}

        return smtTsTaskMsgMapper.pageBy(query);
    }

    public Integer countBy(SmtTsTaskMsgQuery query) {
        return smtTsTaskMsgMapper.countBy(query);
    }

    public Integer deleteById(Long id) {
        return smtTsTaskMsgMapper.deleteById(id);
    }

    public SmtTsTaskMsg getBy(SmtTsTaskMsgQuery query) {
        return smtTsTaskMsgMapper.getBy(query);
    }

    public int insertSelective(SmtTsTaskMsg domain) {
        return smtTsTaskMsgMapper.insertSelective(domain);
    }

    public List<SmtTsTaskMsg> selectBy(SmtTsTaskMsgQuery query) {
        return smtTsTaskMsgMapper.selectBy(query);
    }

    public Integer updateById(SmtTsTaskMsg domain) {
        return smtTsTaskMsgMapper.updateById(domain);
    }
}