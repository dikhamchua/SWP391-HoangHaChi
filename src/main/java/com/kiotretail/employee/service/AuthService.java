package com.kiotretail.employee.service;

import com.kiotretail.employee.dao.EmployeeDAO;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.util.PasswordUtil;

/**
 * Authentication service: login, register, password change, password reset.
 * Uses EmployeeDAO for persistence and PasswordUtil for BCrypt hashing.
 */
public class AuthService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Authenticate an employee by email and password.
     * Supports legacy plaintext passwords by upgrading to BCrypt on successful login.
     *
     * @param email    the employee email
     * @param password the plain text password
     * @return the authenticated Employee
     * @throws ServiceException when credentials are invalid or account is locked
     */
    public Employee login(String email, String password) {
        Employee employee = employeeDAO.getByEmail(email);
        if (employee == null) {
            // Use a generic message to prevent user enumeration via login responses.
            throw new ServiceException(ErrorMessages.INVALID_CREDENTIALS, 401);
        }

        if (!AppConstants.STATUS_ACTIVE.equalsIgnoreCase(employee.getStatus())) {
            throw new ServiceException(ErrorMessages.ACCOUNT_LOCKED, 403);
        }

        String storedHash = employee.getPasswordHash();
        if (PasswordUtil.isHashed(storedHash)) {
            if (!PasswordUtil.verify(password, storedHash)) {
                throw new ServiceException(ErrorMessages.INVALID_CREDENTIALS, 401);
            }
        } else {
            // Legacy plaintext path: compare directly, upgrade to BCrypt on match.
            if (storedHash == null || !storedHash.equals(password)) {
                throw new ServiceException(ErrorMessages.INVALID_CREDENTIALS, 401);
            }
            String newHash = PasswordUtil.hash(password);
            employeeDAO.updatePassword(employee.getEmployeeId(), newHash);
            employee.setPasswordHash(newHash);
        }

        return employee;
    }

    /**
     * Register a new employee with hashed password and active status.
     *
     * @return true if the insert succeeded
     */
    public boolean register(String fullName, String email, String phone,
                            String password, int roleId, int branchId) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Ho ten"));
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Email"));
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Mat khau"));
        }
        if (employeeDAO.existsByEmail(email, null)) {
            throw new ValidationException(ErrorMessages.EMAIL_EXISTS);
        }

        Employee employee = new Employee();
        employee.setFullName(fullName.trim());
        employee.setEmail(email.trim());
        employee.setPhone(phone);
        employee.setPasswordHash(PasswordUtil.hash(password));
        employee.setRoleId(roleId);
        employee.setBranchId(branchId);
        employee.setStatus(AppConstants.STATUS_ACTIVE);

        return employeeDAO.insert(employee);
    }

    /**
     * Change password for the given employee after verifying the current password.
     */
    public boolean changePassword(int employeeId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Mat khau moi khong duoc de trong");
        }
        Employee employee = employeeDAO.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Employee", employeeId);
        }

        String storedHash = employee.getPasswordHash();
        if (PasswordUtil.isHashed(storedHash)) {
            if (!PasswordUtil.verify(oldPassword, storedHash)) {
                throw new ServiceException(ErrorMessages.OLD_PASSWORD_WRONG, 401);
            }
        } else {
            if (storedHash == null || !storedHash.equals(oldPassword)) {
                throw new ServiceException(ErrorMessages.OLD_PASSWORD_WRONG, 401);
            }
        }

        return employeeDAO.updatePassword(employeeId, PasswordUtil.hash(newPassword));
    }

    /**
     * Reset password for the employee identified by email (admin/forgot-password flow).
     */
    public boolean resetPassword(String email, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Mat khau moi khong duoc de trong");
        }
        Employee employee = employeeDAO.getByEmail(email);
        if (employee == null) {
            throw new NotFoundException("Employee", email);
        }
        return employeeDAO.updatePassword(employee.getEmployeeId(), PasswordUtil.hash(newPassword));
    }
}
