package com.kiotretail.dao;

import com.kiotretail.model.Employee;
import com.kiotretail.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee DAO
 * Data Access Object cho nhân viên
 */
public class EmployeeDAO {

    /**
     * Đăng nhập
     */
    public Employee login(String username, String password) {
        String sql = "SELECT e.EmployeeID, e.RoleID, e.BranchID, e.FullName, e.Email, e.Phone, " +
                     "e.PasswordHash, e.Status, e.CreatedAt, r.Name AS RoleName, b.Name AS BranchName " +
                     "FROM Employee e " +
                     "LEFT JOIN Role r ON e.RoleID = r.RoleID " +
                     "LEFT JOIN Branch b ON e.BranchID = b.BranchID " +
                     "WHERE e.Email = ? AND e.PasswordHash = ? AND e.Status = 'active'";

        System.out.println("[LOGIN DEBUG] Attempting login with email=[" + username + "] password=[" + password + "]");

        try (Connection conn = DatabaseUtil.getConnection()) {
            System.out.println("[LOGIN DEBUG] Database connection OK");
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                System.out.println("[LOGIN DEBUG] Executing query...");
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("[LOGIN DEBUG] Found employee: " + rs.getString("FullName") + " Role: " + rs.getString("RoleName"));
                        return extractEmployee(rs);
                    } else {
                        System.out.println("[LOGIN DEBUG] No matching row found. Check email/password/status.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[LOGIN DEBUG] SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tất cả nhân viên
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, r.role_name, b.branch_name " +
                     "FROM employees e " +
                     "LEFT JOIN roles r ON e.role_id = r.role_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "ORDER BY e.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(extractEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Lấy nhân viên theo ID
     */
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT e.*, r.role_name, b.branch_name " +
                     "FROM employees e " +
                     "LEFT JOIN roles r ON e.role_id = r.role_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm nhân viên mới
     */
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employee_code, full_name, email, phone, username, password, " +
                     "role_id, branch_id, department, position, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmployeeCode());
            stmt.setString(2, employee.getFullName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
            stmt.setString(5, employee.getUsername());
            stmt.setString(6, employee.getPassword());
            stmt.setInt(7, employee.getRoleId());
            stmt.setInt(8, employee.getBranchId());
            stmt.setString(9, employee.getDepartment());
            stmt.setString(10, employee.getPosition());
            stmt.setString(11, employee.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật nhân viên
     */
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ?, " +
                     "role_id = ?, branch_id = ?, department = ?, position = ?, status = ? " +
                     "WHERE employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getPhone());
            stmt.setInt(4, employee.getRoleId());
            stmt.setInt(5, employee.getBranchId());
            stmt.setString(6, employee.getDepartment());
            stmt.setString(7, employee.getPosition());
            stmt.setString(8, employee.getStatus());
            stmt.setInt(9, employee.getEmployeeId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa nhân viên
     */
    public boolean deleteEmployee(int employeeId) {
        String sql = "UPDATE employees SET status = 'inactive' WHERE employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extract Employee từ ResultSet
     */
    private Employee extractEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        employee.setEmployeeCode("EMP" + rs.getInt("EmployeeID"));
        employee.setFullName(rs.getString("FullName"));
        employee.setEmail(rs.getString("Email"));
        employee.setPhone(rs.getString("Phone"));
        employee.setUsername(rs.getString("Email"));
        employee.setPassword(rs.getString("PasswordHash"));
        employee.setRoleId(rs.getInt("RoleID"));
        employee.setRoleName(rs.getString("RoleName"));
        employee.setBranchId(rs.getInt("BranchID"));
        employee.setBranchName(rs.getString("BranchName"));
        employee.setStatus(rs.getString("Status"));
        employee.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return employee;
    }
    
    /**
     * Kiểm tra xem Username đã tồn tại trong hệ thống chưa
     */
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT employee_id FROM employees WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Nếu tìm thấy bản ghi nghĩa là Username đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra xem Email đã được sử dụng chưa
     */
    public boolean checkEmailExists(String email) {
        String sql = "SELECT employee_id FROM employees WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Nếu tìm thấy bản ghi nghĩa là Email đã được dùng
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thực hiện thêm tài khoản người dùng mới vào hệ thống Database
     */
    public boolean registerEmployee(String fullName, String username, String email, String password, int roleId, String status) {
        // Đã bổ sung trường branch_id để đồng bộ cấu trúc bảng employees của bạn
        String sql = "INSERT INTO employees (employee_code, full_name, email, username, password, role_id, branch_id, status, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
                   
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Tạo mã định danh nhân viên tự động ngẫu nhiên
            String randomEmpCode = "EMP" + (System.currentTimeMillis() % 100000);
            
            // Đảm bảo số 1 này tồn tại sẵn trong bảng 'branches' của bạn
            int defaultBranchId = 1; 
            
            stmt.setString(1, randomEmpCode);
            stmt.setString(2, fullName);
            stmt.setString(3, email);
            stmt.setString(4, username);
            stmt.setString(5, password); 
            stmt.setInt(6, roleId);
            stmt.setInt(7, defaultBranchId);
            stmt.setString(8, status);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert tài khoản đăng ký mới:");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật mật khẩu mới theo Email người dùng
     */
    public boolean updatePasswordByEmail(String email, String password) {
        String sql = "UPDATE employees SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, password); // Ghi đè mật khẩu mới không mã hóa
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

public boolean checkUsernameAndEmailMatch(String username, String email) {
        String sql = "SELECT employee_id FROM employees WHERE username = ? AND email = ? AND status = 'active'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
