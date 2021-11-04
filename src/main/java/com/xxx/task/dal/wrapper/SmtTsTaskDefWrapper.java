package com.xxx.task.dal.wrapper;

import com.xxx.perform.common.mybatis.Page;
import com.xxx.perform.common.mybatis.PageHelper;
import com.xxx.task.dal.mapper.SmtTsTaskDefMapper;
import com.xxx.task.dal.model.SmtTsTaskDef;
import com.xxx.task.dal.model.SmtTsTaskDefQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsTaskDefWrapper {
    @Resource
    private SmtTsTaskDefMapper smtTsTaskDefMapper;

    public Page<SmtTsTaskDef> pageBy(SmtTsTaskDefQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null){
    PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
}else{
    PageHelper.startPage(0, 999999999, query.getOrderByStr());}

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