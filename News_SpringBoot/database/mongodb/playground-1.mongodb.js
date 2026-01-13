/* global use, db */
// MongoDB 完整初始化脚本
// 参考MongoDB Playground模板格式
// To disable this template go to Settings | MongoDB | Use Default Template For Playground.
// Make sure you are connected to enable completions and to be able to run a playground.
// Use Ctrl+Space inside a snippet or a string literal to trigger completions.
// The result of the last command run in a playground is shown on the results panel.
// By default the first 20 documents will be returned with a cursor.
// Use 'console.log()' to print to the debug output.
// For more documentation on playgrounds please refer to
// https://www.mongodb.com/docs/mongodb-vscode/playgrounds/

// =====================================================
// 新闻头条项目 - MongoDB 完整初始化脚本
// 版本: v1.0
// 创建时间: 2025-11-24
// 说明: 集合结构定义 + 数据初始化 + 验证统计
// 特性: 完全幂等，可重复执行
// =====================================================

// Select the database to use.
use('News_MongoDB');

// =====================================================
// 1. 集合结构定义
// =====================================================

// Print a message to the output window.
console.log('开始创建MongoDB集合结构...');

// =====================================================
// 1.1 新闻内容集合 (news)
// =====================================================

// 创建新闻内容集合
db.getCollection('news').drop(); // 清理现有集合
db.createCollection('news');

// Print a message to the output window.
console.log('新闻内容集合创建完成');

// 集合结构定义阶段不插入示例数据，避免与数据初始化阶段冲突

// 创建索引 - 提升查询性能
console.log('开始创建新闻内容索引...');

db.getCollection('news').createIndex({ "news_id": 1 }, { unique: true, name: "idx_news_id" });
// 合并全文索引 - MongoDB每个集合只能有一个全文索引
db.getCollection('news').createIndex({ 
  "title": "text", 
  "content": "text", 
  "summary": "text", 
  "keywords": "text" 
}, { name: "idx_fulltext_search" });
db.getCollection('news').createIndex({ "tags": 1 }, { name: "idx_tags" });
db.getCollection('news').createIndex({ "status": 1 }, { name: "idx_status" });
db.getCollection('news').createIndex({ "created_at": -1 }, { name: "idx_created_at_desc" });
db.getCollection('news').createIndex({ "word_count": 1 }, { name: "idx_word_count" });
db.getCollection('news').createIndex({ "reading_time": 1 }, { name: "idx_reading_time" });

// Print a message to the output window.
console.log('新闻内容索引创建完成');

// =====================================================
// 1.2 评论集合 (comments)
// =====================================================

// 创建评论集合
db.getCollection('comments').drop(); // 清理现有集合
db.createCollection('comments');

// Print a message to the output window.
console.log('评论集合创建完成');

// 集合结构定义阶段不插入示例数据，避免与数据初始化阶段冲突

// 创建评论索引
console.log('开始创建评论索引...');

db.getCollection('comments').createIndex({ "news_id": 1, "created_at": -1 }, { name: "idx_news_time" });
db.getCollection('comments').createIndex({ "user_id": 1, "created_at": -1 }, { name: "idx_user_time" });
db.getCollection('comments').createIndex({ "parent_id": 1 }, { name: "idx_parent_id" });
db.getCollection('comments').createIndex({ "status": 1 }, { name: "idx_status" });
db.getCollection('comments').createIndex({ "created_at": -1 }, { name: "idx_created_at_desc" });

// Print a message to the output window.
console.log('评论索引创建完成');

// =====================================================
// 1.3 文件元数据集合 (file_metadata)
// =====================================================

// 创建文件元数据集合
db.getCollection('file_metadata').drop(); // 清理现有集合
db.createCollection('file_metadata');

// Print a message to the output window.
console.log('文件元数据集合创建完成');

// 集合结构定义阶段不插入示例数据，避免与数据初始化阶段冲突

// 创建文件元数据索引
console.log('开始创建文件元数据索引...');

db.getCollection('file_metadata').createIndex({ "file_id": 1 }, { unique: true, name: "idx_file_id" });
db.getCollection('file_metadata').createIndex({ "uploader_id": 1, "created_at": -1 }, { name: "idx_uploader_time" });
db.getCollection('file_metadata').createIndex({ "related_type": 1, "related_id": 1 }, { name: "idx_related" });
db.getCollection('file_metadata').createIndex({ "file_type": 1 }, { name: "idx_file_type" });
db.getCollection('file_metadata').createIndex({ "mime_type": 1 }, { name: "idx_mime_type" });
db.getCollection('file_metadata').createIndex({ "status": 1 }, { name: "idx_status" });
db.getCollection('file_metadata').createIndex({ "created_at": -1 }, { name: "idx_created_at_desc" });

