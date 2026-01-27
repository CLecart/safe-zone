Fix Sonar hotspots S4502 (CSRF) and S6702 (secrets):

- Replace global .csrf(AbstractHttpConfigurer::disable) with selective
  .csrf(...).ignoringRequestMatchers('/api/\*\*') plus S4502 justification comments
- Remove hard-coded JWT secrets; make jwt.secret optional and generate a secure runtime key for tests/local runs; CI sets JWT_TEST_SECRET
- Disallow CORS credentials and add property-driven allowed origins ('cors.allowed-origins') with S5122 justification; prevent wildcard origins without justification
- Add SecurityPolicyTest to enforce the rules and detect regressions

Build & tests: full reactor build and module tests passed locally.

---

## Sonar review checklist (quick actions for reviewers)

Please review the Security Hotspots reported for this PR (S4502 - CSRF). For each hotspot below, confirm the rationale and mark it as **Reviewed** in SonarCloud (Security Hotspots UI).

Files to review:

- `order-service/src/main/java/com/safezone/order/config/SecurityConfig.java` — CSRF ignored for `/api/**` with S4502 justification (stateless JWT, no cookies).
- `product-service/src/main/java/com/safezone/product/config/SecurityConfig.java` — CSRF ignored for `/api/**` with S4502 justification (stateless JWT, no cookies).
- `user-service/src/main/java/com/safezone/user/config/SecurityConfig.java` — CSRF ignored for `/api/**` with S4502 justification (stateless JWT, no cookies).

Review notes (what to check):

- Authentication is via JWT in `Authorization` header (no cookies / no server sessions).
- API Gateway sets `corsConfig.setAllowCredentials(false)` (no cross-origin cookies).
- If any change introduces cookies/sessions or enables credentials, remove the CSRF exception immediately and re-enable CSRF protections.

Documentation: see `docs/CSRF_JUSTIFICATION.md` for consolidated rationale and reviewer instructions.

If you confirm safety, please mark the hotspot as **Reviewed** in SonarCloud so the PR can proceed.
