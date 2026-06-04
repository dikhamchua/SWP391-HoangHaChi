package com.kiotretail.product.dao;

import com.kiotretail.product.model.ActivityProduct;
import com.kiotretail.shared.base.BaseDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActivityProductDAO extends BaseDAO {

    public boolean insert(ActivityProduct activity) {
        String sql = "INSERT INTO ActivityProduct (FK_ID, Type, CreatedBy, Description) VALUES (?, ?, ?, ?)";
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
            if (affected == 0) return false;
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) activity.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ActivityProduct> getByFkId(int fkId) {
        List<ActivityProduct> activities = new ArrayList<>();
        String sql = "SELECT ID, FK_ID, Type, CreatedBy, Description FROM ActivityProduct WHERE FK_ID = ? ORDER BY ID DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fkId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(extractActivity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }

    private ActivityProduct extractActivity(ResultSet rs) throws SQLException {
        ActivityProduct activity = new ActivityProduct();
        activity.setId(rs.getInt("ID"));
        activity.setFkId(rs.getInt("FK_ID"));
        activity.setType(rs.getString("Type"));
        int createdBy = rs.getInt("CreatedBy");
        activity.setCreatedBy(rs.wasNull() ? null : createdBy);
        activity.setDescription(rs.getString("Description"));
        return activity;
    }
}
