package com.shinemo.task.dal.mapper;

import com.shinemo.task.dal.model.SmtTsTaskHistoryQuery;
import com.shinemo.task.dal.model.SmtTsTaskHistoryWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskHistoryMapper {
    int insertSelective(SmtTsTaskHistoryWithBLOBs record);

    SmtTsTaskHistoryWithBLOBs getBy(SmtTsTaskHistoryQuery query);

    List<SmtTsTaskHistoryWithBLOBs> selectBy(SmtTsTaskHistoryQuery query);

    Integer countBy(SmtTsTaskHistoryQuery query);

    Integer updateById(SmtTsTaskHistoryWithBLOBs record);

    Integer deleteById(@Param("id") Long id);

    List<SmtTsTaskHistoryWithBLOBs> pageBy(SmtTsTaskHistoryQuery query);
}