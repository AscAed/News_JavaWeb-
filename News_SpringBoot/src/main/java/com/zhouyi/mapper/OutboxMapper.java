package com.zhouyi.mapper;

import com.zhouyi.entity.OutboxMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OutboxMapper {
    int insert(OutboxMessage message);
    List<OutboxMessage> selectPending(@Param("category") String category, @Param("limit") int limit);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    int updateRetry(@Param("id") Long id, @Param("status") String status, @Param("retryCount") int retryCount);
    OutboxMessage selectById(@Param("id") Long id);
}
