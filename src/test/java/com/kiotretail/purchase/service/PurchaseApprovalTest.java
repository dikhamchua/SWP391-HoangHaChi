package com.kiotretail.purchase.service;

import com.kiotretail.purchase.constant.PurchaseOrderStatus;
import com.kiotretail.shared.constant.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the inlined approval gate methods in PurchaseService.
 * Uses reflection to test private canSubmit/canApprove/canReject/canCancel/isApproverRole.
 */
public class PurchaseApprovalTest {

    private PurchaseService service;
    private Method canSubmit;
    private Method canApprove;
    private Method canReject;
    private Method canCancel;
    private Method isApproverRole;

    @BeforeEach
    void setUp() throws Exception {
        service = new PurchaseService();

        canSubmit = PurchaseService.class.getDeclaredMethod("canSubmit", PurchaseOrderStatus.class);
        canSubmit.setAccessible(true);

        canApprove = PurchaseService.class.getDeclaredMethod("canApprove",
                PurchaseOrderStatus.class, String.class, int.class, int.class);
        canApprove.setAccessible(true);

        canReject = PurchaseService.class.getDeclaredMethod("canReject",
                PurchaseOrderStatus.class, String.class);
        canReject.setAccessible(true);

        canCancel = PurchaseService.class.getDeclaredMethod("canCancel",
                PurchaseOrderStatus.class, String.class, boolean.class);
        canCancel.setAccessible(true);

        isApproverRole = PurchaseService.class.getDeclaredMethod("isApproverRole", String.class);
        isApproverRole.setAccessible(true);
    }

    // --- canSubmit ---

    @Test
    @DisplayName("canSubmit: DRAFT returns true")
    void canSubmit_draft_returnsTrue() throws Exception {
        assertTrue((boolean) canSubmit.invoke(service, PurchaseOrderStatus.DRAFT));
    }

    @Test
    @DisplayName("canSubmit: non-DRAFT returns false")
    void canSubmit_nonDraft_returnsFalse() throws Exception {
        assertFalse((boolean) canSubmit.invoke(service, PurchaseOrderStatus.PENDING_APPROVAL));
        assertFalse((boolean) canSubmit.invoke(service, PurchaseOrderStatus.APPROVED));
        assertFalse((boolean) canSubmit.invoke(service, PurchaseOrderStatus.COMPLETED));
    }

    // --- canApprove ---

    @Test
    @DisplayName("canApprove: PENDING_APPROVAL + Owner + different user returns true")
    void canApprove_validOwner_returnsTrue() throws Exception {
        assertTrue((boolean) canApprove.invoke(service,
                PurchaseOrderStatus.PENDING_APPROVAL, AppConstants.ROLE_OWNER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: PENDING_APPROVAL + StoreManager + different user returns true")
    void canApprove_validManager_returnsTrue() throws Exception {
        assertTrue((boolean) canApprove.invoke(service,
                PurchaseOrderStatus.PENDING_APPROVAL, AppConstants.ROLE_STORE_MANAGER, 1, 2));
    }

    @Test
    @DisplayName("canApprove: same creator and approver returns false")
    void canApprove_sameUser_returnsFalse() throws Exception {
        assertFalse((boolean) canApprove.invoke(service,
                PurchaseOrderStatus.PENDING_APPROVAL, AppConstants.ROLE_OWNER, 5, 5));
    }

    @Test
    @DisplayName("canApprove: non-PENDING status returns false")
    void canApprove_wrongStatus_returnsFalse() throws Exception {
        assertFalse((boolean) canApprove.invoke(service,
                PurchaseOrderStatus.DRAFT, AppConstants.ROLE_OWNER, 1, 2));
        assertFalse((boolean) canApprove.invoke(service,
                PurchaseOrderStatus.APPROVED, AppConstants.ROLE_OWNER, 1, 2));
    }

    // --- canReject ---

    @Test
    @DisplayName("canReject: PENDING_APPROVAL + approver role returns true")
    void canReject_valid_returnsTrue() throws Exception {
        assertTrue((boolean) canReject.invoke(service,
                PurchaseOrderStatus.PENDING_APPROVAL, AppConstants.ROLE_OWNER));
        assertTrue((boolean) canReject.invoke(service,
                PurchaseOrderStatus.PENDING_APPROVAL, AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("canReject: non-PENDING status returns false")
    void canReject_wrongStatus_returnsFalse() throws Exception {
        assertFalse((boolean) canReject.invoke(service,
                PurchaseOrderStatus.APPROVED, AppConstants.ROLE_OWNER));
    }

    // --- canCancel ---

    @Test
    @DisplayName("canCancel: DRAFT + creator returns true")
    void canCancel_draftCreator_returnsTrue() throws Exception {
        assertTrue((boolean) canCancel.invoke(service,
                PurchaseOrderStatus.DRAFT, "Staff", true));
    }

    @Test
    @DisplayName("canCancel: DRAFT + approver role returns true")
    void canCancel_draftApprover_returnsTrue() throws Exception {
        assertTrue((boolean) canCancel.invoke(service,
                PurchaseOrderStatus.DRAFT, AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: APPROVED + Owner role returns true")
    void canCancel_approvedOwner_returnsTrue() throws Exception {
        assertTrue((boolean) canCancel.invoke(service,
                PurchaseOrderStatus.APPROVED, AppConstants.ROLE_OWNER, false));
    }

    @Test
    @DisplayName("canCancel: APPROVED + non-Owner returns false")
    void canCancel_approvedNonOwner_returnsFalse() throws Exception {
        assertFalse((boolean) canCancel.invoke(service,
                PurchaseOrderStatus.APPROVED, AppConstants.ROLE_STORE_MANAGER, false));
    }

    @Test
    @DisplayName("canCancel: COMPLETED returns false")
    void canCancel_completed_returnsFalse() throws Exception {
        assertFalse((boolean) canCancel.invoke(service,
                PurchaseOrderStatus.COMPLETED, AppConstants.ROLE_OWNER, true));
    }

    @Test
    @DisplayName("canCancel: null status returns false")
    void canCancel_nullStatus_returnsFalse() throws Exception {
        assertFalse((boolean) canCancel.invoke(service,
                null, AppConstants.ROLE_OWNER, true));
    }

    // --- isApproverRole ---

    @Test
    @DisplayName("isApproverRole: Owner and StoreManager are approvers")
    void isApproverRole_valid() throws Exception {
        assertTrue((boolean) isApproverRole.invoke(service, AppConstants.ROLE_OWNER));
        assertTrue((boolean) isApproverRole.invoke(service, AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("isApproverRole: other roles are not approvers")
    void isApproverRole_invalid() throws Exception {
        assertFalse((boolean) isApproverRole.invoke(service, "Staff"));
        assertFalse((boolean) isApproverRole.invoke(service, "Cashier"));
        assertFalse((boolean) isApproverRole.invoke(service, (Object) null));
    }
}