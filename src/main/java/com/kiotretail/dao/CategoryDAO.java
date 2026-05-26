package com.kiotretail.dao;

import com.kiotretail.model.Category;
import com.kiotretail.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Category DAO
 * Data Access Object cho nhóm hàng theo schema DBFinora.
 */
public class CategoryDAO {

    public List<Category> getCategories(String keyword, String status, String parentName, int page, int limit) {
        List<Category> categories = new ArrayList<>();
        int offset = (page - 1) * limit;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.CategoryID, c.Name, c.Description, c.ParentID, c.Status, ");
        sql.append("p.Name AS ParentName, COUNT(pr.ProductID) AS ProductCount ");
        sql.append("FROM Category c ");
        sql.append("LEFT JOIN Category p ON c.ParentID = p.CategoryID ");
        sql.append("LEFT JOIN Product pr ON c.CategoryID = pr.CategoryID ");
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (c.Name LIKE ? OR c.Description LIKE ?) ");
            String pattern = "%" + keyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND c.Status = ? ");
            params.add(status.trim());
        }
        if (parentName != null && !parentName.trim().isEmpty()) {
            if (isRootParent(parentName)) {
                sql.append("AND c.ParentID IS NULL ");
            } else {
                sql.append("AND p.Name = ? ");
                params.add(parentName.trim());
            }
        }

        sql.append("GROUP BY c.CategoryID, c.Name, c.Description, c.ParentID, c.Status, p.Name ");
        sql.append("ORDER BY c.CategoryID ASC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setParameters(stmt, params);
            stmt.setInt(params.size() + 1, offset);
            stmt.setInt(params.size() + 2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(extractCategory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Category> getAllCategories() {
        return getCategories(null, null, null, 1, Integer.MAX_VALUE);
    }

    public List<Category> getActiveCategories() {
        return getCategories(null, "active", null, 1, Integer.MAX_VALUE);
    }

    public int countCategories(String keyword, String status, String parentName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) FROM Category c ");
        sql.append("LEFT JOIN Category p ON c.ParentID = p.CategoryID ");
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (c.Name LIKE ? OR c.Description LIKE ?) ");
            String pattern = "%" + keyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND c.Status = ? ");
            params.add(status.trim());
        }
        if (parentName != null && !parentName.trim().isEmpty()) {
            if (isRootParent(parentName)) {
                sql.append("AND c.ParentID IS NULL ");
            } else {
                sql.append("AND p.Name = ? ");
                params.add(parentName.trim());
            }
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT c.CategoryID, c.Name, c.Description, c.ParentID, c.Status, " +
                "p.Name AS ParentName, COUNT(pr.ProductID) AS ProductCount " +
                "FROM Category c " +
                "LEFT JOIN Category p ON c.ParentID = p.CategoryID " +
                "LEFT JOIN Product pr ON c.CategoryID = pr.CategoryID " +
                "WHERE c.CategoryID = ? " +
                "GROUP BY c.CategoryID, c.Name, c.Description, c.ParentID, c.Status, p.Name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCategory(Category category) {
        String sql = "INSERT INTO Category (Name, Description, ParentID, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            setNullableInt(stmt, 3, category.getParentId());
            stmt.setString(4, category.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE Category SET Name = ?, Description = ?, ParentID = ?, Status = ? WHERE CategoryID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            setNullableInt(stmt, 3, category.getParentId());
            stmt.setString(4, category.getStatus());
            stmt.setInt(5, category.getCategoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsById(int categoryId) {
        String sql = "SELECT CategoryID FROM Category WHERE CategoryID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCategoryNameExists(String name, Integer excludeCategoryId) {
        StringBuilder sql = new StringBuilder("SELECT CategoryID FROM Category WHERE LOWER(LTRIM(RTRIM(Name))) = LOWER(LTRIM(RTRIM(?)))");
        if (excludeCategoryId != null) {
            sql.append(" AND CategoryID <> ?");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, name);
            if (excludeCategoryId != null) {
                stmt.setInt(2, excludeCategoryId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getCategoryIdByName(String name) {
        String sql = "SELECT CategoryID FROM Category WHERE LOWER(LTRIM(RTRIM(Name))) = LOWER(LTRIM(RTRIM(?)))";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CategoryID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isDescendant(int categoryId, int candidateParentId) {
        String sql = "WITH CategoryTree AS (" +
                " SELECT CategoryID, ParentID FROM Category WHERE ParentID = ?" +
                " UNION ALL" +
                " SELECT c.CategoryID, c.ParentID FROM Category c" +
                " INNER JOIN CategoryTree ct ON c.ParentID = ct.CategoryID" +
                ") SELECT CategoryID FROM CategoryTree WHERE CategoryID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, candidateParentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setParameters(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(index, value);
        }
    }

    private Category extractCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("CategoryID"));
        category.setName(rs.getString("Name"));
        category.setDescription(rs.getString("Description"));
        int parentId = rs.getInt("ParentID");
        category.setParentId(rs.wasNull() ? null : parentId);
        category.setParentName(rs.getString("ParentName"));
        category.setStatus(rs.getString("Status"));
        category.setProductCount(rs.getInt("ProductCount"));
        return category;
    }

    private boolean isRootParent(String parentName) {
        String trimmed = parentName == null ? "" : parentName.trim();
        return trimmed.isEmpty() || "goc".equalsIgnoreCase(trimmed) || "gốc".equalsIgnoreCase(trimmed);
    }
}
