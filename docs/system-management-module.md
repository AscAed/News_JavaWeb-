# 系统管理模块

## 概述

系统管理模块为新闻头条管理系统提供了完整的系统配置管理、操作日志记录和系统健康检查功能。该模块采用分层架构设计，包含实体、映射器、服务和控制器层。

## 功能特性

### 1. 系统配置管理

- **配置分类**: 支持system、upload、security三种配置类型
- **CRUD操作**: 完整的配置增删改查功能
- **类型支持**: 支持STRING、NUMBER、BOOLEAN、JSON四种配置类型
- **批量更新**: 支持批量更新配置项
- **默认配置**: 自动初始化系统默认配置

### 2. 操作日志管理

- **完整记录**: 记录用户的所有操作行为
- **分页查询**: 支持分页查询操作日志
- **多维筛选**: 支持按用户、操作类型、日期范围筛选
- **自动清理**: 支持自动清理过期日志
- **详细信息**: 包含IP地址、用户代理等详细信息

### 3. 系统健康检查

- **多组件检查**: 检查数据库、存储、缓存等组件状态
- **响应时间**: 记录各组件的响应时间
- **状态分级**: 支持healthy、warning、unhealthy三种状态
- **详细信息**: 提供各组件的详细健康信息

## API接口

### 系统配置接口

#### 获取系统配置

```http
GET /api/v1/admin/config?category={category}
Authorization: Bearer {token}
```

**参数说明**:

- `category`: 配置分类（可选），支持：system、upload、security

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "system": {
      "site_name": "新闻头条系统",
      "version": "4.4.0",
      "maintenance_mode": false
    },
    "upload": {
      "max_file_size": 5242880,
      "allowed_types": ["jpg", "jpeg", "png", "gif"]
    },
    "security": {
      "jwt_expiration": 86400,
      "password_min_length": 6
    }
  }
}
```

#### 更新系统配置

```http
PUT /api/v1/admin/config
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:

```json
{
  "site_name": "新闻管理系统",
  "max_file_size": "10485760",
  "jwt_expiration": "172800"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "配置更新成功",
  "data": {
    "updated_keys": ["site_name", "max_file_size", "jwt_expiration"],
    "updated_time": "2025-11-25T12:00:00"
  }
}
```

### 操作日志接口

#### 获取操作日志

```http
GET /api/v1/admin/logs?page=1&pageSize=20&userId=1&action=CREATE&dateFrom=2025-11-01&dateTo=2025-11-25
Authorization: Bearer {token}
```

**参数说明**:

