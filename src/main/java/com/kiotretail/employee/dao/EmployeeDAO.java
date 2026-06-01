package com.kiotretail.employee.dao;

import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.employee.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.kiotretail.shared.exception.ServiceException;

/**
 * DAO for Employee entity. Provides CRUD operations with role/branch joins.
 */
public class EmployeeDAO extends BaseDAO {

    private static final String SELECT_BASE =
            "SELECT e.EmployeeID, e.RoleID, e.BranchID, e.FullName, e.Email, e.Phone, " +
            "       e.PasswordHash, e.Status, e.CreatedAt, " +
            "       r.Name AS RoleName, b.Name AS BranchName " +
            "FROM Employee e " +
            "JOIN Role r ON e.RoleID = r.RoleID " +
            "JOIN Branch b ON e.BranchID = b.BranchID ";

    /**
     * Returns a paginated list of employees with role and branch names joined.
     */
    public List<Employee> getAll(Pagination pagination) {
        List<Employee> employees = new ArrayList<>();
        String sql = SELECT_BASE +
                "ORDER BY e.EmployeeID " +
                "LIMIT ?, ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pagination.getOffset());
            ps.setInt(2, pagination.getSize());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(extractEmployee(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return employees;
    }

    /**
     * Returns the total number of employees.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Employee";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Loads a single employee by ID with joined role/branch names.
     */
    public Employee getById(int employeeId) {
        String sql = SELECT_BASE + "WHERE e.EmployeeID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Loads an employee by email (used during login). Includes PasswordHash.
     */
    public Employee getByEmail(String email) {
        String sql = SELECT_BASE + "WHERE e.Email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Inserts a new employee. Returns true on success.
     */
    public boolean insert(Employee employee) {
        String sql = "INSERT INTO Employee (RoleID, BranchID, FullName, Email, Phone, PasswordHash, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, employee.getRoleId());
            ps.setInt(2, employee.getBranchId());
            ps.setString(3, employee.getFullName());
            ps.setString(4, employee.getEmail());
            ps.setString(5, employee.getPhone());
            ps.setString(6, employee.getPasswordHash());
            ps.setString(7, employee.getStatus());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    employee.setEmployeeId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates editable fields on an existing employee.
     */
    public boolean update(Employee employee) {
        String sql = "UPDATE Employee SET FullName = ?, Email = ?, Phone = ?, " +
                "RoleID = ?, BranchID = ?, Status = ? WHERE EmployeeID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getPhone());
            ps.setInt(4, employee.getRoleId());
            ps.setInt(5, employee.getBranchId());
            ps.setString(6, employee.getStatus());
            ps.setInt(7, employee.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates only the password hash for the given employee.
     */
    public boolean updatePassword(int employeeId, String newPasswordHash) {
        String sql = "UPDATE Employee SET PasswordHash = ? WHERE EmployeeID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public boolean softDelete(int employeeId) {
        String sql = "UPDATE Employee SET Status = ? WHERE EmployeeID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AppConstants.STATUS_INACTIVE);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Checks whether the given email already exists, optionally excluding a specific employee ID.
     */
    public boolean existsByEmail(String email, Integer excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Employee WHERE Email = ?");
        if (excludeId != null) {
            sql.append(" AND EmployeeID <> ?");
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, email);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return false;
    }

    private Employee extractEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        employee.setRoleId(rs.getInt("RoleID"));
        employee.setBranchId(rs.getInt("BranchID"));
        employee.setFullName(rs.getString("FullName"));
        employee.setEmail(rs.getString("Email"));
        employee.setPhone(rs.getString("Phone"));
        employee.setPasswordHash(rs.getString("PasswordHash"));
        employee.setStatus(rs.getString("Status"));
        employee.setCreatedAt(rs.getTimestamp("CreatedAt"));
        employee.setRoleName(rs.getString("RoleName"));
        employee.setBranchName(rs.getString("BranchName"));
        return employee;
    }
}
