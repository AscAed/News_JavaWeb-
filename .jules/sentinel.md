## 2026-07-09 - Fix SQL Injection in NewsTypeMapper
**Vulnerability:** SQL Injection in ORDER BY clause in `NewsTypeMapper.java`. The `findByPage` method used `${sortBy}` and `${sortOrder}` directly which is vulnerable since it directly embeds user input into the SQL query.
**Learning:** MyBatis string interpolation `${}` is dangerous and allows SQL injection. While parameters can usually use `#{}` to be parameterized, `ORDER BY` clauses cannot.
**Prevention:** Avoid `${}` for dynamic sorting. Instead, use a `<choose>` block inside a `<script>` tag in the MyBatis mapper to enforce an allowlist for column names and sort directions.

## 2025-07-11 - [JWT Invalidation Mismatch]
**Vulnerability:** Incomplete JWT invalidation upon logout. The authentication filter correctly checks for token blacklisting using the token's unique ID (`JTI`). However, the logout mechanism was adding the raw token string to the blacklist instead of its `JTI`. As a result, tokens were never properly blacklisted, leaving logged-out sessions still active and vulnerable to unauthorized access.
**Learning:** Security mechanisms often span multiple components (e.g., filter checks vs. endpoint actions). A mismatch in the identifier used (JTI vs. token string) completely neutralizes the protection. It is critical to ensure that the logic for writing to a security blacklist matches the logic for reading from it.
**Prevention:** Standardize security identifiers across the application. When utilizing JWT features like `JTI` for session management, ensure all related components (login, logout, refresh, filters) are aligned to use this exact identifier. Implement comprehensive integration tests that verify an invalidated token is explicitly rejected by the security filters.
