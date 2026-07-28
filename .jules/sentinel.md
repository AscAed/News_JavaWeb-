## 2026-07-09 - Fix SQL Injection in NewsTypeMapper
**Vulnerability:** SQL Injection in ORDER BY clause in `NewsTypeMapper.java`. The `findByPage` method used `${sortBy}` and `${sortOrder}` directly which is vulnerable since it directly embeds user input into the SQL query.
**Learning:** MyBatis string interpolation `${}` is dangerous and allows SQL injection. While parameters can usually use `#{}` to be parameterized, `ORDER BY` clauses cannot.
**Prevention:** Avoid `${}` for dynamic sorting. Instead, use a `<choose>` block inside a `<script>` tag in the MyBatis mapper to enforce an allowlist for column names and sort directions.

## 2026-07-10 - Fix Authorization Bypass in SecurityConfig and NewsTypeController
**Vulnerability:** Authorization bypass via overly permissive `SecurityConfig` configuration. Endpoints like `/api/v1/categories/**` were globally permitted for all HTTP methods in `SecurityConfig.java`. Furthermore, modification endpoints (POST, PUT, DELETE, PATCH) in `NewsTypeController` lacked method-level `@PreAuthorize` protections.
**Learning:** In Spring Security, using `requestMatchers("...")` without specifying an `HttpMethod` permits *all* HTTP methods for that path. Additionally, relying solely on path-based security configuration without defense-in-depth (method-level `@PreAuthorize` annotations on controllers) can lead to critical bypasses if the global configuration is flawed.
**Prevention:**
1. Always specify the explicit `HttpMethod` (e.g., `HttpMethod.GET`) when whitelisting public endpoints in `SecurityConfig`.
2. Implement defense-in-depth by always annotating modification endpoints (POST, PUT, DELETE, PATCH) with method-level authorization (like `@PreAuthorize("hasRole('ADMIN')")`), even if you believe global configuration restricts them.

## 2023-10-27 - [Add Defense-in-Depth for File Upload]
**Vulnerability:** File upload endpoints (e.g., `/api/v1/common/upload`) were inadvertently whitelisted in `SecurityConfig.java`'s `.permitAll()` list and lacked method-level `@PreAuthorize` guards, allowing unauthenticated file uploads.
**Learning:** Resource-intensive and modification endpoints (POST/PUT/DELETE/file uploads) should never be exposed via `.permitAll()`. They are prone to abuse, unauthenticated access, and server exhaustion. Relying solely on `SecurityConfig.java` is brittle.
**Prevention:** Always secure modification and upload endpoints by removing them from `SecurityConfig.java`'s whitelist and applying defense-in-depth with `@PreAuthorize("isAuthenticated()")` or `@PreAuthorize("hasRole('ADMIN')")` at the controller method level.
