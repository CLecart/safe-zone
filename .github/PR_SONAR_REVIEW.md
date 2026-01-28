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
