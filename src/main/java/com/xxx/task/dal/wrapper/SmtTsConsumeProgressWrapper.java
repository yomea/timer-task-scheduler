package com.xxx.task.dal.wrapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.task.dal.mapper.SmtTsConsumeProgressMapper;
import com.xxx.task.dal.model.SmtTsConsumeProgress;
import com.xxx.task.dal.model.SmtTsConsumeProgressQuery;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SmtTsConsumeProgressWrapper {

    @Resource
    private SmtTsConsumeProgressMapper smtTsConsumeProgressMapper;

    public Page<SmtTsConsumeProgress> pageBy(SmtTsConsumeProgressQuery query) {
        if (query.getPageIndex() != null && query.getPageSize() != null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        }
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