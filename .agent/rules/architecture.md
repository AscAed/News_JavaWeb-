---
trigger: always_on
---

# Architecture Rules

## 分层设计（Layered/Hexagonal）
- **Controller/Handler 层**：HTTP 请求处理，参数验证
- **Service 层**：业务逻辑，不访问数据库
- **Repository 层**：数据访问，统一查询接口
- 禁止跨越层直接访问（例如 Controller 直接访问 Repository）

## API 设计
- RESTful 命名规范：`/api/v1/resources`
- 版本管理：URL 路径中包含版本号（如 `/v1/`、`/v2/`）
- 统一错误响应格式：`{ code, message, details }`

## 后向兼容性
- 修改公开 API 时必须考虑兼容性
- 破坏性变更需要用户明确确认
