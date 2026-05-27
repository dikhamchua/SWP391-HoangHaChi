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
import util.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminOwnerDao extends DBContext {

    public List<Employee> getShopOwnersOnly() {
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
            WHERE r.Name = 'Owner'
            ORDER BY e.EmployeeID DESC
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapEmployee(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Employee getOwnerByName(String fullName) {
        String sql = """
            SELECT 
                e.EmployeeID, e.RoleID, e.BranchID,
                e.FullName, e.Email, e.Phone, e.Status,
                r.Name AS RoleName,
                b.Name AS BranchName
            FROM Employee e
            JOIN Role r ON e.RoleID = r.RoleID
            JOIN Branch b ON e.BranchID = b.BranchID
            WHERE e.FullName = ? AND r.Name = 'Owner'
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, fullName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapEmployee(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getOwnerRoleId() {
        String sql = "SELECT RoleID FROM Role WHERE Name = 'Owner'";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("RoleID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean addOwner(Employee e) {
        String sql = """
            INSERT INTO Employee
            (RoleID, BranchID, FullName, Email, Phone, PasswordHash, Status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, getOwnerRoleId());
            ps.setInt(2, e.getBranchID());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getPasswordHash());
            ps.setString(7, "active");

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean updateOwner(Employee e) {
        String sql = """
            UPDATE Employee
            SET BranchID = ?, FullName = ?, Email = ?, Phone = ?, Status = ?
            WHERE EmployeeID = ?
              AND RoleID = (SELECT RoleID FROM Role WHERE Name = 'Owner')
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, e.getBranchID());
            ps.setString(2, e.getFullName());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getStatus());
            ps.setInt(6, e.getEmployeeID());

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean updateOwnerStatus(int employeeID, String status) {
        String sql = """
            UPDATE Employee
            SET Status = ?
            WHERE EmployeeID = ?
              AND RoleID = (SELECT RoleID FROM Role WHERE Name = 'Owner')
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