package com.shinemo.task.dal.mapper;

import com.shinemo.mybatis.common.interceptor.annotation.TableSplit;
import com.shinemo.mybatis.common.interceptor.enums.TypeEnum;
import com.shinemo.perform.common.mybatis.Page;
import com.shinemo.task.dal.model.SmtTsTaskRecord;
import com.shinemo.task.dal.model.SmtTsTaskRecordQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 指定 分表 key，目前的
 */
@TableSplit(splitType = TypeEnum.FIELD, tableName = "smt_ts_task_record", field = "splitKey")
public interface SmtTsTaskRecordMapper {

    int insertSelective(SmtTsTaskRecord record);

    SmtTsTaskRecord getBy(SmtTsTaskRecordQuery query);

    List<SmtTsTaskRecord> selectBy(SmtTsTaskRecordQuery query);

    Integer countBy(SmtTsTaskRecordQuery query);

    Integer updateById(SmtTsTaskRecord record);

    Integer deleteById(@Param("id") Long id, int splitKey);

    Page<SmtTsTaskRecord> pageBy(SmtTsTaskRecordQuery query);
}