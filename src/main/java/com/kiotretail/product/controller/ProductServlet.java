package com.kiotretail.product.controller;

import com.kiotretail.product.dto.ProductFilterDTO;
import com.kiotretail.product.model.Category;
import com.kiotretail.product.model.Product;
import com.kiotretail.product.service.CategoryService;
import com.kiotretail.product.service.ProductService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Product Servlet
 * Service-layer based controller for product management.
 * Replaces the legacy {@code com.kiotretail.controller.ProductServlet}.
 */
public class ProductServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private ProductService productService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, AppConstants.PARAM_ACTION, "list");

        try {
            switch (action) {
                case "view":
                    handleView(request, response);
                    break;
                case "create":
                    handleCreateForm(request, response);
                    break;
                case "edit":
                    handleEditForm(request, response);
                    break;
                case "list":
                default:
                    handleList(request, response);
                    break;
            }
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");

        switch (action) {
            case "add":
                handleAdd(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            default:
                redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
                break;
        }
    }

    // -----------------------------------------------------------------------
    // GET handlers
    // -----------------------------------------------------------------------

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE);
        int size = getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE);
        Pagination pagination = Pagination.of(Math.max(page, 1), Math.max(Math.min(size, AppConstants.MAX_PAGE_SIZE), 1));

        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setKeyword(getStringParam(request, AppConstants.PARAM_KEYWORD, null));
        Integer categoryId = parseNullableInt(request.getParameter(AppConstants.PARAM_CATEGORY_ID));
        filter.setCategoryId(categoryId);
        filter.setStatus(getStringParam(request, AppConstants.PARAM_STATUS, null));

        PageResult<Product> pageResult = productService.listProducts(filter, pagination);
        List<Category> categories = categoryService.getActiveCategories();

        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.ATTR_CATEGORIES, categories);
        request.setAttribute(AppConstants.ATTR_FILTER, filter);

        forward(request, response, ViewPaths.PRODUCT_LIST);
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Product product = productService.getProductById(id);
        request.setAttribute(AppConstants.ATTR_PRODUCT, product);
        forward(request, response, ViewPaths.PRODUCT_DETAIL);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(AppConstants.ATTR_CATEGORIES, categoryService.getActiveCategories());
        forward(request, response, ViewPaths.PRODUCT_CREATE);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Product product = productService.getProductById(id);
        List<Category> categories = categoryService.getActiveCategories();

        request.setAttribute(AppConstants.ATTR_PRODUCT, product);
        request.setAttribute(AppConstants.ATTR_CATEGORIES, categories);
        forward(request, response, ViewPaths.PRODUCT_EDIT);
    }

    // -----------------------------------------------------------------------
    // POST handlers
    // -----------------------------------------------------------------------

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Product product = buildProductFromRequest(request, false);
            productService.createProduct(product);
            setFlashMessage(request, String.format(ErrorMessages.CREATE_SUCCESS, ErrorMessages.ENTITY_PRODUCT), AppConstants.FLASH_SUCCESS);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        } catch (ValidationException ex) {
            setFlashMessage(request, formatValidationMessage(ex), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS + "?action=create");
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int productId = getIntParam(request, "productId", 0);
        try {
            Product product = buildProductFromRequest(request, true);
            productService.updateProduct(product);
            setFlashMessage(request, String.format(ErrorMessages.UPDATE_SUCCESS, ErrorMessages.ENTITY_PRODUCT), AppConstants.FLASH_SUCCESS);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        } catch (ValidationException ex) {
            setFlashMessage(request, formatValidationMessage(ex), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS + "?action=edit&id=" + productId);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int productId = getIntParam(request, "productId", 0);
            productService.deleteProduct(productId);
            setFlashMessage(request, String.format(ErrorMessages.DELETE_SUCCESS, ErrorMessages.ENTITY_PRODUCT), AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirect(request, response, ViewPaths.REDIRECT_PRODUCTS);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Product buildProductFromRequest(HttpServletRequest request, boolean includeId) {
        Product product = new Product();

        if (includeId) {
            product.setProductId(getIntParam(request, "productId", 0));
        }

        product.setProductName(getStringParam(request, "name", ""));
        product.setSku(getStringParam(request, "sku", ""));
        product.setCategoryId(getIntParam(request, AppConstants.PARAM_CATEGORY_ID, 0));
        product.setPrice(parseBigDecimal(request.getParameter(AppConstants.PARAM_PRICE)));
        product.setCostPrice(parseBigDecimal(request.getParameter(AppConstants.PARAM_COST_PRICE)));
        product.setStockAlertQty(getIntParam(request, "stockAlertQty", 0));

        String status = getStringParam(request, AppConstants.PARAM_STATUS, AppConstants.STATUS_ACTIVE);
        product.setStatus(status);

        return product;
    }

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private Integer parseNullableInt(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String formatValidationMessage(ValidationException ex) {
        if (ex.getFieldErrors() == null || ex.getFieldErrors().isEmpty()) {
            return ex.getMessage();
        }
        StringBuilder sb = new StringBuilder();
        ex.getFieldErrors().forEach((field, message) -> {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(field).append(": ").append(message);
        });
        return sb.toString();
    }

    private void setFlashMessage(HttpServletRequest request, String message, String type) {
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, message);
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, type);
    }
}
