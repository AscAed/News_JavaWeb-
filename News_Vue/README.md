# 新闻头条前端项目

## 项目概述

基于Vue3 + TypeScript + Element Plus + Tailwind CSS构建的现代化新闻管理系统前端，与Spring Boot后端完美集成。

## 技术栈

### 核心框架
- **Vue 3.4+** - 渐进式JavaScript框架
- **TypeScript 5.0+** - JavaScript超集，提供类型安全
- **Vite 5.0+** - 下一代前端构建工具

### UI框架
- **Element Plus** - Vue3组件库
- **Tailwind CSS** - 实用优先的CSS框架

### 状态管理
- **Pinia** - Vue3官方推荐的状态管理库

### 路由管理
- **Vue Router 4** - Vue官方路由管理器

### HTTP客户端
- **Axios** - 基于Promise的HTTP客户端

### 开发工具
- **ESLint** - 代码质量检查
- **Prettier** - 代码格式化
- **Vue DevTools** - Vue开发调试工具

## 项目结构

```
News_Vue/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── config.ts      # API配置
│   │   ├── request.ts     # HTTP请求封装
│   │   └── modules/       # API模块
│   │       ├── auth.ts     # 认证相关
│   │       ├── news.ts     # 新闻相关
│   │       └── interaction.ts # 互动相关
│   ├── assets/            # 静态资源
│   │   └── main.css     # 主样式文件
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   │   └── index.ts      # 路由主文件
│   ├── stores/            # 状态管理
│   │   ├── user.ts       # 用户状态
│   │   └── news.ts       # 新闻状态
│   ├── types/             # TypeScript类型定义
│   │   └── index.ts      # 类型主文件
│   ├── views/             # 页面组件
│   │   ├── auth/         # 认证页面
│   │   ├── news/         # 新闻页面
│   │   ├── user/         # 用户页面
│   │   ├── admin/        # 管理页面
│   │   └── error/        # 错误页面
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── package.json           # 项目依赖
├── tsconfig.json          # TypeScript配置
├── vite.config.ts         # Vite配置
├── tailwind.config.js     # Tailwind配置
└── README.md             # 项目文档
```

## 功能特性

### 🚀 核心功能
- ✅ **用户认证** - 登录/注册/JWT认证
- ✅ **新闻展示** - 新闻列表/详情/搜索
- ✅ **分类筛选** - 按分类浏览新闻
- ✅ **响应式设计** - 适配各种设备尺寸
- 🚧 **评论系统** - 评论/回复/点赞
- 🚧 **收藏功能** - 收藏/取消收藏
- 🚧 **管理后台** - 新闻管理/数据统计

### 🛠️ 技术特性
- ✅ **TypeScript支持** - 完整的类型定义
- ✅ **组件化开发** - 可复用的Vue组件
- ✅ **状态管理** - Pinia状态管理
- ✅ **路由守卫** - 权限控制和页面守卫
- ✅ **HTTP拦截** - 统一的请求/响应处理
- ✅ **错误处理** - 全局错误处理机制
- ✅ **代码规范** - ESLint + Prettier

## 开发指南

### 环境要求
- Node.js 18+
- npm 7+

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 代码检查
```bash
npm run lint
```

### 代码格式化
```bash
npm run format
```

## API集成

### 后端API地址
- 开发环境: `http://localhost:8080/api/v1`
- 生产环境: 根据实际部署配置

### 主要API模块
- **认证模块** (`/auth`) - 登录、注册、token管理
- **新闻模块** (`/headlines`) - 新闻CRUD、查询
- **用户模块** (`/users`) - 用户信息管理
- **文件模块** (`/common/upload`) - 文件上传
- **互动模块** (`/comments`, `/favorites`) - 评论、收藏

### 请求/响应格式
```typescript
// 请求格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 认证头
headers: {
  'Authorization': `Bearer ${token}`
}
```

## 部署说明

### 构建配置
项目使用Vite构建，支持多环境配置：
- 开发环境 (`.env.development`)
- 生产环境 (`.env.production`)

### 静态资源
构建后的静态资源位于 `dist/` 目录，可直接部署到任何静态文件服务器。

### 环境变量
```bash
# API基础地址
VITE_API_BASE_URL=http://localhost:8080/api/v1

# 其他配置...
```

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 开发规范

### 代码风格
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化
- 遵循Vue3官方风格指南

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建工具或辅助工具的变动

### 分支管理
- main: 主分支，生产环境代码
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 热修复分支

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](项目地址/issues)
- 邮箱: your-email@example.com

---

**注意**: 这是一个毕业设计项目，旨在展示现代前端开发技术和最佳实践。
