package com.xxx.task.dal.wrapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.task.dal.mapper.SmtTsTaskLockMapper;
import com.xxx.task.dal.model.SmtTsTaskLock;
import com.xxx.task.dal.model.SmtTsTaskLockQuery;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SmtTsTaskLockWrapper {

    @Resource
    private SmtTsTaskLockMapper smtTsTaskLockMapper;

    public Page<SmtTsTaskLock> pageBy(SmtTsTaskLockQuery query) {
        if (query.getPageIndex() != null && query.getPageSize() != null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        }

        return smtTsTaskLockMapper.pageBy(query);
    }

    public Page<SmtTsTaskLock> pageTimeoutBy(SmtTsTaskLockQuery query) {
        if (query.getPageIndex() != null && query.getPageSize() != null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        } else {
            PageHelper.startPage(0, 999999999, query.getOrderByStr());
        }

        return smtTsTaskLockMapper.pageTimeoutBy(query);
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

    public int updateStatusByOldStatus(Long startTime, Long id, String ip, Integer newStatus, Integer oldStatus) {
        return smtTsTaskLockMapper.updateStatusByOldStatus(startTime, id, ip, newStatus, oldStatus);
    }

    public Integer updateStatusByIdList(List<Long> idList, Integer newStatus, Integer oldStatus) {
        return smtTsTaskLockMapper.updateStatusByIdList(idList, newStatus, oldStatus);
    }

    public Integer deleteByDefIdList(List<Long> defIdList) {
        return smtTsTaskLockMapper.deleteByDefIdList(defIdList);
    }
}