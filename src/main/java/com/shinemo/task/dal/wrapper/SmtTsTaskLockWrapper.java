package com.shinemo.task.dal.wrapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.perform.common.mybatis.PageHelper;
import com.shinemo.task.dal.mapper.SmtTsTaskLockMapper;
import com.shinemo.task.dal.model.SmtTsTaskLock;
import com.shinemo.task.dal.model.SmtTsTaskLockQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SmtTsTaskLockWrapper {
    @Resource
    private SmtTsTaskLockMapper smtTsTaskLockMapper;

    public Page<SmtTsTaskLock> pageBy(SmtTsTaskLockQuery query) {
        if(query.getPageIndex()!=null && query.getPageSize()!=null){
    PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
}else{
    PageHelper.startPage(0, 999999999, query.getOrderByStr());}

        return smtTsTaskLockMapper.pageBy(query);
    }

    public Integer countBy(SmtTsTaskLockQuery query) {
        return smtTsTaskLockMapper.countBy(query);
    }

    public Integer deleteById(Long id) {
        return smtTsTaskLockMapper.deleteById(id);
    }

    public SmtTsTaskLock getBy(SmtTsTaskLockQuery query) {
        return smtTsTaskLockMapper.getBy(query);
    }

    public int insertSelective(SmtTsTaskLock domain) {
        return smtTsTaskLockMapper.insertSelective(domain);
    }

    public List<SmtTsTaskLock> selectBy(SmtTsTaskLockQuery query) {
        return smtTsTaskLockMapper.selectBy(query);
    }

    public Integer updateById(SmtTsTaskLock domain) {
        return smtTsTaskLockMapper.updateById(domain);
    }

    public Integer lock(SmtTsTaskLock lock) {
        return smtTsTaskLockMapper.lock(lock);
    }

    public int updateStatusByOldStatus(Long id, Integer newStatus, Integer oldStatus) {
        return smtTsTaskLockMapper.updateStatusByOldStatus(id, newStatus, oldStatus);
    }

    public Integer updateStatusByIdList(List<Long> idList, Integer newStatus, Integer oldStatus) {
        return smtTsTaskLockMapper.updateStatusByIdList(idList, newStatus, oldStatus);
    }
}