- `page`: 页码（从1开始）
- `pageSize`: 每页数量（最大100）
- `userId`: 用户ID筛选（可选）
- `action`: 操作类型筛选（可选）：CREATE、UPDATE、DELETE、READ
- `dateFrom`: 开始日期（可选）
- `dateTo`: 结束日期（可选）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 150,
    "page": 1,
    "pageSize": 20,
    "totalPages": 8,
    "items": [
      {
        "id": 1,
        "user": {
          "id": 1,
          "username": "admin"
        },
        "action": "UPDATE",
        "resource": "配置",
        "resourceId": "site_name",
        "description": "更新系统配置：site_name",
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0...",
        "createdTime": "2025-11-25T12:00:00"
      }
    ]
  }
}
```

### 健康检查接口

#### 系统健康检查

```http
GET /api/v1/health
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "healthy",
    "timestamp": "2025-11-25T12:00:00",
    "components": {
      "database": {
        "status": "healthy",
        "responseTime": 15,
        "details": {
          "mysql": "connected",
          "mongodb": "connected"
        }
      },
      "storage": {
        "status": "healthy",
        "responseTime": 25,
        "details": {
          "minio": "connected",
          "available_space": "500GB"
        }
      },
      "cache": {
        "status": "warning",
        "responseTime": 5,
        "details": {
          "redis": "not_configured"
        }
      }
    }
  }
}
```

## 数据库结构

### 系统配置表 (system_config)

| 字段名          | 类型           | 说明     | 约束                          |
|--------------|--------------|--------|-----------------------------|
| id           | INT          | 主键ID   | AUTO_INCREMENT              |
| config_key   | VARCHAR(100) | 配置键    | UNIQUE, NOT NULL            |
| config_value | TEXT         | 配置值    |                             |
| config_type  | VARCHAR(20)  | 配置类型   | STRING,NUMBER,BOOLEAN,JSON  |
| description  | VARCHAR(255) | 配置描述   |                             |
| is_system    | TINYINT      | 是否系统配置 | 0-否, 1-是                    |
| created_time | TIMESTAMP    | 创建时间   | DEFAULT CURRENT_TIMESTAMP   |
| updated_time | TIMESTAMP    | 更新时间   | ON UPDATE CURRENT_TIMESTAMP |

### 操作日志表 (operation_logs)

| 字段名            | 类型           | 说明   | 约束                        |
|----------------|--------------|------|---------------------------|
| id             | INT          | 主键ID | AUTO_INCREMENT            |
| user_id        | INT          | 用户ID | 外键                        |
| username       | VARCHAR(50)  | 用户名  |                           |
| operation_type | VARCHAR(20)  | 操作类型 | CREATE,UPDATE,DELETE,READ |
| resource_type  | VARCHAR(20)  | 资源类型 | NEWS,USER,FILE,CONFIG     |
| resource_id    | VARCHAR(100) | 资源ID |                           |
| description    | TEXT         | 操作描述 |                           |
| ip_address     | VARCHAR(45)  | IP地址 |                           |
| user_agent     | TEXT         | 用户代理 |                           |
| created_at     | TIMESTAMP    | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

## 配置说明

### 默认配置项

#### 系统配置

- `site_name`: 网站名称
- `site_description`: 网站描述
- `version`: 系统版本
- `maintenance_mode`: 维护模式

#### 上传配置

- `max_file_size`: 最大文件大小（字节）
- `allowed_types`: 允许的文件类型（JSON数组）
- `image_quality`: 图片质量

#### 安全配置

- `jwt_expiration`: JWT过期时间（秒）
- `password_min_length`: 密码最小长度
- `max_login_attempts`: 最大登录尝试次数

### 配置类型说明

- **STRING**: 字符串类型
- **NUMBER**: 数字类型（自动解析为Integer或Long）
- **BOOLEAN**: 布尔类型（自动解析为Boolean）
- **JSON**: JSON类型（自动解析为JsonNode）

## 权限控制

### 管理员权限

系统管理模块的所有接口都需要管理员权限：

```java
@PreAuthorize("hasRole('ADMIN')")
```

### 操作权限

- **系统配置**: 需要ADMIN角色
- **操作日志**: 需要ADMIN角色
- **健康检查**: 无需认证（公开接口）

## 性能优化

### 数据库索引

- 操作日志表已创建多个索引以优化查询性能
- 支持按用户、操作类型、日期等维度快速查询

### 缓存策略

- 系统配置建议使用Redis缓存
- 热点配置数据自动缓存，提高访问速度

### 日志清理

- 支持按时间自动清理过期日志
- 避免日志表数据量过大影响性能

## 安全考虑

### 输入验证

- 所有配置值都经过类型验证
- 防止SQL注入和XSS攻击

### 权限控制

- 严格的RBAC权限控制
- 管理员操作全程记录

### 数据保护

- 敏感配置信息加密存储
- 操作日志完整记录，便于审计

## 扩展功能

### 配置热更新

- 支持配置热更新，无需重启应用
- 配置变更实时生效

### 操作审计

- 完整的操作审计日志
- 支持操作回溯和责任追踪

### 监控告警

- 健康检查结果可集成监控系统
- 支持自定义告警规则

## 使用示例

### 初始化默认配置

```java
@Autowired
private SystemConfigService systemConfigService;

// 初始化默认配置
systemConfigService.initDefaultConfigs();
```

### 获取配置值

```java
// 获取单个配置值
String siteName = systemConfigService.getConfigValue("site_name");

// 获取分类配置
SystemConfigDTO configs = systemConfigService.getSystemConfig("system");
```

### 记录操作日志

```java
@Autowired
private OperationLogService operationLogService;

// 记录操作日志
operationLogService.logOperation(
    userId, 
    username, 
    "CREATE", 
    "NEWS", 
    "123", 
    "创建新闻：测试新闻",
    "192.168.1.100",
    "Mozilla/5.0..."
);
```

### 执行健康检查

```java
@Autowired
private HealthCheckService healthCheckService;

// 执行健康检查
HealthCheckDTO health = healthCheckService.performHealthCheck();
```

## 故障排除

### 常见问题

1. **配置更新失败**
    - 检查数据库连接
    - 确认配置键是否存在
    - 验证配置值格式

2. **日志查询慢**
    - 检查数据库索引
    - 考虑添加筛选条件
    - 定期清理过期日志

3. **健康检查异常**
    - 检查各组件连接状态
    - 验证配置信息
    - 查看详细错误信息

### 调试建议

1. 启用详细日志记录
2. 使用数据库监控工具
3. 定期检查系统状态
4. 建立监控告警机制

## 更新日志

### v1.0.0 (2025-11-25)

- 初始版本发布
- 实现系统配置管理
- 实现操作日志记录
- 实现系统健康检查
- 完善API文档和使用说明
