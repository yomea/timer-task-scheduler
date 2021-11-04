package com.xxx.task.dal.wrapper;

import com.xxx.perform.common.mybatis.Page;
import com.xxx.perform.common.mybatis.PageHelper;
import com.xxx.task.dal.mapper.SmtTsTaskRecordCommonMapper;
import com.xxx.task.dal.mapper.SmtTsTaskRecordMapper;
import com.xxx.task.dal.model.SmtTsTaskRecord;
import com.xxx.task.dal.model.SmtTsTaskRecordQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsTaskRecordWrapper {
    @Resource
    private SmtTsTaskRecordMapper smtTsTaskRecordMapper;
    @Resource
    private SmtTsTaskRecordCommonMapper smtTsTaskRecordCommonMapper;

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

    public Integer deleteById(Long id, int splitKey) {
        return smtTsTaskRecordMapper.deleteById(id, splitKey);
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

        smtTsTaskRecordCommonMapper.createMontTable(newTableName);
    }

    public void createMontTableIndex(String newTableName) {

        smtTsTaskRecordCommonMapper.createMontTableIndex(newTableName);
    }

    public Integer checkTableExists(String newTableName) {

        return smtTsTaskRecordCommonMapper.checkTableExists(newTableName);
    }
}