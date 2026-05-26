package com.kiotretail.controller;

import com.kiotretail.util.RolePermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Role Selection Servlet
 * Xử lý chọn vai trò sau khi đăng nhập
 */
public class RoleSelectionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/role-selection.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String selectedRole = request.getParameter("role");
        String roleName = (String) request.getSession().getAttribute("roleName");

        if ("pos".equals(selectedRole)) {
            if (!RolePermissionUtil.canAccessPos(roleName)) {
                request.getSession().setAttribute("message", "Vai trò hiện tại không có quyền truy cập POS.");
                request.getSession().setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/role-selection");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/pos/sale");
        } else {
            if (!RolePermissionUtil.canAccessManagementArea(roleName)) {
                request.getSession().setAttribute("message", "Vai trò hiện tại không có quyền truy cập khu vực quản lý.");
                request.getSession().setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/role-selection");
                return;
            }
            if (RolePermissionUtil.canViewCategory(roleName)) {
                response.sendRedirect(request.getContextPath() + "/admin/categories");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }
}
