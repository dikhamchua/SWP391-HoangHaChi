package com.kiotretail.product.service;

import com.kiotretail.product.dao.SupplierDAO;
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
        validateSupplier(supplier);

        String name = supplier.getName().trim();
        if (supplierDAO.existsByName(name, null)) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Tên nhà cung cấp"));
        }

        supplier.setName(name);
        if (supplier.getStatus() == null || supplier.getStatus().isEmpty()) {
            supplier.setStatus(AppConstants.STATUS_ACTIVE);
        }
        return supplierDAO.insert(supplier);
    }

    public boolean updateSupplier(Supplier supplier) {
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
        return supplierDAO.update(supplier);
    }

    public boolean deleteSupplier(int supplierId) {
        if (supplierId <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã nhà cung cấp"));
        }
        return supplierDAO.delete(supplierId);
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new ValidationException(String.format(ErrorMessages.NOT_FOUND, "Supplier"));
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
        }
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()
                && !supplier.getEmail().trim().matches(EMAIL_REGEX)) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Email"));
        }
        if (supplier.getStatus() != null && !supplier.getStatus().isEmpty()) {
            String status = supplier.getStatus();
            if (!AppConstants.STATUS_ACTIVE.equals(status) && !AppConstants.STATUS_INACTIVE.equals(status)) {
                throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Trạng thái"));
            }
        }
    }
}
