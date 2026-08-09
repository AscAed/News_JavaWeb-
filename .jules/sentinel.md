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

## 2024-05-18 - Fix Hardcoded User Identity in AdminController
**Vulnerability:** Spoofed audit logs and bypassed traceability due to hardcoded user identity (`1` and `"admin"`) in `AdminController.java`. The `getCurrentUserId()` and `getCurrentUsername()` methods did not extract the actual authenticated user from the request, meaning every operation logged via these methods was attributed to the same static admin account regardless of who actually made the request.
**Learning:** Hardcoding user identities or relying on static defaults for audit logging and modification attribution undermines the entire purpose of an audit trail. Security-sensitive operations must dynamically resolve identity from a secure, request-bound context like `SecurityContextHolder`.
**Prevention:** Always extract identity from the authenticated principal via `SecurityContextHolder.getContext().getAuthentication()` using the application's specific `UserDetails` implementation (e.g., `CustomUserDetails`) to ensure accurate logging and authorization checks.
