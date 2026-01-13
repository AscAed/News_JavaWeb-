---
description: 
---

# /generate-unit-tests

根据现有代码自动生成单元测试（需要用户审核）。

1. **分析源代码**：识别每个函数、类、方法
2. **生成测试用例**：
   - 正常用例（happy path）
   - 边界情况（empty input, null, etc）
   - 异常处理
3. **创建测试文件**：
   - Python: `test_*.py`
   - TypeScript: `*.test.ts`
   - Java: `*Test.java`
4. **覆盖率分析**：计算代码覆盖率，标记未覆盖部分
5. **用户审核**：提示用户检查生成的测试，确保符合预期

**调用命令**：`/generate-unit-tests`

**最佳实践**：TDD 工作流中，先写测试再实现代码
