## ADDED Requirements

### Requirement: PurchaseOrderStatus enum defines purchase lifecycle states
The system SHALL provide a `PurchaseOrderStatus` enum in `purchase/constant/` with exactly these values: DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, RECEIVING, COMPLETED, CANCELLED. The enum SHALL include a `fromString(String)` static method that returns null for null/blank/unknown input (case-insensitive parsing).

#### Scenario: Parse valid status string
- **WHEN** `PurchaseOrderStatus.fromString("PENDING_APPROVAL")` is called
- **THEN** returns `PurchaseOrderStatus.PENDING_APPROVAL`

#### Scenario: Parse case-insensitive status
- **WHEN** `PurchaseOrderStatus.fromString("draft")` is called
- **THEN** returns `PurchaseOrderStatus.DRAFT`

#### Scenario: Parse null or unknown status
- **WHEN** `PurchaseOrderStatus.fromString(null)` or `PurchaseOrderStatus.fromString("INVALID")` is called
- **THEN** returns `null`

### Requirement: PurchaseOrderAction enum defines purchase transition actions
The system SHALL provide a `PurchaseOrderAction` enum in `purchase/constant/` with values: CREATE, SUBMIT, APPROVE, REJECT, CANCEL, RECEIVE, COMPLETE.

#### Scenario: Action enum values exist
- **WHEN** code references `PurchaseOrderAction.APPROVE`
- **THEN** the enum value is available and its `name()` returns `"APPROVE"`

### Requirement: PurchaseOrder model returns PurchaseOrderStatus
The `PurchaseOrder.getStatusEnum()` method SHALL return `PurchaseOrderStatus` instead of `DocumentStatus`. The method SHALL use `PurchaseOrderStatus.fromString(this.status)` internally.

#### Scenario: Model returns typed status
- **WHEN** a PurchaseOrder has status string `"APPROVED"`
- **THEN** `getStatusEnum()` returns `PurchaseOrderStatus.APPROVED`

#### Scenario: Model with null status
- **WHEN** a PurchaseOrder has null status
- **THEN** `getStatusEnum()` returns `null`

### Requirement: PurchaseOrderHistory model stores transition audit records
The system SHALL provide a `PurchaseOrderHistory` POJO in `purchase/model/` with fields: historyId (int), purchaseOrderId (int), fromStatus (String, nullable), toStatus (String), action (String), performedBy (int), reason (String, nullable), createdAt (Timestamp). An optional joined field `performedByName` (String) SHALL be populated when DAO joins with Employee table.

#### Scenario: History model holds all transition fields
- **WHEN** a PurchaseOrderHistory is created with all fields set
- **THEN** all getters return the set values

### Requirement: PurchaseOrderHistoryDAO persists and retrieves history
The system SHALL provide `PurchaseOrderHistoryDAO` in `purchase/dao/` extending `BaseDAO` with methods: `insert(PurchaseOrderHistory)` returning generated ID or -1 on failure, and `getByOrderId(int purchaseOrderId)` returning a list of history rows ordered by CreatedAt DESC joined with Employee for performedByName.

#### Scenario: Insert history row
- **WHEN** `insert()` is called with a valid PurchaseOrderHistory
- **THEN** a row is inserted into `PurchaseOrderHistory` table and the generated HistoryID is returned

#### Scenario: Insert fails
- **WHEN** `insert()` fails (0 rows affected)
- **THEN** returns -1

#### Scenario: Get history by order ID
- **WHEN** `getByOrderId(5)` is called
- **THEN** returns all history rows for PurchaseOrderID=5, newest first, with performedByName populated

### Requirement: PurchaseService inlines canSubmit logic
The `PurchaseService` SHALL determine submit eligibility internally without calling `ApprovalService`. A purchase order can be submitted only when its status is DRAFT.

#### Scenario: Submit from DRAFT
- **WHEN** status is DRAFT
- **THEN** submit is allowed

#### Scenario: Submit from non-DRAFT
- **WHEN** status is PENDING_APPROVAL or any other non-DRAFT status
- **THEN** submit throws ValidationException

