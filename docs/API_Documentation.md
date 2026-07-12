# 「易闻趣事」毕业设计项目 API 接口文档与系统指北

## 1. 概述 (Overview)

本项目 **「易闻趣事」**（News_JavaWeb）是一个大学本科毕业设计级别的、高性能、多源融合的新闻聚合平台。
开发宗旨：**前端追求卓越的视觉呈现，后端贯彻严谨的行业工程架构准则。**
架构核心：结合结构化（MySQL，存储源节点、分类、作者）与非结构化数据（MongoDB，存储大规模新闻正文及 RSS 抓取文档）。

### 1.1 基础环境

- **Server Root**: `http://localhost:8080`
- **Core API Base Path**: `/api/v1` (用于核心实体资源)
- **Special Service Paths**: `/api/search`, `/api/sync` (用于搜索与同步服务)
- **Protocol**: HTTP/1.1
- **Content-Type**: `application/json` (除文件上传使用 `multipart/form-data` 外)

### 1.2 统一响应结构 (Standard Response)

所有 RESTful API 均遵循统一的 `Result<T>` 响应格式：

```json
{
  "code": 200,      // 业务状态码: 200=成功, 400=请求错误, 401=未认证, 403=无权限, 500=系统错误
  "message": "success", // 提示消息
  "data": {},       // 业务数据载荷
  "path": "/api/v1/resource" // 请求路径
}
```

---

## 2. 核心模块与前后端业务联动

> **RESTful 规范提示**：本项目 API 坚持使用名词复数映射资源（如 `/users`），通过 HTTP 方法区分操作。

### 2.1 资讯流视图驱动接口 (新闻阅读业务交互)

本项目采用**左侧纵向导航（订阅源） + 顶部横向导航（带参选项卡） -> 核心内容区（聚合列表）**的类 Google News 布局。

#### 1) 左侧侧边栏：获取订阅源列表 (来源类型)

定义系统中都有哪些源可供选择，例如默认的“原创”(Media 发布)、各个外部抓取的“RSS 订阅”。

- **获取订阅源**: `GET /rss-subscriptions/list` (公开) - 拉取包含第三方和系统的订阅源清单。

#### 2) 顶部横向导航：获取源内的详细带参分类选项

当左侧点击某个“源”（如原创，或某个特定 RSS 站点）时，顶部根据这个源生成对应的分类。

- **获取所有新闻分类（选项卡参数）**: `GET /categories` (公开)

#### 3) 主内容区：获取并渲染列表

根据“左侧选择的源类型（sourceType）”和“顶部选择的参数类别（type/section）”调用本接口：

- **核心聚合查询**: `GET /headlines` (公开)
  - **Query 参数**: `sourceType` ("local" 指代“原创”, "rss" 指代外部), `typeId` (分类映射)。
  - **UI 返回要求**: 返回列表项需包含**标题、Tag 标签、日期、封面图缩略图**。

#### 4) 点击单条详情

- **获取新闻全文**: `GET /headlines/{id}` (公开)
  - 根据 ID 统一屏蔽底层异构差异（无论文字在 MySQL 还是 MongoDB 中存储，一并聚合并返回在 data 载荷内供视图渲染）。

---

### 2.2 发布中心与创作者 (Media)

除被动从外部拉取 RSS，系统支持站内自发创作者（媒体）。

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|POST|`/headlines`|**发表原创文章** (新闻基础存 MySQL，正文入 MongoDB)|站内媒体/管理员|
|PUT|`/headlines/{id}`|更新新闻细节|作者本人/管理员|
|DELETE|`/headlines/{id}`|软删除新闻|作者本人/管理员|

---

### 2.3 认证与安全 (Auth & Security)

