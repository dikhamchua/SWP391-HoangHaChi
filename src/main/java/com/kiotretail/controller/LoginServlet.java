package com.kiotretail.controller;

import com.kiotretail.dao.EmployeeDAO;
import com.kiotretail.model.Employee;
import com.kiotretail.util.RolePermissionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Login Servlet
 * Xử lý đăng nhập
 */
public class LoginServlet extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean rememberMe = request.getParameter("remember-me") != null;

        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin đăng nhập");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Authenticate
        Employee employee = employeeDAO.login(username, password);

        if (employee != null) {
            // Login successful
            HttpSession session = request.getSession();
            session.setAttribute("employee", employee);
            session.setAttribute("employeeId", employee.getEmployeeId());
            session.setAttribute("employeeName", employee.getFullName());
            session.setAttribute("roleId", employee.getRoleId());
            session.setAttribute("roleName", employee.getRoleName());
            session.setAttribute("branchName", employee.getBranchName());
            session.setAttribute("canViewCategory", RolePermissionUtil.canViewCategory(employee.getRoleName()));
            session.setAttribute("canManageCategory", RolePermissionUtil.canManageCategory(employee.getRoleName()));
            session.setAttribute("canViewRoleManagement", RolePermissionUtil.canViewRoleManagement(employee.getRoleName()));
            session.setAttribute("canAccessManagementArea", RolePermissionUtil.canAccessManagementArea(employee.getRoleName()));
            session.setAttribute("canAccessPos", RolePermissionUtil.canAccessPos(employee.getRoleName()));

            if (rememberMe) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            }

            // Redirect to role selection or dashboard
            response.sendRedirect(request.getContextPath() + "/role-selection");
        } else {
            // Login failed
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
