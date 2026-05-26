package com.kiotretail.controller;

import com.kiotretail.dao.CategoryDAO;
import com.kiotretail.model.Category;
import com.kiotretail.util.RolePermissionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Category Servlet
 * Xử lý danh sách, thêm và cập nhật nhóm hàng.
 */
public class CategoryServlet extends HttpServlet {

    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listCategories(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            addCategory(request, response);
        } else if ("update".equals(action)) {
            updateCategory(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!canViewCategory(request)) {
            setFlash(request, "Bạn không có quyền xem danh sách nhóm hàng.", "danger");
            response.sendRedirect(request.getContextPath() + "/role-selection");
            return;
        }

        String keyword = trimToNull(request.getParameter("keyword"));
        String status = normalizeStatus(request.getParameter("status"));
        String parentNameFilter = trimToNull(request.getParameter("parentName"));

        int page = 1;
        int limit = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Category> categories = categoryDAO.getCategories(keyword, status, parentNameFilter, page, limit);
        List<Category> parentOptions = categoryDAO.getActiveCategories();
        int totalItems = categoryDAO.countCategories(keyword, status, parentNameFilter);
        int totalPages = (int) Math.ceil((double) totalItems / limit);

        request.setAttribute("categories", categories);
        request.setAttribute("parentOptions", parentOptions);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("parentNameFilter", parentNameFilter);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.getRequestDispatcher("/WEB-INF/views/admin/categories.jsp").forward(request, response);
    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!canManageCategory(request)) {
            setFlash(request, "Bạn không có quyền thêm nhóm hàng.", "danger");
            response.sendRedirect(request.getContextPath() + "/admin/categories");
            return;
        }

        try {
            Category category = buildCategoryFromRequest(request, false);
            String validationError = validateCategory(category, null);
            if (validationError != null) {
                setFlash(request, validationError, "danger");
            } else if (categoryDAO.addCategory(category)) {
                setFlash(request, "Thêm nhóm hàng thành công!", "success");
            } else {
                setFlash(request, "Thêm nhóm hàng thất bại!", "danger");
            }
        } catch (Exception e) {
            setFlash(request, "Lỗi: " + e.getMessage(), "danger");
        }
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!canManageCategory(request)) {
            setFlash(request, "Bạn không có quyền cập nhật nhóm hàng.", "danger");
            response.sendRedirect(request.getContextPath() + "/admin/categories");
            return;
        }

        try {
            Category category = buildCategoryFromRequest(request, true);
            Integer oldParentId = null;
            Category existing = categoryDAO.getCategoryById(category.getCategoryId());
            if (existing != null) {
                oldParentId = existing.getParentId();
            }
            String validationError = validateCategory(category, category.getCategoryId());
            if (validationError != null) {
                setFlash(request, validationError, "danger");
            } else if (!categoryDAO.existsById(category.getCategoryId())) {
                setFlash(request, "Nhóm hàng không tồn tại.", "danger");
            } else if (category.getParentId() != null && category.getParentId() == category.getCategoryId()) {
                setFlash(request, "Nhóm hàng không thể là nhóm cha của chính nó.", "danger");
            } else if (category.getParentId() != null && categoryDAO.isDescendant(category.getCategoryId(), category.getParentId())) {
                setFlash(request, "Không thể chọn nhóm con làm nhóm cha vì sẽ tạo vòng lặp danh mục.", "danger");
            } else if (oldParentId != null && category.getParentId() != null && oldParentId.equals(category.getParentId())) {
                setFlash(request, "Nhóm cha không thay đổi.", "warning");
            } else if (categoryDAO.updateCategory(category)) {
                setFlash(request, "Cập nhật nhóm hàng thành công!", "success");
            } else {
                setFlash(request, "Cập nhật nhóm hàng thất bại!", "danger");
            }
        } catch (Exception e) {
            setFlash(request, "Lỗi: " + e.getMessage(), "danger");
        }
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private Category buildCategoryFromRequest(HttpServletRequest request, boolean includeId) {
        Category category = new Category();
        if (includeId) {
            category.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        }
        category.setName(trimToNull(request.getParameter("name")));
        category.setDescription(trimToNull(request.getParameter("description")));
        String parentName = trimToNull(request.getParameter("parentName"));
        category.setParentId(resolveParentId(parentName));
        String status = normalizeStatus(request.getParameter("status"));
        category.setStatus(status == null ? "active" : status);
        return category;
    }

    private String validateCategory(Category category, Integer excludeCategoryId) {
        if (category.getName() == null) {
            return "Tên nhóm hàng không được để trống.";
        }
        if (category.getName().length() > 255) {
            return "Tên nhóm hàng không được vượt quá 255 ký tự.";
        }
        if (category.getDescription() != null && category.getDescription().length() > 1000) {
            return "Mô tả không được vượt quá 1000 ký tự.";
        }
        if (!"active".equals(category.getStatus()) && !"inactive".equals(category.getStatus())) {
            return "Trạng thái nhóm hàng không hợp lệ.";
        }
        if (category.getParentId() != null && !categoryDAO.existsById(category.getParentId())) {
            return "Nhóm cha không tồn tại.";
        }
        if (categoryDAO.isCategoryNameExists(category.getName(), excludeCategoryId)) {
            return "Tên nhóm hàng đã tồn tại.";
        }
        return null;
    }

    private boolean canManageCategory(HttpServletRequest request) {
        Object role = request.getSession().getAttribute("roleName");
        return role != null && RolePermissionUtil.canManageCategory(role.toString());
    }

    private boolean canViewCategory(HttpServletRequest request) {
        Object role = request.getSession().getAttribute("roleName");
        return role != null && RolePermissionUtil.canViewCategory(role.toString());
    }

    private Integer parseOptionalInt(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        return Integer.parseInt(trimmed);
    }

    private String normalizeStatus(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        if ("active".equalsIgnoreCase(trimmed)) {
            return "active";
        }
        if ("inactive".equalsIgnoreCase(trimmed)) {
            return "inactive";
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void setFlash(HttpServletRequest request, String message, String type) {
        request.getSession().setAttribute("message", message);
        request.getSession().setAttribute("messageType", type);
    }

    private Integer resolveParentId(String parentName) {
        if (parentName == null || parentName.trim().isEmpty() || "goc".equalsIgnoreCase(parentName) || "gốc".equalsIgnoreCase(parentName)) {
            return null;
        }
        Integer parentId = categoryDAO.getCategoryIdByName(parentName);
        if (parentId != null) {
            return parentId;
        }
        Category parent = new Category();
        parent.setName(parentName.trim());
        parent.setDescription(null);
        parent.setParentId(null);
        parent.setStatus("active");
        boolean created = categoryDAO.addCategory(parent);
        return created ? categoryDAO.getCategoryIdByName(parentName) : null;
    }
}
