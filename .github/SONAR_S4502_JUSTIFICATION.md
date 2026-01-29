# Sonar S4502 (CSRF) justification — copy/paste for Security Hotspot review

**Context**: This project exposes stateless REST APIs that use JWT Bearer tokens in the `Authorization` header and do NOT rely on cookies or server-side sessions by design.

**Measure**: CSRF protection is NOT globally disabled. Instead, CSRF is applied conditionally in `common.config.CommonSecurityConfigurer`: CSRF is required only for mutating HTTP methods (POST/PUT/PATCH/DELETE) _and_ when the request contains cookies (possible cookie-based auth). This reduces false positives while protecting cookie-based flows.

**Proof**: Unit and integration tests validate this behavior:

- `common/src/test/java/.../CommonSecurityConfigurerTest.java`
- `common/src/test/java/.../CommonSecurityIntegrationTest.java`

**Recommendation**: If a future change introduces cookies/sessions or enables CORS credentials (`allowCredentials=true`), remove the exception and re-enable CSRF protections for the affected endpoints.

**Conclusion**: Reviewed and considered safe in the current architectural context. Please mark the S4502 hotspot as **Reviewed / Safe**.
