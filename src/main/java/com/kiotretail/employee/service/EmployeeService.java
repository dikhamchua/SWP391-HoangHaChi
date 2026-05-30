package com.kiotretail.employee.service;

import com.kiotretail.employee.dao.BranchDAO;
import com.kiotretail.employee.dao.EmployeeDAO;
import com.kiotretail.employee.dao.RoleDAO;
import com.kiotretail.employee.model.Branch;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.model.Role;
import com.kiotretail.shared.base.BaseService;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.util.PasswordUtil;

import java.util.List;

/**
 * Employee service for admin CRUD operations (not authentication).
 * Handles validation, password hashing, and reference checks for employees.
 */
public class EmployeeService extends BaseService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final BranchDAO branchDAO = new BranchDAO();

    /**
     * Returns a paginated list of employees.
     */
    public PageResult<Employee> listEmployees(Pagination pagination) {
        if (pagination == null) {
            throw new IllegalArgumentException("pagination must not be null");
        }
        List<Employee> items = employeeDAO.getAll(pagination);
        int total = employeeDAO.countAll();
        return PageResult.of(items, total, pagination);
    }

    /**
     * Loads an employee by ID, throwing NotFoundException if missing.
     */
    public Employee getEmployeeById(int id) {
        Employee employee = employeeDAO.getById(id);
        if (employee == null) {
            throw new NotFoundException("Employee", id);
        }
        return employee;
    }

    /**
     * Creates a new employee. Validates required fields, email uniqueness,
     * and that role/branch exist. Hashes the password before persisting.
     */
    public boolean createEmployee(Employee employee, String password) {
        if (employee == null) {
            throw new ValidationException("Employee must not be null");
        }
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password is required");
        }

        if (employeeDAO.existsByEmail(employee.getEmail(), null)) {
            throw new ValidationException("Email already exists: " + employee.getEmail());
        }

        Role role = roleDAO.getById(employee.getRoleId());
        if (role == null) {
            throw new ValidationException("Invalid role: " + employee.getRoleId());
        }

        Branch branch = branchDAO.getById(employee.getBranchId());
        if (branch == null) {
            throw new ValidationException("Invalid branch: " + employee.getBranchId());
        }

        employee.setPasswordHash(PasswordUtil.hash(password));

        if (employee.getStatus() == null || employee.getStatus().trim().isEmpty()) {
            employee.setStatus(AppConstants.STATUS_ACTIVE);
        }

        try {
            return employeeDAO.insert(employee);
        } catch (Exception e) {
            throw new ServiceException("Failed to create employee", e);
        }
    }

    /**
     * Updates an existing employee. Validates required fields and email
     * uniqueness (excluding the current employee).
     */
    public boolean updateEmployee(Employee employee) {
        if (employee == null) {
            throw new ValidationException("Employee must not be null");
        }
        if (employee.getEmployeeId() <= 0) {
            throw new ValidationException("Employee ID is required");
        }
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }

        if (employeeDAO.existsByEmail(employee.getEmail(), employee.getEmployeeId())) {
            throw new ValidationException("Email already exists: " + employee.getEmail());
        }

        try {
            return employeeDAO.update(employee);
        } catch (Exception e) {
            throw new ServiceException("Failed to update employee", e);
        }
    }

    /**
     * Soft-deletes an employee by setting their status to inactive.
     */
    public boolean deleteEmployee(int employeeId) {
        if (employeeId <= 0) {
            throw new ValidationException("Invalid employee ID");
        }
        try {
            return employeeDAO.softDelete(employeeId);
        } catch (Exception e) {
            throw new ServiceException("Failed to delete employee", e);
        }
    }

    /**
     * Returns all roles available for assignment.
     */
    public List<Role> getAllRoles() {
        return roleDAO.getAll();
    }

    /**
     * Returns all active branches available for assignment.
     */
    public List<Branch> getActiveBranches() {
        return branchDAO.getActive();
    }
}
