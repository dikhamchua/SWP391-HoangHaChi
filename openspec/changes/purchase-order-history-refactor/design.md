## Context

KiotRetail uses a shared `ApprovalService` + `ApprovalHistoryDAO` + `DocumentStatus` enum for all approvable documents. The purchase module (`PurchaseService`, `PurchaseServlet`) currently delegates to these shared components. This refactor decouples the purchase module so it owns its entire lifecycle — status enum, action enum, history table, and approval logic.

Current coupling points:
- `PurchaseService` → `ApprovalService` (5 methods: canSubmit, canApprove, canReject, canCancel, logTransition)
- `PurchaseService` → `DocumentStatus` enum (status comparisons)
- `PurchaseService` → `ApprovalAction` enum (log parameters)
- `PurchaseServlet` → `ApprovalService` (render gate: canApprove check)
- `PurchaseServlet` → `ApprovalHistoryDAO` (load history for detail view)
- `PurchaseOrder` model → `DocumentStatus` (getStatusEnum return type)
- `PurchaseOrderDAO` → `DocumentStatus` (javadoc reference only)

## Goals / Non-Goals

**Goals:**
- Purchase module fully self-contained: no imports from `shared.service.ApprovalService`, `shared.dao.ApprovalHistoryDAO`, `shared.constant.DocumentStatus`, `shared.constant.ApprovalAction`
- New `PurchaseOrderHistory` table with proper FK to `PurchaseOrder`
- Inline approval logic in `PurchaseService` (canSubmit/canApprove/canReject/canCancel)
- 10-15 unit tests covering the inlined logic
- `mvn clean package` passes

**Non-Goals:**
- Migrate existing data from `ApprovalHistory` table
- Remove shared approval components (Phase 4)
- Modify `ApprovalServlet` or approval JSPs (Phase 4)
- Refactor StockTransfer or StockAdjustment modules (Phase 2, 3)

## Decisions

### D1: Inline logic vs shared helper

**Decision**: Copy approval logic directly into `PurchaseService` private methods.

**Rationale**: Full module independence per Phương án A (decentralized). Accept duplication over coupling. The logic is small (~40 lines for all 4 gate methods) and may diverge per module in the future.

**Alternative rejected**: Shared static utility — adds coupling back, defeats the purpose.

### D2: PurchaseOrderStatus enum values

**Decision**: DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, RECEIVING, COMPLETED, CANCELLED (7 values).

**Rationale**: Exact match of what PurchaseOrder actually uses today. Drops FINALIZED and IN_PROGRESS which were never used by purchase.

### D3: History DAO pattern

**Decision**: `PurchaseOrderHistoryDAO` mirrors `ApprovalHistoryDAO` structure but with typed FK (`PurchaseOrderID` instead of polymorphic `DocumentType` + `DocumentID`).

**Rationale**: Stronger referential integrity, simpler queries (no WHERE DocumentType filter), same audit trail functionality.

### D4: Breaking change handling for getStatusEnum()

**Decision**: Change return type directly. Fix all callers in the same commit.

**Rationale**: All callers are within the purchase module + `ApprovalServlet` (which is kept temporarily and can use string comparison). Clean break is better than deprecation dance for an internal API.

### D5: ApprovalServlet compatibility

**Decision**: Keep `ApprovalServlet` functional during transition by having it use `PurchaseOrderDAO.getById()` + string status comparison instead of `DocumentStatus` enum. Minimal touch — only fix compile errors if any arise from the `getStatusEnum()` change.

**Rationale**: Phase 4 will remove it entirely. Minimal effort now to keep it working.

## Risks / Trade-offs

- **[Risk] ApprovalServlet breaks** → Mitigation: Fix compile errors only. It still reads `PurchaseOrder.status` as String from DB, which doesn't change. The `getStatusEnum()` breaking change only affects code that calls that specific method.
- **[Risk] Missed caller of getStatusEnum()** → Mitigation: `mvn compile` will catch any unresolved type mismatch immediately.
- **[Risk] Duplicate logic drifts between modules** → Accepted trade-off for Phase 2/3. Each module owns its rules independently.
- **[Risk] Old ApprovalHistory continues accumulating PO rows** → Mitigation: After refactor, PurchaseService no longer calls `ApprovalService.logTransition()`, so no new rows. Old data stays until Phase 4 cleanup.
