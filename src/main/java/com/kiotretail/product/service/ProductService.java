package com.kiotretail.product.service;

import com.kiotretail.product.dao.CategoryDAO;
import com.kiotretail.product.dao.ProductDAO;
import com.kiotretail.product.dto.ProductFilterDTO;
import com.kiotretail.product.model.Product;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic layer for Products.
 * Coordinates ProductDAO and CategoryDAO with input validation and existence checks.
 */
public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    /**
     * List products with filter + pagination, wrapped as PageResult.
     */
    public PageResult<Product> listProducts(ProductFilterDTO filter, Pagination pagination) {
        List<Product> items = productDAO.getProducts(filter, pagination);
        int total = productDAO.countProducts(filter);
        return PageResult.of(items, total, pagination);
    }

    /**
     * Fetch a product by id or throw NotFoundException.
     */
    public Product getProductById(int id) {
        Product product = productDAO.getById(id);
        if (product == null) {
            throw new NotFoundException("Product", id);
        }
        return product;
    }

    /**
     * Fetch a product by SKU or throw NotFoundException.
     */
    public Product getProductBySku(String sku) {
        Product product = productDAO.getBySku(sku);
        if (product == null) {
            throw new NotFoundException("Product", sku);
        }
        return product;
    }

    /**
     * Validate, ensure SKU uniqueness and category existence, then insert.
     */
    public boolean createProduct(Product product) {
        validateProduct(product);

        Product existingBySku = productDAO.getBySku(product.getSku());
        if (existingBySku != null) {
            Map<String, String> errors = new HashMap<>();
            errors.put("sku", "SKU already exists: " + product.getSku());
            throw new ValidationException(errors);
        }

        if (categoryDAO.getById(product.getCategoryId()) == null) {
            Map<String, String> errors = new HashMap<>();
            errors.put("categoryId", "Category does not exist: " + product.getCategoryId());
            throw new ValidationException(errors);
        }

        if (product.getStatus() == null || product.getStatus().trim().isEmpty()) {
            product.setStatus(Product.STATUS_ACTIVE);
        }

        return productDAO.insert(product);
    }

    /**
     * Validate, ensure SKU uniqueness (excluding self) and category existence, then update.
     */
    public boolean updateProduct(Product product) {
        validateProduct(product);

        Product existingBySku = productDAO.getBySku(product.getSku());
        if (existingBySku != null && existingBySku.getProductId() != product.getProductId()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("sku", "SKU already exists: " + product.getSku());
            throw new ValidationException(errors);
        }

        if (categoryDAO.getById(product.getCategoryId()) == null) {
            Map<String, String> errors = new HashMap<>();
            errors.put("categoryId", "Category does not exist: " + product.getCategoryId());
            throw new ValidationException(errors);
        }

        return productDAO.update(product);
    }

    /**
     * Soft-delete (status flipped to inactive) a product.
     */
    public boolean deleteProduct(int productId) {
        return productDAO.softDelete(productId);
    }

    /**
     * Quick keyword search delegating to DAO.
     */
    public List<Product> searchProducts(String keyword, int limit) {
        return productDAO.searchByKeyword(keyword, limit);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void validateProduct(Product product) {
        Map<String, String> errors = new HashMap<>();
        if (product == null) {
            errors.put("product", "Product must not be null");
            throw new ValidationException(errors);
        }
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            errors.put("name", "Product name must not be empty");
        }
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            errors.put("sku", "SKU must not be empty");
        }
        if (product.getCategoryId() <= 0) {
            errors.put("categoryId", "categoryId must be > 0");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("price", "price must be >= 0");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
