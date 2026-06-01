package com.kiotretail.shared.constant;

/**
 * Actions that can be performed against an approvable document.
 * Each action represents a state-transition trigger captured in
 * {@link com.kiotretail.shared.model.ApprovalHistory}.
 */
public enum ApprovalAction {
    CREATE,
    SUBMIT,
    APPROVE,
    REJECT,
    CANCEL,
    COMPLETE,
    RECEIVE,
    SHIP,
    FINALIZE
}
