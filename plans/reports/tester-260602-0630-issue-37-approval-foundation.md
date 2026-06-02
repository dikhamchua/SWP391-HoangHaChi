# Test Report - Issue #37 Approval Workflow Foundation

- Date: 2026-06-02 06:30 (Asia/Bangkok)
- Issue: https://github.com/dikhamchua/SWP391-HoangHaChi/issues/37
- Tester: tester (re-run by main agent)
- Branch: main

## 1. Tóm tắt

| Test Class | Tests | Pass | Fail | Skip | Time |
|---|---|---|---|---|---|
| `ApprovalServiceTest` | 60 | 60 | 0 | 0 | 0.100s |
| `ApprovalServiceThresholdTest` | 4 | 4 | 0 | 0 | 0.006s |
| **Total** | **64** | **64** | **0** | **0** | **~0.11s** |

Result: **BUILD SUCCESS** - toàn bộ 64/64 test xanh.

Lệnh chạy:
```
mvn test -Dtest='ApprovalServiceTest,ApprovalServiceThresholdTest'
```

## 2. Coverage hiện trạng

### 2.1 Core test cases (APPR-001..APPR-013) - 13/13 pass
- canSubmit (DRAFT vs non-DRAFT)
- canApprove (PENDING_APPROVAL + role + segregation of duties)
- canReject (PENDING_APPROVAL + role)
- canCancel (DRAFT/PENDING/APPROVED + isOwner)
- logTransition (REJECT/CANCEL bắt buộc reason, valid -> insert)

### 2.2 Edge cases (APPR-014..APPR-060) - 47/47 pass
- canSubmit: null/APPROVED/REJECTED/CANCELLED/COMPLETED -> false
- canApprove: status null, role null/empty/lowercase, role WAREHOUSE_STAFF -> false
- canReject: status null, role null -> false (no NPE)
- canCancel: COMPLETED/CANCELLED/REJECTED/FINALIZED + matrix role x status
- logTransition: null/blank docType, null toStatus, null action, performedBy <= 0, blank reason
- DocumentStatus.fromString case-insensitive + null/blank/unknown

### 2.3 Threshold tests (THR-001..THR-004) - 4/4 pass
Bonus suite cho overload `canApprove` 5 tham số kiểm tra ngưỡng `OWNER_APPROVAL_THRESHOLD`:
- Manager + dưới ngưỡng -> true
- Manager + tại ngưỡng -> false (chỉ Owner duyệt)
- Owner + trên ngưỡng -> true
- Self-approve (creator == approver) -> false

## 3. Database verification

### 3.1 Bảng `ApprovalHistory`
```
HistoryID      int          PK auto_increment
DocumentType   varchar(50)  NOT NULL  (idx)
DocumentID     int          NOT NULL  (idx composite)
FromStatus     varchar(30)  NOT NULL
ToStatus       varchar(30)  NOT NULL
Action         varchar(30)  NOT NULL
PerformedBy    int          NOT NULL  FK -> Employee
Reason         varchar(500) NULL
CreatedAt      datetime     DEFAULT CURRENT_TIMESTAMP
```
Indexes: `PRIMARY`, `IX_ApprovalHistory_Document(DocumentType, DocumentID)`, `FK_ApprovalHistory_Employee(PerformedBy)`.
Số dòng hiện có: 6 (đã có dữ liệu mẫu của PURCHASE_ORDER).

### 3.2 Cột approval trên bảng nghiệp vụ
| Bảng | Status | SubmittedBy | SubmittedAt | ApprovedBy | ApprovedAt | RejectReason |
|---|---|---|---|---|---|---|
| PurchaseOrder | OK | - | OK | OK | OK | - |
| StockTransfer | OK | - | OK | OK | OK | - |
| StockAdjustment | OK | - | OK | OK | OK | - |

Lưu ý: `SubmittedBy` và `RejectReason` chưa có trên 3 bảng nghiệp vụ - thiếu so với Acceptance Criteria.

## 4. Acceptance Criteria - rà soát

| AC | Trạng thái | Ghi chú |
|---|---|---|
| Tạo migration SQL bảng `ApprovalHistory` + cột status | Đạt một phần | Bảng OK; thiếu `SubmittedBy` & `RejectReason` ở các bảng nghiệp vụ |
| Implement `DocumentStatus`, `DocumentType` enums | Một phần | `DocumentStatus` có; `DocumentType` enum riêng chưa thấy - dùng `String docType` truyền vào `logTransition` |
| Implement `ApprovalService` + Impl | Đạt | `ApprovalService` (concrete class, không tách interface) |
| `ApprovalHistoryDAO` (insert + truy vấn theo docType+docId) | Đạt | + thêm `search`, `countSearch`, `countByAction` |
| JSP partial `approval-history.jsp` reusable | Cần kiểm tra | Tách ra task UI #45 |
| Unit test `ApprovalService` (transition hợp lệ + invalid) | Đạt | 64/64 pass |
| Document trong `docs/decisions/` & `docs/ARCHITECTURE.md` | Cần kiểm tra | Chưa rà |

## 5. Vấn đề phát hiện

1. **`DocumentType` enum chưa tồn tại** - mã hiện tại truyền `String` (vd `"PURCHASE_ORDER"`) thay vì enum cứng. Risk: typo runtime, không đồng nhất.
2. **Thiếu cột `SubmittedBy`, `RejectReason`** trên `PurchaseOrder/StockTransfer/StockAdjustment`. Cần migration bổ sung trước khi UI #45 hook workflow.
3. **`ApprovalService` không có interface tách rời** - khác với AC `Interface ApprovalService voi method...`. Hiện là concrete class. Nếu chấp nhận thay đổi thiết kế, cập nhật tài liệu cho khớp.
4. **Test logTransition dùng reflection inject stub DAO** - hoạt động nhưng dễ vỡ khi đổi tên field. Có thể cân nhắc constructor injection cho dễ test hơn.

## 6. Kết luận

Backend foundation **PASS** - tất cả 64 test xanh, schema DB khớp `ApprovalHistory` và logic business rules đã chính xác (status transitions, role gates, segregation of duties, threshold rule).

Phần UI/JSP và hook workflow vào `PurchaseService/InventoryService` đã tách thành issue #45 (UI Pending Inbox / Detail / History) - chưa nằm trong scope re-test này.

## 7. Câu hỏi mở

- Có nên bổ sung `DocumentType` enum để thay thế raw `String` trong `logTransition` không?
- Migration thêm `SubmittedBy` và `RejectReason` lên các bảng nghiệp vụ - làm trong issue này hay tách phụ?
- Phần `docs/decisions/` & `docs/ARCHITECTURE.md` - có cần `docs-manager` rà cập nhật không?
