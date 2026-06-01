package com.kiotretail.purchase.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Lightweight projection of a document awaiting approval, used by the
 * pending-approvals queue screen (UC-4.3).
 *
 * <p>Plain POJO. {@code documentType} carries an {@code AppConstants.DOC_TYPE_*}
 * value so the same DTO can represent purchase orders today and other approvable
 * document types later. {@code submittedAt} mirrors the {@code java.sql.Timestamp}
 * type used by {@code PurchaseOrder} for consistent rendering.</p>
 */
public class PendingApprovalItem {

    private String documentType;
    private int documentId;
    private String documentCode;
    private String submitterName;
    private Timestamp submittedAt;
    private BigDecimal totalAmount;
    private String description;

    public PendingApprovalItem() {
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

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
