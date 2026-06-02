## Why

The current approval workflow uses a shared polymorphic `ApprovalHistory` table and shared `DocumentStatus`/`ApprovalAction` enums across all modules (Purchase, StockTransfer, StockAdjustment). This causes: (1) a bloated enum with states irrelevant to specific modules, (2) no FK constraints between history and the owning document, (3) inability to add module-specific fields, (4) difficult debugging due to mixed data. Each module should own its lifecycle independently.

## What Changes

- **BREAKING**: `PurchaseOrder.getStatusEnum()` changes return type from `DocumentStatus` to `PurchaseOrderStatus`
- New `PurchaseOrderStatus` enum with states specific to purchase lifecycle (DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, RECEIVING, COMPLETED, CANCELLED)
- New `PurchaseOrderAction` enum (CREATE, SUBMIT, APPROVE, REJECT, CANCEL, RECEIVE, COMPLETE)
- New `PurchaseOrderHistory` model and `PurchaseOrderHistoryDAO` for module-owned audit trail
- New `PurchaseOrderHistory` DB table with FK to PurchaseOrder
- `PurchaseService` inlines all approval logic (canSubmit, canApprove, canReject, canCancel) — removes dependency on shared `ApprovalService`
- `PurchaseServlet` switches from shared `ApprovalHistoryDAO` to `PurchaseOrderHistoryDAO`
- Shared `ApprovalService`/`ApprovalHistoryDAO`/`DocumentStatus`/`ApprovalAction` remain untouched (cleanup in future Phase 4)

## Capabilities

### New Capabilities
- `purchase-order-lifecycle`: Self-contained status enum, action enum, history model, history DAO, and inline approval logic for the purchase order module

### Modified Capabilities
- None (shared approval artifacts are kept as-is for other modules)

## Impact

- **Code**: `purchase/` package (service, controller, model, DAO) — all references to `DocumentStatus`, `ApprovalAction`, `ApprovalService`, `ApprovalHistoryDAO` replaced with module-owned equivalents
- **Database**: New `PurchaseOrderHistory` table (no data migration from old table)
- **Tests**: Old `ApprovalServiceTest`/`ApprovalServiceThresholdTest` untouched; new `PurchaseApprovalTest` with 10-15 tests
- **Breaking**: Any code calling `PurchaseOrder.getStatusEnum()` must handle `PurchaseOrderStatus` instead of `DocumentStatus`
