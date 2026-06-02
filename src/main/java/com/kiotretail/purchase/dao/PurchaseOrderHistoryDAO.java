package com.kiotretail.purchase.dao;

import com.kiotretail.purchase.model.PurchaseOrderHistory;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderHistoryDAO extends BaseDAO {

    private static final String SELECT_WITH_USER =
            "SELECT h.HistoryID, h.PurchaseOrderID, h.FromStatus, h.ToStatus, " +
            "h.Action, h.PerformedBy, h.Reason, h.CreatedAt, " +
            "e.FullName AS PerformedByName " +
            "FROM PurchaseOrderHistory h " +
            "LEFT JOIN Employee e ON h.PerformedBy = e.EmployeeID ";

    public int insert(PurchaseOrderHistory history) {
        String sql = "INSERT INTO PurchaseOrderHistory " +
                "(PurchaseOrderID, FromStatus, ToStatus, Action, PerformedBy, Reason) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, history.getPurchaseOrderId());
            stmt.setString(2, history.getFromStatus());
            stmt.setString(3, history.getToStatus());
            stmt.setString(4, history.getAction());
            stmt.setInt(5, history.getPerformedBy());
            stmt.setString(6, history.getReason());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                return -1;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    history.setHistoryId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<PurchaseOrderHistory> getByOrderId(int purchaseOrderId) {
        List<PurchaseOrderHistory> list = new ArrayList<>();
        String sql = SELECT_WITH_USER +
                "WHERE h.PurchaseOrderID = ? " +
                "ORDER BY h.CreatedAt DESC, h.HistoryID DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extract(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    private PurchaseOrderHistory extract(ResultSet rs) throws SQLException {
        PurchaseOrderHistory h = new PurchaseOrderHistory();
        h.setHistoryId(rs.getInt("HistoryID"));
        h.setPurchaseOrderId(rs.getInt("PurchaseOrderID"));
        h.setFromStatus(rs.getString("FromStatus"));
        h.setToStatus(rs.getString("ToStatus"));
        h.setAction(rs.getString("Action"));
        h.setPerformedBy(rs.getInt("PerformedBy"));
        h.setReason(rs.getString("Reason"));
        h.setCreatedAt(rs.getTimestamp("CreatedAt"));
        h.setPerformedByName(rs.getString("PerformedByName"));
        return h;
    }
}