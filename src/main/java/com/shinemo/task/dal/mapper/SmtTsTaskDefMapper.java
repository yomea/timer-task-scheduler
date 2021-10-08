package com.shinemo.task.dal.mapper;

import com.shinemo.task.dal.model.SmtTsTaskDefQuery;
import com.shinemo.task.dal.model.SmtTsTaskDefWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskDefMapper {
    int insertSelective(SmtTsTaskDefWithBLOBs record);

    SmtTsTaskDefWithBLOBs getBy(SmtTsTaskDefQuery query);

    List<SmtTsTaskDefWithBLOBs> selectBy(SmtTsTaskDefQuery query);

    Integer countBy(SmtTsTaskDefQuery query);

    Integer updateById(SmtTsTaskDefWithBLOBs record);

    Integer deleteById(@Param("id") Long id);

    List<SmtTsTaskDefWithBLOBs> pageBy(SmtTsTaskDefQuery query);
}