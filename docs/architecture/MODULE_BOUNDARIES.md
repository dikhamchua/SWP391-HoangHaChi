# Module Boundaries

## Current Modules

| Module | Source Areas | Responsibility | Protection Level |
| --- | --- | --- | --- |
| Authentication | `LoginServlet`, `LogoutServlet`, `RegisterServlet`, `ForgotPasswordServlet`, auth JSPs | Login, registration, reset-password, logout | Protected |
| Authorization | `RoleSelectionServlet`, `RolePermissionUtil`, `AuthFilter`, role session attributes | Role checks and protected navigation | Protected |
| Product Management | `ProductServlet`, `ProductDAO`, `Product`, `products.jsp`, product API action | Product listing, search, CRUD, API listing | Normal |
| Category Management | `CategoryServlet`, `CategoryDAO`, `Category`, `categories.jsp` | Category tree/listing, validation, CRUD | Normal with authorization dependency |
| Dashboard | `DashboardServlet`, `dashboard.jsp` | Admin landing/dashboard page | Normal |
| API | `BaseController`, `ApiAction`, `ApiResponse`, `api/action`, `api/dto` | JSON endpoint routing and response generation | Protected infrastructure |
| Persistence | DAOs, `DatabaseUtil`, `sql` | JDBC access and database schema | Protected |
| Presentation | JSPs, CSS, JS | Server-rendered pages and client behavior | Normal, except auth views |

## Boundary Rules

- A module may own its servlet, DAO, model, JSP, and API action when applicable.
- Cross-module data access must go through DAOs or future service methods, not direct table access from unrelated controllers.
- Authorization checks must stay centralized through `RolePermissionUtil` or a future dedicated authorization component.
- Module-specific validation should remain near the controller until it becomes shared or complex enough to justify a service.
- Schema changes must be reflected in DAOs and database documentation in the same change.

## Protected Module Change Process

Before changing a protected module:

1. Identify all direct imports and route mappings.
2. Identify all JSP/session attributes used by the flow.
3. Identify database tables and columns touched by the change.
4. Describe risks in the plan or implementation summary.
5. Make the smallest possible source change.
6. Run relevant verification or document why it could not run.

## Current Boundary Risks

- Authentication and password storage rules are inconsistent and must be fixed before production use.
- Database schema naming does not fully match all DAO SQL statements.
- Some controllers parse and validate many raw request parameters directly.
- API DTO package contains names that overlap domain model names, requiring careful imports.
