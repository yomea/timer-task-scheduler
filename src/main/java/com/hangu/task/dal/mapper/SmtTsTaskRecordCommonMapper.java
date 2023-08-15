package com.hangu.task.dal.mapper;

import org.apache.ibatis.annotations.Param;

public interface SmtTsTaskRecordCommonMapper {

    void createMontTable(@Param("tableName") String newTableName);
}