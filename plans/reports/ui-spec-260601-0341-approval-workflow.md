# UI Spec — Approval Workflow (Issue #37)

> Mô tả UI để tích hợp `ApprovalService` vào module Purchase Order (mẫu tham khảo, dùng được cho mọi module nghiệp vụ khác).
>
> Audience: developer FE/BE tự tạo JSP + servlet rồi E2E.

---

## 1. Tổng quan

`ApprovalService` là tầng nền — không có UI riêng. UI sẽ được chèn vào màn **chi tiết chứng từ** của từng module (Purchase Order trước, sau đó Stock Transfer / Stock Adjustment / Credit Sales / Order Return / Payment Voucher).

3 thành phần UI cần có:

1. **Status badge** — hiển thị `DocumentStatus` ở danh sách & chi tiết
2. **Action buttons** — Submit / Approve / Reject / Cancel (hiển thị có điều kiện theo `canSubmit/canApprove/canReject/canCancel`)
3. **Approval history timeline** — JSP partial `approval-history.jsp` (tái sử dụng được)

---

## 2. Status Badge (component dùng chung)

File mới: `src/main/webapp/WEB-INF/views/common/status-badge.jsp`

| DocumentStatus | Label hiển thị | Màu nền | Màu chữ |
|----------------|----------------|---------|---------|
| DRAFT | Nháp | `#e5e7eb` | `#374151` |
| PENDING_APPROVAL | Chờ duyệt | `#fef3c7` | `#92400e` |
| APPROVED | Đã duyệt | `#d1fae5` | `#065f46` |
| REJECTED | Đã từ chối | `#fee2e2` | `#991b1b` |
| IN_PROGRESS | Đang xử lý | `#dbeafe` | `#1e40af` |
| RECEIVING | Đang nhập kho | `#dbeafe` | `#1e40af` |
| COMPLETED | Hoàn thành | `#d1fae5` | `#065f46` |
| FINALIZED | Đã chốt | `#e0e7ff` | `#3730a3` |
| CANCELLED | Đã hủy | `#f3f4f6` | `#6b7280` |

**Input**: param `status` (String).
**Output**: `<span class="kr-status-badge" data-status="${status}">…</span>`.

Cách dùng:
```jsp
<jsp:include page="../common/status-badge.jsp">
    <jsp:param name="status" value="${order.status}" />
</jsp:include>
```

---

## 3. Action Buttons (chi tiết chứng từ)

File: `src/main/webapp/WEB-INF/views/purchase/purchase-detail.jsp` (sửa khu page-header).

### 3.1 Logic hiển thị

Servlet truyền vào request 4 boolean flags (đã gọi `ApprovalService.canXxx(...)`):
- `canSubmit`
- `canApprove`
- `canReject`
- `canCancel`

JSP chỉ render nút khi flag tương ứng = `true`.

### 3.2 Nút và behavior

| Nút | Điều kiện hiển thị | Màu | Hành vi khi click |
|-----|---------------------|-----|---------------------|
| **Gửi duyệt** (Submit) | `canSubmit` | xanh dương `#2563eb` | POST `action=submit` → status DRAFT → PENDING_APPROVAL |
| **Duyệt** (Approve) | `canApprove` | xanh lá `#10b981` | POST `action=approve` → PENDING_APPROVAL → APPROVED |
| **Từ chối** (Reject) | `canReject` | đỏ `#ef4444` | Mở **modal nhập lý do** rồi POST `action=reject` + `reason=...` |
| **Hủy phiếu** (Cancel) | `canCancel` | xám `#6b7280` | Mở **modal nhập lý do** rồi POST `action=cancel` + `reason=...` |

### 3.3 Modal "Nhập lý do" (dùng chung cho Reject / Cancel)

File mới: `src/main/webapp/WEB-INF/views/common/reason-modal.jsp`

- Tiêu đề: `Lý do từ chối` hoặc `Lý do hủy phiếu` (truyền qua param `title`)
- Textarea `reason` (required, minlength=5, maxlength=500)
- Hiển thị warning nếu textarea trống
- 2 button: "Xác nhận" (submit form), "Hủy" (đóng modal)
- Trigger bằng JS thuần (no jQuery), data-attribute `data-action-target`

