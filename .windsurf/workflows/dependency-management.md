---
auto_execution_mode: 1
description: 自动更新依赖，检查破坏性变更。
---
# /dependency-management

自动更新依赖，检查破坏性变更。

1. **检查过期依赖**：
   - npm: `npm outdated`
   - pip: `pip list --outdated`
   - Maven: `mvn versions:display-dependency-updates`
2. **安全更新**：
   - npm: `npm update`（小版本）或指定版本更新
   - pip: `pip install --upgrade package-name`
3. **破坏性变更检测**：
   - 查看 CHANGELOG 或发布说明
   - 如有重大版本变更，询问用户是否继续
4. **测试验证**：运行测试确保更新不破坏功能
5. **报告**：
   - 更新的包及版本
   - 如有问题的包回滚建议

**调用命令**：`/dependency-management`
