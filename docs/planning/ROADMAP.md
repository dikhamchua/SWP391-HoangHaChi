# Development Roadmap - KiotRetail

## Tong Quan Tien Do

| Metric | Gia tri |
|--------|---------|
| Tong UC trong RDS | 53 |
| Da hoan thanh (DONE) | 17 |
| Dang lam do (PARTIAL) | 5 |
| Chua bat dau | 31 |
| Phan tram Hoan thanh | ~37% |
| Cong thuc | (DONE + 0.5*PARTIAL) / Tong = (17 + 2.5) / 53 |

> Cap nhat: 2026-05-31. Dua tren gap-analysis, git log, va GitHub issues.

---

## Trang Thai Hien Tai (Post-Sprint 0)

### Da hoan thanh gan day (commits 16edc09, eaf8014, f578360)

| UC | Ten | Ghi chu |
|----|-----|---------|
| UC-1.4 | Manage Staff Accounts | Employee JSP create/edit/detail da xong |
| UC-1.6 | Branch Management | BranchServlet + Service + CRUD JSP da xong |
| UC-2.2 | Manage Categories | Category delete action + JSP da xong |
| UC-3.10 | View Sales History | invoice-detail.jsp da xong |
| UC-4.1 | Manage Suppliers (partial) | SupplierServlet + CRUD da xong |

### Tong hop UC theo trang thai

**DONE (17 UC):**
- UC-1.1 Login, UC-1.2 Logout, UC-1.3 Register
- UC-1.4 Manage Staff Accounts
- UC-1.6 Manage Branches
- UC-2.2 Manage Categories
- UC-2.3 Manage Products, UC-2.5 Search Products
- UC-3.2 Create Sales Order, UC-3.3 Apply Discount, UC-3.4 Cash Payment, UC-3.7 Cancel Orders
- UC-3.10 View Sales History
- UC-4.1 Manage Suppliers
- UC-6.1 Add Customer, UC-6.2 Edit Customer, UC-6.3 Search Customers
- UC-8.1 Revenue Reports by Time

**PARTIAL (5 UC):**
- UC-5.4 View Inventory (stockQuantity hien thi, thieu dashboard rieng)
- UC-6.4 Customer Levels (co field, thieu auto-tier + ranking screen)
- UC-8.2 Revenue by Branch (backend co, thieu UI so sanh)
- UC-8.8 Best-selling Products (getTopProducts co, thieu man hinh rieng)
- UC-4.1 Supplier (CRUD xong, thieu purchase order integration)

**NOT STARTED (31 UC):**
- Xem chi tiet ben duoi theo sprint plan

---

## GitHub Issues Con Mo

| # | Title | Priority | Module | Status |
|---|-------|----------|--------|--------|
| 16 | [SECURITY] Hardcoded DB password trong web.xml | HIGH | security | OPEN |
| 25 | Thieu story packet cho Branch/Supplier/EmployeeAdmin | MEDIUM | tech-debt | OPEN |
| 13 | UC-2.4/3.1: Product Pricing per Branch and Unit | LOW | product | not-started |
| 12 | UC-9.1~9.7: Public Website for Guest | LOW | website | not-started |
| 11 | UC-8.2~8.8: Extended Reports | MEDIUM | report | partial |
| 10 | UC-7.1~7.5: Finance Management | LOW | finance | not-started |
| 9 | UC-6.4/6.6/6.7: Customer Loyalty and Debt | MEDIUM | customer | partial |
| 8 | UC-5.1~5.5: Inventory Management | HIGH | inventory | not-started |
| 7 | UC-4.1~4.6: Supplier and Purchase Management | HIGH | supplier | partial |
| 6 | UC-3.8/3.9: Credit Sales and Customer Debt | MEDIUM | sales | not-started |
| 5 | UC-3.6: Process Return/Exchange | MEDIUM | sales | not-started |
| 4 | UC-3.5: Print Invoice | HIGH | sales | partial |
| 3 | UC-1.7/1.8/2.1: System Configuration and Audit Log | LOW | auth | not-started |
| 2 | UC-1.6: Branch Management (CRUD) | MEDIUM | branch | DONE (close issue) |
| 1 | UC-1.5: Manage Roles and Permissions | MEDIUM | user-mgmt | not-started |