// Print a message to the output window.
console.log('文件元数据索引创建完成');

// =====================================================
// 1.4 用户行为集合 (user_behavior)
// =====================================================

// 创建用户行为集合
db.getCollection('user_behavior').drop(); // 清理现有集合
db.createCollection('user_behavior');

// Print a message to the output window.
console.log('用户行为集合创建完成');

// 集合结构定义阶段不插入示例数据，避免与数据初始化阶段冲突

// 创建用户行为索引
console.log('开始创建用户行为索引...');

db.getCollection('user_behavior').createIndex({ "user_id": 1, "created_at": -1 }, { name: "idx_user_time" });
db.getCollection('user_behavior').createIndex({ "behavior_type": 1 }, { name: "idx_behavior_type" });
db.getCollection('user_behavior').createIndex({ "target_type": 1, "target_id": 1 }, { name: "idx_target" });
db.getCollection('user_behavior').createIndex({ "session_id": 1 }, { name: "idx_session_id" });
db.getCollection('user_behavior').createIndex({ "created_at": -1 }, { name: "idx_created_at_desc" });

// TTL索引：行为数据保留90天
db.getCollection('user_behavior').createIndex({ "created_at": 1 }, { expireAfterSeconds: 7776000, name: "idx_ttl_behavior" });

// Print a message to the output window.
console.log('用户行为索引创建完成');

// =====================================================
// 1.5 系统缓存集合 (system_cache) - 可选
// =====================================================

// 创建系统缓存集合
db.getCollection('system_cache').drop(); // 清理现有集合
db.createCollection('system_cache');

// Print a message to the output window.
console.log('系统缓存集合创建完成');

// 集合结构定义阶段不插入示例数据，避免与数据初始化阶段冲突

// 创建系统缓存索引
console.log('开始创建系统缓存索引...');

db.getCollection('system_cache').createIndex({ "cache_key": 1 }, { unique: true, name: "idx_cache_key" });
db.getCollection('system_cache').createIndex({ "cache_type": 1 }, { name: "idx_cache_type" });

// TTL索引：自动清理过期缓存（合并了普通索引和TTL功能）
db.getCollection('system_cache').createIndex({ "expire_time": 1 }, { expireAfterSeconds: 0, name: "idx_expire_time" });

// Print a message to the output window.
console.log('系统缓存索引创建完成');

// =====================================================
// 2. 数据初始化
// =====================================================

// Print a message to the output window.
console.log('开始初始化MongoDB数据...');

// =====================================================
// 2.1 新闻内容数据初始化
// =====================================================

// Print a message to the output window.
console.log('开始初始化新闻内容数据...');

