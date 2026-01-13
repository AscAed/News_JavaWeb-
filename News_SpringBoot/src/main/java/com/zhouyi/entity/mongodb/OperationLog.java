package com.zhouyi.entity.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

/**
 * 操作日志实体类（MongoDB存储）
 * 对应operation_logs集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "operation_logs")
public class OperationLog {
    
    @Id
    private String id;                    // MongoDB主键
    
    @Field("user_id")
    private Integer userId;               // 操作用户ID
    
    @Field("username")
    private String username;              // 用户名
    
    @Field("operation_type")
    private String operationType;         // 操作类型：LOGIN、LOGOUT、CREATE、UPDATE、DELETE
    
    @Field("module")
    private String module;                // 操作模块：USER、NEWS、COMMENT
    
    @Field("target_id")
    private String targetId;              // 操作目标ID
    
    @Field("description")
    private String description;           // 操作描述
    
    @Field("ip_address")
    private String ipAddress;             // IP地址
    
    @Field("user_agent")
    private String userAgent;             // 用户代理
    
    @Field("request_url")
    private String requestUrl;            // 请求URL
    
    @Field("request_method")
    private String requestMethod;         // 请求方法：GET、POST、PUT、DELETE
    
    @Field("response_status")
    private Integer responseStatus;       // 响应状态码
    
    @Field("execution_time")
    private Long executionTime;           // 执行时间（毫秒）
    
    @Field("created_at")
    private LocalDateTime createdAt;       // 创建时间
}
