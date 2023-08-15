package com.hangu.task.dal.wrapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hangu.task.dal.mapper.SmtTsTaskRecordCommonMapper;
import com.hangu.task.dal.mapper.SmtTsTaskRecordMapper;
import com.hangu.task.dal.model.SmtTsTaskRecord;
import com.hangu.task.dal.model.SmtTsTaskRecordQuery;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SmtTsTaskRecordWrapper {

    @Resource
    private SmtTsTaskRecordMapper smtTsTaskRecordMapper;
    @Resource
    private SmtTsTaskRecordCommonMapper smtTsTaskRecordCommonMapper;

    public Page<SmtTsTaskRecord> pageBy(SmtTsTaskRecordQuery query) {
        if (query.getPageIndex() != null && query.getPageSize() != null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        }

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
}