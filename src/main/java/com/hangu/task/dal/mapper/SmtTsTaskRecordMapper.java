package com.hangu.task.dal.mapper;

import com.github.pagehelper.Page;
import com.hangu.task.dal.model.SmtTsTaskRecord;
import com.hangu.task.dal.model.SmtTsTaskRecordQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 指定 分表 key，目前的
 */
public interface SmtTsTaskRecordMapper {

    int insertSelective(SmtTsTaskRecord record);

    SmtTsTaskRecord getBy(SmtTsTaskRecordQuery query);

    List<SmtTsTaskRecord> selectBy(SmtTsTaskRecordQuery query);

    Integer countBy(SmtTsTaskRecordQuery query);

    Integer updateById(SmtTsTaskRecord record);

    Integer deleteById(@Param("id") Long id, @Param("splitKey") int splitKey);

    Page<SmtTsTaskRecord> pageBy(SmtTsTaskRecordQuery query);
}