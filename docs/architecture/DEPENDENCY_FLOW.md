# Dependency Flow

## Allowed Direction

Dependencies must flow inward from web entry points toward persistence helpers.

```text
JSP views
  ↑ request attributes/session attributes
Controllers / API actions
  → DAOs
  → Models
  → Utilities
DAOs
  → DatabaseUtil
  → Models
DatabaseUtil
  → JDBC driver
```

## Allowed Dependencies

| From | May Depend On |
| --- | --- |
| `controller` | `dao`, `model`, focused `util`, servlet APIs |
| `api` | `api.action`, `ApiAction`, `ApiResponse`, Gson, servlet APIs |
| `api.action` | `dao`, `model` or `api.dto`, servlet request, `ApiResponse` |
| `dao` | `model`, `DatabaseUtil`, JDBC APIs |
| `filter` | servlet APIs, focused auth/session utilities only |
| `model` | Java standard library only |
| `util` | Java/Jakarta/JDBC libraries as needed for focused cross-cutting helpers |
| `JSP` | request/session attributes, JSTL/taglibs, static assets |

## Disallowed Dependencies

- DAO classes must not depend on servlet request, response, session, JSP APIs, or controller classes.
- Model classes must not depend on DAOs, controllers, servlet APIs, or database utilities.
- JSP files must not open database connections or execute SQL.
- Controllers must not contain raw JDBC calls.
- Filters must not contain business workflow logic.
- Utility classes must not become hidden service layers.

## Protected Dependency Rules

- Authentication flow depends on `EmployeeDAO`, session attributes, and `RolePermissionUtil`. Changes must preserve login, logout, role selection, and protected route behavior.
- Database access depends on `DatabaseUtil`. Credential or connection changes affect every DAO.
- API routing depends on exact path keys in `BaseController`. New API routes must be registered deterministically and documented in `docs/api/API_STANDARDS.md`.

## Service Layer Policy

There is no broad service layer today. Do not add one for a single CRUD controller.

Introduce `com.kiotretail.service` only when at least one condition is true:

- A workflow spans multiple DAOs.
- Logic must be reused by JSP controllers and JSON API actions.
- A transaction boundary must cover multiple persistence operations.
- Complex domain validation no longer belongs in a servlet.