---

## Sprint Plan

### Phase Hien Tai: Stabilization (Dang Thuc Hien)

Muc tieu: On dinh code, fix security, dam bao build thanh cong.

- [x] Modular monolith architecture refactor
- [x] Eliminate hardcoded strings (AppConstants, ErrorMessages, ViewPaths)
- [x] Input validation cho Customer/Employee/Invoice
- [x] Branch CRUD module
- [x] Supplier CRUD module
- [x] Employee JSP (create/edit/detail)
- [x] Invoice detail JSP
- [x] Category delete action
- [ ] Fix #16: Remove hardcoded DB password tu web.xml
- [ ] Fix authorization gaps (AuthFilter chi check login, khong check role)

---

### Sprint 1: POS Flow Hoan Chinh + Authorization (1-2 tuan)

**Muc tieu**: Sales Staff co luong ban hang end-to-end. Enforce role-based access.

| Task | UC | Effort | Priority |
|------|----|--------|----------|
| Print Invoice (JSP + @media print) | UC-3.5 | 4h | HIGH |
| Implement role-based AuthFilter | UC-1.5 (partial) | 12h | HIGH |
| Role-based redirect sau login | - | 4h | HIGH |
| Hide menu items theo role trong header.jsp | - | 4h | MEDIUM |
| Fix #16: externalize DB credentials | - | 2h | HIGH |

**Ket qua**: POS flow hoan chinh (tim SP -> gio hang -> thanh toan -> in hoa don). Authorization enforced theo RDS matrix.

---

### Sprint 2: Purchase Orders + Inventory (3-4 tuan)

**Muc tieu**: Mo khoa Warehouse Staff workflow. Nhap hang va quan ly ton kho hoat dong.

| Task | UC | Effort | Priority |
|------|----|--------|----------|
| Purchase Order CRUD (model/DAO/service/servlet/JSP) | UC-4.2 | 16h | HIGH |
| Receive Goods (update stock on receive) | UC-4.3 | 8h | HIGH |
| Inventory Dashboard | UC-5.4 | 6h | HIGH |
| Stock Adjustment (kiem ke + dieu chinh) | UC-5.1, UC-5.2 | 6h | HIGH |
| Stock Transfer giua chi nhanh | UC-5.3 | 6h | MEDIUM |
| Low Stock Alert tren Dashboard | UC-5.5 | 2h | MEDIUM |

**Schema moi can tao:**
- PurchaseOrder, PurchaseOrderDetail
- StockAdjustment, StockTransfer, StockTransferDetail

**Ket qua**: Warehouse Staff co luong: Nhap hang -> Nhan hang -> Kiem ke -> Chuyen kho -> Canh bao het hang.

---

### Sprint 3: Customer Loyalty + Credit Sales (2 tuan)

**Muc tieu**: Hoan thien module khach hang va ban no.

| Task | UC | Effort | Priority |
|------|----|--------|----------|
| Auto-tier logic (Silver/Gold/VIP theo doanh so) | UC-6.4 | 6h | MEDIUM |
| Loyal Customer Ranking screen | UC-6.4 | 4h | MEDIUM |
| Apply Loyalty Discount trong POS | UC-6.6 | 4h | MEDIUM |
| Credit Sales Order (ban no) | UC-3.8 | 8h | MEDIUM |
| Collect Customer Debt | UC-3.9 | 6h | MEDIUM |
| View Customer Debt | UC-6.7 | 4h | MEDIUM |

**Ket qua**: He thong khach hang than thiet hoat dong. Ban no va thu cong no kha dung.

---

### Sprint 4: Extended Reports (1-2 tuan)

**Muc tieu**: Dashboard va bao cao an tuong cho demo.

