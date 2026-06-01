package com.kiotretail.purchase.dao;

import com.kiotretail.purchase.model.PurchaseOrderDetail;
import com.kiotretail.shared.base.BaseDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for PurchaseOrderDetail rows (line items of a purchase order).
 *
 * <p>Schema columns: PODetailID, PurchaseOrderID, ProductID, Quantity,
 * UnitCost, Subtotal, ReceivedQuantity (default 0).</p>
 *
 * <p>Reads join Product so callers can render product name + SKU without
 * an extra round trip.</p>
 */
public class PurchaseOrderDetailDAO extends BaseDAO {

    private static final String SELECT_WITH_PRODUCT =
            "SELECT pod.PODetailID, pod.PurchaseOrderID, pod.ProductID, " +
            "pod.Quantity, pod.UnitCost, pod.Subtotal, pod.ReceivedQuantity, " +
            "p.Name AS ProductName, p.SKU AS ProductSKU " +
            "FROM PurchaseOrderDetail pod " +
            "JOIN Product p ON pod.ProductID = p.ProductID ";

    public List<PurchaseOrderDetail> getByOrderId(int purchaseOrderId) {
        List<PurchaseOrderDetail> list = new ArrayList<>();
        String sql = SELECT_WITH_PRODUCT +
                "WHERE pod.PurchaseOrderID = ? ORDER BY pod.PODetailID ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extract(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(PurchaseOrderDetail line) {
        String sql = "INSERT INTO PurchaseOrderDetail " +
                     "(PurchaseOrderID, ProductID, Quantity, UnitCost, Subtotal, ReceivedQuantity) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, line.getPurchaseOrderId());
            stmt.setInt(2, line.getProductId());
            stmt.setInt(3, line.getQuantity());
            stmt.setBigDecimal(4, line.getUnitCost() != null ? line.getUnitCost() : BigDecimal.ZERO);
            stmt.setBigDecimal(5, line.getSubtotal() != null ? line.getSubtotal() : BigDecimal.ZERO);
            stmt.setInt(6, line.getReceivedQuantity());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int poDetailId) {
        return executeUpdate(
                "DELETE FROM PurchaseOrderDetail WHERE PODetailID = ?",
                poDetailId);
    }

    public int deleteByOrderId(int purchaseOrderId) {
        String sql = "DELETE FROM PurchaseOrderDetail WHERE PurchaseOrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrderId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateReceivedQuantity(int poDetailId, int receivedQty) {
        return executeUpdate(
                "UPDATE PurchaseOrderDetail SET ReceivedQuantity = ? WHERE PODetailID = ?",
                receivedQty, poDetailId);
    }

    /**
     * Sum of Subtotal across all detail rows of an order. Returns
     * {@link BigDecimal#ZERO} when the order has no lines.
     */
    public BigDecimal sumSubtotalByOrderId(int purchaseOrderId) {
        String sql = "SELECT IFNULL(SUM(Subtotal), 0) AS Total " +
                     "FROM PurchaseOrderDetail WHERE PurchaseOrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("Total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private boolean executeUpdate(String sql, Object... values) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PurchaseOrderDetail extract(ResultSet rs) throws SQLException {
        PurchaseOrderDetail d = new PurchaseOrderDetail();
        d.setPoDetailId(rs.getInt("PODetailID"));
        d.setPurchaseOrderId(rs.getInt("PurchaseOrderID"));
        d.setProductId(rs.getInt("ProductID"));
        d.setQuantity(rs.getInt("Quantity"));
        d.setUnitCost(rs.getBigDecimal("UnitCost"));
        d.setSubtotal(rs.getBigDecimal("Subtotal"));
        d.setReceivedQuantity(rs.getInt("ReceivedQuantity"));
        d.setProductName(rs.getString("ProductName"));
        d.setProductSku(rs.getString("ProductSKU"));
        return d;
    }
}
