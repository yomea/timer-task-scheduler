package com.shinemo.task.dal.mapper;

import com.shinemo.task.dal.model.SmtTsTaskTimerQuery;
import com.shinemo.task.dal.model.SmtTsTaskTimerWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskTimerMapper {
    int insertSelective(SmtTsTaskTimerWithBLOBs record);

    SmtTsTaskTimerWithBLOBs getBy(SmtTsTaskTimerQuery query);

    List<SmtTsTaskTimerWithBLOBs> selectBy(SmtTsTaskTimerQuery query);

    Integer countBy(SmtTsTaskTimerQuery query);

    Integer updateById(SmtTsTaskTimerWithBLOBs record);

    Integer deleteById(@Param("id") Long id);

    List<SmtTsTaskTimerWithBLOBs> pageBy(SmtTsTaskTimerQuery query);
}