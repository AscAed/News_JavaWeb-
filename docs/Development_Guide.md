# 「易闻趣事」项目开发配置与架构指北 (Development Guide)

这是为了帮助后继开发人员与 AI 快速掌握 **易闻趣事（News_JavaWeb）** 毕业设计项目的全系统运行机制而设立的开发手册。

## 一、 系统架构理念与需求基准

本项目是一个专业级本科毕业设计，执行典型的前后端分离开发模式。
系统定位：**新闻聚合器平台**。能自动从 RSS 等外部媒介抽取入库，也允许驻场媒体公开发源。

### 1. 前端设计哲学 ("Google News" 风格化)

1. **品牌呈现**：网站名为**「易闻趣事」**。在首页 Logo 和各类品牌标识中，文字“易”字必须进行**艺术化加工**，其设计灵感来自于《周易》（例如：融入太极图边缘、引入八卦爻线几何图形，融合现代时尚风格等创新），既蕴含传统智慧，又结合大众流行视觉元素呈现个性化。
2. **布局规范理念 (类 Google News)**：
   - **全局纵向导航栏（左侧）**：用于承载大区域的“订阅源切换”。如：“原创”（此为系统站内创作者生态，**默认选中项**）、外部抓取的“IT之家”、“凤凰网”等独立源。
   - **内容横向导航栏（顶部）**：依附于左侧选中的栏目，生成对应的“细分参数列表”（相当于各类目的选项卡 `Category / Section`，如分类“科技”、“社会”）。
   - **主体信息流区域**：由侧边栏和顶部标签确定的带参请求，向后端发起拉取数据，并渲染单条呈现界面（含有独立新闻标题，下挂新闻 `Tag`、出版日期和配图缩略图）。
   - **详情全屏覆盖**：点击列表任意项，弹出或跳转文章的专注详情页面展示全文。
3. **技术栈**: `@/News_Vue` 运用 Vue 3 + Vite + TypeScript 技术结合成熟 CSS 风格库，利用 Axios 处理调用。

### 2. 后端底层驱动理念

后端遵守业界严谨、标准的抽象设计同时为系统的“新闻聚合”特殊性做了深层架构优化：

1. **基础设施**: `@/News_SpringBoot` 基于 Spring Boot 4.x 构建，遵守标准三层结构理念（Controller, Service, Repository）。
2. **混合存储策略 (Storage Tier Dual-Engine)**:
   - **MySQL (结构化中枢)**: 维护核心结构，包含用户权限系统、订阅源的配置参数及地址、以及原创或是 RSS 获取新闻的基础属性（如标题、来源、归档日期等便于快速搜索的字段）。
   - **MongoDB (文档中枢)**: 专业处理文章巨型正文非结构化数据或页面爬虫回传的 DOM。它天然切合“富文本保存”、“高频非结构日志读写”特性，极大降低了关系库表的体积。后侧也可针对它做相关算法统计检索优化处理。
   - **本地 Files**: 采用本地上传挂载保存用户头像或文章附件。

---

## 二、 项目整体结构与目录体系 (Project Structure)

### 1. 后端工程架构 (`News_SpringBoot`)

后端核心包位于 `src/main/java/com/zhouyi`，全面应用 MVC 与领域驱动结合的分层思想：
- **`controller/`**: 对外暴露交互 API。其中分化出 `/mongo` 目录专栏处理 RSS 及 MongoDB 相关的大数据业务。
- **`service/`**: 承载核心业务逻辑，定义接口与具体实现类（`Impl`）。
- **`mapper/`**: MyBatis 接口定义层，操作 MySQL。
- **`repository/`**: Spring Data MongoDB 继承的文档型操作接口，对原生 RSS 文档数据进行存取。
- **`entity/` & `dto/`**: 数据传输对象层。区别于底层的实体映射，DTO 严格保护并塑造了输出给前端的数据形状。
- **`common/`**: 通用组件层。包含全局异常拦截器、JWT 令牌签发器、统一响应包装 `Result<T>` 以及各类 Utility 工具。
- **`config/`**: 注入 Spring 容器的核心配置中心，包括跨域（CORS）、WebMVC 以及 MongoDB 配置等。

### 2. 前端工程架构 (`News_Vue`)
基于 Vite 驱动的 Vue 3 单页面应用，开发重心位于 `src/` 目录下：
- **`assets/`**: 存放全局样式（CSS/SCSS）、定制的“易闻趣事”静态图片及 SVG 矢量图标。
- **`components/`**: 存放公共组件。如顶部横向导航 `CategoryNav.vue`、左侧纵向源导航 `SourceSidebar.vue`、新闻卡片切片 `NewsCard.vue` 及布局脚手架 `AppLayout.vue` 等。这些是实现 Google News 风格化的 UI 基石。
- **`views/`**: 页面级路由组件。映射各个访问路径的骨架容器。
- **`api/`**: 聚合了所有的请求发送。内含 Axios 拦截器 `request.ts` 与划分好领域的独立请求集合（例如 `headline.ts`, `modules/news.ts`）。
- **`router/` & `stores/`**: 配置前端路由逻辑与 Pinia 状态管理库（主要用于维护和穿透已登录用户的 Session 状态）。

## 三、 当前模块实现情况概览 (Module Implementation Status)

平台的核心能力按领域划分为以下几大板块，当前实现度及逻辑如下：

