package com.kiotretail.inventory.dao;

import com.kiotretail.inventory.dto.StockAdjustmentFilterDTO;
import com.kiotretail.inventory.model.StockAdjustment;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the StockAdjustment table (UC-5.1 / UC-5.2 approval workflow).
 *
 * <p>Reads use the {@code V_StockAdjustmentList} view; writes target the
 * base table directly. Status string is stored uppercase per
 * {@code DocumentStatus} enum. Detail-line CRUD lives in
 * {@link StockAdjustmentDetailDAO}.</p>
 */
public class StockAdjustmentDAO extends BaseDAO {

    private static final String VIEW_SELECT =
            "SELECT AdjustmentID, AdjustmentCode, BranchID, BranchName, Status, " +
            "Reason, Note, TotalVarianceValue, CreatedBy, CreatedByName, " +
            "SubmittedAt, ApprovedBy, ApprovedByName, ApprovedAt, " +
            "RejectedBy, RejectedByName, RejectedAt, RejectedReason, " +
            "CancelledBy, CancelledByName, CancelledAt, CancelledReason, " +
            "CreatedAt, UpdatedAt " +
            "FROM V_StockAdjustmentList ";

    public List<StockAdjustment> search(StockAdjustmentFilterDTO filter, Pagination pagination) {
        List<StockAdjustment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(VIEW_SELECT);
        sql.append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        appendFilterClauses(sql, params, filter);
        sql.append("ORDER BY CreatedAt DESC ");
        sql.append("LIMIT ?, ?");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll(StockAdjustmentFilterDTO filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM V_StockAdjustmentList WHERE 1 = 1 ");
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

    public StockAdjustment getById(int adjustmentId) {
        String sql = VIEW_SELECT + "WHERE AdjustmentID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adjustmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromView(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(StockAdjustment adj) {
        String sql = "INSERT INTO StockAdjustment (AdjustmentCode, BranchID, Status, " +
                     "Reason, Note, TotalVarianceValue, CreatedBy) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, adj.getAdjustmentCode());
            stmt.setInt(2, adj.getBranchId());
            stmt.setString(3, adj.getStatus());
            stmt.setString(4, adj.getReason());
            stmt.setString(5, adj.getNote());
            stmt.setBigDecimal(6, adj.getTotalVarianceValue() != null ? adj.getTotalVarianceValue() : BigDecimal.ZERO);
            if (adj.getCreatedBy() != null) {
                stmt.setInt(7, adj.getCreatedBy());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            if (stmt.executeUpdate() == 1) {
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

    public boolean updateStatus(int adjustmentId, String newStatus) {
        String sql = "UPDATE StockAdjustment SET Status = ?, UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE AdjustmentID = ?";
        return executeUpdate(sql, newStatus, adjustmentId);
    }

    public boolean updateSubmittedAt(int adjustmentId) {
        String sql = "UPDATE StockAdjustment SET Status = 'PENDING_APPROVAL', " +
                     "SubmittedAt = SYSUTCDATETIME(), UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE AdjustmentID = ?";
        return executeUpdate(sql, adjustmentId);
    }

    public boolean updateApproval(int adjustmentId, int approvedBy) {
        String sql = "UPDATE StockAdjustment SET Status = 'APPROVED', ApprovedBy = ?, " +
                     "ApprovedAt = SYSUTCDATETIME(), UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE AdjustmentID = ?";
        return executeUpdate(sql, approvedBy, adjustmentId);
    }

    public boolean updateRejection(int adjustmentId, int rejectedBy, String reason) {
        String sql = "UPDATE StockAdjustment SET Status = 'REJECTED', RejectedBy = ?, " +
                     "RejectedAt = SYSUTCDATETIME(), RejectedReason = ?, " +
                     "UpdatedAt = SYSUTCDATETIME() WHERE AdjustmentID = ?";
        return executeUpdate(sql, rejectedBy, reason, adjustmentId);
    }

    public boolean updateCancellation(int adjustmentId, int cancelledBy, String reason) {
        String sql = "UPDATE StockAdjustment SET Status = 'CANCELLED', CancelledBy = ?, " +
                     "CancelledAt = SYSUTCDATETIME(), CancelledReason = ?, " +
                     "UpdatedAt = SYSUTCDATETIME() WHERE AdjustmentID = ?";
        return executeUpdate(sql, cancelledBy, reason, adjustmentId);
    }

    /**
     * Returns the highest sequence number used in AdjustmentCodes for the given
     * yyyyMMdd date. Codes follow {@code SA-yyyyMMdd-NNN}; values without a
     * numeric suffix are ignored.
     */
    public int getMaxSequenceForDate(String yyyyMMdd) {
        String prefix = "SA-" + yyyyMMdd + "-";
        String sql = "SELECT AdjustmentCode FROM StockAdjustment WHERE AdjustmentCode LIKE ?";
        int max = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString(1);
                    if (code == null) continue;
                    String tail = code.substring(prefix.length());
                    try {
                        int seq = Integer.parseInt(tail);
                        if (seq > max) max = seq;
                    } catch (NumberFormatException ignored) {
                        // skip non-numeric suffixes
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    private void appendFilterClauses(StringBuilder sql, List<Object> params, StockAdjustmentFilterDTO filter) {
        if (filter == null) return;
        if (filter.hasKeyword()) {
            sql.append("AND (AdjustmentCode LIKE ? OR Reason LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getBranchId() != null && filter.getBranchId() > 0) {
            sql.append("AND BranchID = ? ");
            params.add(filter.getBranchId());
        }
        if (filter.hasStatus()) {
            sql.append("AND Status = ? ");
            params.add(filter.getStatus().trim());
        }
        if (filter.getCreatedBy() != null && filter.getCreatedBy() > 0) {
            sql.append("AND CreatedBy = ? ");
            params.add(filter.getCreatedBy());
        }
        if (filter.hasDateFrom()) {
            sql.append("AND CreatedAt >= ? ");
            params.add(filter.getDateFrom().trim() + " 00:00:00");
        }
        if (filter.hasDateTo()) {
            sql.append("AND CreatedAt <= ? ");
            params.add(filter.getDateTo().trim() + " 23:59:59");
        }
    }

    private int bindParams(PreparedStatement stmt, List<Object> params, int startIndex) throws SQLException {
        int idx = startIndex;
        for (Object value : params) {
            stmt.setObject(idx++, value);
        }
        return idx;
    }

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

    private StockAdjustment extractFromView(ResultSet rs) throws SQLException {
        StockAdjustment a = new StockAdjustment();
        a.setAdjustmentId(rs.getInt("AdjustmentID"));
        a.setAdjustmentCode(rs.getString("AdjustmentCode"));
        a.setBranchId(rs.getInt("BranchID"));
        a.setBranchName(rs.getString("BranchName"));
        a.setStatus(rs.getString("Status"));
        a.setReason(rs.getString("Reason"));
        a.setNote(rs.getString("Note"));
        a.setTotalVarianceValue(rs.getBigDecimal("TotalVarianceValue"));
        a.setCreatedBy(getNullableInt(rs, "CreatedBy"));
        a.setCreatedByName(rs.getString("CreatedByName"));
        a.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
        a.setApprovedBy(getNullableInt(rs, "ApprovedBy"));
        a.setApprovedByName(rs.getString("ApprovedByName"));
        a.setApprovedAt(rs.getTimestamp("ApprovedAt"));
        a.setRejectedBy(getNullableInt(rs, "RejectedBy"));
        a.setRejectedByName(rs.getString("RejectedByName"));
        a.setRejectedAt(rs.getTimestamp("RejectedAt"));
        a.setRejectedReason(rs.getString("RejectedReason"));
        a.setCancelledBy(getNullableInt(rs, "CancelledBy"));
        a.setCancelledByName(rs.getString("CancelledByName"));
        a.setCancelledAt(rs.getTimestamp("CancelledAt"));
        a.setCancelledReason(rs.getString("CancelledReason"));
        a.setCreatedAt(rs.getTimestamp("CreatedAt"));
        a.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return a;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int v = rs.getInt(column);
        return rs.wasNull() ? null : v;
    }
}
