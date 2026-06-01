package com.kiotretail.employee.service;

import com.kiotretail.employee.model.Branch;
import com.kiotretail.shared.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BranchService} validation logic.
 *
 * Strategy: only exercise paths that throw {@link ValidationException} BEFORE
 * the service touches the {@link com.kiotretail.employee.dao.BranchDAO}. These
 * tests therefore do not require a database connection.
 */
public class BranchServiceTest {

    private BranchService service;

    @BeforeEach
    void setUp() {
        service = new BranchService();
    }

    @Test
    @DisplayName("createBranch with null branch throws ValidationException")
    void testCreateBranch_NullBranch_ThrowsValidationException() {
        Assertions.assertThrows(ValidationException.class,
                () -> service.createBranch(null));
    }

    @Test
    @DisplayName("createBranch with empty name throws ValidationException")
    void testCreateBranch_EmptyName_ThrowsValidationException() {
        Branch branch = new Branch();
        branch.setName("   ");
        branch.setAddress("123 Le Loi");
        branch.setPhone("0901234567");
        Assertions.assertThrows(ValidationException.class,
                () -> service.createBranch(branch));
    }

    @Test
    @DisplayName("createBranch with invalid phone format throws ValidationException")
    void testCreateBranch_InvalidPhone_ThrowsValidationException() {
        Branch branch = new Branch();
        branch.setName("Chi nhanh Quan 1");
        branch.setAddress("123 Le Loi");
        branch.setPhone("not-a-phone-!!");
        Assertions.assertThrows(ValidationException.class,
                () -> service.createBranch(branch));
    }

    @Test
    @DisplayName("createBranch with name longer than 255 chars throws ValidationException")
    void testCreateBranch_NameTooLong_ThrowsValidationException() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 260; i++) {
            sb.append('a');
        }
        Branch branch = new Branch();
        branch.setName(sb.toString());
        branch.setAddress("123 Le Loi");
        branch.setPhone("0901234567");
        Assertions.assertThrows(ValidationException.class,
                () -> service.createBranch(branch));
    }

    @Test
    @DisplayName("updateBranch with non-positive id throws ValidationException")
    void testUpdateBranch_InvalidId_ThrowsValidationException() {
        Branch branch = new Branch();
        branch.setBranchId(0);
        branch.setName("Chi nhanh A");
        Assertions.assertThrows(ValidationException.class,
                () -> service.updateBranch(branch));
    }

    @Test
    @DisplayName("deleteBranch with non-positive id throws ValidationException")
    void testDeleteBranch_InvalidId_ThrowsValidationException() {
        Assertions.assertThrows(ValidationException.class,
                () -> service.deleteBranch(-1));
    }
}
