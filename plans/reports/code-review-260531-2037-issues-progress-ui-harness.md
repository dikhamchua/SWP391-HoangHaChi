# Báo cáo Review Tiến độ GitHub Issues + UI + Harness Compliance

- **Ngày**: 2026-05-31
- **Branch**: main
- **Reviewer**: Claude (Playwright + code review + Harness audit)
- **Phạm vi**: Issues #1, #2, #4, #7, #8 + UI tại http://localhost:9999/kiotretail/

---

## 1. Tổng kết tiến độ Issue

| Issue | Module | Trạng thái user khai | Trạng thái thực tế | Mức độ chính xác |
|---|---|---|---|---|
| #1 | Roles & Permissions | "Employee CRUD done, còn role mgmt" | Employee CRUD code có; Role mgmt CHƯA có Servlet/Service/JSP, chỉ có RoleDAO | Đúng |
| #2 | Branch Management | "DONE - full CRUD implemented" | Code đầy đủ, NHƯNG endpoint /admin/branches trả 404 trên server đang chạy | **Sai một phần** — code đủ, deploy chưa đủ |
| #4 | Print Invoice | "Invoice detail JSP done, còn print template" | invoice-detail.jsp có đủ, KHÔNG có nút Print, KHÔNG có template print | Đúng |
| #7 | Supplier & Purchase | "Supplier CRUD done (Phase 1), còn PO" | Supplier code đủ, NHƯNG /admin/suppliers trả 404. Phase 2-4 chưa bắt đầu | **Sai một phần** — code đủ, deploy chưa đủ |
| #8 | Inventory | "Pre-requisites noted, còn toàn bộ" | Không có module nào liên quan. Chỉ có field stockQuantity ở Product | Đúng |

---

## 2. Phát hiện UI (Playwright tại :9999)

### 2.1 Vấn đề nghiêm trọng

1. **Branch & Supplier 404 trên runtime**
   - GET /admin/branches → 404
   - GET /admin/suppliers → 404
   - Mặc dù `BranchServlet.java` (line 20: `@WebServlet("/admin/branches")`) và `SupplierServlet.java` (line 20: `@WebServlet("/admin/suppliers")`) đã có annotation đúng
   - Nguyên nhân khả nghi: Tomcat đang chạy bản WAR cũ chưa rebuild sau khi thêm 2 servlet này
   - Action: chạy `mvn clean package` rồi redeploy

2. **Navbar chưa có link cho 3 module mới**
   - File `src/main/webapp/WEB-INF/views/common/navbar.jsp` chỉ có 6 tab: Tổng quan / Hàng hóa / Đơn hàng / Khách hàng / Nhân viên / Báo cáo
   - **Thiếu**: Chi nhánh, Nhà cung cấp, Vai trò
   - Hậu quả: user đăng nhập không có cách nào tới các trang Branch/Supplier qua UI

3. **Footer + dashboard rỗng**
   - Trang `/admin/dashboard` không hiển thị widget thống kê nào (cần check thêm — có thể do DB chưa seed dữ liệu, hoặc DashboardService bị break)

### 2.2 Vấn đề nhỏ

4. **Console error 404**: `http://localhost:9999/kiotretail/assets/js/:0` — có chỗ trong JSP đang `<script src="${ctx}/assets/js/">` với path rỗng. Cần grep và sửa.

5. **Login error UX kém**: nhập email không tồn tại hiện text "Email không tồn tại" (lộ thông tin tồn tại của email — security smell, nên đổi thành "Email hoặc mật khẩu sai")

---

## 3. Code Review

### 3.1 Điểm tốt

- Pattern Modular Monolith được tuân thủ chặt: `controller → service → DAO → model` rõ ràng cho cả 4 module mới (Employee, Branch, Supplier, Invoice)
- DAO dùng `try-with-resources` + `PreparedStatement` đúng chuẩn — không có SQL injection rõ ràng
- Servlet mở rộng `BaseServlet`, dùng `AppConstants`, `ErrorMessages`, `ViewPaths` — tuân thủ "no magic strings" của AGENTS.md
- BranchService, SupplierService có validate phone regex, length limit, status whitelist
- `existsByName(name, excludeId)` được làm đúng cho cả create lẫn update

### 3.2 Vấn đề code

| # | File | Mức | Vấn đề |
|---|------|-----|--------|
| C1 | `BranchDAO.java`, `SupplierDAO.java`, `EmployeeDAO.java`, `RoleDAO.java` | MEDIUM | Catch `SQLException` rồi `e.printStackTrace()` mà không rethrow → service không biết có lỗi DB, trả về list rỗng/`false` âm thầm. Tốt hơn: throw `ServiceException` hoặc dùng SLF4J + rethrow runtime |
| C2 | `EmployeeService.java` | MEDIUM | Validate message hardcode tiếng Anh ("Full name is required", "Invalid role") — vi phạm "no magic strings". Nên dùng `ErrorMessages.FIELD_REQUIRED` như BranchService |
| C3 | `EmployeeService.updateEmployee` | LOW | Không update password, không validate role/branch khi update (chỉ check ở create) — rủi ro inconsistency |
| C4 | `EmployeeService.deleteEmployee` | LOW | Soft delete nhưng không kiểm tra ràng buộc (employee có Order chưa hoàn thành) |
| C5 | `InvoiceServlet.handleAddPayment` | MEDIUM | Catch `RuntimeException` cùng `ServiceException` rồi `setFlashMessage(ex.getMessage())` — leak stacktrace/message kỹ thuật ra UI |
| C6 | `InvoiceServlet` | LOW | Action "create" chỉ forward sang form trống; không có action "place" để thật sự tạo Order — Issue #4 print sẽ bị block nếu chưa có order completed |
| C7 | `BranchServlet.doPost.delete` | MEDIUM | `branchService.deleteBranch` → `BranchDAO.delete` là **HARD DELETE** (`DELETE FROM Branch`). Nếu Employee.BranchID FK → sẽ ném `SQLException` không xử lý, user thấy redirect không có flash error rõ ràng. Nên soft-delete (Status=inactive) như Employee |
| C8 | `web.xml` line 38-45 | **HIGH (security)** | DB password `123` hardcode plaintext trong web.xml. Vi phạm cả AGENTS.md ("No hardcoded secrets") lẫn Harness hard gate "Audit/security". Phải đẩy ra biến môi trường / JNDI Resource |
| C9 | `header.jsp` line 35 | LOW | `<title>KiotRetail</title>` cứng, mọi trang đều cùng title — bad SEO/UX |
| C10 | Toàn module mới | MEDIUM | **Không có test nào** — `src/test/` không tồn tại. Vi phạm Harness "Done definition: validation commands were run" |

