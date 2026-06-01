# Verify Bug Fixes — Playwright E2E

Date: 2026-05-31 21:38 +07
Stack: Tomcat (port 9999) + Playwright MCP, owner@retail.com session

## Scope
3 GitHub Issues: #2 UC-1.6 Branch, #7 UC-4.1 Supplier, #4 UC-3.5 Print Invoice

## Result Matrix

| Module | Test | Status | Note |
|---|---|---|---|
| Branch | Pagination XSS | PASS | Payload `"><script>` escaped, 0 console errors |
| Branch | List loads | PASS | Table + Tạo mới button OK |
| Branch | View id=999999 → flash error | PASS (after re-fix) | URL stripped, toast `showToast(...,'danger')` |
| Supplier | Pagination XSS | PASS | Keyword URL-encoded in href |
| Supplier | List loads | PASS | 3 rows render OK |
| Supplier | View id=999999 → flash error | PASS (after re-fix) | URL stripped, toast `showToast(...,'danger')` |
| Invoice-Print | Filter inputs XSS | PASS | `<c:out>` HTML-escapes value |
| Invoice-Print | List loads | PASS | Headers + filter sidebar |
| Invoice-Print | Print 80mm receipt | SKIP | DB chưa có order data |
| Invoice-Print | ĐÃ HỦY badge | SKIP | DB chưa có cancelled order |

**Verdict:** 8/8 testable PASS, 2 SKIP do thiếu seed data.

## Bug phát hiện thêm trong quá trình verify

`toast.jsp` chỉ check `flashMessage`, không check `flashError`. Servlet doGet error path set `flashError` (`AppConstants.SESSION_FLASH_ERROR`) nhưng toast không render. Đã fix bằng cách thêm `<c:if test="${not empty sessionScope.flashError}">` block với type `danger`.

## Files Modified (final)

```
src/main/webapp/WEB-INF/views/common/toast.jsp           (+15)
src/main/webapp/WEB-INF/views/branch/branches.jsp        (XSS pagination)
src/main/webapp/WEB-INF/views/supplier/suppliers.jsp     (XSS pagination)
src/main/webapp/WEB-INF/views/invoice/invoices.jsp       (XSS filter+pagination)
src/main/webapp/WEB-INF/views/invoice/invoice-print.jsp  (ĐÃ HỦY marker)
src/main/java/com/kiotretail/employee/controller/BranchServlet.java   (redirect+flash)
src/main/java/com/kiotretail/product/controller/SupplierServlet.java  (redirect+flash)
src/main/java/com/kiotretail/employee/service/BranchService.java      (validate template)
src/main/java/com/kiotretail/product/service/SupplierService.java     (softDelete + email length + trim)
src/main/java/com/kiotretail/invoice/service/InvoiceService.java      (cancel/payment guards)
src/main/java/com/kiotretail/invoice/dao/OrderDAO.java                (dateTo SQL fix)
```

## Validation
- `mvn test`: BUILD SUCCESS, 6/6 PASS
- `mvn -DskipTests package`: BUILD SUCCESS
- Tomcat hot-reload OK

## Screenshots
`plans/reports/screenshots/{branch,supplier,invoice}-{1,2,3,4}.png` + `*-flash-final.png`

## Unresolved
- Branch ManagerID column (UC-1.6 acceptance) — schema change, High-Risk, cần story riêng.
- Print/cancelled E2E test cần seed dữ liệu order DB.
- Lỗi `NotFoundException` đang trả tiếng Anh ("Branch not found: 999999"). Có nên i18n sang tiếng Việt không?