| Task | UC | Effort | Priority |
|------|----|--------|----------|
| Revenue by Branch comparison UI | UC-8.2 | 4h | MEDIUM |
| Employee Sales Report | UC-8.3 | 6h | MEDIUM |
| Inventory Report | UC-8.4 | 4h | MEDIUM |
| Best-selling Product Report (man hinh rieng) | UC-8.8 | 4h | MEDIUM |

**Ket qua**: Admin/Owner co bao cao day du de ra quyet dinh.

---

### Sprint 5: Return/Exchange + Supplier Debt (2 tuan)

**Muc tieu**: Xu ly tra hang va cong no nha cung cap.

| Task | UC | Effort | Priority |
|------|----|--------|----------|
| Process Return/Exchange | UC-3.6 | 12h | MEDIUM |
| Return Goods to Supplier | UC-4.4 | 8h | MEDIUM |
| Pay Supplier Debt | UC-4.5 | 6h | MEDIUM |
| View Purchase History | UC-4.6 | 4h | MEDIUM |
| Debt Reports | UC-8.6 | 4h | LOW |

**Ket qua**: Luong tra hang va cong no NCC hoan chinh.

---

### Backlog (Neu Con Thoi Gian)

| Nhom | UC | Effort uoc tinh |
|------|----|-----------------|
| Finance Management | UC-7.1~7.5 | 40h |
| Public Website (Guest) | UC-9.1~9.7 | 30h |
| System Config + Audit Log | UC-1.7, 1.8, 2.1 | 20h |
| Multi-branch Pricing | UC-2.4 | 12h |
| Unit Management | UC-3.1 | 8h |
| Profit/Loss + Loyalty Reports | UC-8.5, 8.7 | 12h |

---

## Tien Do Du Kien

> Effort da bao gom approval workflow (+18h). Chi tiet: docs/planning/APPROVAL_WORKFLOWS.md

| Giai doan | Thoi gian | UC them | Effort | % Tong (tich luy) |
|-----------|-----------|---------|--------|-------------------|
| Hien tai (done) | - | 17 done + 5 partial | - | ~37% |
| Sprint 1: POS + Auth | 1-2 tuan | +2 | 26h | ~42% |
| Sprint 2: Purchase + Inventory | 3-4 tuan | +6 | 52h (+8h workflow) | ~55% |
| Sprint 3: Loyalty + Credit | 2 tuan | +5 | 38h (+6h workflow) | ~65% |
| Sprint 4: Reports | 1-2 tuan | +4 | 18h | ~72% |
| Sprint 5: Return + Supplier Debt | 2 tuan | +5 | 38h (+4h workflow) | ~82% |
| Backlog | 4-6 tuan | +12 | ~120h | 100% |

---

## Rui Ro va Giai Phap

| Rui ro | Muc do | Giai phap |
|--------|--------|-----------|
| Authorization la Hard Gate (CLAUDE.md) | CAO | Tao high-risk story, yeu cau human confirmation |
| Schema migration (PurchaseOrder, Stock tables) | TRUNG BINH | Test tren DB dev, backup truoc khi chay |
| Thieu thoi gian cho tat ca sprint | CAO | Uu tien Sprint 1-3, cat Sprint 5 + Backlog |
| DB password hardcoded (issue #16) | CAO | Chuyen sang env variable hoac JNDI |
| Khong co automated tests | TRUNG BINH | Them unit test cho auth + DAO layer |

---

## Cac Van De Can Xac Nhan (Open Questions)

1. Guest flow (Website UC-9.x) co nam trong scope bat buoc cua SWP391 khong?
2. Finance module (UC-7.x) co can cho demo cuoi ky khong?
3. Ma tran phan quyen: Admin khong co quyen Edit/Delete Product - dung y dinh?
4. Owner khong co quyen Add Customer - dung y dinh?
5. Warehouse Staff khong truy cap website pages - logic hay loi?
6. Issue #2 (Branch Management) da xong - can close tren GitHub?
