package com.xxx.task.dal.wrapper;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.task.dal.mapper.SmtTsTaskMsgMapper;
import com.xxx.task.dal.model.SmtTsTaskMsg;
import com.xxx.task.dal.model.SmtTsTaskMsgQuery;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmtTsTaskMsgWrapper {

    @Resource
    private SmtTsTaskMsgMapper smtTsTaskMsgMapper;

    public Page<SmtTsTaskMsg> pageBy(SmtTsTaskMsgQuery query) {
        if (query.getPageIndex() != null && query.getPageSize() != null) {
            PageHelper.startPage(query.getPageIndex(), query.getPageSize(), query.getOrderByStr());
        }

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

    public Integer cleanOldTaskUpdateMsg(SmtTsTaskMsgQuery query) {
        return smtTsTaskMsgMapper.cleanOldTaskUpdateMsg(query);
    }

    public Date getCurDbDate() {
        String date = smtTsTaskMsgMapper.getCurDbDate();
        Date dbCurDate = DateUtil.parse(date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return dbCurDate;
    }
}