---
trigger: model_decision
description: Testing
---
# Testing Rules

## 单元测试要求
- 为每个新功能或 bug 修复编写单元测试
- 测试覆盖率必须 ≥ 80%，关键路径 ≥ 95%
- 测试文件命名规范：`{module}.test.{ext}` 或 `test_{module}.{ext}`

## 测试框架
- Frontend: Jest + @testing-library
- Backend (Java): JUnit 5 / Mockito
- Backend (Python): pytest
- 保持测试框架与现有代码一致

## TDD 工作流（必须遵循）
1. 先写红色测试（测试现有代码无法通过）
2. 再写最少代码让测试通过
3. 重构改进，保证测试继续通过
4. 禁止删除失败的测试，除非获得用户明确授权

## 测试命令
- `npm test` / `npm run test:watch`
- `mvn test` / `gradle test`
- `pytest -v` / `pytest --cov`
- 在提交前必须本地运行测试