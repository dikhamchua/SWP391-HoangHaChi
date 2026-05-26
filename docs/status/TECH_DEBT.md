# Technical Debt

## Critical

- Database credentials are committed in `DatabaseUtil` and `web.xml`.
- Login flow logs raw credentials in debug output.
- Password handling is inconsistent between login, registration, and reset-password flows.

## High

- DAO SQL naming is inconsistent with the active SQL Server schema in some employee methods.
- Error handling uses `printStackTrace` and direct console output.
- There is no migration/versioning system for database changes.
- No CSRF protection is documented for POST forms.
- Session cookie `secure` is disabled in `web.xml`, which must not be used for HTTPS production deployments.

## Medium

- Controllers contain repeated parameter parsing, validation, and flash-message logic.
- API pagination parsing is currently action-local and should be standardized when more endpoints are added.
- API response content type contains extra spacing in `charset= UTF-8`.
- API DTO and domain model class names overlap, which can cause import confusion.
- Some imports appear unused in API action code.

## Low

- Root README references documentation files that are not currently present in the repository.
- Some README feature descriptions are aspirational compared with implemented source code.
- Build compiler properties and plugin source/target values should be reconciled with the documented JDK runtime.
