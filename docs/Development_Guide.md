# News_Web 项目开发指引 (Development Guide)

本指引旨在为本项目（基于混合存储与多源聚合的新闻 Web 系统）的后续开发、调试与工程维护提供标准化依据。

---

## 一、 基础架构设计 (Infrastructure Overview)

### 1. 后端工程 (Spring Boot)
- **技术栈**: JDK 25, Spring Boot 4.0.1, MyBatis-Plus, Spring Security, JWT.
- **工程目录**: `News_SpringBoot`
- **核心职能**: 提供 RESTful API，处理多表关联查询、分布式缓存逻辑维护、RSS 数据转换流水线。

### 2. 前端工程 (Vue 3)
- **技术栈**: Vue 3 (Composition API), Vite, Pinia, Axios, Vanilla CSS (Mantel Style).
- **工程目录**: `News_Vue`
- **核心职能**: 实现 Google News 风格的高响应式 UI，处理多源数据的异步渲染，维护本地用户状态。

### 3. 混合存储架构 (Hybrid Storage)
- **MySQL**: 存储核心元数据（用户、分类、新闻头条、权限配置）。
- **MongoDB**: 存储非结构化长文本（新闻正文、富文本内容、RSS 抓取原始文档）。
- **Elasticsearch**: 基于头条信息的全量分布式搜索，处理高并发关键词匹配。
- **Redis**: 针对高频访问的列表页数据提供缓存，预防数据库压力骤增。

---

## 二、 基础组件依赖 (Database & Components)

### 1. MySQL (关系型数据库)
- **端口**: `3306`
- **库名**: `news_db`
- **角色**: 唯一可靠的“业务元数据”源。

### 2. Redis (缓存与分布式锁机制)
- **端口**: `6379`
- **默认选择**: `0`

### 3. MongoDB (非结构化内容处理)
- **本地默认端口**: `27017`
- **数据库名**: `News_MongoDB`
- **必须执行的启动命令**：
```shell
# 建议通过 Powershell 或 CMD 进入 MongoDB bin 目录后运行：
mongod --dbpath=../data/db
```
> *注意*: 该命令依赖上级目录存在 `data/db` 文件夹。该终端需常驻保持运行不关闭，以持续监听前端正文数据的查询索降。

---

## 三、 工程联调启动指引

### 1. 启动 Backend (Spring Boot)
1. 使用 IDEA 等 IDE 加载 `News_SpringBoot` 目录内的代码。
2. 配置好 Java 环境要求 (该底层用较新框架建议 JDK 21+)。
3. 在 VM options (可选项) 里添加 `--enable-native-access=ALL-UNNAMED` 消除控制台原生访问警告。
4. 加载 `pom.xml`，运行启动类应用实例。后端服务将承载在 `http://localhost:8080` 上。可通过浏览器访问 `/api/v1/health` 检查可用性状态。

### 2. 启动 Frontend (Vue 3)
1. 终端深入 `News_Vue` 文件夹内部。
2. 安装环境包：
   ```bash
   npm install
   ```
3. 调出本地开发热更新服务器：
   ```bash
   npm run dev
   ```
4. 控制台一般会提供 `http://localhost:5173/` 等地址访问前门。Vite Proxy 会自动将发送出的 `/api/...` 转发到 `http://localhost:8080/api...`。

---

## 四、 Redis 缓存体系 (Cache)

系统为了应对高并发读取（如新闻列表、详情详情和分类获取），全面引入了 Spring Cache 与 Redis 集成：

1. **基础设置**：`RedisConfig` 中配置了防范 ClassCastException 的 `GenericJackson2JsonRedisSerializer` 以及全局的 TTL（默认 10 分钟）。
2. **列表缓存防击穿设计 (手动控制 TTL)**：
   在 `HeadlineServiceImpl` 的 `getHeadlinesByPage` 对首页或无条件搜索的第一页，采取了主动的短效期（5 分钟）快取 `redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);`，以在提供极致性能的同时保证信息新鲜度，并防止全部失效造成的 DB 压力。
