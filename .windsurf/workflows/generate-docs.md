---
description: 根据代码注释自动生成 API 文档或 README。
---
# /generate-docs

根据代码注释自动生成 API 文档或 README。

1. **API 文档生成**：
   - 从代码注释提取信息
   - 生成 OpenAPI/Swagger 规范
   - 输出 HTML 或 Markdown 文档
2. **README 更新**：
   - 自动列举项目功能和命令
   - 添加依赖列表、安装说明
3. **CHANGELOG 更新**：
   - 根据 Git 历史记录生成变更日志
4. **输出**：
   - `docs/API.md`
   - `README.md`（如需要）
   - `CHANGELOG.md`

**调用命令**：`/generate-docs`
