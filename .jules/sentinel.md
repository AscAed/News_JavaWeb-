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

## 2026-07-17 - Fix Authorization Bypass in Admin Endpoints
**Vulnerability:** Several administrative endpoints (, , and ) lacked method-level authorization. While some paths might be protected by global , relying solely on path-based configuration without defense-in-depth is risky and prone to configuration errors or bypasses.
**Learning:** Always implement defense-in-depth. Method-level  annotations on controllers provide a second layer of security, ensuring that even if global security rules are misconfigured or paths change, sensitive endpoints remain protected.
**Prevention:** Apply  annotations directly to sensitive controller methods, particularly those that modify state, perform administrative tasks, or trigger resource-intensive operations.

## 2024-07-16 - Fix Authorization Bypass in Admin Endpoints
**Vulnerability:** Several administrative endpoints (`/api/v1/migration/rss-to-mongo`, `/api/v1/mongo/rss-subscriptions/{id}/fetch`, and `/api/v1/mongo/articles/cleanup`) lacked method-level authorization. While some paths might be protected by global `SecurityConfig`, relying solely on path-based configuration without defense-in-depth is risky and prone to configuration errors or bypasses.
**Learning:** Always implement defense-in-depth. Method-level `@PreAuthorize("hasRole('ADMIN')")` annotations on controllers provide a second layer of security, ensuring that even if global security rules are misconfigured or paths change, sensitive endpoints remain protected.
**Prevention:** Apply `@PreAuthorize` annotations directly to sensitive controller methods, particularly those that modify state, perform administrative tasks, or trigger resource-intensive operations.
