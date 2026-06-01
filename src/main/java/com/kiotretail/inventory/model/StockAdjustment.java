package com.kiotretail.inventory.model;

import com.kiotretail.shared.constant.DocumentStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * StockAdjustment POJO mapping the StockAdjustment table (UC-5.1 / UC-5.2).
 *
 * <p>DB columns: AdjustmentID, AdjustmentCode, BranchID, Status, Reason, Note,
 * TotalVarianceValue, CreatedBy, SubmittedAt, ApprovedBy, ApprovedAt,
 * RejectedBy, RejectedAt, RejectedReason, CancelledBy, CancelledAt,
 * CancelledReason, CreatedAt, UpdatedAt.</p>
 *
 * <p>Status values from {@link DocumentStatus}: DRAFT, PENDING_APPROVAL,
 * APPROVED, REJECTED, CANCELLED.</p>
 *
 * <p>Joined fields: branchName, createdByName, approvedByName,
 * rejectedByName, cancelledByName.</p>
 *
 * <p>Transient: {@link #details} loaded only when callers need full
 * adjustment context (view/edit/approve screens).</p>
 */
public class StockAdjustment {

    private int adjustmentId;
    private String adjustmentCode;
    private int branchId;
    private String status;
    private String reason;
    private String note;
    private BigDecimal totalVarianceValue;

    // Approval / audit
    private Integer createdBy;
    private Timestamp submittedAt;
    private Integer approvedBy;
    private Timestamp approvedAt;
    private Integer rejectedBy;
    private Timestamp rejectedAt;
    private String rejectedReason;
    private Integer cancelledBy;
    private Timestamp cancelledAt;
    private String cancelledReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Join fields
    private String branchName;
    private String createdByName;
    private String approvedByName;
    private String rejectedByName;
    private String cancelledByName;

    // Transient: detail lines loaded on demand
    private transient List<StockAdjustmentDetail> details = new ArrayList<>();

    public StockAdjustment() {
    }

    public int getAdjustmentId() { return adjustmentId; }
    public void setAdjustmentId(int adjustmentId) { this.adjustmentId = adjustmentId; }

    public String getAdjustmentCode() { return adjustmentCode; }
    public void setAdjustmentCode(String adjustmentCode) { this.adjustmentCode = adjustmentCode; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public BigDecimal getTotalVarianceValue() { return totalVarianceValue; }
    public void setTotalVarianceValue(BigDecimal totalVarianceValue) { this.totalVarianceValue = totalVarianceValue; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }

    public Integer getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(Integer rejectedBy) { this.rejectedBy = rejectedBy; }

    public Timestamp getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(Timestamp rejectedAt) { this.rejectedAt = rejectedAt; }

    public String getRejectedReason() { return rejectedReason; }
    public void setRejectedReason(String rejectedReason) { this.rejectedReason = rejectedReason; }

    public Integer getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(Integer cancelledBy) { this.cancelledBy = cancelledBy; }

    public Timestamp getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Timestamp cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelledReason() { return cancelledReason; }
    public void setCancelledReason(String cancelledReason) { this.cancelledReason = cancelledReason; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public String getRejectedByName() { return rejectedByName; }
    public void setRejectedByName(String rejectedByName) { this.rejectedByName = rejectedByName; }

    public String getCancelledByName() { return cancelledByName; }
    public void setCancelledByName(String cancelledByName) { this.cancelledByName = cancelledByName; }

    public List<StockAdjustmentDetail> getDetails() { return details; }
    public void setDetails(List<StockAdjustmentDetail> details) {
        this.details = details != null ? details : new ArrayList<>();
    }

    /**
     * Parses the raw status string into a {@link DocumentStatus} enum.
     * Returns {@code null} when the value is missing or unknown.
     */
    public DocumentStatus getStatusEnum() {
        return DocumentStatus.fromString(status);
    }
}
