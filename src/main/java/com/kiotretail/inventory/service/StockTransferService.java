package com.kiotretail.inventory.service;

import com.kiotretail.inventory.dao.StockTransferDAO;
import com.kiotretail.inventory.dao.StockTransferDetailDAO;
import com.kiotretail.inventory.dto.StockTransferFilterDTO;
import com.kiotretail.inventory.model.StockTransfer;
import com.kiotretail.inventory.model.StockTransferDetail;
import com.kiotretail.inventory.util.TransferCodeGenerator;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service that orchestrates StockTransfer + StockTransferDetail flow with the
 * full approval + ship + receive workflow (UC-5.3).
 *
 * <p>State machine:</p>
 * <pre>
 *   DRAFT -> PENDING_APPROVAL -> APPROVED -> IN_TRANSIT (ship: -stock src) -> COMPLETED (receive: +stock dst)
 *                            \-> REJECTED
 *   DRAFT / PENDING_APPROVAL  -> CANCELLED
 * </pre>
 *
 * <p>Every transition writes an {@code ApprovalHistory} row via
 * {@link ApprovalService}.</p>
 */
public class StockTransferService {

    private final StockTransferDAO transferDAO = new StockTransferDAO();
    private final StockTransferDetailDAO detailDAO = new StockTransferDetailDAO();
    private final ApprovalService approvalService = new ApprovalService();
    private final StockUpdater stockUpdater = new StockUpdater();

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    public PageResult<StockTransfer> listTransfers(StockTransferFilterDTO filter, Pagination pagination) {
        List<StockTransfer> items = transferDAO.search(filter, pagination);
        int total = transferDAO.countAll(filter);
        return PageResult.of(items, total, pagination);
    }

    public StockTransfer getById(int id) {
        StockTransfer t = transferDAO.getById(id);
        if (t == null) {
            throw new NotFoundException("StockTransfer", id);
        }
        t.setDetails(detailDAO.getByTransferId(id));
        return t;
    }

    // ---------------------------------------------------------------------
    // Create / submit
    // ---------------------------------------------------------------------

    public int createDraft(StockTransfer header, List<StockTransferDetail> details, int userId) {
        validate(header, details);
        if (userId <= 0) {
            throw new ValidationException("Thieu thong tin nguoi tao");
        }

        if (header.getTransferCode() == null || header.getTransferCode().trim().isEmpty()) {
            header.setTransferCode(TransferCodeGenerator.generate(transferDAO));
        }
        header.setStatus(DocumentStatus.DRAFT.name());
        header.setCreatedBy(userId);

        int totalItems = details.size();
        int totalQty = 0;
        for (StockTransferDetail d : details) {
            totalQty += d.getQuantity();
        }
        header.setTotalItems(totalItems);
        header.setTotalQuantity(totalQty);

        int newId = transferDAO.insert(header);
        if (newId <= 0) {
            throw new ValidationException("Khong the tao phieu chuyen kho");
        }

        for (StockTransferDetail d : details) {
            d.setTransferId(newId);
            d.setReceivedQuantity(0);
            if (!detailDAO.insert(d)) {
                throw new ValidationException("Khong the them chi tiet phieu chuyen kho");
            }
        }

        approvalService.logTransition(
                AppConstants.DOC_TYPE_STOCK_TRANSFER,
                newId,
                null,
                DocumentStatus.DRAFT,
                ApprovalAction.CREATE,
                userId,
                null);
        return newId;
    }

    public boolean submit(int transferId, int userId) {
        StockTransfer t = getById(transferId);
        DocumentStatus current = t.getStatusEnum();
        if (!approvalService.canSubmit(current)) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }
        if (detailDAO.getByTransferId(transferId).isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        if (t.getCreatedBy() != null && t.getCreatedBy() != userId) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = transferDAO.updateSubmittedAt(transferId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_TRANSFER,
                    transferId,
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

    public boolean approve(int transferId, int userId, String userRole) {
        StockTransfer t = getById(transferId);
        DocumentStatus current = t.getStatusEnum();
        int creatorId = t.getCreatedBy() != null ? t.getCreatedBy() : 0;

        if (!approvalService.canApprove(current, userRole, creatorId, userId)) {
            if (creatorId == userId) {
                throw new ValidationException(ErrorMessages.PO_CREATOR_CANNOT_APPROVE);
            }
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }

        if (detailDAO.getByTransferId(transferId).isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }

        boolean ok = transferDAO.updateApproval(transferId, userId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_TRANSFER,
                    transferId,
                    current,
                    DocumentStatus.APPROVED,
                    ApprovalAction.APPROVE,
                    userId,
                    null);
        }
        return ok;
    }

    public boolean reject(int transferId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        StockTransfer t = getById(transferId);
        DocumentStatus current = t.getStatusEnum();
        if (!approvalService.canReject(current, userRole)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = transferDAO.updateRejection(transferId, userId, reason.trim());
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_TRANSFER,
                    transferId,
                    current,
                    DocumentStatus.REJECTED,
                    ApprovalAction.REJECT,
                    userId,
                    reason.trim());
        }
        return ok;
    }