db.getCollection('news').insertMany([
  {
    "news_id": 1001,
    "title": "Spring Boot 3.0 正式发布，带来革命性改进",
    "content": `
      <h2>Spring Boot 3.0 重大更新</h2>
      <p>经过长时间的期待，Spring Boot 3.0 终于正式发布了。这个版本带来了许多令人兴奋的新特性和改进。</p>
      
      <h3>主要特性</h3>
      <ul>
        <li><strong>原生支持 Java 17</strong>：充分利用 Java 17 的新特性，提升性能</li>
        <li><strong>改进的可观测性</strong>：内置 Micrometer 和 OpenTelemetry 支持</li>
        <li><strong>增强的 GraalVM 支持</strong>：更好的原生镜像编译支持</li>
        <li><strong>模块化的依赖管理</strong>：更清晰的依赖关系</li>
      </ul>
      
      <h3>性能提升</h3>
      <p>根据官方测试，Spring Boot 3.0 在启动时间、内存占用等方面都有显著提升：</p>
      <ul>
        <li>启动时间减少 20-30%</li>
        <li>内存占用降低 15-25%</li>
        <li>运行时性能提升 10-20%</li>
      </ul>
      
      <h3>迁移指南</h3>
      <p>对于现有的 Spring Boot 2.x 项目，官方提供了详细的迁移指南。主要的迁移步骤包括：</p>
      <ol>
        <li>升级 Java 版本到 17 或更高</li>
        <li>更新依赖版本</li>
        <li>调整配置文件</li>
        <li>测试兼容性</li>
      </ol>
      
      <p>总的来说，Spring Boot 3.0 是一个值得升级的重要版本，为开发者带来了更好的开发体验和应用性能。</p>
    `,
    "summary": "Spring Boot 3.0 正式发布，带来 Java 17 原生支持、改进的可观测性、增强的 GraalVM 支持和模块化依赖管理等重大更新，性能显著提升。",
    "keywords": "Spring Boot, Java 17, 微服务, 新特性, 性能提升, GraalVM",
    "content_type": "html",
    "word_count": 1250,
    "reading_time": 5,
    "tags": ["技术", "Java", "Spring Boot", "微服务", "新版本"],
    "author_info": {
      "bio": "资深Java开发工程师，专注于微服务架构和云原生技术",
      "social_links": {
        "github": "https://github.com/tech-expert",
        "twitter": "@tech_expert",
        "linkedin": "https://linkedin.com/in/tech-expert"
      }
    },
    "seo_info": {
      "meta_description": "Spring Boot 3.0 正式发布，带来革命性改进和性能提升",
      "meta_keywords": "Spring Boot,Java 17,微服务,性能提升",
      "og_image": "https://example.com/images/spring-boot-3-og.jpg",
      "canonical_url": "https://news.example.com/spring-boot-3-release"
    },
    "status": 1,
    "created_at": new Date("2025-11-20T10:00:00Z"),
    "updated_at": new Date("2025-11-20T10:00:00Z")
  },
  {
    "news_id": 1002,
    "title": "Vue 3.5 新特性详解：性能提升与开发体验优化",
    "content": `
      <h2>Vue 3.5 的重要更新</h2>
      <p>Vue.js 团队发布了 3.5 版本，带来了许多令人兴奋的新特性和性能优化。</p>
      
      <h3>核心改进</h3>
      <ul>
        <li><strong>更快的响应式系统</strong>：Proxy-based 响应式系统性能提升 40%</li>
        <li><strong>优化的编译器</strong>：模板编译速度提升 25%</li>
        <li><strong>增强的 TypeScript 支持</strong>：更好的类型推断和智能提示</li>
        <li><strong>新的组合式 API</strong>：更灵活的组件逻辑组织</li>
      </ul>
      
      <h3>开发体验提升</h3>
      <p>Vue 3.5 专注于改善开发者的日常工作流程：</p>
      <ul>
        <li>更快的开发服务器启动</li>
        <li>更精确的错误提示</li>
        <li>更好的调试工具集成</li>
        <li>改进的热重载性能</li>
      </ul>
    `,
    "summary": "Vue 3.5 版本发布，带来显著的性能提升和开发体验优化，包括更快的响应式系统、优化的编译器和增强的 TypeScript 支持。",
    "keywords": "Vue, JavaScript, 前端框架, 性能优化, TypeScript",
    "content_type": "html",
    "word_count": 980,
    "reading_time": 4,
    "tags": ["技术", "Vue", "JavaScript", "前端", "框架"],
    "author_info": {
      "bio": "前端开发专家，专注于现代JavaScript框架和用户体验设计",
      "social_links": {
        "github": "https://github.com/frontend-master",
        "twitter": "@frontend_master"
      }
    },
    "seo_info": {
      "meta_description": "Vue 3.5 新特性详解，性能提升与开发体验优化",
      "meta_keywords": "Vue,JavaScript,前端框架,性能优化",
      "og_image": "https://example.com/images/vue-3-5-og.jpg"
    },
    "status": 1,
    "created_at": new Date("2025-11-21T14:30:00Z"),
    "updated_at": new Date("2025-11-21T14:30:00Z")
  },
  {
    "news_id": 1003,
    "title": "2025年世界杯：精彩瞬间回顾",
    "content": `
      <h2>2025年世界杯精彩回顾</h2>
      <p>2025年世界杯落下帷幕，留下了无数令人难忘的精彩瞬间。</p>
      
      <h3>决赛亮点</h3>
      <p>在激动人心的决赛中，两支顶级球队展现了世界级的足球水平。</p>
      
      <h3>最佳球员</h3>
      <ul>
        <li>金球奖：表现出色的中场核心</li>
        <li>金靴奖：本届赛事最佳射手</li>
        <li>最佳门将：零失球记录保持者</li>
      </ul>
      
      <h3>技术统计</h3>
      <p>本届世界杯在技术应用方面也取得了突破：</p>
      <ul>
        <li>VAR技术更加成熟</li>
        <li>球门线技术精准无误</li>
        <li>数据分析帮助球队制定战术</li>
      </ul>
    `,
    "summary": "2025年世界杯圆满结束，回顾本届赛事的精彩瞬间、最佳球员表现和技术统计亮点。",
    "keywords": "世界杯, 足球, 体育赛事, 决赛, 最佳球员",
    "content_type": "html",
    "word_count": 850,
    "reading_time": 3,
    "tags": ["体育", "足球", "世界杯", "赛事回顾"],
    "author_info": {
      "bio": "体育记者，专注于足球赛事报道和分析",
      "social_links": {
        "twitter": "@sports_reporter"
      }
    },
    "seo_info": {
      "meta_description": "2025年世界杯精彩瞬间回顾，决赛亮点和最佳球员",
      "meta_keywords": "世界杯,足球,体育赛事,决赛"
    },
    "status": 1,
    "created_at": new Date("2025-11-22T09:15:00Z"),
    "updated_at": new Date("2025-11-22T09:15:00Z")
  }
]);

