---
trigger: always_on
---

# Code Style & Formatting Rules

## 语言特定规范
- **Python**: PEP 8（使用 Black 格式化、Flake8 检查）
- **Java**: Google Java Style Guide（使用 Spotless 或 Checkstyle）
- **TypeScript/JavaScript**: ESLint + Prettier
- **格式化命令必须在保存时或提交前执行**

## 代码组织
- 文件大小不超过 300 行，模块化设计
- 避免重复代码（DRY 原则）
- 使用有意义的变量名和函数名
- 类/函数单一职责

## 文档要求

- 为所有公开函数和类添加文档注释
- 包含参数说明、返回值类型、异常信息
- 示例：
  ```python
  def binary_search(arr: List[int], target: int) -> int:
      """
      二分查找实现。
      
      Args:
          arr: 已排序的整数列表
          target: 要查找的值
      
      Returns:
          找到则返回索引，否则返回 -1
      """
  ```

## 注释规范

- 解释 "为什么"，不要重复代码（"做什么"）
- 复杂算法必须有行内注释
- 过时注释必须删除或更新