3. **详情页热点锁定**：
   单篇文章获取时 (`getHeadlineById`) 利用了 `@Cacheable(value = "articleDetail", key = "#hid", sync = true)` 注解。此处 `sync = true` 是**防止缓存击穿**的关键配置。当高并发同时访问一个过期的资源，只有一条线程会深入 DB 查询，其他将被阻塞等待。

---

## 五、 后续开发协同规约

1. **RESTful 与产品逻辑一致性校验**:
   - 进行前后端接口封装与对接时，务必将前方的参数格式映射入后端规定。
   - 对于 UI 要求的“源”+“参数”组合的带参查询，须组合构建 Axios 对象参数传向后端的 `GET /headlines` （利用其内置的 `sourceType` 以及 `typeId` 进行多重判定）。
2. **内容隔离原则**: 无论是“内部媒体原创的内容产生”，还是“系统通过 RSS 搜刮的新闻”，统统按照『基础字段打入 MySQL，富文本扔进 MongoDB』的动作进行执行入库。读取时也是依靠内部聚合并作响应，不要破坏此双轴原则。
3. **视觉设计边界**: 从此往后生成新的 Vue 样式和模板及图片时，都必须把“现代化”、“类 Google News 科技感排列交互”以及“国风易学艺术字标识”深深融入界面设计稿当中。杜绝复古、粗糙的“毕业设计式面板”。

---

## 六、 毕业设计“优秀”潜力评估 (Graduation Project Evaluation)

本系统作为 2.5.0 版本的完整产物，在技术深度、架构设计及工程完备性上展现了冲击“校级优秀毕业设计”的强劲实力。以下是基于当前项目状态的深度审计评估：

### 1. 技术栈领先性 (Technological Edge)
- **极致前沿底座**：采用 **JDK 25** (最新 LTS 预览版本) 与 **Spring Boot 4.0.1**，并配合 **Vite 7** + **Vue 3.5**。这种全栈“追新”策略在学术评审中具有极高的“创新点”得分，体现了作者对现代化 Web 生态系统的敏锐把握。
- **异构多维存储模型**：深度集成 **MySQL** (核心元数据)、**MongoDB** (高性能正文)、**Elasticsearch** (全文本分词检索)、**Redis** (分层缓存)、**MinIO** (云原生存储)。五位一体的存储矩阵是典型的“高并发、大流量”互联网架构缩影。

### 2. 架构设计亮点 (Architectural Highlights)
- **发件箱模式 (Outbox Pattern) 与最终一致性**: 弃用了脆弱的内存事件模型，引入了数据库驱动的 **Outbox 消息表**。通过应用内 Worker 与定时补发机制，确保了 MySQL 与 Elasticsearch 之间在极端故障（如断电重启、索引服务宕机）下的**最终一致性**。这是分布式系统架构中解决“双写一致性”问题的工业级标准方案。
- **Java 25 虚拟线程池优化**: 全面开启 **Virtual Threads**，将传统的“一请求一线程”重构为“高并发纤程”模型。在 IO 密集型场景（如 RSS 抓取、异构存储写入）下，显著提升了系统的吞吐上限，无需繁琐的响应式编程模型即可实现极致性能。
- **智能 RSS 聚合流水线**: 利用 `rometools` 与 `jsoup` 实现多源数据抓取与标准化转换，展现了处理外部非结构化数据的能力，增加了项目的业务复杂度。
- **多端持久化保障**: 结合 **MongoDB 数据补偿机制** 与 **Spring 事务强制回滚**，构建了异构存储环境下的“柔性事务”模型。

### 3. 工程化质量保证 (Engineering Excellence)
- **非侵入式治理 (AOP)**：通过自定义注解实现了自动化的 **操作审计 (`@LogOperation`)** 与 **精细化限流 (`@RateLimit`)**，代码结构优雅，业务逻辑纯粹。
- **健壮性保障**：全量接入 `GlobalExceptionHandler`，具备数据库唯一性冲突 (`DataIntegrityViolation`)、参数校验 (`Validation`)、跨域及安全异常的标准化响应能力。

---

## 七、 工程审计报告与进阶路线图 (Audit & Roadmap)

通过对项目的深度扫描，我们识别出以下可进一步优化的“加分项”，作为“从优秀到卓越”的工程进阶路径：

