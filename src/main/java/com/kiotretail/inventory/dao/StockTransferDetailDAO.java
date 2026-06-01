package com.kiotretail.inventory.dao;

import com.kiotretail.inventory.model.StockTransferDetail;
import com.kiotretail.shared.base.BaseDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for StockTransferDetail rows (line items of a stock transfer).
 *
 * <p>Schema columns: TransferDetailID, TransferID, ProductID, Quantity,
 * ReceivedQuantity, Note.</p>
 *
 * <p>Reads join Product so callers can render product name + SKU without
 * an extra round trip.</p>
 */
public class StockTransferDetailDAO extends BaseDAO {

    private static final String SELECT_WITH_PRODUCT =
            "SELECT std.TransferDetailID, std.TransferID, std.ProductID, " +
            "std.Quantity, std.ReceivedQuantity, std.Note, " +
            "p.Name AS ProductName, p.SKU AS ProductSKU " +
            "FROM StockTransferDetail std " +
            "JOIN Product p ON std.ProductID = p.ProductID ";

    public List<StockTransferDetail> getByTransferId(int transferId) {
        List<StockTransferDetail> list = new ArrayList<>();
        String sql = SELECT_WITH_PRODUCT +
                "WHERE std.TransferID = ? ORDER BY std.TransferDetailID ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transferId);
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

    public boolean insert(StockTransferDetail line) {
        String sql = "INSERT INTO StockTransferDetail " +
                     "(TransferID, ProductID, Quantity, ReceivedQuantity, Note) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, line.getTransferId());
            stmt.setInt(2, line.getProductId());
            stmt.setInt(3, line.getQuantity());
            stmt.setInt(4, line.getReceivedQuantity());
            stmt.setString(5, line.getNote());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int deleteByTransferId(int transferId) {
        String sql = "DELETE FROM StockTransferDetail WHERE TransferID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transferId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateReceivedQuantity(int detailId, int qty) {
        String sql = "UPDATE StockTransferDetail SET ReceivedQuantity = ? " +
                     "WHERE TransferDetailID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, qty);
            stmt.setInt(2, detailId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private StockTransferDetail extract(ResultSet rs) throws SQLException {
        StockTransferDetail d = new StockTransferDetail();
        d.setTransferDetailId(rs.getInt("TransferDetailID"));
        d.setTransferId(rs.getInt("TransferID"));
        d.setProductId(rs.getInt("ProductID"));
        d.setQuantity(rs.getInt("Quantity"));
        d.setReceivedQuantity(rs.getInt("ReceivedQuantity"));
        d.setNote(rs.getString("Note"));
        d.setProductName(rs.getString("ProductName"));
        d.setProductSku(rs.getString("ProductSKU"));
        return d;
    }
}
