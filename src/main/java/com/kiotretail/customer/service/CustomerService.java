package com.kiotretail.customer.service;

import com.kiotretail.customer.dao.ActivityCustomerDAO;
import com.kiotretail.customer.dao.CustomerDAO;
import com.kiotretail.customer.dto.CustomerFilterDTO;
import com.kiotretail.customer.model.ActivityCustomer;
import com.kiotretail.customer.model.Customer;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;

import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ActivityCustomerDAO activityCustomerDAO = new ActivityCustomerDAO();

    public PageResult<Customer> listCustomers(CustomerFilterDTO filter, Pagination pagination) {
        List<Customer> items = customerDAO.getCustomers(filter, pagination);
        int total = customerDAO.countCustomers(filter);
        return PageResult.of(items, total, pagination);
    }

    public Customer getCustomerById(int id) {
        Customer customer = customerDAO.getById(id);
        if (customer == null) {
            throw new NotFoundException("Customer", id);
        }
        return customer;
    }

    public boolean createCustomer(Customer customer) {
        return createCustomer(customer, null);
    }

    public boolean createCustomer(Customer customer, Integer createdBy) {
        validateCustomer(customer);

        String phone = customer.getPhone().trim();
        if (customerDAO.existsByPhone(phone, null)) {
            throw new ValidationException(ErrorMessages.PHONE_EXISTS);
        }

        if (customer.getMembershipTier() == null || customer.getMembershipTier().isEmpty()) {
            customer.setMembershipTier(AppConstants.TIER_MEMBER);
        }
        customer.setPoints(0);

        boolean created = customerDAO.insert(customer);
        if (created) {
            recordActivity(customer.getCustomerId(), AppConstants.ACTION_ADD, createdBy,
                    "Thêm khách hàng: " + customer.getFullName());
        }
        return created;
    }

    public boolean updateCustomer(Customer customer) {
        return updateCustomer(customer, null);
    }

    public boolean updateCustomer(Customer customer, Integer createdBy) {
        validateCustomer(customer);

        String phone = customer.getPhone().trim();
        if (customerDAO.existsByPhone(phone, customer.getCustomerId())) {
            throw new ValidationException(ErrorMessages.PHONE_EXISTS);
        }

        Customer existing = getCustomerById(customer.getCustomerId());
        customer.setPoints(existing.getPoints());
        boolean updated = customerDAO.update(customer);
        if (updated) {
            recordActivity(customer.getCustomerId(), AppConstants.ACTION_UPDATE, createdBy,
                    "Cập nhật thông tin khách hàng: " + customer.getFullName());
        }
        return updated;
    }

    public boolean deleteCustomer(int customerId) {
        return deleteCustomer(customerId, null);
    }

    public boolean deleteCustomer(int customerId, Integer createdBy) {
        Customer existing = getCustomerById(customerId);
        boolean deleted = customerDAO.delete(customerId);
        if (deleted) {
            recordActivity(customerId, AppConstants.ACTION_DELETE, createdBy,
                    "Xóa khách hàng: " + existing.getFullName());
        }
        return deleted;
    }

    public List<ActivityCustomer> getActivitiesByCustomerId(int customerId) {
        return activityCustomerDAO.getByFkId(customerId);
    }

    public List<Customer> searchCustomers(String keyword, int limit) {
        CustomerFilterDTO filter = new CustomerFilterDTO();
        filter.setKeyword(keyword);
        Pagination pagination = Pagination.of(1, limit);
        return customerDAO.getCustomers(filter, pagination);
    }

    private void recordActivity(int fkId, String type, Integer createdBy, String description) {
        if (fkId <= 0) {
            return;
        }
        ActivityCustomer activity = new ActivityCustomer();
        activity.setFkId(fkId);
        activity.setType(type);
        activity.setCreatedBy(createdBy);
        activity.setDescription(description);
        activityCustomerDAO.insert(activity);
    }

    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MAX_NAME_LENGTH = 255;

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new ValidationException(String.format(ErrorMessages.NOT_FOUND, "Customer"));
        }
        if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Ho ten"));
        }
        if (customer.getFullName().trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Ho ten (toi da 255 ky tu)"));
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "So dien thoai"));
        }
        if (!customer.getPhone().trim().matches(PHONE_REGEX)) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "So dien thoai"));
        }
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()
                && !customer.getEmail().trim().matches(EMAIL_REGEX)) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Email"));
        }
    }
}
