package com.zhouyi.mapper;

import com.zhouyi.entity.RssSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RssSubscriptionMapper {

    RssSubscription findById(Long id);

    List<RssSubscription> findAll();

    void updateLastFetchedTime(@Param("id") Long id, @Param("time") LocalDateTime time);

    // 插入方法如果需要
}
