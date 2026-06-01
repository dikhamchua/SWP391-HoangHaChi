# US-003 Supplier Management CRUD (Phase 1)

## Status

completed (re-verified 2026-06-01: soft-delete + XSS fix shipped)

## Lane

normal

## Product Contract

Manager/Warehouse có thể list/create/edit/soft-delete nhà cung cấp tại `/admin/suppliers`. Mỗi supplier có name, phone, email, address, status. Phase 2-6 (Purchase Order, Receive Goods, Returns, Debt) chưa làm — issue #7 vẫn open.

## Relevant Product Docs

- `AGENTS.md` - Architecture Contract (product domain)
- GitHub issue #7 (UC-4.1~4.6 Supplier & Purchase) — chỉ Phase 1 hoàn thành

## Acceptance Criteria

- GET `/admin/suppliers` list paginated, search keyword (name/phone/email/address)
- POST `?action=add` validate name required, phone regex, email regex (max 100 ký tự), status whitelist
- POST `?action=update` update theo id
- POST `?action=delete` SOFT delete (Status=inactive) để tránh FK violation `Product.SupplierID`
- Name unique
- Navbar có link "Nhà cung cấp"
- doGet error path: redirect + flash danger toast (không forward để tránh NPE pageResult)

## Design Notes

- Servlet: `com.kiotretail.product.controller.SupplierServlet` extends BaseServlet
- Service: `com.kiotretail.product.service.SupplierService` — `deleteSupplier()` gọi `softDelete()`
- DAO: `com.kiotretail.product.dao.SupplierDAO`
- Model: `com.kiotretail.product.model.Supplier`
- View: `WEB-INF/views/supplier/suppliers.jsp` (pagination dùng `<c:url><c:param>`), `supplier-create.jsp`, `supplier-edit.jsp`, `supplier-detail.jsp`

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | TODO — viết SupplierServiceTest tương tự BranchServiceTest |
| Integration | `mvn test` BUILD SUCCESS (6/6 PASS, 2026-06-01) |
| E2E | Playwright 2026-06-01: list 3 rows OK, XSS payload escaped, view 999999 redirect + toast danger |
| Platform | Servlet mapping `/admin/suppliers` resolve qua AuthFilter |
| Release | Deployed Tomcat :9999 (touched web.xml hot-reload) |

## Harness Delta

- Story này tạo retroactively sau fix #14 #15. Friction record vào HARNESS_BACKLOG: "lane Normal nhưng story packet không được tạo lúc impl"
- Phase 2-6 vẫn pending → cần story riêng khi bắt đầu
- 2026-06-01: bug-scan workflow phát hiện thêm — XSS pagination, hard DELETE FK risk, validateSupplier null-template sai, phone/email không write back trim, missing email length check. Đã fix. Friction → HARNESS_BACKLOG #6, #7, #10, #11.

## Evidence

- Code: `src/main/java/com/kiotretail/product/{controller,service,dao,model}/Supplier*.java`
- Screenshot: `plans/reports/11-suppliers-fixed.png`, `plans/reports/screenshots/supplier-{1,2,3,flash-final}.png`
- Issue fix: #14, #15 (closed); #7 still open (chỉ Phase 1 done)
- Verify report: `plans/reports/verify-260531-2120-bug-fix-playwright.md`

## Open Questions

- [x] SupplierDAO.delete có cần soft-delete như Branch không? — **YES, đã fix 2026-06-01 sau khi bug-scan phát hiện FK risk với `Product.SupplierID`.**
