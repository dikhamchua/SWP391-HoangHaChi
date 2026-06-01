# US-004 Employee Admin CRUD

## Status

completed

## Lane

normal

## Product Contract

Owner/Admin có thể list/create/edit/soft-delete nhân viên tại `/admin/employees`. Mỗi employee có fullName, email, phone, password (hashed), roleId, branchId, status. Validate role/branch tồn tại, email unique.

## Relevant Product Docs

- `AGENTS.md` - Architecture Contract (employee domain)
- GitHub issue #1 (UC-1.5 Roles & Permissions) — Employee CRUD done; Role management chưa

## Acceptance Criteria

- GET `/admin/employees` list paginated with role + branch joined
- POST `?action=add` create employee với password hash (BCrypt), validate role + branch tồn tại
- POST `?action=update` update fullName/email/phone/role/branch/status — validate role + branch tồn tại (fix #23)
- POST `?action=delete` soft-delete (Status=inactive)
- Email unique (qua `existsByEmail(email, excludeId)`)
- Validation messages tiếng Việt qua ErrorMessages constants (fix #19)

## Design Notes

- Servlet: `com.kiotretail.employee.controller.EmployeeServlet`
- Service: `com.kiotretail.employee.service.EmployeeService`
- DAO: `com.kiotretail.employee.dao.{EmployeeDAO,RoleDAO,BranchDAO}`
- Model: `com.kiotretail.employee.model.{Employee,Role,Branch}`
- View: `WEB-INF/views/employee/employees.jsp`, `employee-create.jsp`, `employee-edit.jsp`, `employee-detail.jsp`

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | TODO — viết EmployeeServiceTest |
| Integration | Compile passes |
| E2E | Playwright: GET /admin/employees sau login owner → 200 OK |
| Platform | Servlet mapping resolve, BCrypt password hash hoạt động trong AuthService |
| Release | Deployed Tomcat :9999 |

## Harness Delta

- Story này tạo retroactively. Quality fix kèm theo: #18 (rethrow SQLException), #19 (i18n messages), #23 (validate role/branch trong update)

## Evidence

- Code: `src/main/java/com/kiotretail/employee/{controller,service,dao,model}/Employee*.java`
- Issues fix: #18, #19, #23 (all closed); #1 vẫn open (Role management chưa làm)

## Open Questions

- [ ] Role management UI khi nào làm? Cần story riêng (high-risk lane vì Authorization hard gate)
- [ ] EmployeeService.deleteEmployee có nên check Order chưa hoàn thành trước khi soft-delete không?
