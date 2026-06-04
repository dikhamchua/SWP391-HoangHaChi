package com.kiotretail.purchase.dao;

import com.kiotretail.purchase.model.PurchaseOrderHistory;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderHistoryDAO extends BaseDAO {

    private static final String SELECT_WITH_USER =
            "SELECT h.HistoryID, h.PurchaseOrderID, h.FromStatus, h.ToStatus, " +
            "h.Action, h.PerformedBy, h.Reason, h.CreatedAt, " +
            "e.FullName AS PerformedByName " +
            "FROM PurchaseOrderHistory h " +
            "LEFT JOIN Employee e ON h.PerformedBy = e.EmployeeID ";

    public int insert(PurchaseOrderHistory history) {
        String sql = "INSERT INTO PurchaseOrderHistory " +
                "(PurchaseOrderID, FromStatus, ToStatus, Action, PerformedBy, Reason) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, history.getPurchaseOrderId());
            stmt.setString(2, history.getFromStatus());
            stmt.setString(3, history.getToStatus());
            stmt.setString(4, history.getAction());
            stmt.setInt(5, history.getPerformedBy());
            stmt.setString(6, history.getReason());

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

    public List<PurchaseOrderHistory> getByOrderId(int purchaseOrderId) {
        List<PurchaseOrderHistory> list = new ArrayList<>();
        String sql = SELECT_WITH_USER +
                "WHERE h.PurchaseOrderID = ? " +
                "ORDER BY h.CreatedAt DESC, h.HistoryID DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, purchaseOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extract(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    private PurchaseOrderHistory extract(ResultSet rs) throws SQLException {
        PurchaseOrderHistory h = new PurchaseOrderHistory();
        h.setHistoryId(rs.getInt("HistoryID"));
        h.setPurchaseOrderId(rs.getInt("PurchaseOrderID"));
        h.setFromStatus(rs.getString("FromStatus"));
        h.setToStatus(rs.getString("ToStatus"));
        h.setAction(rs.getString("Action"));
        h.setPerformedBy(rs.getInt("PerformedBy"));
        h.setReason(rs.getString("Reason"));
        h.setCreatedAt(rs.getTimestamp("CreatedAt"));
        h.setPerformedByName(rs.getString("PerformedByName"));
        return h;
    }

    // ---------------------------------------------------------------------
    // Filtered search + pagination (for history page)
    // ---------------------------------------------------------------------

    /**
     * Searches PO history with optional filters, newest first, paginated.
     *
     * @param action      optional action filter (e.g. "APPROVE"); null = any
     * @param performedBy optional employee id; null or &lt;= 0 = any
     * @param from        optional inclusive start date; null = open
     * @param to          optional inclusive end date; null = open
     * @param pagination  page/size; must not be null
     */
    public List<PurchaseOrderHistory> getHistoryFiltered(String action,
                                                         Integer performedBy,
                                                         LocalDate from,
                                                         LocalDate to,
                                                         Pagination pagination) {
        List<PurchaseOrderHistory> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_WITH_USER);
        sql.append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, action, performedBy, from, to);
        sql.append("ORDER BY h.CreatedAt DESC, h.HistoryID DESC ");
        sql.append("LIMIT ?, ?");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extract(rs));
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Counts rows matching the same filters as {@link #getHistoryFiltered}.
     */
    public int countHistoryFiltered(String action,
                                    Integer performedBy,
                                    LocalDate from,
                                    LocalDate to) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM PurchaseOrderHistory h WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, action, performedBy, from, to);

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
    // Aggregate stats (for history page stat cards)
    // ---------------------------------------------------------------------

    /**
     * Counts PO history rows grouped by action. Returns stats with
     * total, approved, rejected, cancelled counts.
     */
    public ActionStats countByAction() {
        ActionStats stats = new ActionStats();
        String sql = "SELECT Action, COUNT(*) AS cnt FROM PurchaseOrderHistory GROUP BY Action";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String act = rs.getString("Action");
                int cnt = rs.getInt("cnt");
                if ("APPROVE".equals(act)) {
                    stats.approved = cnt;
                } else if ("REJECT".equals(act)) {
                    stats.rejected = cnt;
                } else if ("CANCEL".equals(act)) {
                    stats.cancelled = cnt;
                }
                stats.total += cnt;
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        return stats;
    }

    /**
     * Lightweight stats object for the history page stat cards.
     */
    public static class ActionStats {
        private int total;
        private int approved;
        private int rejected;
        private int cancelled;

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getApproved() { return approved; }
        public void setApproved(int approved) { this.approved = approved; }
        public int getRejected() { return rejected; }
        public void setRejected(int rejected) { this.rejected = rejected; }
        public int getCancelled() { return cancelled; }
        public void setCancelled(int cancelled) { this.cancelled = cancelled; }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String action, Integer performedBy,
                               LocalDate from, LocalDate to) {
        if (action != null && !action.trim().isEmpty()) {
            sql.append("AND h.Action = ? ");
            params.add(action.trim());
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

    private int bindParams(PreparedStatement stmt, List<Object> params, int startIndex)
            throws SQLException {
        int idx = startIndex;
        for (Object value : params) {
            stmt.setObject(idx++, value);
        }
        return idx;
    }
}