# Modular Monolith Refactor Plan

## Status: In Progress
## Architecture: Modular Monolith (domain-based packages, single WAR)

## Domains (by priority)

| # | Domain | New Files | Moved Files | JSP Views |
|---|--------|-----------|-------------|-----------|
| 1 | shared | 15 | 6 | 10 |
| 2 | product | 16 | 8 | 9 |
| 3 | customer | 11 | 0 | 4 |
| 4 | employee | 14 | 8 | 6 |
| 5 | invoice | 15 | 0 | 4 |
| 6 | pos | 9 | 0 | 1 |
| 7 | report | 15 | 2 | 5 |
| 8 | api | 16 | 5 | 0 |

## Target Package Structure

```
com.kiotretail/
├── shared/              # Cross-cutting: DB, filters, base classes, exceptions
│   ├── config/
│   ├── filter/
│   ├── util/
│   ├── base/
│   └── exception/
├── product/             # Product + Category + Supplier + Warehouse
│   ├── controller/
│   ├── service/
│   ├── dao/
│   ├── model/
│   └── dto/
├── customer/            # Customer + CustomerGroup
│   ├── controller/
│   ├── service/
│   ├── dao/
│   ├── model/
│   └── dto/
├── employee/            # Employee + Auth + Role + Branch
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── model/
├── invoice/             # Invoice/Order + OrderDetail + Payment
│   ├── controller/
│   ├── service/
│   ├── dao/
│   ├── model/
│   └── dto/
├── pos/                 # POS sale (session-based cart)
│   ├── controller/
│   ├── service/
│   └── dto/
├── report/              # Dashboard + Reports
│   ├── controller/
│   ├── service/
│   └── dto/
└── api/                 # REST API layer
    ├── product/
    ├── customer/
    ├── invoice/
    └── dashboard/
```

## Migration Phases

### Phase 0
Phase 0 - Branch + safety net. Create branch refactor/modular-monolith. Run mvn clean package on main first to capture a working baseline. Snapshot DBFinora schema. Add a smoke test list (login + product list + category list + dashboard) that must pass after every phase.

### Phase 1
Phase 1 - Shared foundation (priority 1). Create com.kiotretail.shared.{config,filter,util,base,exception}. Move DatabaseUtil, EmailUtil, RolePermissionUtil, TestConnection -> shared.util. Move AuthFilter, EncodingFilter -> shared.filter. Add PasswordUtil (BCrypt jBCrypt 0.4 in pom.xml), CodeGenerator, SessionUtil, JsonUtil, CurrencyUtil, DateUtil. Add BaseServlet (common forward/redirect/error helpers), BaseService (validation + transaction helper using a single Connection), BaseDAO (connection acquire/release), Pagination, PageResult, ServiceException, ValidationException, NotFoundException. Update web.xml filter-class FQNs. Run mvn clean package; smoke test login + dashboard.

### Phase 2
Phase 2 - Product domain (priority 2). Move ProductServlet, CategoryServlet -> product.controller. Move ProductDAO, CategoryDAO -> product.dao. Move Product, Category models -> product.model. Add ProductService, CategoryService, SupplierService, InventoryService and route servlets through services. Fix ProductDAO.updateStock to mutate Warehouse.Quantity (via WarehouseDAO + WarehouseTransaction audit) instead of StockAlertQty. Add SupplierDAO/WarehouseDAO/WarehouseTransactionDAO + Supplier/Warehouse/WarehouseTransaction models. Move existing JSPs to views/product/. Convert KIOT-Viet/product-create.html, product-edit.html, product-detail.html, product-delete-confirm.html, product-import.html, product-export.html into matching JSPs that include common/topbar.jsp + common/navbar.jsp + common/admin-sidebar.jsp. Add SupplierServlet, ProductImportServlet, ProductExportServlet, ProductImageUploadServlet. Run mvn clean package; smoke test product list/create/edit.

### Phase 3
Phase 3 - Customer domain (priority 3). Create com.kiotretail.customer.{controller,service,dao,model,dto}. Add Customer, CustomerGroup models. Add CustomerDAO (PascalCase Customer table from DBFinora.sql) and CustomerGroupDAO. Add CustomerService, CustomerGroupService. Add CustomerServlet (/admin/customers with list/search/filter/create/edit/delete), CustomerImportServlet, CustomerExportServlet. Convert KIOT-Viet/pos-customer-management.html -> customer/customers.jsp; create customer-create/edit/detail.jsp using common fragments. Run mvn clean package; smoke test customer CRUD.

