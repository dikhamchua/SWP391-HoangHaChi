# Harness Backlog

Use this file when an agent discovers a missing harness capability but should
not change the operating model immediately.

## Template

```md
## Missing Harness Capability

### Title

Short name.

### Discovered While

Task or story that exposed the gap.

### Current Pain

What was hard, repeated, ambiguous, or unsafe?

### Suggested Improvement

What should be added or changed?

### Risk

Tiny, normal, or high-risk.

### Status

proposed | accepted | implemented | rejected
```

## Items

### 1. Vietnamese Diacritics Missing Across All JSP Views

**Discovered While:** UI review against KIOT-Viet reference HTML files.

**Current Pain:** All JSP files were written without Vietnamese diacritics (e.g., "Them moi hang hoa" instead of "Thêm mới hàng hóa"). This affected every user-facing page: products, customers, employees, invoices, reports, POS sale, dashboard. Root cause: initial file writes used encoding methods that stripped diacritics (Python heredoc conflicts with JSP `${}` syntax, PowerShell here-string encoding issues).

**Suggested Improvement:**
- Add a validation gate: after writing any JSP file, grep for common non-diacritic Vietnamese patterns (e.g., `Danh sach`, `Them moi`, `Chinh sua`, `Khong co`) and flag as error.
- Prefer PowerShell `[System.IO.File]::WriteAllText()` with `UTF8Encoding($false)` for JSP files containing Vietnamese text.
- Never use Python heredoc (`'''`) for JSP content — `${}` EL expressions and single quotes conflict with all Python string delimiters.

**Risk:** Tiny (cosmetic, no logic change).

**Status:** implemented

---

### 2. CSS Sidebar Overflow — Input Elements Breaking Layout

**Discovered While:** Visual review of invoices page sidebar with date inputs.

**Current Pain:** `kr-filter-input` had no `box-sizing: border-box` and no `min-width: 0`, causing `<input type="date">` to overflow the 240px sidebar. The browser's default `min-width` for date inputs pushed them beyond the container boundary.

**Suggested Improvement:**
- All form elements inside `.kr-sidebar` must have `box-sizing: border-box` and `min-width: 0`.
- Add to `kr-common.css` base rules: `*, *::before, *::after { box-sizing: border-box; }` to prevent this class of bug globally.
- When adding new filter inputs to sidebar, visually verify they fit within 240px - 24px padding = 216px usable width.

**Risk:** Tiny (CSS-only fix).

**Status:** implemented

---

### 3. Create/Edit Pages Not Following KIOT-Viet Reference Design

**Discovered While:** UI review of product-create page against `KIOT-Viet/kiotviet-product-create-v2.html`.

**Current Pain:** Trang tạo mới sản phẩm (`product-create.jsp`) ban đầu không match design reference:
- Thiếu sidebar menu (QUẢN LÝ HÀNG HÓA với các link: Danh sách, Thêm mới, Import, Xuất file).
- Thiếu breadcrumb navigation (Hàng hóa / Thêm mới hàng hóa).
- Form không chia thành 3 card riêng biệt (Thông tin cơ bản, Giá & Tồn kho, Thông tin bổ sung).
- Không dùng grid layout 2-col / 3-col cho form fields.
- Các trang customer-create, customer-edit cũng dùng layout đơn giản (max-width:600px, không sidebar, không breadcrumb) — chưa match chuẩn KIOT-Viet.

**Suggested Improvement:**
- Mọi trang create/edit phải tuân theo pattern: `kr-page > kr-main > (kr-sidebar + kr-content)`.
- Sidebar chứa menu ngữ cảnh của module đang ở (highlight trang hiện tại).
- Content area có breadcrumb, page-title, form chia thành card sections với grid layout.
- Tham chiếu file HTML trong `KIOT-Viet/` làm design spec trước khi implement UI.
- Khi tạo trang mới, kiểm tra xem có file reference HTML tương ứng trong `KIOT-Viet/` không.

**Risk:** Normal (UI restructure, no logic change).

**Status:** implemented (product-create, product-edit, customer-create, customer-edit all done)

---