管理用户、创作者和管理员的身份进出。**最新集成**：滑块验证码与邮箱验证注册机制。

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|GET|`/auth/captcha/generate`|生成滑块验证码数据|公开|
|POST|`/auth/captcha/verify`|校验滑块位置并获取凭证|公开|
|POST|`/auth/send-code`|发送注册验证码邮件 (需校验验证码)|公开|
|POST|`/auth/register`|用户注册 (需校验邮件验证码)|公开|
|POST|`/auth/login`|用户登录 (支持手机号+密码)|公开|
|POST|`/auth/validate`|验证 Access Token 有效性|公开|
|POST|`/auth/refresh`|刷新令牌 (支持 Refresh Token)|需持有 RT|
|POST|`/auth/refresh/legacy`|刷新令牌 (向后兼容接口)|需登录|
|POST|`/auth/logout`|用户登出|需登录|
|GET|`/auth/profile`|获取当前在线用户信息|需登录|
|GET|`/auth/me`|获取当前用户信息 (向后兼容接口)|需登录|

> [!IMPORTANT]
> **注册安全流程 (2025 升级版)**
>
> 1. **滑块校验**: 调用 `GET /auth/captcha/generate` 获取背景图及拼图块。校验成功后 `POST /auth/captcha/verify` 返回一次性 `captchaToken`。
> 2. **发送邮件**: 调用 `POST /auth/send-code` 时必须携带 `captchaToken`。
> 3. **落地注册**: 调用 `POST /auth/register` 时需提交 `email`、`code`（邮件验证码）及其他基础信息。

---

### 2.4 用户管理与属性更新 (Users)

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|GET|`/users/profile`|获取当前登录用户的详细资料 (含角色信息)|需登录|
|PUT|`/users/{id}/profile`|更新个人资料 (用户名、头像)|本人|
|PUT|`/users/password`|修改登录密码|需登录|
|POST|`/users/{id}/email/request`|请求更改绑定邮箱 (发送原验证码)|本人|
|POST|`/users/{id}/email/verify`|校验并完成新邮箱绑定|本人|
|POST|`/users/{id}/phone/request`|请求更改手机号 (发送原验证码)|本人|
|POST|`/users/{id}/phone/verify`|校验并完成新手机号绑定|本人|
|PUT|`/users/{id}/admin`|**管理员专用**: 修改用户状态或角色|管理员|
|GET|`/users`|**管理员专用**: 分页查询用户列表|管理员|

---

### 2.5 搜索与同步拓展 (Search & Sync)

系统接入 **Elasticsearch** 引擎以支撑大规模文本检索与高性能同步。

#### 1) 搜索引擎交互 (Search)

- **全局全文检索**: `GET /api/search/news`
  - **Query**: `keyword` (关键词), `type` (可选分类ID), `page`, `pageSize`
  - **特性**: 支持标题及内容的高亮回显。

#### 2) 数据同步控制 (Sync)

- **ES 索引全量重建**: `POST /api/sync/es` (管理员)
  - **逻辑**: 清空 ES 索引并重新从 MySQL 和 MongoDB 中拉取全量存量数据进行灌库。

---

### 2.6 统计分析中心 (Statistics - 管理员专用)

提供全方位的系统运行状态与业务数据分布指标。

|路径|描述|
|:---|:---|
|`GET /v1/statistics/overview`|核心指标概览 (总数、增长率等)|
|`GET /v1/statistics/news`|新闻发布动态与分类分布统计|
|`GET /v1/statistics/users`|用户活跃度与角色占比统计|
|`GET /v1/statistics/comments`|评论情感倾向或互动热度统计 (待细化)|
|`GET /v1/statistics/system`|系统运行环境监控 (CPU/Memory/JVM)|

---

### 2.7 聚合后台 RSS 源管理与 MongoDB 拓展 (Admin)

后端提供专门针对于 MongoDB 的数据查询与管理 API 集合，用于支持海量及外部 RSS 流入。

