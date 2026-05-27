package com.kiotretail.controller;

import com.kiotretail.dao.EmployeeDAO;
import com.kiotretail.model.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Login Servlet Xử lý đăng nhập
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

        // Đổi tên biến từ username thành identifier để hợp logic (Email/Phone)
        String identifier = request.getParameter("username");
        String password = request.getParameter("password");
        boolean rememberMe = request.getParameter("remember-me") != null;

        // Validate input
        if (identifier == null || identifier.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin đăng nhập");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Xác thực bằng Email hoặc Phone thông qua hàm DAO đã cập nhật
        Employee employee = employeeDAO.login(identifier, password);

        if (employee != null) {
            // Đăng nhập thành công (Giữ nguyên logic session của bạn)
            HttpSession session = request.getSession();
            session.setAttribute("employee", employee);
            session.setAttribute("employeeId", employee.getEmployeeId());
            session.setAttribute("employeeName", employee.getFullName());
            session.setAttribute("roleName", employee.getRoleName());
            session.setAttribute("branchName", employee.getBranchName());

            if (rememberMe) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            }

            response.sendRedirect(request.getContextPath() + "/role-selection");
        } else {
            // Đăng nhập thất bại
            request.setAttribute("error", "Email/Số điện thoại hoặc mật khẩu không đúng");
            request.setAttribute("username", identifier); // Đẩy lại dữ liệu cũ về input
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
