# Gap Analysis: UI Features vs Backend & Issues

**Tài liệu:** Báo cáo phân tích khoảng cách (gap-analysis) giữa UI hiện có, backend đã triển khai, và các GitHub Issues OPEN còn tồn đọng.
**Ngày báo cáo:** 2026-06-01
**Phạm vi:** Toàn bộ codebase KiotRetail (SWP391_Group_5)
**Tác giả:** Senior Tech Lead (workflow-subagent)
**Tham chiếu:** docs/planning/ROADMAP.md, plans/reports/analyze-260601-* (3 phân tích nguồn)

---

## 1. Tóm tắt điều hành (Executive Summary)

Dự án KiotRetail hiện ở giai đoạn cuối Sprint Stabilization với khoảng **37% use-case hoàn thành** (17 UC done, 5 UC partial, 31 UC not started). Tầng kiến trúc đã sạch: 12 servlet kế thừa BaseServlet, không còn SQL trong controller, service layer và flash-message PRG được áp dụng nhất quán. UI có 42 JSP với pattern KiotViet-style (toolbar + filter sidebar + bảng + pagination + toast) tương đối thống nhất ở 8 module CRUD (auth, branch, supplier, employee, customer, product, POS, error).

Tuy nhiên, có một **bug runtime nghiêm trọng**: file `common/footer.jsp` không tồn tại nhưng được include bởi 10+ trang list, sẽ ném `JasperException` trên Tomcat khi render thật. Bên cạnh đó, hai hệ navigation song song (`sidebar.jsp` Bootstrap-based dead code và `navbar.jsp` thực tế đang dùng) gây nhiễu kiến trúc. Mismatch backend-vs-UI nóng nhất: `InvoiceServlet` có GET `create` nhưng thiếu POST `add`, `POSServlet.checkout` không trả `orderId` cho UI để in hóa đơn, `CategoryServlet` thiếu GET dedicated cho view/create/edit form.

**5 module thiếu hoàn toàn UI** cần thiết kế từ đầu: `inventory/`, `finance/`, `role/`, `system/`, `public/`. **27 GitHub Issue OPEN** với 13 cần tạo JSP mới, 10 cần enhance JSP có sẵn, 2 thuần backend, 2 parent gộp. Khuyến nghị ưu tiên Sprint 1-2 tới: fix footer.jsp, hợp nhất navigation, design Inventory module (UC-5.x), bổ sung Purchase workflow (receive/return/approve), thiết kế Roles và Permissions UI làm nền cho RBAC.

---

## 2. Tổng quan trạng thái dự án

| Hạng mục | Chỉ số | Ghi chú |
|---|---|---|
| Tổng số UC theo roadmap | 53 | 9 nhóm UC chính |
| UC hoàn thành (Done) | 17 (~32%) | Auth, CRUD core, POS cơ bản, Dashboard |
| UC một phần (Partial) | 5 (~9%) | Invoice, Purchase, Report, Sidebar, POS checkout |
| UC chưa làm (Not Started) | 31 (~59%) | Inventory, Finance, RBAC, Public site, Reports mở rộng |
| Tổng số JSP hiện có | 42 | 12 thư mục module + common/error |
| Servlet đã refactor | 12/12 | Zero SQL trong controller |
| Service files | 11 | Tách rõ business logic |
| Module CRUD đầy đủ | 8 | auth, branch, supplier, employee, customer, product, POS, error |
| Module thiếu hoàn toàn UI | 5 | inventory, finance, role, system, public |
| Issues OPEN cần xử lý UI | 27 | 13 missing + 10 partial + 2 backend + 2 parent |
| Tỉ lệ UI Coverage so với UC roadmap | ~50% | Tính theo số UC có ít nhất 1 JSP |
| Bug nghiêm trọng đang tồn | 1 | `common/footer.jsp` không tồn tại |

**Trạng thái sprint hiện tại:**

- Sprint Stabilization: Đang fix bug, refactor, thống nhất pattern.
- Sprint 1 (sắp tới): POS + Auth hoàn thiện (credit sale, loyalty discount, role redirect).
- Sprint 2 (kế tiếp): Purchase workflow + Inventory module (stock check, transfer, alert).

---

## 3. Bảng UI Screens hiện có theo module

