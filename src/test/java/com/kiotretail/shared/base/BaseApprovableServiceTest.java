package com.kiotretail.shared.base;

import com.kiotretail.shared.constant.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Unit tests for {@link BaseApprovableService}.
 * Uses a concrete subclass stub to test the abstract class behavior.
 */
public class BaseApprovableServiceTest {

    private BaseApprovableService service;

    @BeforeEach
    void setUp() {
        service = new BaseApprovableService() {
            @Override
            protected String getDocumentType() {
                return "TEST_DOCUMENT";
            }
        };
    }

    // ============================================================
    //  getDocumentType
    // ============================================================

    @Test
    @DisplayName("getDocumentType returns subclass value")
    void getDocumentType_returnsSubclassValue() {
        Assertions.assertEquals("TEST_DOCUMENT", service.getDocumentType());
    }

    // ============================================================
    //  canSubmit
    // ============================================================

    @Test
    @DisplayName("canSubmit: DRAFT -> true")
    void canSubmit_draft_true() {
        Assertions.assertTrue(service.canSubmit("DRAFT"));
    }

    @Test
    @DisplayName("canSubmit: PENDING_APPROVAL -> false")
    void canSubmit_pending_false() {
        Assertions.assertFalse(service.canSubmit("PENDING_APPROVAL"));
    }

    @Test
    @DisplayName("canSubmit: APPROVED -> false")
    void canSubmit_approved_false() {
        Assertions.assertFalse(service.canSubmit("APPROVED"));
    }

    @Test
    @DisplayName("canSubmit: null -> false")
    void canSubmit_null_false() {
        Assertions.assertFalse(service.canSubmit(null));
    }

    @Test
    @DisplayName("canSubmit: empty string -> false")
    void canSubmit_empty_false() {
        Assertions.assertFalse(service.canSubmit(""));
    }

    // ============================================================
    //  canApprove (basic)
    // ============================================================

