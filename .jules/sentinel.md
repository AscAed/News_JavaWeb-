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

## 2026-07-11 - Fix Unauthenticated File Upload Vulnerability
**Vulnerability:** Unauthenticated users could upload files via the `/api/v1/common/upload` endpoint. The endpoint was exposed globally in `SecurityConfig.java` under `permitAll()`, and lacked method-level authorization in the controller.
**Learning:** Resource-intensive endpoints, such as file uploads, should never be globally permitted (`permitAll()`) as they allow anonymous users to potentially exhaust server storage and resources. This is particularly dangerous for file uploads since they consume disk space and bandwidth.
**Prevention:** Avoid using `.permitAll()` in Spring Security configuration for resource-intensive endpoints like file uploads. Always secure these endpoints, and add method-level `@PreAuthorize` annotations (e.g., `@PreAuthorize("isAuthenticated()")`) for defense-in-depth to ensure only authenticated users can access them.
