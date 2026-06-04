package com.kiotretail.employee.dao;

import com.kiotretail.employee.model.ActivityBranch;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActivityBranchDAO extends BaseDAO {

    public boolean insert(ActivityBranch activity) {
        String sql = "INSERT INTO ActivityBranch (FK_ID, Type, CreatedBy, Description) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, activity.getFkId());
            stmt.setString(2, activity.getType());
            if (activity.getCreatedBy() == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, activity.getCreatedBy());
            }
            stmt.setString(4, activity.getDescription());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                return false;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    activity.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<ActivityBranch> getByFkId(int fkId) {
        List<ActivityBranch> activities = new ArrayList<>();
        String sql = "SELECT ID, FK_ID, Type, CreatedBy, Description FROM ActivityBranch WHERE FK_ID = ? ORDER BY ID DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fkId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(extractActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return activities;
    }

    private ActivityBranch extractActivity(ResultSet rs) throws SQLException {
        ActivityBranch activity = new ActivityBranch();
        activity.setId(rs.getInt("ID"));
        activity.setFkId(rs.getInt("FK_ID"));
        activity.setType(rs.getString("Type"));
        int createdBy = rs.getInt("CreatedBy");
        activity.setCreatedBy(rs.wasNull() ? null : createdBy);
        activity.setDescription(rs.getString("Description"));
        return activity;
    }
}