| Module | Đường dẫn | Số JSP | Trạng thái | Thiếu / Cần bổ sung |
|---|---|---|---|---|
| Auth | `views/auth/` | 6 | Đầy đủ | Không có profile page (xem/sửa info nhân viên hiện tại) |
| Common (layout) | `views/common/` | 5 | Lỗi nghiêm trọng | `footer.jsp` KHÔNG TỒN TẠI dù được include 10+ nơi |
| Branch CRUD | `views/branch/` | 4 | Đầy đủ | Inline CSS dày, lặp với supplier |
| Supplier CRUD | `views/supplier/` | 4 | Đầy đủ | Thiếu tab debt/payment-history trong supplier-detail |
| Employee CRUD | `views/employee/` | 4 | Đầy đủ | List backend không filter theo role/branch (mismatch tiềm năng) |
| Customer CRUD | `views/customer/` | 4 | Đầy đủ | Thiếu customer-debt, debt-collect, points-history |
| Product CRUD | `views/product/` | 4 | Đầy đủ | Toolbar có Import/Export nhưng controller chưa rõ; thiếu pricing-matrix |
| Category | `views/category/` (chưa có) | 0 | Thiếu | Sidebar link tới `/admin/categories` nhưng không có JSP, phụ thuộc modal? |
| Invoice | `views/invoice/` | 3 | Một phần | Có `create` GET form nhưng thiếu POST handler `add` (form submit fail) |
| POS | `views/pos/` | 1 | Đầy đủ | Cần enhance: credit-sale, loyalty discount, points redemption, multi-payment |
| Purchase | `views/purchase/` | 3 | Một phần | Thiếu receive, return-to-supplier, approve, supplier-payment; tiếng Việt KHÔNG dấu |
| Report | `views/report/` | 2 | Một phần | Render table thay biểu đồ; thiếu employee-sales, inventory, customer, profit, supplier |
| Error | `views/error/` | 2 | Đầy đủ | Cần verify mapping trong web.xml |
| Inventory | `views/inventory/` (chưa có) | 0 | Thiếu hoàn toàn | Cần tạo: stock-checks, stock-adjustment, transfers, low-stock-alert |
| Finance | `views/finance/` (chưa có) | 0 | Thiếu hoàn toàn | Cần tạo: transactions, cash-flow, expense-categories, payable-receivable |
| Role / Permission | `views/role/` (chưa có) | 0 | Thiếu hoàn toàn | Cần tạo: roles list, role-create/edit, permission-matrix |
| System / Settings | `views/system/` (chưa có) | 0 | Thiếu hoàn toàn | Cần tạo: settings, audit-log, audit-detail |
| Public website | `views/public/` (chưa có) | 0 | Thiếu hoàn toàn | Effort lớn nhất: home, catalog, cart, checkout, tracking, contact, about |
| Approval workflow | `views/approval/` (chưa có) | 0 | Thiếu hoàn toàn | Cần: pending-approvals (inbox), approval-detail; cross-cutting block #30/#31/#33/#34 |
| Promotion / Voucher | `views/promotion/` (chưa có) | 0 | Thiếu | Hệ retail thường có giảm giá/voucher, chưa làm |

**Tổng cộng:** 42 JSP có sẵn, **6 module cần tạo mới hoàn toàn**, **3 module cần bổ sung enhance**.

---

## 4. Bảng Backend Features không có UI (Ghost Features)

Đây là những controller/service đã viết hoàn chỉnh ở backend nhưng UI chưa wire, gây dead code hoặc bug.

| Tên feature | File backend | Trạng thái UI | Mức độ ảnh hưởng |
|---|---|---|---|
| `ApprovalService` (canSubmit/canApprove/canReject/logTransition) | `shared/service/ApprovalService.java` | Không có UI gọi tới | Dead code, block UC duyệt phiếu cho purchase/transfer/return |
| `CategoryServlet` (GET view/edit/create dedicated) | `product/controller/CategoryServlet.java` | Không có JSP `category/` nào | Sidebar link `/admin/categories` có khả năng 404 |
| `InvoiceServlet.create` (GET form) | `invoice/controller/InvoiceServlet.java` | JSP `invoice-create.jsp` có thể tồn tại nhưng thiếu POST `add` | Form admin tạo đơn ngoài POS submit fail |
| `POSServlet.checkout` không trả `orderId` | `pos/controller/POSServlet.java` | JSP `sale.jsp` không nhận được orderId | Không thể auto-redirect sang `invoice-print.jsp` |
| `ProductService.searchProducts('a',500)` hack fallback | `product/service/ProductService.java` | `purchase-create.jsp` dùng | Cần thêm `listAllActive()` clean hơn |
| `EmployeeServlet` list filter (role/branch) | `employee/controller/EmployeeServlet.java` | UI `employees.jsp` có dropdown nhưng backend chỉ paginate | Filter UI giả, chỉ hoạt động phần tử client-side |
| `AuthService.resetPassword` qua email + newPassword | `employee/service/AuthService.java` | UI `forgot-password.jsp` có form | KHÔNG OTP/token, rủi ro bảo mật cao |
| `DashboardService.getSalesReport` tổng quát | `report/service/DashboardService.java` | Chỉ có `dashboard.jsp` và `sales-report.jsp` consume | Có thể tái dùng cho 5 báo cáo mở rộng |
| `Product.stockAlertQty` field | Entity `Product` | Không có UI alert | Backend có ngưỡng nhưng không trigger UI cảnh báo |
| `Order.cancelOrder` action | `InvoiceService` | Có UI nút Hủy đơn trong invoice-detail | OK, đã wire |
| `Purchase` workflow `confirm/receive/cancel` | `PurchaseService` | UI `purchase-detail.jsp` có nút | OK, đã wire |

