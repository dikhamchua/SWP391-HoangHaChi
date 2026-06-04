package com.kiotretail.employee.dao;

import com.kiotretail.employee.model.ActivityEmployee;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActivityEmployeeDAO extends BaseDAO {
    public boolean insert(ActivityEmployee activity) {
        String sql = "INSERT INTO ActivityEmployee (FK_ID, Type, CreatedBy, Description) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, activity.getFkId());
            ps.setString(2, activity.getType());
            if (activity.getCreatedBy() == null) ps.setNull(3, java.sql.Types.INTEGER);
            else ps.setInt(3, activity.getCreatedBy());
            ps.setString(4, activity.getDescription());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) activity.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<ActivityEmployee> getByFkId(int fkId) {
        List<ActivityEmployee> activities = new ArrayList<>();
        String sql = "SELECT ID, FK_ID, Type, CreatedBy, Description FROM ActivityEmployee WHERE FK_ID = ? ORDER BY ID DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fkId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) activities.add(extractActivity(rs));
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return activities;
    }

    private ActivityEmployee extractActivity(ResultSet rs) throws SQLException {
        ActivityEmployee activity = new ActivityEmployee();
        activity.setId(rs.getInt("ID"));
        activity.setFkId(rs.getInt("FK_ID"));
        activity.setType(rs.getString("Type"));
        int createdBy = rs.getInt("CreatedBy");
        activity.setCreatedBy(rs.wasNull() ? null : createdBy);
        activity.setDescription(rs.getString("Description"));
        return activity;
    }
}
