package com.kiotretail.product.service;

import com.kiotretail.product.dao.ActivitySupplierDAO;
import com.kiotretail.product.dao.SupplierDAO;
import com.kiotretail.product.model.ActivitySupplier;
import com.kiotretail.product.model.Supplier;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;

import java.util.List;

public class SupplierService {

    private static final String PHONE_REGEX = "^[0-9+\\-\\s]{6,30}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_ADDRESS_LENGTH = 255;
    private static final int MAX_PHONE_LENGTH = 30;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ActivitySupplierDAO activitySupplierDAO = new ActivitySupplierDAO();

    public PageResult<Supplier> listSuppliers(String keyword, Pagination pagination) {
        List<Supplier> items = supplierDAO.search(keyword, pagination);
        int total = supplierDAO.countAll(keyword);
        return PageResult.of(items, total, pagination);
    }

    public PageResult<Supplier> listSuppliers(Pagination pagination) {
        return listSuppliers(null, pagination);
    }

    public Supplier getSupplierById(int id) {
        Supplier supplier = supplierDAO.getById(id);
        if (supplier == null) {
            throw new NotFoundException("Supplier", id);
        }
        return supplier;
    }

    public boolean createSupplier(Supplier supplier) {
        return createSupplier(supplier, null);
    }

    public boolean createSupplier(Supplier supplier, Integer createdBy) {
        validateSupplier(supplier);

        String name = supplier.getName().trim();
        if (supplierDAO.existsByName(name, null)) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Tên nhà cung cấp"));
        }

        supplier.setName(name);
        if (supplier.getStatus() == null || supplier.getStatus().isEmpty()) {
            supplier.setStatus(AppConstants.STATUS_ACTIVE);
        }
        boolean created = supplierDAO.insert(supplier);
        if (created) {
            recordActivity(supplier.getSupplierId(), AppConstants.ACTION_ADD, createdBy,
                    "Thêm nhà cung cấp: " + supplier.getName());
        }
        return created;
    }

    public boolean updateSupplier(Supplier supplier) {
        return updateSupplier(supplier, null);
    }

    public boolean updateSupplier(Supplier supplier, Integer createdBy) {
        if (supplier.getSupplierId() <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã nhà cung cấp"));
        }
        getSupplierById(supplier.getSupplierId());

        validateSupplier(supplier);

        String name = supplier.getName().trim();
        if (supplierDAO.existsByName(name, supplier.getSupplierId())) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Tên nhà cung cấp"));
        }

        supplier.setName(name);
        if (supplier.getStatus() == null || supplier.getStatus().isEmpty()) {
            supplier.setStatus(AppConstants.STATUS_ACTIVE);
        }
        boolean updated = supplierDAO.update(supplier);
        if (updated) {
            recordActivity(supplier.getSupplierId(), AppConstants.ACTION_UPDATE, createdBy,
                    "Cập nhật thông tin nhà cung cấp: " + supplier.getName());
        }
        return updated;
    }

    public boolean deleteSupplier(int supplierId) {
        return deleteSupplier(supplierId, null);
    }

    public boolean deleteSupplier(int supplierId, Integer createdBy) {
        if (supplierId <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã nhà cung cấp"));
        }
        Supplier existing = getSupplierById(supplierId);
        boolean deleted = supplierDAO.softDelete(supplierId);
        if (deleted) {
            recordActivity(supplierId, AppConstants.ACTION_DELETE, createdBy,
                    "Xóa nhà cung cấp: " + existing.getName());
        }
        return deleted;
    }

    public List<ActivitySupplier> getActivitiesBySupplierId(int supplierId) {
        return activitySupplierDAO.getByFkId(supplierId);
    }

    private void recordActivity(int fkId, String type, Integer createdBy, String description) {
        if (fkId <= 0) return;
        ActivitySupplier activity = new ActivitySupplier();
        activity.setFkId(fkId);
        activity.setType(type);
        activity.setCreatedBy(createdBy);
        activity.setDescription(description);
        activitySupplierDAO.insert(activity);
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Dữ liệu nhà cung cấp"));
        }
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Tên nhà cung cấp"));
        }
        if (supplier.getName().trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Tên nhà cung cấp (tối đa 255 ký tự)"));
        }
        if (supplier.getAddress() != null && supplier.getAddress().trim().length() > MAX_ADDRESS_LENGTH) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Địa chỉ (tối đa 255 ký tự)"));
        }
        if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
            String phone = supplier.getPhone().trim();
            if (phone.length() > MAX_PHONE_LENGTH || !phone.matches(PHONE_REGEX)) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Số điện thoại"));
            }
            supplier.setPhone(phone);
        }
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            String email = supplier.getEmail().trim();
            if (email.length() > 100) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Email (tối đa 100 ký tự)"));
            }
            if (!email.matches(EMAIL_REGEX)) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Email"));
            }
            supplier.setEmail(email);
        }
        if (supplier.getStatus() != null && !supplier.getStatus().isEmpty()) {
            String status = supplier.getStatus();
            if (!AppConstants.STATUS_ACTIVE.equals(status) && !AppConstants.STATUS_INACTIVE.equals(status)) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Trạng thái"));
            }
        }
    }
}