**Tóm gọn:** 6 ghost features cần fix mismatch (Category, Invoice POST add, POS orderId redirect, Employee filter, Approval, Profile page); 2 cần refactor clean (ProductService fallback, AuthService reset password); 1 cần thêm widget UI (Low stock alert).

---

## 5. Bảng GitHub Issues cần UI Design mới (Priority High to Low)

Sắp xếp theo độ ưu tiên, độ chặn (blocking), và effort.

| Priority | Issue # | Tên | Trạng thái UI | Effort | Module mới? | Blocking |
|---|---|---|---|---|---|---|
| P0 | #37 | Approval Workflow Foundation | backend-only + cần inbox UI | M | Có (`views/approval/`) | Block #30, #31, #33, #34 |
| P0 | #1 | UC-1.5 Manage Roles & Permissions | ui-missing | L | Có (`views/role/`) | Block toàn bộ RBAC |
| P0 | (bug) | `common/footer.jsp` không tồn tại | broken | XS | Không | Block runtime 10+ trang |
| P1 | #8 | UC-5.1~5.5 Inventory Management | ui-missing (parent) | XL | Có (`views/inventory/`) | Sprint 2 trọng tâm |
| P1 | #31 | UC-5.1/5.2 Stock Check & Adjustment | ui-missing | L | Trong inventory/ | Block kiểm kho |
| P1 | #34 | UC-5.3 Stock Transfer | ui-missing | L | Trong inventory/ | Multi-branch core |
| P1 | #29 | UC-5.5 Low Stock Alert | ui-missing | M | Trong inventory/ + widget navbar | Sprint 2 |
| P1 | #10 | UC-7.1~7.5 Finance Management | ui-missing (parent) | XL | Có (`views/finance/`) | Module mới hoàn toàn |
| P1 | #30 | UC-4.3 Receive Goods | ui-partial | M | Không (mở rộng purchase/) | Sprint 2 Purchase |
| P1 | #33 | UC-4.4 Return Goods to Supplier | ui-missing | M | Không (mở rộng purchase/) | Cần #37 |
| P1 | #36 | UC-4.2 Create Purchase Orders | ui-partial | S | Không | Verify form đầy đủ |
| P2 | #12 | UC-9.1~9.7 Public Website for Guest | ui-missing | XL | Có (`views/public/`) | Effort lớn nhất, có thể defer |
| P2 | #3 | UC-1.7/1.8/2.1 System Config & Audit Log | ui-missing | M | Có (`views/system/`) | Cross-cutting governance |
| P2 | #5 | UC-3.6 Process Return/Exchange | ui-missing | M | Không (mở rộng invoice/) | Cần workflow refund |
| P2 | #11 | UC-8.2~8.8 Extended Reports (parent) | ui-partial | L | Không (mở rộng report/) | 5 báo cáo mới |
| P2 | #41 | UC-8.4 Inventory Report | ui-missing | M | Trong report/ | Cần inventory backend trước |
| P2 | #42 | UC-8.3 Employee Sales Report | ui-missing | S | Trong report/ | Tái dùng layout sales-report |
| P2 | #32 | UC-4.5 Pay Supplier Debt | ui-missing | M | Mở rộng supplier/ | Cần finance backend |
| P2 | #39 | UC-6.7 View Customer Debt | ui-missing | M | Mở rộng customer/ | Cần credit-sale workflow |
| P2 | #43 | UC-3.9 Collect Customer Debt | ui-missing | M | Mở rộng customer/ | Sau #39 |
| P3 | #38 | UC-6.6 Apply Loyalty Discount in POS | ui-partial | S | Không (enhance sale.jsp) | Cần tier_discount lookup |
| P3 | #40 | UC-3.8 Credit Sales Order | ui-partial | S | Không (enhance sale.jsp) | Cần customer.credit_limit |
| P3 | #13 | UC-2.4/3.1 Pricing per Branch & Unit | ui-partial | M | Mở rộng product/ | Cần product_pricing table |
| P3 | #28 | UC-4.6 View Purchase History | ui-partial | XS | Tái dùng purchases.jsp | Effort thấp |
| P3 | #6 | UC-3.8/3.9 Credit Sales & Customer Debt (parent) | ui-partial | M | Gộp #40 + #43 | Tham chiếu sub |
| P3 | #7 | UC-4.1~4.6 Supplier & Purchase (parent) | ui-partial | L | Gộp 5 sub | Tham chiếu sub |
| P3 | #9 | UC-6.4/6.6/6.7 Loyalty & Debt (parent) | ui-partial | L | Gộp #35 #38 #39 | Tham chiếu sub |
| P4 | #35 | UC-6.4 Customer Loyalty Auto-tier | backend-only | XS | Không (badge widget) | Cron + UI badge |

