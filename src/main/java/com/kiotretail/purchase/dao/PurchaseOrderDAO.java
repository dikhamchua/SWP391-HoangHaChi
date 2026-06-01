package com.kiotretail.purchase.dao;

import com.kiotretail.purchase.dto.PendingApprovalItem;
import com.kiotretail.purchase.dto.PurchaseFilterDTO;
import com.kiotretail.purchase.model.PurchaseOrder;
import com.kiotretail.purchase.model.PurchaseOrderDetail;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the PurchaseOrder table (UC-4.2 / UC-4.3 approval workflow).
 *
 * <p>Reads use the {@code V_PurchaseOrderList} view so all join columns
 * (supplier / branch / employee / approver names) come from a single
 * indexed source. Writes target the base table directly.</p>
 *
 * <p>Status string is stored uppercase per {@code DocumentStatus} enum.
 * Detail-line CRUD lives in {@link PurchaseOrderDetailDAO}; this class
 * keeps {@link #insertDetail}, {@link #getDetails}, {@link #recalculateTotal}
 * for backward compatibility with older callers.</p>
 */
public class PurchaseOrderDAO extends BaseDAO {

    private static final String VIEW_SELECT =
            "SELECT PurchaseOrderID, OrderCode, Status, TotalAmount, Note, CreatedAt, " +
            "SubmittedAt, ApprovedAt, RejectedAt, RejectedReason, CancelledAt, " +
            "CancelledReason, CompletedAt, UpdatedAt, " +
            "SupplierID, SupplierName, BranchID, BranchName, " +
            "EmployeeID, EmployeeName, CreatedBy, CreatedByName, " +
            "ApprovedBy, ApprovedByName, RejectedBy, RejectedByName, " +
            "CancelledBy, CancelledByName " +
            "FROM V_PurchaseOrderList ";

    // ---------------------------------------------------------------------
    // Search / list / count
    // ---------------------------------------------------------------------

    public List<PurchaseOrder> search(PurchaseFilterDTO filter, Pagination pagination) {
        List<PurchaseOrder> orders = new ArrayList<>();
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
                    orders.add(extractFromView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public int countAll(PurchaseFilterDTO filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM V_PurchaseOrderList WHERE 1 = 1 ");
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

    /** Legacy list method for older callers (keyword + status only). */
    public List<PurchaseOrder> getAll(String keyword, String status, Pagination pagination) {
        PurchaseFilterDTO filter = new PurchaseFilterDTO();
        filter.setKeyword(keyword);
        filter.setStatus(status);
        return search(filter, pagination);
    }

    /** Legacy count method for older callers (keyword + status only). */
    public int countAll(String keyword, String status) {
        PurchaseFilterDTO filter = new PurchaseFilterDTO();
        filter.setKeyword(keyword);
        filter.setStatus(status);
        return countAll(filter);
    }

    // ---------------------------------------------------------------------
    // Approval queue (UC-4.3)
    // ---------------------------------------------------------------------

    /**
     * Lists purchase orders awaiting approval as lightweight projections for the
     * pending-approvals queue. Status is pinned to {@code PENDING_APPROVAL};
     * supplier and submitter (created-by) names come from the
     * {@code V_PurchaseOrderList} view. Any extra filter clauses (keyword,
     * supplier, branch, date range, createdBy) are applied on top.
     *
     * @param filter     optional extra filters; status filter is ignored here
     * @param pagination page/size for LIMIT
     * @return ordered (oldest submitted first) list of pending items
     */
    public List<PendingApprovalItem> findPendingApprovals(PurchaseFilterDTO filter, Pagination pagination) {
        List<PendingApprovalItem> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT PurchaseOrderID, OrderCode, TotalAmount, Note, SubmittedAt, " +
                "SupplierName, CreatedByName " +
                "FROM V_PurchaseOrderList WHERE Status = 'PENDING_APPROVAL' ");
        List<Object> params = new ArrayList<>();
        appendApprovalFilterClauses(sql, params, filter);
        sql.append("ORDER BY SubmittedAt ASC ");
        sql.append("LIMIT ?, ?");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(extractPendingItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /** Counts purchase orders in the given status. Mirrors {@link #countAll}. */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) AS total FROM V_PurchaseOrderList WHERE Status = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
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

    /**
     * Counts PENDING_APPROVAL purchase orders this approver is allowed to act on.
     *
     * <p>Separation of duties: rows the approver created themselves are excluded.
     * A Store Manager additionally cannot approve orders at or above
     * {@link AppConstants#OWNER_APPROVAL_THRESHOLD}; an Owner sees all pending
     * orders not created by themselves.</p>
     *
     * @param role       approver role (e.g. {@link AppConstants#ROLE_OWNER})
     * @param employeeId the approver's employee id
     * @return number of actionable pending orders
     */
    public int countPendingForApprover(String role, int employeeId) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM V_PurchaseOrderList " +
                "WHERE Status = 'PENDING_APPROVAL' AND (CreatedBy IS NULL OR CreatedBy <> ?) ");
        List<Object> params = new ArrayList<>();
        params.add(employeeId);
        if (!AppConstants.ROLE_OWNER.equals(role)) {
            sql.append("AND TotalAmount < ? ");
            params.add(AppConstants.OWNER_APPROVAL_THRESHOLD);
        }

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

    public PurchaseOrder getById(int purchaseOrderId) {
        String sql = VIEW_SELECT + "WHERE PurchaseOrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrderId);
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

    /** Detail rows joined with product info; kept for compatibility. */
    public List<PurchaseOrderDetail> getDetails(int purchaseOrderId) {
        return new PurchaseOrderDetailDAO().getByOrderId(purchaseOrderId);
    }

    // ---------------------------------------------------------------------
    // Insert / update
    // ---------------------------------------------------------------------

    public int insert(PurchaseOrder order) {
        String sql = "INSERT INTO PurchaseOrder (SupplierID, BranchID, EmployeeID, OrderCode, " +
                     "Status, TotalAmount, Note, CreatedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getSupplierId());
            stmt.setInt(2, order.getBranchId());
            stmt.setInt(3, order.getEmployeeId());
            stmt.setString(4, order.getOrderCode());
            stmt.setString(5, order.getStatus());
            stmt.setBigDecimal(6, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            stmt.setString(7, order.getNote());
            if (order.getCreatedBy() != null) {
                stmt.setInt(8, order.getCreatedBy());
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

    /** Legacy detail insert; new code should use {@link PurchaseOrderDetailDAO#insert}. */
    public boolean insertDetail(PurchaseOrderDetail detail) {
        return new PurchaseOrderDetailDAO().insert(detail);
    }

    public boolean updateStatus(int purchaseOrderId, String newStatus) {
        String sql = "UPDATE PurchaseOrder SET Status = ?, UpdatedAt = UTC_TIMESTAMP() " +
                     "WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, newStatus, purchaseOrderId);
    }

    /**
     * Updates the editable header fields of a DRAFT order (supplier, branch,
     * note). Status and audit fields are left untouched; line items and the
     * total are handled separately by the service.
     */
    public boolean update(PurchaseOrder order) {
        String sql = "UPDATE PurchaseOrder SET SupplierID = ?, BranchID = ?, Note = ?, " +
                     "UpdatedAt = UTC_TIMESTAMP() WHERE PurchaseOrderID = ?";
        return executeUpdate(sql,
                order.getSupplierId(),
                order.getBranchId(),
                order.getNote(),
                order.getPurchaseOrderId());
    }

    public boolean updateSubmittedAt(int purchaseOrderId) {
        String sql = "UPDATE PurchaseOrder SET Status = 'PENDING_APPROVAL', " +
                     "SubmittedAt = UTC_TIMESTAMP(), UpdatedAt = UTC_TIMESTAMP() " +
                     "WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, purchaseOrderId);
    }

    public boolean updateApproval(int purchaseOrderId, int approvedBy) {
        String sql = "UPDATE PurchaseOrder SET Status = 'APPROVED', ApprovedBy = ?, " +
                     "ApprovedAt = UTC_TIMESTAMP(), UpdatedAt = UTC_TIMESTAMP() " +
                     "WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, approvedBy, purchaseOrderId);
    }

    public boolean updateRejection(int purchaseOrderId, int rejectedBy, String reason) {
        String sql = "UPDATE PurchaseOrder SET Status = 'REJECTED', RejectedBy = ?, " +
                     "RejectedAt = UTC_TIMESTAMP(), RejectedReason = ?, " +
                     "UpdatedAt = UTC_TIMESTAMP() WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, rejectedBy, reason, purchaseOrderId);
    }

    public boolean updateCancellation(int purchaseOrderId, int cancelledBy, String reason) {
        String sql = "UPDATE PurchaseOrder SET Status = 'CANCELLED', CancelledBy = ?, " +
                     "CancelledAt = UTC_TIMESTAMP(), CancelledReason = ?, " +
                     "UpdatedAt = UTC_TIMESTAMP() WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, cancelledBy, reason, purchaseOrderId);
    }

    public boolean updateCompletedAt(int purchaseOrderId) {
        String sql = "UPDATE PurchaseOrder SET Status = 'COMPLETED', " +
                     "CompletedAt = UTC_TIMESTAMP(), UpdatedAt = UTC_TIMESTAMP() " +
                     "WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, purchaseOrderId);
    }

    public boolean recalculateTotal(int purchaseOrderId) {
        String sql = "UPDATE PurchaseOrder SET TotalAmount = " +
                     "(SELECT IFNULL(SUM(Quantity * UnitCost), 0) FROM PurchaseOrderDetail " +
                     " WHERE PurchaseOrderID = ?), UpdatedAt = UTC_TIMESTAMP() " +
                     "WHERE PurchaseOrderID = ?";
        return executeUpdate(sql, purchaseOrderId, purchaseOrderId);
    }

    /**
     * Returns the highest sequence number used in OrderCodes for the given
     * yyyyMMdd date. Codes follow {@code PO-yyyyMMdd-NNN}; values without a
     * numeric suffix are ignored.
     */
    public int getMaxSequenceForDate(String yyyyMMdd) {
        String prefix = "PO-" + yyyyMMdd + "-";
        String sql = "SELECT OrderCode FROM PurchaseOrder WHERE OrderCode LIKE ?";
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

    /** Legacy helper retained for old callers; new code uses CodeGenerator. */
    public String generateCode() {
        return "PO-" + System.currentTimeMillis();
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void appendFilterClauses(StringBuilder sql, List<Object> params, PurchaseFilterDTO filter) {
        if (filter == null) return;
        if (filter.hasKeyword()) {
            sql.append("AND (OrderCode LIKE ? OR SupplierName LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getSupplierId() != null && filter.getSupplierId() > 0) {
            sql.append("AND SupplierID = ? ");
            params.add(filter.getSupplierId());
        }
        if (filter.getBranchId() != null && filter.getBranchId() > 0) {
            sql.append("AND BranchID = ? ");
            params.add(filter.getBranchId());
        }
        if (filter.hasStatus()) {
            sql.append("AND Status = ? ");
            params.add(filter.getStatus().trim());
        }
        if (filter.hasApprovalStatus()) {
            sql.append("AND UPPER(Status) = ? ");
            params.add(filter.getApprovalStatus().trim().toUpperCase());
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

    /**
     * Appends the optional filter clauses relevant to the approval queue.
     * Unlike {@link #appendFilterClauses}, the status clause is skipped because
     * the queue query already pins status to PENDING_APPROVAL.
     */
    private void appendApprovalFilterClauses(StringBuilder sql, List<Object> params, PurchaseFilterDTO filter) {
        if (filter == null) return;
        if (filter.hasKeyword()) {
            sql.append("AND (OrderCode LIKE ? OR SupplierName LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getSupplierId() != null && filter.getSupplierId() > 0) {
            sql.append("AND SupplierID = ? ");
            params.add(filter.getSupplierId());
        }
        if (filter.getBranchId() != null && filter.getBranchId() > 0) {
            sql.append("AND BranchID = ? ");
            params.add(filter.getBranchId());
        }
        if (filter.getCreatedBy() != null && filter.getCreatedBy() > 0) {
            sql.append("AND CreatedBy = ? ");
            params.add(filter.getCreatedBy());
        }
        if (filter.hasDateFrom()) {
            sql.append("AND SubmittedAt >= ? ");
            params.add(filter.getDateFrom().trim() + " 00:00:00");
        }
        if (filter.hasDateTo()) {
            sql.append("AND SubmittedAt <= ? ");
            params.add(filter.getDateTo().trim() + " 23:59:59");
        }
    }

    private PendingApprovalItem extractPendingItem(ResultSet rs) throws SQLException {
        PendingApprovalItem item = new PendingApprovalItem();
        item.setDocumentType(AppConstants.DOC_TYPE_PURCHASE_ORDER);
        item.setDocumentId(rs.getInt("PurchaseOrderID"));
        item.setDocumentCode(rs.getString("OrderCode"));
        item.setSubmitterName(rs.getString("CreatedByName"));
        item.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
        item.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        item.setDescription(rs.getString("Note"));
        return item;
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

    private PurchaseOrder extractFromView(ResultSet rs) throws SQLException {
        PurchaseOrder o = new PurchaseOrder();
        o.setPurchaseOrderId(rs.getInt("PurchaseOrderID"));
        o.setOrderCode(rs.getString("OrderCode"));
        o.setStatus(rs.getString("Status"));
        o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        o.setNote(rs.getString("Note"));
        o.setCreatedAt(rs.getTimestamp("CreatedAt"));
        o.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
        o.setApprovedAt(rs.getTimestamp("ApprovedAt"));
        o.setRejectedAt(rs.getTimestamp("RejectedAt"));
        o.setRejectedReason(rs.getString("RejectedReason"));
        o.setCancelledAt(rs.getTimestamp("CancelledAt"));
        o.setCancelledReason(rs.getString("CancelledReason"));
        o.setCompletedAt(rs.getTimestamp("CompletedAt"));
        o.setUpdatedAt(rs.getTimestamp("UpdatedAt"));

        o.setSupplierId(rs.getInt("SupplierID"));
        o.setSupplierName(rs.getString("SupplierName"));
        o.setBranchId(rs.getInt("BranchID"));
        o.setBranchName(rs.getString("BranchName"));
        o.setEmployeeId(rs.getInt("EmployeeID"));
        o.setEmployeeName(rs.getString("EmployeeName"));
        o.setCreatedBy(getNullableInt(rs, "CreatedBy"));
        o.setCreatedByName(rs.getString("CreatedByName"));
        o.setApprovedBy(getNullableInt(rs, "ApprovedBy"));
        o.setApprovedByName(rs.getString("ApprovedByName"));
        o.setRejectedBy(getNullableInt(rs, "RejectedBy"));
        o.setRejectedByName(rs.getString("RejectedByName"));
        o.setCancelledBy(getNullableInt(rs, "CancelledBy"));
        o.setCancelledByName(rs.getString("CancelledByName"));
        return o;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int v = rs.getInt(column);
        return rs.wasNull() ? null : v;
    }

    @SuppressWarnings("unused")
    private Timestamp ts(java.util.Date d) {
        return d == null ? null : new Timestamp(d.getTime());
    }
}
