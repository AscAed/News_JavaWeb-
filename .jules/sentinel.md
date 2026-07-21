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

## 2026-07-21 - Fix Unauthenticated File Upload in SecurityConfig
**Vulnerability:** Unauthenticated file upload via `/api/v1/common/upload`. The endpoint was globally permitted in `SecurityConfig.java` (`.requestMatchers("/api/v1/common/upload").permitAll()`) and lacked method-level authorization. This could allow unauthenticated users to upload arbitrary files, potentially leading to server resource exhaustion, hosting malicious files, or exploiting upload-related vulnerabilities.
**Learning:** Endpoints dealing with file uploads or resource-intensive operations should never be left globally unauthenticated ("Allow upload for now") unless explicitly required by the business logic and protected by other means (like rate limiting or strict validation).
**Prevention:**
1. Remove such endpoints from the global `permitAll()` list in `SecurityConfig`.
2. Implement defense-in-depth by adding method-level authorization (e.g., `@PreAuthorize("isAuthenticated()")`) to controller methods handling file uploads or modifications.
