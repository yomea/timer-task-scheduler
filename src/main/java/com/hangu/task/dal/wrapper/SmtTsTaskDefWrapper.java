package com.hangu.task.dal.wrapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hangu.task.dal.mapper.SmtTsTaskDefMapper;
import com.hangu.task.dal.model.SmtTsTaskDef;
import com.hangu.task.dal.model.SmtTsTaskDefQuery;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SmtTsTaskDefWrapper {
    @Resource
    private SmtTsTaskDefMapper smtTsTaskDefMapper;

    public Page<SmtTsTaskDef> pageBy(SmtTsTaskDefQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        }

        return smtTsTaskDefMapper.pageBy(query);
    }

    public Integer countBy(SmtTsTaskDefQuery query) {
        return smtTsTaskDefMapper.countBy(query);
    }

    public Integer deleteById(Long id) {
        return smtTsTaskDefMapper.deleteById(id);
    }

    public SmtTsTaskDef getBy(SmtTsTaskDefQuery query) {
        return smtTsTaskDefMapper.getBy(query);
    }

    public int insertSelective(SmtTsTaskDef domain) {
        return smtTsTaskDefMapper.insertSelective(domain);
    }

    public List<SmtTsTaskDef> selectBy(SmtTsTaskDefQuery query) {
        return smtTsTaskDefMapper.selectBy(query);
    }

    public Integer updateById(SmtTsTaskDef domain) {
        return smtTsTaskDefMapper.updateById(domain);
    }

    public Integer deleteByTopId(Long taskId) {

        return smtTsTaskDefMapper.deleteByTopId(taskId);
    }

}