// Print a message to the output window.
console.log('新闻内容数据初始化完成');

// =====================================================
// 2.2 评论数据初始化
// =====================================================

// Print a message to the output window.
console.log('开始初始化评论数据...');

db.getCollection('comments').insertMany([
  {
    "news_id": 1001,
    "user_id": 1,
    "parent_id": null,
    "content": "Spring Boot 3.0 确实是一个重要的里程碑！特别是对 Java 17 的原生支持，这将大大提升应用性能。",
    "like_count": 12,
    "reply_count": 3,
    "is_deleted": false,
    "is_pinned": true,
    "user_info": {
      "username": "admin",
      "avatar_url": "https://example.com/avatar/admin.jpg"
    },
    "mentions": [],
    "media": {
      "images": [],
      "videos": []
    },
    "location": {
      "country": "中国",
      "city": "北京",
      "ip_address": "192.168.1.100"
    },
    "device_info": {
      "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
      "platform": "web",
      "browser": "Chrome"
    },
    "status": 0,
    "created_at": new Date("2025-11-20T10:30:00Z"),
    "updated_at": new Date("2025-11-20T10:30:00Z")
  },
  {
    "news_id": 1001,
    "user_id": 2,
    "parent_id": null,
    "content": "期待看到更多关于 GraalVM 支持的详细说明。我们在生产环境中正在考虑使用原生镜像。",
    "like_count": 8,
    "reply_count": 2,
    "is_deleted": false,
    "is_pinned": false,
    "user_info": {
      "username": "user1",
      "avatar_url": "https://example.com/avatar/user1.jpg"
    },
    "mentions": [
      {
        "user_id": 1,
        "username": "admin"
      }
    ],
    "media": {
      "images": [],
      "videos": []
    },
    "location": {
      "country": "中国",
      "city": "上海",
      "ip_address": "192.168.1.101"
    },
    "device_info": {
      "user_agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
      "platform": "web",
      "browser": "Safari"
    },
    "status": 0,
    "created_at": new Date("2025-11-20T11:15:00Z"),
    "updated_at": new Date("2025-11-20T11:15:00Z")
  },
  {
    "news_id": 1002,
    "user_id": 1,
    "parent_id": null,
    "content": "Vue 3.5 的性能提升确实令人印象深刻！我们在项目中已经升级了，开发体验明显改善。",
    "like_count": 6,
    "reply_count": 1,
    "is_deleted": false,
    "is_pinned": false,
    "user_info": {
      "username": "admin",
      "avatar_url": "https://example.com/avatar/admin.jpg"
    },
    "mentions": [],
    "media": {
      "images": [],
      "videos": []
    },
    "location": {
      "country": "中国",
      "city": "北京",
      "ip_address": "192.168.1.100"
    },
    "device_info": {
      "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
      "platform": "web",
      "browser": "Chrome"
    },
    "status": 0,
    "created_at": new Date("2025-11-21T15:00:00Z"),
    "updated_at": new Date("2025-11-21T15:00:00Z")
  }
]);

// Print a message to the output window.
console.log('评论数据初始化完成');

// =====================================================
// 2.3 用户行为数据初始化
// =====================================================

