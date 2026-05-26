package com.kiotretail.dao;

import com.kiotretail.model.Product;
import com.kiotretail.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Product DAO
 * Data Access Object cho hàng hóa
 */
public class ProductDAO {

    /**
     * Lấy tất cả sản phẩm
     */

    public List<Product> getAllProducts(int page, int limit) {
        int offset = (page-1)*limit;
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.CategoryID, p.Name AS ProductName, p.SKU, p.Price, p.CostPrice, " +
                "p.StockAlertQty, p.Status, p.CreatedAt, c.Name AS CategoryName " +
                "FROM Product p " +
                "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
                "ORDER BY p.CreatedAt DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offset);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();


                while (rs.next()) {
                    products.add(extractProduct(rs));
                }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public Product getProductById(int productId) {
        String sql = "SELECT p.ProductID, p.CategoryID, p.Name AS ProductName, p.SKU, p.Price, p.CostPrice, " +
                     "p.StockAlertQty, p.Status, p.CreatedAt, c.Name AS CategoryName " +
                     "FROM Product p " +
                     "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
                     "WHERE p.ProductID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tìm kiếm sản phẩm
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.CategoryID, p.Name AS ProductName, p.SKU, p.Price, p.CostPrice, " +
                     "p.StockAlertQty, p.Status, p.CreatedAt, c.Name AS CategoryName " +
                     "FROM Product p " +
                     "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
                     "WHERE p.SKU LIKE ? OR p.Name LIKE ? " +
                     "ORDER BY p.Name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Lấy sản phẩm theo danh mục
     */
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.CategoryID, p.Name AS ProductName, p.SKU, p.Price, p.CostPrice, " +
                     "p.StockAlertQty, p.Status, p.CreatedAt, c.Name AS CategoryName " +
                     "FROM Product p " +
                     "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
                     "WHERE p.CategoryID = ? AND p.Status = 'active' " +
                     "ORDER BY p.Name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Thêm sản phẩm mới
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Product (CategoryID, Name, SKU, Price, CostPrice, StockAlertQty, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getCategoryId());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getProductCode());
            stmt.setBigDecimal(4, product.getSellingPrice());
            stmt.setBigDecimal(5, product.getCostPrice());
            stmt.setInt(6, product.getMinStock());
            stmt.setString(7, product.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật sản phẩm
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE Product SET Name = ?, CategoryID = ?, Price = ?, CostPrice = ?, " +
                     "StockAlertQty = ?, Status = ? WHERE ProductID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setBigDecimal(3, product.getSellingPrice());
            stmt.setBigDecimal(4, product.getCostPrice());
            stmt.setInt(5, product.getMinStock());
            stmt.setString(6, product.getStatus());
            stmt.setInt(7, product.getProductId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật tồn kho
     */
    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE Product SET StockAlertQty = StockAlertQty + ? WHERE ProductID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa sản phẩm (soft delete)
     */
    public boolean deleteProduct(int productId) {
        String sql = "UPDATE Product SET Status = 'inactive' WHERE ProductID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extract Product từ ResultSet
     */
    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("ProductID"));
        product.setProductCode(rs.getString("SKU"));
        product.setProductName(rs.getString("ProductName"));
        product.setCategoryId(rs.getInt("CategoryID"));
        product.setCategoryName(rs.getString("CategoryName"));
        product.setCostPrice(rs.getBigDecimal("CostPrice"));
        product.setSellingPrice(rs.getBigDecimal("Price"));
        int stockAlert = rs.getInt("StockAlertQty");
        product.setMinStock(stockAlert);
        product.setStockQuantity(stockAlert);
        product.setStatus(rs.getString("Status"));
        product.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return product;
    }
}