    @Test
    @DisplayName("canApprove: PENDING + StoreManager + different user -> true")
    void canApprove_storeManager_true() {
        Assertions.assertTrue(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_STORE_MANAGER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: PENDING + Owner + different user -> true")
    void canApprove_owner_true() {
        Assertions.assertTrue(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_OWNER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: self-approve (creator == approver) -> false")
    void canApprove_selfApprove_false() {
        Assertions.assertFalse(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_OWNER, 5, 5));
    }

    @Test
    @DisplayName("canApprove: non-approver role -> false")
    void canApprove_nonApproverRole_false() {
        Assertions.assertFalse(service.canApprove(
                "PENDING_APPROVAL", "SalesStaff", 1, 2));
    }

    @Test
    @DisplayName("canApprove: DRAFT status -> false")
    void canApprove_draft_false() {
        Assertions.assertFalse(service.canApprove(
                "DRAFT", AppConstants.ROLE_OWNER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: APPROVED status -> false")
    void canApprove_alreadyApproved_false() {
        Assertions.assertFalse(service.canApprove(
                "APPROVED", AppConstants.ROLE_OWNER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: null status -> false")
    void canApprove_nullStatus_false() {
        Assertions.assertFalse(service.canApprove(
                null, AppConstants.ROLE_OWNER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: null role -> false")
    void canApprove_nullRole_false() {
        Assertions.assertFalse(service.canApprove(
                "PENDING_APPROVAL", null, 1, 2));
    }

    // ============================================================
    //  canApprove (with amount / owner threshold)
    // ============================================================

    @Test
    @DisplayName("canApprove with amount: below threshold + StoreManager -> true")
    void canApproveAmount_belowThreshold_storeManager_true() {
        BigDecimal belowThreshold = AppConstants.OWNER_APPROVAL_THRESHOLD.subtract(BigDecimal.ONE);
        Assertions.assertTrue(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_STORE_MANAGER, 1, 2, belowThreshold));
    }

    @Test
    @DisplayName("canApprove with amount: at threshold + StoreManager -> false (owner only)")
    void canApproveAmount_atThreshold_storeManager_false() {
        Assertions.assertFalse(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_STORE_MANAGER, 1, 2,
                AppConstants.OWNER_APPROVAL_THRESHOLD));
    }

    @Test
    @DisplayName("canApprove with amount: at threshold + Owner -> true")
    void canApproveAmount_atThreshold_owner_true() {
        Assertions.assertTrue(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_OWNER, 1, 2,
                AppConstants.OWNER_APPROVAL_THRESHOLD));
    }

    @Test
    @DisplayName("canApprove with amount: above threshold + Owner -> true")
    void canApproveAmount_aboveThreshold_owner_true() {
        BigDecimal aboveThreshold = AppConstants.OWNER_APPROVAL_THRESHOLD.add(BigDecimal.ONE);
        Assertions.assertTrue(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_OWNER, 1, 2, aboveThreshold));
    }

    @Test
    @DisplayName("canApprove with amount: null amount -> threshold skipped, StoreManager ok")
    void canApproveAmount_nullAmount_thresholdSkipped() {
        Assertions.assertTrue(service.canApprove(
                "PENDING_APPROVAL", AppConstants.ROLE_STORE_MANAGER, 1, 2, null));
    }

    // ============================================================
    //  canReject
    // ============================================================

    @Test
    @DisplayName("canReject: PENDING + StoreManager -> true")
    void canReject_storeManager_true() {
        Assertions.assertTrue(service.canReject("PENDING_APPROVAL", AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("canReject: PENDING + Owner -> true")
    void canReject_owner_true() {
        Assertions.assertTrue(service.canReject("PENDING_APPROVAL", AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("canReject: DRAFT -> false")
    void canReject_draft_false() {
        Assertions.assertFalse(service.canReject("DRAFT", AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("canReject: non-approver role -> false")
    void canReject_nonApprover_false() {
        Assertions.assertFalse(service.canReject("PENDING_APPROVAL", "SalesStaff"));
    }

    @Test
    @DisplayName("canReject: null status -> false")
    void canReject_nullStatus_false() {
        Assertions.assertFalse(service.canReject(null, AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("canReject: null role -> false")
    void canReject_nullRole_false() {
        Assertions.assertFalse(service.canReject("PENDING_APPROVAL", null));
    }

    // ============================================================
    //  canCancel
    // ============================================================

    @Test
    @DisplayName("canCancel: DRAFT + isOwner=true -> true")
    void canCancel_draft_isOwner_true() {
        Assertions.assertTrue(service.canCancel("DRAFT", "SalesStaff", true));
    }

    @Test
    @DisplayName("canCancel: DRAFT + approver role -> true")
    void canCancel_draft_approverRole_true() {
        Assertions.assertTrue(service.canCancel("DRAFT", AppConstants.ROLE_STORE_MANAGER, false));
    }

    @Test
    @DisplayName("canCancel: DRAFT + non-owner non-approver -> false")
    void canCancel_draft_notOwnerNotApprover_false() {
        Assertions.assertFalse(service.canCancel("DRAFT", "SalesStaff", false));
    }

    @Test
    @DisplayName("canCancel: PENDING_APPROVAL + isOwner=true -> true")
    void canCancel_pending_isOwner_true() {
        Assertions.assertTrue(service.canCancel("PENDING_APPROVAL", "SalesStaff", true));
    }

    @Test
    @DisplayName("canCancel: APPROVED + Owner role -> true")
    void canCancel_approved_owner_true() {
        Assertions.assertTrue(service.canCancel("APPROVED", AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: APPROVED + StoreManager -> false")
    void canCancel_approved_storeManager_false() {
        Assertions.assertFalse(service.canCancel("APPROVED", AppConstants.ROLE_STORE_MANAGER, true));
    }

    @Test
    @DisplayName("canCancel: IN_PROGRESS + Owner -> true")
    void canCancel_inProgress_owner_true() {
        Assertions.assertTrue(service.canCancel("IN_PROGRESS", AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: RECEIVING + Owner -> true")
    void canCancel_receiving_owner_true() {
        Assertions.assertTrue(service.canCancel("RECEIVING", AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: COMPLETED -> false (terminal)")
    void canCancel_completed_false() {
        Assertions.assertFalse(service.canCancel("COMPLETED", AppConstants.ROLE_OWNER, true));
    }

    @Test
    @DisplayName("canCancel: CANCELLED -> false")
    void canCancel_cancelled_false() {
        Assertions.assertFalse(service.canCancel("CANCELLED", AppConstants.ROLE_OWNER, true));
    }

    @Test
    @DisplayName("canCancel: null status -> false")
    void canCancel_nullStatus_false() {
        Assertions.assertFalse(service.canCancel(null, AppConstants.ROLE_OWNER, true));
    }

    // ============================================================
    //  isApproverRole
    // ============================================================

    @Test
    @DisplayName("isApproverRole: Owner -> true")
    void isApproverRole_owner_true() {
        Assertions.assertTrue(service.isApproverRole(AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("isApproverRole: StoreManager -> true")
    void isApproverRole_storeManager_true() {
        Assertions.assertTrue(service.isApproverRole(AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("isApproverRole: SalesStaff -> false")
    void isApproverRole_salesStaff_false() {
        Assertions.assertFalse(service.isApproverRole("SalesStaff"));
    }

    @Test
    @DisplayName("isApproverRole: null -> false")
    void isApproverRole_null_false() {
        Assertions.assertFalse(service.isApproverRole(null));
    }

    // ============================================================
    //  requiresOwnerApproval
    // ============================================================

    @Test
    @DisplayName("requiresOwnerApproval: at threshold -> true")
    void requiresOwner_atThreshold_true() {
        Assertions.assertTrue(service.requiresOwnerApproval(AppConstants.OWNER_APPROVAL_THRESHOLD));
    }

    @Test
    @DisplayName("requiresOwnerApproval: below threshold -> false")
    void requiresOwner_belowThreshold_false() {
        Assertions.assertFalse(service.requiresOwnerApproval(
                AppConstants.OWNER_APPROVAL_THRESHOLD.subtract(BigDecimal.ONE)));
    }

    @Test
    @DisplayName("requiresOwnerApproval: null -> false")
    void requiresOwner_null_false() {
        Assertions.assertFalse(service.requiresOwnerApproval(null));
    }
}