    public boolean cancel(int transferId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        StockTransfer t = getById(transferId);
        DocumentStatus current = t.getStatusEnum();
        boolean isCreator = t.getCreatedBy() != null && t.getCreatedBy() == userId;
        if (!approvalService.canCancel(current, userRole, isCreator)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = transferDAO.updateCancellation(transferId, userId, reason.trim());
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_TRANSFER,
                    transferId,
                    current,
                    DocumentStatus.CANCELLED,
                    ApprovalAction.CANCEL,
                    userId,
                    reason.trim());
        }
        return ok;
    }

    // ---------------------------------------------------------------------
    // Ship / receive
    // ---------------------------------------------------------------------

    /**
     * Ship the approved transfer: deduct stock from the source branch
     * (Product.StockQuantity is global today; deduct globally).
     */
    public boolean ship(int transferId, int userId) {
        StockTransfer t = getById(transferId);
        DocumentStatus current = t.getStatusEnum();
        if (current != DocumentStatus.APPROVED) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }
        List<StockTransferDetail> lines = detailDAO.getByTransferId(transferId);
        if (lines.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }

        for (StockTransferDetail line : lines) {
            if (!stockUpdater.adjustStock(line.getProductId(), -line.getQuantity())) {
                throw new ValidationException(
                        "Khong the tru ton kho cho san pham ID=" + line.getProductId());
            }
        }

        boolean ok = transferDAO.updateShipped(transferId, userId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_TRANSFER,
                    transferId,
                    current,
                    DocumentStatus.IN_PROGRESS,
                    ApprovalAction.SHIP,
                    userId,
                    null);
        }
        return ok;
    }

    /**
     * Receive at the destination branch: add stock and persist received quantities.
     * Map key = TransferDetailID, value = received quantity.
     */
    public boolean receive(int transferId, Map<Integer, Integer> receivedQtys, int userId) {
        StockTransfer t = getById(transferId);
        DocumentStatus current = t.getStatusEnum();
        // DB stores IN_TRANSIT; enum maps it to IN_PROGRESS for the workflow.
        if (!"IN_TRANSIT".equalsIgnoreCase(t.getStatus())) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }

        List<StockTransferDetail> lines = detailDAO.getByTransferId(transferId);
        if (lines.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }

        for (StockTransferDetail line : lines) {
            Integer qty = receivedQtys != null ? receivedQtys.get(line.getTransferDetailId()) : null;
            int receivedQty = qty != null ? qty : line.getQuantity();
            if (receivedQty < 0) {
                throw new ValidationException(ErrorMessages.PO_RECEIVE_INVALID_QTY);
            }
            if (receivedQty > line.getQuantity()) {
                throw new ValidationException(ErrorMessages.PO_RECEIVE_OVER_ORDERED);
            }
            detailDAO.updateReceivedQuantity(line.getTransferDetailId(), receivedQty);
            if (receivedQty > 0) {
                if (!stockUpdater.adjustStock(line.getProductId(), receivedQty)) {
                    throw new ValidationException(
                            "Khong the cap nhat ton kho cho san pham ID=" + line.getProductId());
                }
            }
        }

        boolean ok = transferDAO.updateReceived(transferId, userId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_TRANSFER,
                    transferId,
                    current,
                    DocumentStatus.COMPLETED,
                    ApprovalAction.RECEIVE,
                    userId,
                    null);
        }
        return ok;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void validate(StockTransfer header, List<StockTransferDetail> details) {
        if (header == null) {
            throw new ValidationException("Phieu chuyen kho khong duoc rong");
        }
        if (header.getFromBranchId() <= 0) {
            throw new ValidationException("Vui long chon chi nhanh nguon");
        }
        if (header.getToBranchId() <= 0) {
            throw new ValidationException("Vui long chon chi nhanh dich");
        }
        if (header.getFromBranchId() == header.getToBranchId()) {
            throw new ValidationException("Chi nhanh nguon va dich phai khac nhau");
        }
        if (details == null || details.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        for (StockTransferDetail d : details) {
            if (d.getProductId() <= 0) {
                throw new ValidationException("Vui long chon san pham hop le");
            }
            if (d.getQuantity() <= 0) {
                throw new ValidationException("So luong chuyen phai lon hon 0");
            }
        }
    }

    /**
     * Tiny inline helper that adjusts Product.StockQuantity by a signed delta.
     */
    private static class StockUpdater extends BaseDAO {
        boolean adjustStock(int productId, int delta) {
            String sql = "UPDATE Product SET StockQuantity = IFNULL(StockQuantity, 0) + ? " +
                         "WHERE ProductID = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, delta);
                stmt.setInt(2, productId);
                return stmt.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
