# Protected Modules

Protected modules require extra analysis before changes because defects can compromise authentication, authorization, data integrity, or system-wide behavior.

## Protected Areas

| Area | Files/Paths | Why Protected |
| --- | --- | --- |
| Authentication | `LoginServlet`, `LogoutServlet`, `RegisterServlet`, `ForgotPasswordServlet`, auth JSPs | Controls user identity and session entry/exit |
| Authorization | `AuthFilter`, `RoleSelectionServlet`, `RolePermissionUtil`, role session attributes | Controls access to admin/POS/management behavior |
| Database connection | `DatabaseUtil`, `web.xml` DB context params | Affects every DAO and all persistence behavior |
| Database schema | `sql/*.sql` | Affects data integrity and DAO compatibility |
| API routing | `BaseController`, `ApiAction`, `ApiResponse` | Affects all JSON endpoints under `/api/*` |
| Payment/finance | `Payments`, `FinanceTransaction`, `Orders`, future payment modules | Financial correctness and auditability |
| Shared filters | `EncodingFilter`, `AuthFilter` | Affects every or protected request path |
| Build/deploy config | `pom.xml`, `context.xml`, `web.xml` | Affects runtime compatibility and deployment |

## Change Checklist

Before modifying protected modules:

1. Read all directly related files.
2. Identify route mappings and filters.
3. Identify session attributes and JSP dependencies.
4. Identify database tables and columns affected.
5. Identify rollback and compatibility risks.
6. Make the minimal change.
7. Run build/tests or document why not possible.
8. Update relevant docs.

## Current Protected Risks

- Hardcoded database credentials exist in source/config.
- Password handling is inconsistent and not production-safe.
- Login debug output exposes sensitive credentials.
- Session cookie `secure` is currently `false`, acceptable only for local HTTP development.
