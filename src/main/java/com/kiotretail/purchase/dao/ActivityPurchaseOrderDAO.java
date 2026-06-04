package com.kiotretail.purchase.dao;

import com.kiotretail.purchase.model.ActivityPurchaseOrder;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActivityPurchaseOrderDAO extends BaseDAO {

    public boolean insert(ActivityPurchaseOrder activity) {
        String sql = "INSERT INTO ActivityPurchaseOrder (FK_ID, Type, CreatedBy, Description) VALUES (?, ?, ?, ?)";
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

    public List<ActivityPurchaseOrder> getByFkId(int fkId) {
        List<ActivityPurchaseOrder> activities = new ArrayList<>();
        String sql = "SELECT a.ID, a.FK_ID, a.Type, a.CreatedBy, a.Description, e.FullName AS CreatedByName " +
                     "FROM ActivityPurchaseOrder a " +
                     "LEFT JOIN Employee e ON a.CreatedBy = e.EmployeeID " +
                     "WHERE a.FK_ID = ? ORDER BY a.ID DESC";
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

    private ActivityPurchaseOrder extractActivity(ResultSet rs) throws SQLException {
        ActivityPurchaseOrder activity = new ActivityPurchaseOrder();
        activity.setId(rs.getInt("ID"));
        activity.setFkId(rs.getInt("FK_ID"));
        activity.setType(rs.getString("Type"));
        int createdBy = rs.getInt("CreatedBy");
        activity.setCreatedBy(rs.wasNull() ? null : createdBy);
        activity.setDescription(rs.getString("Description"));
        try {
            activity.setCreatedByName(rs.getString("CreatedByName"));
        } catch (SQLException ignored) {
            // Column may not exist in insert context
        }
        return activity;
    }
}
