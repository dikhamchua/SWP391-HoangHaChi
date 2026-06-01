package com.kiotretail.employee.dao;

import com.kiotretail.employee.model.Branch;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.kiotretail.shared.exception.ServiceException;

/**
 * Branch DAO
 * Data Access Object for the Branch table.
 * Schema: BranchID (PK IDENTITY), Name, Address, Phone, Status, CreatedAt
 */
public class BranchDAO extends BaseDAO {

    private static final String BASE_SELECT =
            "SELECT BranchID, Name, Address, Phone, Status, CreatedAt FROM Branch ";

    /**
     * Returns all branches ordered by name ascending.
     */
    public List<Branch> getAll() {
        List<Branch> branches = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY Name ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                branches.add(extractBranch(rs));
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return branches;
    }

    /**
     * Returns paginated list of branches ordered by BranchID DESC.
     */
    public List<Branch> getAll(Pagination pagination) {
        List<Branch> branches = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY BranchID DESC LIMIT ?, ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pagination.getOffset());
            stmt.setInt(2, pagination.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(extractBranch(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return branches;
    }

    /**
     * Returns paginated branches with optional keyword filter (matches Name/Address/Phone).
     */
    public List<Branch> search(String keyword, Pagination pagination) {
        List<Branch> branches = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        if (hasKeyword) {
            sql.append("WHERE Name LIKE ? OR Address LIKE ? OR Phone LIKE ? ");
        }
        sql.append("ORDER BY BranchID DESC LIMIT ?, ?");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(idx++, pattern);
                stmt.setString(idx++, pattern);
                stmt.setString(idx++, pattern);
            }
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(extractBranch(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return branches;
    }

    /**
     * Counts branches matching the given keyword (or all when keyword empty).
     */
    public int countAll(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Branch ");
        if (hasKeyword) {
            sql.append("WHERE Name LIKE ? OR Address LIKE ? OR Phone LIKE ?");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
                stmt.setString(3, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Returns total branch count (no filter).
     */
    public int countAll() {
        return countAll(null);
    }

    /**
     * Returns only active branches ordered by name ascending.
     */
    public List<Branch> getActive() {
        List<Branch> branches = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE Status = ? ORDER BY Name ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, AppConstants.STATUS_ACTIVE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(extractBranch(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return branches;
    }

    /**
     * Loads a single branch by its primary key.
     */
    public Branch getById(int branchId) {
        String sql = BASE_SELECT + "WHERE BranchID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractBranch(rs);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Inserts a new branch row. CreatedAt is left to the database default.
     */
    public boolean insert(Branch branch) {
        String sql = "INSERT INTO Branch (Name, Address, Phone, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getPhone());
            stmt.setString(4, branch.getStatus() == null ? AppConstants.STATUS_ACTIVE : branch.getStatus());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates name/address/phone/status for an existing branch.
     */
    public boolean update(Branch branch) {
        String sql = "UPDATE Branch SET Name = ?, Address = ?, Phone = ?, Status = ? WHERE BranchID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getPhone());
            stmt.setString(4, branch.getStatus() == null ? AppConstants.STATUS_ACTIVE : branch.getStatus());
            stmt.setInt(5, branch.getBranchId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Soft delete by setting Status to inactive.
     * Avoids FK violation when Employee rows still reference this branch.
     */
    public boolean softDelete(int branchId) {
        String sql = "UPDATE Branch SET Status = ? WHERE BranchID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, AppConstants.STATUS_INACTIVE);
            stmt.setInt(2, branchId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Checks whether a branch with the given name already exists.
     * When excludeId is non-null, that row is ignored (useful for updates).
     */
    public boolean existsByName(String name, Integer excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder("SELECT 1 FROM Branch WHERE Name = ?");
        if (excludeId != null) {
            sql.append(" AND BranchID <> ?");
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
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    private Branch extractBranch(ResultSet rs) throws SQLException {
        Branch branch = new Branch();
        branch.setBranchId(rs.getInt("BranchID"));
        branch.setName(rs.getString("Name"));
        branch.setAddress(rs.getString("Address"));
        branch.setPhone(rs.getString("Phone"));
        branch.setStatus(rs.getString("Status"));
        branch.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return branch;
    }
}
