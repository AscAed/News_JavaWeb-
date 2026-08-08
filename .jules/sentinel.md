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

## 2026-08-08 - Prevent Password Exposure in Logs and API Responses
**Vulnerability:** Plaintext passwords were logged to the console using `System.out.println` during login attempts in `UserServiceImpl.java`. Additionally, the `User.java` entity exposed the password hash in JSON responses from API endpoints returning user profiles.
**Learning:** Hardcoded print statements can easily expose sensitive PII and authentication credentials in server logs. Default JSON serialization (like Jackson in Spring Boot) will serialize all entity fields unless explicitly told not to, leading to unintentional exposure of password hashes.
**Prevention:**
1. Never use `System.out.println` for application logging, especially in authentication logic. Use properly configured logging frameworks (like SLF4J/Logback) and ensure sensitive fields are obfuscated or excluded.
2. Always add `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` (or equivalent) to password fields in entity classes and DTOs that interact with the web layer.
