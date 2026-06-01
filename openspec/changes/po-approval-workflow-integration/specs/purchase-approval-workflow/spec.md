## ADDED Requirements

### Requirement: Purchase Order State Machine

The system SHALL enforce the following state transitions for Purchase Orders, where each transition MUST be logged to `ApprovalHistory` with the acting employee, timestamp, and (for negative transitions) a reason.

Allowed transitions:
- `DRAFT → PENDING_APPROVAL` (action: SUBMIT)
- `DRAFT → CANCELLED` (action: CANCEL, by creator or approver)
- `PENDING_APPROVAL → APPROVED` (action: APPROVE)
- `PENDING_APPROVAL → REJECTED` (action: REJECT, requires reason)
- `PENDING_APPROVAL → CANCELLED` (action: CANCEL, by creator or approver, requires reason)
- `APPROVED → RECEIVING` (action: RECEIVE_PARTIAL)
- `APPROVED → COMPLETED` (action: RECEIVE_FULL)
- `APPROVED → CANCELLED` (action: CANCEL, Owner only, requires reason)
- `RECEIVING → COMPLETED` (action: RECEIVE_FULL when last line filled)
- `RECEIVING → CANCELLED` (action: CANCEL, Owner only, requires reason)

Any transition not listed MUST be rejected with HTTP 400 / `ValidationException`.

#### Scenario: Submit a draft PO
- **WHEN** the creator clicks "Gửi duyệt" on a `DRAFT` PO
- **THEN** the system sets `status = PENDING_APPROVAL`, `submittedAt = NOW()`, and writes an `ApprovalHistory` row with `action = SUBMIT`, `fromStatus = DRAFT`, `toStatus = PENDING_APPROVAL`, `performedBy = creatorId`

#### Scenario: Approve a pending PO
- **WHEN** an approver (different from creator, with role `Owner` or `StoreManager`) clicks "Duyệt" on a `PENDING_APPROVAL` PO whose total < 50M
- **THEN** the system sets `status = APPROVED`, `approvedBy = approverId`, `approvedAt = NOW()`, and logs `action = APPROVE`

#### Scenario: Approve high-value PO requires Owner
- **WHEN** an approver with role `StoreManager` attempts to approve a PO with `totalAmount >= 50_000_000`
- **THEN** the system rejects with `ValidationException` "Đơn hàng từ 50 triệu trở lên cần Owner duyệt"

#### Scenario: Reject without reason fails
- **WHEN** an approver submits a reject action with empty or whitespace-only reason
- **THEN** the system returns 400 with message "Lý do từ chối là bắt buộc" and does NOT change the status

#### Scenario: Invalid transition fails
- **WHEN** any role attempts to transition a `COMPLETED` PO to `APPROVED`
- **THEN** the system rejects with `ValidationException` "Trạng thái không cho phép thao tác này"

---

### Requirement: Separation of Duties

The system SHALL prevent a creator from approving their own Purchase Order.

#### Scenario: Creator cannot approve own PO
- **WHEN** the user who created a PO (matched by `createdBy = sessionEmployeeId`) tries to call the approve action
- **THEN** the system rejects with HTTP 403 / `ValidationException` "Người tạo không được duyệt phiếu của mình" and does NOT change status

#### Scenario: Creator can still cancel own draft
- **WHEN** the creator cancels their own `DRAFT` PO with a valid reason
- **THEN** the system sets `status = CANCELLED` and logs the transition

---

### Requirement: Threshold-Based Approval Authority

The system SHALL gate approval based on `totalAmount` and `userRole`:
- `totalAmount < 50_000_000`: any role in {`Owner`, `StoreManager`} may approve
- `totalAmount >= 50_000_000`: only `Owner` may approve

The threshold value MUST come from `AppConstants.OWNER_APPROVAL_THRESHOLD` and MUST NOT be hardcoded in JSPs.

#### Scenario: Manager approves under threshold
- **WHEN** a `StoreManager` approves a PO with `totalAmount = 30_000_000`
- **THEN** the approval succeeds

#### Scenario: Manager blocked above threshold
- **WHEN** a `StoreManager` attempts to approve a PO with `totalAmount = 60_000_000`
- **THEN** the approval is rejected and the UI displays "Đơn hàng cần Owner duyệt"

#### Scenario: Owner approves any amount
- **WHEN** an `Owner` approves a PO with any `totalAmount`
- **THEN** the approval succeeds

---

### Requirement: Partial Receiving

The system SHALL support receiving a Purchase Order across multiple sessions, tracking per-line `ReceivedQuantity` and updating product stock atomically.

#### Scenario: Receive part of a line
- **WHEN** the user submits `receivedQty = 5` for a line where `quantity = 10` and current `receivedQuantity = 0`
- **THEN** the system sets the line's `receivedQuantity = 5`, increases `Product.stockQuantity` by 5, sets `PurchaseOrder.status = RECEIVING`, and logs `action = RECEIVE_PARTIAL`

