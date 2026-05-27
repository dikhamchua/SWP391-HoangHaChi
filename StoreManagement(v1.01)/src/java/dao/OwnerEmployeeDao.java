/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author PCQN
 */
import model.Employee;
import model.Branch;
import model.Role;
import util.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerEmployeeDao extends DBContext {

    public List<Employee> getEmployees(String keyword, String roleFilter, String branchFilter, String statusFilter) {
        List<Employee> list = new ArrayList<>();

        String sql = """
            SELECT 
                e.EmployeeID, e.RoleID, e.BranchID,
                e.FullName, e.Email, e.Phone, e.Status,
                r.Name AS RoleName,
                b.Name AS BranchName
            FROM Employee e
            JOIN Role r ON e.RoleID = r.RoleID
            JOIN Branch b ON e.BranchID = b.BranchID
            WHERE r.Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
              AND (? IS NULL OR e.FullName LIKE ? OR e.Email LIKE ? OR e.Phone LIKE ?)
              AND (? IS NULL OR r.Name = ?)
              AND (? IS NULL OR e.BranchID = ?)
              AND (? IS NULL OR e.Status = ?)
            ORDER BY e.EmployeeID DESC
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            String searchValue = keyword == null || keyword.trim().isEmpty()
                    ? null
                    : "%" + keyword.trim() + "%";

            ps.setString(1, searchValue);
            ps.setString(2, searchValue);
            ps.setString(3, searchValue);
            ps.setString(4, searchValue);

            String roleValue = roleFilter == null || roleFilter.trim().isEmpty() ? null : roleFilter;
            ps.setString(5, roleValue);
            ps.setString(6, roleValue);

            if (branchFilter == null || branchFilter.trim().isEmpty()) {
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.INTEGER);
            } else {
                int branchID = Integer.parseInt(branchFilter);
                ps.setInt(7, branchID);
                ps.setInt(8, branchID);
            }

            String statusValue = statusFilter == null || statusFilter.trim().isEmpty() ? null : statusFilter;
            ps.setString(9, statusValue);
            ps.setString(10, statusValue);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapEmployee(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Employee getEmployeeById(int employeeID) {
        String sql = """
            SELECT 
                e.EmployeeID, e.RoleID, e.BranchID,
                e.FullName, e.Email, e.Phone, e.Status,
                r.Name AS RoleName,
                b.Name AS BranchName
            FROM Employee e
            JOIN Role r ON e.RoleID = r.RoleID
            JOIN Branch b ON e.BranchID = b.BranchID
            WHERE e.EmployeeID = ?
              AND r.Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapEmployee(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Role> getAllowedRoles() {
        List<Role> list = new ArrayList<>();

        String sql = """
            SELECT RoleID, Name, Description
            FROM Role
            WHERE Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
            ORDER BY RoleID
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Role r = new Role();
                r.setRoleID(rs.getInt("RoleID"));
                r.setName(rs.getString("Name"));
                r.setDescription(rs.getString("Description"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addEmployee(Employee e) {
        String sql = """
            INSERT INTO Employee
            (RoleID, BranchID, FullName, Email, Phone, PasswordHash, Status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, e.getRoleID());
            ps.setInt(2, e.getBranchID());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getPasswordHash());
            ps.setString(7, "active");

            return ps.executeUpdate() > 0;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return false;
    }

    public boolean updateEmployee(Employee e) {
        String sql = """
            UPDATE Employee
            SET RoleID = ?, BranchID = ?, FullName = ?, Email = ?, Phone = ?, Status = ?
            WHERE EmployeeID = ?
              AND RoleID IN (
                    SELECT RoleID FROM Role 
                    WHERE Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
              )
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, e.getRoleID());
            ps.setInt(2, e.getBranchID());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getStatus());
            ps.setInt(7, e.getEmployeeID());

            return ps.executeUpdate() > 0;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return false;
    }

    public boolean updateStatus(int employeeID, String status) {
        String sql = """
            UPDATE Employee
            SET Status = ?
            WHERE EmployeeID = ?
              AND RoleID IN (
                    SELECT RoleID FROM Role 
                    WHERE Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
              )
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, employeeID);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteEmployee(int employeeID) {
        String sql = """
            DELETE FROM Employee
            WHERE EmployeeID = ?
              AND RoleID IN (
                    SELECT RoleID FROM Role 
                    WHERE Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
              )
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeID);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean resetPassword(int employeeID, String newPassword) {
        String sql = """
            UPDATE Employee
            SET PasswordHash = ?
            WHERE EmployeeID = ?
              AND RoleID IN (
                    SELECT RoleID FROM Role 
                    WHERE Name IN ('StoreManager', 'SalesStaff', 'WarehouseStaff')
              )
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, employeeID);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Branch> getAllBranches() {
        List<Branch> list = new ArrayList<>();

        String sql = """
            SELECT BranchID, Name, Address, Phone, Status
            FROM Branch
            ORDER BY BranchID
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Branch b = new Branch();
                b.setBranchID(rs.getInt("BranchID"));
                b.setName(rs.getString("Name"));
                b.setAddress(rs.getString("Address"));
                b.setPhone(rs.getString("Phone"));
                b.setStatus(rs.getString("Status"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();

        e.setEmployeeID(rs.getInt("EmployeeID"));
        e.setRoleID(rs.getInt("RoleID"));
        e.setBranchID(rs.getInt("BranchID"));
        e.setFullName(rs.getString("FullName"));
        e.setEmail(rs.getString("Email"));
        e.setPhone(rs.getString("Phone"));
        e.setStatus(rs.getString("Status"));
        e.setRoleName(rs.getString("RoleName"));
        e.setBranchName(rs.getString("BranchName"));

        return e;
    }
}