### 3.4 Form HTML mẫu (Submit button)

```html
<c:if test="${canSubmit}">
  <form method="post" action="${ctx}/admin/purchases/approval" style="display:inline;"
        onsubmit="return confirm('Gửi duyệt phiếu nhập này?');">
    <input type="hidden" name="action" value="submit" />
    <input type="hidden" name="documentType" value="PURCHASE_ORDER" />
    <input type="hidden" name="documentId" value="${order.purchaseOrderId}" />
    <button type="submit" class="kr-btn kr-btn-primary"
            data-testid="btn-submit-approval">
      Gửi duyệt
    </button>
  </form>
</c:if>
```

> **Lưu ý cho E2E**: tất cả các nút PHẢI có `data-testid` để Playwright/agent-browser bám:
> - `btn-submit-approval`
> - `btn-approve`
> - `btn-reject`
> - `btn-cancel`
> - `modal-reason-input`
> - `modal-reason-confirm`
> - `modal-reason-cancel`
> - `approval-history-row` (mỗi row trong timeline)

---

## 4. Approval History Timeline

File mới: `src/main/webapp/WEB-INF/views/common/approval-history.jsp`

### 4.1 Input

Servlet `request.setAttribute("approvalHistory", List<ApprovalHistory>)` trước khi forward.

### 4.2 Layout

Card "Lịch sử duyệt" đặt cuối trang chi tiết, dưới bảng item.

Mỗi row hiển thị (mới nhất trên cùng):

```
[icon by action] [Action label]  • [performedByName]  • [createdAt]
                 [from] → [to]
                 Lý do: [reason]   (chỉ hiện khi có reason)
```

### 4.3 Mapping action → icon + label

| ApprovalAction | Icon (text/emoji thay thế nếu chưa có icon font) | Label |
|----------------|---------------------------------------------------|-------|
| CREATE | `+` | Tạo phiếu |
| SUBMIT | `→` | Gửi duyệt |
| APPROVE | `✓` | Duyệt |
| REJECT | `✗` | Từ chối |
| CANCEL | `⊘` | Hủy phiếu |
| COMPLETE | `●` | Hoàn thành |
| RECEIVE | `↓` | Nhập kho |
| SHIP | `↑` | Xuất kho |
| FINALIZE | `★` | Chốt phiếu |

> Project chưa dùng icon font → dùng ký tự unicode hoặc style CSS `::before`.

### 4.4 Markup

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="kr-card" data-testid="approval-history">
  <div class="kr-card-header">
    <h3>Lịch sử duyệt</h3>
  </div>
  <div class="kr-card-body">
    <c:choose>
      <c:when test="${empty approvalHistory}">
        <p style="color:#6b7280;font-style:italic;">Chưa có lịch sử duyệt.</p>
      </c:when>
      <c:otherwise>
        <ul class="kr-timeline">
          <c:forEach var="h" items="${approvalHistory}">
            <li class="kr-timeline-item" data-testid="approval-history-row"
                data-action="${h.action}">
              <span class="kr-timeline-icon kr-action-${h.action}">…</span>
              <div class="kr-timeline-body">
                <div class="kr-timeline-title">
                  <strong>${h.action}</strong>
                  <span> • </span>
                  <span>${h.performedByName}</span>
                  <span> • </span>
                  <fmt:formatDate value="${h.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                </div>
                <div class="kr-timeline-transition">
                  <c:if test="${not empty h.fromStatus}">
                    <span>${h.fromStatus}</span> → 
                  </c:if>
                  <span>${h.toStatus}</span>
                </div>
                <c:if test="${not empty h.reason}">
                  <div class="kr-timeline-reason">
                    Lý do: <c:out value="${h.reason}" />
                  </div>
                </c:if>
              </div>
            </li>
          </c:forEach>
        </ul>
      </c:otherwise>
    </c:choose>
  </div>
