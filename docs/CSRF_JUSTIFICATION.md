# CSRF exceptions and rationale (Sonar S4502)

This document centralizes the project's decisions to ignore CSRF on certain API endpoints and provides review guidance for SonarCloud security hotspot reviewers.

## Summary

- We **do not disable CSRF globally**. Instead we use:

```java
.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
```

- The exception is applied only to stateless API endpoints that:
  - Use JWT Bearer tokens in the `Authorization` header
  - Do not rely on cookies or server-side sessions
  - Are behind the API Gateway that **does NOT allow credentials** (`setAllowCredentials(false)`).

## Files with CSRF exceptions (S4502)

- `user-service/src/main/java/com/safezone/user/config/SecurityConfig.java`
- `product-service/src/main/java/com/safezone/product/config/SecurityConfig.java`
- `order-service/src/main/java/com/safezone/order/config/SecurityConfig.java`

Each of these files contains an explicit "Sonar S4502 justification" comment block explaining the rationale and review notes. When reviewing the Sonar Security Hotspot, confirm:

- The endpoint uses JWT auth (Authorization header), not cookies or forms.
- No `setAllowCredentials(true)` is present in the Gateway (see `api-gateway` CORS config).
- If cookies/sessions are introduced later, the CSRF exception is removed and CSRF protection re-enabled.

## How to mark hotspot as "Reviewed" in SonarCloud

1. Open the Security Hotspot list for the project and select the hotspot (pull request context recommended).
2. Add review comments if needed and set the status to **"Reviewed"** when you confirm the code is safe.

If you want, I can prepare a short PR description and checklist to attach to the Sonar review to help reviewers mark the hotspots as Reviewed.
