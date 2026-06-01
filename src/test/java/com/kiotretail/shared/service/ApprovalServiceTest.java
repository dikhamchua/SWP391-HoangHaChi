package com.kiotretail.shared.service;

import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ApprovalAction;
import com.kiotretail.shared.constant.DocumentStatus;
import com.kiotretail.shared.dao.ApprovalHistoryDAO;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.model.ApprovalHistory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests cho {@link ApprovalService}.
 *
 * Bao phu: 13 test case bat buoc theo issue #37 (APPR-001..APPR-013) cong
 * cac edge case bo sung de cung co quy tac trang thai, role va validation
 * cua logTransition.
 *
 * Cac case canSubmit/canApprove/canReject/canCancel khong cham DAO.
 * Cac case logTransition co cham DAO duoc inject 1 stub thong qua reflection
 * de tranh phu thuoc database.
 */
public class ApprovalServiceTest {

    private ApprovalService service;

    @BeforeEach
    void setUp() {
        service = new ApprovalService();
    }

    // ============================================================
    //  canSubmit (APPR-001, APPR-002 + edges)
    // ============================================================

    @Test
    @DisplayName("APPR-001: canSubmit khi status = DRAFT -> true")
    void appr001_canSubmit_draft_true() {
        Assertions.assertTrue(service.canSubmit(DocumentStatus.DRAFT));
    }

    @Test
    @DisplayName("APPR-002: canSubmit khi status = PENDING_APPROVAL -> false")
    void appr002_canSubmit_pendingApproval_false() {
        Assertions.assertFalse(service.canSubmit(DocumentStatus.PENDING_APPROVAL));
    }

    @Test
    @DisplayName("APPR-014 [edge]: canSubmit khi status = null -> false")
    void appr014_canSubmit_null_false() {
        Assertions.assertFalse(service.canSubmit(null));
    }

    @Test
    @DisplayName("APPR-015 [edge]: canSubmit khi status = APPROVED -> false")
    void appr015_canSubmit_approved_false() {
        Assertions.assertFalse(service.canSubmit(DocumentStatus.APPROVED));
    }

    @Test
    @DisplayName("APPR-016 [edge]: canSubmit khi status = REJECTED -> false (khong resubmit)")
    void appr016_canSubmit_rejected_false() {
        Assertions.assertFalse(service.canSubmit(DocumentStatus.REJECTED));
    }

    @Test
    @DisplayName("APPR-017 [edge]: canSubmit khi status = CANCELLED -> false")
    void appr017_canSubmit_cancelled_false() {
        Assertions.assertFalse(service.canSubmit(DocumentStatus.CANCELLED));
    }

    @Test
    @DisplayName("APPR-018 [edge]: canSubmit khi status = COMPLETED -> false")
    void appr018_canSubmit_completed_false() {
        Assertions.assertFalse(service.canSubmit(DocumentStatus.COMPLETED));
    }

    // ============================================================
    //  canApprove (APPR-003, APPR-004, APPR-005 + edges)
    // ============================================================

