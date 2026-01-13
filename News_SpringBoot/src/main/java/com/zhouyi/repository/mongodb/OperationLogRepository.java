package com.zhouyi.repository.mongodb;

import com.zhouyi.entity.mongodb.OperationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志MongoDB仓库
 */
@Repository
public interface OperationLogRepository extends MongoRepository<OperationLog, String> {
    
    /**
     * 根据用户ID查找日志
     */
    List<OperationLog> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * 根据操作类型查找日志
     */
    List<OperationLog> findByOperationTypeOrderByCreatedAtDesc(String operationType);
    
    /**
     * 根据模块查找日志
     */
    List<OperationLog> findByModuleOrderByCreatedAtDesc(String module);
    
    /**
     * 根据时间范围查找日志
     */
    List<OperationLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和时间范围查找日志
     */
    List<OperationLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Integer userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据IP地址查找日志
     */
    List<OperationLog> findByIpAddressOrderByCreatedAtDesc(String ipAddress);
    
    /**
     * 查找指定时间后的日志
     */
    List<OperationLog> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTime);
    
    /**
     * 统计用户操作次数
     */
    long countByUserIdAndOperationType(Integer userId, String operationType);
    
    /**
     * 统计模块操作次数
     */
    long countByModule(String module);
    
    /**
     * 查找执行时间超过阈值的操作
     */
    @Query("{'executionTime': {$gt: ?0}}")
    List<OperationLog> findByExecutionTimeGreaterThan(Long threshold);
    
    /**
     * 查找失败的操作（状态码非2xx）
     */
    @Query("{'responseStatus': {$gte: 400}}")
    List<OperationLog> findFailedOperations();
}
