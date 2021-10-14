package com.shinemo.task.dal.wrapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.perform.common.mybatis.PageHelper;
import com.shinemo.task.dal.mapper.SmtTsTaskRecordMapper;
import com.shinemo.task.dal.model.SmtTsTaskRecord;
import com.shinemo.task.dal.model.SmtTsTaskRecordQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsTaskRecordWrapper {
    @Resource
    private SmtTsTaskRecordMapper smtTsTaskRecordMapper;

    public Page<SmtTsTaskRecord> pageBy(SmtTsTaskRecordQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null){
    PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
}else{
    PageHelper.startPage(0, 999999999, query.getOrderByStr());}

        return smtTsTaskRecordMapper.pageBy(query);
    }

    public Integer countBy(SmtTsTaskRecordQuery query) {
        return smtTsTaskRecordMapper.countBy(query);
    }

    public Integer deleteById(Long id) {
        return smtTsTaskRecordMapper.deleteById(id);
    }

    public SmtTsTaskRecord getBy(SmtTsTaskRecordQuery query) {
        return smtTsTaskRecordMapper.getBy(query);
    }

    public int insertSelective(SmtTsTaskRecord domain) {
        return smtTsTaskRecordMapper.insertSelective(domain);
    }

    public List<SmtTsTaskRecord> selectBy(SmtTsTaskRecordQuery query) {
        return smtTsTaskRecordMapper.selectBy(query);
    }

    public Integer updateById(SmtTsTaskRecord domain) {
        return smtTsTaskRecordMapper.updateById(domain);
    }

    public void createMontTable(String newTableName) {

        smtTsTaskRecordMapper.createMontTable(newTableName);
    }
}