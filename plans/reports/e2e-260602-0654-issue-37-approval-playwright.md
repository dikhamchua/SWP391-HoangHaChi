# E2E Test Report - Issue #37 Approval Workflow (Playwright MCP)

- DateTime: 2026-06-02 06:54 (Asia/Bangkok)
- Issue: https://github.com/dikhamchua/SWP391-HoangHaChi/issues/37
- Server: Tomcat (cargo:run) at http://localhost:9999/kiotretail
- Tool: MCP Playwright + MySQL verification
- Branch: main

## 1. Tóm tắt

| TC | Scenario | Result |
|----|----------|--------|
| E2E-01 | Login Owner (`owner@retail.com` / `123456`) | PASS |
| E2E-02 | Pending Inbox liệt kê đúng 2 phiếu PENDING_APPROVAL | PASS |
| E2E-03 | Approval Detail hiển thị items, info, history block | PASS |
| E2E-04 | Approve PO 5M (Owner < ngưỡng) → DB cập nhật + log | PASS |
| E2E-05 | Reject PO 75M kèm reason → DB cập nhật + log + reason | PASS |
| E2E-06 | History page: 8 audit records + stat cards (3/1/0) | PASS |
| E2E-07 | History filter `historyAction=REJECT` → còn 1 dòng | PASS |
| E2E-08 | Threshold rule: Manager mở PO 60M (≥ 50M) → ẩn nút duyệt + banner cảnh báo | PASS |
| E2E-09 | Backend gate: Manager POST approve 60M → server từ chối, status giữ nguyên | PASS |

**Tổng: 9/9 PASS**

## 2. Setup data test

Tạo 3 PurchaseOrder ở trạng thái `PENDING_APPROVAL` (sau cleanup):
| ID | Code | Total | Mục đích |
|----|------|-------|----------|
| 3 | PO-E2E-260602-A | 5,000,000 | Test approve |
| 4 | PO-E2E-260602-B | 75,000,000 | Test reject + threshold |
| 5 | PO-E2E-260602-C | 60,000,000 | Test threshold rule (Manager) |

## 3. Chi tiết test cases

### E2E-01 Login
- Email: `owner@retail.com`, password `123456`
- Redirect tới `/admin/dashboard` → PASS

### E2E-02 Pending Inbox `/admin/approvals?action=pending`
- Hiển thị "Phiếu đang chờ duyệt 2"
- Bộ lọc: documentType / submitter / fromDate / toDate / keyword
- Stat: "Tổng phiếu chờ: 2"
- Screenshot: `02-pending-list.png`

### E2E-03 Approval Detail (`?action=detail&type=PURCHASE_ORDER&id=3`)
Khối hiển thị đầy đủ:
- Header: mã phiếu + status badge "Chờ duyệt"
- Thông tin chứng từ: code, type, submitter, branch, supplier, ngày tạo, ghi chú
- Bảng items: SKU, tên SP, số lượng, đơn giá, thành tiền (đúng 5,000,000)
- Lịch sử thao tác (placeholder khi chưa có)
- Aside "Quyết định phê duyệt": textarea reason + nút Phê duyệt / Từ chối

### E2E-04 Approve PO-A
Confirm dialog "Xác nhận phê duyệt phiếu này?" → accept.
Verify DB sau action:
```sql
PurchaseOrderID=3, Status=APPROVED, ApprovedBy=1, ApprovedAt=2026-06-01 23:48:57
ApprovalHistory#7: PURCHASE_ORDER/3 PENDING_APPROVAL→APPROVED action=APPROVE PerformedBy=1
```

### E2E-05 Reject PO-B kèm reason
Reason: "Vuot ngan sach quy 2, can xem xet lai"
Verify DB:
```sql
PurchaseOrderID=4, Status=REJECTED, RejectedBy=1, RejectedReason="Vuot ngan sach quy 2, can xem xet lai"
ApprovalHistory#8: PENDING_APPROVAL→REJECTED action=REJECT Reason="Vuot ngan sach quy 2..."
```

### E2E-06 History page `/admin/approvals?action=history`
- 8 dòng audit (gồm 4 record cũ + 4 record vừa tạo trong workflow)
- Stat cards: **Tổng quyết định 4 / Đã duyệt 3 / Đã từ chối 1 / Đã hủy 0**
- Filters: action / docType / approver / dateFrom / dateTo / keyword
- Screenshot: `05-history-all.png`

### E2E-07 Filter `historyAction=REJECT`
- Còn duy nhất 1 dòng `#4 Nhập kho Từ chối ... Vuot ngan sach quy 2 ...`
- Stat cards giữ nguyên (global counts) - đúng spec

