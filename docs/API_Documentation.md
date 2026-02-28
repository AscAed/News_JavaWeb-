# News JavaWeb API 接口文档

## 1. 概述 (Overview)

本项目 **News_JavaWeb** 致力于构建一个高性能、多源融合的新闻聚合平台。后端基于 **Spring Boot 4.x (Java 25)** 开发，前端使用
Vue 3。数据架构采用 MySQL（结构化数据）、MongoDB（非结构化内容）和 MinIO（二进制文件）的混合存储策略。

### 1.1 基础环境

- **Server Root**: `http://localhost:8080`
- **API Base Path**: `/api/v1`
- **Protocol**: HTTP/1.1 (生产环境建议 HTTPS)
- **Content-Type**: `application/json` (除文件上传外)

### 1.2 统一响应结构 (Standard Response)

所有 API 均遵循统一的 `Result<T>` 响应格式：

```json
{
  "code": 200,
  // 业务状态码: 200=成功, 500=错误, 401=未认证
  "message": "success",
  // 提示消息
  "data": {},
  // 业务数据载荷
  "timestamp": "2024-03-20T10:00:00",
  // 时间戳
  "path": "/api/v1/resource"
  // 请求路径
}
```

### 1.3 身份认证 (Authentication)

- **机制**: JWT (JSON Web Token)
- **Header**: `Authorization: Bearer <token>`
- **有效期**:
  - `access_token`: 短期有效 (如 1 小时)
  - `refresh_token`: 长期有效 (如 7 天)

---

## 2. 核心模块 API 详解

### 2.1 认证模块 (Auth)

负责用户的注册、登录及令牌管理。

| 方法   | 路径               | 描述              | 权限                   |
|:-----|:-----------------|:----------------|:---------------------|
| POST | `/auth/login`    | 用户登录，获取 Token   | 公开                   |
| POST | `/auth/register` | 用户注册            | 公开                   |
| POST | `/auth/refresh`  | 刷新 Access Token | 公开 (需 Refresh Token) |
| POST | `/auth/logout`   | 用户登出            | 需登录                  |

#### 接口示例: 用户登录 `/auth/login`

**Request Body**:
```json
{
  "phone": "13800138000",
  "password": "encrypted_password"
}
```

**Response Data**:
```json
{
  "token": "eyJhbGcV...",
  "refreshToken": "eyJhbGcC...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "NewsUser",
    "role_name": "普通用户"
  }
}
```

---

### 2.2 用户管理 (User)

管理用户个人信息、密码及管理员功能。

| 方法     | 路径                    | 描述               | 权限      |
|:-------|:----------------------|:-----------------|:--------|
| GET    | `/users/profile`      | 获取当前登录用户信息       | 需登录     |
| PUT    | `/users/{id}/profile` | 更新个人资料 (头像/昵称)   | 本人      |
| PUT    | `/users/password`     | 修改密码             | 需登录     |
| GET    | `/users`              | 获取用户列表 (支持分页/搜索) | **管理员** |
| PUT    | `/users/{id}/admin`   | 修改用户状态/角色        | **管理员** |
| DELETE | `/users/{id}`         | 删除用户             | **管理员** |

#### 接口示例: 获取用户列表 `/users`

**Query Parameters**:

- `page`: 页码 (默认 1)
- `pageSize`: 每页数量 (默认 10)
- `keywords`: 搜索用户名/手机号 (可选)

---

### 2.3 核心新闻管理 (Headlines)

处理内部发布的新闻内容的增删改查。

| 方法     | 路径                | 描述                 | 权限     |
|:-------|:------------------|:-------------------|:-------|
| GET    | `/headlines`      | 分页查询新闻列表 (支持多条件过滤) | 公开     |
| GET    | `/headlines/{id}` | 获取新闻详情 (含正文)       | 公开     |
| POST   | `/headlines`      | 发布新闻               | 需权限    |
| PUT    | `/headlines/{id}` | 更新新闻全量信息           | 作者/管理员 |
| DELETE | `/headlines/{id}` | 删除新闻 (软删除)         | 作者/管理员 |

#### 接口示例: 发布新闻 `/headlines`

**Request Body**:
```json
{
  "title": "Spring Boot 4.0 发布",
  "content": "<p>详细正文...</p>",
  // 存储于 MongoDB
  "type_id": 1,
  // 分类ID
  "status": 1
  // 1=发布, 0=草稿
}
```

