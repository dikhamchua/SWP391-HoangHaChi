## Why

GitHub issue #36 (UC-4.2) requires Purchase Order to use the real Approval Workflow that was built in issue #37 (closed). The backend already has `ApprovalService`, `ApprovalHistoryDAO`, `DocumentStatus` enum, and `PurchaseService.submit/approve/reject/cancel` methods. The UI scaffolding from issue #45 (closed) renders mock data only. Today the user-facing controller (`PurchaseServlet`) and approval inbox (`ApprovalPreviewServlet`) bypass these methods, so the workflow is dead code. This change wires the existing backend into the controllers and JSPs so users can actually submit, approve, reject, and partially receive purchase orders end-to-end.

## What Changes

- Wire `PurchaseServlet` actions: `submit`, `approve`, `reject`, `cancel`, `edit`, `receive`, `print`
- Replace `ApprovalPreviewServlet` mock data with real queries against `PurchaseOrderDAO` + `ApprovalHistoryDAO`
- Add new JSPs: `purchase-edit.jsp` (DRAFT-only), `purchase-receive.jsp` (per-line received qty), `purchase-print.jsp` (auto window.print)
- Upgrade `purchase-detail.jsp`: approval timeline, status badge, conditional action buttons by status + role
- Conditional buttons: DRAFT→[Sửa, Gửi duyệt, Hủy], PENDING_APPROVAL→[Duyệt, Từ chối, Hủy], APPROVED→[Nhận hàng, Hủy], RECEIVING→[Nhận tiếp, Hủy], COMPLETED/REJECTED/CANCELLED→read-only
- Threshold display: badge "Cần Owner duyệt" when total ≥ 50M VND
- Navbar badge: replace mock count with real `COUNT(*) WHERE status='PENDING_APPROVAL'` query
- Purchase list filter: support new uppercase statuses (DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, RECEIVING, COMPLETED, CANCELLED)
- **BREAKING**: `purchase-detail.jsp` no longer references legacy lowercase statuses (`confirmed`, `received`); existing rows must be migrated via SQL script (already done in `DBFinora_Extension.sql`)

## Capabilities

### New Capabilities
- `purchase-approval-workflow`: End-to-end approval lifecycle for Purchase Orders — submit, approve/reject with reason, role-based threshold gating, separation of duties, and partial receiving against approved orders.

### Modified Capabilities
<!-- No existing approval spec exists in openspec/specs yet; this is the first formal spec. -->

## Impact

**Code (modified):**
- `src/main/java/com/kiotretail/purchase/controller/PurchaseServlet.java` — add submit/approve/reject/cancel/edit/receive/print actions
- `src/main/java/com/kiotretail/shared/controller/ApprovalPreviewServlet.java` — replace with real DAO queries (rename to `ApprovalServlet` or keep file but remove mock)
- `src/main/webapp/WEB-INF/views/purchase/purchase-detail.jsp` — timeline + conditional buttons
- `src/main/webapp/WEB-INF/views/purchase/purchases.jsp` — uppercase status filter
- `src/main/webapp/WEB-INF/views/common/navbar.jsp` — real pending count

**Code (new):**
- `src/main/webapp/WEB-INF/views/purchase/purchase-edit.jsp`
- `src/main/webapp/WEB-INF/views/purchase/purchase-receive.jsp`
- `src/main/webapp/WEB-INF/views/purchase/purchase-print.jsp`

**APIs (servlet routes):**
- `POST /admin/purchases?action=submit&id={id}`
- `POST /admin/purchases?action=approve&id={id}`
- `POST /admin/purchases?action=reject&id={id}` (body: reason)
- `POST /admin/purchases?action=cancel&id={id}` (body: reason)
- `GET  /admin/purchases?action=edit&id={id}`
- `POST /admin/purchases?action=update&id={id}`
- `GET  /admin/purchases?action=receive&id={id}`
- `POST /admin/purchases?action=receiveSubmit&id={id}` (body: receivedQty per line)
- `GET  /admin/purchases?action=print&id={id}`
- `GET  /admin/approvals?action=pending` (now real)
- `GET  /admin/approvals?action=detail&type=PURCHASE_ORDER&id={id}` (now real)
- `POST /admin/approvals?action=approve|reject` (now real)

**Dependencies:**
- Reuses existing `ApprovalService`, `ApprovalHistoryDAO`, `DocumentStatus` enum, `PurchaseService.submit/approve/reject/cancel`
- No new third-party libraries
- No DB migration (schema already prepared in issue #37)

**Risk:**
- Threshold logic (50M VND) must match `AppConstants.OWNER_APPROVAL_THRESHOLD`
- Separation of duties enforced server-side in `ApprovalService.canApprove` — must not be bypassable from JSP
- Partial receiving stock updates must run inside transaction with status update to avoid drift between `Product.stockQuantity` and `PurchaseOrderDetail.receivedQuantity`
