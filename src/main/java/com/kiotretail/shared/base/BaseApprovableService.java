package com.kiotretail.shared.base;

import com.kiotretail.shared.constant.AppConstants;

import java.math.BigDecimal;

/**
 * Abstract base class for services that implement approval workflows.
 * Provides default RBAC-based permission logic (segregation of duties,
 * role gating, owner threshold) that modules extend.
 *
 * <p>Subclasses MUST implement {@link #getDocumentType()} and inherit
 * transaction helpers from {@link BaseService}.</p>
 */
public abstract class BaseApprovableService extends BaseService {

    /**
     * Returns the document type identifier for this module.
     * Used for audit logging and routing.
     */
    protected abstract String getDocumentType();

    /**
     * Only DRAFT documents can be submitted for approval.
     */
    public boolean canSubmit(String currentStatus) {
        return "DRAFT".equals(currentStatus);
    }

    /**
     * Approver must be different from creator (segregation of duties),
     * the document must be PENDING_APPROVAL, and the role must have
     * approval rights (Owner or StoreManager).
     */
    public boolean canApprove(String currentStatus, String userRole,
                              int creatorId, int approverId) {
        if (!"PENDING_APPROVAL".equals(currentStatus)) {
            return false;
        }
        if (creatorId == approverId) {
            return false;
        }
        return isApproverRole(userRole);
    }

    /**
     * Amount-aware approval gate. Applies the base rules and then enforces
     * the owner-only threshold: any document whose total is at or above
     * {@link AppConstants#OWNER_APPROVAL_THRESHOLD} can only be approved
     * by an Owner (Store Manager is blocked at or above the threshold).
     *
     * @param totalAmount document total; if null the threshold rule is skipped
     */
    public boolean canApprove(String currentStatus, String userRole,
                              int creatorId, int approverId,
                              BigDecimal totalAmount) {
        if (!canApprove(currentStatus, userRole, creatorId, approverId)) {
            return false;
        }
        if (totalAmount != null
                && totalAmount.compareTo(AppConstants.OWNER_APPROVAL_THRESHOLD) >= 0) {
            return AppConstants.ROLE_OWNER.equals(userRole);
        }
        return true;
    }

    /**
     * Same gate as approve: PENDING_APPROVAL plus an approver role.
     */
    public boolean canReject(String currentStatus, String userRole) {
        if (!"PENDING_APPROVAL".equals(currentStatus)) {
            return false;
        }
        return isApproverRole(userRole);
    }

    /**
     * Cancel rules:
     * - DRAFT or PENDING_APPROVAL: owner (creator) or any approver role can cancel.
     * - APPROVED, IN_PROGRESS, or RECEIVING: only Owner can cancel post-approval.
     * - Any other state: cannot cancel.
     */
    public boolean canCancel(String currentStatus, String userRole, boolean isOwner) {
        if (currentStatus == null) {
            return false;
        }
        switch (currentStatus) {
            case "DRAFT":
            case "PENDING_APPROVAL":
                return isOwner || isApproverRole(userRole);
            case "APPROVED":
            case "IN_PROGRESS":
            case "RECEIVING":
                return AppConstants.ROLE_OWNER.equals(userRole);
            default:
                return false;
        }
    }

    /**
     * Checks if the given role has approval authority.
     */
    protected boolean isApproverRole(String userRole) {
        return AppConstants.ROLE_OWNER.equals(userRole)
                || AppConstants.ROLE_STORE_MANAGER.equals(userRole);
    }

    /**
     * Checks if the amount requires Owner-level approval.
     */
    protected boolean requiresOwnerApproval(BigDecimal amount) {
        return amount != null
                && amount.compareTo(AppConstants.OWNER_APPROVAL_THRESHOLD) >= 0;
    }
}