### Requirement: PurchaseService inlines canApprove logic with threshold
The `PurchaseService` SHALL determine approval eligibility internally. Rules: status MUST be PENDING_APPROVAL, approver MUST NOT be the creator (segregation of duties), approver role MUST be OWNER or STORE_MANAGER, and if totalAmount >= 50M then role MUST be OWNER.

#### Scenario: Manager approves order under 50M
- **WHEN** status is PENDING_APPROVAL, role is STORE_MANAGER, approver != creator, total < 50M
- **THEN** approve is allowed

#### Scenario: Manager attempts to approve order >= 50M
- **WHEN** status is PENDING_APPROVAL, role is STORE_MANAGER, total >= 50M
- **THEN** approve throws ValidationException (owner required)

#### Scenario: Self-approve blocked
- **WHEN** approverId == creatorId
- **THEN** approve throws ValidationException

#### Scenario: Non-approver role attempts approve
- **WHEN** role is SALES_STAFF or WAREHOUSE_STAFF
- **THEN** approve throws ValidationException

### Requirement: PurchaseService inlines canReject logic
The `PurchaseService` SHALL determine rejection eligibility internally. Rules: status MUST be PENDING_APPROVAL, role MUST be OWNER or STORE_MANAGER. Reason is required.

#### Scenario: Manager rejects pending order with reason
- **WHEN** status is PENDING_APPROVAL, role is STORE_MANAGER, reason is provided
- **THEN** reject is allowed

#### Scenario: Reject without reason
- **WHEN** reason is null or blank
- **THEN** reject throws ValidationException

### Requirement: PurchaseService inlines canCancel logic
The `PurchaseService` SHALL determine cancellation eligibility internally. Rules: DRAFT/PENDING_APPROVAL can be cancelled by creator or approver role; APPROVED/RECEIVING can only be cancelled by OWNER. Reason is required.

#### Scenario: Creator cancels DRAFT order
- **WHEN** status is DRAFT, user is creator, reason provided
- **THEN** cancel is allowed

#### Scenario: Owner cancels APPROVED order
- **WHEN** status is APPROVED, role is OWNER, reason provided
- **THEN** cancel is allowed

#### Scenario: Manager attempts to cancel APPROVED order
- **WHEN** status is APPROVED, role is STORE_MANAGER
- **THEN** cancel throws ValidationException

### Requirement: PurchaseService logs transitions to PurchaseOrderHistoryDAO
Every state transition in PurchaseService (create, submit, approve, reject, cancel, receive, complete) SHALL write a row to `PurchaseOrderHistory` via `PurchaseOrderHistoryDAO.insert()`. The shared `ApprovalService.logTransition()` SHALL NOT be called.

#### Scenario: Approve logs transition
- **WHEN** a purchase order is approved
- **THEN** a PurchaseOrderHistory row is inserted with fromStatus=PENDING_APPROVAL, toStatus=APPROVED, action=APPROVE, performedBy=approverId

#### Scenario: Log insert failure
- **WHEN** PurchaseOrderHistoryDAO.insert() returns -1
- **THEN** ServiceException is thrown

### Requirement: SQL migration creates PurchaseOrderHistory table
A migration file SHALL create table `PurchaseOrderHistory` with columns: HistoryID (PK AUTO_INCREMENT), PurchaseOrderID (FK NOT NULL), FromStatus (VARCHAR 30 NULL), ToStatus (VARCHAR 30 NOT NULL), Action (VARCHAR 30 NOT NULL), PerformedBy (FK to Employee NOT NULL), Reason (VARCHAR 500 NULL), CreatedAt (DATETIME DEFAULT CURRENT_TIMESTAMP). Index on PurchaseOrderID.

#### Scenario: Table created successfully
- **WHEN** migration SQL is executed
- **THEN** table `PurchaseOrderHistory` exists with proper columns, FKs, and index

### Requirement: PurchaseServlet uses module-owned components
The `PurchaseServlet` SHALL NOT import or instantiate `ApprovalService` or `ApprovalHistoryDAO`. It SHALL use `PurchaseOrderHistoryDAO` for loading history and inline status checks using `PurchaseOrderStatus`.

#### Scenario: Detail view loads history from module DAO
- **WHEN** user views purchase order detail
- **THEN** history is loaded via `PurchaseOrderHistoryDAO.getByOrderId()`, not `ApprovalHistoryDAO`