### 4. product-detail.jsp Not Using Shared Layout (kr-common.css)

**Discovered While:** Reading product-detail.jsp source code.

**Current Pain:** `product-detail.jsp` có toàn bộ CSS inline trong `<style>` tag riêng, không dùng `kr-common.css` shared styles. Trang có DOCTYPE/html/head riêng thay vì include header.jsp/navbar.jsp pattern chuẩn. Điều này gây:
- Inconsistent look & feel so với các trang khác.
- Duplicate CSS definitions.
- Khó maintain khi thay đổi design system.

**Suggested Improvement:**
- Refactor product-detail.jsp theo pattern chuẩn: include header.jsp + navbar.jsp, dùng kr-common.css classes.
- Xóa inline `<style>` block, thay bằng kr-* classes.
- Áp dụng layout: `kr-page > kr-main > (kr-sidebar + kr-content)` giống product-create.
- Kiểm tra tất cả các trang detail/view khác xem có cùng vấn đề không.

**Risk:** Normal (UI refactor, no business logic change).

**Status:** proposed

---

### 5. POS Sale Page (sale.jsp) Uses Fully Inline Styles

**Discovered While:** Reading sale.jsp source — 215 lines of inline CSS.

**Current Pain:** `sale.jsp` có ~200 dòng CSS inline trong `<style>` tag, không share bất kỳ class nào từ `kr-common.css`. Trang POS có layout riêng (pos-wrapper, pos-left, pos-right) nên có lý do hợp lệ, nhưng vẫn nên extract thành file CSS riêng để dễ maintain.

**Suggested Improvement:**
- Extract inline styles thành `assets/css/kr-pos.css`.
- Giữ layout riêng cho POS (không cần sidebar/breadcrumb pattern) nhưng reuse common variables (colors, fonts, border-radius, shadows).
- Share button styles (`.kr-btn`, `.kr-btn-primary`) thay vì define lại.

**Risk:** Tiny (CSS extraction, no logic change).

**Status:** proposed

---

### 6. Pagination baseUrl Built With Raw EL → Reflected XSS / URL Corruption

**Discovered While:** Bug-scan workflow trên 3 module (Branch UC-1.6, Supplier UC-4.1, Invoice-Print UC-3.5) ngày 2026-06-01.

**Current Pain:** Nhiều JSP list page build pagination `baseUrl` bằng concat EL trực tiếp:

```jsp
<c:set var="baseUrl" value="${ctx}/admin/branches?keyword=${keyword}" />
```

Hậu quả:
- Reflected XSS: keyword `"><script>alert(1)</script>` thoát attribute `href` trong pagination.jsp khi browser render.
- URL corruption: keyword chứa `&` / `=` / `+` / khoảng trắng khiến link phân trang sinh query string sai (mất keyword, lẫn param).
- Filter inputs (`<input value="${filter.keyword}">`) cũng không escape, cùng class lỗi.

Đã fix ở 3 trang (`branches.jsp`, `suppliers.jsp`, `invoices.jsp`) nhưng có thể còn các list page khác mắc cùng pattern.

**Suggested Improvement:**
- Quy ước CHUẨN: build pagination baseUrl bằng `<c:url><c:param/></c:url>` — JSTL tự URL-encode value:
  ```jsp
  <c:url var="baseUrl" value="/admin/<module>" scope="request">
      <c:param name="keyword" value="${keyword}" />
      <c:param name="status" value="${filter.status}" />
  </c:url>
  ```
- Mọi `<input>` value attribute từ user input phải bọc `<c:out value='${...}'/>` (kể cả hidden input).
- Thêm grep check pre-commit: `grep -rn "baseUrl.*?keyword=\\\${.*}" src/main/webapp` → nếu match thì FAIL.
- Cập nhật `docs/patterns/jsp-pagination.md` (cần tạo) với template chuẩn.

**Risk:** Tiny (JSP-only, không động Java).

**Status:** implemented (3 trang + pre-commit guard `scripts/check-jsp-xss.sh` đã wire vào `.git/hooks/pre-commit` ngày 2026-06-01)

---

### 7. toast.jsp Bỏ Sót Key `flashError` → Toast Lỗi Không Hiển Thị

