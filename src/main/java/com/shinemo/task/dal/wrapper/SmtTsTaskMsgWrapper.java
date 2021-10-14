package com.shinemo.task.dal.wrapper;

import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.perform.common.mybatis.PageHelper;
import com.shinemo.task.dal.mapper.SmtTsTaskMsgMapper;
import com.shinemo.task.dal.model.SmtTsTaskMsg;
import com.shinemo.task.dal.model.SmtTsTaskMsgQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
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

    public Integer cleanOldTaskUpdateMsg(SmtTsTaskMsgQuery query) {
        return smtTsTaskMsgMapper.cleanOldTaskUpdateMsg(query);
    }

    public Date getCurDbDate() {
        String date =  smtTsTaskMsgMapper.getCurDbDate();
        Date dbCurDate;
        try {
            dbCurDate = DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error("时间格式解析错误！", e);
            throw new ApiException("时间格式解析错误！", e);
        }
        return dbCurDate;
    }
}