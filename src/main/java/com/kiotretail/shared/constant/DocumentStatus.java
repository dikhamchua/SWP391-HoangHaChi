package com.kiotretail.shared.constant;

/**
 * Lifecycle status for any approvable business document
 * (purchase orders, stock transfers, invoices, etc.).
 */
public enum DocumentStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    IN_PROGRESS,
    RECEIVING,
    COMPLETED,
    FINALIZED,
    CANCELLED;

    /**
     * Safe parser. Returns {@code null} when value is null/blank/unknown.
     */
    public static DocumentStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return DocumentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