**Discovered While:** Playwright E2E verify ngày 2026-06-01 cho UC-1.6 / UC-4.1 sau khi Servlet đã chuyển từ `forward + ATTR_ERROR_MESSAGE` sang `redirect + SESSION_FLASH_ERROR`.

**Current Pain:** `toast.jsp` chỉ render toast khi `sessionScope.flashMessage` (thành công) tồn tại. Nhưng `AppConstants.SESSION_FLASH_ERROR = "flashError"` được set bởi mọi servlet khi catch ServiceException — toast không bao giờ hiển thị, lỗi bị nuốt câm.

```jsp
<%-- TRƯỚC --%>
<c:if test="${not empty sessionScope.flashMessage}">
    showToast(...)
</c:if>
<%-- thiếu xử lý flashError --%>
```

Hậu quả: Mọi flow `redirect + setAttribute(SESSION_FLASH_ERROR, ...)` (Branch view 404, Supplier view 404, BranchServlet doPost catch, SupplierServlet doPost catch, ...) im lặng — UX confusing, user không biết tại sao trang trống.

**Suggested Improvement:**
- `toast.jsp` PHẢI render cả 2 keys: `flashMessage` (success/info) và `flashError` (danger):
  ```jsp
  <c:if test="${not empty sessionScope.flashError}">
      <c:set var="_toastErr" value="${sessionScope.flashError}" />
      <c:remove var="flashError" scope="session" />
      <script>
        (function _waitToastErr() {
          if (typeof showToast === 'function') {
            showToast('<c:out value="${_toastErr}" escapeXml="true"/>', 'danger');
          } else { setTimeout(_waitToastErr, 50); }
        })();
      </script>
  </c:if>
  ```
- Quy ước: bất kỳ session flash key mới nào (`flashWarning`, `flashInfo`, ...) phải được wire vào `toast.jsp` cùng commit.
- Pre-commit check: nếu một servlet set `SESSION_FLASH_*` thì phải có block xử lý tương ứng trong `toast.jsp`.
- Cập nhật `docs/patterns/flash-messages.md` (cần tạo) ghi rõ contract servlet ↔ toast.jsp.

**Risk:** Tiny (JSP-only fix, đã verify qua Playwright).

**Status:** implemented

---

### 8. SQL Date Range Filter So Sánh Sai Cận Trên `dateTo`

**Discovered While:** Bug-scan invoice-print module 2026-06-01.

**Current Pain:** `OrderDAO.appendFilterClauses` dùng `o.CreatedAt <= ?` với value yyyy-MM-dd → SQL Server cast thành `2026-06-01 00:00:00`, mọi order tạo cùng ngày sau midnight bị **loại khỏi kết quả** (ví dụ filter "đến 31/05" mất hết hóa đơn ngày 31/05). Đây là class lỗi phổ biến cho mọi DAO có date range trên kiểu DATETIME.

**Suggested Improvement:**
- Quy ước CHUẨN cho mọi date range filter trên cột DATETIME (SQL Server):
  ```sql
  -- INCLUSIVE both ends:
  AND col >= CAST(? AS date)
  AND col < DATEADD(day, 1, CAST(? AS date))
  ```
- Hoặc parse `dateTo` thành `Timestamp` 23:59:59.997 ở Java layer.
- Audit các DAO khác có filter date range: `ReportDAO`, `OrderDetailDAO` (nếu có), tránh lỗi cùng class.
- Cập nhật `docs/patterns/sql-date-range.md` (cần tạo).

**Risk:** Tiny (DAO SQL-only).

**Status:** implemented (OrderDAO); proposed cho audit toàn bộ DAO date filters.

---

### 9. Service Layer Bỏ Sót Status Guard Trên State Transition

**Discovered While:** Bug-scan invoice-print 2026-06-01.

**Current Pain:**
- `InvoiceService.cancelOrder` không check `status == pending` → forged POST có thể cancel đơn `completed`/`cancelled`, phá audit trail.
- `InvoiceService.addPayment` không reject `status == cancelled` → đơn đã hủy vẫn nhận thanh toán, sau khi đủ tiền tự flip thành `completed`, mâu thuẫn.

