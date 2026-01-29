SONAR S5122 JUSTIFICATION: CORS origin configuration

## Summary

This project exposes several backend REST APIs behind the API Gateway. Cross-Origin Resource Sharing (CORS) is required to let browser-based frontends access these APIs. Sonar rule S5122 flags the use of permissive origin patterns (e.g. wildcard "\*") as a security hotspot.

## Why this configuration is acceptable

- Default behavior is conservative: the gateway reads the property `cors.allowed-origins` and defaults to `http://localhost,http://127.0.0.1` for development. No wildcard is used by default.
- Wildcard origin patterns are allowed only when **explicitly** configured by operators (set to `*`), and in that case a warning is logged at startup so reviewers and operators are aware of the risk.
- The gateway is not exposing public web pages that require cookies-based authentication: our APIs use **stateless JWT** bearer tokens. State-changing operations still require authentication and proper authorization checks, so browser-origin alone cannot bypass server-side protections.

## Reviewer checklist (S5122)

1. Confirm that `cors.allowed-origins` is set to a **restricted** list of origins in production (e.g. `https://app.example.com`), not `*`.
2. If `*` is configured, confirm this is intentional and acceptable for the deployment context and that compensating controls exist (WAF, API gateway rules, additional origin checks in business endpoints, etc.).
3. Verify the application logs include the startup warning when `*` is used.
4. Approve and mark the S5122 hotspot as **Reviewed** in Sonar if the above checks are satisfied.

## Notes

- See `api-gateway/src/main/java/com/safezone/gateway/config/CorsConfig.java` for the implementation and comments.
- If you want a stricter operational guard, consider rejecting startup when `cors.allowed-origins` is `*` in non-dev environments, or add an environment variable to explicitly opt-in for wildcard usage.

Thank you for reviewing — please mark the hotspot S5122 as Reviewed after validation.
