# Naming Conventions

## Java Packages

- Root package: `com.kiotretail`
- Servlet controllers: `com.kiotretail.controller`
- DAOs: `com.kiotretail.dao`
- Models: `com.kiotretail.model`
- Filters: `com.kiotretail.filter`
- Utilities: `com.kiotretail.util`
- API contracts/actions: `com.kiotretail.api`, `com.kiotretail.api.action`, `com.kiotretail.api.dto`

## Java Classes

- Servlet controllers end with `Servlet`, for example `ProductServlet`.
- DAO classes end with `DAO`, for example `CategoryDAO`.
- Domain models use entity names, for example `Employee`, `Product`, `Category`.
- Utility classes end with `Util`, for example `DatabaseUtil`.
- API actions use action names, for example `GetProductsAction`.

## Java Methods

- Query methods use `get`, `find`, `search`, `count`, or `exists` prefixes.
- Mutation methods use `add`, `update`, `delete`, `register`, or domain-specific verbs.
- Result-set mapping helpers use `extract<Entity>`.
- Request parsing helpers should use explicit names such as `buildCategoryFromRequest` or `normalizeStatus`.

## Web Routes

- Page routes are lowercase and hyphenated where needed, for example `/role-selection`.
- Admin routes are grouped under `/admin`.
- API routes are grouped under `/api`.
- Authentication routes use simple root paths such as `/login`, `/logout`, `/register`, `/forgot-password`.

## JSP Files

- JSP files use lowercase kebab-case where names contain multiple words, for example `role-selection.jsp`.
- Shared JSP fragments live under `WEB-INF/views/common`.
- Auth JSPs live under `WEB-INF/views/auth`.
- Admin JSPs live under `WEB-INF/views/admin`.

## Database Naming

The primary active SQL script uses PascalCase singular table and column names such as `Employee`, `Product`, `Category`, `EmployeeID`, and `CreatedAt`.

Current DAO code contains mixed naming in some methods. New code must align with the active SQL Server schema unless a migration plan explicitly changes the convention.

## Documentation Naming

- Governance files use uppercase snake case, for example `SYSTEM_ARCHITECTURE.md`.
- Feature plan files use deterministic uppercase names under topic folders, for example `docs/planning/invoice/INVOICE_IMPLEMENTATION_PLAN.md`.
- Architecture decision records should use `YYYY-MM-DD-short-title.md` under `docs/decisions`.
