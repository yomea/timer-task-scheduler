package com.xxx.task.dal.mapper;

import com.xxx.perform.common.mybatis.Page;
import com.xxx.task.dal.model.SmtTsTaskDef;
import com.xxx.task.dal.model.SmtTsTaskDefQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskDefMapper {
    int insertSelective(SmtTsTaskDef record);

    SmtTsTaskDef getBy(SmtTsTaskDefQuery query);

    List<SmtTsTaskDef> selectBy(SmtTsTaskDefQuery query);

    Integer countBy(SmtTsTaskDefQuery query);

    Integer updateById(SmtTsTaskDef record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsTaskDef> pageBy(SmtTsTaskDefQuery query);

    Integer deleteByTopId(Long taskId);
}