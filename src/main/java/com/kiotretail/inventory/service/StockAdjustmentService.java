package com.kiotretail.inventory.service;

import com.kiotretail.inventory.dao.StockAdjustmentDAO;
import com.kiotretail.inventory.dao.StockAdjustmentDetailDAO;
import com.kiotretail.inventory.dto.StockAdjustmentFilterDTO;
import com.kiotretail.inventory.model.StockAdjustment;
import com.kiotretail.inventory.model.StockAdjustmentDetail;
import com.kiotretail.inventory.util.AdjustmentCodeGenerator;
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

/**
 * Service that orchestrates StockAdjustment + StockAdjustmentDetail flow with
 * the full approval workflow (UC-5.1 / UC-5.2).
 *
 * <p>State machine:</p>
 * <pre>
 *   DRAFT -> PENDING_APPROVAL -> APPROVED  (applies variance to Product.StockQuantity)
 *                            \-> REJECTED
 *   DRAFT / PENDING_APPROVAL -> CANCELLED  (creator or approver)
 * </pre>
 *
 * <p>Every transition writes an {@code ApprovalHistory} row via
 * {@link ApprovalService}.</p>
 */
public class StockAdjustmentService {

    private final StockAdjustmentDAO adjustmentDAO = new StockAdjustmentDAO();
    private final StockAdjustmentDetailDAO detailDAO = new StockAdjustmentDetailDAO();
    private final ApprovalService approvalService = new ApprovalService();
    private final StockUpdater stockUpdater = new StockUpdater();

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    public PageResult<StockAdjustment> listAdjustments(StockAdjustmentFilterDTO filter, Pagination pagination) {
        List<StockAdjustment> items = adjustmentDAO.search(filter, pagination);
        int total = adjustmentDAO.countAll(filter);
        return PageResult.of(items, total, pagination);
    }

    public StockAdjustment getById(int id) {
        StockAdjustment adj = adjustmentDAO.getById(id);
        if (adj == null) {
            throw new NotFoundException("StockAdjustment", id);
        }
        adj.setDetails(detailDAO.getByAdjustmentId(id));
        return adj;
    }

    // ---------------------------------------------------------------------
    // Create / submit
    // ---------------------------------------------------------------------

    /**
     * Create a draft stock adjustment with detail lines. Generates the code
     * automatically when blank, computes variance + variance value per line,
     * and records an ApprovalHistory CREATE row.
     */
    public int createDraft(StockAdjustment header, List<StockAdjustmentDetail> details, int userId) {
        validate(header, details);
        if (userId <= 0) {
            throw new ValidationException("Thieu thong tin nguoi tao");
        }

        if (header.getAdjustmentCode() == null || header.getAdjustmentCode().trim().isEmpty()) {
            header.setAdjustmentCode(AdjustmentCodeGenerator.generate(adjustmentDAO));
        }
        header.setStatus(DocumentStatus.DRAFT.name());
        header.setCreatedBy(userId);

        BigDecimal totalVariance = BigDecimal.ZERO;
        for (StockAdjustmentDetail d : details) {
            int variance = d.getActualQuantity() - d.getSystemQuantity();
            d.setVariance(variance);
            if (d.getVarianceValue() == null) {
                d.setVarianceValue(BigDecimal.ZERO);
            }
            totalVariance = totalVariance.add(d.getVarianceValue());
        }
        header.setTotalVarianceValue(totalVariance);

        int newId = adjustmentDAO.insert(header);
        if (newId <= 0) {
            throw new ValidationException("Khong the tao phieu kiem ke");
        }

        for (StockAdjustmentDetail d : details) {
            d.setAdjustmentId(newId);
            if (!detailDAO.insert(d)) {
                throw new ValidationException("Khong the them chi tiet phieu kiem ke");
            }
        }

        approvalService.logTransition(
                AppConstants.DOC_TYPE_STOCK_ADJUSTMENT,
                newId,
                null,
                DocumentStatus.DRAFT,
                ApprovalAction.CREATE,
                userId,
                null);
        return newId;
    }