#### 抓取系统控制

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|GET|`/rss-subscriptions/list`|获取启用的订阅源列表 (侧边栏使用)|公开|
|POST|`/rss-subscriptions`|添加 RSS 订阅源|**管理员**|
|POST|`/rss-subscriptions/{id}/fetch`|核心：触发单源拉取任务|**管理员**|

#### MongoDB 拓展查询

本部分在 `/api/v1/mongo` 空间下或独立存在，为平台扩展内容处理能力：

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|POST|`/mongo/rss-subscriptions/{id}/fetch`|触发 Mongo 侧执行数据处理同步任务|**管理员**|
|GET|`/mongo/categories/hot`|获取热门文章计算排行|公开|
|GET|`/mongo/articles/search`|使用 Mongo 提供的内容全文搜索|公开|

#### 数据迁移 (Migration)

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|POST|`/migration/rss-to-mongo`|历史数据从关系型主动导入到文档树中的任务|**管理员**|

---

### 2.8 社交互动与文件支撑 (Social & Common)

|方法|路径|描述|权限|
|:---|:---|:---|:---|
|GET|`/headlines/{id}/comments`|获取指定新闻的评论列表 (分页)|公开|
|GET|`/comments/{id}`|获取评论详情|公开|
|POST|`/comments`|提交新评论|需登录|
|PUT|`/comments/{id}`|更新评论内容|本人|
|DELETE|`/comments/{id}`|删除评论|本人/管理|
|PATCH|`/comments/{id}/status`|更新评论状态 (如审核)|管理员|
|POST|`/comments/{id}/like`|点赞/取消点赞该评论|需登录|
|GET|`/users/{user_id}/comments`|获取特定用户的评论列表|本人/管理|
|GET|`/favorites`|获取当前用户的收藏夹 (支持分类筛选)|需登录|
|GET|`/favorites/{id}`|获取单笔收藏详情|本人|
|POST|`/favorites`|添加收藏|需登录|
|DELETE|`/favorites/{id}`|取消收藏 (依据 ID)|本人|
|PATCH|`/favorites/{id}/note`|更新收藏备注|本人|
|GET|`/headlines/{id}/favorites/count`|获取指定文章的被收藏总数|公开|
|GET|`/headlines/{id}/favorites/status`|检查当前用户对该文章的收藏状态|需登录|
|POST|`/favorites/batch`|**批量收藏/取消收藏**|需登录|
|POST|`/common/upload`|多媒体文件上传接口 (返回 URL/PATH)|需登录|

---

## 3. 错误码规范参考

|状态码|HTTP 状态|描述|
|:---|:---|:---|
|200|200|请求执行完毕并成功。|
|400|400|前置条件及传参校验未通过，如缺失字段、类型错误。|
|401|401|未携带有效的 Token 令牌，凭证丢失或过期。|
|403|403|用户越权，尝试请求权限等级之上的资源。|
|404|404|指示的映射文件或路由在程序中不存在。|
|500|500|代码抛出且未捕获的后端服务崩溃，数据库断联等。|

> **提示**：前端拦截器通过判定状态码为 200 直接解包取得 `data` 体。如非 200，它会直接捕获外层 `message` 并弹窗呈现。因此后端应该合理使用 `message` 提供有意义的报错中文解释。

---

## 4. 性能优化注解 (Performance / Caching)

为了系统吞吐能力，以下高频读取的公开 API 已接入 Redis 热数据缓冲：

- `GET /categories`: `@Cacheable("categories")`，所有新增/更新将自动清除此命名空间。
- `GET /rss-subscriptions`: `@Cacheable("rssSubscriptions")`。
- `GET /headlines`: 针对首屏默认第一页的热点数据采用了代码级 5 分钟短效期 Redis 缓存拦截。
- `GET /headlines/{id}`: `@Cacheable(value="articleDetail", sync=true)` 具备防止缓存雪崩/击穿机制。

请注意数据变更可能存在最高 10 分钟或 5 分钟的不一致窗口期。
