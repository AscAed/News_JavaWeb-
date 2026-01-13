# 项目 AGENTS.md

## 项目概述
- 项目名称与用途
- 核心技术栈
- 开发环境要求

## 构建与测试命令
- Build: `npm run build` / `mvn clean package`
- Test: `npm test` / `pytest -v`
- Lint: `npm run lint` / `flake8 .`
- Format: `npm run format` / `black .`

## 代码风格
- 使用 2 空格缩进（JavaScript）或 4 空格（Python）
- 禁止使用 `var`，使用 `const`/`let`（JS）
- 所有函数必须有文档注释

## 文件结构
- `src/` - 源代码
- `test/` - 测试代码
- `docs/` - 文档
- `.windsurf/rules/` - AI Rules
- `.windsurf/workflows/` - AI Workflows

## 安全要求
- 禁止提交密钥、API 密钥、密码
- 所有外部输入必须验证
- 数据库查询使用 ORM

## 注意事项
- 保持提交消息清晰和简洁
- 向上兼容 API，破坏性变更需确认
