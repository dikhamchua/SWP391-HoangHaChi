package com.kiotretail.purchase.model;

import com.kiotretail.purchase.constant.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * PurchaseOrder POJO mapping the PurchaseOrder table.
 *
 * <p>DB columns: PurchaseOrderID, SupplierID, BranchID, EmployeeID, OrderCode,
 * Status, TotalAmount, Note, CreatedAt, CreatedBy, SubmittedAt, ApprovedBy,
 * ApprovedAt, RejectedBy, RejectedAt, RejectedReason, CancelledBy, CancelledAt,
 * CancelledReason, CompletedAt, UpdatedAt.</p>
 *
 * <p>Status uses {@link PurchaseOrderStatus} values stored as uppercase strings
 * (DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, RECEIVING,
 * COMPLETED, CANCELLED).</p>
 *
 * <p>Joined fields: supplierName, branchName, employeeName, createdByName,
 * approvedByName.</p>
 *
 * <p>Transient: {@link #details} is populated only when callers need full
 * order context (view/edit/receive screens); not persisted directly.</p>
 */
public class PurchaseOrder {

    private int purchaseOrderId;
    private int supplierId;
    private int branchId;
    private int employeeId;
    private String orderCode;
    private String status;
    private BigDecimal totalAmount;
    private String note;
    private Timestamp createdAt;

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
    private Timestamp completedAt;
    private Timestamp updatedAt;

    // Join fields
    private String supplierName;
    private String branchName;
    private String employeeName;
    private String createdByName;
    private String approvedByName;
    private String rejectedByName;
    private String cancelledByName;

    // Transient: detail lines loaded on demand
    private transient List<PurchaseOrderDetail> details = new ArrayList<>();

    public PurchaseOrder() {
    }

    public int getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(int purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

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

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public String getRejectedByName() { return rejectedByName; }
    public void setRejectedByName(String rejectedByName) { this.rejectedByName = rejectedByName; }

    public String getCancelledByName() { return cancelledByName; }
    public void setCancelledByName(String cancelledByName) { this.cancelledByName = cancelledByName; }

    public List<PurchaseOrderDetail> getDetails() { return details; }
    public void setDetails(List<PurchaseOrderDetail> details) {
        this.details = details != null ? details : new ArrayList<>();
    }

    /**
     * Parses the raw status string into a {@link PurchaseOrderStatus} enum.
     * Returns {@code null} when the value is missing or unknown.
     */
    public PurchaseOrderStatus getStatusEnum() {
        return PurchaseOrderStatus.fromString(status);
    }

    /**
     * Normalized uppercase approval status name (DRAFT, PENDING_APPROVAL, ...)
     * for the workflow UI. Returns empty string when status is missing/unknown
     * so JSP EL comparisons stay null-safe.
     */
    public String getApprovalStatus() {
        PurchaseOrderStatus s = getStatusEnum();
        return s != null ? s.name() : "";
    }
}
