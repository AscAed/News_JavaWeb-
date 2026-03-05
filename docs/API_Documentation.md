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
  "code": 200,      // 业务状态码: 200=成功, 400=请求错误, 401=未认证, 403=无权限, 500=系统错误
  "message": "success", // 提示消息
  "data": {},       // 业务数据载荷 (可以是对象、列表或简单数字/字符串)
  "path": "/api/v1/resource" // 请求路径
}
```

### 1.3 身份认证 (Authentication)

- **机制**: JWT (JSON Web Token)
- **Header**: `Authorization: Bearer <token>`
- **Token 刷新**:
    - 支持通过 Refresh Token 获取新 Access Token。
    - 前端应在 401 响应时尝试静默刷新。

---

## 2. 核心模块 API 详解

### 2.1 认证模块 (Auth)

负责用户的注册、登录及令牌管理。

| 方法   | 路径               | 描述              | 权限              |
|:-----|:-----------------|:----------------|:----------------|
| POST | `/auth/login`    | 用户登录            | 公开              |
| POST | `/auth/register` | 用户注册            | 公开              |
| POST | `/auth/refresh`  | 刷新 Access Token | 需 Refresh Token |
| GET  | `/auth/me`       | 获取当前登录用户信息      | 需登录             |
| GET  | `/auth/validate` | 验证 Token 有效性    | 公开              |
| POST | `/auth/logout`   | 用户登出            | 需登录             |

#### 接口示例: 用户登录 `/auth/login`

**Request Body**:

```json
{
  "phone": "13800138000",
  "password": "plain_text_password"
}
```

**Response Data**:

```json
{
  "token": "eyJhbGcV...",
  "refreshToken": "eyJhbGcC...",
  "user": {
    "id": 1,
    "username": "NewsUser",
    "phone": "13800138000"
  }
}
```

---

### 2.2 用户管理 (User)

管理用户个人信息、密码、邮箱/手机号更新及管理员功能。

| 方法     | 路径                          | 描述               | 权限      |
|:-------|:----------------------------|:-----------------|:--------|
| GET    | `/users/me`                 | 获取当前登录用户信息       | 需登录     |
| GET    | `/users/{id}`               | 根据 ID 查询用户信息     | 需登录     |
| GET    | `/users`                    | 获取用户列表 (支持分页/搜索) | **管理员** |
| POST   | `/users`                    | 创建新用户            | **管理员** |
| PUT    | `/users/{id}`               | 更新用户信息 (基本资料)    | 本人/管理员  |
| PUT    | `/users/password`           | 修改登录密码           | 需登录     |
| PUT    | `/users/{id}/profile`       | 更新个人资料 (头像/昵称)   | 本人      |
| PUT    | `/users/{id}/attributes`    | 更新用户角色/状态        | **管理员** |
| POST   | `/users/{id}/email/request` | 请求更改邮箱 (发送验证码)   | 本人      |
| POST   | `/users/{id}/email/verify`  | 验证并完成邮箱更改        | 本人      |
| POST   | `/users/{id}/phone/request` | 请求更改手机号 (发送验证码)  | 本人      |
| POST   | `/users/{id}/phone/verify`  | 验证并完成手机号更改       | 本人      |
| DELETE | `/users/{id}`               | 删除用户             | **管理员** |

#### 接口示例: 获取用户列表 `/users`

**Query Parameters**:

- `page`: 页码 (默认 1)
- `pageSize`: 每页数量 (默认 10)
- `keywords`: 搜索用户名/手机号 (可选)
- `status`: 用户状态 (可选, 1=启用, 0=禁用)

---

### 2.3 核心新闻管理 (Headlines)

处理内部发布的新闻内容的增删改查。

| 方法     | 路径                | 描述               | 权限                |
|:-------|:------------------|:-----------------|:------------------|
| GET    | `/headlines`      | 分页查询新闻列表 (多条件过滤) | 公开                |
| GET    | `/headlines/{id}` | 获取新闻详情 (含正文)     | 公开                |
| POST   | `/headlines`      | 发布新闻             | 需权限 (Media/Admin) |
| PUT    | `/headlines/{id}` | 更新新闻信息           | 作者/管理员            |
| DELETE | `/headlines/{id}` | 删除新闻 (软删除)       | 作者/管理员            |

#### 接口示例: 查询新闻列表 `/headlines` (GET)

**Query Parameters**:

- `keywords`: 搜索标题/关键字
- `typeId`: 分类 ID
- `status`: 状态 (1=发布, 0=草稿)
- `sortBy`: 排序字段 (默认 `published_time`)
- `sortOrder`: 排序方向 (`asc` / `desc`)
- `page`: 页码
- `pageSize`: 每页数量
- `sourceType`: 来源类型 (`api`, `rss`)
- `section`: 特定版块

#### 接口示例: 发布新闻 `/headlines` (POST)

**Request Body**:

```json
{
  "title": "Spring Boot 4.0 发布",
  "summary": "这是新闻摘要...",
  "content": "<p>详细正文...</p>", // 存储于 MongoDB
  "cover_image_url": "http://minio/news/1.jpg",
  "tags": "Java,Spring,Backend",
  "type": 1, // 分类ID
  "status": 1 // 1=发布, 0=草稿
}
```

---

### 2.4 统一聚合新闻 (Unified News)

**核心特色功能**: 统一查询入口，从多源 (Local, RSS, API) 提取数据并按统一格式返回。

| 方法   | 路径           | 描述                   | 权限      |
|:-----|:-------------|:---------------------|:--------|
| POST | `/news/list` | 统一聚合查询               | 公开      |
| GET  | `/news/{id}` | 获取统一新闻详情             | 公开      |
| POST | `/news/sync` | 同步 RSS 数据到 Headlines | **管理员** |

#### 接口示例: 聚合查询 `/news/list` (POST)

**Request Body**:

```json
{
  "pageNum": 1,
  "pageSize": 20,
  "keyWords": "Technology",
  "type": null, // 特定分类ID
  "sourceType": "all", // "local", "rss", "api", "all"
  "sortBy": "published_time",
  "sortOrder": "desc"
}
```

**Response Data**:

返回 `UnifiedNewsDetailDTO` 列表，包含统一的 `hid`, `title`, `source`, `publishedTime` 等核心字段。

---

### 2.5 混合 RSS 引擎 (Hybrid RSS)

管理 RSS 订阅源及混合存储 (SQL + MongoDB) 中的文章。

#### 订阅管理 (Subscriptions)

| 方法     | 路径                                       | 描述        | 权限      |
|:-------|:-----------------------------------------|:----------|:--------|
| GET    | `/rss/subscriptions`                     | 获取所有订阅源   | 公开      |
| GET    | `/rss/subscriptions/{id}`                | 获取指定订阅源详情 | 公开      |
| POST   | `/rss/subscriptions`                     | 添加新订阅源    | **管理员** |
| PUT    | `/rss/subscriptions/{id}`                | 更新订阅源信息   | **管理员** |
| DELETE | `/rss/subscriptions/{id}`                | 删除订阅及关联文章 | **管理员** |
| GET    | `/rss/subscriptions/category/{category}` | 按分类获取订阅源  | 公开      |
| POST   | `/rss/subscriptions/{id}/fetch`          | 触发指定源抓取同步 | **管理员** |
| POST   | `/rss/subscriptions/fetch/all`           | 触发全局同步    | **管理员** |

#### 文章与统计 (Articles & Stats)

| 方法   | 路径                                   | 描述                  | 权限      |
|:-----|:-------------------------------------|:--------------------|:--------|
| GET  | `/rss/articles`                      | 分页查询 RSS 文章         | 公开      |
| GET  | `/rss/articles/{id}`                 | 获取文章详情 (MongoDB ID) | 公开      |
| GET  | `/rss/articles/search`               | 全文搜索 RSS 文章         | 公开      |
| GET  | `/rss/articles/category/{category}`  | 按分类获取文章             | 公开      |
| GET  | `/rss/articles/important`            | 获取高权重文章             | 公开      |
| GET  | `/rss/stats/subscriptions/{id}`      | 获取订阅源统计信息           | **管理员** |
| POST | `/rss/stats/subscriptions/{id}/sync` | 强制同步统计数据            | **管理员** |
| POST | `/rss/sync/elasticsearch`            | 同步文章到 ES            | **管理员** |

---

### 2.6 互动模块 (Comments & Favorites)

#### 评论管理 (Comments)

| 方法     | 路径                         | 描述          | 权限      |
|:-------|:---------------------------|:------------|:--------|
| GET    | `/headlines/{id}/comments` | 获取新闻评论列表    | 公开      |
| GET    | `/comments/{id}`           | 获取单条评论详情    | 公开      |
| POST   | `/comments`                | 发表评论        | 需登录     |
| PUT    | `/comments/{id}`           | 更新评论内容      | 本人/管理员  |
| DELETE | `/comments/{id}`           | 删除评论        | 本人/管理员  |
| PATCH  | `/comments/{id}/status`    | 更新评论状态      | **管理员** |
| POST   | `/comments/{id}/like`      | 点赞/取消点赞     | 需登录     |
| GET    | `/users/{id}/comments`     | 获取指定用户的评论列表 | 本人/管理员  |

#### 收藏管理 (Favorites)

| 方法     | 路径                                 | 描述         | 权限       |
|:-------|:-----------------------------------|:-----------|:---------|
| GET    | `/favorites`                       | 获取当前用户收藏列表 | 需登录      |
| GET    | `/favorites/{id}`                  | 获取单条收藏详情   | 需登录 (本人) |
| POST   | `/favorites`                       | 添加收藏       | 需登录      |
| DELETE | `/favorites/{id}`                  | 取消收藏       | 需登录 (本人) |
| PATCH  | `/favorites/{id}/note`             | 更新收藏备注     | 需登录 (本人) |
| GET    | `/headlines/{id}/favorites/count`  | 获取新闻收藏总数   | 公开       |
| GET    | `/headlines/{id}/favorites/status` | 检查当前用户收藏状态 | 需登录      |
| POST   | `/favorites/batch`                 | 批量添加/取消收藏  | 需登录      |

---

### 2.7 新闻分类 (News Categories)

| 方法     | 路径                            | 描述          | 权限      |
|:-------|:------------------------------|:------------|:--------|
| GET    | `/categories`                 | 获取分类列表      | 公开      |
| GET    | `/categories/{id}`            | 获取分类详情      | 公开      |
| POST   | `/categories`                 | 创建新分类       | **管理员** |
| PUT    | `/categories/{id}`            | 更新分类信息      | **管理员** |
| PATCH  | `/categories/{id}/status`     | 更新分类状态      | **管理员** |
| DELETE | `/categories/{id}`            | 删除分类        | **管理员** |
| GET    | `/categories/{id}/statistics` | 获取该分类下的新闻统计 | **管理员** |

---

### 2.8 文件服务 (Files - MinIO)

| 方法     | 路径                         | 描述               | 权限  |
|:-------|:---------------------------|:-----------------|:----|
| POST   | `/files/upload`            | 上传文件             | 需登录 |
| GET    | `/files/{fileId}`          | 获取文件详情           | 需登录 |
| GET    | `/files/url/{fileId}`      | 获取文件访问链接 (永久/临时) | 公开  |
| GET    | `/files/download/{fileId}` | 下载文件 (附件方式)      | 公开  |
| GET    | `/files/exists/{fileId}`   | 检查文件是否存在         | 公开  |
| DELETE | `/files/{fileId}`          | 删除文件             | 需登录 |

**Upload Parameters (multipart/form-data)**:

- `file`: 二进制文件流
- `category`: 分类 (`image`, `video`, `document`, 默认 `default`)
- `description`: 文件描述 (可选)

---

### 2.9 系统管理与统计 (Admin & Statistics)

#### 管理操作 (Admin Operations)

| 方法  | 路径              | 描述                     | 权限      |
|:----|:----------------|:-----------------------|:--------|
| GET | `/admin/config` | 获取系统配置信息               | **管理员** |
| PUT | `/admin/config` | 批量更新系统配置               | **管理员** |
| GET | `/admin/logs`   | 分页查询系统操作日志             | **管理员** |
| GET | `/admin/health` | 系统组件健康检查 (DB/ES/MinIO) | **管理员** |

#### 统计分析 (Statistics)

| 方法  | 路径                      | 描述          | 权限      |
|:----|:------------------------|:------------|:--------|
| GET | `/statistics/overview`  | 获取系统运营概览    | **管理员** |
| GET | `/statistics/news`      | 新闻发布趋势及阅读统计 | **管理员** |
| GET | `/statistics/users`     | 用户增长及活跃统计   | **管理员** |
| GET | `/statistics/comments`  | 评论互动趋势统计    | **管理员** |
| GET | `/statistics/favorites` | 收藏偏好统计      | **管理员** |
| GET | `/statistics/files`     | 存储资源占用统计    | **管理员** |

---

### 2.10 系统服务 (System)

| 方法  | 路径         | 描述        | 权限 |
|:----|:-----------|:----------|:---|
| GET | `/health`  | 基础健康检查    | 公开 |
| GET | `/version` | 后端版本及环境信息 | 公开 |

---

## 3. 错误码规范 (Standard Error Codes)

系统采用 HTTP 状态码与业务状态码相结合的模式。

| 业务码 | HTTP 状态 | 描述                          |
|:----|:--------|:----------------------------|
| 200 | 200     | 请求成功                        |
| 400 | 400     | 参数验证失败 (Invalid Parameters) |
| 401 | 401     | 未认证或 Token 已过期              |
| 403 | 403     | 权限不足 (Forbidden)            |
| 404 | 404     | 资源未找到 (Not Found)           |
| 409 | 409     | 业务冲突 (如用户名已存在)              |
| 500 | 500     | 系统内部错误                      |

---

## 4. 行业规范与最佳实践

1. **RESTful 命名**: 资源路径统一使用复数名词 (如 `/users`, `/categories`)，禁止使用动词 (如 `/getUser`)。
2. **安全性**:
    - 所有写操作 (POST/PUT/DELETE) 必须包含有效的 JWT 令牌。
    - 敏感数据 (如密码) 严禁在 GET 请求中返回。
3. **分页**: 统一使用 `page` (1-indexed) 和 `pageSize` 参数。
4. **软删除**: 新闻、评论等核心数据使用软删除机制。
