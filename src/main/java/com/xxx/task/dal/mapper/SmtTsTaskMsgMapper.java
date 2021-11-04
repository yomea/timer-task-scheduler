package com.xxx.task.dal.mapper;

import com.xxx.perform.common.mybatis.Page;
import com.xxx.task.dal.model.SmtTsTaskMsg;
import com.xxx.task.dal.model.SmtTsTaskMsgQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskMsgMapper {
    int insertSelective(SmtTsTaskMsg record);

    SmtTsTaskMsg getBy(SmtTsTaskMsgQuery query);

    List<SmtTsTaskMsg> selectBy(SmtTsTaskMsgQuery query);

    Integer countBy(SmtTsTaskMsgQuery query);

    Integer updateById(SmtTsTaskMsg record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsTaskMsg> pageBy(SmtTsTaskMsgQuery query);

    Integer cleanOldTaskUpdateMsg(SmtTsTaskMsgQuery query);

    String getCurDbDate();
}