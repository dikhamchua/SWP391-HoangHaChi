package com.kiotretail.report.dao;

import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.constant.AppConstants;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO extends BaseDAO {

    public BigDecimal getTotalRevenue(String dateFrom, String dateTo, Integer branchId) {
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(TotalAmount) AS total FROM Orders WHERE Status = ?");
        List<Object> params = new ArrayList<>();
        params.add(AppConstants.STATUS_COMPLETED);
        appendDateRange(sql, params, "CreatedAt", dateFrom, dateTo);
        if (branchId != null) {
            sql.append(" AND BranchID = ?");
            params.add(branchId);
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public int getTotalOrders(String dateFrom, String dateTo, Integer branchId) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS cnt FROM Orders WHERE Status = ?");
        List<Object> params = new ArrayList<>();
        params.add(AppConstants.STATUS_COMPLETED);
        appendDateRange(sql, params, "CreatedAt", dateFrom, dateTo);
        if (branchId != null) {
            sql.append(" AND BranchID = ?");
            params.add(branchId);
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalCustomers() {
        String sql = "SELECT COUNT(*) AS cnt FROM Customer";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalProducts() {
        String sql = "SELECT COUNT(*) AS cnt FROM Product WHERE Status = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AppConstants.STATUS_ACTIVE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, Object>> getTopProducts(int limit, String dateFrom, String dateTo) {
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT TOP(?) p.Name AS name, SUM(od.Quantity) AS totalQty, "
                        + "SUM(od.Subtotal) AS totalRevenue "
                        + "FROM OrderDetail od "
                        + "JOIN Product p ON od.ProductID = p.ProductID "
                        + "JOIN Orders o ON od.OrderID = o.OrderID "
                        + "WHERE o.Status = ?");
        List<Object> params = new ArrayList<>();
        params.add(limit);
        params.add(AppConstants.STATUS_COMPLETED);
        appendDateRange(sql, params, "o.CreatedAt", dateFrom, dateTo);
        sql.append(" GROUP BY p.Name ORDER BY totalRevenue DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", rs.getString("name"));
                    row.put("totalQty", rs.getInt("totalQty"));
                    row.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Map<String, Object>> getRevenueByDate(String dateFrom, String dateTo, Integer branchId) {
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT CAST(CreatedAt AS DATE) AS saleDate, "
                        + "SUM(TotalAmount) AS revenue, COUNT(*) AS orderCount "
                        + "FROM Orders WHERE Status = ?");
        List<Object> params = new ArrayList<>();
        params.add(AppConstants.STATUS_COMPLETED);
        appendDateRange(sql, params, "CreatedAt", dateFrom, dateTo);
        if (branchId != null) {
            sql.append(" AND BranchID = ?");
            params.add(branchId);
        }
        sql.append(" GROUP BY CAST(CreatedAt AS DATE) ORDER BY saleDate");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("saleDate", rs.getDate("saleDate"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    row.put("orderCount", rs.getInt("orderCount"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void appendDateRange(StringBuilder sql, List<Object> params,
                                 String column, String dateFrom, String dateTo) {
        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append(" AND ").append(column).append(" >= ?");
            params.add(dateFrom);
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            sql.append(" AND ").append(column).append(" <= ?");
            params.add(dateTo);
        }
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