**Phân loại theo nhãn:**

- **13 issue ui-missing** (cần tạo JSP mới): #1, #3, #5, #8, #10, #12, #29, #31, #32, #33, #34, #41, #42, #43, #39
- **10 issue ui-partial** (enhance JSP có sẵn): #6, #7, #9, #11, #13, #28, #30, #36, #38, #40
- **2 issue backend-only** (chỉ widget nhỏ): #35, #37
- **5 issue parent** (gộp logic, trace qua sub): #6, #7, #8, #9, #10, #11

---

## 6. Khuyến nghị thiết kế UI ưu tiên (Top 7 màn cần design ngay)

Dựa trên độ chặn (blocking), giá trị business, và sequence sprint hiện tại:

### 6.1. Fix `common/footer.jsp` (P0, XS effort, BLOCKER)

- **Tại sao:** Bug runtime nghiêm trọng, 10+ trang list ném `JasperException` khi Tomcat render thật.
- **Action:** Tạo `src/main/webapp/WEB-INF/views/common/footer.jsp` tối thiểu với:
  - Đóng `</body></html>`
  - Khai báo hàm JS `showToast()` (hoặc include `kr-common.js` nếu có)
  - Optional: copyright bar + version stamp
- **Convention:** Đặt trong `views/common/`, kebab-case, sync với header.jsp.

### 6.2. Approval Workflow Inbox, `approval/pending-approvals.jsp` (P0, M effort)

