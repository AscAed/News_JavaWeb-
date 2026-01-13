---
description: 标准化 Git 提交和 PR 流程。
---
# /git-workflows

标准化 Git 提交和 PR 流程。

1. **提交前检查**：
   - 运行格式化：`/code-formatting`
   - 运行测试：`/run-tests-and-fix`
   - 运行安全扫描：`/security-scan`
2. **生成提交信息**：
   - 格式：`feat: 功能描述` / `fix: 问题修复` / `docs: 文档更新`
   - 附加相关 issue 号：`Closes #123`
3. **创建 Pull Request**：
   - 自动填充 PR 标题和描述
   - 列举变更内容和测试情况
4. **PR 检查清单**：
   - [ ] 代码通过 lint
   - [ ] 测试全部通过
   - [ ] 更新了相关文档
   - [ ] 没有安全漏洞

**调用命令**：`/git-workflows`
