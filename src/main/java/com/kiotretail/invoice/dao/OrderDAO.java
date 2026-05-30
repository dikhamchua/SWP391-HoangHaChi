package com.kiotretail.invoice.dao;

import com.kiotretail.invoice.dto.InvoiceFilterDTO;
import com.kiotretail.invoice.model.Order;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Orders against the SQL Server schema.
 *
 * Table: Orders (OrderID INT PK IDENTITY, BranchID INT FK, EmployeeID INT FK,
 *                CustomerID INT FK NULL, SupplierID INT FK NULL, OrderCode VARCHAR,
 *                OrderType VARCHAR, Subtotal DECIMAL(18,2), DiscountAmount DECIMAL(18,2),
 *                TotalAmount DECIMAL(18,2), Status VARCHAR, CreatedAt DATETIME)
 *
 * All queries use parameterized PreparedStatements; no string concatenation of user input.
 */
public class OrderDAO extends BaseDAO {

    private static final String BASE_SELECT =
            "SELECT o.OrderID, o.BranchID, o.EmployeeID, o.CustomerID, o.SupplierID, " +
            "o.OrderCode, o.OrderType, o.Subtotal, o.DiscountAmount, o.TotalAmount, " +
            "o.Status, o.CreatedAt, " +
            "c.FullName AS CustomerName, e.FullName AS EmployeeName, b.Name AS BranchName " +
            "FROM Orders o " +
            "LEFT JOIN Customer c ON o.CustomerID = c.CustomerID " +
            "JOIN Employee e ON o.EmployeeID = e.EmployeeID " +
            "JOIN Branch b ON o.BranchID = b.BranchID ";

    /**
     * List orders with dynamic filter and pagination.
     */
    public List<Order> getOrders(InvoiceFilterDTO filter, Pagination pagination) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        appendFilterClauses(sql, params, filter);

        sql.append("ORDER BY o.CreatedAt DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Count orders matching the given filter (no pagination).
     */
    public int countOrders(InvoiceFilterDTO filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM Orders o " +
                "LEFT JOIN Customer c ON o.CustomerID = c.CustomerID " +
                "JOIN Employee e ON o.EmployeeID = e.EmployeeID " +
                "JOIN Branch b ON o.BranchID = b.BranchID " +
                "WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        appendFilterClauses(sql, params, filter);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            bindParams(stmt, params, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Fetch single order by primary key with joined names, or null if not found.
     */
    public Order getById(int orderId) {
        String sql = BASE_SELECT + "WHERE o.OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch single order by OrderCode (unique), or null if not found.
     */
    public Order getByCode(String orderCode) {
        if (orderCode == null || orderCode.trim().isEmpty()) {
            return null;
        }
        String sql = BASE_SELECT + "WHERE o.OrderCode = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderCode.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert a new order. CreatedAt is left to the database default.
     *
     * @return the generated OrderID, or -1 on failure
     */
    public int insert(Order order) {
        String sql = "INSERT INTO Orders (BranchID, EmployeeID, CustomerID, SupplierID, OrderCode, " +
                     "OrderType, Subtotal, DiscountAmount, TotalAmount, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getBranchId());
            stmt.setInt(2, order.getEmployeeId());
            if (order.getCustomerId() != null) {
                stmt.setInt(3, order.getCustomerId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            if (order.getSupplierId() != null) {
                stmt.setInt(4, order.getSupplierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, order.getOrderCode());
            stmt.setString(6, order.getOrderType());
            stmt.setBigDecimal(7, order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO);
            stmt.setBigDecimal(8, order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
            stmt.setBigDecimal(9, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            stmt.setString(10, order.getStatus());

            int affected = stmt.executeUpdate();
            if (affected == 1) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update only the Status of an existing order.
     */
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void appendFilterClauses(StringBuilder sql, List<Object> params, InvoiceFilterDTO filter) {
        if (filter == null) {
            return;
        }
        if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            sql.append("AND (o.OrderCode LIKE ? OR c.FullName LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getOrderType() != null && !filter.getOrderType().trim().isEmpty()) {
            sql.append("AND o.OrderType = ? ");
            params.add(filter.getOrderType().trim());
        }
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            sql.append("AND o.Status = ? ");
            params.add(filter.getStatus().trim());
        }
        if (filter.getEmployeeId() != null) {
            sql.append("AND o.EmployeeID = ? ");
            params.add(filter.getEmployeeId());
        }
        if (filter.getDateFrom() != null && !filter.getDateFrom().trim().isEmpty()) {
            sql.append("AND o.CreatedAt >= ? ");
            params.add(filter.getDateFrom().trim());
        }
        if (filter.getDateTo() != null && !filter.getDateTo().trim().isEmpty()) {
            sql.append("AND o.CreatedAt <= ? ");
            params.add(filter.getDateTo().trim());
        }
    }

    private int bindParams(PreparedStatement stmt, List<Object> params, int startIndex) throws SQLException {
        int idx = startIndex;
        for (Object value : params) {
            stmt.setObject(idx++, value);
        }
        return idx;
    }

    private Order extractOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("OrderID"));
        order.setBranchId(rs.getInt("BranchID"));
        order.setEmployeeId(rs.getInt("EmployeeID"));

        int customerId = rs.getInt("CustomerID");
        order.setCustomerId(rs.wasNull() ? null : customerId);

        int supplierId = rs.getInt("SupplierID");
        order.setSupplierId(rs.wasNull() ? null : supplierId);

        order.setOrderCode(rs.getString("OrderCode"));
        order.setOrderType(rs.getString("OrderType"));
        order.setSubtotal(rs.getBigDecimal("Subtotal"));
        order.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setCreatedAt(rs.getTimestamp("CreatedAt"));

        order.setCustomerName(rs.getString("CustomerName"));
        order.setEmployeeName(rs.getString("EmployeeName"));
        order.setBranchName(rs.getString("BranchName"));
        return order;
    }
}