- **Tại sao:** Block 4 issue cùng lúc (#30 receive, #31 stock-check, #33 supplier-return, #34 transfer).
- **Action:** Tạo thư mục `views/approval/` với:
  - `pending-approvals.jsp` (inbox: list đơn chờ duyệt, filter theo type, branch, requester)
  - `approval-detail.jsp` (chi tiết đơn + nút Approve/Reject + comment)
  - Badge `Pending Approval` trên `purchases.jsp`, `inventory/transfers.jsp`, `inventory/stock-checks.jsp`
- **Backend đã có:** `ApprovalService.canSubmit/canApprove/canReject/logTransition` (currently dead code).

### 6.3. Roles và Permissions Matrix, `role/permission-matrix.jsp` (P0, L effort)

- **Tại sao:** Nền tảng RBAC, block UC-1.5 và toàn bộ phân quyền chi tiết.
- **Action:** Tạo thư mục `views/role/` với 5 JSP:
  - `roles.jsp` (list role + count user mỗi role)
  - `role-create.jsp`, `role-edit.jsp`, `role-detail.jsp` (form CRUD)
  - `permission-matrix.jsp` (ma trận checkbox `role x permission`, UI nóng nhất, cần UX cẩn thận)
- **Convention:** Tái dùng pattern toolbar-search + sidebar-filter từ branch/supplier.

### 6.4. Stock Check và Adjustment, `inventory/stock-checks.jsp` (P1, L effort)

- **Tại sao:** Sprint 2 trọng tâm, core của Inventory module.
- **Action:** Tạo thư mục `views/inventory/` với:
  - `stock-checks.jsp` (list phiếu kiểm kê + status DRAFT/CONFIRMED/COMPLETED)
  - `stock-check-create.jsp` (form chọn chi nhánh + sản phẩm + nhập count thực tế)
  - `stock-check-detail.jsp` (so sánh hệ thống vs thực tế + duyệt điều chỉnh, tích hợp #37)
  - `stock-adjustment.jsp` (điều chỉnh thủ công ngoài phiếu kiểm)
- **Convention:** Pattern KiotViet-style như branch/supplier; cần multi-line item editor (giống purchase-create).

### 6.5. Stock Transfer, `inventory/transfers.jsp` (P1, L effort)

- **Tại sao:** Multi-branch core, cần thiết khi có 2+ chi nhánh.
- **Action:** Trong `views/inventory/`:
  - `transfers.jsp` (list phiếu chuyển kho)
  - `transfer-create.jsp` (form from-branch + to-branch + items + quantity)
  - `transfer-detail.jsp` (workflow approve/dispatch/receive, tích hợp #37)
- **Convention:** Tái dùng layout purchase-create.jsp (multi-line item) + workflow buttons như purchase-detail.jsp.

### 6.6. Receive Goods, Enhance `purchase/purchase-detail.jsp` (P1, M effort)

- **Tại sao:** Sprint 2 Purchase workflow chưa hoàn thiện; backend đã có action `receive`.
- **Action:** Hai hướng:
  - **Hướng A (đề xuất):** Thêm modal/section `receive-goods` trong `purchase-detail.jsp` hiện có, chế độ nhập số lượng thực nhận, ghi chú lệch, button Receive.
  - **Hướng B:** Tạo `purchase/receive-goods.jsp` riêng (nếu workflow cần nhiều trường ghi nhận chi tiết).
- **Đồng thời:** Sửa tiếng Việt KHÔNG dấu hiện có (Phieu nhap thành Phiếu nhập, Nha cung cap thành Nhà cung cấp) để consistent với rest of UI.

### 6.7. Low Stock Alert Widget + `inventory/low-stock-alert.jsp` (P1, M effort)

- **Tại sao:** Quick win, backend đã có `Product.stockAlertQty`, chỉ cần wire UI.
- **Action:**
  - Trang đầy đủ: `inventory/low-stock-alert.jsp` (list sản phẩm dưới ngưỡng + gợi ý reorder qty + nút Tạo PO nhanh).
  - Badge alert đỏ trên `common/navbar.jsp` (số sản phẩm sắp hết) + widget cảnh báo trên `dashboard.jsp`.
- **Convention:** Tái dùng pattern badge từ pagination component.

---

## 7. Risks và Open Questions

### 7.1. Risks (Rủi ro kỹ thuật)

| ID | Mô tả rủi ro | Mức độ | Khuyến nghị xử lý |
|---|---|---|---|
| R-1 | `common/footer.jsp` không tồn tại, runtime crash 10+ trang | Cao | Fix ngay trong Sprint Stabilization (task XS effort) |
| R-2 | Hai hệ navigation song song (`sidebar.jsp` + `navbar.jsp`), maintenance gấp đôi, layout vỡ khi sidebar render | Trung bình | Quyết định 1 component duy nhất; xóa cái còn lại hoặc thống nhất CSS framework |
| R-3 | Bootstrap không được include nhưng `sidebar.jsp` dùng class Bootstrap | Trung bình | Hoặc include Bootstrap, hoặc refactor sidebar về CSS thuần |
| R-4 | `AuthService.resetPassword` không OTP/token, rủi ro bảo mật | Cao (nếu lên prod) | Thêm flow OTP qua email; hiện tại OK cho SWP391 nhưng phải ghi chú rõ |
| R-5 | `InvoiceServlet` GET `create` không có POST `add`, form fail | Trung bình | Hoặc thêm POST handler, hoặc xóa GET form |
| R-6 | `POSServlet.checkout` không trả `orderId`, không auto-print | Trung bình | Truyền `orderId` qua flash hoặc redirect param |
| R-7 | Inline CSS rất dày trên hầu hết button/badge, vi phạm SoC | Thấp | Refactor sang `kr-common.css` với class `kr-action-link`, `kr-status-*` |
| R-8 | Báo cáo gọi là "biểu đồ" nhưng render table, UX yếu | Trung bình | Thêm Chart.js/ApexCharts; refactor 2 trang report |
| R-9 | `ApprovalService` dead code, block 4 issue | Trung bình | Wire qua issue #37 trong Sprint sắp tới |
| R-10 | Tiếng Việt KHÔNG dấu trong `purchase/*`, inconsistent | Thấp | Normalize toàn bộ chuỗi sang có dấu |
| R-11 | Module `category/` không có JSP, sidebar link có thể 404 | Trung bình | Verify `CategoryServlet` redirect; tạo `categories.jsp` modal-based hoặc dedicated page |
| R-12 | Effort `#12 Public Website` quá lớn, cần UX riêng | Cao (effort) | Defer sang sprint sau hoặc cắt scope (chỉ home + catalog cho MVP) |

### 7.2. Open Questions (Câu hỏi cần xác nhận)

1. **Footer.jsp:** Có phải team đã vô tình xóa file? Hay chưa từng tạo? Cần check git log để truy nguyên.
2. **Navigation:** Quyết định cuối, giữ `sidebar.jsp` (Bootstrap) hay `navbar.jsp` (CSS thuần)? Ảnh hưởng tới CSS framework và toàn bộ layout.
3. **Category:** Module này dùng modal trên product list hay cần dedicated page? Backend `CategoryServlet` không có GET view/edit, cần xác định product owner intent.
4. **Invoice manual create:** Admin có thực sự cần tạo đơn ngoài POS không? Nếu không, xóa GET `create`. Nếu có, thêm POST `add`.
5. **POS auto-print:** Sau checkout có cần auto-redirect sang `invoice-print.jsp` không? Quyết định ảnh hưởng tới `POSServlet.checkout` return.
6. **Public website effort:** Có nằm trong scope SWP391 không, hay là stretch goal? Effort XL có thể ăn 2-3 sprint.
7. **Promotion/Voucher:** Có cần module này không? Hệ retail thường có nhưng chưa thấy issue OPEN.
8. **Profile page:** Topbar avatar không clickable, có cần `auth/profile.jsp` không?
9. **Approval scope:** `#37` áp dụng cho những entity nào? Hiện đã rõ Purchase + Transfer + Stock Check + Return; có thêm Invoice cancel không?
10. **Report charts:** Dùng Chart.js, ApexCharts, hay D3? Quyết định ảnh hưởng tới bundle size và learning curve.
11. **Tiếng Việt purchase:** Có lý do nào giữ KHÔNG dấu không? Hay là legacy chưa kịp sửa?
12. **Multi-payment POS:** Có cần thanh toán đồng thời nhiều phương thức (tiền mặt + thẻ + voucher) trong 1 đơn không?
13. **Stock adjustment vs Stock check:** Hai concept tách rời hay gộp? Backend hiện chưa có entity riêng.

---

## 8. Tổng kết và Next Steps

**Số liệu chốt:**

- 42 JSP có sẵn trên 17 module phân biệt; 6 module cần tạo mới (`inventory/`, `finance/`, `role/`, `system/`, `public/`, `approval/`).
- 27 GitHub Issue OPEN đã phân loại theo priority P0-P4.
- 1 bug runtime nghiêm trọng (`footer.jsp`) cần fix trước mọi việc khác.
- 6 ghost features (backend có, UI thiếu/mismatch), cần wire trong Sprint Stabilization.
- 7 màn UI ưu tiên design ngay đã liệt kê chi tiết tại Section 6.

**Đề xuất sprint roadmap ngắn:**

- **Sprint Stabilization (now):** Fix R-1, R-2, R-5, R-6, R-10, R-11. Hợp nhất navigation. Refactor inline CSS. Wire ghost features.
- **Sprint 1 (POS + Auth):** Enhance `pos/sale.jsp` (credit, loyalty, points). Tạo `auth/profile.jsp`. Hoàn thiện role-based redirect.
- **Sprint 2 (Purchase + Inventory):** Hoàn thiện purchase workflow (receive, return). Build full Inventory module (stock-check, transfer, low-stock-alert). Wire `#37 Approval`.
- **Sprint 3 (Roles + Reports):** Build Roles và Permissions matrix. Add 5 báo cáo mở rộng. Bổ sung Chart.js.
- **Sprint 4+ (Finance + Public):** Build Finance module. Cân nhắc Public website (cắt scope MVP).

**File path báo cáo:**
`C:/Users/ADMIN/OneDrive - vinhdeptrai/Code Thue/SWP391/SU26/SWP391 - Quan Ly Cua Hang - Hoang Ha Chi/SWP391_Group_5/plans/reports/analyze-260601-0346-ui-features-gap.md`