    @Test
    @DisplayName("APPR-003: canApprove PENDING_APPROVAL + STORE_MANAGER + approver!=creator -> true")
    void appr003_canApprove_storeManager_true() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_STORE_MANAGER,
                1, 2);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("APPR-004: canApprove khi approver == creator (self-approve) -> false")
    void appr004_canApprove_selfApprove_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_STORE_MANAGER,
                5, 5);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-005: canApprove role SALES_STAFF -> false")
    void appr005_canApprove_salesStaff_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_SALES_STAFF,
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-019 [edge]: canApprove role OWNER -> true (Owner cung duyet duoc)")
    void appr019_canApprove_owner_true() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_OWNER,
                1, 2);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("APPR-020 [edge]: canApprove status = DRAFT -> false (chua submit)")
    void appr020_canApprove_draft_false() {
        boolean result = service.canApprove(
                DocumentStatus.DRAFT,
                AppConstants.ROLE_STORE_MANAGER,
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-021 [edge]: canApprove status = APPROVED -> false (da duyet)")
    void appr021_canApprove_alreadyApproved_false() {
        boolean result = service.canApprove(
                DocumentStatus.APPROVED,
                AppConstants.ROLE_OWNER,
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-022 [edge]: canApprove status = REJECTED -> false")
    void appr022_canApprove_rejected_false() {
        boolean result = service.canApprove(
                DocumentStatus.REJECTED,
                AppConstants.ROLE_OWNER,
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-023 [edge]: canApprove role WAREHOUSE_STAFF -> false")
    void appr023_canApprove_warehouseStaff_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_WAREHOUSE_STAFF,
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-024 [edge]: canApprove role = null -> false (khong NPE)")
    void appr024_canApprove_nullRole_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                null,
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-025 [edge]: canApprove role = chuoi rong -> false")
    void appr025_canApprove_emptyRole_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                "",
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-026 [edge]: canApprove role lowercase 'owner' -> false (case sensitive)")
    void appr026_canApprove_lowercaseRole_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                "owner",
                1, 2);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("APPR-046 [edge]: canApprove status = null -> false")
    void appr046_canApprove_nullStatus_false() {
        boolean result = service.canApprove(
                null,
                AppConstants.ROLE_OWNER,
                1, 2);
        Assertions.assertFalse(result);
    }

    // ============================================================
    //  canReject (APPR-006, APPR-007 + edges)
    // ============================================================

    @Test
    @DisplayName("APPR-006: canReject PENDING_APPROVAL + STORE_MANAGER -> true")
    void appr006_canReject_storeManager_true() {
        Assertions.assertTrue(service.canReject(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("APPR-007: canReject status = DRAFT -> false")
    void appr007_canReject_draft_false() {
        Assertions.assertFalse(service.canReject(
                DocumentStatus.DRAFT,
                AppConstants.ROLE_STORE_MANAGER));
    }

    @Test
    @DisplayName("APPR-027 [edge]: canReject PENDING_APPROVAL + OWNER -> true")
    void appr027_canReject_owner_true() {
        Assertions.assertTrue(service.canReject(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("APPR-028 [edge]: canReject SALES_STAFF -> false")
    void appr028_canReject_salesStaff_false() {
        Assertions.assertFalse(service.canReject(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_SALES_STAFF));
    }

    @Test
    @DisplayName("APPR-029 [edge]: canReject WAREHOUSE_STAFF -> false")
    void appr029_canReject_warehouseStaff_false() {
        Assertions.assertFalse(service.canReject(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_WAREHOUSE_STAFF));
    }

    @Test
    @DisplayName("APPR-030 [edge]: canReject status = APPROVED -> false")
    void appr030_canReject_approved_false() {
        Assertions.assertFalse(service.canReject(
                DocumentStatus.APPROVED,
                AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("APPR-031 [edge]: canReject status = CANCELLED -> false")
    void appr031_canReject_cancelled_false() {
        Assertions.assertFalse(service.canReject(
                DocumentStatus.CANCELLED,
                AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("APPR-032 [edge]: canReject status = null -> false")
    void appr032_canReject_nullStatus_false() {
        Assertions.assertFalse(service.canReject(null, AppConstants.ROLE_OWNER));
    }

    @Test
    @DisplayName("APPR-033 [edge]: canReject role = null -> false (khong NPE)")
    void appr033_canReject_nullRole_false() {
        Assertions.assertFalse(service.canReject(
                DocumentStatus.PENDING_APPROVAL, null));
    }

    // ============================================================
    //  canCancel (APPR-008, APPR-009, APPR-010 + edges)
    // ============================================================

    @Test
    @DisplayName("APPR-008: canCancel DRAFT + isOwner=true -> true")
    void appr008_canCancel_draftCreator_true() {
        Assertions.assertTrue(service.canCancel(
                DocumentStatus.DRAFT,
                AppConstants.ROLE_SALES_STAFF,
                true));
    }

    @Test
    @DisplayName("APPR-009: canCancel APPROVED + role OWNER -> true")
    void appr009_canCancel_approvedOwner_true() {
        Assertions.assertTrue(service.canCancel(
                DocumentStatus.APPROVED,
                AppConstants.ROLE_OWNER,
                false));
    }

    @Test
    @DisplayName("APPR-010: canCancel APPROVED + role STORE_MANAGER -> false")
    void appr010_canCancel_approvedStoreManager_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.APPROVED,
                AppConstants.ROLE_STORE_MANAGER,
                true));
    }

    @Test
    @DisplayName("APPR-034 [edge]: canCancel PENDING_APPROVAL + isOwner=true -> true")
    void appr034_canCancel_pendingCreator_true() {
        Assertions.assertTrue(service.canCancel(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_SALES_STAFF,
                true));
    }

    @Test
    @DisplayName("APPR-035 [edge]: canCancel PENDING_APPROVAL + STORE_MANAGER -> true")
    void appr035_canCancel_pendingStoreManager_true() {
        Assertions.assertTrue(service.canCancel(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_STORE_MANAGER,
                false));
    }

    @Test
    @DisplayName("APPR-036 [edge]: canCancel PENDING_APPROVAL + SALES_STAFF + khong phai owner -> false")
    void appr036_canCancel_pendingSalesStaffNotOwner_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_SALES_STAFF,
                false));
    }

    @Test
    @DisplayName("APPR-037 [edge]: canCancel IN_PROGRESS + OWNER -> true")
    void appr037_canCancel_inProgressOwner_true() {
        Assertions.assertTrue(service.canCancel(
                DocumentStatus.IN_PROGRESS,
                AppConstants.ROLE_OWNER,
                false));
    }

    @Test
    @DisplayName("APPR-038 [edge]: canCancel IN_PROGRESS + STORE_MANAGER -> false")
    void appr038_canCancel_inProgressStoreManager_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.IN_PROGRESS,
                AppConstants.ROLE_STORE_MANAGER,
                true));
    }

    @Test
    @DisplayName("APPR-039 [edge]: canCancel RECEIVING + OWNER -> true")
    void appr039_canCancel_receivingOwner_true() {
        Assertions.assertTrue(service.canCancel(
                DocumentStatus.RECEIVING,
                AppConstants.ROLE_OWNER,
                false));
    }

    @Test
    @DisplayName("APPR-040 [edge]: canCancel RECEIVING + SALES_STAFF -> false")
    void appr040_canCancel_receivingSalesStaff_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.RECEIVING,
                AppConstants.ROLE_SALES_STAFF,
                true));
    }

    @Test
    @DisplayName("APPR-041 [edge]: canCancel COMPLETED -> false (terminal)")
    void appr041_canCancel_completed_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.COMPLETED,
                AppConstants.ROLE_OWNER,
                true));
    }

    @Test
    @DisplayName("APPR-042 [edge]: canCancel CANCELLED -> false (da huy)")
    void appr042_canCancel_cancelled_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.CANCELLED,
                AppConstants.ROLE_OWNER,
                true));
    }

    @Test
    @DisplayName("APPR-043 [edge]: canCancel REJECTED -> false")
    void appr043_canCancel_rejected_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.REJECTED,
                AppConstants.ROLE_OWNER,
                true));
    }

    @Test
    @DisplayName("APPR-044 [edge]: canCancel FINALIZED -> false")
    void appr044_canCancel_finalized_false() {
        Assertions.assertFalse(service.canCancel(
                DocumentStatus.FINALIZED,
                AppConstants.ROLE_OWNER,
                true));
    }

    @Test
    @DisplayName("APPR-045 [edge]: canCancel status = null -> false")
    void appr045_canCancel_nullStatus_false() {
        Assertions.assertFalse(service.canCancel(
                null, AppConstants.ROLE_OWNER, true));
    }

    // ============================================================
    //  logTransition (APPR-011, APPR-012, APPR-013 + edges)
    // ============================================================

    @Test
    @DisplayName("APPR-011: logTransition REJECT khong co reason -> ValidationException")
    void appr011_logTransition_rejectNoReason_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.PENDING_APPROVAL,
                        DocumentStatus.REJECTED,
                        ApprovalAction.REJECT,
                        10, null));
    }

    @Test
    @DisplayName("APPR-012: logTransition CANCEL khong co reason -> ValidationException")
    void appr012_logTransition_cancelNoReason_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.APPROVED,
                        DocumentStatus.CANCELLED,
                        ApprovalAction.CANCEL,
                        10, null));
    }

    @Test
    @DisplayName("APPR-013: logTransition hop le -> insert vao ApprovalHistory")
    void appr013_logTransition_valid_inserts() {
        StubApprovalHistoryDAO stub = new StubApprovalHistoryDAO();
        injectFakeDao(service, stub);

        service.logTransition("PURCHASE_ORDER", 42,
                DocumentStatus.DRAFT,
                DocumentStatus.PENDING_APPROVAL,
                ApprovalAction.SUBMIT,
                7, "Submit don hang");

        Assertions.assertEquals(1, stub.stored.size(), "Phai insert dung 1 row");
        ApprovalHistory saved = stub.stored.get(0);
        Assertions.assertEquals("PURCHASE_ORDER", saved.getDocumentType());
        Assertions.assertEquals(42, saved.getDocumentId());
        Assertions.assertEquals("DRAFT", saved.getFromStatus());
        Assertions.assertEquals("PENDING_APPROVAL", saved.getToStatus());
        Assertions.assertEquals("SUBMIT", saved.getAction());
        Assertions.assertEquals(7, saved.getPerformedBy());
        Assertions.assertEquals("Submit don hang", saved.getReason());
    }

    @Test
    @DisplayName("APPR-047 [edge]: logTransition docType = null -> ValidationException")
    void appr047_logTransition_nullDocType_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition(null, 1,
                        DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                        ApprovalAction.SUBMIT, 1, "x"));
    }

    @Test
    @DisplayName("APPR-048 [edge]: logTransition docType blank -> ValidationException")
    void appr048_logTransition_blankDocType_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("   ", 1,
                        DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                        ApprovalAction.SUBMIT, 1, "x"));
    }

    @Test
    @DisplayName("APPR-049 [edge]: logTransition target status = null -> ValidationException")
    void appr049_logTransition_nullToStatus_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.DRAFT, null,
                        ApprovalAction.SUBMIT, 1, "x"));
    }

    @Test
    @DisplayName("APPR-050 [edge]: logTransition action = null -> ValidationException")
    void appr050_logTransition_nullAction_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                        null, 1, "x"));
    }

    @Test
    @DisplayName("APPR-051 [edge]: logTransition performedBy = 0 -> ValidationException")
    void appr051_logTransition_performedByZero_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                        ApprovalAction.SUBMIT, 0, "x"));
    }

    @Test
    @DisplayName("APPR-052 [edge]: logTransition performedBy < 0 -> ValidationException")
    void appr052_logTransition_performedByNegative_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                        ApprovalAction.SUBMIT, -5, "x"));
    }

    @Test
    @DisplayName("APPR-053 [edge]: logTransition REJECT reason chi co whitespace -> ValidationException")
    void appr053_logTransition_rejectBlankReason_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.PENDING_APPROVAL, DocumentStatus.REJECTED,
                        ApprovalAction.REJECT, 5, "    "));
    }

    @Test
    @DisplayName("APPR-054 [edge]: logTransition CANCEL reason chi co whitespace -> ValidationException")
    void appr054_logTransition_cancelBlankReason_throws() {
        Assertions.assertThrows(ValidationException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.APPROVED, DocumentStatus.CANCELLED,
                        ApprovalAction.CANCEL, 5, ""));
    }

    @Test
    @DisplayName("APPR-055 [edge]: logTransition APPROVE khong can reason -> insert ok")
    void appr055_logTransition_approveNoReason_ok() {
        StubApprovalHistoryDAO stub = new StubApprovalHistoryDAO();
        injectFakeDao(service, stub);

        service.logTransition("PURCHASE_ORDER", 1,
                DocumentStatus.PENDING_APPROVAL, DocumentStatus.APPROVED,
                ApprovalAction.APPROVE, 9, null);

        Assertions.assertEquals(1, stub.stored.size());
        Assertions.assertEquals("APPROVE", stub.stored.get(0).getAction());
        Assertions.assertNull(stub.stored.get(0).getReason());
    }

    @Test
    @DisplayName("APPR-056 [edge]: logTransition CREATE voi from = null -> insert ok (CREATE khong co fromStatus)")
    void appr056_logTransition_createNullFrom_ok() {
        StubApprovalHistoryDAO stub = new StubApprovalHistoryDAO();
        injectFakeDao(service, stub);

        service.logTransition("PURCHASE_ORDER", 1,
                null, DocumentStatus.DRAFT,
                ApprovalAction.CREATE, 3, null);

        Assertions.assertEquals(1, stub.stored.size());
        Assertions.assertNull(stub.stored.get(0).getFromStatus(),
                "fromStatus phai la null khi CREATE");
    }

    @Test
    @DisplayName("APPR-057 [edge]: logTransition trim docType truoc khi luu")
    void appr057_logTransition_trimsDocType() {
        StubApprovalHistoryDAO stub = new StubApprovalHistoryDAO();
        injectFakeDao(service, stub);

        service.logTransition("  PURCHASE_ORDER  ", 1,
                DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                ApprovalAction.SUBMIT, 1, null);

        Assertions.assertEquals("PURCHASE_ORDER", stub.stored.get(0).getDocumentType());
    }

    @Test
    @DisplayName("APPR-058 [edge]: logTransition khi DAO tra ve -1 -> ServiceException")
    void appr058_logTransition_daoFails_throws() {
        injectFakeDao(service, new FailingApprovalHistoryDAO());

        ServiceException ex = Assertions.assertThrows(ServiceException.class, () ->
                service.logTransition("PURCHASE_ORDER", 1,
                        DocumentStatus.DRAFT, DocumentStatus.PENDING_APPROVAL,
                        ApprovalAction.SUBMIT, 1, null));
        Assertions.assertEquals(500, ex.getStatusCode());
    }

    // ============================================================
    //  DocumentStatus.fromString (parser tien ich di kem)
    // ============================================================

    @Test
    @DisplayName("APPR-059 [edge]: DocumentStatus.fromString case-insensitive")
    void appr059_fromString_caseInsensitive() {
        Assertions.assertEquals(DocumentStatus.DRAFT,
                DocumentStatus.fromString("draft"));
        Assertions.assertEquals(DocumentStatus.PENDING_APPROVAL,
                DocumentStatus.fromString("Pending_Approval"));
    }

    @Test
    @DisplayName("APPR-060 [edge]: DocumentStatus.fromString null/blank/unknown -> null")
    void appr060_fromString_invalid_null() {
        Assertions.assertNull(DocumentStatus.fromString(null));
        Assertions.assertNull(DocumentStatus.fromString(""));
        Assertions.assertNull(DocumentStatus.fromString("   "));
        Assertions.assertNull(DocumentStatus.fromString("WHATEVER"));
    }

    // ============================================================
    //  Test helpers
    // ============================================================

    private static void injectFakeDao(ApprovalService target, ApprovalHistoryDAO fake) {
        try {
            Field f = ApprovalService.class.getDeclaredField("historyDAO");
            f.setAccessible(true);
            f.set(target, fake);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot inject fake DAO", e);
        }
    }

    /** Stub luu ban ghi vao memory thay vi cham DB. */
    static class StubApprovalHistoryDAO extends ApprovalHistoryDAO {
        final List<ApprovalHistory> stored = new ArrayList<>();
        int nextId = 1;

        @Override
        public int insert(ApprovalHistory history) {
            int id = nextId++;
            history.setHistoryId(id);
            stored.add(history);
            return id;
        }
    }

    /** Stub mo phong DAO that bai (insert tra ve -1). */
    static class FailingApprovalHistoryDAO extends ApprovalHistoryDAO {
        @Override
        public int insert(ApprovalHistory history) {
            return -1;
        }
    }
}