---

## 4. Harness Compliance Audit

Đối chiếu với `AGENTS.md`, `docs/FEATURE_INTAKE.md`, `docs/CONTEXT_RULES.md`:

| Rule | Yêu cầu | Tuân thủ |
|------|---------|----------|
| Smallest correct change | Smallest patch | OK — mỗi servlet/service/DAO mới tách module gọn |
| No SQL in servlets/JSPs | Không SQL ngoài DAO | OK — đã grep, không thấy raw SQL ở servlet/JSP |
| No request/session in DAO | DAO sạch | OK |
| No hardcoded secrets | Không plaintext password | **VI PHẠM** — web.xml `db.password=123` |
| Reuse DAO/model patterns | Theo pattern cũ | OK |
| Update docs khi đổi boundary | Update docs | **VI PHẠM** — thêm 3 module mới (Branch, Supplier, EmployeeAdmin) nhưng không thấy story packet trong `docs/stories/`, không thấy update `docs/architecture/` hay `docs/status/` |
| Validation `mvn test` | Phải chạy test | **VI PHẠM** — `src/test/` không tồn tại, không có proof |
| Lane selection | Tiny/Normal/High-risk | **VI PHẠM** — Branch CRUD = ≥2 risk flags (Data model, Cross-domain) → Normal lane → cần story packet, **không có** |
| Hard gate: Authorization | Issue #1 chạm role-based access → high-risk | **CHƯA TRIGGER** — chưa làm Issue #1 nên chưa vi phạm, nhưng **phải vào high-risk lane** khi bắt đầu (template `docs/templates/high-risk-story/`) |
| Hard gate: Audit/security | Hardcode password trong web.xml | **VI PHẠM** — phải vào high-risk, có decision doc |
| AuthFilter cho /admin/* | Filter bảo vệ | OK — `/admin/branches` và `/admin/suppliers` được AuthFilter cover qua `<url-pattern>/admin/*</url-pattern>` |

### 4.1 Story / Decision thiếu

- Không có `docs/stories/US-XXX-branch-management.md`
- Không có `docs/stories/US-XXX-supplier-phase1.md`
- Không có `docs/stories/US-XXX-employee-admin-crud.md`
- Không có decision cho việc Branch hard-delete vs soft-delete

---

## 5. Khuyến nghị ưu tiên

### P0 (làm ngay)

1. **Build lại WAR + redeploy**: `mvn clean package` để Branch/Supplier 404 biến mất
2. **Cập nhật `navbar.jsp`** thêm 3 link: Chi nhánh, Nhà cung cấp, (sau này) Vai trò
3. **Đổi BranchDAO sang soft-delete** (Status=inactive) trùng với Employee để tránh FK violation
4. **Đẩy DB password ra ngoài web.xml** — dùng JNDI hoặc system property — vi phạm hard gate

### P1 (sprint hiện tại)

5. **Sửa lỗi 404 console** `assets/js/:0` — grep tìm `<script src="${ctx}/assets/js/">` rỗng
6. **Login error message** đổi "Email không tồn tại" → "Email hoặc mật khẩu không đúng"
7. **Tạo story packet** cho 3 module đã làm + Issue #4 + Issue #7 Phase 2 (theo template `docs/templates/story.md`)
8. **Thêm test** dù tối thiểu — `src/test/java/com/kiotretail/employee/service/BranchServiceTest.java` cho validate phone/name

### P2 (kế tiếp)

9. **Issue #4 Print Invoice**: thêm action `print` ở InvoiceServlet, view `invoice-print.jsp` với CSS `@media print` 80mm
10. **Issue #1 Role mgmt** — vào high-risk lane (hard gate Authorization), tạo `docs/stories/epics/.../US-XXX-role-management/` với 4 file template
11. **Refactor `EmployeeService` validation messages** dùng `ErrorMessages` constants thay vì hardcode tiếng Anh
12. **Issue #7 Phase 2**: PurchaseOrder + PurchaseOrderDetail model/DAO/service/servlet
13. **Issue #8 Inventory**: Phase 1 (dashboard read-only) trước

---

## 6. Câu hỏi chưa giải quyết

- [ ] Tomcat đang chạy bản WAR nào? Có ai đó đã `mvn package` sau khi commit Branch/Supplier chưa?
- [ ] Issue #1 sẽ dùng RBAC table mới (RolePermission) hay enum cứng trong code?
- [ ] Issue #4 print template có yêu cầu cụ thể về kích thước (58mm hay 80mm)?
- [ ] Issue #7 Purchase Order: có cần workflow approval (manager duyệt) hay sales manager tự confirm?
- [ ] Có đồng ý đẩy DB credential khỏi web.xml ngay sprint này không?