UI có hide button bằng `<c:if>` nhưng server không enforce → trust boundary sai.

**Suggested Improvement:**
- Quy ước: mọi state transition (cancel, complete, refund, archive, ...) phải có **server-side guard kiểm tra current status hợp lệ** trước khi update. Không tin UI.
- Thêm utility method `assertCurrentStatus(entity, expectedStatus, errMsg)` ở base service hoặc shared validation helper để giảm boilerplate.
- Test pattern: mỗi service method state-transition phải có ít nhất 1 negative unit test "không được transition khi status sai".
- Code review checklist: PR có thay đổi service xử lý state phải kèm guard test.

**Risk:** Tiny (logic only).

**Status:** implemented (InvoiceService); proposed cho audit các Service khác (Employee status, Branch status, ...).

---

### 10. Soft-Delete Mặc Định Cho Entity Có FK Reference

**Discovered While:** Bug-scan supplier 2026-06-01.

**Current Pain:** `SupplierService.deleteSupplier` gọi `supplierDAO.delete()` (hard DELETE). Supplier được FK reference từ `Product.SupplierID`, nên delete khi có product liên kết → SQL Server raise FK violation, ServiceException bao text raw "Database error: ..." hiển thị cho user (information disclosure + UX kém). DAO đã có `softDelete()` (set `Status='inactive'`) nhưng service không gọi.

`BranchDAO` cũng có cùng pattern (softDelete tồn tại + được gọi đúng) nhưng list page không filter `Status` → soft-deleted branch vẫn hiển thị, không phân biệt với "deactivate".

**Suggested Improvement:**
- Quy ước: Entity có FK reference từ entity khác (Supplier, Branch, Category, Customer, Employee) → `delete()` action ở service PHẢI gọi `softDelete()` thay vì hard DELETE. Hard DELETE chỉ dùng khi:
  1. Không có FK reference, hoặc
  2. Service kiểm tra trước (count references) và throw ValidationException khi vẫn còn ràng buộc.
- Khi đã soft-delete: list page mặc định lọc `Status = active`, có toggle "Show inactive" nếu UC yêu cầu.
- DAO never raw-throw `ServiceException("Database error: " + e.getMessage(), e)` chứa SQL error text — wrap thành ValidationException tiếng Việt thân thiện.
- Cập nhật `docs/patterns/soft-delete.md` (cần tạo).

**Risk:** Tiny (logic only).

**Status:** implemented (SupplierService); proposed cho audit các Service khác và list-page filter.

---

### 11. JSP `<input value=...>` Không Escape Attribute Context

**Discovered While:** Bug-scan invoice-print 2026-06-01.

**Current Pain:** `invoices.jsp` có nhiều `<input value="${filter.keyword}">` không bọc `<c:out>`. Keyword `"><script>...</script>` thoát attribute → reflected XSS. Pattern này lặp lại ở mọi JSP có form filter.

**Suggested Improvement:**
- Quy tắc cứng: **mọi `value=`, `placeholder=`, `title=`, `data-*=`** trong JSP nhận giá trị từ EL phải bọc `<c:out>` hoặc `fn:escapeXml`.
- ELvanilla `${...}` chỉ chấp nhận cho:
  - Số (formatNumber result)
  - Giá trị hằng từ AppConstants
  - Class names hardcoded
- Thêm pre-commit grep: pattern `value="\$\{[^}]*\}"` không có `<c:out>` → FAIL.
- Cập nhật `docs/patterns/jsp-xss-safety.md` (cần tạo) liệt kê tất cả attribute risk.

**Risk:** Tiny (JSP only, security).

**Status:** implemented (invoices.jsp + pre-commit guard `scripts/check-jsp-xss.sh` chain qua `.git/hooks/pre-commit` ngày 2026-06-01; whitelist `value="${entity.id}"` / `value="${entity.somethingId}"` cho numeric PK)

---

## CSRF protection cho form POST (deferred from po-approval-workflow-integration)