### Phase 4
Phase 4 - Employee domain (priority 4) - HIGH-RISK lane (auth + password). Per CLAUDE.md, draft a story under docs/stories/ before code. Move all auth servlets (Login, Logout, Register, ForgotPassword, ChangePassword, RoleSelection) -> employee.controller. Move EmployeeDAO -> employee.dao and REWRITE every CRUD method to match DBFinora PascalCase schema (Employee, Role, Branch); drop references to non-existent username/department/position columns. Extract login/register/password flows into AuthService; replace plaintext PasswordHash with BCrypt via PasswordUtil (one-time backfill script for the 123456 seeds documented in the story). Add RoleDAO, BranchDAO, AuditLogDAO + Role/Branch/AuditLog models. Add EmployeeService, RoleService, BranchService. Add EmployeeServlet, RolePermissionServlet, BranchServlet. Convert KIOT-Viet/pos-employee-management.html -> employee/employees.jsp + create/edit/detail/roles/branches JSPs. Wire AuditLogDAO into AuthService and CRUD services. Run mvn clean package; smoke test login (old hash + new hash), register, change-password, employee CRUD.

### Phase 5
Phase 5 - Invoice domain (priority 5). Add Invoice, OrderDetail, Payment models against existing Orders/OrderDetail/Payments tables. Add InvoiceDAO, OrderDetailDAO, PaymentDAO. Add InvoiceService (orchestrates Order + OrderDetail + Payment + WarehouseTransaction in a single shared.base.BaseService transaction), OrderDetailService, PaymentService. Add InvoiceServlet, InvoiceDetailServlet, PaymentServlet. Convert KIOT-Viet/pos-invoice-management.html -> invoice/invoices.jsp; create invoice-detail.jsp, invoice-create.jsp, invoice-print.jsp. Run mvn clean package; smoke test invoice list, filter, detail.

### Phase 6
Phase 6 - POS domain (priority 6) - resolves /pos/sale gap. Add CartItem, CartTab, CartSession DTOs (HttpSession-backed, multi-tab per pos-sale.html). Add CartService (add/remove/update qty/clear/switch tab/load draft) and POSService (search products, search customers, finalize checkout). Add POSServlet (/pos/sale GET renders pos/sale.jsp), POSCartServlet (/pos/cart for AJAX cart ops), POSCheckoutServlet (/pos/checkout consumes invoice.service.InvoiceService + product.service.InventoryService inside one transaction; emits FinanceTransaction record). Convert KIOT-Viet/pos-sale.html -> pos/sale.jsp. Run mvn clean package; end-to-end test: login -> role-selection?role=pos -> /pos/sale -> add items -> checkout -> Orders/OrderDetail/Payments rows + Warehouse.Quantity decremented + WarehouseTransaction audit row.

### Phase 7
Phase 7 - Report domain (priority 7). Move DashboardServlet -> report.controller. Move admin/dashboard.jsp -> report/dashboard.jsp and refactor to KIOT-Viet/pos-dashboard.html layout (revenue card, bar chart, top-products, top-customers, employee revenue, timekeeping, recent activities, login anomaly banner). Add DashboardService, SalesReportService backed by ReportDAO (aggregations over Orders + OrderDetail + Payments + Customer + Employee) and FinanceTransactionDAO. Add DashboardSummaryDTO, RevenueSeriesDTO, RevenuePointDTO, TopProductDTO, TopCustomerDTO, EmployeeRevenueDTO, RecentActivityDTO, SalesReportDTO. Add ReportServlet, SalesReportServlet. Convert KIOT-Viet/pos-report-sales.html -> report/sales-report.jsp. Run mvn clean package; smoke test dashboard renders real data, sales-report filters work.

