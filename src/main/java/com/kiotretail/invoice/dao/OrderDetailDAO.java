package com.kiotretail.invoice.dao;

import com.kiotretail.invoice.model.OrderDetail;
import com.kiotretail.shared.base.BaseDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for OrderDetail rows belonging to an order.
 *
 * Table: OrderDetail (OrderDetailID INT PK IDENTITY, OrderID INT FK, ProductID INT FK,
 *                    Quantity INT, UnitPrice DECIMAL(18,2), Subtotal DECIMAL(18,2))
 *
 * Read paths join Product to surface the product Name and SKU for invoice rendering.
 * All statements use parameterized PreparedStatements.
 */
public class OrderDetailDAO extends BaseDAO {

    private static final String BASE_SELECT =
            "SELECT od.OrderDetailID, od.OrderID, od.ProductID, od.Quantity, " +
            "od.UnitPrice, od.Subtotal, p.Name AS ProductName, p.SKU AS ProductSKU " +
            "FROM OrderDetail od " +
            "INNER JOIN Product p ON od.ProductID = p.ProductID ";

    /**
     * Fetch all detail lines for an order, joined with the parent product so the
     * caller can render the product name and SKU without an additional round trip.
     */
    public List<OrderDetail> getByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE od.OrderID = ? ORDER BY od.OrderDetailID ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(extractDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    /**
     * Insert a batch of detail rows for the given order on a single connection.
     * Each detail is inserted via its own executeUpdate so callers using auto-commit
     * still see meaningful per-row results; pair with a service-level transaction
     * if all-or-nothing semantics are required.
     *
     * @return true when every row reports a single insertion
     */
    public boolean insertBatch(int orderId, List<OrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return true;
        }
        String sql = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice, Subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (OrderDetail detail : details) {
                if (detail == null) {
                    continue;
                }
                stmt.setInt(1, orderId);
                stmt.setInt(2, detail.getProductId());
                stmt.setInt(3, detail.getQuantity());
                stmt.setBigDecimal(4, detail.getUnitPrice() != null ? detail.getUnitPrice() : BigDecimal.ZERO);
                stmt.setBigDecimal(5, detail.getSubtotal() != null ? detail.getSubtotal() : BigDecimal.ZERO);
                if (stmt.executeUpdate() != 1) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove every detail line attached to an order. Useful when an order is
     * being rebuilt or cancelled.
     */
    public boolean deleteByOrderId(int orderId) {
        String sql = "DELETE FROM OrderDetail WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private OrderDetail extractDetail(ResultSet rs) throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setOrderDetailId(rs.getInt("OrderDetailID"));
        detail.setOrderId(rs.getInt("OrderID"));
        detail.setProductId(rs.getInt("ProductID"));
        detail.setQuantity(rs.getInt("Quantity"));
        detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        detail.setSubtotal(rs.getBigDecimal("Subtotal"));
        detail.setProductName(rs.getString("ProductName"));
        detail.setProductSku(rs.getString("ProductSKU"));
        return detail;
    }
}
