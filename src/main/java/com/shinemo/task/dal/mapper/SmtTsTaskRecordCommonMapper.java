package com.shinemo.task.dal.mapper;

import org.apache.ibatis.annotations.Param;

public interface SmtTsTaskRecordCommonMapper {

    void createMontTable(@Param("tableName") String newTableName);
    void createMontTableIndex(@Param("tableName") String newTableName);
    Integer checkTableExists(@Param("tableName") String newTableName);
}