</div>
```

### 4.5 Cách dùng

```jsp
<jsp:include page="../common/approval-history.jsp" />
```

---

## 5. Servlet & Routing

### 5.1 Servlet mới (đề xuất)

`com.kiotretail.purchase.controller.PurchaseApprovalServlet`

- URL pattern: `/admin/purchases/approval`
- Method `doPost` đọc:
  - `action` ∈ {submit, approve, reject, cancel}
  - `documentId` (int)
  - `documentType` = "PURCHASE_ORDER"
  - `reason` (required khi action=reject hoặc cancel)
- Workflow:
  1. Load document, lấy `currentStatus`
  2. Check authorization qua `ApprovalService.canXxx(...)`
  3. Nếu không pass → flash error, redirect lại detail
  4. Nếu pass → update status trong PurchaseOrder DAO + gọi `approvalService.logTransition(...)`
  5. Flash success → redirect `/admin/purchases?id=...`

### 5.2 Sửa servlet hiện có

`PurchaseServlet#doGet(detail)`:
- Sau khi load order, gọi:
  ```java
  DocumentStatus current = DocumentStatus.fromString(order.getStatus());
  String role = (String) session.getAttribute(AppConstants.SESSION_ROLE_NAME);
  int userId = ((Employee) session.getAttribute(AppConstants.SESSION_EMPLOYEE)).getEmployeeId();
  boolean isOwner = order.getCreatedBy() == userId;

  request.setAttribute("canSubmit",  approvalService.canSubmit(current));
  request.setAttribute("canApprove", approvalService.canApprove(current, role, order.getCreatedBy(), userId));
  request.setAttribute("canReject",  approvalService.canReject(current, role));
  request.setAttribute("canCancel",  approvalService.canCancel(current, role, isOwner));
  request.setAttribute("approvalHistory",
          approvalHistoryDAO.getByDocument("PURCHASE_ORDER", order.getPurchaseOrderId()));
  ```

---

## 6. Migration SQL (đi kèm để chạy được)

File mới: `sql/migrations/V37__approval_history.sql`

```sql
-- Bảng lịch sử duyệt dùng chung
CREATE TABLE ApprovalHistory (
    HistoryID     INT IDENTITY(1,1) PRIMARY KEY,
    DocumentType  VARCHAR(50) NOT NULL,
    DocumentID    INT          NOT NULL,
    FromStatus    VARCHAR(30)  NULL,
    ToStatus      VARCHAR(30)  NOT NULL,
    Action        VARCHAR(20)  NOT NULL,
    PerformedBy   INT          NOT NULL,
    Reason        NVARCHAR(500) NULL,
    CreatedAt     DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_ApprovalHistory_Employee
        FOREIGN KEY (PerformedBy) REFERENCES Employee(EmployeeID)
);

CREATE INDEX IX_ApprovalHistory_Document
    ON ApprovalHistory(DocumentType, DocumentID, CreatedAt DESC);

-- Cột approval cho PurchaseOrder
ALTER TABLE PurchaseOrder ADD
    SubmittedBy   INT NULL,
    SubmittedAt   DATETIME2 NULL,
    ApprovedBy    INT NULL,
    ApprovedAt    DATETIME2 NULL,
    RejectReason  NVARCHAR(500) NULL;

-- Lặp lại cho StockTransfer / StockAdjustment / CreditSale / OrderReturn / PaymentVoucher
```

---

## 7. E2E Test Plan (sau khi UI xong)

### 7.1 Setup data

- 1 Owner: `owner@test.com` / mật khẩu test
- 1 StoreManager: `manager@test.com`
- 1 SalesStaff: `staff@test.com`
- 1 PurchaseOrder DRAFT do staff tạo

### 7.2 Test scenarios

