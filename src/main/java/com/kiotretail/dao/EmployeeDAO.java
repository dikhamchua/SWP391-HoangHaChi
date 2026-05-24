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
        String sql = "SELECT e.*, r.role_name, b.branch_name " +
                     "FROM employees e " +
                     "LEFT JOIN roles r ON e.role_id = r.role_id " +
                     "LEFT JOIN branches b ON e.branch_id = b.branch_id " +
                     "WHERE e.username = ? AND e.password = ? AND e.status = 'active'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

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
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setEmployeeCode(rs.getString("employee_code"));
        employee.setFullName(rs.getString("full_name"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setUsername(rs.getString("username"));
        employee.setRoleId(rs.getInt("role_id"));
        employee.setRoleName(rs.getString("role_name"));
        employee.setBranchId(rs.getInt("branch_id"));
        employee.setBranchName(rs.getString("branch_name"));
        employee.setDepartment(rs.getString("department"));
        employee.setPosition(rs.getString("position"));
        employee.setStatus(rs.getString("status"));
        employee.setCreatedAt(rs.getTimestamp("created_at"));
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
    public boolean registerEmployee(String fullName, String username, String email, String encryptedPassword, int roleId, String status) {
        // Ánh xạ chính xác theo thuộc tính bảng employees của bạn bao gồm:
        // full_name, username, email, password, role_id, status, created_at, và sinh mã employee_code tự động ngắn gọn
        String sql = "INSERT INTO employees (employee_code, full_name, email, username, password, role_id, status, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
                   
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Tạo tạm một mã định danh ngẫu nhiên ngắn cho nhân viên mới đăng ký tự do
            String randomEmpCode = "EMP" + (System.currentTimeMillis() % 100000);
            
            stmt.setString(1, randomEmpCode);
            stmt.setString(2, fullName);
            stmt.setString(3, email);
            stmt.setString(4, username);
            stmt.setString(5, encryptedPassword); // Mật khẩu đã được hash
            stmt.setInt(6, roleId);
            stmt.setString(7, status);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật mật khẩu mới theo Email người dùng
     */
    public boolean updatePasswordByEmail(String email, String encryptedPassword) {
        String sql = "UPDATE employees SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, encryptedPassword);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
