package com.shinemo.task.dal.mapper;

import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.task.dal.model.SmtTsTaskRecord;
import com.shinemo.task.dal.model.SmtTsTaskRecordQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskRecordMapper {
    int insertSelective(SmtTsTaskRecord record);

    SmtTsTaskRecord getBy(SmtTsTaskRecordQuery query);

    List<SmtTsTaskRecord> selectBy(SmtTsTaskRecordQuery query);

    Integer countBy(SmtTsTaskRecordQuery query);

    Integer updateById(SmtTsTaskRecord record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsTaskRecord> pageBy(SmtTsTaskRecordQuery query);
}