package com.kiotretail.employee.service;

import com.kiotretail.employee.dao.ActivityBranchDAO;
import com.kiotretail.employee.dao.BranchDAO;
import com.kiotretail.employee.model.ActivityBranch;
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
    private final ActivityBranchDAO activityBranchDAO = new ActivityBranchDAO();

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
        return createBranch(branch, null);
    }

    public boolean createBranch(Branch branch, Integer createdBy) {
        validateBranch(branch);

        String name = branch.getName().trim();
        if (branchDAO.existsByName(name, null)) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Tên chi nhánh"));
        }

        branch.setName(name);
        if (branch.getStatus() == null || branch.getStatus().isEmpty()) {
            branch.setStatus(AppConstants.STATUS_ACTIVE);
        }

        boolean created = branchDAO.insert(branch);
        if (created) {
            recordActivity(branch.getBranchId(), AppConstants.ACTION_ADD, createdBy,
                    "Thêm chi nhánh: " + branch.getName());
        }
        return created;
    }

    public boolean updateBranch(Branch branch) {
        return updateBranch(branch, null);
    }

    public boolean updateBranch(Branch branch, Integer createdBy) {
        if (branch.getBranchId() <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã chi nhánh"));
        }
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

        boolean updated = branchDAO.update(branch);
        if (updated) {
            recordActivity(branch.getBranchId(), AppConstants.ACTION_UPDATE, createdBy,
                    "Cập nhật thông tin chi nhánh: " + branch.getName());
        }
        return updated;
    }

    public boolean deleteBranch(int branchId) {
        return deleteBranch(branchId, null);
    }

    public boolean deleteBranch(int branchId, Integer createdBy) {
        if (branchId <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã chi nhánh"));
        }
        Branch existing = getBranchById(branchId);
        boolean deleted = branchDAO.softDelete(branchId);
        if (deleted) {
            recordActivity(branchId, AppConstants.ACTION_DELETE, createdBy,
                    "Xóa chi nhánh: " + existing.getName());
        }
        return deleted;
    }

    public List<ActivityBranch> getActivitiesByBranchId(int branchId) {
        return activityBranchDAO.getByFkId(branchId);
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

    private void recordActivity(int fkId, String type, Integer createdBy, String description) {
        if (fkId <= 0) {
            return;
        }
        ActivityBranch activity = new ActivityBranch();
        activity.setFkId(fkId);
        activity.setType(type);
        activity.setCreatedBy(createdBy);
        activity.setDescription(description);
        activityBranchDAO.insert(activity);
    }
}