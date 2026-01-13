package com.zhouyi.service.impl;

import com.zhouyi.dto.HealthCheckDTO;
import com.zhouyi.service.HealthCheckService;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查服务实现
 */
@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MinioClient minioClient;

    @Override
    public HealthCheckDTO performHealthCheck() {
        Map<String, HealthCheckDTO.ComponentHealthDTO> components = new HashMap<>();
        
        // 检查数据库
        HealthCheckDTO.ComponentHealthDTO databaseHealth = checkDatabaseHealth();
        components.put("database", databaseHealth);
        
        // 检查存储
        HealthCheckDTO.ComponentHealthDTO storageHealth = checkStorageHealth();
        components.put("storage", storageHealth);
        
        // 检查缓存
        HealthCheckDTO.ComponentHealthDTO cacheHealth = checkCacheHealth();
        components.put("cache", cacheHealth);
        
        // 确定整体状态
        String overallStatus = determineOverallStatus(components);
        
        return new HealthCheckDTO(overallStatus, LocalDateTime.now(), components);
    }

    @Override
    public HealthCheckDTO.ComponentHealthDTO checkDatabaseHealth() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> details = new HashMap<>();
        String status = "healthy";
        
        try {
            // 检查MySQL连接
            try (Connection connection = dataSource.getConnection()) {
                boolean mysqlValid = connection.isValid(5);
                details.put("mysql", mysqlValid ? "connected" : "disconnected");
                if (!mysqlValid) {
                    status = "unhealthy";
                }
            }
            
            // 检查MongoDB连接
            try {
                mongoTemplate.getCollection("test").countDocuments();
                details.put("mongodb", "connected");
            } catch (Exception e) {
                details.put("mongodb", "disconnected: " + e.getMessage());
                status = "unhealthy";
            }
            
        } catch (Exception e) {
            logger.error("Database health check failed", e);
            status = "unhealthy";
            details.put("error", e.getMessage());
        }
        
        long responseTime = System.currentTimeMillis() - startTime;
        return new HealthCheckDTO.ComponentHealthDTO(status, responseTime, details);
    }

    @Override
    public HealthCheckDTO.ComponentHealthDTO checkStorageHealth() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> details = new HashMap<>();
        String status = "healthy";
        
        try {
            // 检查MinIO连接
            minioClient.listBuckets();
            details.put("minio", "connected");
            
            // 获取存储空间信息（简化版本）
            details.put("available_space", "500GB"); // 实际项目中应该获取真实空间
            
        } catch (Exception e) {
            logger.error("Storage health check failed", e);
            status = "unhealthy";
            details.put("minio", "disconnected: " + e.getMessage());
        }
        
        long responseTime = System.currentTimeMillis() - startTime;
        return new HealthCheckDTO.ComponentHealthDTO(status, responseTime, details);
    }

    @Override
    public HealthCheckDTO.ComponentHealthDTO checkCacheHealth() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> details = new HashMap<>();
        String status = "warning"; // 默认警告，因为项目中还没有集成Redis
        
        // 检查Redis连接（如果已配置）
        try {
            // 这里应该检查Redis连接
            // 由于项目还没有集成Redis，暂时返回warning状态
            details.put("redis", "not_configured");
            status = "warning";
        } catch (Exception e) {
            logger.error("Cache health check failed", e);
            status = "unhealthy";
            details.put("redis", "error: " + e.getMessage());
        }
        
        long responseTime = System.currentTimeMillis() - startTime;
        return new HealthCheckDTO.ComponentHealthDTO(status, responseTime, details);
    }

    /**
     * 确定整体健康状态
     */
    private String determineOverallStatus(Map<String, HealthCheckDTO.ComponentHealthDTO> components) {
        boolean hasWarning = false;
        
        for (HealthCheckDTO.ComponentHealthDTO component : components.values()) {
            if ("unhealthy".equals(component.getStatus())) {
                return "unhealthy";
            }
            if ("warning".equals(component.getStatus())) {
                hasWarning = true;
            }
        }
        
        return hasWarning ? "warning" : "healthy";
    }
}
