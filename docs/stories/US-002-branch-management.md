# US-002 Branch Management CRUD

## Status

completed (re-verified 2026-06-01 after bug-scan workflow)

## Lane

normal

## Product Contract

Admin/Owner có thể list/create/edit/soft-delete chi nhánh tại `/admin/branches`. Mỗi chi nhánh có name, address, phone, status. Validate phone regex, name không trùng, status whitelist.

## Relevant Product Docs

- `AGENTS.md` - Architecture Contract (employee domain)
- GitHub issue #2 (UC-1.6: Branch Management)

## Acceptance Criteria

- GET `/admin/branches` trả list paginated với search keyword (name/address/phone)
- POST `?action=add` tạo branch mới, validate name required + length ≤255, phone regex `^[0-9+\-\s]{6,30}$`
- POST `?action=update` update branch theo id
- POST `?action=delete` SOFT delete (Status=inactive) — tránh FK violation Employee.BranchID
- Tên branch unique (case-sensitive), check qua `existsByName(name, excludeId)`
- Navbar có link "Chi nhánh"
- Verify trên http://localhost:9999/kiotretail/admin/branches

## Design Notes

- Servlet: `com.kiotretail.employee.controller.BranchServlet` extends BaseServlet
- Service: `com.kiotretail.employee.service.BranchService` (validate + orchestration)
- DAO: `com.kiotretail.employee.dao.BranchDAO` (JDBC, soft-delete sau fix #17)
- Model: `com.kiotretail.employee.model.Branch`
- View: `WEB-INF/views/branch/branches.jsp`, `branch-create.jsp`, `branch-edit.jsp`, `branch-detail.jsp`

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | BranchServiceTest: 6 test cases validate-only (Pending mvn test execution) |
| Integration | Compile passes (javac exit 0); deployed WAR đã reload Tomcat :9999 |
| E2E | Playwright: GET /admin/branches sau login owner@retail.com → list 2 branches OK |
| Platform | Servlet mapping `/admin/branches` resolve qua AuthFilter |
| Release | Đã deploy lên Tomcat exploded webapp tại `target/kiotretail` |

## Harness Delta

- Bổ sung soft-delete pattern (decision: hard delete vi phạm FK Employee.BranchID — xem fix #17)
- Story này được tạo retroactively sau fix bugs #14 #15 #17 — friction record vào HARNESS_BACKLOG
- 2026-06-01: bug-scan workflow phát hiện thêm — XSS pagination keyword (#6), error path swallow message (forward → redirect+flash), validateBranch null-template sai. Đã fix. Friction → HARNESS_BACKLOG #6, #7, #11.

## Evidence

- Code: `src/main/java/com/kiotretail/employee/{controller,service,dao,model}/Branch*.java`
- Tests: `src/test/java/com/kiotretail/employee/service/BranchServiceTest.java` (#24)
- Screenshot: `plans/reports/10-branches-fixed.png`, `plans/reports/screenshots/branch-{1,2,3,flash-final}.png`
- Issues fix: #14, #15, #17 (closed)
- Verify report: `plans/reports/verify-260531-2120-bug-fix-playwright.md`