    public boolean submit(int adjustmentId, int userId) {
        StockAdjustment adj = getById(adjustmentId);
        DocumentStatus current = adj.getStatusEnum();
        if (!approvalService.canSubmit(current)) {
            throw new ValidationException(ErrorMessages.PO_INVALID_STATUS);
        }
        if (detailDAO.getByAdjustmentId(adjustmentId).isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        if (adj.getCreatedBy() != null && adj.getCreatedBy() != userId) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = adjustmentDAO.updateSubmittedAt(adjustmentId);
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_ADJUSTMENT,
                    adjustmentId,
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

    /**
     * Approve the adjustment and apply each line's variance to Product.StockQuantity.
     */
    public boolean approve(int adjustmentId, int userId, String userRole) {
        StockAdjustment adj = getById(adjustmentId);
        DocumentStatus current = adj.getStatusEnum();
        int creatorId = adj.getCreatedBy() != null ? adj.getCreatedBy() : 0;

        if (!approvalService.canApprove(current, userRole, creatorId, userId)) {
            if (creatorId == userId) {
                throw new ValidationException(ErrorMessages.PO_CREATOR_CANNOT_APPROVE);
            }
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }

        List<StockAdjustmentDetail> lines = detailDAO.getByAdjustmentId(adjustmentId);
        if (lines.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }

        boolean ok = adjustmentDAO.updateApproval(adjustmentId, userId);
        if (!ok) {
            return false;
        }

        // Apply variance to product stock for each line.
        for (StockAdjustmentDetail line : lines) {
            int variance = line.getVariance();
            if (variance == 0) continue;
            if (!stockUpdater.adjustStock(line.getProductId(), variance)) {
                throw new ValidationException(
                        "Khong the cap nhat ton kho cho san pham ID=" + line.getProductId());
            }
        }

        approvalService.logTransition(
                AppConstants.DOC_TYPE_STOCK_ADJUSTMENT,
                adjustmentId,
                current,
                DocumentStatus.APPROVED,
                ApprovalAction.APPROVE,
                userId,
                null);
        return true;
    }

    public boolean reject(int adjustmentId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        StockAdjustment adj = getById(adjustmentId);
        DocumentStatus current = adj.getStatusEnum();
        if (!approvalService.canReject(current, userRole)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = adjustmentDAO.updateRejection(adjustmentId, userId, reason.trim());
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_ADJUSTMENT,
                    adjustmentId,
                    current,
                    DocumentStatus.REJECTED,
                    ApprovalAction.REJECT,
                    userId,
                    reason.trim());
        }
        return ok;
    }

    public boolean cancel(int adjustmentId, int userId, String userRole, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_REASON_REQUIRED);
        }
        StockAdjustment adj = getById(adjustmentId);
        DocumentStatus current = adj.getStatusEnum();
        boolean isCreator = adj.getCreatedBy() != null && adj.getCreatedBy() == userId;
        if (!approvalService.canCancel(current, userRole, isCreator)) {
            throw new ValidationException(ErrorMessages.PO_NO_PERMISSION);
        }
        boolean ok = adjustmentDAO.updateCancellation(adjustmentId, userId, reason.trim());
        if (ok) {
            approvalService.logTransition(
                    AppConstants.DOC_TYPE_STOCK_ADJUSTMENT,
                    adjustmentId,
                    current,
                    DocumentStatus.CANCELLED,
                    ApprovalAction.CANCEL,
                    userId,
                    reason.trim());
        }
        return ok;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void validate(StockAdjustment header, List<StockAdjustmentDetail> details) {
        if (header == null) {
            throw new ValidationException("Phieu kiem ke khong duoc rong");
        }
        if (header.getBranchId() <= 0) {
            throw new ValidationException("Vui long chon chi nhanh");
        }
        if (header.getReason() == null || header.getReason().trim().isEmpty()) {
            throw new ValidationException("Vui long nhap ly do kiem ke");
        }
        if (details == null || details.isEmpty()) {
            throw new ValidationException(ErrorMessages.PO_EMPTY_LINES);
        }
        for (StockAdjustmentDetail d : details) {
            if (d.getProductId() <= 0) {
                throw new ValidationException("Vui long chon san pham hop le");
            }
            if (d.getActualQuantity() < 0) {
                throw new ValidationException("So luong thuc te khong hop le");
            }
            if (d.getSystemQuantity() < 0) {
                throw new ValidationException("So luong he thong khong hop le");
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
