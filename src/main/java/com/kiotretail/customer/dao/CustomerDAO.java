package com.kiotretail.customer.dao;

import com.kiotretail.customer.dto.CustomerFilterDTO;
import com.kiotretail.customer.model.Customer;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.base.Pagination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO for Customer CRUD against the SQL Server schema.
 *
 * Table: Customer (CustomerID INT PK IDENTITY, FullName NVARCHAR, Phone VARCHAR UNIQUE,
 *                  Email NVARCHAR, Address NVARCHAR, DoB DATE, Gender VARCHAR,
 *                  MembershipTier NVARCHAR, Points INT DEFAULT 0,
 *                  CreatedAt DATETIME DEFAULT GETDATE())
 *
 * All queries use parameterized PreparedStatements; no string concatenation of user input.
 */
public class CustomerDAO extends BaseDAO {

    /** Whitelist of sortable columns to defend against SQL injection via sortBy. */
    private static final Set<String> SORTABLE_COLUMNS = new HashSet<>(Arrays.asList(
            "CustomerID", "FullName", "Phone", "Email", "DoB",
            "Gender", "MembershipTier", "Points", "CreatedAt"
    ));

    private static final String BASE_SELECT =
            "SELECT CustomerID, FullName, Phone, Email, Address, DoB, Gender, " +
            "MembershipTier, Points, CreatedAt FROM Customer ";

    /**
     * List customers with dynamic keyword filter and pagination.
     */
    public List<Customer> getCustomers(CustomerFilterDTO filter, Pagination pagination) {
        List<Customer> customers = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        appendFilterClauses(sql, params, filter);

        sql.append("ORDER BY ").append(resolveOrderBy(filter)).append(' ');
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(extractCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Count customers matching the given filter (no pagination).
     */
    public int countCustomers(CustomerFilterDTO filter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM Customer WHERE 1 = 1 ");

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

    /**
     * Fetch single customer by primary key, or null if not found.
     */
    public Customer getById(int customerId) {
        String sql = BASE_SELECT + "WHERE CustomerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch single customer by phone (unique), or null if not found.
     */
    public Customer getByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        String sql = BASE_SELECT + "WHERE Phone = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert a new customer. CreatedAt is left to the database default.
     *
     * @return true when exactly one row was inserted
     */
    public boolean insert(Customer customer) {
        String sql = "INSERT INTO Customer (FullName, Phone, Email, Address, DoB, Gender, MembershipTier, Points) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setDate(5, customer.getDateOfBirth());
            stmt.setString(6, customer.getGender());
            stmt.setString(7, customer.getMembershipTier());
            stmt.setInt(8, customer.getPoints());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update all mutable fields of an existing customer.
     */
    public boolean update(Customer customer) {
        String sql = "UPDATE Customer SET FullName = ?, Phone = ?, Email = ?, Address = ?, " +
                     "DoB = ?, Gender = ?, MembershipTier = ?, Points = ? WHERE CustomerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setDate(5, customer.getDateOfBirth());
            stmt.setString(6, customer.getGender());
            stmt.setString(7, customer.getMembershipTier());
            stmt.setInt(8, customer.getPoints());
            stmt.setInt(9, customer.getCustomerId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hard delete by primary key.
     */
    public boolean delete(int customerId) {
        String sql = "DELETE FROM Customer WHERE CustomerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check whether a phone number is already used by some customer.
     * When {@code excludeId} is non-null, that customer is ignored (useful for updates).
     */
    public boolean existsByPhone(String phone, Integer excludeId) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder("SELECT 1 FROM Customer WHERE Phone = ?");
        if (excludeId != null) {
            sql.append(" AND CustomerID <> ?");
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, phone.trim());
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

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void appendFilterClauses(StringBuilder sql, List<Object> params, CustomerFilterDTO filter) {
        if (filter == null) {
            return;
        }
        if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            sql.append("AND (FullName LIKE ? OR Phone LIKE ? OR Email LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getGender() != null && !filter.getGender().trim().isEmpty()) {
            sql.append("AND Gender = ? ");
            params.add(filter.getGender().trim());
        }
        if (filter.getMembershipTier() != null && !filter.getMembershipTier().trim().isEmpty()) {
            sql.append("AND MembershipTier = ? ");
            params.add(filter.getMembershipTier().trim());
        }
    }

    private int bindParams(PreparedStatement stmt, List<Object> params, int startIndex) throws SQLException {
        int idx = startIndex;
        for (Object value : params) {
            stmt.setObject(idx++, value);
        }
        return idx;
    }

    private String resolveOrderBy(CustomerFilterDTO filter) {
        String sortBy = (filter == null || filter.getSortBy() == null) ? "CreatedAt" : filter.getSortBy();
        if (!SORTABLE_COLUMNS.contains(sortBy)) {
            sortBy = "CreatedAt";
        }
        String dir = (filter != null && "ASC".equalsIgnoreCase(filter.getSortDir())) ? "ASC" : "DESC";
        return sortBy + " " + dir;
    }

    private Customer extractCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("CustomerID"));
        customer.setFullName(rs.getString("FullName"));
        customer.setPhone(rs.getString("Phone"));
        customer.setEmail(rs.getString("Email"));
        customer.setAddress(rs.getString("Address"));
        customer.setDateOfBirth(rs.getDate("DoB"));
        customer.setGender(rs.getString("Gender"));
        customer.setMembershipTier(rs.getString("MembershipTier"));
        customer.setPoints(rs.getInt("Points"));
        customer.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return customer;
    }
}
