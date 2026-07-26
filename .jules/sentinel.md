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

## 2026-07-25 - Fix Unauthenticated File Upload via permitAll
**Vulnerability:** Unauthenticated File Upload and potential Server Exhaustion. The endpoint `"/api/v1/common/upload"` was explicitly allowed without authentication via `.permitAll()` in `SecurityConfig.java`, and the `uploadFile` methods in both `CommonController.java` and `FileController.java` lacked method-level `@PreAuthorize` protections.
**Learning:** File upload endpoints are inherently resource-intensive and should never be globally permitted without authentication. Exposing them can allow malicious actors to exhaust server storage or bandwidth. Even when testing or temporarily allowing access ("Allow upload for now"), security controls must be maintained.
**Prevention:**
1. Never use `.permitAll()` for resource-intensive endpoints like file uploads.
2. Implement defense-in-depth by always annotating upload endpoints with `@PreAuthorize("isAuthenticated()")` or stricter roles to ensure only authenticated users can upload files.
