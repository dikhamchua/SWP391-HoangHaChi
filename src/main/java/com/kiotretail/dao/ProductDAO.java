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
        String sql = "SELECT p.*, c.category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "ORDER BY p.created_at DESC " +
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
        String sql = "SELECT p.*, c.category_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE p.product_id = ?";

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
        String sql = "SELECT p.*, c.category_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE p.product_code LIKE ? OR p.product_name LIKE ? " +
                     "ORDER BY p.product_name";

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
        String sql = "SELECT p.*, c.category_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE p.category_id = ? AND p.status = 'active' " +
                     "ORDER BY p.product_name";

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
        String sql = "INSERT INTO products (product_code, product_name, category_id, unit, " +
                     "cost_price, selling_price, stock_quantity, min_stock, max_stock, image_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getProductName());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getUnit());
            stmt.setBigDecimal(5, product.getCostPrice());
            stmt.setBigDecimal(6, product.getSellingPrice());
            stmt.setInt(7, product.getStockQuantity());
            stmt.setInt(8, product.getMinStock());
            stmt.setInt(9, product.getMaxStock());
            stmt.setString(10, product.getImageUrl());
            stmt.setString(11, product.getStatus());

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
        String sql = "UPDATE products SET product_name = ?, category_id = ?, unit = ?, " +
                     "cost_price = ?, selling_price = ?, stock_quantity = ?, min_stock = ?, " +
                     "max_stock = ?, image_url = ?, status = ? WHERE product_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setString(3, product.getUnit());
            stmt.setBigDecimal(4, product.getCostPrice());
            stmt.setBigDecimal(5, product.getSellingPrice());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setInt(7, product.getMinStock());
            stmt.setInt(8, product.getMaxStock());
            stmt.setString(9, product.getImageUrl());
            stmt.setString(10, product.getStatus());
            stmt.setInt(11, product.getProductId());

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
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";

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
        String sql = "UPDATE products SET status = 'inactive' WHERE product_id = ?";

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
        product.setProductId(rs.getInt("product_id"));
        product.setProductCode(rs.getString("product_code"));
        product.setProductName(rs.getString("product_name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setUnit(rs.getString("unit"));
        product.setCostPrice(rs.getBigDecimal("cost_price"));
        product.setSellingPrice(rs.getBigDecimal("selling_price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setMinStock(rs.getInt("min_stock"));
        product.setMaxStock(rs.getInt("max_stock"));
        product.setImageUrl(rs.getString("image_url"));
        product.setStatus(rs.getString("status"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        return product;
    }
}
