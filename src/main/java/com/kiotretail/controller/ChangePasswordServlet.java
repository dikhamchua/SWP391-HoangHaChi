package com.kiotretail.controller;

import com.kiotretail.dao.EmployeeDAO;
import com.kiotretail.model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra xem có tài khoản nào đang trong trạng thái đăng nhập/kích hoạt không
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employee") == null) {
            // Nếu không có ai đăng nhập, đá về trang login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Hiển thị giao diện đổi mật khẩu
        request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employee") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy thông tin ID của nhân viên mới được tạo từ session
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        int employeeId = currentEmployee.getEmployeeId();

        // Lấy mật khẩu cũ (do người dùng check mail nhập vào) và mật khẩu mới từ Form
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");

        if (currentPassword == null || currentPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ các trường thông tin!");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        // Validate Regex bảo mật của mật khẩu mới (Ít nhất 6 ký tự, gồm chữ, số, ký tự đặc biệt)
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$";
        if (!newPassword.matches(passwordRegex)) {
            request.setAttribute("error", "Mật khẩu mới không đúng định dạng bảo mật yêu cầu!");
            request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
            return;
        }

        // Gọi cập nhật xuống Database (Dạng thô không băm theo mong muốn của bạn)
        boolean isSuccess = employeeDAO.updatePassword(employeeId, currentPassword.trim(), newPassword.trim());

        if (isSuccess) {
            request.setAttribute("successMessage", "Kích hoạt và đổi mật khẩu tài khoản thành công!");
            request.setAttribute("isChanged", true);
            
            // Xóa toàn bộ Session tạm thời để bắt buộc họ phải đăng nhập bằng mật khẩu mới
            session.invalidate(); 
        } else {
            request.setAttribute("error", "Mật khẩu tạm thời (được gửi trong Mail) nhập vào không chính xác!");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(request, response);
    }
}