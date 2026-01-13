# MongoDB RSS存储功能

## 概述

基于你的需求，我已经改进了RSS数据存储方式，将信息有序分类存放到MongoDB数据库中。新的存储方式包含了RSS中的标签信息，并提供了更丰富的数据结构和查询功能。

## 主要改进

### 1. 完整的RSS信息提取

- **标签/分类信息**: 从RSS的`<category>`标签中提取分类信息
- **频道信息**: 保存RSS源的完整元数据（标题、描述、语言等）
- **文章内容**: 支持HTML内容和纯文本提取
- **时间信息**: 准确的发布时间和采集时间

### 2. MongoDB数据模型

#### rss_subscriptions 集合

```json
{
  "_id": "string",
  "name": "联合早报",
  "url": "https://rsshub.isrss.com/zaobao/realtime/china",
  "channel_title": "《联合早报》-中港台-即时",
  "channel_description": "新加坡、中国、亚洲和国际的即时新闻...",
  "language": "zh",
  "last_fetched_at": "2026-01-15T14:20:14Z",
  "enabled": true,
  "fetch_status": "success"
}
```

#### rss_articles 集合

```json
{
  "_id": "string",
  "subscription_id": "1",
  "subscription_name": "联合早报",
  "title": "王毅：中国愿同加拿大加强沟通 排除干扰",
  "link": "https://www.zaobao.com/realtime/china/story20260115-8104899",
  "description": "<div><p>在加拿大总理卡尼访华之际...</p></div>",
  "content_text": "在加拿大总理卡尼访华之际...",
  "categories": ["王毅", "中加关系"],
  "pub_date": "2026-01-15T05:39:25Z",
  "author": "string",
  "word_count": 1250,
  "summary": "在加拿大总理卡尼访华之际，中国外长王毅说...",
  "importance_score": 2.5,
  "language": "zh",
  "created_at": "2026-01-15T14:20:13Z"
}
```

#### rss_categories 集合

```json
{
  "_id": "string",
  "name": "中加关系",
  "article_count": 15,
  "last_used_at": "2026-01-15T14:20:13Z",
  "enabled": true,
  "color": "#FF5722",
  "icon": "tag"
}
```

## API接口

### 1. 数据迁移

```bash
# 迁移MySQL数据到MongoDB
POST /api/v1/migration/rss-to-mongo
```

### 2. RSS采集

```bash
# 手动触发RSS采集到MongoDB
POST /api/v1/mongo/rss-subscriptions/{id}/fetch
```

### 3. 查询接口

```bash
# 获取所有订阅源
GET /api/v1/mongo/rss-subscriptions

# 根据订阅源获取文章
GET /api/v1/mongo/rss-subscriptions/{subscriptionId}/articles?page=0&size=20

# 根据分类获取文章
GET /api/v1/mongo/categories/{category}/articles?page=0&size=20

# 搜索文章
GET /api/v1/mongo/articles/search?keyword=王毅&page=0&size=20

# 获取热门分类
GET /api/v1/mongo/categories/hot?limit=10

# 获取统计信息
GET /api/v1/mongo/statistics
```

## 配置说明

### application.yml

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: News_MongoDB
      enabled: true
```

## 使用步骤

### 1. 启动MongoDB

确保MongoDB服务正在运行：

```bash
mongod --dbpath /path/to/your/db
```

### 2. 启动应用

```bash
mvn spring-boot:run
```

### 3. 迁移现有数据

```bash
curl -X POST "http://localhost:8080/api/v1/migration/rss-to-mongo" \
  -H "Content-Type: application/json"
```

### 4. 测试RSS采集

```bash
curl -X POST "http://localhost:8080/api/v1/mongo/rss-subscriptions/1/fetch" \
  -H "Content-Type: application/json"
```

## 功能特性

### 1. 智能内容处理

- **HTML标签清理**: 自动提取纯文本内容
- **摘要生成**: 基于内容自动生成摘要
- **字数统计**: 统计文章字数
- **重要度评分**: 基于多个因素计算文章重要度

### 2. 分类管理

- **自动分类**: 从RSS中提取标签信息
- **分类统计**: 统计每个分类的文章数量
- **热门分类**: 基于文章数量排序的热门分类

### 3. 搜索功能

- **全文搜索**: 支持标题、描述、内容的全文搜索
- **分类筛选**: 根据分类筛选文章
- **时间范围**: 支持按发布时间范围查询

### 4. 数据管理

- **重复检测**: 基于链接和GUID的重复检测
- **数据清理**: 支持清理过期文章
- **统计信息**: 提供详细的数据统计

## 性能优化

### 1. 索引建议

```javascript
// rss_articles集合索引
db.rss_articles.createIndex({"subscription_id": 1})
db.rss_articles.createIndex({"categories": 1})
db.rss_articles.createIndex({"pub_date": -1})
db.rss_articles.createIndex({"title": "text", "content_text": "text"})
db.rss_articles.createIndex({"link": 1}, {unique: true})
db.rss_articles.createIndex({"guid": 1}, {unique: true})

// rss_categories集合索引
db.rss_categories.createIndex({"name": 1}, {unique: true})
db.rss_categories.createIndex({"article_count": -1})
```

### 2. 查询优化

- 使用分页查询避免大量数据传输
- 利用MongoDB的聚合管道进行复杂统计
- 合理使用索引提高查询性能

## 测试功能

启用测试模式：

```bash
mvn spring-boot:run -Dapp.test.mongo=true
```

测试将自动执行：

1. 数据迁移测试
2. RSS采集测试
3. 查询功能测试

## 注意事项

1. **MongoDB连接**: 确保MongoDB服务正常运行
2. **数据迁移**: 迁移前建议备份现有数据
3. **内存使用**: 大量文章采集时注意内存使用
4. **网络超时**: RSS采集设置了合理的超时时间
5. **重复处理**: 系统会自动检测和处理重复文章

## 扩展功能

未来可以考虑添加：

1. **内容分析**: 集成NLP进行情感分析
2. **推荐系统**: 基于用户行为的文章推荐
3. **实时推送**: WebSocket实时推送新文章
4. **图片处理**: 提取和处理文章中的图片
5. **多语言支持**: 支持多语言RSS源

这个新的MongoDB存储方案完全解决了你提到的标签信息遗漏问题，并提供了更强大和灵活的数据管理功能。
