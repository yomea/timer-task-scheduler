package com.hangu.task.dal.mapper;

import com.github.pagehelper.Page;
import com.hangu.task.dal.model.SmtTsTaskTimer;
import com.hangu.task.dal.model.SmtTsTaskTimerQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

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