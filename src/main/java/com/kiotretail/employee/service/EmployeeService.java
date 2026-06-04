package com.kiotretail.employee.service;

import com.kiotretail.employee.dao.ActivityEmployeeDAO;
import com.kiotretail.employee.dao.BranchDAO;
import com.kiotretail.employee.dao.EmployeeDAO;
import com.kiotretail.employee.dao.RoleDAO;
import com.kiotretail.employee.dto.EmployeeFilterDTO;
import com.kiotretail.employee.model.ActivityEmployee;
import com.kiotretail.employee.model.Branch;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.model.Role;
import com.kiotretail.shared.base.BaseService;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.util.PasswordUtil;

import java.util.List;

/**
 * Employee service for admin CRUD operations (not authentication).
 * Handles validation, password hashing, reference checks, and activity history.
 */
public class EmployeeService extends BaseService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ActivityEmployeeDAO activityEmployeeDAO = new ActivityEmployeeDAO();
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
     * Returns a filtered, paginated list of employees.
     */
    public PageResult<Employee> listEmployees(EmployeeFilterDTO filter, Pagination pagination) {
        if (pagination == null) {
            throw new IllegalArgumentException("pagination must not be null");
        }
        if (filter == null) {
            filter = new EmployeeFilterDTO();
        }
        List<Employee> items = employeeDAO.getEmployees(filter, pagination);
        int total = employeeDAO.countEmployees(filter);
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
        return createEmployee(employee, password, null);
    }

    public boolean createEmployee(Employee employee, String password, Integer createdBy) {
        validateEmployee(employee, false);
        if (password == null || password.isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Mật khẩu"));
        }
        if (employeeDAO.existsByEmail(employee.getEmail(), null)) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Email"));
        }

        employee.setPasswordHash(PasswordUtil.hash(password));
        if (employee.getStatus() == null || employee.getStatus().trim().isEmpty()) {
            employee.setStatus(AppConstants.STATUS_ACTIVE);
        }

        boolean created = employeeDAO.insert(employee);
        if (created) {
            recordActivity(employee.getEmployeeId(), AppConstants.ACTION_ADD, createdBy,
                    "Thêm nhân viên: " + employee.getFullName());
        }
        return created;
    }

    /**
     * Updates an existing employee. Validates required fields and email
     * uniqueness (excluding the current employee).
     */
    public boolean updateEmployee(Employee employee) {
        return updateEmployee(employee, null);
    }

    public boolean updateEmployee(Employee employee, Integer createdBy) {
        validateEmployee(employee, true);
        if (employeeDAO.existsByEmail(employee.getEmail(), employee.getEmployeeId())) {
            throw new ValidationException(String.format(ErrorMessages.ALREADY_EXISTS, "Email"));
        }

        boolean updated = employeeDAO.update(employee);
        if (updated) {
            recordActivity(employee.getEmployeeId(), AppConstants.ACTION_UPDATE, createdBy,
                    "Cập nhật thông tin nhân viên: " + employee.getFullName());
        }
        return updated;
    }

    /**
     * Soft-deletes an employee by setting their status to inactive.
     */
    public boolean deleteEmployee(int employeeId) {
        return deleteEmployee(employeeId, null);
    }

    public boolean deleteEmployee(int employeeId, Integer createdBy) {
        if (employeeId <= 0) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Mã nhân viên"));
        }

        Employee existing = getEmployeeById(employeeId);
        boolean deleted = employeeDAO.softDelete(employeeId);
        if (deleted) {
            recordActivity(employeeId, AppConstants.ACTION_DELETE, createdBy,
                    "Xóa nhân viên: " + existing.getFullName());
        }
        return deleted;
    }

    public List<ActivityEmployee> getActivitiesByEmployeeId(int employeeId) {
        return activityEmployeeDAO.getByFkId(employeeId);
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

    private void validateEmployee(Employee employee, boolean requireId) {
        if (employee == null) {
            throw new ValidationException(String.format(ErrorMessages.NOT_FOUND, "Nhân viên"));
        }
        if (requireId && employee.getEmployeeId() <= 0) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Mã nhân viên"));
        }
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Họ tên"));
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Email"));
        }
        if (employee.getStatus() != null && !employee.getStatus().trim().isEmpty()
                && !AppConstants.STATUS_ACTIVE.equals(employee.getStatus())
                && !AppConstants.STATUS_INACTIVE.equals(employee.getStatus())) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Trạng thái"));
        }

        Role role = roleDAO.getById(employee.getRoleId());
        if (role == null) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Vai trò"));
        }

        Branch branch = branchDAO.getById(employee.getBranchId());
        if (branch == null) {
            throw new ValidationException(String.format(ErrorMessages.INVALID_VALUE, "Chi nhánh"));
        }
    }

    private void recordActivity(int fkId, String type, Integer createdBy, String description) {
        if (fkId <= 0) {
            return;
        }
        ActivityEmployee activity = new ActivityEmployee();
        activity.setFkId(fkId);
        activity.setType(type);
        activity.setCreatedBy(createdBy);
        activity.setDescription(description);
        activityEmployeeDAO.insert(activity);
    }
}
