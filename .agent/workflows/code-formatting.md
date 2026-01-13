---
description: 
---

# /code-formatting

统一执行代码格式化和 Lint 检查，确保代码风格一致。

1. **检测项目类型**：识别语言和格式化工具（Prettier / Black / ESLint 等）
2. **执行格式化**：
   - JavaScript/TypeScript: `npm run prettier --write .` + `npm run eslint --fix .`
   - Python: `black .` + `flake8 .`
   - Java: `mvn checkstyle:check` + spotless
3. **生成格式化报告**：
   - 修改的文件列表
   - 修复的问题数量
   - 剩余的 Lint 警告（如需要手动修复）
4. **验证通过**：所有文件格式化完成，可提交

**调用命令**：`/code-formatting`

**快捷配置**：修改命令以适配你的项目
