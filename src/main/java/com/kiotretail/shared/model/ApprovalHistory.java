package com.kiotretail.shared.model;

import java.sql.Timestamp;

/**
 * Audit record describing a single status transition for an approvable
 * document. One row per action (CREATE, SUBMIT, APPROVE, REJECT, ...).
 *
 * <p>The pair ({@code documentType}, {@code documentId}) identifies the
 * target document polymorphically, allowing the same table to serve
 * purchase orders, stock transfers, invoices, etc.</p>
 */
public class ApprovalHistory {

    private int historyId;
    private String documentType;
    private int documentId;
    private String fromStatus;
    private String toStatus;
    private String action;
    private int performedBy;
    private String reason;
    private Timestamp createdAt;

    // Joined fields (optional, populated when DAO joins with Employee)
    private String performedByName;

    public ApprovalHistory() {
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(int performedBy) {
        this.performedBy = performedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public void setPerformedByName(String performedByName) {
        this.performedByName = performedByName;
    }
}
