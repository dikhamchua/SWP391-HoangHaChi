package com.kiotretail.shared.service;

import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ApprovalAction;
import com.kiotretail.shared.constant.DocumentStatus;
import com.kiotretail.shared.dao.ApprovalHistoryDAO;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.model.ApprovalHistory;

/**
 * Centralised approval-workflow rules and audit logging.
 *
 * <p>Stateless helper used by every module that owns an approvable
 * document (purchase order, stock transfer, invoice, ...). It enforces
 * permission rules and writes a row to {@code ApprovalHistory} for
 * every transition.</p>
 *
 * <p>Role names follow {@link AppConstants#ROLE_OWNER},
 * {@link AppConstants#ROLE_STORE_MANAGER}, etc.</p>
 */
public class ApprovalService {

    private final ApprovalHistoryDAO historyDAO = new ApprovalHistoryDAO();

    /**
     * Only DRAFT documents can be submitted for approval.
     */
    public boolean canSubmit(DocumentStatus currentStatus) {
        return currentStatus == DocumentStatus.DRAFT;
    }

    /**
     * Approver must be different from creator (segregation of duties),
     * the document must be PENDING_APPROVAL, and the role must have
     * approval rights (Owner or StoreManager).
     */
    public boolean canApprove(DocumentStatus currentStatus, String userRole, int creatorId, int approverId) {
        if (currentStatus != DocumentStatus.PENDING_APPROVAL) {
            return false;
        }
        if (creatorId == approverId) {
            return false;
        }
        return isApproverRole(userRole);
    }

    /**
     * Amount-aware approval gate. Applies the base rules from
     * {@link #canApprove(DocumentStatus, String, int, int)} and then enforces
     * the owner-only threshold: any document whose total is at or above
     * {@link AppConstants#OWNER_APPROVAL_THRESHOLD} can only be approved by an
     * Owner (Store Manager is blocked at or above the threshold).
     *
     * @param totalAmount document total; if null the threshold rule is skipped
     */
    public boolean canApprove(DocumentStatus currentStatus,
                              String userRole,
                              int creatorId,
                              int approverId,
                              java.math.BigDecimal totalAmount) {
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
    public boolean canReject(DocumentStatus currentStatus, String userRole) {
        if (currentStatus != DocumentStatus.PENDING_APPROVAL) {
            return false;
        }
        return isApproverRole(userRole);
    }

    /**
     * Cancel rules:
     * - DRAFT or PENDING_APPROVAL: owner (creator) or any approver role can cancel.
     * - APPROVED / IN_PROGRESS / RECEIVING: only Owner can cancel post-approval.
     * - Any other state: cannot cancel.
     */
    public boolean canCancel(DocumentStatus currentStatus, String userRole, boolean isOwner) {
        if (currentStatus == null) {
            return false;
        }
        switch (currentStatus) {
            case DRAFT:
            case PENDING_APPROVAL:
                return isOwner || isApproverRole(userRole);
            case APPROVED:
            case IN_PROGRESS:
            case RECEIVING:
                return AppConstants.ROLE_OWNER.equals(userRole);
            default:
                return false;
        }
    }

    /**
     * Persists a status transition to the audit table.
     *
     * @param docType     polymorphic document type identifier (e.g. "PURCHASE_ORDER")
     * @param docId       primary key of the document
     * @param from        current status (may be null for CREATE)
     * @param to          target status
     * @param action      action that triggered the transition
     * @param performedBy employee id performing the action
     * @param reason      free-form note (required for REJECT/CANCEL)
     */
    public void logTransition(String docType,
                              int docId,
                              DocumentStatus from,
                              DocumentStatus to,
                              ApprovalAction action,
                              int performedBy,
                              String reason) {
        if (docType == null || docType.trim().isEmpty()) {
            throw new ValidationException("Document type is required");
        }
        if (to == null) {
            throw new ValidationException("Target status is required");
        }
        if (action == null) {
            throw new ValidationException("Approval action is required");
        }
        if (performedBy <= 0) {
            throw new ValidationException("Performed-by employee is required");
        }
        if ((action == ApprovalAction.REJECT || action == ApprovalAction.CANCEL)
                && (reason == null || reason.trim().isEmpty())) {
            throw new ValidationException("Reason is required for " + action.name());
        }

        ApprovalHistory entry = new ApprovalHistory();
        entry.setDocumentType(docType.trim());
        entry.setDocumentId(docId);
        entry.setFromStatus(from == null ? null : from.name());
        entry.setToStatus(to.name());
        entry.setAction(action.name());
        entry.setPerformedBy(performedBy);
        entry.setReason(reason);

        int newId = historyDAO.insert(entry);
        if (newId <= 0) {
            throw new ServiceException("Failed to log approval transition", 500);
        }
    }

    private boolean isApproverRole(String userRole) {
        return AppConstants.ROLE_OWNER.equals(userRole)
                || AppConstants.ROLE_STORE_MANAGER.equals(userRole);
    }
}