### 1. 深度审计发现 (Audit Findings)
- **同步 I/O 阻塞风险** `[已修复]`：`HeadlineServiceImpl` 原本在列表查询中同步调用 RSS 抓取，导致请求耗时极长且易超时。现已重构为 `RssSyncScheduler` 异步后台同步。
- **高并发计数器瓶颈** `[已修复]`：`page_views` 的逻辑已迁移至 **Redis INCR** 缓冲，并由 `HeadlineStatScheduler` 每 5 分钟批量回写 MySQL，解决了数据库锁争用问题。
- **异常处理颗粒度不足** `[已修复]`：构建了完整的 **业务码体系 (Structured Error Codes)**。引入 `ResultCode` 枚举与 `BusinessException`，实现了业务逻辑与错误响应的解耦。
- **ID 命名一致性陷阱** `[待优化]`：实体层同时存在 `hid` 与 `id`（如 `Headline` 与 `HeadlineDetailDTO` 映射中），这增加了代码重构时的失误风险，建议在后续版本中统一为单一标准（如 `newsId`）。
- **缓存穿透防护缺失 (Cache Penetration)** `[工程隐患]`：目前仅通过 `sync=true` 防范了缓存击穿，但面对恶意请求查询不存在的 `id`，仍会直接穿透至数据库，缺乏布隆过滤器 (Bloom Filter) 或缓存空值机制的前置拦截。
- **富文本跨站脚本攻击 (XSS) 风险** `[技术漏洞]`：MongoDB 存储的富文本未见严格的 HTML 净化逻辑。系统采用混合存储，存在来自外部抓取的 RSS 数据注入恶意脚本的危险。
- **JWT 无状态生命周期失控** `[安全设计瓶颈]`：当前的 JWT 缺乏主动失效控制。用户登出或角色变更后，原 Token 在有效期内依然合法，这在严密的安全系统中是一个明显的短板。

### 2. “优秀论文”冲刺建议 (Success Strategy)
> [!TIP]
> **提升学术维度的六大核心进阶方案：**
> 1. **虚拟线程池并发红利测评 (Virtual Threads under JDK 25)**：建议在论文中重点论证“**虚拟线程对高 I/O 密集型 Web 系统的吞吐量增益及资源利用率优化**”，通过对比传统线程池并配合 JMeter 压测绘制吞吐量曲线，大幅提升学术深度。
> 2. **ETL 流水线与可视化作业大屏**：不仅将 RSS 的“拉取-解析-入库”完全转为异步调度流水线，还建议在前端提供直观的作业大盘与系统级监控雷达。
> 3. **引入布隆过滤器 (Bloom Filter) 防御穿透打击**：针对恶意大流量查询空新闻的风险，引入布隆过滤器进行拦截，作为专门凸显系统“高可用降级与防御策略”的设计亮点写入论文。
> 4. **双 Token 认证体系与动态黑名单**：堵住 JWT 撤销漏洞，颁发短效 AccessToken 与核心刷新票据 RefreshToken，同时接入基于 Redis 的 Token 实时黑名单，构筑强健的安全身份闭环，让论文在此章节彻底脱颖而出。
> 5. **富文本净化安全屏障 (HTML Sanitization)**：在数据持久化层全面接入 OWASP Java HTML Sanitizer 行动级清理，彻底抹除从外部数据源导入 XSS 载荷的风险。
> 6. **构建全链路观测与度量体系 (Observability)**：强烈建议为当前架构引入 Prometheus 探针 + Grafana 面板以汇聚运行时指标。在最终演示防守时，如能直接展现微服务级别的热力图与请求监控，必能对评审组产生统治级碾压效果。

---
### 3. final 审计结论
本项目目前的底层工程水平已**显著超越**常规本科毕业设计要求，完美覆盖了“高可用、高性能、强一致性”三大核心分布式系统特征。若能同步完成上述“进阶方案”中的图表可视化工作，在校级优秀论文甚至更高等级的技术评审中将具备极强的技术压制力。

---
*注：文档更新日期 2026-03-21，项目目前处于 2.5.5-Stable 稳定版本。*