---

### 2.4 统一聚合新闻 (Unified News)

**核心特色功能**: 统一查询入口，聚合本地新闻、RSS 订阅源及外部 API 抓取的内容。

| 方法   | 路径           | 描述                     | 权限      |
|:-----|:-------------|:-----------------------|:--------|
| POST | `/news/list` | 高级聚合查询 (Complex Query) | 公开      |
| GET  | `/news/{id}` | 获取统一新闻详情               | 公开      |
| POST | `/news/sync` | 手动触发 RSS 同步            | **管理员** |

#### 接口示例: 聚合查询 `/news/list`

使用 `POST` 以支持复杂的查询条件体。

**Request Body**:
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "keyWords": "AI Technology",
  "type": null,
  // 特定分类，null为全部
  "sourceType": "all"
  // "local" | "rss" | "external" | "all"
}
```

**Response Data**:
返回统一的数据结构 `UnifiedNewsDetailDTO` 列表，屏蔽底层存储差异。

---

### 2.5 混合 RSS 引擎 (Hybrid RSS)

管理 RSS 订阅源及爬取到的文章。

#### 订阅管理

| 方法     | 路径                              | 描述      | 权限      |
|:-------|:--------------------------------|:--------|:--------|
| GET    | `/rss/subscriptions`            | 获取所有订阅源 | 公开      |
| POST   | `/rss/subscriptions`            | 添加新订阅源  | **管理员** |
| POST   | `/rss/subscriptions/{id}/fetch` | 触发指定源抓取 | **管理员** |
| DELETE | `/rss/subscriptions/{id}`       | 删除订阅及文章 | **管理员** |

#### 文章与统计

| 方法  | 路径                              | 描述          | 权限 |
|:----|:--------------------------------|:------------|:---|
| GET | `/rss/articles`                 | 获取 RSS 文章列表 | 公开 |
| GET | `/rss/articles/search`          | 全文搜索 RSS 文章 | 公开 |
| GET | `/rss/stats/subscriptions/{id}` | 获取订阅源统计信息   | 公开 |

---

### 2.6 互动模块 (Comments & Favorites)

| 方法     | 路径                         | 描述        | 权限  |
|:-------|:---------------------------|:----------|:----|
| GET    | `/headlines/{id}/comments` | 获取新闻评论    | 公开  |
| POST   | `/comments`                | 发表评论      | 需登录 |
| POST   | `/comments/{id}/like`      | 点赞/取消点赞评论 | 需登录 |
| GET    | `/favorites`               | 获取我的收藏    | 需登录 |
| POST   | `/favorites`               | 添加收藏      | 需登录 |
| DELETE | `/favorites/{id}`          | 取消收藏      | 需登录 |

---

### 2.7 文件服务 (Files - MinIO)

| 方法     | 路径                    | 描述       | 权限  |
|:-------|:----------------------|:---------|:----|
| POST   | `/files/upload`       | 上传文件     | 需登录 |
| DELETE | `/files/{fileId}`     | 删除文件     | 需登录 |
| GET    | `/files/url/{fileId}` | 获取文件访问链接 | 公开  |

**Request (Upload)**: `multipart/form-data`, file字段为二进制流，category字段指定分类(image/video)。

---

### 2.8 系统管理 (System)

| 方法  | 路径         | 描述                        | 权限 |
|:----|:-----------|:--------------------------|:---|
| GET | `/health`  | 系统健康检查 (DB/Redis/MinIO状态) | 公开 |
| GET | `/version` | 获取后端版本信息                  | 公开 |

---

## 3. 错误码参照 (Error Codes)

| 代码  | 描述                  | 解决方案                        |
|:----|:--------------------|:----------------------------|
| 200 | Success             | 请求成功                        |
| 400 | Bad Request         | 检查请求参数格式                    |
| 401 | Unauthorized        | Token 过期或无效，请重新登录           |
| 403 | Forbidden           | 权限不足，请联系管理员                 |
| 404 | Not Found           | 资源不存在                       |
| 500 | Internal Error      | 服务器内部错误，请查看日志               |
| 503 | Service Unavailable | 依赖服务 (如 Database/MinIO) 不可用 |
