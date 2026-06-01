package com.kiotretail.purchase.service;

import com.kiotretail.product.dao.SupplierDAO;
import com.kiotretail.product.model.Supplier;
import com.kiotretail.purchase.dao.PurchaseOrderDAO;
import com.kiotretail.purchase.dao.PurchaseOrderDetailDAO;
import com.kiotretail.purchase.dto.PurchaseFilterDTO;
import com.kiotretail.purchase.model.PurchaseOrder;
import com.kiotretail.purchase.model.PurchaseOrderDetail;
import com.kiotretail.purchase.util.PurchaseOrderCodeGenerator;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ApprovalAction;
import com.kiotretail.shared.constant.DocumentStatus;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.service.ApprovalService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service that orchestrates PurchaseOrder + PurchaseOrderDetail flow with
 * the full approval workflow (UC-4.2 / UC-4.3).
 *
 * <p>State machine:</p>
 * <pre>
 *   DRAFT -> PENDING_APPROVAL -> APPROVED -> RECEIVING -> COMPLETED
 *                            \-> REJECTED
 *                                                          \-> CANCELLED
 *   DRAFT -> CANCELLED  (by creator or approver)
 *   APPROVED / RECEIVING -> CANCELLED  (Owner only)
 * </pre>
 *
 * <p>Rules enforced here (see {@link ApprovalService} for details):</p>
 * <ul>
 *   <li>Creator cannot approve their own order (segregation of duties).</li>
 *   <li>Orders &ge; {@link AppConstants#OWNER_APPROVAL_THRESHOLD} require
 *       the {@code Owner} role to approve.</li>
 *   <li>Every transition writes an {@code ApprovalHistory} row.</li>
 * </ul>
 */
public class PurchaseService {

    private final PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private final PurchaseOrderDetailDAO detailDAO = new PurchaseOrderDetailDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ApprovalService approvalService = new ApprovalService();
    private final StockUpdater stockUpdater = new StockUpdater();

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    public PageResult<PurchaseOrder> listOrders(PurchaseFilterDTO filter, Pagination pagination) {
        List<PurchaseOrder> items = purchaseOrderDAO.search(filter, pagination);
        int total = purchaseOrderDAO.countAll(filter);
        return PageResult.of(items, total, pagination);
    }

    /** Backward compat overload for legacy callers. */
    public PageResult<PurchaseOrder> listOrders(String keyword, String status, Pagination pagination) {
        PurchaseFilterDTO filter = new PurchaseFilterDTO();
        filter.setKeyword(keyword);
        filter.setStatus(status);
        return listOrders(filter, pagination);
    }

    public PurchaseOrder getOrderById(int id) {
        PurchaseOrder po = purchaseOrderDAO.getById(id);
        if (po == null) {
            throw new NotFoundException("PurchaseOrder", id);
        }
        return po;
    }

    public List<PurchaseOrderDetail> getOrderDetails(int purchaseOrderId) {
        return detailDAO.getByOrderId(purchaseOrderId);
    }

    // ---------------------------------------------------------------------
    // Create / submit
    // ---------------------------------------------------------------------

    /**
     * Create a draft purchase order with detail lines. Generates the order
     * code automatically when blank, computes line subtotals + total, and
     * records an ApprovalHistory CREATE row.
     */
    public int createDraft(PurchaseOrder order, List<PurchaseOrderDetail> details, int userId) {
        validateOrder(order, details);
        if (userId <= 0) {
            throw new ValidationException("Thieu thong tin nguoi tao");
        }

        Supplier supplier = supplierDAO.getById(order.getSupplierId());
        if (supplier == null) {
            throw new ValidationException("Nha cung cap khong ton tai");
        }

        if (order.getOrderCode() == null || order.getOrderCode().trim().isEmpty()) {
            order.setOrderCode(PurchaseOrderCodeGenerator.generate(purchaseOrderDAO));
        }
        order.setStatus(DocumentStatus.DRAFT.name());
        order.setCreatedBy(userId);
        if (order.getEmployeeId() <= 0) {
            order.setEmployeeId(userId);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderDetail d : details) {
            BigDecimal sub = computeSubtotal(d);
            d.setSubtotal(sub);
            total = total.add(sub);
        }
        order.setTotalAmount(total);

        int newId = purchaseOrderDAO.insert(order);
        if (newId <= 0) {
            throw new ValidationException("Khong the tao phieu nhap");
        }

        for (PurchaseOrderDetail d : details) {
            d.setPurchaseOrderId(newId);
            if (!detailDAO.insert(d)) {
                throw new ValidationException("Khong the them chi tiet phieu nhap");
            }
        }
        purchaseOrderDAO.recalculateTotal(newId);

        approvalService.logTransition(
                AppConstants.DOC_TYPE_PURCHASE_ORDER,
                newId,
                null,
                DocumentStatus.DRAFT,
                ApprovalAction.CREATE,
                userId,
                null);
        return newId;
    }

    /**
     * Legacy alias kept so older callers (servlet / tests) continue to compile.
     * Defers to {@link #createDraft(PurchaseOrder, List, int)} using the
     * order's employeeId as the creator.
     */
    public int createOrder(PurchaseOrder order, List<PurchaseOrderDetail> details) {
        int userId = order != null ? order.getEmployeeId() : 0;
        return createDraft(order, details, userId);
    }

    /**
     * Updates an editable (DRAFT) purchase order: header fields plus a full
     * replacement of its detail lines. Recomputes the total from the new lines
     * and refuses to touch orders that have left DRAFT.
     */
    public void updateDraft(int purchaseOrderId, PurchaseOrder order,
                            List<PurchaseOrderDetail> details, int userId) {
        validateOrder(order, details);
        if (userId <= 0) {
            throw new ValidationException("Thieu thong tin nguoi thuc hien");
        }

        PurchaseOrder existing = getOrderById(purchaseOrderId);
        if (existing.getStatusEnum() != DocumentStatus.DRAFT) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }

        Supplier supplier = supplierDAO.getById(order.getSupplierId());
        if (supplier == null) {
            throw new ValidationException("Nha cung cap khong ton tai");
        }

        order.setPurchaseOrderId(purchaseOrderId);
        if (!purchaseOrderDAO.update(order)) {
            throw new ValidationException("Khong the cap nhat phieu nhap");
        }

        detailDAO.deleteByOrderId(purchaseOrderId);
        for (PurchaseOrderDetail d : details) {
            d.setPurchaseOrderId(purchaseOrderId);
            d.setSubtotal(computeSubtotal(d));
            if (!detailDAO.insert(d)) {
                throw new ValidationException("Khong the cap nhat chi tiet phieu nhap");
            }
        }
        purchaseOrderDAO.recalculateTotal(purchaseOrderId);
    }

    public boolean submit(int purchaseOrderId, int userId) {
        PurchaseOrder po = getOrderById(purchaseOrderId);
        DocumentStatus current = po.getStatusEnum();
        if (!approvalService.canSubmit(current)) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }
        if (detailDAO.getByOrderId(purchaseOrderId).isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        if (po.getCreatedBy() != null && po.getCreatedBy() != userId) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = purchaseOrderDAO.updateSubmittedAt(purchaseOrderId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_PURCHASE_ORDER,
                    purchaseOrderId,
                    current,
                    DocumentStatus.PENDING_APPROVAL,
                    ApprovalAction.SUBMIT,
                    userId,
                    null);
        }
        return ok;
    }

    // ---------------------------------------------------------------------
    // Approve / reject / cancel
    // ---------------------------------------------------------------------

    public boolean approve(int purchaseOrderId, int userId, String userRole) {
        PurchaseOrder po = getOrderById(purchaseOrderId);
        DocumentStatus current = po.getStatusEnum();
        int creatorId = po.getCreatedBy() != null ? po.getCreatedBy() : 0;

        if (!approvalService.canApprove(current, userRole, creatorId, userId)) {
            if (creatorId == userId) {
                throw new ValidationException(ErrorMessages.PO_CREATOR_CANNOT_APPROVE);
            }
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        if (requiresOwner(po) && !AppConstants.ROLE_OWNER.equals(userRole)) {
            throw new ValidationException(ErrorMessages.PO_OWNER_REQUIRED);
        }

        boolean ok = purchaseOrderDAO.updateApproval(purchaseOrderId, userId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_PURCHASE_ORDER,
                    purchaseOrderId,
                    current,
                    DocumentStatus.APPROVED,
                    ApprovalAction.APPROVE,
                    userId,
                    null);
        }
        return ok;
    }

    public boolean reject(int purchaseOrderId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        PurchaseOrder po = getOrderById(purchaseOrderId);
        DocumentStatus current = po.getStatusEnum();
        if (!approvalService.canReject(current, userRole)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = purchaseOrderDAO.updateRejection(purchaseOrderId, userId, reason.trim());
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_PURCHASE_ORDER,
                    purchaseOrderId,
                    current,
                    DocumentStatus.REJECTED,
                    ApprovalAction.REJECT,
                    userId,
                    reason.trim());
        }
        return ok;
    }

    public boolean cancel(int purchaseOrderId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        PurchaseOrder po = getOrderById(purchaseOrderId);
        DocumentStatus current = po.getStatusEnum();
        boolean isCreator = po.getCreatedBy() != null && po.getCreatedBy() == userId;
        if (!approvalService.canCancel(current, userRole, isCreator)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = purchaseOrderDAO.updateCancellation(purchaseOrderId, userId, reason.trim());
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_PURCHASE_ORDER,
                    purchaseOrderId,
                    current,
                    DocumentStatus.CANCELLED,
                    ApprovalAction.CANCEL,
                    userId,
                    reason.trim());
        }
        return ok;
    }

    /** Backward compat: legacy single-arg cancel used by older controllers. */
    public boolean cancelOrder(int purchaseOrderId) {
        return purchaseOrderDAO.updateStatus(purchaseOrderId, DocumentStatus.CANCELLED.name());
    }

    /** Backward compat: legacy confirm used by older controllers. */
    public boolean confirmOrder(int purchaseOrderId) {
        return purchaseOrderDAO.updateStatus(purchaseOrderId, DocumentStatus.APPROVED.name());
    }

    // ---------------------------------------------------------------------
    // Receive goods (stock-in)
    // ---------------------------------------------------------------------

    /**
     * Receive goods against an APPROVED or RECEIVING order. The supplied
     * map carries detailId -&gt; received-this-time quantity. Stock is
     * incremented per product. Order moves to RECEIVING when partial or
     * COMPLETED when every line is fully received.
     */
    public boolean receive(int purchaseOrderId, Map<Integer, Integer> receivedByDetail, int userId) {
        if (receivedByDetail == null || receivedByDetail.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_RECEIVE_INVALID_QTY);
        }
        PurchaseOrder po = getOrderById(purchaseOrderId);
        DocumentStatus current = po.getStatusEnum();
        if (current != DocumentStatus.APPROVED && current != DocumentStatus.RECEIVING) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }

        List<PurchaseOrderDetail> lines = detailDAO.getByOrderId(purchaseOrderId);
        if (lines.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }

        boolean allFulfilled = true;
        for (PurchaseOrderDetail line : lines) {
            Integer addQtyObj = receivedByDetail.get(line.getPoDetailId());
            int addQty = addQtyObj != null ? addQtyObj : 0;
            if (addQty < 0) {
                throw new ValidationException(ErrorMessages.PO_RECEIVE_INVALID_QTY);
            }
            int newReceived = line.getReceivedQuantity() + addQty;
            if (newReceived > line.getQuantity()) {
                throw new ValidationException(ErrorMessages.PO_RECEIVE_OVER_ORDERED);
            }
            if (addQty > 0) {
                if (!detailDAO.updateReceivedQuantity(line.getPoDetailId(), newReceived)) {
                    throw new ValidationException("Khong the cap nhat so luong nhan");
                }
                if (!stockUpdater.increaseStock(line.getProductId(), addQty)) {
                    throw new ValidationException(
                            "Khong the cap nhat ton kho cho san pham ID=" + line.getProductId());
                }
            }
            if (newReceived < line.getQuantity()) {
                allFulfilled = false;
            }
        }

        DocumentStatus next = allFulfilled ? DocumentStatus.COMPLETED : DocumentStatus.RECEIVING;
        boolean ok;
        ApprovalAction action;
        if (next == DocumentStatus.COMPLETED) {
            ok = purchaseOrderDAO.updateCompletedAt(purchaseOrderId);
            action = ApprovalAction.COMPLETE;
        } else {
            ok = purchaseOrderDAO.updateStatus(purchaseOrderId, DocumentStatus.RECEIVING.name());
            action = ApprovalAction.RECEIVE;
        }
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_PURCHASE_ORDER,
                    purchaseOrderId,
                    current,
                    next,
                    action,
                    userId,
                    null);
        }
        return ok;
    }

    /** Backward compat: full-receive shortcut used by legacy controllers. */
    public boolean receiveGoods(int purchaseOrderId) {
        List<PurchaseOrderDetail> lines = detailDAO.getByOrderId(purchaseOrderId);
        if (lines.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        for (PurchaseOrderDetail line : lines) {
            int outstanding = line.getOutstandingQuantity();
            if (outstanding <= 0) continue;
            stockUpdater.increaseStock(line.getProductId(), outstanding);
            detailDAO.updateReceivedQuantity(line.getPoDetailId(), line.getQuantity());
        }
        return purchaseOrderDAO.updateCompletedAt(purchaseOrderId);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private boolean requiresOwner(PurchaseOrder po) {
        BigDecimal total = po.getTotalAmount() != null ? po.getTotalAmount() : BigDecimal.ZERO;
        return total.compareTo(AppConstants.OWNER_APPROVAL_THRESHOLD) >= 0;
    }

    private BigDecimal computeSubtotal(PurchaseOrderDetail d) {
        BigDecimal cost = d.getUnitCost() != null ? d.getUnitCost() : BigDecimal.ZERO;
        return cost.multiply(BigDecimal.valueOf(d.getQuantity()));
    }

    private void validateOrder(PurchaseOrder order, List<PurchaseOrderDetail> details) {
        if (order == null) {
            throw new ValidationException("Phieu nhap khong duoc rong");
        }
        if (order.getSupplierId() <= 0) {
            throw new ValidationException("Vui long chon nha cung cap");
        }
        if (order.getBranchId() <= 0) {
            throw new ValidationException("Vui long chon chi nhanh");
        }
        if (details == null || details.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        for (PurchaseOrderDetail d : details) {
            if (d.getProductId() <= 0) {
                throw new ValidationException("Vui long chon san pham hop le");
            }
            if (d.getQuantity() <= 0) {
                throw new ValidationException("So luong phai lon hon 0");
            }
            if (d.getUnitCost() == null || d.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Don gia khong hop le");
            }
        }
    }

    /**
     * Tiny inline helper that increments Product.StockQuantity on receive.
     */
    private static class StockUpdater extends BaseDAO {
        boolean increaseStock(int productId, int quantity) {
            String sql = "UPDATE Product SET StockQuantity = IFNULL(StockQuantity, 0) + ? " +
                         "WHERE ProductID = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                return stmt.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
