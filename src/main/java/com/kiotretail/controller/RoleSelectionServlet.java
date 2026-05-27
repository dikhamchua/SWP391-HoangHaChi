package com.kiotretail.controller;

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

        if ("pos".equals(selectedRole)) {
            response.sendRedirect(request.getContextPath() + "/pos/sale");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }
}
