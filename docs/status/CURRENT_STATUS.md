# Current Status

## Snapshot

- Application type: Java Servlet/JSP Maven WAR
- Database target: SQL Server database `DBFinora`
- Primary UI: JSP pages with Bootstrap-oriented assets
- Primary backend access: JDBC DAOs through `DatabaseUtil`
- Build output: `target/kiotretail.war`

## Implemented Source Areas

- Authentication pages and servlets exist for login, registration, forgot password, reset password, logout, and role selection.
- Admin dashboard page and servlet exist.
- Product management servlet, DAO, model, JSP, and API listing action exist.
- Category management servlet, DAO, model, and JSP exist.
- Authorization helper exists through `RolePermissionUtil`.
- Auth and encoding filters exist and are configured in `web.xml`.
- SQL scripts define broad retail domain schema including roles, branches, employees, products, categories, orders, payments, finance, warehouse, and related entities.

## Current Architecture State

The application is not yet production-hardened. It is structured enough for continued development but requires security and database consistency work before production use.

## Current High-Risk Items

- Hardcoded database credentials.
- Inconsistent password storage and verification behavior.
- Sensitive debug logging in login flow.
- DAO/schema naming mismatches in some employee methods.
- No visible automated test suite beyond the Maven JUnit dependency.
- No formal database migration tool.
