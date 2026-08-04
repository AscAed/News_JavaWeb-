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

## 2026-08-04 - Fix Unauthenticated File Upload Vulnerability
**Vulnerability:** Unauthenticated file upload in `/api/v1/common/upload` allowed any anonymous user to upload files via MinIO, potentially leading to storage exhaustion (DoS) or arbitrary file upload vulnerabilities. The endpoint was globally whitelisted in `SecurityConfig.java` with a "Allow upload for now" comment, and lacked method-level protection in `CommonController.java`.
**Learning:** Temporary security exceptions for resource-intensive endpoints (like file uploads) are dangerous if forgotten. Global permitAll configuration without defense-in-depth method-level `@PreAuthorize` annotations compounds the risk, violating the principle of least privilege.
**Prevention:** Never use `.permitAll()` for modification endpoints (POST/PUT/DELETE) or resource-intensive operations in `SecurityConfig`. Always apply method-level `@PreAuthorize` annotations (e.g., `@PreAuthorize("isAuthenticated()")` or `@PreAuthorize("hasRole('ADMIN')")`) to controller endpoints as a defense-in-depth measure.