### E2E-08 Threshold rule (Manager + PO 60M)
Login lại bằng `manager@retail.com`, mở `/admin/approvals?action=detail&type=PURCHASE_ORDER&id=5`:
- Aside hiển thị: "Bạn không có quyền duyệt phiếu này hoặc bạn là người gửi phiếu."
- Không có nút Phê duyệt / Từ chối (`buttonCount = 0`)
- Khớp `ApprovalService.canApprove(...)` overload 5 tham số: PO 60M ≥ ngưỡng 50M, Manager bị chặn (THR-002)

### E2E-09 Backend gate (defense-in-depth)
POST trực tiếp với fetch:
```
POST /admin/approvals  body: action=approve&documentType=PURCHASE_ORDER&documentId=5
→ 200 redirect to ?action=pending
DB sau POST: PurchaseOrderID=5, Status=PENDING_APPROVAL, ApprovedBy=NULL
```
Server không cập nhật status → backend ApprovalService gate hoạt động đúng dù bypass UI.

## 4. Screenshots

`plans/reports/screenshots/issue-37-e2e/`:
- `01-pending-empty.png` - inbox trước khi seed
- `02-pending-list.png` - inbox với 2 phiếu chờ
- `03-detail-pending.png` - detail view phiếu A
- `04-after-decisions.png` - inbox sau approve A, còn lại B
- `05-history-all.png` - history page với 8 records + stat cards
- `06-history-filter-reject.png` - filter REJECT
- `07-manager-blocked.png` - Manager xem PO 60M, bị ẩn nút duyệt

## 5. Vấn đề phát hiện (UI/UX)

### 5.1 BUG (severity: medium) - UTF-8 mojibake tên người dùng
Tất cả tên tiếng Việt trên navbar/dropdown/cell đều bị mojibake:
- `Nguyễn Hoàng Owner` → `Nguyá»…n HoÃ ng Owner`
- `Lê Minh Sales` → `LÃª Minh Sales`
- `Chi Nhánh Hà Nội` → `Chi NhÃ¡nh HÃ Ná»™i`
- `Công Ty Coca Cola Việt Nam` → `CÃ´ng Ty Coca Cola Viá»‡t Nam`

**Nguyên nhân nghi ngờ**: thiếu `ResultSet getString` UTF-8 hoặc connector chưa set `useUnicode=true&characterEncoding=UTF-8` trên đường đọc Employee/Branch/Supplier (mặc dù `DatabaseUtil` URL đã có sẵn). Cũng có thể do JSP chưa khai báo `<%@ page contentType="text/html;charset=UTF-8" %>` nhất quán.

**Impact**: Hiển thị sai trên mọi screen. Filter "submitter" / "approver" trong combobox cũng bị mojibake.

**Đề xuất**: kiểm tra response Content-Type header + JSP page directive + JDBC URL trên đường đọc danh sách Employee.

### 5.2 BUG (severity: low) - Console errors trên trang home
2 console errors xuất hiện khi mở `/`. Chưa root-cause vì không trong scope #37, đề xuất tách issue riêng.

### 5.3 OBS - Lịch sử thao tác trên detail không reload sau decision
Sau khi approve/reject, redirect về pending list (PRG flow), không quay lại detail nên user không thấy lịch sử transition mới ngay tức thì. Có thể là design intent (clear queue), không tính là bug.

## 6. Cleanup

Sau test đã xóa data tạm:
```sql
DELETE FROM PurchaseOrderDetail WHERE PurchaseOrderID IN (3,4,5);
DELETE FROM ApprovalHistory WHERE DocumentID IN (3,4,5) AND DocumentType='PURCHASE_ORDER';
DELETE FROM PurchaseOrder WHERE OrderCode LIKE 'PO-E2E-%';
```

## 7. Kết luận

Backend ApprovalService + UI Approval (Pending / Detail / History) **HOẠT ĐỘNG ĐÚNG** theo Acceptance Criteria của issue #37:
- State machine: DRAFT → PENDING_APPROVAL → APPROVED/REJECTED đúng
- Audit log: ApprovalHistory ghi đầy đủ for/to/action/reason/performer
- Authorization: phân quyền theo role + segregation of duties + amount threshold đúng (cả frontend lẫn backend)
- Confirm dialogs hoạt động cho cả approve/reject

**Vấn đề tiếng Việt mojibake** là blocker cho UAT trải nghiệm thật, cần fix trước khi đóng issue.

## 8. Câu hỏi mở

- Mojibake là issue toàn cục hay chỉ cục bộ Approval module? Có nên tách issue riêng để fix?
- Có cần test thêm role SalesStaff/WarehouseStaff bị chặn truy cập `/admin/approvals` không?
- Test cancel flow chưa được kiểm tra qua UI (UI hiện không có nút cancel)?