### Phase 8
Phase 8 - API layer expansion (priority 8). Reorganize com.kiotretail.api into api.product.{action,dto}, api.customer.{action,dto}, api.invoice.{action,dto}, api.dashboard.{action,dto}. Move GetProductsAction + Product/Category/Variant/ProductListResponse DTOs into api.product. Make BaseController dispatch by HTTP method (doGet/doPost/doPut/doDelete) instead of always service(). Add Get/Create/Update/Delete actions for Product, Customer, Invoice, plus dashboard endpoints. Have actions delegate to domain services (no DAO calls from actions). Extend AuthFilter url-pattern to include /api/* so the REST surface is no longer wide open. Smoke test GET /api/products still works and new endpoints return ApiResponse JSON.

### Phase 9
Phase 9 - Common JSP layout extraction. Carve KIOT-Viet topbar (logo + delivery + lang VI + bell + help + settings + avatar + Bán hàng CTA), navbar (10 module tabs with active state), admin-sidebar (per-domain side menus), pagination control, footer-bar (hotline 1900 6522) into common/*.jsp fragments. Replace inline duplications across product/, customer/, employee/, invoice/, report/ JSPs with <jsp:include>. Add a JSP attribute or request var (activeTab) to drive navbar active state per page.

### Phase 10
Phase 10 - web.xml + URL backward compatibility. Repoint every existing <servlet-class> to its new FQN (com.kiotretail.{domain}.controller.X). Keep every existing <url-pattern> intact (/login, /logout, /register, /forgot-password, /change-password, /role-selection, /admin/dashboard, /admin/products, /admin/categories) - only the class FQN changes, so all current bookmarks/links keep working. Add new mappings for /pos/sale, /admin/customers, /admin/employees, /admin/invoices, /admin/reports, /admin/suppliers, /admin/branches, /admin/roles. Remove the TODO comment block. Update <filter-class> FQNs to shared.filter.*. Add /api/* to AuthFilter url-pattern. Update <error-page> JSP locations only if files moved.

### Phase 11
Phase 11 - Cleanup + verification. Delete now-empty old packages (com.kiotretail.controller, com.kiotretail.dao, com.kiotretail.model, com.kiotretail.filter, com.kiotretail.util) and views/admin/. Run mvn clean package and the full smoke-test list. Update docs/ARCHITECTURE.md with the new layout. Per CLAUDE.md Done Definition: state what changed, run validation commands, capture friction in docs/HARNESS_BACKLOG.md if any.

## Critical Gaps Found

- POSServlet -> /pos/sale (referenced by RoleSelectionServlet redirect AND by AuthFilter pattern /pos/*; no class exists, no JSP serving the actual POS sale screen). Highest-priority gap for a 'POS' system.
- InvoiceServlet -> /admin/invoices or /pos/checkout (TODO in web.xml). No Orders/OrderDetail/Payments DAO or controller exists. Cannot create, list, refund, or print sale invoices.
- CustomerServlet -> /admin/customers (TODO). Customer table is seeded but unreachable from UI; no CustomerDAO, no Customer model class.
- EmployeeServlet -> /admin/employees (TODO). EmployeeDAO has CRUD methods but they target non-existent lowercase tables (employees, roles, branches, employee_id, full_name, etc.) and reference removed columns username/department/position - all CRUD beyond login/register/password is broken.
- RolePermissionServlet -> /admin/roles (TODO). Role table exists with seed data, but no DAO, no model, and RolePermissionUtil only encodes hard-coded role-name checks (canViewCategory, canManageCategory, canAccessPos) - not data-driven permissions.
- ReportServlet -> /admin/reports (TODO). No revenue, profit, top-product, stock-aging, branch-comparison reports; FinanceTransaction table is unused.
- Supplier domain: Supplier table seeded, but no SupplierDAO, no Supplier model, no SupplierServlet. Required for purchase orders.
- Branch domain: Branch table seeded and referenced by login session, but no BranchDAO/Branch model/BranchServlet. Hardcoded selection in RegisterServlet.
- Warehouse / Inventory: Warehouse and WarehouseTransaction tables exist; ProductDAO.updateStock incorrectly mutates StockAlertQty instead of Warehouse.Quantity. No WarehouseDAO, no Warehouse model, no stock-in/stock-out workflow, no link from sales to stock decrement.
- StockTransfer: table exists, no DAO/model/servlet. Cross-branch transfers unsupported.
- Orders / OrderDetail / Payments: schema is in place for sales, returns, purchases; zero Java code touches them. POS cannot persist transactions.
- FinanceTransaction: schema present, no code. No revenue ledger.
- AuditLog: schema present, no code. AuthFilter and CRUD writes do not log audit entries despite EmployeeID FK suggesting it.
- EmployeeDAO inconsistencies: getAllEmployees / getEmployeeById / addEmployee / updateEmployee / deleteEmployee / checkUsernameExists / checkEmailExists / checkUsernameAndEmailMatch query a snake_case schema that does NOT match DBFinora.sql (PascalCase). These methods will throw SQLException at runtime. Needs full rewrite to match Employee(EmployeeID,FullName,Email,Phone,PasswordHash,RoleID,BranchID,Status,CreatedAt).
- Password handling: PasswordHash column stores plaintext. registerEmployee, login, updatePassword, updatePasswordByEmail all bypass hashing. CLAUDE.md flags auth as a high-risk lane.
- REST API: only GET /api/products is wired; api/dto/Variant exists but no Variant table or DAO; no /api/categories, /api/orders, /api/customers, /api/login endpoints.
- ApiResponse + BaseController do not enforce HTTP method (always service). No POST/PUT/DELETE handling, no auth check on /api/*.
- ProductServlet image/url field is in model but never read from request or stored - no image upload pipeline.
- Product variants: api.dto.Variant present but Product schema has no variant child table.
- JSP layout: dashboard, products, categories present; admin/employees.jsp, admin/customers.jsp, admin/suppliers.jsp, admin/branches.jsp, admin/warehouses.jsp, admin/orders.jsp, admin/reports.jsp, pos/sale.jsp do not exist (or at least nothing maps to them).
- AuthFilter only protects /admin/* and /pos/*; /api/* is wide open.

## Shared Package Purpose

Cross-cutting infrastructure consumed by every domain: DB access, filters, base classes for the new Controller -> Service -> DAO layering, password hashing (replaces the current plaintext PasswordHash flow), shared exceptions, pagination primitives, and reusable JSP fragments (topbar, navbar, sidebar, pagination, footer-bar) extracted from KIOT-Viet HTML so domain views stay thin.