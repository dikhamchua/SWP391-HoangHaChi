package com.kiotretail.dao;

import com.kiotretail.model.Employee;
import com.kiotretail.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee DAO Data Access Object cho nhân viên
 */
public class EmployeeDAO {

    /**
     * Đăng nhập
     */
    public Employee login(String identifier, String password) {
        String sql = "SELECT e.*, r.Name as RoleName, b.Name as BranchName "
                + "FROM Employee e "
                + "JOIN Role r ON e.RoleID = r.RoleID "
                + "JOIN Branch b ON e.BranchID = b.BranchID "
                + "WHERE (e.Email = ? OR e.Phone = ?) AND e.PasswordHash = ? AND e.Status = 'active'";

        try (Connection conn = DatabaseUtil.getConnection(); // Hoặc cách lấy connection của bạn
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            // Tham số 1 truyền vào cho Email
            ps.setString(1, identifier);
            // Tham số 2 truyền vào cho Phone (cùng là chuỗi người dùng nhập từ giao diện)
            ps.setString(2, identifier);
            // Tham số 3 cho Password
            ps.setString(3, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                Employee employee = new Employee();

                employee.setEmployeeId(rs.getInt("EmployeeID"));
                employee.setFullName(rs.getString("FullName"));
                employee.setEmail(rs.getString("Email"));
                employee.setPhone(rs.getString("Phone"));
                employee.setRoleId(rs.getInt("RoleID"));
                employee.setRoleName(rs.getString("RoleName"));
                employee.setBranchId(rs.getInt("BranchID"));
                employee.setBranchName(rs.getString("BranchName"));
                employee.setStatus(rs.getString("Status"));

                return employee;
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
        String sql = "SELECT e.*, r.role_name, b.branch_name "
                + "FROM employees e "
                + "LEFT JOIN roles r ON e.role_id = r.role_id "
                + "LEFT JOIN branches b ON e.branch_id = b.branch_id "
                + "ORDER BY e.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

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
        String sql = "SELECT e.*, r.role_name, b.branch_name "
                + "FROM employees e "
                + "LEFT JOIN roles r ON e.role_id = r.role_id "
                + "LEFT JOIN branches b ON e.branch_id = b.branch_id "
                + "WHERE e.employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "INSERT INTO employees (employee_code, full_name, email, phone, username, password, "
                + "role_id, branch_id, department, position, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmployeeCode());
            stmt.setString(2, employee.getFullName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
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
        String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ?, "
                + "role_id = ?, branch_id = ?, department = ?, position = ?, status = ? "
                + "WHERE employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
     * Cập nhật mật khẩu mới theo Email người dùng
     */
    public boolean updatePasswordByEmail(String email, String newPassword) {

    String sql = "UPDATE Employee "
               + "SET PasswordHash = ? "
               + "WHERE Email = ?";

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, newPassword);
        ps.setString(2, email);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

    public boolean checkUsernameAndEmailMatch(String username, String email) {
        String sql = "SELECT employee_id FROM employees WHERE username = ? AND email = ? AND status = 'active'";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    public boolean updatePassword(int employeeId, String currentPassword, String newPassword) {
        String checkSql = "SELECT PasswordHash FROM Employee WHERE EmployeeID = ? AND Status = 'active'";
        String updateSql = "UPDATE Employee SET PasswordHash = ? WHERE EmployeeID = ?";

        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, employeeId);
            rs = psCheck.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("PasswordHash");
                if (storedPassword != null && storedPassword.trim().equals(currentPassword.trim())) {

                    psUpdate = conn.prepareStatement(updateSql);
                    psUpdate.setString(1, newPassword); // Mật khẩu mới đạt chuẩn 6 ký tự + ký tự đặc biệt
                    psUpdate.setInt(2, employeeId);

                    int rowsAffected = psUpdate.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psCheck != null) {
                    psCheck.close();
                }
                if (psUpdate != null) {
                    psUpdate.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Employee WHERE Email = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu số lượng tìm thấy lớn hơn 0 nghĩa là đã trùng email
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // Ghi nhận lỗi log ra màn hình console để dễ dàng debug khi chạy lỗi kết nối
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerEmployee(String fullName, String email, String phone, String autoGeneratedPassword, int roleId, int branchId, String defaultStatus) {
        // Câu lệnh SQL bám sát cấu trúc bảng Employee trong DBFinora.sql
        // Trường CreatedAt không cần chèn vì đã được định nghĩa tự động: DEFAULT GETDATE() trong Database
        String sql = "INSERT INTO Employee (RoleID, BranchID, FullName, Email, Phone, PasswordHash, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Gán các tham số đầu vào theo đúng thứ tự cột trong câu lệnh INSERT phía trên
            ps.setInt(1, roleId);
            ps.setInt(2, branchId);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, autoGeneratedPassword); // Lưu trực tiếp mật khẩu tự sinh dạng văn bản thô
            ps.setString(7, defaultStatus);

            // Thực thi lệnh cập nhật cơ sở dữ liệu
            int rowsAffected = ps.executeUpdate();

            // Nếu số dòng bị ảnh hưởng lớn hơn 0 nghĩa là insert thành công dữ liệu xuống SQL Server
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean checkFullNameAndEmailMatch(String fullName, String email) {

     String sql = "SELECT COUNT(*) FROM Employee "
               + "WHERE LOWER(LTRIM(RTRIM(FullName))) = LOWER(LTRIM(RTRIM(?))) "
               + "AND LOWER(LTRIM(RTRIM(Email))) = LOWER(LTRIM(RTRIM(?))) "
               + "AND Status = 'active'";

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, fullName);
        ps.setString(2, email);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

}
