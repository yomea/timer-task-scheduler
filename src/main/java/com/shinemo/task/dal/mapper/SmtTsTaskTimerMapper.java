package com.shinemo.task.dal.mapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.task.dal.model.SmtTsTaskTimer;
import com.shinemo.task.dal.model.SmtTsTaskTimerQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskTimerMapper {
    int insertSelective(SmtTsTaskTimer record);

    SmtTsTaskTimer getBy(SmtTsTaskTimerQuery query);

    List<SmtTsTaskTimer> selectBy(SmtTsTaskTimerQuery query);

    Integer countBy(SmtTsTaskTimerQuery query);

    Integer updateById(SmtTsTaskTimer record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsTaskTimer> pageBy(SmtTsTaskTimerQuery query);
}