package com.kiotretail.product.controller;

import com.kiotretail.product.model.Category;
import com.kiotretail.product.service.CategoryService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/categories")
public class CategoryServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final String REDIRECT_LIST = "/admin/categories";
    private static final String VIEW_LIST = "product/categories.jsp";

    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        this.categoryService = new CategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, "action", "list");

        try {
            switch (action) {
                case "list":
                default:
                    handleList(request, response);
                    break;
            }
        } catch (ServiceException ex) {
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ex.getMessage());
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, "action", "");

        try {
            switch (action) {
                case "add":
                    handleAdd(request, response);
                    break;
                case "update":
                    handleUpdate(request, response);
                    break;
                case "delete":
                    int categoryId = getIntParam(request, "categoryId", 0);
                    categoryService.deleteCategory(categoryId);
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.DELETE_SUCCESS, ErrorMessages.ENTITY_CATEGORY));
                    break;
                default:
                    redirect(request, response, REDIRECT_LIST);
                    break;
            }
        } catch (ValidationException ex) {
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ex.getMessage());
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST);
        } catch (ServiceException ex) {
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ex.getMessage());
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST);
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE);
        int size = getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE);
        String keyword = getStringParam(request, AppConstants.PARAM_KEYWORD, null);
        String status = getStringParam(request, AppConstants.PARAM_STATUS, null);

        Pagination pagination = Pagination.of(Math.max(page, 1), Math.max(Math.min(size, AppConstants.MAX_PAGE_SIZE), 1));
        PageResult<Category> pageResult = categoryService.listCategories(keyword, status, null, pagination);
        List<Category> activeCategories = categoryService.getActiveCategories();

        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.ATTR_CATEGORIES, activeCategories);
        forward(request, response, VIEW_LIST);
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Category category = new Category();
        category.setName(getStringParam(request, "name", ""));
        category.setDescription(getStringParam(request, "description", ""));
        String parentIdStr = request.getParameter("parentId");
        if (parentIdStr != null && !parentIdStr.trim().isEmpty()) {
            category.setParentId(Integer.parseInt(parentIdStr.trim()));
        }
        category.setStatus(getStringParam(request, "status", AppConstants.STATUS_ACTIVE));

        categoryService.createCategory(category);
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, String.format(ErrorMessages.CREATE_SUCCESS, "nhom hang"));
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, AppConstants.FLASH_SUCCESS);
        redirect(request, response, REDIRECT_LIST);
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Category category = new Category();
        category.setCategoryId(getIntParam(request, "categoryId", 0));
        category.setName(getStringParam(request, "name", ""));
        category.setDescription(getStringParam(request, "description", ""));
        String parentIdStr = request.getParameter("parentId");
        if (parentIdStr != null && !parentIdStr.trim().isEmpty()) {
            category.setParentId(Integer.parseInt(parentIdStr.trim()));
        }
        category.setStatus(getStringParam(request, "status", AppConstants.STATUS_ACTIVE));

        categoryService.updateCategory(category);
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, String.format(ErrorMessages.UPDATE_SUCCESS, "nhom hang"));
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, AppConstants.FLASH_SUCCESS);
        redirect(request, response, REDIRECT_LIST);
    }
}
