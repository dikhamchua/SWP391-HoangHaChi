package com.kiotretail.product.service;

import com.kiotretail.product.dao.CategoryDAO;
import com.kiotretail.product.model.Category;
import com.kiotretail.shared.base.BaseService;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;

import java.util.List;

/**
 * CategoryService
 * Business logic layer for Category management. Orchestrates validation rules
 * and CategoryDAO operations.
 */
public class CategoryService extends BaseService {

    private static final String ENTITY_NAME = "Category";
    private static final String DEFAULT_STATUS = AppConstants.STATUS_ACTIVE;

    private final CategoryDAO categoryDAO = new CategoryDAO();

    /**
     * Lists categories with optional filters and pagination.
     */
    public PageResult<Category> listCategories(String keyword, String status, Integer parentId, Pagination pagination) {
        List<Category> items = categoryDAO.getCategories(keyword, status, parentId, pagination);
        int total = categoryDAO.countCategories(keyword, status, parentId);
        return PageResult.of(items, total, pagination);
    }

    /**
     * Returns active categories ordered by name. Useful for dropdowns and selectors.
     */
    public List<Category> getActiveCategories() {
        return categoryDAO.getActiveCategories();
    }

    /**
     * Loads a category by id. Throws NotFoundException if missing.
     */
    public Category getCategoryById(int id) {
        Category category = categoryDAO.getById(id);
        if (category == null) {
            throw new NotFoundException(ENTITY_NAME, id);
        }
        return category;
    }

    /**
     * Creates a new category. Validates name presence, name uniqueness, and parent existence.
     */
    public boolean createCategory(Category category) {
        if (category == null) {
            throw new ValidationException("Category must not be null");
        }

        validateName(category.getName());

        String trimmedName = category.getName().trim();
        category.setName(trimmedName);

        if (categoryDAO.existsByName(trimmedName, null)) {
            throw new ValidationException("Category name already exists: " + trimmedName);
        }

        Integer parentId = category.getParentId();
        if (parentId != null && parentId > 0) {
            Category parent = categoryDAO.getById(parentId);
            if (parent == null) {
                throw new ValidationException("Parent category not found: " + parentId);
            }
        } else {
            category.setParentId(null);
        }

        if (category.getStatus() == null || category.getStatus().trim().isEmpty()) {
            category.setStatus(DEFAULT_STATUS);
        }

        return categoryDAO.insert(category);
    }

    /**
     * Updates an existing category. Validates name, uniqueness (excluding self), and
     * prevents circular parent assignment.
     */
    public boolean updateCategory(Category category) {
        if (category == null) {
            throw new ValidationException("Category must not be null");
        }

        int categoryId = category.getCategoryId();
        if (categoryId <= 0) {
            throw new ValidationException("Category id is required for update");
        }

        validateName(category.getName());

        String trimmedName = category.getName().trim();
        category.setName(trimmedName);

        if (categoryDAO.existsByName(trimmedName, categoryId)) {
            throw new ValidationException("Category name already exists: " + trimmedName);
        }

        Integer parentId = category.getParentId();
        if (parentId != null && parentId > 0) {
            if (parentId == categoryId) {
                throw new ValidationException("A category cannot be its own parent");
            }
            Category parent = categoryDAO.getById(parentId);
            if (parent == null) {
                throw new ValidationException("Parent category not found: " + parentId);
            }
            if (categoryDAO.isDescendant(categoryId, parentId)) {
                throw new ValidationException("Parent assignment would create a circular reference");
            }
        } else {
            category.setParentId(null);
        }

        if (category.getStatus() == null || category.getStatus().trim().isEmpty()) {
            category.setStatus(DEFAULT_STATUS);
        }

        return categoryDAO.update(category);
    }

    public boolean deleteCategory(int categoryId) {
        Category category = categoryDAO.getById(categoryId);
        if (category == null) {
            throw new NotFoundException(ENTITY_NAME, categoryId);
        }
        boolean deleted = categoryDAO.delete(categoryId);
        if (!deleted) {
            throw new ValidationException("Không thể xóa danh mục đang có sản phẩm");
        }
        return true;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name is required");
        }
    }
}
