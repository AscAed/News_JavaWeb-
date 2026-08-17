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

## 2026-08-17 - Fix Hardcoded Identity in AdminController
**Vulnerability:** Hardcoded user identity in `AdminController.java`. The `getCurrentUserId` and `getCurrentUsername` methods were returning fixed placeholder values (`1` and `"admin"`) instead of dynamically resolving the user context. This severely compromised the system's security, as all admin actions, including sensitive ones like updating news status, were logged and processed under user ID `1`, regardless of the actual user performing them. It effectively bypassed individual identity and audit trail constraints.
**Learning:** Hardcoding sensitive contextual information like user identities in controllers instead of utilizing the authentication framework creates critical vulnerabilities. It falsifies audit logs and nullifies identity-based authorization logic for modification endpoints.
**Prevention:** Never hardcode user identities in controllers. Always extract identity dynamically from the authenticated principal via `SecurityContextHolder.getContext().getAuthentication()` and safely cast to `CustomUserDetails` to obtain the correct user information for audit logs and modifications. If the context is missing, throw an `AccessDeniedException`.
