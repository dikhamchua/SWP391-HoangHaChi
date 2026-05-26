# Module Index

This directory tracks module ownership and boundaries.

## Current Modules

| Module | Primary Files | Status |
| --- | --- | --- |
| Auth | `controller/*Login*`, `RegisterServlet`, `ForgotPasswordServlet`, auth JSPs | Implemented, security debt |
| Authorization | `AuthFilter`, `RolePermissionUtil`, `RoleSelectionServlet` | Implemented, protected |
| Category | `CategoryServlet`, `CategoryDAO`, `Category`, `categories.jsp` | Implemented |
| Product | `ProductServlet`, `ProductDAO`, `Product`, `products.jsp`, `GetProductsAction` | Implemented |
| API | `BaseController`, `ApiAction`, `ApiResponse` | Initial implementation |
| Database | `DatabaseUtil`, `sql` | Implemented, needs hardening |

Create a dedicated module file here when a module requires ownership notes, invariants, or workflow details.
