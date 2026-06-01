## 1. Backend foundation hardening

- [x] 1.1 Add `ApprovalService.canApprove(currentStatus, userRole, creatorId, approverId, totalAmount)` overload that enforces threshold (≥50M VND requires Owner) using `AppConstants.OWNER_APPROVAL_THRESHOLD`
- [x] 1.2 Update `PurchaseService.approve(...)` to call the new overload and surface a Vietnamese error message when blocked by threshold
- [x] 1.3 Add `PurchaseOrderDAO.findPendingApprovals(filter, pagination)` returning rows with `status = 'PENDING_APPROVAL'` joined with supplier name and submitter name
- [x] 1.4 Add `PurchaseOrderDAO.countByStatus(status)` for the navbar badge query
- [x] 1.5 Add `PurchaseOrderDAO.countPendingForApprover(role, employeeId)` (excludes own creations)
- [x] 1.6 Add unit test `ApprovalServiceThresholdTest` covering: Manager <50M ok, Manager ≥50M blocked, Owner any amount ok, Creator self-approve blocked ← (verify: all four scenarios pass; threshold value sourced from AppConstants)

## 2. Servlet wiring

- [x] 2.1 Extend `PurchaseServlet` `doGet` switch to handle `action=edit`, `action=receive`, `action=print`
- [x] 2.2 Extend `PurchaseServlet` `doPost` switch to handle `action=submit`, `action=approve`, `action=reject`, `action=cancel`, `action=update`, `action=receiveSubmit`
- [x] 2.3 Each POST handler MUST: re-load PO from DB, re-check permission via `ApprovalService.canXxx`, call corresponding `PurchaseService.xxx`, set flash message, PRG redirect to `?action=view&id={id}`
- [x] 2.4 Each handler catches `ValidationException` / `ServiceException`, sets error flash, redirects (never renders JSP directly on POST)
- [x] 2.5 Replace `ApprovalPreviewServlet` mock data with real DAO calls: `PurchaseOrderDAO.findPendingApprovals` for inbox, `ApprovalHistoryDAO.findByDocument` for detail timeline, `ApprovalHistoryDAO.search` for history page
- [x] 2.6 Add filter `AuthFilter` (or update existing) to populate `session.pendingApprovalCount` for users with approver roles on every request ← (verify: badge updates immediately after approve/reject without manual session refresh)

## 3. JSP — purchase-detail.jsp upgrade

- [x] 3.1 Replace existing simple status badge with full `DocumentStatus`-based pill (DRAFT gray, PENDING_APPROVAL amber, APPROVED green, RECEIVING blue, COMPLETED dark green, REJECTED red, CANCELLED slate)
- [x] 3.2 Render approval timeline section using JSTL `<c:forEach>` over `${approvalHistory}` (already exists in current JSP scaffolding — confirm)
- [x] 3.3 Add conditional action buttons block using `<c:choose>` matching the matrix in spec.md Requirement "Conditional Action Buttons"
- [x] 3.4 Render threshold hint badge "Cần Owner duyệt" when `${order.totalAmount >= ownerThreshold}`
- [x] 3.5 Each action button posts to its servlet route with hidden `csrf` token (use existing pattern) and `confirm()` JS for cancel/reject
- [x] 3.6 Reject and Cancel buttons open a modal with `<textarea name="reason" required minlength="5">`

## 4. JSP — new pages

- [x] 4.1 Create `purchase-edit.jsp` mirroring `purchase-create.jsp` layout but pre-filled from `${order}` and only rendered when `${order.statusEnum == 'DRAFT'}` (servlet redirects with error if not)
- [x] 4.2 Create `purchase-receive.jsp` with one row per detail line, input `name="receivedQty[${detail.poDetailId}]" min="0" max="${detail.outstandingQuantity}"`, sticky bottom action bar with "Lưu nhận hàng"
- [x] 4.3 Create `purchase-print.jsp` standalone page (no header/navbar/footer), `@media print` styles, `window.print()` on body load, "Đóng" button after print
- [x] 4.4 Update `purchases.jsp` filter dropdown options to use uppercase `DocumentStatus` values + Vietnamese labels ← (verify: all 7 statuses selectable, server filter SQL matches)

## 5. Approval inbox real data

- [x] 5.1 Update `pending-approvals.jsp` data binding from mock fields to real `PendingApprovalItem` DTO (documentType, documentId, documentCode, submitterName, submittedAt, totalAmount, description)
- [x] 5.2 Update `approval-detail.jsp`: when `documentType=PURCHASE_ORDER`, query `PurchaseOrderDAO.getById(documentId)` and render real PO header + line items
- [x] 5.3 Update `approval-history.jsp` to use `ApprovalHistoryDAO.search(filter, pagination)` with optional documentType / action / performer / date filters
- [x] 5.4 Update stat cards on `approval-history.jsp` to use real counts from `ApprovalHistoryDAO.countByAction()` (total, approved, rejected, cancelled) ← (verify: all 3 approval JSPs render real DB data, counts match seed data)

## 6. Navbar badge integration

- [x] 6.1 Update `navbar.jsp` to read `${sessionScope.pendingApprovalCount}` instead of hardcoded value
- [x] 6.2 Hide "Phê duyệt" tab entirely for roles outside {Owner, StoreManager}
- [x] 6.3 Style: red dot only when count > 0; no badge when count == 0

## 7. Verification

- [x] 7.1 Run `mvn clean package -DskipTests` — must succeed without compile errors
- [x] 7.2 Run `mvn test` — all unit tests pass including new `ApprovalServiceThresholdTest`
- [x] 7.3 Manual E2E via Playwright: login as creator → create PO → submit → logout → login as approver → approve via inbox → logout → login as warehouse → receive partial → receive remaining → verify status COMPLETED ← (verify: full happy path completes, all transitions logged in ApprovalHistory, stock updated correctly)
- [x] 7.4 Manual E2E negative cases: creator cannot self-approve, Manager cannot approve ≥50M, reject without reason fails, over-receive rejected
- [ ] 7.5 Update GitHub issue #36 with completion comment, close, move to Done on Project board
