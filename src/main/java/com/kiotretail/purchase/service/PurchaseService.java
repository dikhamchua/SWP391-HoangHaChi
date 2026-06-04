package com.kiotretail.purchase.service;

import com.kiotretail.product.dao.SupplierDAO;
import com.kiotretail.product.model.Supplier;
import com.kiotretail.purchase.constant.PurchaseOrderAction;
import com.kiotretail.purchase.constant.PurchaseOrderStatus;
import com.kiotretail.purchase.dao.ActivityPurchaseOrderDAO;
import com.kiotretail.purchase.dao.PurchaseOrderDAO;
import com.kiotretail.purchase.dao.PurchaseOrderDetailDAO;
import com.kiotretail.purchase.dao.PurchaseOrderHistoryDAO;
import com.kiotretail.purchase.dto.PurchaseFilterDTO;
import com.kiotretail.purchase.model.PurchaseOrder;
import com.kiotretail.purchase.model.PurchaseOrderDetail;
import com.kiotretail.purchase.model.ActivityPurchaseOrder;
import com.kiotretail.purchase.model.PurchaseOrderHistory;
import com.kiotretail.purchase.util.PurchaseOrderCodeGenerator;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PurchaseService extends com.kiotretail.shared.base.BaseApprovableService {

    @Override
    protected String getDocumentType() {
        return AppConstants.DOC_TYPE_PURCHASE_ORDER;
    }

    private final PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private final PurchaseOrderDetailDAO detailDAO = new PurchaseOrderDetailDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final PurchaseOrderHistoryDAO historyDAO = new PurchaseOrderHistoryDAO();
    private final ActivityPurchaseOrderDAO activityDAO = new ActivityPurchaseOrderDAO();
    private final StockUpdater stockUpdater = new StockUpdater();

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    public PageResult<PurchaseOrder> listOrders(PurchaseFilterDTO filter, Pagination pagination) {
        List<PurchaseOrder> items = purchaseOrderDAO.search(filter, pagination);
        int total = purchaseOrderDAO.countAll(filter);
        return PageResult.of(items, total, pagination);
    }

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
        order.setStatus(PurchaseOrderStatus.DRAFT.name());
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

        logTransition(newId, null, PurchaseOrderStatus.DRAFT, PurchaseOrderAction.CREATE, userId, null);
        recordActivity(newId, "add", userId, "Tạo phiếu nhập mới");
        return newId;
    }

    public int createOrder(PurchaseOrder order, List<PurchaseOrderDetail> details) {
        int userId = order != null ? order.getEmployeeId() : 0;
        return createDraft(order, details, userId);
    }

    public void updateDraft(int purchaseOrderId, PurchaseOrder order,
                            List<PurchaseOrderDetail> details, int userId) {
        validateOrder(order, details);
        if (userId <= 0) {
            throw new ValidationException("Thieu thong tin nguoi thuc hien");
        }

        PurchaseOrder existing = getOrderById(purchaseOrderId);
        if (existing.getStatusEnum() != PurchaseOrderStatus.DRAFT) {
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
        recordActivity(purchaseOrderId, "update", userId, "Cập nhật phiếu nhập");
    }

    public boolean submit(int purchaseOrderId, int userId) {
        PurchaseOrder po = getOrderById(purchaseOrderId);
        PurchaseOrderStatus current = po.getStatusEnum();
        if (!canSubmit(current.name())) {
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
            logTransition(purchaseOrderId, current, PurchaseOrderStatus.PENDING_APPROVAL,
                    PurchaseOrderAction.SUBMIT, userId, null);
            recordActivity(purchaseOrderId, "other", userId, "Gửi phiếu lên cấp quản lý để phê duyệt");
        }
        return ok;
    }

    // ---------------------------------------------------------------------
    // Approve / reject / cancel
    // ---------------------------------------------------------------------

    public boolean approve(int purchaseOrderId, int userId, String userRole) {
        PurchaseOrder po = getOrderById(purchaseOrderId);
        PurchaseOrderStatus current = po.getStatusEnum();
        int creatorId = po.getCreatedBy() != null ? po.getCreatedBy() : 0;

        if (!canApprove(current.name(), userRole, creatorId, userId)) {
            if (creatorId == userId) {
                throw new ValidationException(ErrorMessages.PO_CREATOR_CANNOT_APPROVE);
            }
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        if (requiresOwnerApproval(po.getTotalAmount()) && !AppConstants.ROLE_OWNER.equals(userRole)) {
            throw new ValidationException(ErrorMessages.PO_OWNER_REQUIRED);
        }

        boolean ok = purchaseOrderDAO.updateApproval(purchaseOrderId, userId);
        if (ok) {
            logTransition(purchaseOrderId, current, PurchaseOrderStatus.APPROVED,
                    PurchaseOrderAction.APPROVE, userId, null);
            recordActivity(purchaseOrderId, "other", userId, "Phê duyệt phiếu nhập");
        }
        return ok;
    }

    public boolean reject(int purchaseOrderId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        PurchaseOrder po = getOrderById(purchaseOrderId);
        PurchaseOrderStatus current = po.getStatusEnum();
        if (!canReject(current.name(), userRole)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = purchaseOrderDAO.updateRejection(purchaseOrderId, userId, reason.trim());
        if (ok) {
            logTransition(purchaseOrderId, current, PurchaseOrderStatus.REJECTED,
                    PurchaseOrderAction.REJECT, userId, reason.trim());
            recordActivity(purchaseOrderId, "other", userId, "Từ chối phiếu nhập: " + reason.trim());
        }
        return ok;
    }

    public boolean cancel(int purchaseOrderId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        PurchaseOrder po = getOrderById(purchaseOrderId);
        PurchaseOrderStatus current = po.getStatusEnum();
        boolean isCreator = po.getCreatedBy() != null && po.getCreatedBy() == userId;
        if (!canCancel(current.name(), userRole, isCreator)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = purchaseOrderDAO.updateCancellation(purchaseOrderId, userId, reason.trim());
        if (ok) {
            logTransition(purchaseOrderId, current, PurchaseOrderStatus.CANCELLED,
                    PurchaseOrderAction.CANCEL, userId, reason.trim());
            recordActivity(purchaseOrderId, "other", userId, "Hủy phiếu nhập: " + reason.trim());
        }
        return ok;
    }

    public boolean cancelOrder(int purchaseOrderId) {
        return purchaseOrderDAO.updateStatus(purchaseOrderId, PurchaseOrderStatus.CANCELLED.name());
    }

    public boolean confirmOrder(int purchaseOrderId) {
        return purchaseOrderDAO.updateStatus(purchaseOrderId, PurchaseOrderStatus.APPROVED.name());
    }

    // ---------------------------------------------------------------------
    // Receive goods (stock-in)
    // ---------------------------------------------------------------------

    public boolean receive(int purchaseOrderId, Map<Integer, Integer> receivedByDetail, int userId) {
        if (receivedByDetail == null || receivedByDetail.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_RECEIVE_INVALID_QTY);
        }
        PurchaseOrder po = getOrderById(purchaseOrderId);
        PurchaseOrderStatus current = po.getStatusEnum();
        if (current != PurchaseOrderStatus.APPROVED && current != PurchaseOrderStatus.RECEIVING) {
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

        PurchaseOrderStatus next = allFulfilled ? PurchaseOrderStatus.COMPLETED : PurchaseOrderStatus.RECEIVING;
        boolean ok;
        PurchaseOrderAction action;
        if (next == PurchaseOrderStatus.COMPLETED) {
            ok = purchaseOrderDAO.updateCompletedAt(purchaseOrderId);
            action = PurchaseOrderAction.COMPLETE;
        } else {
            ok = purchaseOrderDAO.updateStatus(purchaseOrderId, PurchaseOrderStatus.RECEIVING.name());
            action = PurchaseOrderAction.RECEIVE;
        }
        if (ok) {
            logTransition(purchaseOrderId, current, next, action, userId, null);
            String desc = (next == PurchaseOrderStatus.COMPLETED) ? "Hoàn tất nhận hàng" : "Nhận hàng một phần";
            recordActivity(purchaseOrderId, "other", userId, desc);
        }
        return ok;
    }

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
        boolean ok = purchaseOrderDAO.updateCompletedAt(purchaseOrderId);
        if (ok) {
            recordActivity(purchaseOrderId, "other", null, "Nhận toàn bộ hàng và cập nhật tồn kho");
        }
        return ok;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void logTransition(int purchaseOrderId,
                               PurchaseOrderStatus from,
                               PurchaseOrderStatus to,
                               PurchaseOrderAction action,
                               int performedBy,
                               String reason) {
        PurchaseOrderHistory entry = new PurchaseOrderHistory();
        entry.setPurchaseOrderId(purchaseOrderId);
        entry.setFromStatus(from == null ? null : from.name());
        entry.setToStatus(to.name());
        entry.setAction(action.name());
        entry.setPerformedBy(performedBy);
        entry.setReason(reason);

        int newId = historyDAO.insert(entry);
        if (newId <= 0) {
            throw new ServiceException("Failed to log purchase order transition", 500);
        }
    }

    // ---------------------------------------------------------------------
    // Activity logging
    // ---------------------------------------------------------------------

    /**
     * Records an activity for a purchase order.
     */
    private void recordActivity(int fkId, String type, Integer createdBy, String description) {
        if (fkId <= 0) {
            return;
        }
        ActivityPurchaseOrder activity = new ActivityPurchaseOrder();
        activity.setFkId(fkId);
        activity.setType(type);
        activity.setCreatedBy(createdBy);
        activity.setDescription(description);
        activityDAO.insert(activity);
    }

    /**
     * Returns all activities for a purchase order, newest first.
     */
    public List<ActivityPurchaseOrder> getActivitiesByOrderId(int purchaseOrderId) {
        return activityDAO.getByFkId(purchaseOrderId);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

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