package com.kiotretail.employee.service;

import com.kiotretail.employee.dao.BranchDAO;
import com.kiotretail.employee.model.Branch;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;

import java.util.List;

public class BranchService {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_ADDRESS_LENGTH = 255;
    private static final int MAX_PHONE_LENGTH = 30;
    private static final String PHONE_REGEX = "^[0-9+\\-\\s]{6,30}$";

    private final BranchDAO branchDAO = new BranchDAO();

    public PageResult<Branch> listBranches(String keyword, Pagination pagination) {
        List<Branch> items = branchDAO.search(keyword, pagination);
        int total = branchDAO.countAll(keyword);
        return PageResult.of(items, total, pagination);
    }

    public PageResult<Branch> listBranches(Pagination pagination) {
        return listBranches(null, pagination);
    }

    public Branch getBranchById(int id) {
        Branch branch = branchDAO.getById(id);
        if (branch == null) {
            throw new NotFoundException("Branch", id);
        }
        return branch;
    }

    public boolean createBranch(Branch branch) {
        validateBranch(branch);

        String name = branch.getName().trim();
        if (branchDAO.existsByName(name, null)) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Tên chi nhánh"));
        }

        branch.setName(name);
        if (branch.getStatus() == null || branch.getStatus().isEmpty()) {
            branch.setStatus(AppConstants.STATUS_ACTIVE);
        }
        return branchDAO.insert(branch);
    }

    public boolean updateBranch(Branch branch) {
        if (branch.getBranchId() <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã chi nhánh"));
        }
        // Ensure target row exists before validating uniqueness against itself.
        getBranchById(branch.getBranchId());

        validateBranch(branch);

        String name = branch.getName().trim();
        if (branchDAO.existsByName(name, branch.getBranchId())) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Tên chi nhánh"));
        }

        branch.setName(name);
        if (branch.getStatus() == null || branch.getStatus().isEmpty()) {
            branch.setStatus(AppConstants.STATUS_ACTIVE);
        }
        return branchDAO.update(branch);
    }

    public boolean deleteBranch(int branchId) {
        if (branchId <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã chi nhánh"));
        }
        // Ensure target branch exists before attempting soft delete.
        getBranchById(branchId);
        return branchDAO.softDelete(branchId);
    }

    private void validateBranch(Branch branch) {
        if (branch == null) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Dữ liệu chi nhánh"));
        }
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Tên chi nhánh"));
        }
        if (branch.getName().trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Tên chi nhánh (tối đa 255 ký tự)"));
        }
        if (branch.getAddress() != null && branch.getAddress().trim().length() > MAX_ADDRESS_LENGTH) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Địa chỉ (tối đa 255 ký tự)"));
        }
        if (branch.getPhone() != null && !branch.getPhone().trim().isEmpty()) {
            String phone = branch.getPhone().trim();
            if (phone.length() > MAX_PHONE_LENGTH || !phone.matches(PHONE_REGEX)) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Số điện thoại"));
            }
        }
        if (branch.getStatus() != null && !branch.getStatus().isEmpty()) {
            String status = branch.getStatus();
            if (!AppConstants.STATUS_ACTIVE.equals(status) && !AppConstants.STATUS_INACTIVE.equals(status)) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Trạng thái"));
            }
        }
    }
}
