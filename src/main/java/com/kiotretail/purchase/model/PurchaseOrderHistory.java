package com.kiotretail.purchase.model;

import java.sql.Timestamp;

public class PurchaseOrderHistory {

    private int historyId;
    private int purchaseOrderId;
    private String fromStatus;
    private String toStatus;
    private String action;
    private int performedBy;
    private String reason;
    private Timestamp createdAt;

    // Join field
    private String performedByName;

    public PurchaseOrderHistory() {
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(int purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getPerformedBy() { return performedBy; }
    public void setPerformedBy(int performedBy) { this.performedBy = performedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
}