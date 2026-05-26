# Implemented Features

## Authentication And User Flow

- Login servlet and JSP.
- Logout servlet.
- Registration servlet and JSP.
- Forgot-password and reset-password servlet/JSP flow.
- Role-selection servlet and JSP.

## Authorization

- `AuthFilter` protects `/admin/*` and `/pos/*` URL patterns.
- `RolePermissionUtil` defines role-based checks for category management, role management visibility, management-area access, and POS access.
- Login flow stores role and permission flags in session attributes.

## Product Management

- Admin product page exists.
- Product servlet supports listing, searching, viewing, adding, updating, and soft delete routing.
- Product DAO supports listing with pagination, lookup, search, category filtering, add, update, stock update, and soft delete.
- JSON product listing action exists under API routing.

## Category Management

- Admin category page exists.
- Category servlet supports list, add, and update flows.
- Category DAO supports filters, pagination, count, lookup, add, update, existence checks, duplicate-name checks, parent lookup, and descendant checks.
- Category flow includes role checks for view/manage permissions.

## Infrastructure

- UTF-8 encoding filter is configured.
- SQL Server JDBC dependency is configured.
- Gson dependency is configured for JSON serialization.
- JSTL and Jakarta Servlet/JSP dependencies are configured.