// Print a message to the output window.
console.log('开始初始化用户行为数据...');

db.getCollection('user_behavior').insertMany([
  {
    "user_id": 1,
    "session_id": "sess_admin_001",
    "behavior_type": "VIEW",
    "target_type": "NEWS",
    "target_id": 1001,
    "metadata": {
      "duration": 120,
      "scroll_depth": 0.8,
      "source": "recommendation"
    },
    "device_info": {
      "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
      "platform": "web",
      "browser": "Chrome",
      "screen_resolution": "1920x1080"
    },
    "location": {
      "country": "中国",
      "region": "北京",
      "city": "北京",
      "ip_address": "192.168.1.100"
    },
    "created_at": new Date("2025-11-20T10:05:00Z")
  },
  {
    "user_id": 1,
    "session_id": "sess_admin_001",
    "behavior_type": "LIKE",
    "target_type": "NEWS",
    "target_id": 1001,
    "metadata": {
      "source": "recommendation"
    },
    "device_info": {
      "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
      "platform": "web",
      "browser": "Chrome",
      "screen_resolution": "1920x1080"
    },
    "location": {
      "country": "中国",
      "region": "北京",
      "city": "北京",
      "ip_address": "192.168.1.100"
    },
    "created_at": new Date("2025-11-20T10:30:00Z")
  },
  {
    "user_id": 2,
    "session_id": "sess_user1_001",
    "behavior_type": "VIEW",
    "target_type": "NEWS",
    "target_id": 1002,
    "metadata": {
      "duration": 95,
      "scroll_depth": 0.6,
      "source": "search"
    },
    "device_info": {
      "user_agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
      "platform": "web",
      "browser": "Safari",
      "screen_resolution": "2560x1600"
    },
    "location": {
      "country": "中国",
      "region": "上海",
      "city": "上海",
      "ip_address": "192.168.1.101"
    },
    "created_at": new Date("2025-11-21T14:35:00Z")
  }
]);

// Print a message to the output window.
console.log('用户行为数据初始化完成');

// =====================================================
// 3. 数据验证和统计
// =====================================================

// 验证数据初始化结果
console.log('开始验证数据初始化结果...');

// 统计各集合的文档数量
const newsCount = db.getCollection('news').countDocuments();
const commentsCount = db.getCollection('comments').countDocuments();
const userBehaviorCount = db.getCollection('user_behavior').countDocuments();
const fileMetadataCount = db.getCollection('file_metadata').countDocuments();
const systemCacheCount = db.getCollection('system_cache').countDocuments();

// Print statistics to the output window.
console.log(`新闻内容: ${newsCount} 条`);
console.log(`评论数据: ${commentsCount} 条`);
console.log(`用户行为: ${userBehaviorCount} 条`);
console.log(`文件元数据: ${fileMetadataCount} 条`);
console.log(`系统缓存: ${systemCacheCount} 条`);

// 示例查询：验证数据完整性
const latestNews = db.getCollection('news').find({}).sort({ "created_at": -1 }).limit(3);
console.log('最新3条新闻:');
latestNews.forEach(news => {
  console.log(`- ${news.title} (ID: ${news.news_id})`);
});

// 示例聚合：用户行为统计
const behaviorSummary = db.getCollection('user_behavior').aggregate([
  { $group: { _id: "$behavior_type", count: { $sum: 1 } } },
  { $sort: { count: -1 } }
]);

console.log('用户行为统计:');
behaviorSummary.forEach(stat => {
  console.log(`- ${stat._id}: ${stat.count} 次`);
});

// =====================================================
// 4. 示例查询操作
// =====================================================

// 示例查询：查找热门新闻
const hotNews = db.getCollection('news').find({
  "status": 1,
  "created_at": { $gte: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000) }
}).sort({ "created_at": -1 }).limit(10);

// Print a message to the output window.
console.log(`找到 ${hotNews.count()} 条热门新闻`);

// 示例聚合：统计用户行为
const behaviorStats = db.getCollection('user_behavior').aggregate([
  { $match: { "created_at": { $gte: new Date(Date.now() - 24 * 60 * 60 * 1000) } } },
  { $group: { _id: "$behavior_type", count: { $sum: 1 } } },
  { $sort: { count: -1 } }
]);

// Print a message to the output window.
console.log('用户行为统计完成');

// Print completion message
console.log('MongoDB完整初始化脚本执行完成！');
