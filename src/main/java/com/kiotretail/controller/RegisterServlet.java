package com.kiotretail.controller;

import com.kiotretail.dao.EmployeeDAO;
import com.kiotretail.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng người dùng sang trang điền thông tin đăng ký tài khoản
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đọc dữ liệu từ form gửi lên
        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // 1. Kiểm tra xem các trường có được nhập đầy đủ không
        if (fullName == null || fullName.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {
            
            request.setAttribute("error", "Vui lòng điền đầy đủ tất cả các trường thông tin!");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        // 2. Kiểm tra độ mạnh mật khẩu (Yêu cầu tối thiểu từ 6 ký tự)
        if (password.length() < 6) {
            request.setAttribute("error", "Mật khẩu quá ngắn! Yêu cầu tối thiểu phải từ 6 ký tự trở lên.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        // 3. Kiểm tra xem Password và Confirm Password có khớp nhau không
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không trùng khớp! Vui lòng nhập lại.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        // 4. Kiểm tra xem Username đã tồn tại trong DB chưa
        if (employeeDAO.checkUsernameExists(username.trim())) {
            request.setAttribute("error", "Tên đăng nhập (Username) này đã được đăng ký bởi người khác!");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        // 5. Kiểm tra xem Email đã được sử dụng chưa
        if (employeeDAO.checkEmailExists(email.trim())) {
            request.setAttribute("error", "Địa chỉ Email này đã được sử dụng hệ thống!");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        // --- NẾU TẤT CẢ ĐỀU ĐÚNG: THỰC HIỆN ĐĂNG KÝ VÀ LƯU DỮ LIỆU ---
        
        // Mã hóa mật khẩu thô sang chuỗi băm bảo mật SHA-256
        String encryptedPassword = PasswordUtil.hashPassword(password);
        
        // Thiết lập tham số mặc định theo yêu cầu của bạn:
        int defaultRoleId = 3;       // Quyền nhân viên mặc định (RoleID = 3)
        String defaultStatus = "active"; // Trạng thái hoạt động tức thì cho tài khoản mới

        boolean isSuccess = employeeDAO.registerEmployee(
                fullName.trim(), username.trim(), email.trim(), encryptedPassword, defaultRoleId, defaultStatus
        );

        if (isSuccess) {
            // Thông báo đăng ký thành công và chuyển hướng sang màn hình đăng nhập
            request.setAttribute("successMessage", "Đăng ký thành công! Vui lòng thực hiện đăng nhập.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        } else {
            // Trường hợp lỗi kết nối hoặc DB bất ngờ
            request.setAttribute("error", "Đã xảy ra sự cố hệ thống trong quá trình xử lý dữ liệu. Hãy thử lại!");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}