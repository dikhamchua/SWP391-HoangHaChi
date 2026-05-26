# Security Rules

## Current Security Model

- `AuthFilter` requires an `employee` session attribute for `/admin/*` and `/pos/*`.
- `LoginServlet` creates session state after successful employee lookup.
- `RolePermissionUtil` centralizes role permission checks.
- `web.xml` marks cookies as HTTP-only and sets a 30-minute default session timeout.

## Mandatory Rules

- Do not log credentials or secrets.
- Do not hardcode production credentials.
- Do not store raw passwords.
- Do not compare raw passwords once password hashing is standardized.
- Do not bypass `RolePermissionUtil` for role-sensitive checks.
- Do not expose JSPs directly outside `WEB-INF/views`.
- Do not add state-changing GET endpoints.
- Validate all request parameters before persistence calls.

## Known Security Gaps

- Database credentials are hardcoded.
- Login debug logging exposes email and password.
- Password hashing is inconsistent.
- CSRF protection is not documented or implemented for forms.
- Cookie `secure` is disabled for local development and must be enabled for HTTPS production.
- Error handling may expose stack traces in logs without structured sanitization.

## Security Refactor Priority

1. Remove sensitive login debug logging.
2. Externalize database credentials.
3. Standardize password hashing and migration strategy.
4. Add CSRF protection to POST forms.
5. Review role coverage for all admin and POS features.
6. Add audit logging for authentication, authorization denial, finance, inventory, and payment operations.
