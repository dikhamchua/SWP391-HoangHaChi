package com.kiotretail.product.dao;

import com.kiotretail.product.model.Supplier;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;
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

    private static final String BASE_SELECT = "SELECT " + COLUMNS + " FROM Supplier ";

    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY Name";

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
        String sql = BASE_SELECT + "WHERE Status = ? ORDER BY Name";

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

    /**
     * Returns paginated suppliers filtered by keyword (matches Name/Phone/Email/Address).
     */
    public List<Supplier> search(String keyword, Pagination pagination) {
        List<Supplier> suppliers = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        if (hasKeyword) {
            sql.append("WHERE Name LIKE ? OR Phone LIKE ? OR Email LIKE ? OR Address LIKE ? ");
        }
        sql.append("ORDER BY SupplierID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(idx++, pattern);
                stmt.setString(idx++, pattern);
                stmt.setString(idx++, pattern);
                stmt.setString(idx++, pattern);
            }
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());
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

    /**
     * Counts suppliers matching keyword (or all when keyword empty).
     */
    public int countAll(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Supplier ");
        if (hasKeyword) {
            sql.append("WHERE Name LIKE ? OR Phone LIKE ? OR Email LIKE ? OR Address LIKE ?");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
                stmt.setString(3, pattern);
                stmt.setString(4, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAll() {
        return countAll(null);
    }

    public Supplier getById(int supplierId) {
        String sql = BASE_SELECT + "WHERE SupplierID = ?";

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
            stmt.setString(5, supplier.getStatus() == null ? AppConstants.STATUS_ACTIVE : supplier.getStatus());
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
            stmt.setString(5, supplier.getStatus() == null ? AppConstants.STATUS_ACTIVE : supplier.getStatus());
            stmt.setInt(6, supplier.getSupplierId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hard delete by primary key.
     */
    public boolean delete(int supplierId) {
        String sql = "DELETE FROM Supplier WHERE SupplierID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            return stmt.executeUpdate() == 1;
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

    /**
     * Checks whether a supplier with the given name exists.
     * When excludeId is non-null, that row is ignored (useful for updates).
     */
    public boolean existsByName(String name, Integer excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder("SELECT 1 FROM Supplier WHERE Name = ?");
        if (excludeId != null) {
            sql.append(" AND SupplierID <> ?");
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, name.trim());
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
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
