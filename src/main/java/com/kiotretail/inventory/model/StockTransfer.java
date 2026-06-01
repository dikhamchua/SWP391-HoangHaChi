package com.kiotretail.inventory.model;

import com.kiotretail.shared.constant.DocumentStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * StockTransfer POJO mapping the StockTransfer table (UC-5.3).
 *
 * <p>DB columns: TransferID, TransferCode, FromBranchID, ToBranchID, Status,
 * Note, TotalItems, TotalQuantity, CreatedBy, SubmittedAt, ApprovedBy,
 * ApprovedAt, RejectedBy, RejectedAt, RejectedReason, ShippedBy, ShippedAt,
 * ReceivedBy, ReceivedAt, CancelledBy, CancelledAt, CancelledReason,
 * CreatedAt, UpdatedAt.</p>
 */
public class StockTransfer {

    private int transferId;
    private String transferCode;
    private int fromBranchId;
    private int toBranchId;
    private String status;
    private String note;
    private int totalItems;
    private int totalQuantity;

    private Integer createdBy;
    private Timestamp submittedAt;
    private Integer approvedBy;
    private Timestamp approvedAt;
    private Integer rejectedBy;
    private Timestamp rejectedAt;
    private String rejectedReason;
    private Integer shippedBy;
    private Timestamp shippedAt;
    private Integer receivedBy;
    private Timestamp receivedAt;
    private Integer cancelledBy;
    private Timestamp cancelledAt;
    private String cancelledReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private String fromBranchName;
    private String toBranchName;
    private String createdByName;
    private String approvedByName;
    private String rejectedByName;
    private String shippedByName;
    private String receivedByName;
    private String cancelledByName;

    private transient List<StockTransferDetail> details = new ArrayList<>();

    public StockTransfer() {}

    public int getTransferId() { return transferId; }
    public void setTransferId(int transferId) { this.transferId = transferId; }

    public String getTransferCode() { return transferCode; }
    public void setTransferCode(String transferCode) { this.transferCode = transferCode; }

    public int getFromBranchId() { return fromBranchId; }
    public void setFromBranchId(int fromBranchId) { this.fromBranchId = fromBranchId; }

    public int getToBranchId() { return toBranchId; }
    public void setToBranchId(int toBranchId) { this.toBranchId = toBranchId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

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

    public Integer getShippedBy() { return shippedBy; }
    public void setShippedBy(Integer shippedBy) { this.shippedBy = shippedBy; }

    public Timestamp getShippedAt() { return shippedAt; }
    public void setShippedAt(Timestamp shippedAt) { this.shippedAt = shippedAt; }

    public Integer getReceivedBy() { return receivedBy; }
    public void setReceivedBy(Integer receivedBy) { this.receivedBy = receivedBy; }

    public Timestamp getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Timestamp receivedAt) { this.receivedAt = receivedAt; }

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

    public String getFromBranchName() { return fromBranchName; }
    public void setFromBranchName(String fromBranchName) { this.fromBranchName = fromBranchName; }

    public String getToBranchName() { return toBranchName; }
    public void setToBranchName(String toBranchName) { this.toBranchName = toBranchName; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public String getRejectedByName() { return rejectedByName; }
    public void setRejectedByName(String rejectedByName) { this.rejectedByName = rejectedByName; }

    public String getShippedByName() { return shippedByName; }
    public void setShippedByName(String shippedByName) { this.shippedByName = shippedByName; }

    public String getReceivedByName() { return receivedByName; }
    public void setReceivedByName(String receivedByName) { this.receivedByName = receivedByName; }

    public String getCancelledByName() { return cancelledByName; }
    public void setCancelledByName(String cancelledByName) { this.cancelledByName = cancelledByName; }

    public List<StockTransferDetail> getDetails() { return details; }
    public void setDetails(List<StockTransferDetail> details) {
        this.details = details != null ? details : new ArrayList<>();
    }

    public DocumentStatus getStatusEnum() {
        return DocumentStatus.fromString(status);
    }
}
