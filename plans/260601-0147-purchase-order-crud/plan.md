# Plan: UC-4.2 Purchase Order CRUD

## Overview

- **Sprint**: 2
- **Effort**: 16h
- **Issue**: #36
- **Dependencies**:
  - Approval Foundation (#37) - DocumentStatus, ApprovalAction, ApprovalService, ApprovalHistoryDAO
  - SupplierDAO (com.kiotretail.product.dao.SupplierDAO) for supplier dropdown
  - ProductDAO (com.kiotretail.product.dao.ProductDAO) for line item product picker
  - BranchDAO (com.kiotretail.employee.dao.BranchDAO) for branch dropdown
  - WarehouseDAO (or inline SQL) for stock updates on receive

This module implements the full lifecycle of a Purchase Order (phieu nhap) in KiotRetail with the approval workflow integrated: create draft, submit for approval, approve/reject by role-based threshold, cancel, and partial/full goods receipt that updates inventory atomically.

## Architecture

### Package structure

```
com.kiotretail.purchase
- controller.PurchaseServlet           // @WebServlet("/admin/purchases")
- service.PurchaseService              // business rules + transaction boundaries
- dao.PurchaseOrderDAO                 // header CRUD + V_PurchaseOrderList search
- dao.PurchaseOrderDetailDAO           // line item CRUD
- model.PurchaseOrder                  // header POJO with approval audit fields
- model.PurchaseOrderDetail            // line POJO with receivedQuantity
- dto.PurchaseFilterDTO                // list-page filter params
- util.PurchaseOrderCodeGenerator      // PO-yyyyMMdd-NNN

src/main/webapp/WEB-INF/views/purchase
- purchase-list.jsp
- purchase-create.jsp
- purchase-edit.jsp
- purchase-detail.jsp
- purchase-receive.jsp
- purchase-print.jsp
```

### Module dependencies

```
PurchaseServlet
  -> PurchaseService
       -> PurchaseOrderDAO        (BaseDAO)
       -> PurchaseOrderDetailDAO  (BaseDAO)
       -> ApprovalService         (canSubmit/canApprove/canReject/canCancel/logTransition)
       -> ApprovalHistoryDAO      (timeline display)
       -> ProductDAO              (StockQuantity update)
       -> WarehouseDAO            (AvailableQuantity per branch)
  -> SupplierDAO       (form dropdowns + filter)
  -> BranchDAO         (form dropdowns + filter)
  -> ProductDAO        (line item picker)
  -> ApprovalHistoryDAO (detail page timeline)
```

All DAO writes that span multiple tables (createDraft, updateDraft, receive) execute under a single JDBC Connection with manual transaction (`setAutoCommit(false)` + commit/rollback) to keep header, lines, Product.StockQuantity, and Warehouse.AvailableQuantity consistent.

## Database Schema

Migration file: `sql/V004_purchase_order_approval.sql`

The base tables PurchaseOrder and PurchaseOrderDetail already exist in `sql/purchase-order-schema.sql`. This migration is **additive and idempotent** - it only adds approval-workflow columns, widens Status to fit DocumentStatus names, replaces the legacy CHECK constraint, backfills legacy lower-case statuses, and adds ReceivedQuantity to detail rows.

```sql
-- ============================================================
-- Purchase Order Module - Schema Migration (idempotent)
-- File: sql/V004_purchase_order_approval.sql
-- ============================================================

-- 1. Drop old CHECK constraint that hard-codes legacy statuses
IF EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_PurchaseOrder_Status')
    ALTER TABLE PurchaseOrder DROP CONSTRAINT CK_PurchaseOrder_Status;

-- 2. Add approval-workflow columns to PurchaseOrder
IF COL_LENGTH('PurchaseOrder', 'CreatedBy') IS NULL
    ALTER TABLE PurchaseOrder ADD CreatedBy INT NULL;
IF COL_LENGTH('PurchaseOrder', 'SubmittedAt') IS NULL
    ALTER TABLE PurchaseOrder ADD SubmittedAt DATETIME NULL;
IF COL_LENGTH('PurchaseOrder', 'ApprovedBy') IS NULL
    ALTER TABLE PurchaseOrder ADD ApprovedBy INT NULL;
IF COL_LENGTH('PurchaseOrder', 'ApprovedAt') IS NULL
    ALTER TABLE PurchaseOrder ADD ApprovedAt DATETIME NULL;
IF COL_LENGTH('PurchaseOrder', 'RejectedBy') IS NULL
    ALTER TABLE PurchaseOrder ADD RejectedBy INT NULL;
IF COL_LENGTH('PurchaseOrder', 'RejectedAt') IS NULL
    ALTER TABLE PurchaseOrder ADD RejectedAt DATETIME NULL;
IF COL_LENGTH('PurchaseOrder', 'RejectedReason') IS NULL
    ALTER TABLE PurchaseOrder ADD RejectedReason NVARCHAR(500) NULL;
IF COL_LENGTH('PurchaseOrder', 'CancelledBy') IS NULL
    ALTER TABLE PurchaseOrder ADD CancelledBy INT NULL;
IF COL_LENGTH('PurchaseOrder', 'CancelledAt') IS NULL
    ALTER TABLE PurchaseOrder ADD CancelledAt DATETIME NULL;
IF COL_LENGTH('PurchaseOrder', 'CancelledReason') IS NULL
    ALTER TABLE PurchaseOrder ADD CancelledReason NVARCHAR(500) NULL;
IF COL_LENGTH('PurchaseOrder', 'CompletedAt') IS NULL
    ALTER TABLE PurchaseOrder ADD CompletedAt DATETIME NULL;
IF COL_LENGTH('PurchaseOrder', 'UpdatedAt') IS NULL
    ALTER TABLE PurchaseOrder ADD UpdatedAt DATETIME NULL;

-- 3. Widen Status column to fit DocumentStatus names (PENDING_APPROVAL = 16 chars)
ALTER TABLE PurchaseOrder ALTER COLUMN Status NVARCHAR(30) NOT NULL;

-- 4. Default value for Status -> DRAFT (drop existing default first if any)
DECLARE @df NVARCHAR(200);
SELECT @df = dc.name
FROM sys.default_constraints dc
JOIN sys.columns c ON c.default_object_id = dc.object_id
WHERE OBJECT_NAME(dc.parent_object_id) = 'PurchaseOrder' AND c.name = 'Status';
IF @df IS NOT NULL EXEC ('ALTER TABLE PurchaseOrder DROP CONSTRAINT ' + @df);
ALTER TABLE PurchaseOrder ADD CONSTRAINT DF_PurchaseOrder_Status DEFAULT ('DRAFT') FOR Status;

-- 5. New CHECK constraint aligned with DocumentStatus enum
ALTER TABLE PurchaseOrder ADD CONSTRAINT CK_PurchaseOrder_Status
    CHECK (Status IN ('DRAFT','PENDING_APPROVAL','APPROVED','REJECTED',
                      'IN_PROGRESS','RECEIVING','COMPLETED','CANCELLED'));

-- 6. FKs for new audit columns -> Employee
ALTER TABLE PurchaseOrder ADD CONSTRAINT FK_PurchaseOrder_CreatedBy   FOREIGN KEY (CreatedBy)   REFERENCES Employee(EmployeeID);
ALTER TABLE PurchaseOrder ADD CONSTRAINT FK_PurchaseOrder_ApprovedBy  FOREIGN KEY (ApprovedBy)  REFERENCES Employee(EmployeeID);
ALTER TABLE PurchaseOrder ADD CONSTRAINT FK_PurchaseOrder_RejectedBy  FOREIGN KEY (RejectedBy)  REFERENCES Employee(EmployeeID);
ALTER TABLE PurchaseOrder ADD CONSTRAINT FK_PurchaseOrder_CancelledBy FOREIGN KEY (CancelledBy) REFERENCES Employee(EmployeeID);

-- 7. Backfill: legacy lower-case statuses -> DocumentStatus values
UPDATE PurchaseOrder SET Status = 'DRAFT'      WHERE Status = 'draft';
UPDATE PurchaseOrder SET Status = 'APPROVED'   WHERE Status = 'confirmed';
UPDATE PurchaseOrder SET Status = 'COMPLETED'  WHERE Status = 'received';
UPDATE PurchaseOrder SET Status = 'CANCELLED'  WHERE Status = 'cancelled';
UPDATE PurchaseOrder SET CreatedBy = EmployeeID WHERE CreatedBy IS NULL;

-- 8. Add ReceivedQuantity to detail line items
IF COL_LENGTH('PurchaseOrderDetail', 'ReceivedQuantity') IS NULL
    ALTER TABLE PurchaseOrderDetail ADD ReceivedQuantity INT NOT NULL DEFAULT 0;

ALTER TABLE PurchaseOrderDetail ADD CONSTRAINT CK_PODetail_ReceivedQty
    CHECK (ReceivedQuantity >= 0 AND ReceivedQuantity <= Quantity);

-- 9. Helpful indexes for filter/list queries
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IDX_PurchaseOrder_Branch')
    CREATE INDEX IDX_PurchaseOrder_Branch ON PurchaseOrder(BranchID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IDX_PurchaseOrder_CreatedBy')
    CREATE INDEX IDX_PurchaseOrder_CreatedBy ON PurchaseOrder(CreatedBy);

-- 10. Optional view that joins lookups for list page (avoids N+1 in DAO)
IF OBJECT_ID('V_PurchaseOrderList', 'V') IS NOT NULL DROP VIEW V_PurchaseOrderList;
GO
CREATE VIEW V_PurchaseOrderList AS
SELECT po.PurchaseOrderID, po.OrderCode, po.Status, po.TotalAmount, po.Note,
       po.CreatedAt, po.SubmittedAt, po.ApprovedAt, po.CompletedAt,
       po.SupplierID, s.Name AS SupplierName,
       po.BranchID, b.Name AS BranchName,
       po.CreatedBy, ec.FullName AS CreatedByName,
       po.ApprovedBy, ea.FullName AS ApprovedByName
FROM PurchaseOrder po
JOIN Supplier s ON s.SupplierID = po.SupplierID
JOIN Branch   b ON b.BranchID   = po.BranchID
LEFT JOIN Employee ec ON ec.EmployeeID = po.CreatedBy
LEFT JOIN Employee ea ON ea.EmployeeID = po.ApprovedBy;
GO
```

### Status lifecycle

```
DRAFT --submit--> PENDING_APPROVAL --approve--> APPROVED --receive(partial)--> IN_PROGRESS --receive(more)--> RECEIVING --receive(final)--> COMPLETED
                                  \--reject--> REJECTED

Any of {DRAFT, PENDING_APPROVAL, APPROVED, IN_PROGRESS, RECEIVING} --cancel--> CANCELLED
{IN_PROGRESS, RECEIVING} --completeManually--> COMPLETED  (Owner/Manager only)
```

## Implementation Steps

### Step 1: SQL Migration + Models (2h)

**1.1 sql/V004_purchase_order_approval.sql**

Run the migration script above against DBFinora. The script is idempotent and safe to re-run. Verify:
- `SELECT name, type FROM sys.check_constraints WHERE name = 'CK_PurchaseOrder_Status'` returns the new constraint
- `SELECT DISTINCT Status FROM PurchaseOrder` shows only upper-case DocumentStatus values
- `SELECT COL_LENGTH('PurchaseOrderDetail','ReceivedQuantity')` returns 4 (int)
- `SELECT * FROM V_PurchaseOrderList` runs without error

**1.2 com.kiotretail.purchase.model.PurchaseOrder**

POJO with all approval audit fields. Joined name properties (`supplierName`, `branchName`, `createdByName`, `approvedByName`) are populated by DAO joins; the transient `details` list is filled by service when the detail page or update flow needs it.

Fields:
- `int purchaseOrderId`
- `String orderCode` (PO-yyyyMMdd-NNN, unique)
- `int supplierId` / `String supplierName`
- `int branchId` / `String branchName`
- `int employeeId` (legacy column kept for back-compat = creator)
- `String status` (DocumentStatus.name())
- `BigDecimal totalAmount`
- `String note`
- `Timestamp createdAt`, `Timestamp updatedAt`
- `Integer createdBy` / `String createdByName`
- `Timestamp submittedAt`
- `Integer approvedBy` / `String approvedByName` / `Timestamp approvedAt`
- `Integer rejectedBy` / `Timestamp rejectedAt` / `String rejectedReason`
- `Integer cancelledBy` / `Timestamp cancelledAt` / `String cancelledReason`
- `Timestamp completedAt`
- `transient List<PurchaseOrderDetail> details`

Public getters/setters for all fields plus convenience: `getStatusEnum()` returning `DocumentStatus.valueOf(status)`.

**1.3 com.kiotretail.purchase.model.PurchaseOrderDetail**

Fields:
- `int poDetailId`
- `int purchaseOrderId`
- `int productId` / `String productName` / `String productSku`
- `int quantity`
- `int receivedQuantity` (default 0, must be <= quantity)
- `BigDecimal unitCost`
- `BigDecimal subtotal` (computed in DAO as `quantity * unitCost`)
- `transient int outstandingQuantity` (computed `quantity - receivedQuantity`)

**1.4 com.kiotretail.purchase.dto.PurchaseFilterDTO**

Fields:
- `String keyword` (matches OrderCode or Supplier.Name LIKE)
- `Integer supplierId`
- `Integer branchId`
- `String status` (DocumentStatus.name() or null = all)
- `String dateFrom` (yyyy-MM-dd, applied to CreatedAt)
- `String dateTo` (yyyy-MM-dd)
- `Integer createdBy` (auto-applied to current user when role = Employee)
- `BigDecimal minAmount` / `BigDecimal maxAmount`

### Step 2: DAOs (3h)

**2.1 com.kiotretail.purchase.dao.PurchaseOrderDAO** (extends BaseDAO)

Constants: `BASE_SELECT` against `V_PurchaseOrderList` for search; column list constants for INSERT/UPDATE.

Methods:
- `List<PurchaseOrder> search(PurchaseFilterDTO filter, Pagination pagination)` - uses V_PurchaseOrderList, dynamic WHERE for keyword (OrderCode OR SupplierName LIKE), supplierId, branchId, status, dateFrom/dateTo on CreatedAt, minAmount/maxAmount on TotalAmount, createdBy. ORDER BY CreatedAt DESC + OFFSET/FETCH NEXT for paging.
- `int countAll(PurchaseFilterDTO filter)` - same WHERE clause, returns COUNT(*)
- `PurchaseOrder getById(int purchaseOrderId)` - SELECT from PurchaseOrder joined with Supplier, Branch, Employee (creator + approver) for *Name fields
- `boolean existsByOrderCode(String orderCode)` - guard for code generator retry
- `int insert(PurchaseOrder po)` - returns generated PurchaseOrderID via Statement.RETURN_GENERATED_KEYS. Sets Status = 'DRAFT', CreatedAt = GETDATE(), CreatedBy. Has transactional overload `insert(PurchaseOrder po, Connection conn)` used inside service transactions.
- `boolean update(PurchaseOrder po)` - updates note, totalAmount, supplierId, branchId, UpdatedAt = GETDATE() (DRAFT only enforcement is in service)
- `boolean updateStatus(int poId, String newStatus, int actorId)` - single UPDATE used by all transitions, sets UpdatedAt = GETDATE(). Has `(... Connection conn)` overload.
- `boolean updateApproval(int poId, String status, int approvedBy, Timestamp approvedAt)` - sets Status, ApprovedBy, ApprovedAt
- `boolean updateRejection(int poId, String status, int rejectedBy, Timestamp rejectedAt, String reason)` - sets Status, RejectedBy, RejectedAt, RejectedReason
- `boolean updateCancellation(int poId, String status, int cancelledBy, Timestamp cancelledAt, String reason)` - sets Status, CancelledBy, CancelledAt, CancelledReason
- `boolean updateSubmittedAt(int poId, Timestamp submittedAt)` - paired with updateStatus on submit
- `boolean updateCompletedAt(int poId, Timestamp completedAt)` - on COMPLETED transition
- `boolean updateTotalAmount(int poId, BigDecimal total)` / `(... Connection conn)` overload for createDraft/updateDraft recompute
- `String getMaxSequenceForDate(String yyyyMMdd)` - SELECT MAX(SUBSTRING(OrderCode, 13, 3)) WHERE OrderCode LIKE 'PO-yyyyMMdd-%'. Used by util.PurchaseOrderCodeGenerator.
- `private PurchaseOrder extractPurchaseOrder(ResultSet rs)` - mapping helper

**2.2 com.kiotretail.purchase.dao.PurchaseOrderDetailDAO** (extends BaseDAO)

Methods:
- `List<PurchaseOrderDetail> getByOrderId(int purchaseOrderId)` - JOIN Product to populate productName/productSku, ORDER BY PODetailID
- `PurchaseOrderDetail getById(int poDetailId)` - JOIN Product
- `boolean insert(PurchaseOrderDetail line)` / `(... Connection conn)` overload
- `int insertReturningId(PurchaseOrderDetail line)` / `(... Connection conn)` overload
- `boolean update(PurchaseOrderDetail line)` - quantity, unitCost, subtotal
- `boolean updateReceivedQuantity(int poDetailId, int receivedQty)` / `(... Connection conn)` overload - used by receive flow
- `boolean delete(int poDetailId)` / `(... Connection conn)` overload
- `boolean deleteByOrderId(int purchaseOrderId)` / `(... Connection conn)` overload - used by updateDraft to replace lines
- `boolean existsByOrderAndProduct(int purchaseOrderId, int productId)` - duplicate guard for addItem
- `BigDecimal sumSubtotalByOrderId(int purchaseOrderId)` / `(... Connection conn)` overload - recompute totalAmount after line changes
- `private PurchaseOrderDetail extractDetail(ResultSet rs)`

**2.3 com.kiotretail.purchase.util.PurchaseOrderCodeGenerator**

Method `String generate(PurchaseOrderDAO dao)`:
1. Format today as `yyyyMMdd`
2. Call `dao.getMaxSequenceForDate(today)` -> max NNN string or null
3. Increment: NNN = (max == null ? 1 : Integer.parseInt(max) + 1), zero-padded to 3 digits
4. Return `PO-yyyyMMdd-NNN`
5. Caller (service) wraps in retry loop on `existsByOrderCode` collision (race between concurrent inserts)

### Step 3: Service Layer (4h)

**com.kiotretail.purchase.service.PurchaseService**

Constructor injects: `PurchaseOrderDAO`, `PurchaseOrderDetailDAO`, `ApprovalService`, `ApprovalHistoryDAO`, `ProductDAO`, `WarehouseDAO` (or reuse existing).

Validation constants:
- `private static final BigDecimal OWNER_THRESHOLD = AppConstants.OWNER_APPROVAL_THRESHOLD;` (50,000,000)
- `private static final int NOTE_MAX_LENGTH = 500;`

**Public methods:**

- `PageResult<PurchaseOrder> listOrders(PurchaseFilterDTO filter, Pagination pagination)` - delegates to `dao.search` and `dao.countAll`, wraps in PageResult.

- `PurchaseOrder getOrderById(int id)` - calls `dao.getById`; throws `NotFoundException(ENTITY_PURCHASE_ORDER, id)` if null. Eagerly loads `details` via detailDAO.

- `List<PurchaseOrderDetail> getOrderDetails(int purchaseOrderId)` - thin pass-through.

- `List<ApprovalHistory> getOrderHistory(int purchaseOrderId)` - delegates to `ApprovalHistoryDAO.findByDocument(DOC_TYPE_PURCHASE_ORDER, purchaseOrderId)`.

- `int createDraft(PurchaseOrder header, List<PurchaseOrderDetail> lines, int currentUserId)`:
  1. `validateHeader(header)`, `validateLines(lines)`
  2. Compute `totalAmount = sum(line.quantity * line.unitCost)`
  3. Generate code via `PurchaseOrderCodeGenerator.generate(dao)` with retry-on-collision (max 3 retries)
  4. Open Connection, `setAutoCommit(false)`
  5. `dao.insert(header, conn)` -> capture `purchaseOrderId`
  6. For each line: set `purchaseOrderId`, `detailDAO.insert(line, conn)`
  7. `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, purchaseOrderId, ApprovalAction.CREATE, currentUserId, null, conn)`
  8. Commit, return id. Rollback + rethrow as `ServiceException` on SQLException.

- `void updateDraft(int poId, PurchaseOrder header, List<PurchaseOrderDetail> lines, int currentUserId)`:
  1. Load current `po = getOrderById(poId)`; require `po.status = DRAFT` (else ValidationException with `PO_NOT_DRAFT`)
  2. `validateHeader(header)`, `validateLines(lines)`
  3. Recompute `totalAmount`
  4. Open connection + transaction
  5. `dao.update(header, conn)`
  6. `detailDAO.deleteByOrderId(poId, conn)`
  7. Insert each line
  8. `dao.updateTotalAmount(poId, total, conn)`
  9. Commit.

- `void submit(int poId, int currentUserId)`:
  1. Load PO + details. Require `details.size() >= 1` and `totalAmount > 0`.
  2. `approvalService.canSubmit(po.statusEnum, currentUserId, po.createdBy)` - throws if denied
  3. `dao.updateStatus(poId, PENDING_APPROVAL, currentUserId)`
  4. `dao.updateSubmittedAt(poId, now())`
  5. `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, poId, SUBMIT, currentUserId, null)`

- `void approve(int poId, int currentUserId, String userRole)`:
  1. Load PO. Require `status = PENDING_APPROVAL`.
  2. `validateApprovalThreshold(po.totalAmount, userRole)` - if totalAmount >= 50M and role != ROLE_OWNER -> ValidationException(`PO_THRESHOLD_OWNER_ONLY`)
  3. `approvalService.canApprove(po.statusEnum, currentUserId, po.createdBy, userRole)` - enforces creator != approver and role permission. Throws if denied.
  4. `dao.updateApproval(poId, APPROVED, currentUserId, now())`
  5. `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, poId, APPROVE, currentUserId, null)`

- `void reject(int poId, int currentUserId, String userRole, String reason)`:
  1. Require non-empty `reason` (trim, length <= 500). Else ValidationException.
  2. Load PO. Require `status = PENDING_APPROVAL`.
  3. `approvalService.canReject(po.statusEnum, currentUserId, po.createdBy, userRole)` - throws if denied.
  4. `dao.updateRejection(poId, REJECTED, currentUserId, now(), reason)`
  5. `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, poId, REJECT, currentUserId, reason)`

- `void cancel(int poId, int currentUserId, String userRole, String reason)`:
  1. Require non-empty `reason`.
  2. Load PO. Require status in `{DRAFT, PENDING_APPROVAL, APPROVED, IN_PROGRESS, RECEIVING}`.
  3. `approvalService.canCancel(po.statusEnum, currentUserId, po.createdBy, userRole)` - encodes the matrix:
     - DRAFT/PENDING_APPROVAL: creator OR Owner OR Manager
     - APPROVED/IN_PROGRESS/RECEIVING: Owner only
     - else deny
  4. `dao.updateCancellation(poId, CANCELLED, currentUserId, now(), reason)`
  5. `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, poId, CANCEL, currentUserId, reason)`

- `void addItem(int poId, PurchaseOrderDetail line, int currentUserId)`:
  1. Load PO. Require status = DRAFT.
  2. `validateLines(Collections.singletonList(line))`
  3. If `detailDAO.existsByOrderAndProduct(poId, line.productId)` -> merge (load existing, increase quantity, update). Else `detailDAO.insertReturningId(line)`.
  4. Recompute total via `detailDAO.sumSubtotalByOrderId(poId)`, `dao.updateTotalAmount(poId, total)`.

- `void removeItem(int poId, int detailId, int currentUserId)`:
  1. Load PO. Require status = DRAFT.
  2. `detailDAO.delete(detailId)`
  3. Recompute total.

- `void receive(int poId, Map<Integer,Integer> receivedByDetailId, int currentUserId)`:
  1. Load PO + details. Require status in `{APPROVED, IN_PROGRESS, RECEIVING}`.
  2. For each (detailId, qty) in input:
     - Find matching line in `details` (else ValidationException - unknown detailId)
     - Validate `qty >= 0` and `qty <= line.outstandingQuantity` (else `PO_RECEIVE_OVER_QTY`)
     - Skip lines where qty == 0
  3. Open connection, `setAutoCommit(false)`:
     - For each accepted line:
       - `detailDAO.updateReceivedQuantity(detailId, line.receivedQuantity + qty, conn)`
       - `productDAO.incrementStockQuantity(productId, qty, conn)` - SQL: `UPDATE Product SET StockQuantity = StockQuantity + ? WHERE ProductID = ?`
       - `warehouseDAO.upsertAvailableQuantity(branchId, productId, qty, conn)` - try UPDATE first; if 0 rows affected, INSERT row with AvailableQuantity = qty
     - Recompute completion: `sum(received), sum(quantity)` over all lines
     - If `sum(received) == sum(quantity)` -> new status = COMPLETED, also update CompletedAt
     - Else if old status = APPROVED -> new status = IN_PROGRESS
     - Else -> new status = RECEIVING
     - `dao.updateStatus(poId, newStatus, currentUserId, conn)` if changed
     - If COMPLETED: `dao.updateCompletedAt(poId, now(), conn)`
  4. Commit.
  5. After commit: `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, poId, RECEIVE, currentUserId, null)` - and additional `COMPLETE` log if status went to COMPLETED.

- `void completeManually(int poId, int currentUserId, String userRole, String reason)`:
  1. Require role in `{ROLE_OWNER, ROLE_STORE_MANAGER}`.
  2. Load PO. Require status in `{IN_PROGRESS, RECEIVING}`.
  3. `dao.updateStatus(poId, COMPLETED, currentUserId)`, `dao.updateCompletedAt(poId, now())`
  4. `approvalService.logTransition(DOC_TYPE_PURCHASE_ORDER, poId, COMPLETE, currentUserId, reason)`
  Outstanding lines are NOT auto-received - StockQuantity unchanged for outstanding qty.

**Private helpers:**

- `private void validateHeader(PurchaseOrder po)` - supplierId > 0, branchId > 0, note length <= 500. Else ValidationException.
- `private void validateLines(List<PurchaseOrderDetail> lines)` - non-empty, no duplicate productId, each qty > 0, unitCost >= 0.
- `private void validateApprovalThreshold(BigDecimal total, String role)` - if total >= OWNER_THRESHOLD and role != ROLE_OWNER -> ValidationException(`PO_THRESHOLD_OWNER_ONLY`).
- `private DocumentStatus requireStatus(PurchaseOrder po, DocumentStatus... allowed)` - throws ValidationException if not in set.

**Approval workflow integration points:**

- Every transition call must go through `ApprovalService.canX` first to centralize the role/state matrix.
- Every successful transition writes one ApprovalHistory row via `ApprovalService.logTransition(documentType=PURCHASE_ORDER, ...)`.
- Connection-aware overloads of logTransition (if available) keep audit and state change in one transaction; otherwise log immediately after commit.

### Step 4: Servlet Controller (3h)

**com.kiotretail.purchase.controller.PurchaseServlet** `@WebServlet("/admin/purchases")` extends BaseServlet.

`init()`:
```
this.purchaseService = new PurchaseService(...);
this.supplierDAO = new SupplierDAO();
this.branchDAO = new BranchDAO();
this.productDAO = new ProductDAO();
this.approvalHistoryDAO = new ApprovalHistoryDAO();
```

**doGet routing** (action default = list):

- `list` - parse PurchaseFilterDTO + Pagination from request; if session user role = Employee, force `filter.createdBy = currentUserId`; call `service.listOrders`; load supplier/branch dropdowns; forward to PURCHASE_LIST view. Attributes: `purchaseOrders`, `pageResult`, `filter`, `suppliers`, `branches`, `statuses` (DocumentStatus.values()).
- `view` - require `id`; load PO + details + approvalHistory; forward to PURCHASE_DETAIL with attributes `purchaseOrder`, `purchaseLines`, `approvalHistory`.
- `create` - load suppliers, branches, products; forward to PURCHASE_CREATE.
- `edit` - require `id`; if status != DRAFT setFlash danger + redirect to view; else load suppliers/branches/products + PO + lines, forward to PURCHASE_EDIT.
- `receive` - require `id`; if status not in APPROVED/IN_PROGRESS/RECEIVING setFlash danger + redirect to view; else forward to PURCHASE_RECEIVE with PO + lines.
- `print` - require `id`; load PO + lines + history; forward to PURCHASE_PRINT (no navbar).

**doPost routing** (each handler wrapped in try/catch like InvoiceServlet.handleAddPayment - on ValidationException/ServiceException setFlash danger, on success setFlash success, then redirect):

- `create` - parse supplierId, branchId, note, parallel arrays productIds[], quantities[], unitCosts[]; build header + lines; call `service.createDraft(header, lines, currentUserId)`; redirect to view.
- `update` - same as create but with id; call `service.updateDraft(id, header, lines, currentUserId)`.
- `submit` - require id; call `service.submit(id, currentUserId)`.
- `approve` - require id; call `service.approve(id, currentUserId, currentUserRole)`.
- `reject` - require id, reason; call `service.reject(id, currentUserId, currentUserRole, reason)`.
- `cancel` - require id, reason; call `service.cancel(id, currentUserId, currentUserRole, reason)`.
- `addItem` - require id, productId, quantity, unitCost; build PurchaseOrderDetail; call `service.addItem(id, line, currentUserId)`; redirect back to edit.
- `removeItem` - require id, detailId; call `service.removeItem(id, detailId, currentUserId)`; redirect back to edit.
- `receive` - require id; iterate request params with prefix `received_` -> Map<Integer,Integer> by detailId; call `service.receive(id, map, currentUserId)`.
- `completeManually` - require id, reason; call `service.completeManually(id, currentUserId, currentUserRole, reason)`.

After every successful POST: `redirectWithFlash("/admin/purchases?action=view&id=" + id, success, msg)`.

### Step 5: JSP Views (3h)

All views use `WEB-INF/views/common/header.jsp`, `navbar.jsp`, `toast.jsp`, `footer.jsp` and `pagination.jsp`. Layout uses the `kr-page` class system from `assets/css/kr-common.css`.

**purchase-list.jsp**

- Toolbar: keyword search input, primary `Tao phieu nhap` button -> /admin/purchases?action=create
- Sidebar filter form (GET): status select (all DocumentStatus values + 'All'), supplierId select, branchId select, dateFrom, dateTo, minAmount, maxAmount
- Table columns: OrderCode (link to view) | Supplier | Branch | CreatedByName | TotalAmount (formatted) | Status badge | CreatedAt
- Status badge map: DRAFT=default, PENDING_APPROVAL=warning, APPROVED=info, REJECTED=danger, IN_PROGRESS=info, RECEIVING=info, COMPLETED=completed, CANCELLED=cancelled
- Pagination via `<jsp:include page="../common/pagination.jsp"/>`

**purchase-create.jsp**

- Form posts `action=create`
- Header: supplier `<select>` (required), branch `<select>` (required), note `<textarea>` (max 500)
- Line-items `<table>` with one row template; client-side JS adds rows. Each row: product `<select>` + quantity input + unitCost input + computed subtotal cell + remove button
- Hidden parallel arrays: `productIds[]`, `quantities[]`, `unitCosts[]`
- JS recomputes subtotal per row + grand total on every change
- Buttons: `Luu nhap` (submit form), `Huy` (back to list)

**purchase-edit.jsp**

- Same form structure as create, pre-populated with existing PO + lines
- Hidden `id` field, action = `update`
- Servlet redirects with flash danger BEFORE rendering if status != DRAFT, so this view always sees a draft

**purchase-detail.jsp**

- `kr-page-header` with breadcrumb back to /admin/purchases
- Right-side action buttons rendered conditionally by status + sessionScope.user.role:
  - `Edit` if DRAFT and currentUserId == createdBy
  - `Submit` if DRAFT and currentUserId == createdBy
  - `Approve` and `Reject` if PENDING_APPROVAL and role in [Owner, StoreManager] and currentUserId != createdBy
  - `Cancel` based on canCancel matrix
  - `Receive` if status in {APPROVED, IN_PROGRESS, RECEIVING}
  - `Print` always
  - `Back` always
- Order Info card: supplier, branch, createdByName + createdAt, submittedAt, approvedByName + approvedAt, rejectedReason, cancelledReason, completedAt
- Line items table: product, qty, receivedQty, outstanding, unitCost, subtotal
- Approval timeline at bottom: iterate `approvalHistory` list, badge per Action with PerformedByName + CreatedAt + Reason

**purchase-receive.jsp**

- Form posts `action=receive`
- Table of detail lines: product, ordered qty, already received, outstanding, `<input type="number" name="received_${line.poDetailId}" max="${line.outstandingQuantity}" min="0">`
- Submit button `Xac nhan nhap kho`
- Optional checkbox `Dong don ngay (force complete)` -> when checked, change form action to `completeManually` (with reason input)

**purchase-print.jsp**

- Bare layout, no navbar
- Renders order header (supplier, branch, dates) + line items table + total
- `<script>window.onload=function(){window.print();}</script>`

### Step 6: Integration + Navigation (1h)

**6.1 Navbar entry** - in `WEB-INF/views/common/navbar.jsp`, add a sidebar item under the inventory section: link `/admin/purchases` labelled `Phieu nhap kho`. Visible to roles Owner, StoreManager, Inventory (use `<c:if>` against sessionScope.user.role).

**6.2 ViewPaths constants** - add to `com.kiotretail.shared.constant.ViewPaths`:

```java
public static final String PURCHASE_LIST    = "/WEB-INF/views/purchase/purchase-list.jsp";
public static final String PURCHASE_CREATE  = "/WEB-INF/views/purchase/purchase-create.jsp";
public static final String PURCHASE_EDIT    = "/WEB-INF/views/purchase/purchase-edit.jsp";
public static final String PURCHASE_DETAIL  = "/WEB-INF/views/purchase/purchase-detail.jsp";
public static final String PURCHASE_RECEIVE = "/WEB-INF/views/purchase/purchase-receive.jsp";
public static final String PURCHASE_PRINT   = "/WEB-INF/views/purchase/purchase-print.jsp";
public static final String REDIRECT_PURCHASES = "/admin/purchases";
```

**6.3 AppConstants** - add to `com.kiotretail.shared.constant.AppConstants`:

```java
public static final String DOC_TYPE_PURCHASE_ORDER = "PURCHASE_ORDER";
public static final java.math.BigDecimal OWNER_APPROVAL_THRESHOLD = new java.math.BigDecimal("50000000");
public static final String ATTR_PURCHASE_ORDER   = "purchaseOrder";
public static final String ATTR_PURCHASE_LINES   = "purchaseLines";
public static final String ATTR_APPROVAL_HISTORY = "approvalHistory";
public static final String ATTR_SUPPLIERS = "suppliers";
public static final String ATTR_BRANCHES  = "branches";
public static final String ATTR_PRODUCTS  = "products";
```

**6.4 ErrorMessages** - add to `com.kiotretail.shared.constant.ErrorMessages`:

```java
public static final String ENTITY_PURCHASE_ORDER   = "phieu nhap";
public static final String PO_LINES_REQUIRED       = "Don nhap phai co it nhat mot san pham";
public static final String PO_NOT_DRAFT            = "Chi chinh sua duoc phieu o trang thai Nhap";
public static final String PO_THRESHOLD_OWNER_ONLY = "Don tu 50.000.000d tro len chi chu cua hang duoc duyet";
public static final String PO_SELF_APPROVE_DENIED  = "Nguoi tao khong duoc tu duyet don cua minh";
public static final String PO_RECEIVE_OVER_QTY     = "So luong nhan vuot qua so luong con lai";
```

**6.5 web.xml** - Servlet mapping is auto-registered via `@WebServlet("/admin/purchases")`; no web.xml entry required. Verify that `/admin/*` is covered by the existing AuthFilter for session enforcement.

## File Checklist

- [ ] `sql/V004_purchase_order_approval.sql`
- [ ] `src/main/java/com/kiotretail/purchase/model/PurchaseOrder.java`
- [ ] `src/main/java/com/kiotretail/purchase/model/PurchaseOrderDetail.java`
- [ ] `src/main/java/com/kiotretail/purchase/dto/PurchaseFilterDTO.java`
- [ ] `src/main/java/com/kiotretail/purchase/util/PurchaseOrderCodeGenerator.java`
- [ ] `src/main/java/com/kiotretail/purchase/dao/PurchaseOrderDAO.java`
- [ ] `src/main/java/com/kiotretail/purchase/dao/PurchaseOrderDetailDAO.java`
- [ ] `src/main/java/com/kiotretail/purchase/service/PurchaseService.java`
- [ ] `src/main/java/com/kiotretail/purchase/controller/PurchaseServlet.java`
- [ ] `src/main/webapp/WEB-INF/views/purchase/purchase-list.jsp`
- [ ] `src/main/webapp/WEB-INF/views/purchase/purchase-create.jsp`
- [ ] `src/main/webapp/WEB-INF/views/purchase/purchase-edit.jsp`
- [ ] `src/main/webapp/WEB-INF/views/purchase/purchase-detail.jsp`
- [ ] `src/main/webapp/WEB-INF/views/purchase/purchase-receive.jsp`
- [ ] `src/main/webapp/WEB-INF/views/purchase/purchase-print.jsp`
- [ ] `src/main/java/com/kiotretail/shared/constant/ViewPaths.java` (add PURCHASE_* + REDIRECT_PURCHASES)
- [ ] `src/main/java/com/kiotretail/shared/constant/AppConstants.java` (add DOC_TYPE_PURCHASE_ORDER, OWNER_APPROVAL_THRESHOLD, ATTR_*)
- [ ] `src/main/java/com/kiotretail/shared/constant/ErrorMessages.java` (add ENTITY_PURCHASE_ORDER + PO_*)
- [ ] `src/main/webapp/WEB-INF/views/common/navbar.jsp` (add Phieu nhap kho link)

## Acceptance Criteria

- [ ] Owner/StoreManager/Inventory can list purchase orders with status badges, paging, and full filter set (keyword, status, supplier, branch, date range, amount range)
- [ ] Employee role only sees POs created by themselves
- [ ] Create form persists header + lines as DRAFT with auto-generated OrderCode in format PO-yyyyMMdd-NNN
- [ ] Edit allowed ONLY when status = DRAFT; any other status shows flash error and redirects to detail
- [ ] Submit transitions DRAFT -> PENDING_APPROVAL only when lines.size() >= 1 AND totalAmount > 0
- [ ] Approve transitions PENDING_APPROVAL -> APPROVED with role gate: totalAmount < 50,000,000 -> Owner or StoreManager; totalAmount >= 50,000,000 -> Owner only
- [ ] Self-approval is rejected (creator != approver enforced)
- [ ] Reject requires non-empty reason and transitions PENDING_APPROVAL -> REJECTED
- [ ] Cancel matrix enforced: DRAFT/PENDING_APPROVAL = creator/Owner/Manager; APPROVED/IN_PROGRESS/RECEIVING = Owner only; COMPLETED/REJECTED/CANCELLED = denied
- [ ] Cancel requires non-empty reason; transitions to CANCELLED with audit fields populated
- [ ] Receive flow updates `PurchaseOrderDetail.ReceivedQuantity`, `Product.StockQuantity`, and `Warehouse.AvailableQuantity (BranchID, ProductID)` atomically (transaction)
- [ ] Partial receive transitions APPROVED -> IN_PROGRESS on first non-zero, IN_PROGRESS -> RECEIVING on subsequent partials
- [ ] Full receive (sum received == sum ordered for all lines) transitions to COMPLETED with CompletedAt set
- [ ] Receive rejects qty > outstanding for any line with `PO_RECEIVE_OVER_QTY`
- [ ] Manual close (`completeManually`) only for Owner/Manager when status in {IN_PROGRESS, RECEIVING}; outstanding qty NOT auto-received
- [ ] Every status change writes one ApprovalHistory row with documentType=PURCHASE_ORDER and the correct ApprovalAction
- [ ] Detail page shows the full approval timeline with action badges, performer, timestamp, reason
- [ ] Print view (`action=print`) renders bare printable layout and auto-invokes `window.print()`
- [ ] All money fields use BigDecimal; total recomputed in service from lines (not trusted from form)
- [ ] `mvn test` passes
- [ ] `mvn clean package` produces a deployable WAR
- [ ] `gitnexus_detect_changes()` shows only purchase package + constants + navbar + sql migration as changed

## Risks

- **Status backfill**: legacy lower-case values must be present in production data; the migration handles `draft`, `confirmed`, `received`, `cancelled`. Any other legacy value will fail the new CHECK constraint - inspect distinct existing values before running step 7 of the migration on production.
- **OrderCode race condition**: two concurrent creates on the same day can compute the same NNN. Mitigated by retry-on-collision (max 3) + UNIQUE constraint on OrderCode. Hard collisions surface as ServiceException to the user.
- **Receive transaction scope**: must update detail + Product + Warehouse + PO status under one Connection. If any DAO is not refactored to accept a Connection overload, the partial-failure window risks inventory drift. Add the Connection-aware overloads listed in step 2 before wiring receive.
- **Warehouse upsert**: if `Warehouse(BranchID, ProductID)` row is missing, the receive flow inserts a new row. Confirm the Warehouse table allows INSERT with only (BranchID, ProductID, AvailableQuantity) - if other NOT NULL columns exist (e.g. ReservedQuantity), provide defaults.
- **ApprovalService API drift**: this plan assumes `canSubmit/canApprove/canReject/canCancel/logTransition` exist in ApprovalService with signatures accepting (status, currentUserId, creatorId, role[, reason/connection]). If the foundation issue (#37) finalises a different signature, adapt service calls accordingly - business logic stays the same.
- **JSP role checks**: action button visibility is enforced in JSP via `sessionScope.user.role` for UX, but server-side enforcement in Service is the source of truth. Do not rely on JSP gating alone.
- **Threshold tunability**: `OWNER_APPROVAL_THRESHOLD` lives in AppConstants. If business asks to make this per-branch or DB-driven later, refactor to a settings table - not in scope for this story.
- **Manual close inventory**: `completeManually` does NOT auto-receive outstanding qty - intentional. Document this in user-facing docs to prevent confusion when COMPLETED status appears with outstanding > 0.
