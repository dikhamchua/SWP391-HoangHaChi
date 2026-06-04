package com.kiotretail.purchase.service;

import com.kiotretail.purchase.constant.PurchaseOrderStatus;
import com.kiotretail.shared.constant.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the approval gate methods in PurchaseService.
 * Now tests the public inherited methods from BaseApprovableService
 * (using String status values instead of PurchaseOrderStatus enum).
 */
public class PurchaseApprovalTest {

    private PurchaseService service;

    @BeforeEach
    void setUp() {
        service = new PurchaseService();
    }

    // --- getDocumentType ---

    @Test
    @DisplayName("getDocumentType returns PURCHASE_ORDER")
    void getDocumentType_returnsPurchaseOrder() {
        assertEquals(AppConstants.DOC_TYPE_PURCHASE_ORDER, service.getDocumentType());
    }

    // --- canSubmit ---

    @Test
    @DisplayName("canSubmit: DRAFT returns true")
    void canSubmit_draft_returnsTrue() {
        assertTrue(service.canSubmit(PurchaseOrderStatus.DRAFT.name()));
    }

    @Test
    @DisplayName("canSubmit: non-DRAFT returns false")
    void canSubmit_nonDraft_returnsFalse() {
        assertFalse(service.canSubmit(PurchaseOrderStatus.PENDING_APPROVAL.name()));
        assertFalse(service.canSubmit(PurchaseOrderStatus.APPROVED.name()));
        assertFalse(service.canSubmit(PurchaseOrderStatus.COMPLETED.name()));
    }

    // --- canApprove ---

    @Test
    @DisplayName("canApprove: PENDING_APPROVAL + Owner + different user returns true")
    void canApprove_validOwner_returnsTrue() {
        assertTrue(service.canApprove(
                PurchaseOrderStatus.PENDING_APPROVAL.name(), AppConstants.ROLE_OWNER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: PENDING_APPROVAL + StoreManager + different user returns true")
    void canApprove_validManager_returnsTrue() {
        assertTrue(service.canApprove(
                PurchaseOrderStatus.PENDING_APPROVAL.name(), AppConstants.ROLE_STORE_MANAGER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: same creator and approver returns false")
    void canApprove_sameUser_returnsFalse() {
        assertFalse(service.canApprove(
                PurchaseOrderStatus.PENDING_APPROVAL.name(), AppConstants.ROLE_OWNER, 5, 5));
    }

    @Test
    @DisplayName("canApprove: non-PENDING status returns false")
    void canApprove_wrongStatus_returnsFalse() {
        assertFalse(service.canApprove(
                PurchaseOrderStatus.DRAFT.name(), AppConstants.ROLE_OWNER, 1, 2));
        assertFalse(service.canApprove(
                PurchaseOrderStatus.APPROVED.name(), AppConstants.ROLE_OWNER, 1, 2));
    }

    // --- canReject ---

    @Test
    @DisplayName("canReject: PENDING_APPROVAL + approver role returns true")
    void canReject_valid_returnsTrue() {
        assertTrue(service.canReject(
                PurchaseOrderStatus.PENDING_APPROVAL.name(), AppConstants.ROLE_OWNER));
        assertTrue(service.canReject(
                PurchaseOrderStatus.PENDING_APPROVAL.name(), AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("canReject: non-PENDING status returns false")
    void canReject_wrongStatus_returnsFalse() {
        assertFalse(service.canReject(
                PurchaseOrderStatus.APPROVED.name(), AppConstants.ROLE_OWNER));
    }

    // --- canCancel ---

    @Test
    @DisplayName("canCancel: DRAFT + creator returns true")
    void canCancel_draftCreator_returnsTrue() {
        assertTrue(service.canCancel(PurchaseOrderStatus.DRAFT.name(), "Staff", true));
    }

    @Test
    @DisplayName("canCancel: DRAFT + approver role returns true")
    void canCancel_draftApprover_returnsTrue() {
        assertTrue(service.canCancel(
                PurchaseOrderStatus.DRAFT.name(), AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: APPROVED + Owner role returns true")
    void canCancel_approvedOwner_returnsTrue() {
        assertTrue(service.canCancel(
                PurchaseOrderStatus.APPROVED.name(), AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: APPROVED + non-Owner returns false")
    void canCancel_approvedNonOwner_returnsFalse() {
        assertFalse(service.canCancel(
                PurchaseOrderStatus.APPROVED.name(), AppConstants.ROLE_STORE_MANAGER, false));
    }

    @Test
    @DisplayName("canCancel: COMPLETED returns false")
    void canCancel_completed_returnsFalse() {
        assertFalse(service.canCancel(
                PurchaseOrderStatus.COMPLETED.name(), AppConstants.ROLE_OWNER, true));
    }

    @Test
    @DisplayName("canCancel: null status returns false")
    void canCancel_nullStatus_returnsFalse() {
        assertFalse(service.canCancel(null, AppConstants.ROLE_OWNER, true));
    }

}
