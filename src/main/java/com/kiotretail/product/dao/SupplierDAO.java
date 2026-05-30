package com.kiotretail.product.dao;

import com.kiotretail.product.model.Supplier;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.constant.AppConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Supplier DAO
 * Data Access Object for the Supplier table.
 * Schema: SupplierID, Name, Phone, Email, Address, Status, CreatedAt
 */
public class SupplierDAO extends BaseDAO {

    private static final String COLUMNS =
            "SupplierID, Name, Phone, Email, Address, Status, CreatedAt";

    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM Supplier ORDER BY Name";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                suppliers.add(extractSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public List<Supplier> getActive() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM Supplier WHERE Status = ? ORDER BY Name";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, AppConstants.STATUS_ACTIVE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(extractSupplier(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public Supplier getById(int supplierId) {
        String sql = "SELECT " + COLUMNS + " FROM Supplier WHERE SupplierID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractSupplier(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Supplier supplier) {
        String sql = "INSERT INTO Supplier (Name, Phone, Email, Address, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhone());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Supplier supplier) {
        String sql = "UPDATE Supplier SET Name = ?, Phone = ?, Email = ?, Address = ?, Status = ? WHERE SupplierID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhone());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getStatus());
            stmt.setInt(6, supplier.getSupplierId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean softDelete(int supplierId) {
        String sql = "UPDATE Supplier SET Status = ? WHERE SupplierID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, AppConstants.STATUS_INACTIVE);
            stmt.setInt(2, supplierId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Supplier extractSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("SupplierID"));
        supplier.setName(rs.getString("Name"));
        supplier.setPhone(rs.getString("Phone"));
        supplier.setEmail(rs.getString("Email"));
        supplier.setAddress(rs.getString("Address"));
        supplier.setStatus(rs.getString("Status"));
        supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return supplier;
    }
}
