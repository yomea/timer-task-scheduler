package com.xxx.task.dal.mapper;

import com.github.pagehelper.Page;
import com.xxx.task.dal.model.SmtTsConsumeProgress;
import com.xxx.task.dal.model.SmtTsConsumeProgressQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmtTsConsumeProgressMapper {
    int insertSelective(SmtTsConsumeProgress record);

    SmtTsConsumeProgress getBy(SmtTsConsumeProgressQuery query);

    List<SmtTsConsumeProgress> selectBy(SmtTsConsumeProgressQuery query);

    Integer countBy(SmtTsConsumeProgressQuery query);

    Integer updateById(SmtTsConsumeProgress record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsConsumeProgress> pageBy(SmtTsConsumeProgressQuery query);
}