1. **认证与鉴权模块 (Authentication)**
   - **完成度**: 高。
   - **实现机制**: 
     - **双 Token 体系**: Access + Refresh Token。后端使用 SHA-512 加密。前端通过 Axios 注入请求头实现静默流转。包含基本的登录、个人信息管理。
     - **Mailjet 邮件安全注册**: 从普通注册升级为基于发送邮件验证码的二次校验。
       - **依赖与配置**: 后端集成 `mailjet-client`（v6.0.1），需在 `application.yml` 的 `custom.mailjet` 节点配置一对秘钥及经过网关验证的发件人邮箱(`sender-email`)。
       - **交互流程**: 前端在注册页发送验证码并启动 60 秒防刷冷冻倒计时；后端开放 `/api/auth/send-code`，收到请求生成随机 6 位验证码存入 DB/Redis(限制 5 分钟有效)，借助第三方 API 模板下发；最终在 `/api/auth/register` 提交时比对判定。

2. **内容创作模块 (Media/Original Content)**
   - **完成度**: 核心完成。
   - **实现机制**: 支持平台系统内被授权的媒体用户和管理员“手动发稿”。数据采取 MySQL（录入标题、来源）和 MongoDB（录入极长正文流、多媒体 URL 链接）并库储存策略。目前该源在左侧侧边栏标示为“原创”。

3. **RSS 聚合与定时调度模块 (RSS Aggregation)**
   - **完成度**: 高级成型状态。
   - **实现机制**: 通过后端服务周期性抓取（Fetch）设定的第三方外部 RSS 地址（可通过后台配置）。将拉落的数据转换、归入 MongoDB 文档树。在前端体现为左侧导航处的其它订阅分类源（如“IT之家”、“凤凰网”）。

4. **主页信息流聚合分发系统 (News Feed)**
   - **完成度**: 高，已完成双轴兼容。
   - **实现机制**: 前端界面深度仿照 Google News 布局。用户点选组合“数据源 (来源)”和“栏目 (分类)”时，统一交由路由控制。后端暴露出的 `/api/v1/headlines` 和 `/api/v1/mongo` 做无缝桥接，聚合并抹平原生数据库的查表差异返回统一 `UnifiedNews` 结构体进行页面绘制。

5. **互动系统 (Comments & Interactions)**
   - **完成度**: 基础闭环。
   - **实现机制**: 包括独立评论功能、针对新闻的收藏以及点赞动作。

## 四、 数据库环境准备与启动参数

在启动后端工程前，必须先保证数据库双引擎的存活和相互连通。前端的数据渲染本质来源于此两大库的数据透传。

### 1. MySQL 引擎配置
- **连接字符串**: `jdbc:mysql://localhost:3306/news_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai`
- **用户名 / 密码**: `root` / `root`
- **目标数据库库名**: `news_db` (若是第一次启动，须自行建立 `utf8mb4` 的库)

### 2. MongoDB 引擎配置
MongoDB 扮演着本系统正文容器的核心角色。项目中通过 Spring Data MongoDB 对它进行通信操作。
MongoDB 的二进制存放在指定的绝对路径下，不需要配置系统环境变量。由于未设置额外鉴权，使用命令启动无账户密码的默认模式。

- **MongoDB 存放物理路径**: 
  `D:\PUBLIC\STUDY\MongoDB\mongodb-windows-x86_64-8.2.2\mongodb-win32-x86_64-windows-8.2.2\bin`
- **本地默认端口**: `27017`
- **数据库集名**: `News_MongoDB`
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

## 五、 Redis 分布式快取 (Cache) 体系

系统为了应对高并发读取（如新闻列表、详情详情和分类获取），全面引入了 Spring Cache 与 Redis 集成：

1. **基礎設定**：`RedisConfig` 中配置了防範 ClassCastException 的 `GenericJackson2JsonRedisSerializer` 以及全域的 TTL（預設 10 分鐘）。
2. **列表快取防擊穿設計 (手動控制 TTL)**：
   在 `HeadlineServiceImpl` 的 `getHeadlinesByPage` 對首頁或無條件搜尋的第一頁，採取了主動的短效期（5 分鐘）快取 `redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);`，以在提供極致效能的同時保證資訊新鮮度，並防止全部失效造成的 DB 壓力。
3. **詳情頁熱點鎖定**：
   單篇文章取得時 (`getHeadlineById`) 利用了 `@Cacheable(value = "articleDetail", key = "#hid", sync = true)` 注解。此處 `sync = true` 是**防止快取擊穿**的關鍵配置。當高併發同時訪問一個過期的資源，只有一條線程會深入 DB 查詢，其他將被阻塞等待。

---

## 六、 后续开发协同规约（AI 与 Dev 通用）

1. **RESTful 与产品逻辑一致性校验**: 
   - 进行前后端接口封装与对接时，务必将前方的参数格式映射入后端规定。
   - 对于 UI 要求的“源”+“参数”组合的带参查询，须组合构建 Axios 对象参数传向后端的 `GET /headlines` （利用其内置的 `sourceType` 以及 `typeId` 进行多重判定）。
2. **内容隔离原则**: 无论是“内部媒体原创的内容产生”，还是“系统通过 RSS 搜刮的新闻”，统统按照『基础字段打入 MySQL，富文本扔进 MongoDB』的动作进行执行入库。读取时也是依靠内部聚合并作响应，不要破坏此双轴原则。
3. **视觉设计边界**: 从此往后生成新的 Vue 样式和模板或图片时，都必须把“现代化”、“类 Google News 科技感排列交互”以及“国风易学艺术字标识”深深融入界面设计稿当中。杜绝复古、粗糙的“毕业设计式面板”。
