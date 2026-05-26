# System Architecture

## Current System

KiotRetail is a Maven-built Java WAR application using a traditional server-rendered MVC structure.

The current application surface includes:

- Servlet controllers in `src/main/java/com/kiotretail/controller`
- JDBC DAOs in `src/main/java/com/kiotretail/dao`
- Domain models in `src/main/java/com/kiotretail/model`
- Servlet filters in `src/main/java/com/kiotretail/filter`
- Shared utility classes in `src/main/java/com/kiotretail/util`
- JSON API routing and actions in `src/main/java/com/kiotretail/api`
- JSP views in `src/main/webapp/WEB-INF/views`
- Static assets in `src/main/webapp/assets`
- SQL Server schema scripts in `sql`

## Runtime Flow

1. Browser requests enter Tomcat under the `/kiotretail` context.
2. `EncodingFilter` applies UTF-8 handling.
3. `AuthFilter` protects configured management and POS URL spaces.
4. `web.xml` servlet mappings route page requests to servlet controllers.
5. Controllers parse request parameters, call DAOs, set request/session attributes, and forward or redirect.
6. DAOs use `DatabaseUtil.getConnection()` and JDBC prepared statements to query SQL Server.
7. JSP views under `WEB-INF/views` render server-side HTML using request/session state.
8. JSON API requests under `/api/*` are routed by `BaseController` to `ApiAction` implementations.

## Architectural Layers

| Layer | Package/Path | Responsibility |
| --- | --- | --- |
| Web entry | `web.xml`, servlet annotations | URL mapping, filters, session policy |
| Filters | `com.kiotretail.filter` | Cross-cutting request concerns |
| Controllers | `com.kiotretail.controller` | Request flow, validation, forwarding, redirects |
| API | `com.kiotretail.api` | JSON route dispatch and API response formatting |
| DAO | `com.kiotretail.dao` | SQL, JDBC, result-set mapping, persistence operations |
| Model | `com.kiotretail.model` | Domain data carriers |
| Utility | `com.kiotretail.util` | Focused shared helpers for database, passwords, permissions |
| View | `WEB-INF/views` | JSP rendering |
| Assets | `assets` | CSS and browser JavaScript |
| Database | `sql` | Schema and seed scripts |

## Current Patterns

- MVC servlet + JSP pages for admin/auth UI.
- DAO classes own SQL and map `ResultSet` values into model objects.
- Session attributes carry authenticated employee and role permissions.
- Flash messages use session attributes `message` and `messageType` before redirect.
- API routes use a `Map<String, ApiAction>` inside `BaseController`.
- Soft delete is represented through `Status` updates for selected entities.

## Known Architecture Risks

- Database credentials are currently hardcoded in `DatabaseUtil` and repeated in `web.xml`.
- SQL naming is inconsistent across current DAO methods and schema scripts, mixing uppercase singular table names such as `Employee` with lowercase plural names such as `employees`.
- Password handling is inconsistent: some flows compare or store raw passwords while reset flow hashes via `PasswordUtil`.
- Controllers currently contain validation and mapping logic that may grow large as features expand.
- API and JSP flows currently share DAOs directly without a service layer.
- Error handling is mostly `printStackTrace`, which is not production-grade.

## Evolution Direction

Do not introduce a service layer speculatively. Add service classes only when a business workflow is reused by multiple controllers/API actions or needs transaction boundaries across multiple DAOs.

The preferred evolution path is:

1. Stabilize schema naming and credential configuration.
2. Standardize authentication/password behavior.
3. Extract repeated request parsing and validation only after repeated patterns are proven.
4. Introduce service-layer boundaries for multi-step business workflows such as checkout, inventory movement, payments, and reporting.
5. Add tests around protected modules before refactoring them.
