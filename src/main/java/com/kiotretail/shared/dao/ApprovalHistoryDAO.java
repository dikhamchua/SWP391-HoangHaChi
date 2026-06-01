package com.kiotretail.shared.dao;

import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.ApprovalAction;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.model.ApprovalHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the ApprovalHistory audit table.
 * Schema: HistoryID, DocumentType, DocumentID, FromStatus, ToStatus,
 *         Action, PerformedBy, Reason, CreatedAt
 */
public class ApprovalHistoryDAO extends BaseDAO {

    private static final String COLUMNS =
            "HistoryID, DocumentType, DocumentID, FromStatus, ToStatus, " +
            "Action, PerformedBy, Reason, CreatedAt";

    private static final String BASE_SELECT =
            "SELECT " + COLUMNS + " FROM ApprovalHistory ";

    private static final String SELECT_WITH_USER =
            "SELECT h.HistoryID, h.DocumentType, h.DocumentID, h.FromStatus, " +
            "h.ToStatus, h.Action, h.PerformedBy, h.Reason, h.CreatedAt, " +
            "e.FullName AS PerformedByName " +
            "FROM ApprovalHistory h " +
            "LEFT JOIN Employee e ON h.PerformedBy = e.EmployeeID ";

    /**
     * Inserts a new history row.
     * @return the generated HistoryID, or -1 when the insert fails.
     */
    public int insert(ApprovalHistory history) {
        String sql = "INSERT INTO ApprovalHistory " +
                "(DocumentType, DocumentID, FromStatus, ToStatus, Action, PerformedBy, Reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, history.getDocumentType());
            stmt.setInt(2, history.getDocumentId());
            stmt.setString(3, history.getFromStatus());
            stmt.setString(4, history.getToStatus());
            stmt.setString(5, history.getAction());
            stmt.setInt(6, history.getPerformedBy());
            stmt.setString(7, history.getReason());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                return -1;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    history.setHistoryId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Returns all history rows for a document, newest first.
     * Joins Employee to populate {@code performedByName}.
     */
    public List<ApprovalHistory> getByDocument(String documentType, int documentId) {
        List<ApprovalHistory> list = new ArrayList<>();
        String sql = SELECT_WITH_USER +
                "WHERE h.DocumentType = ? AND h.DocumentID = ? " +
                "ORDER BY h.CreatedAt DESC, h.HistoryID DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentType);
            stmt.setInt(2, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApprovalHistory h = extract(rs);
                    h.setPerformedByName(rs.getString("PerformedByName"));
                    list.add(h);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    // ---------------------------------------------------------------------
    // Task 5.3 - filtered search + companion count (for pagination)
    // ---------------------------------------------------------------------

    /**
     * Searches the audit log with optional filters, newest first, paginated.
     * All filter arguments are nullable; a {@code null}/blank value means
     * "no restriction on that column". Joins Employee to populate
     * {@code performedByName} so the history JSP can render the approver name.
     *
     * @param documentType optional document type (e.g. PURCHASE_ORDER); null/blank = any
     * @param action       optional approval action; null = any
     * @param performedBy  optional employee id of the actor; null or &lt;= 0 = any
     * @param from         optional inclusive lower bound on CreatedAt (date); null = open
     * @param to           optional inclusive upper bound on CreatedAt (date); null = open
     * @param pagination   page/size (MySQL {@code LIMIT ?, ?}); must not be null
     * @return matching rows for the requested page
     */
    public List<ApprovalHistory> search(String documentType,
                                        ApprovalAction action,
                                        Integer performedBy,
                                        LocalDate from,
                                        LocalDate to,
                                        Pagination pagination) {
        List<ApprovalHistory> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_WITH_USER);
        sql.append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        appendSearchFilters(sql, params, documentType, action, performedBy, from, to);
        sql.append("ORDER BY h.CreatedAt DESC, h.HistoryID DESC ");
        sql.append("LIMIT ?, ?");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApprovalHistory h = extract(rs);
                    h.setPerformedByName(rs.getString("PerformedByName"));
                    list.add(h);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Counts rows matching the same filters as
     * {@link #search(String, ApprovalAction, Integer, LocalDate, LocalDate, Pagination)}.
     * Used to populate {@code pageResult.totalItems} for pagination.
     */
    public int countSearch(String documentType,
                           ApprovalAction action,
                           Integer performedBy,
                           LocalDate from,
                           LocalDate to) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM ApprovalHistory h WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        appendSearchFilters(sql, params, documentType, action, performedBy, from, to);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            bindParams(stmt, params, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return 0;
    }

    // ---------------------------------------------------------------------
    // Task 5.4 - aggregate counts per action
    // ---------------------------------------------------------------------

    /**
     * Aggregates the audit log into decision-summary counts for the stat cards.
     * Returns total plus per-action counts. {@code total} is the sum of the
     * three tracked decision actions (APPROVE + REJECT + CANCEL). Counts are
     * global (unfiltered) so the cards summarise the whole history table.
     * Getter names match the history JSP EL: {@code stats.total},
     * {@code stats.approved}, {@code stats.rejected}, {@code stats.cancelled}.
     */
    public ActionStats countByAction() {
        ActionStats stats = new ActionStats();
        String sql = "SELECT Action, COUNT(*) AS cnt FROM ApprovalHistory GROUP BY Action";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String act = rs.getString("Action");
                int cnt = rs.getInt("cnt");
                if (ApprovalAction.APPROVE.name().equals(act)) {
                    stats.setApproved(cnt);
                } else if (ApprovalAction.REJECT.name().equals(act)) {
                    stats.setRejected(cnt);
                } else if (ApprovalAction.CANCEL.name().equals(act)) {
                    stats.setCancelled(cnt);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        stats.setTotal(stats.getApproved() + stats.getRejected() + stats.getCancelled());
        return stats;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void appendSearchFilters(StringBuilder sql,
                                     List<Object> params,
                                     String documentType,
                                     ApprovalAction action,
                                     Integer performedBy,
                                     LocalDate from,
                                     LocalDate to) {
        if (documentType != null && !documentType.trim().isEmpty()) {
            sql.append("AND h.DocumentType = ? ");
            params.add(documentType.trim());
        }
        if (action != null) {
            sql.append("AND h.Action = ? ");
            params.add(action.name());
        }
        if (performedBy != null && performedBy > 0) {
            sql.append("AND h.PerformedBy = ? ");
            params.add(performedBy);
        }
        if (from != null) {
            sql.append("AND h.CreatedAt >= ? ");
            params.add(from.toString() + " 00:00:00");
        }
        if (to != null) {
            sql.append("AND h.CreatedAt <= ? ");
            params.add(to.toString() + " 23:59:59");
        }
    }

    private int bindParams(PreparedStatement stmt, List<Object> params, int startIndex) throws SQLException {
        int idx = startIndex;
        for (Object value : params) {
            stmt.setObject(idx++, value);
        }
        return idx;
    }

    private ApprovalHistory extract(ResultSet rs) throws SQLException {
        ApprovalHistory h = new ApprovalHistory();
        h.setHistoryId(rs.getInt("HistoryID"));
        h.setDocumentType(rs.getString("DocumentType"));
        h.setDocumentId(rs.getInt("DocumentID"));
        h.setFromStatus(rs.getString("FromStatus"));
        h.setToStatus(rs.getString("ToStatus"));
        h.setAction(rs.getString("Action"));
        h.setPerformedBy(rs.getInt("PerformedBy"));
        h.setReason(rs.getString("Reason"));
        h.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return h;
    }

    /**
     * Lightweight decision-summary stats consumed by the approval-history JSP
     * stat cards. Field/getter names intentionally match the EL expressions
     * {@code stats.total|approved|rejected|cancelled}.
     */
    public static class ActionStats {
        private int total;
        private int approved;
        private int rejected;
        private int cancelled;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getApproved() {
            return approved;
        }

        public void setApproved(int approved) {
            this.approved = approved;
        }

        public int getRejected() {
            return rejected;
        }

        public void setRejected(int rejected) {
            this.rejected = rejected;
        }

        public int getCancelled() {
            return cancelled;
        }

        public void setCancelled(int cancelled) {
            this.cancelled = cancelled;
        }
    }
}
