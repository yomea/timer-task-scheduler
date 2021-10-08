package com.shinemo.task.dal.mapper;

import com.shinemo.task.dal.model.SmtTsTaskInstanceQuery;
import com.shinemo.task.dal.model.SmtTsTaskInstanceWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskInstanceMapper {
    int insertSelective(SmtTsTaskInstanceWithBLOBs record);

    SmtTsTaskInstanceWithBLOBs getBy(SmtTsTaskInstanceQuery query);

    List<SmtTsTaskInstanceWithBLOBs> selectBy(SmtTsTaskInstanceQuery query);

    Integer countBy(SmtTsTaskInstanceQuery query);

    Integer updateById(SmtTsTaskInstanceWithBLOBs record);

    Integer deleteById(@Param("id") Long id);

    List<SmtTsTaskInstanceWithBLOBs> pageBy(SmtTsTaskInstanceQuery query);
}