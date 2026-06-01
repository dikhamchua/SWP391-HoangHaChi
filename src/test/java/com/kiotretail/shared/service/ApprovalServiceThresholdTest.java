package com.kiotretail.shared.service;

import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.DocumentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Unit tests cho overload canApprove 5 tham so (amount-aware) cua
 * {@link ApprovalService}.
 *
 * <p>Quy tac kiem tra: ap dung lai cac dieu kien co ban (PENDING_APPROVAL,
 * approver khac creator, role co quyen duyet) roi ap nguong
 * {@link AppConstants#OWNER_APPROVAL_THRESHOLD}: tu nguong tro len chi Owner
 * moi duoc duyet, Store Manager bi chan.</p>
 *
 * <p>Nhanh kiem tra nguong khong cham DAO/DB nen khong can inject stub.</p>
 */
public class ApprovalServiceThresholdTest {

    private ApprovalService service;

    /** Duoi nguong: thap hon OWNER_APPROVAL_THRESHOLD mot khoang. */
    private static final BigDecimal BELOW_THRESHOLD =
            AppConstants.OWNER_APPROVAL_THRESHOLD.subtract(new BigDecimal("10000000"));

    /** Dung bang nguong (boundary). */
    private static final BigDecimal AT_THRESHOLD =
            AppConstants.OWNER_APPROVAL_THRESHOLD;

    /** Tren nguong: cao hon OWNER_APPROVAL_THRESHOLD mot khoang. */
    private static final BigDecimal ABOVE_THRESHOLD =
            AppConstants.OWNER_APPROVAL_THRESHOLD.add(new BigDecimal("30000000"));

    @BeforeEach
    void setUp() {
        service = new ApprovalService();
    }

    @Test
    @DisplayName("THR-001: Manager + duoi nguong + approver!=creator + PENDING_APPROVAL -> true")
    void thr001_manager_belowThreshold_true() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_STORE_MANAGER,
                1, 2,
                BELOW_THRESHOLD);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("THR-002: Manager + dung bang nguong -> false (chi Owner moi duyet)")
    void thr002_manager_atThreshold_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_STORE_MANAGER,
                1, 2,
                AT_THRESHOLD);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("THR-003: Owner + tren nguong -> true")
    void thr003_owner_aboveThreshold_true() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_OWNER,
                1, 2,
                ABOVE_THRESHOLD);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("THR-004: Self-approve (creator == approver) + bat ky amount -> false")
    void thr004_selfApprove_false() {
        boolean result = service.canApprove(
                DocumentStatus.PENDING_APPROVAL,
                AppConstants.ROLE_OWNER,
                5, 5,
                ABOVE_THRESHOLD);
        Assertions.assertFalse(result);
    }
}