| ID | Vai trò | Hành động | Kết quả mong đợi |
|----|---------|-----------|-------------------|
| E2E-01 | Staff | Mở chi tiết PO DRAFT | Thấy nút "Gửi duyệt", "Hủy phiếu"; KHÔNG có "Duyệt"/"Từ chối"; status badge "Nháp" |
| E2E-02 | Staff | Click "Gửi duyệt" → confirm | Status badge → "Chờ duyệt"; history thêm row SUBMIT |
| E2E-03 | Staff | Mở lại PO PENDING_APPROVAL | KHÔNG thấy nút "Duyệt" (segregation of duties) |
| E2E-04 | Manager | Mở PO PENDING_APPROVAL | Thấy "Duyệt" và "Từ chối" |
| E2E-05 | Manager | Click "Từ chối" mà không nhập lý do | Modal hiện validation, KHÔNG submit |
| E2E-06 | Manager | "Từ chối" với reason "Sai giá" | Status → "Đã từ chối"; history có row REJECT + reason |
| E2E-07 | Owner | Mở PO REJECTED | KHÔNG có nút Submit/Approve/Reject; có thể Cancel |
| E2E-08 | Manager | Mở PO PENDING_APPROVAL khác → "Duyệt" | Status → "Đã duyệt" |
| E2E-09 | Manager | Mở PO APPROVED | KHÔNG thấy "Hủy phiếu" (chỉ Owner mới hủy được sau khi approved) |
| E2E-10 | Owner | Mở PO APPROVED → "Hủy phiếu" với reason "Đối tác hủy" | Status → "Đã hủy"; history có row CANCEL |
| E2E-11 | Bất kỳ | Mở PO COMPLETED | Không có action button nào |
| E2E-12 | Bất kỳ | Kiểm tra timeline | Hiện đủ rows theo thứ tự mới nhất trên cùng, có user, ngày, lý do |

### 7.3 Selectors (cho Playwright/agent-browser)

```javascript
const SELECTORS = {
  statusBadge: '[data-testid="status-badge"]',
  btnSubmit:   '[data-testid="btn-submit-approval"]',
  btnApprove:  '[data-testid="btn-approve"]',
  btnReject:   '[data-testid="btn-reject"]',
  btnCancel:   '[data-testid="btn-cancel"]',
  reasonInput: '[data-testid="modal-reason-input"]',
  reasonOk:    '[data-testid="modal-reason-confirm"]',
  historyRows: '[data-testid="approval-history-row"]',
};
```

---

## 8. Checklist tạo UI

- [ ] `sql/migrations/V37__approval_history.sql` + chạy trên DB local
- [ ] `common/status-badge.jsp` (param `status`)
- [ ] `common/reason-modal.jsp` (param `title`, `targetFormId`)
- [ ] `common/approval-history.jsp` (đọc `approvalHistory` từ request)
- [ ] CSS bổ sung trong `assets/css/admin.css`: `.kr-status-badge`, `.kr-timeline`, `.kr-action-*`
- [ ] `PurchaseApprovalServlet` mới (4 action: submit/approve/reject/cancel)
- [ ] Sửa `PurchaseServlet` để set `canSubmit/canApprove/canReject/canCancel/approvalHistory` trước khi forward `purchase-detail.jsp`
- [ ] Sửa `purchase-detail.jsp`: thay block button hiện tại bằng action buttons mới + include `approval-history.jsp` dưới cuối
- [ ] Test thủ công: tạo 3 user (Owner, Manager, Staff), chạy đủ 12 scenario E2E ở mục 7.2
- [ ] Khi pass → kích hoạt skill `e2e-agent` chạy automation thật

---

## Câu hỏi mở (cần xác nhận trước khi code)

1. **Field `createdBy`** trong `PurchaseOrder` đã có chưa? (Cần để check self-approve)
2. **Threshold 50M** (Manager <50M, Owner ≥50M) — đã quyết wire vào logic chưa, hay để giai đoạn 2? Nếu enable → `canApprove` cần thêm tham số `BigDecimal totalAmount`.
3. **Module nào triển khai trước**: PurchaseOrder (đã có UI) hay tạo demo standalone?
4. **i18n**: project hiện đang dùng tiếng Việt **không dấu** trong JSP — UI mới có giữ pattern đó không?
