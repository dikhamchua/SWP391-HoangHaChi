package com.kiotretail.product.dao;

import com.kiotretail.product.model.ActivitySupplier;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActivitySupplierDAO extends BaseDAO {

    public boolean insert(ActivitySupplier activity) {
        String sql = "INSERT INTO ActivitySupplier (FK_ID, Type, CreatedBy, Description) VALUES (?, ?, ?, ?)";
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
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<ActivitySupplier> getByFkId(int fkId) {
        List<ActivitySupplier> activities = new ArrayList<>();
        String sql = "SELECT ID, FK_ID, Type, CreatedBy, Description FROM ActivitySupplier WHERE FK_ID = ? ORDER BY ID DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fkId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(extract(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return activities;
    }

    private ActivitySupplier extract(ResultSet rs) throws SQLException {
        ActivitySupplier a = new ActivitySupplier();
        a.setId(rs.getInt("ID"));
        a.setFkId(rs.getInt("FK_ID"));
        a.setType(rs.getString("Type"));
        int createdBy = rs.getInt("CreatedBy");
        a.setCreatedBy(rs.wasNull() ? null : createdBy);
        a.setDescription(rs.getString("Description"));
        return a;
    }
}
