## Context

The Purchase Order module (`com.kiotretail.purchase`) currently has:
- **Model**: `PurchaseOrder` and `PurchaseOrderDetail` already include all audit columns (CreatedBy, ApprovedBy, SubmittedAt, ApprovedAt, RejectedBy, RejectedReason, CancelledReason, CompletedAt) and `ReceivedQuantity`
- **Service**: `PurchaseService.submit/approve/reject/cancel` methods exist and call `ApprovalService` correctly
- **DAO**: `PurchaseOrderDAO` supports filtering and pagination

What is missing is the **wiring**: the `PurchaseServlet` only handles `list/view/create`, and `ApprovalPreviewServlet` returns mock data. The user-facing flow is broken even though the foundation works.

The Approval foundation (`com.kiotretail.shared.service.ApprovalService`) is stateless and pure-Java, with permission gates: `canSubmit`, `canApprove`, `canReject`, `canCancel`. It writes audit rows via `ApprovalHistoryDAO` on every `logTransition` call. Threshold logic (≥50M VND requires Owner) lives in `AppConstants.OWNER_APPROVAL_THRESHOLD`, but is currently **not enforced** inside `canApprove` — it's only documented. We must enforce it during this change.

## Goals / Non-Goals

**Goals:**
- Wire the existing `PurchaseService` approval methods to the servlet routes documented in the proposal
- Replace the `ApprovalPreviewServlet` mock data with real DAO queries
- Render approval state correctly in `purchase-detail.jsp` (timeline, status badge, conditional buttons)
- Add `purchase-edit.jsp`, `purchase-receive.jsp`, `purchase-print.jsp`
- Enforce threshold (≥50M VND requires Owner) at the service layer, not the JSP
- Make the navbar pending-count badge real

**Non-Goals:**
- Approval workflow for other document types (Stock Transfer, Stock Adjustment, etc.) — those are separate issues (#31, #33, #34)
- Email/SMS notification on approval events
- Multi-level approval chains (foundation only supports single approver)
- DB migration scripts (the schema is already in `DBFinora_Extension.sql`)
- Approval delegation / out-of-office handling
- Bulk approve (one-at-a-time only)

## Decisions

### D1: Enforce threshold inside `ApprovalService.canApprove`, not in the controller

**Decision:** Add an overload `canApprove(currentStatus, userRole, creatorId, approverId, totalAmount)` that returns `false` when `totalAmount >= OWNER_APPROVAL_THRESHOLD` and `userRole != ROLE_OWNER`.

**Rationale:** Putting the rule in the service means every consumer (Purchase, future Transfer/Adjustment) gets the same gate. JSPs only render hint text; they cannot be the source of truth.

**Alternative:** Check in `PurchaseService.approve` directly — rejected because it duplicates the check in every caller and won't apply to other document types.

### D2: Use PRG (Post-Redirect-Get) for every state transition

**Decision:** All POST actions (`submit`, `approve`, `reject`, `cancel`, `update`, `receiveSubmit`) end with `response.sendRedirect("/admin/purchases?action=view&id=" + id)` and a flash message in session.

**Rationale:** Browser refresh after a transition must not re-submit. Aligns with `BaseServlet.redirect()` already used in the codebase.

**Alternative:** Return JSP directly — rejected because of double-submit risk.

### D3: Approval inbox queries stay polymorphic

**Decision:** `ApprovalServlet` (renamed from `ApprovalPreviewServlet`) queries `PurchaseOrderDAO.findPendingApprovals()` and unions with future `StockTransferDAO`, `StockAdjustmentDAO`. For now, only PURCHASE_ORDER results are returned. The JSP `pending-approvals.jsp` already accepts a polymorphic `PendingApprovalItem` DTO.

**Rationale:** Keeps the inbox future-proof for issues #31/#33/#34 without rewriting later.

### D4: Partial receive uses a per-line form, transaction-bound stock update

**Decision:** `purchase-receive.jsp` renders one row per `PurchaseOrderDetail` with an input `receivedQty[poDetailId]`. The servlet handler:
1. Opens a JDBC transaction
2. For each line, updates `PurchaseOrderDetail.ReceivedQuantity += submittedQty` and `Product.StockQuantity += submittedQty`
3. If all lines fully received → `PurchaseOrder.status = COMPLETED, completedAt = NOW()`. Else → `status = RECEIVING`
4. Commits or rolls back atomically

**Rationale:** Stock and order status must never drift. The existing `StockUpdater` inner class in `PurchaseService` already does this — we just need to expose it via a `receive()` method.

**Alternative:** Auto-receive everything in one click — rejected because business requires partial receive (UC-4.3).

### D5: Conditional buttons are rendered server-side via JSTL `<c:choose>`

**Decision:** `purchase-detail.jsp` evaluates `${order.statusEnum}` and `${sessionScope.user.role}` to render the right buttons. No client-side JS state.

**Rationale:** Server-side rendering matches the existing JSP conventions; defense in depth (the servlet still re-checks permissions on POST).

### D6: Reject/Cancel reason validation is a hard gate

**Decision:** Both server (`ApprovalService.logTransition` already throws `ValidationException` for empty reason) and client (`required minlength="5"` on the textarea) enforce reason ≥ 5 chars.

**Rationale:** Audit trail must always have a reason on negative transitions.

### D7: Print view is a standalone JSP, no chrome

**Decision:** `purchase-print.jsp` excludes `header.jsp` / `navbar.jsp` / `footer.jsp`, applies `@media print` styles, and runs `window.print()` on `body.onload`.

**Rationale:** Standard print pattern; users expect a clean printable PO without navigation.

## Risks / Trade-offs

- **[Threshold drift]** If `AppConstants.OWNER_APPROVAL_THRESHOLD` changes, the badge in `purchase-detail.jsp` must use the same constant. → Mitigation: expose constant via `request.setAttribute("ownerThreshold", AppConstants.OWNER_APPROVAL_THRESHOLD)` in the servlet, never hardcode in JSP.

- **[Stale UI after approve]** User on the detail page might not see the new status if they don't reload after a peer approves. → Mitigation: PRG redirects to fresh GET; no auto-refresh needed for v1.

- **[Approval inbox count drift]** The navbar badge is set on login but stays stale during the session. → Mitigation: re-query via filter `AuthFilter` on each request for users with approver roles. Cheap query (`COUNT(*)` on indexed `Status` column).

- **[Concurrency on approve]** Two managers approving simultaneously could both succeed. → Mitigation: in `PurchaseService.approve`, re-load the row inside the transaction with `SELECT ... FOR UPDATE` and re-check `status == PENDING_APPROVAL`. Out of scope for v1 if MySQL transaction isolation already serializes; accept the race for now and document.

- **[Breaking legacy lowercase status]** Any code path still reading `"confirmed"` will break. → Mitigation: grep for `"confirmed"`, `"received"`, `"draft"` in JSPs and Java code; migrate or delete.

- **[Partial receive over-receive]** Receiving more than ordered. → Mitigation: server clamps `submittedQty <= order.quantity - order.receivedQuantity` and rejects with `ValidationException` if exceeded.

## Migration Plan

1. Code change is backward-compatible at the DB layer (schema already supports both old and new statuses)
2. SQL one-liner to migrate any remaining lowercase rows: `UPDATE PurchaseOrder SET Status = UPPER(Status) WHERE Status IN ('draft','confirmed','received','cancelled')` — already in `DBFinora_Extension.sql`
3. Deploy WAR; test login → create PO → submit → approve → receive → complete
4. Rollback: revert WAR; the new servlet routes simply 404

## Open Questions

None blocking. Future work: notification, multi-level chain, delegation.
