package com.hangu.task.dal.mapper;

import com.github.pagehelper.Page;
import com.hangu.task.dal.model.SmtTsTaskLock;
import com.hangu.task.dal.model.SmtTsTaskLockQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmtTsTaskLockMapper {
    int insertSelective(SmtTsTaskLock record);

    SmtTsTaskLock getBy(SmtTsTaskLockQuery query);

    List<SmtTsTaskLock> selectBy(SmtTsTaskLockQuery query);

    Integer countBy(SmtTsTaskLockQuery query);

    Integer updateById(SmtTsTaskLock record);

    Integer deleteById(@Param("id") Long id);

    Page<SmtTsTaskLock> pageBy(SmtTsTaskLockQuery query);

    Integer lock(SmtTsTaskLock lock);

    Integer updateStatusByOldStatus(@Param("startTime") Long startTime, @Param("id") Long id, @Param("ip") String ip, @Param("newStatus") Integer newStatus, @Param("oldStatus") Integer oldStatus);

    Integer updateStatusByIdList(@Param("idList") List<Long> idList, @Param("newStatus") Integer newStatus, @Param("oldStatus") Integer oldStatus);

    Page<SmtTsTaskLock> pageTimeoutBy(SmtTsTaskLockQuery query);

    Integer deleteByDefIdList(List<Long> defIdList);
}