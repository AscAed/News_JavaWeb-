---
trigger: always_on
---

# Performance Optimization Rules

## 查询优化
- 添加数据库索引到频繁查询的字段
- 避免 N+1 查询问题，使用联接或批处理
- 使用查询结果缓存（Redis/Memcached）

## 依赖分析
- 监控依赖包大小，避免不必要的重依赖
- 定期检查未使用的导入和死代码
- 使用树摇优化（Tree Shaking）移除未使用代码

## 资源约束
- API 响应时间 < 500ms
- 前端首次加载 < 3s
- 内存使用监控，避免内存泄漏