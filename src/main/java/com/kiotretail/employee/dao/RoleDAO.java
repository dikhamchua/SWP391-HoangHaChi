package com.kiotretail.employee.dao;

import com.kiotretail.employee.model.Role;
import com.kiotretail.shared.base.BaseDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Role DAO
 * Data Access Object for the Role table.
 * Schema: RoleID (PK IDENTITY), Name, Description
 */
public class RoleDAO extends BaseDAO {

    private static final String BASE_SELECT =
            "SELECT RoleID, Name, Description FROM Role ";

    /**
     * Returns all roles ordered by RoleID ascending.
     */
    public List<Role> getAll() {
        List<Role> roles = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY RoleID ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                roles.add(extractRole(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    /**
     * Loads a single role by its primary key.
     */
    public Role getById(int roleId) {
        String sql = BASE_SELECT + "WHERE RoleID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractRole(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads a single role by its unique name.
     */
    public Role getByName(String name) {
        if (name == null) {
            return null;
        }
        String sql = BASE_SELECT + "WHERE Name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractRole(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Role extractRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setRoleId(rs.getInt("RoleID"));
        role.setName(rs.getString("Name"));
        role.setDescription(rs.getString("Description"));
        return role;
    }
}
