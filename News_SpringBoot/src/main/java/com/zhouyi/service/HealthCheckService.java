package com.zhouyi.service;

import com.zhouyi.dto.HealthCheckDTO;

/**
 * 系统健康检查服务接口
 */
public interface HealthCheckService {

    /**
     * 执行系统健康检查
     * @return 健康检查结果
     */
    HealthCheckDTO performHealthCheck();

    /**
     * 检查数据库连接状态
     * @return 数据库健康状态
     */
    HealthCheckDTO.ComponentHealthDTO checkDatabaseHealth();

    /**
     * 检查存储系统状态
     * @return 存储健康状态
     */
    HealthCheckDTO.ComponentHealthDTO checkStorageHealth();

    /**
     * 检查缓存系统状态
     * @return 缓存健康状态
     */
    HealthCheckDTO.ComponentHealthDTO checkCacheHealth();
}
