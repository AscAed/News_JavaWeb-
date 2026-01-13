---
description: 
---

# /security-scan

执行 SAST（静态应用安全测试）+ 依赖安全检查。

1. **依赖安全扫描**：
   - npm: `npm audit`
   - pip: `pip-audit`
   - Maven: `mvn dependency-check:check`
2. **SAST 工具**：
   - 使用 Trivy（容器）/ SonarQube（代码质量）
   - 检查常见漏洞（CWE-94 Code Injection, CWE-78 Command Injection 等）
3. **报告输出**：
   - 发现的漏洞列表（严重程度）
   - 修复建议
   - 可忽略项说明

**调用命令**：`/security-scan`

**后续行动**：与 `/run-tests-and-fix` 配合，修复安全问题后重新测试
