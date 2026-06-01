package com.kiotretail.product.dao;

import com.kiotretail.product.dto.ProductFilterDTO;
import com.kiotretail.product.model.Product;
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
 * DAO for Product CRUD against the SQL Server DBFinora schema.
 *
 * Table: Product (ProductID INT PK IDENTITY, CategoryID INT FK, Name NVARCHAR,
 *                 SKU VARCHAR UNIQUE, Price DECIMAL(18,2), CostPrice DECIMAL(18,2),
 *                 StockAlertQty INT, Status VARCHAR, CreatedAt DATETIME)
 *
 * All queries use parameterized PreparedStatements; no string concatenation of user input.
 */
public class ProductDAO extends BaseDAO {

    /** Whitelist of sortable columns to defend against SQL injection via sortBy. */
    private static final Set<String> SORTABLE_COLUMNS = new HashSet<>(Arrays.asList(
            "createdAt", "name", "sku", "price", "costPrice", "stockAlertQty", "status"
    ));

    private static final String BASE_SELECT =
            "SELECT p.ProductID, p.CategoryID, p.Name AS ProductName, p.SKU, p.Price, p.CostPrice, " +
            "p.StockAlertQty, p.Status, p.CreatedAt, c.Name AS CategoryName " +
            "FROM Product p " +
            "LEFT JOIN Category c ON p.CategoryID = c.CategoryID ";

    /**
     * List products with dynamic filter and pagination.
     */
    public List<Product> getProducts(ProductFilterDTO filter, Pagination pagination) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        appendFilterClauses(sql, params, filter);

        sql.append("ORDER BY ").append(resolveOrderBy(filter)).append(' ');
        sql.append("LIMIT ?, ?");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = bindParams(stmt, params, 1);
            stmt.setInt(idx++, pagination.getOffset());
            stmt.setInt(idx, pagination.getSize());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Count products matching the given filter (no pagination).
     */
    public int countProducts(ProductFilterDTO filter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM Product p ");
        sql.append("LEFT JOIN Category c ON p.CategoryID = c.CategoryID ");
        sql.append("WHERE 1 = 1 ");

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
     * Fetch single product by primary key, or null if not found.
     */
    public Product getById(int productId) {
        String sql = BASE_SELECT + "WHERE p.ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch single product by SKU (unique), or null if not found.
     */
    public Product getBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return null;
        }
        String sql = BASE_SELECT + "WHERE p.SKU = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sku.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert a new product. CreatedAt is left to the database default.
     *
     * @return true when exactly one row was inserted
     */
    public boolean insert(Product product) {
        String sql = "INSERT INTO Product (CategoryID, Name, SKU, Price, CostPrice, StockAlertQty, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, product.getCategoryId());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getSku());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setBigDecimal(5, product.getCostPrice());
            stmt.setInt(6, product.getStockAlertQty());
            stmt.setString(7, product.getStatus());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update mutable product fields (SKU and CreatedAt remain immutable here).
     */
    public boolean update(Product product) {
        String sql = "UPDATE Product SET Name = ?, CategoryID = ?, Price = ?, CostPrice = ?, " +
                     "StockAlertQty = ?, Status = ? WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setBigDecimal(4, product.getCostPrice());
            stmt.setInt(5, product.getStockAlertQty());
            stmt.setString(6, product.getStatus());
            stmt.setInt(7, product.getProductId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Soft delete by flipping status to inactive.
     */
    public boolean softDelete(int productId) {
        String sql = "UPDATE Product SET Status = ? WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Product.STATUS_INACTIVE);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Quick autocomplete search on Name or SKU. Returns at most {@code limit} matches.
     */
    public List<Product> searchByKeyword(String keyword, int limit) {
        List<Product> products = new ArrayList<>();
        if (limit <= 0) {
            return products;
        }
        String sql = "SELECT p.ProductID, p.CategoryID, p.Name AS ProductName, p.SKU, p.Price, p.CostPrice, " +
                     "p.StockAlertQty, p.Status, p.CreatedAt, c.Name AS CategoryName " +
                     "FROM Product p " +
                     "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
                     "WHERE p.Name LIKE ? OR p.SKU LIKE ? " +
                     "ORDER BY p.Name " +
                     "LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String kw = (keyword == null) ? "" : keyword.trim();
            String pattern = "%" + kw + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setInt(3, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void appendFilterClauses(StringBuilder sql, List<Object> params, ProductFilterDTO filter) {
        if (filter == null) {
            return;
        }
        if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            sql.append("AND (p.Name LIKE ? OR p.SKU LIKE ?) ");
            String pattern = "%" + filter.getKeyword().trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (filter.getCategoryId() != null) {
            sql.append("AND p.CategoryID = ? ");
            params.add(filter.getCategoryId());
        }
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            sql.append("AND p.Status = ? ");
            params.add(filter.getStatus().trim());
        }
    }

    private int bindParams(PreparedStatement stmt, List<Object> params, int startIndex) throws SQLException {
        int idx = startIndex;
        for (Object value : params) {
            stmt.setObject(idx++, value);
        }
        return idx;
    }

    private String resolveOrderBy(ProductFilterDTO filter) {
        String sortBy = (filter == null || filter.getSortBy() == null) ? "createdAt" : filter.getSortBy();
        if (!SORTABLE_COLUMNS.contains(sortBy)) {
            sortBy = "createdAt";
        }
        String column;
        switch (sortBy) {
            case "name":           column = "p.Name"; break;
            case "sku":            column = "p.SKU"; break;
            case "price":          column = "p.Price"; break;
            case "costPrice":      column = "p.CostPrice"; break;
            case "stockAlertQty":  column = "p.StockAlertQty"; break;
            case "status":         column = "p.Status"; break;
            case "createdAt":
            default:               column = "p.CreatedAt"; break;
        }
        String dir = (filter != null && "ASC".equalsIgnoreCase(filter.getSortDir())) ? "ASC" : "DESC";
        return column + " " + dir;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("ProductID"));
        product.setCategoryId(rs.getInt("CategoryID"));
        product.setCategoryName(rs.getString("CategoryName"));
        product.setProductName(rs.getString("ProductName"));
        product.setSku(rs.getString("SKU"));
        product.setPrice(rs.getBigDecimal("Price"));
        product.setCostPrice(rs.getBigDecimal("CostPrice"));
        product.setStockAlertQty(rs.getInt("StockAlertQty"));
        product.setStatus(rs.getString("Status"));
        product.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return product;
    }
}
