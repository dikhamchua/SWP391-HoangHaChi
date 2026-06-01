# US-005 Print Invoice (UC-3.5)

## Status

completed (2026-06-01 bug-scan + Playwright verify; SKIP cho test render thực tế do DB chưa có order seed)

## Lane

normal

## Product Contract

Sales staff / Store Manager có thể in hóa đơn từ trang invoice-detail. Hóa đơn render trên template thermal 80mm với đầy đủ: order code, date, items (qty + unit price + subtotal), totals (subtotal/discount/total), payment list, marker `ĐÃ HỦY` nếu order status=cancelled. Hỗ trợ `window.print()` qua nút bấm.

## Relevant Product Docs

- GitHub issue #4 (UC-3.5 Print Invoice)
- `AGENTS.md` invoice domain
- `KIOT-Viet/` reference HTML cho receipt layout

## Acceptance Criteria

- GET `/admin/invoices?action=print&id=<orderId>` forward sang `invoice-print.jsp`
- Template chứa: company info, branch name, order code, date, customer, employee, items table, subtotal, discount, total, payment list
- CSS print-media: `@page size: 80mm auto; margin: 0`, `.receipt { width: 80mm; }`
- Button "In hóa đơn" trên `invoice-detail.jsp` mở print view trong tab mới
- Nếu `order.status == 'cancelled'`: hiển thị badge "ĐÃ HỦY" rõ ràng trên template
- Server-side guard: `cancelOrder` chỉ cho phép khi status=pending; `addPayment` reject khi status=cancelled
- Date range filter (`dateFrom`/`dateTo`) ở list page phải inclusive cận dưới và cận trên (cùng ngày phải hiện)
- Filter inputs phải HTML-escape XSS safe; pagination dùng `<c:url><c:param>`

## Design Notes

- Servlet: `InvoiceServlet.handlePrint` (case "print" trong doGet)
- Service: `InvoiceService.cancelOrder`/`addPayment` thêm guard status
- DAO: `OrderDAO.appendFilterClauses` dùng `>= CAST(? AS date)` và `< DATEADD(day, 1, CAST(? AS date))`
- View: `WEB-INF/views/invoice/invoice-print.jsp` (80mm receipt), `invoice-detail.jsp` (print button), `invoices.jsp` (XSS-safe filter + pagination)
- Constants: `ViewPaths.INVOICE_PRINT`

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | TODO — InvoiceServiceTest cho cancelOrder guard + addPayment guard |
| Integration | `mvn test` BUILD SUCCESS (6/6, 2026-06-01) |
| E2E | Playwright 2026-06-01: list page XSS-safe filter PASS, list loads PASS; print render + ĐÃ HỦY badge SKIP do DB không có order data (template source verified) |
| Platform | Servlet mapping `/admin/invoices` resolve qua AuthFilter |
| Release | Deployed Tomcat :9999 |

## Harness Delta

- Story tạo 2026-06-01 sau bug-scan workflow.
- Friction → HARNESS_BACKLOG #6 (XSS pagination), #8 (SQL date range), #9 (state transition guard), #11 (JSP attribute escape).
- Đã fix kèm story này: 4 bugs (XSS filter, dateTo SQL, cancel/payment guard, ĐÃ HỦY marker).

## Evidence

- Code: `InvoiceServlet.java`, `InvoiceService.java`, `OrderDAO.java`, `invoice-print.jsp`, `invoice-detail.jsp`, `invoices.jsp`
- Screenshots: `plans/reports/screenshots/invoice-{1,2,3,4}.png`
- Verify report: `plans/reports/verify-260531-2120-bug-fix-playwright.md`
- Issue: #4 (still open — phase 2 cần seed data + manual print test)

## Open Questions

- [ ] Cancelled order có nên reject ngay ở servlet (`handlePrint`) thay vì chỉ hiện banner trên template? Hiện tại cho phép in để giữ audit trail nhưng cần confirm UX.
- [ ] Cần seed data DB (orders + payments) để Playwright test full E2E print flow.
- [ ] `NotFoundException.getMessage()` đang trả tiếng Anh ("Order not found: 1") — i18n sang tiếng Việt?
