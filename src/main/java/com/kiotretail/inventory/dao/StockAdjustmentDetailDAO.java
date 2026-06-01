package com.kiotretail.inventory.dao;

import com.kiotretail.inventory.model.StockAdjustmentDetail;
import com.kiotretail.shared.base.BaseDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for StockAdjustmentDetail rows (line items of a stock adjustment).
 *
 * <p>Schema columns: AdjustmentDetailID, AdjustmentID, ProductID,
 * SystemQuantity, ActualQuantity, Variance, VarianceValue, Reason.</p>
 *
 * <p>Reads join Product so callers can render product name + SKU without
 * an extra round trip.</p>
 */
public class StockAdjustmentDetailDAO extends BaseDAO {

    private static final String SELECT_WITH_PRODUCT =
            "SELECT sad.AdjustmentDetailID, sad.AdjustmentID, sad.ProductID, " +
            "sad.SystemQuantity, sad.ActualQuantity, sad.Variance, " +
            "sad.VarianceValue, sad.Reason, " +
            "p.Name AS ProductName, p.SKU AS ProductSKU " +
            "FROM StockAdjustmentDetail sad " +
            "JOIN Product p ON sad.ProductID = p.ProductID ";

    public List<StockAdjustmentDetail> getByAdjustmentId(int adjustmentId) {
        List<StockAdjustmentDetail> list = new ArrayList<>();
        String sql = SELECT_WITH_PRODUCT +
                "WHERE sad.AdjustmentID = ? ORDER BY sad.AdjustmentDetailID ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adjustmentId);
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

    public boolean insert(StockAdjustmentDetail line) {
        String sql = "INSERT INTO StockAdjustmentDetail " +
                     "(AdjustmentID, ProductID, SystemQuantity, ActualQuantity, " +
                     "Variance, VarianceValue, Reason) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, line.getAdjustmentId());
            stmt.setInt(2, line.getProductId());
            stmt.setInt(3, line.getSystemQuantity());
            stmt.setInt(4, line.getActualQuantity());
            stmt.setInt(5, line.getVariance());
            stmt.setBigDecimal(6, line.getVarianceValue() != null
                    ? line.getVarianceValue() : BigDecimal.ZERO);
            stmt.setString(7, line.getReason());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int deleteByAdjustmentId(int adjustmentId) {
        String sql = "DELETE FROM StockAdjustmentDetail WHERE AdjustmentID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adjustmentId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private StockAdjustmentDetail extract(ResultSet rs) throws SQLException {
        StockAdjustmentDetail d = new StockAdjustmentDetail();
        d.setAdjustmentDetailId(rs.getInt("AdjustmentDetailID"));
        d.setAdjustmentId(rs.getInt("AdjustmentID"));
        d.setProductId(rs.getInt("ProductID"));
        d.setSystemQuantity(rs.getInt("SystemQuantity"));
        d.setActualQuantity(rs.getInt("ActualQuantity"));
        d.setVariance(rs.getInt("Variance"));
        d.setVarianceValue(rs.getBigDecimal("VarianceValue"));
        d.setReason(rs.getString("Reason"));
        d.setProductName(rs.getString("ProductName"));
        d.setProductSku(rs.getString("ProductSKU"));
        return d;
    }
}
