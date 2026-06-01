package com.kiotretail.inventory.dao;

import com.kiotretail.inventory.dto.StockTransferFilterDTO;
import com.kiotretail.inventory.model.StockTransfer;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the StockTransfer table (UC-5.3).
 *
 * <p>Reads use the V_StockTransferList view; writes target the base table directly.</p>
 */
public class StockTransferDAO extends BaseDAO {

    private static final String VIEW_SELECT =
            "SELECT TransferID, TransferCode, Status, Note, TotalItems, TotalQuantity, " +
            "FromBranchID, FromBranchName, ToBranchID, ToBranchName, " +
            "CreatedBy, CreatedByName, SubmittedAt, " +
            "ApprovedBy, ApprovedByName, ApprovedAt, " +
            "RejectedBy, RejectedByName, RejectedAt, RejectedReason, " +
            "ShippedBy, ShippedByName, ShippedAt, " +
            "ReceivedBy, ReceivedByName, ReceivedAt, " +
            "CancelledBy, CancelledByName, CancelledAt, CancelledReason, " +
            "CreatedAt, UpdatedAt " +
            "FROM V_StockTransferList ";

    public List<StockTransfer> search(StockTransferFilterDTO filter, Pagination pagination) {
        List<StockTransfer> list = new ArrayList<>();
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

    public int countAll(StockTransferFilterDTO filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM V_StockTransferList WHERE 1 = 1 ");
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

    public StockTransfer getById(int transferId) {
        String sql = VIEW_SELECT + "WHERE TransferID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transferId);
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

    public int insert(StockTransfer st) {
        String sql = "INSERT INTO StockTransfer (TransferCode, FromBranchID, ToBranchID, " +
                     "Status, Note, TotalItems, TotalQuantity, CreatedBy) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, st.getTransferCode());
            stmt.setInt(2, st.getFromBranchId());
            stmt.setInt(3, st.getToBranchId());
            stmt.setString(4, st.getStatus());
            stmt.setString(5, st.getNote());
            stmt.setInt(6, st.getTotalItems());
            stmt.setInt(7, st.getTotalQuantity());
            if (st.getCreatedBy() != null) {
                stmt.setInt(8, st.getCreatedBy());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
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

    public boolean updateStatus(int transferId, String newStatus) {
        String sql = "UPDATE StockTransfer SET Status = ?, UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE TransferID = ?";
        return executeUpdate(sql, newStatus, transferId);
    }

    public boolean updateSubmittedAt(int transferId) {
        String sql = "UPDATE StockTransfer SET Status = 'PENDING_APPROVAL', " +
                     "SubmittedAt = SYSUTCDATETIME(), UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE TransferID = ?";
        return executeUpdate(sql, transferId);
    }

    public boolean updateApproval(int transferId, int approvedBy) {
        String sql = "UPDATE StockTransfer SET Status = 'APPROVED', ApprovedBy = ?, " +
                     "ApprovedAt = SYSUTCDATETIME(), UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE TransferID = ?";
        return executeUpdate(sql, approvedBy, transferId);
    }

    public boolean updateRejection(int transferId, int rejectedBy, String reason) {
        String sql = "UPDATE StockTransfer SET Status = 'REJECTED', RejectedBy = ?, " +
                     "RejectedAt = SYSUTCDATETIME(), RejectedReason = ?, " +
                     "UpdatedAt = SYSUTCDATETIME() WHERE TransferID = ?";
        return executeUpdate(sql, rejectedBy, reason, transferId);
    }

    public boolean updateCancellation(int transferId, int cancelledBy, String reason) {
        String sql = "UPDATE StockTransfer SET Status = 'CANCELLED', CancelledBy = ?, " +
                     "CancelledAt = SYSUTCDATETIME(), CancelledReason = ?, " +
                     "UpdatedAt = SYSUTCDATETIME() WHERE TransferID = ?";
        return executeUpdate(sql, cancelledBy, reason, transferId);
    }

    public boolean updateShipped(int transferId, int shippedBy) {
        String sql = "UPDATE StockTransfer SET Status = 'IN_TRANSIT', ShippedBy = ?, " +
                     "ShippedAt = SYSUTCDATETIME(), UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE TransferID = ?";
        return executeUpdate(sql, shippedBy, transferId);
    }

    public boolean updateReceived(int transferId, int receivedBy) {
        String sql = "UPDATE StockTransfer SET Status = 'COMPLETED', ReceivedBy = ?, " +
                     "ReceivedAt = SYSUTCDATETIME(), UpdatedAt = SYSUTCDATETIME() " +
                     "WHERE TransferID = ?";
        return executeUpdate(sql, receivedBy, transferId);
    }

    public int getMaxSequenceForDate(String yyyyMMdd) {
        String prefix = "ST-" + yyyyMMdd + "-";
        String sql = "SELECT TransferCode FROM StockTransfer WHERE TransferCode LIKE ?";
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
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    private void appendFilterClauses(StringBuilder sql, List<Object> params, StockTransferFilterDTO filter) {
        if (filter == null) return;
        if (filter.hasKeyword()) {
            sql.append("AND (TransferCode LIKE ? OR Note LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getFromBranchId() != null && filter.getFromBranchId() > 0) {
            sql.append("AND FromBranchID = ? ");
            params.add(filter.getFromBranchId());
        }
        if (filter.getToBranchId() != null && filter.getToBranchId() > 0) {
            sql.append("AND ToBranchID = ? ");
            params.add(filter.getToBranchId());
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

    private StockTransfer extractFromView(ResultSet rs) throws SQLException {
        StockTransfer t = new StockTransfer();
        t.setTransferId(rs.getInt("TransferID"));
        t.setTransferCode(rs.getString("TransferCode"));
        t.setStatus(rs.getString("Status"));
        t.setNote(rs.getString("Note"));
        t.setTotalItems(rs.getInt("TotalItems"));
        t.setTotalQuantity(rs.getInt("TotalQuantity"));
        t.setFromBranchId(rs.getInt("FromBranchID"));
        t.setFromBranchName(rs.getString("FromBranchName"));
        t.setToBranchId(rs.getInt("ToBranchID"));
        t.setToBranchName(rs.getString("ToBranchName"));
        t.setCreatedBy(getNullableInt(rs, "CreatedBy"));
        t.setCreatedByName(rs.getString("CreatedByName"));
        t.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
        t.setApprovedBy(getNullableInt(rs, "ApprovedBy"));
        t.setApprovedByName(rs.getString("ApprovedByName"));
        t.setApprovedAt(rs.getTimestamp("ApprovedAt"));
        t.setRejectedBy(getNullableInt(rs, "RejectedBy"));
        t.setRejectedByName(rs.getString("RejectedByName"));
        t.setRejectedAt(rs.getTimestamp("RejectedAt"));
        t.setRejectedReason(rs.getString("RejectedReason"));
        t.setShippedBy(getNullableInt(rs, "ShippedBy"));
        t.setShippedByName(rs.getString("ShippedByName"));
        t.setShippedAt(rs.getTimestamp("ShippedAt"));
        t.setReceivedBy(getNullableInt(rs, "ReceivedBy"));
        t.setReceivedByName(rs.getString("ReceivedByName"));
        t.setReceivedAt(rs.getTimestamp("ReceivedAt"));
        t.setCancelledBy(getNullableInt(rs, "CancelledBy"));
        t.setCancelledByName(rs.getString("CancelledByName"));
        t.setCancelledAt(rs.getTimestamp("CancelledAt"));
        t.setCancelledReason(rs.getString("CancelledReason"));
        t.setCreatedAt(rs.getTimestamp("CreatedAt"));
        t.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return t;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int v = rs.getInt(column);
        return rs.wasNull() ? null : v;
    }
}