#### Scenario: Complete the order via final receive
- **WHEN** the user submits `receivedQty` values that bring every line to `receivedQuantity = quantity`
- **THEN** the system sets `status = COMPLETED`, `completedAt = NOW()`, and logs `action = RECEIVE_FULL`

#### Scenario: Over-receive is rejected
- **WHEN** the user submits a `receivedQty` that would cause `receivedQuantity > quantity` on any line
- **THEN** the system rejects the entire submission with `ValidationException` "Số lượng nhận vượt quá số đặt"; no partial update is committed

#### Scenario: Failure during receive rolls back
- **WHEN** the stock-update SQL on line N+1 fails after lines 1..N succeeded in the same transaction
- **THEN** the system rolls back the transaction; neither `receivedQuantity` nor `Product.stockQuantity` changes for any line

---

### Requirement: Approval Inbox and History (real data)

The system SHALL render the approval inbox (`/admin/approvals?action=pending`) and approval history (`/admin/approvals?action=history`) using live database queries — no mock data.

The pending inbox SHALL list all documents with `status = PENDING_APPROVAL` across supported document types (initially `PURCHASE_ORDER` only). The history SHALL list `ApprovalHistory` rows with optional filters by document type, action, performer, and date range.

#### Scenario: Pending inbox shows real PO
- **WHEN** an approver navigates to `/admin/approvals?action=pending` and a PO exists with `status = PENDING_APPROVAL`
- **THEN** the inbox lists the PO with its `orderCode`, submitter name, submitted date, and total

#### Scenario: Empty pending inbox
- **WHEN** there are no documents in `PENDING_APPROVAL` matching the filters
- **THEN** the inbox renders the empty-state component "Không có phiếu chờ duyệt"

#### Scenario: History filter by action
- **WHEN** the user filters history by `action = REJECT`
- **THEN** only rows where `ApprovalHistory.action = 'REJECT'` are returned

---

### Requirement: Conditional Action Buttons by Status and Role

The system SHALL render action buttons in `purchase-detail.jsp` based on the current `status` and the session user's `role`. The same gates MUST be re-checked server-side on POST.

| Status | Buttons visible | Visible to |
|---|---|---|
| `DRAFT` | Sửa, Gửi duyệt, Hủy | Creator only |
| `PENDING_APPROVAL` | Duyệt, Từ chối, Hủy | Approvers (Owner / StoreManager) |
| `APPROVED` | Nhận hàng, Hủy | Warehouse / Owner |
| `RECEIVING` | Nhận tiếp, Hủy | Warehouse / Owner |
| `COMPLETED` | (none — read only) | Anyone |
| `REJECTED` | (none — read only) | Anyone |
| `CANCELLED` | (none — read only) | Anyone |

#### Scenario: Creator sees edit on draft
- **WHEN** the creator views their own `DRAFT` PO
- **THEN** the page renders "Sửa", "Gửi duyệt", and "Hủy" buttons

#### Scenario: Approver sees approve/reject
- **WHEN** a `StoreManager` (not the creator) views a `PENDING_APPROVAL` PO
- **THEN** the page renders "Duyệt", "Từ chối", "Hủy" buttons

#### Scenario: Completed PO is read-only
- **WHEN** any role views a `COMPLETED` PO
- **THEN** no action buttons are rendered; only the "In phiếu" link is available

---

### Requirement: Approval Timeline

The system SHALL render a chronological timeline of `ApprovalHistory` entries on `purchase-detail.jsp`, showing each transition with action, performer name, timestamp, and reason (when present).

#### Scenario: Timeline shows submit + approve
- **WHEN** a PO has been submitted and approved
- **THEN** the timeline shows two entries in chronological order: SUBMIT (DRAFT→PENDING_APPROVAL) and APPROVE (PENDING_APPROVAL→APPROVED), each with the actor's full name and `dd/MM/yyyy HH:mm` timestamp

#### Scenario: Timeline shows reject reason
- **WHEN** a PO was rejected with reason "Sai số lượng"
- **THEN** the timeline displays the reason text under the REJECT entry

---

### Requirement: Pending Approval Badge in Navbar

The system SHALL display a badge in the navbar showing the count of pending approvals visible to the current user. The count MUST refresh on every request for users with approver roles.

#### Scenario: Badge shown for approvers
- **WHEN** an `Owner` or `StoreManager` loads any admin page and 5 POs are in `PENDING_APPROVAL`
- **THEN** the navbar "Phê duyệt" tab shows a red badge "5"

#### Scenario: Badge hidden for non-approvers
- **WHEN** a `Cashier` loads any admin page
- **THEN** the "Phê duyệt" tab is not rendered

#### Scenario: Badge updates after approval
- **WHEN** an approver approves a PO and is redirected back to the dashboard
- **THEN** the badge count is decremented by 1
