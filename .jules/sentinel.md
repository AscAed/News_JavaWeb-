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

## 2026-07-20 - Fix Sensitive Data Exposure and Unauthenticated File Upload
**Vulnerability:**
1. The `User` entity lacked Jackson serialization annotations for the `password` field, causing password hashes to be inadvertently exposed in JSON responses when returning `Result<User>`.
2. The `SecurityConfig` explicitly permitted unauthenticated access to the file upload endpoint `/api/v1/common/upload`, leading to potential storage exhaustion and unauthenticated malicious file uploads.
**Learning:**
1. Always explicitly mark sensitive fields like `password` with `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` to prevent unintentional exposure in APIs, even if DTOs are usually used.
2. Be cautious with `.permitAll()` in Spring Security, particularly for modification endpoints (POST/PUT/DELETE) and resource-intensive endpoints like file uploads.
**Prevention:**
1. Default to using DTOs for API responses instead of raw entities. If entities are returned directly, diligently apply `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` on sensitive fields.
2. Regularly audit Spring Security `.permitAll()` lists to ensure they only contain truly public read-only paths.
