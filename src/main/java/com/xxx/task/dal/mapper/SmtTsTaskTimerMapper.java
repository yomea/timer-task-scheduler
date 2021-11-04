package com.xxx.task.dal.mapper;

import com.xxx.perform.common.mybatis.Page;
import com.xxx.task.dal.model.SmtTsTaskTimer;
import com.xxx.task.dal.model.SmtTsTaskTimerQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtTsTaskTimerMapper {
    int insertSelective(SmtTsTaskTimer record);

    SmtTsTaskTimer getBy(SmtTsTaskTimerQuery query);

    List<SmtTsTaskTimer> selectBy(SmtTsTaskTimerQuery query);

    Integer countBy(SmtTsTaskTimerQuery query);

    Integer updateById(SmtTsTaskTimer record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsTaskTimer> pageBy(SmtTsTaskTimerQuery query);

    Integer batchSave(List<SmtTsTaskTimer> taskTimerList);

    Integer deleteByTaskId(Long taskId);

    Integer deleteByIdList(List<Long> delayTaskTimerIdList);

    Integer modifyStatusByIdList(@Param("status") Integer status, @Param("list") List<Long> delayTaskTimerIdList);
}