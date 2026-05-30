package com.kiotretail.employee.dao;

import com.kiotretail.employee.model.Branch;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.constant.AppConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            e.printStackTrace();
        }
        return branches;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
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
