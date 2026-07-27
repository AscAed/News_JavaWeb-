## 2024-05-24 - Unauthenticated File Upload
**Vulnerability:** The `/api/v1/common/upload` endpoint was exposed to unauthenticated users via `permitAll()` in `SecurityConfig.java`.
**Learning:** Temporary exceptions during development (like `// Allow upload for now`) often leak into production, bypassing intended authentication requirements and risking server exhaustion.
**Prevention:** Avoid using `permitAll()` for modification endpoints (POST/PUT/DELETE) or resource-intensive endpoints like file uploads. Use method-level `@PreAuthorize` annotations for defense-in-depth to ensure endpoints are explicitly protected even if global security configurations change.

## 2024-05-24 - Unauthenticated File Upload
**Vulnerability:** The `/api/v1/common/upload` endpoint was exposed to unauthenticated users via `permitAll()` in `SecurityConfig.java`.
**Learning:** Temporary exceptions during development (like `// Allow upload for now`) often leak into production, bypassing intended authentication requirements and risking server exhaustion.
**Prevention:** Avoid using `permitAll()` for modification endpoints (POST/PUT/DELETE) or resource-intensive endpoints like file uploads. Use method-level `@PreAuthorize` annotations for defense-in-depth to ensure endpoints are explicitly protected even if global security configurations change.
