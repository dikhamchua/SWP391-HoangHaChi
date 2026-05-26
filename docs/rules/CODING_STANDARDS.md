# Coding Standards

## Java Standards

- Use Java package `com.kiotretail` and existing subpackages unless a documented boundary requires a new package.
- Keep servlet methods focused on request handling and delegation.
- Keep DAO methods focused on a single persistence operation.
- Use `PreparedStatement` for all SQL with user input.
- Use try-with-resources for `Connection`, `PreparedStatement`, `Statement`, and `ResultSet`.
- Avoid `System.out.println` for production diagnostics in new code.
- Prefer clear, direct code over premature abstractions.
- Do not swallow exceptions silently. If no logging framework exists, return safe failure values and plan logging improvement.

## JSP Standards

- JSPs render data passed by controllers.
- Do not execute SQL or instantiate DAOs in JSPs.
- Keep shared layout fragments under `WEB-INF/views/common`.
- Use existing CSS assets before creating new style files.
- Preserve UTF-8 handling for Vietnamese text.

## DAO Standards

- DAO classes own SQL strings, parameter binding, and result mapping.
- Private `extract<Entity>` methods are the preferred mapping pattern.
- Do not return raw `ResultSet` or JDBC objects outside DAOs.
- Avoid dynamic SQL unless it is parameterized and narrowly scoped.
- Keep table/column naming aligned with the active SQL Server schema.

## Controller Standards

- Validate request parameters before calling DAOs.
- Use redirects after successful POST operations.
- Use forwards for initial page rendering and validation failure when preserving request attributes.
- Use session flash messages consistently through `message` and `messageType` until a shared helper is introduced.
- Do not put database access or multi-table business transactions in controllers.

## API Standards

- API actions implement `ApiAction`.
- API routes are registered in `BaseController` until routing is refactored.
- API responses should use `ApiResponse` for status, message, and data shape.
- Keep API DTOs under `com.kiotretail.api.dto` when response shape differs from domain models.

## Security Standards

- Do not hardcode secrets in new code.
- Never log passwords, reset tokens, session identifiers, or credentials.
- Use centralized password hashing behavior before production deployment.
- Protect admin, POS, payment, finance, and database administration routes.