**Date:** 2026-06-02
**Source:** OpenSpec change `po-approval-workflow-integration` (issue #36 / UC-4.2)

**Current Pain:** Spec (task 3.5, 2.2-2.4) yêu cầu hidden CSRF token "use existing pattern" nhưng codebase KHÔNG có CSRF infra nào (0 grep hit). Các form POST state-transition mới (submit/approve/reject/cancel/update/receiveSubmit trên PurchaseServlet + approve/reject trên ApprovalServlet) hiện chỉ dựa AuthFilter session auth — không có token chống CSRF. Một trang độc có thể tự động POST approve/reject thay người dùng đã đăng nhập.

**Decision:** DEFER cho v1 (quyết định của user khi thực thi change). Wiring workflow ưu tiên trước; CSRF làm sau như một hard-gate security riêng.

**Suggested Improvement:**
- Tạo `CsrfUtil` (shared/util): sinh token per-session, lưu session attr `csrfToken`.
- Helper trong `BaseServlet`: `requireCsrf(request)` ném ValidationException khi token mismatch; gọi ở đầu mọi doPost state-transition.
- JSP: hidden `<input name="csrf" value="${sessionScope.csrfToken}">` trong mọi form POST (purchase-detail action buttons, reject/cancel modal, purchase-edit, purchase-receive, approval inbox approve/reject).
- Cân nhắc dùng SameSite=Strict cho session cookie như lớp phòng thủ bổ sung.

**Risk:** High-Risk (security hard-gate per CLAUDE.md) — cần human confirmation + story packet khi triển khai.

**Status:** deferred — chưa implement. Workflow approval hoạt động end-to-end dựa session auth; CSRF là hardening còn thiếu.

---

## Tàn dư SQL Server dialect trong MySQL codebase (phát hiện khi E2E po-approval-workflow-integration)

**Date:** 2026-06-02
**Source:** E2E test issue #36 — app dùng MySQL (mysql-connector-j 8.4, DatabaseUtil hardcode jdbc:mysql) nhưng nhiều DAO còn viết SQL kiểu SQL Server, ném SQLSyntaxErrorException lúc runtime (build/unit-test KHÔNG bắt được vì không chạm DB).

**Đã fix trong change này (nằm trên đường happy path PO):**
- ProductDAO.searchByKeyword: `SELECT TOP (?)` → `LIMIT ?` (đặt cuối) + cho phép keyword rỗng (form tạo PO load được sản phẩm).
- PurchaseOrderDAO: 13 chỗ `SYSUTCDATETIME()` → `UTC_TIMESTAMP()` (submit/approve/reject/cancel/receive/complete/recalc đều fail trước khi fix).

**CÒN TÀN DƯ chưa fix (ngoài scope #36 — thuộc module Stock #31/#33/#34 + report):**
- inventory/dao/StockAdjustmentDAO.java: 7 chỗ `SYSUTCDATETIME()`
- inventory/dao/StockTransferDAO.java: 9 chỗ `SYSUTCDATETIME()`
- invoice/dao/OrderDAO.java:233 `DATEADD(day, 1, CAST(? AS date))` → MySQL `DATE_ADD`/`INTERVAL`
- report/dao/ReportDAO.java:102 `SELECT TOP(?)` → `LIMIT ?`

**Suggested Improvement:**
- Sweep toàn bộ DAO thay hàm SQL Server sang MySQL: SYSUTCDATETIME→UTC_TIMESTAMP, GETDATE→NOW, SELECT TOP→LIMIT, ISNULL→IFNULL, DATEADD/DATEDIFF→DATE_ADD/TIMESTAMPDIFF, NEWID→UUID, SCOPE_IDENTITY→LAST_INSERT_ID.
- Thêm pre-commit grep chặn các token SQL Server trong src/main/java.
- Cân nhắc integration test chạm DB thật (Testcontainers MySQL) để bắt loại lỗi này trong CI, vì unit test hiện dùng reflection-stub không chạm DB.

**Risk:** Medium (mỗi DAO lỗi làm chết một luồng nghiệp vụ khi chạy thật).

**Status:** partial — PO + Product path đã fix & verify E2E; Stock/Order/Report path còn lỗi, chưa test.
