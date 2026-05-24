package com.kiotretail.controller;

import com.kiotretail.dao.EmployeeDAO;
import com.kiotretail.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

public class ForgotPasswordServlet extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Mặc định chuyển đến trang nhập email quên mật khẩu
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("send-otp".equals(action)) {
            String email = request.getParameter("email");

            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng nhập địa chỉ Email!");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            // 1. Kiểm tra xem Email có tồn tại trong Database không
            if (!employeeDAO.checkEmailExists(email.trim())) {
                request.setAttribute("error", "Email này không tồn tại trên hệ thống!");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            // 2. Tạo mã OTP ngẫu nhiên gồm 6 chữ số
            Random random = new Random();
            int otpValue = 100000 + random.nextInt(900000);
            String strOtp = String.valueOf(otpValue);

            // 3. Lưu OTP và Email vào Session để xác thực ở bước sau
            session.setAttribute("resetOtp", strOtp);
            session.setAttribute("resetEmail", email.trim());

            // 4. IN MÃ OTP RA CONSOLE NETBEANS ĐỂ LẤY TEST NHANH CHÓNG
            System.out.println("==========================================");
            System.out.println("[KIOTRETAIL OTP]: Ma xac thuc cho " + email + " la: " + strOtp);
            System.out.println("==========================================");

            // Chuyển sang màn hình nhập OTP và mật khẩu mới
            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);

        } else if ("reset-password".equals(action)) {
            String inputOtp = request.getParameter("otp");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            String sessionOtp = (String) session.getAttribute("resetOtp");
            String sessionEmail = (String) session.getAttribute("resetEmail");

            // Kiểm tra tính hợp lệ của phiên làm việc
            if (sessionOtp == null || sessionEmail == null) {
                request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng yêu cầu lại mã OTP.");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            // Kiểm tra tính đầy đủ đầu vào
            if (inputOtp == null || newPassword == null || confirmPassword == null || inputOtp.isEmpty() || newPassword.isEmpty()) {
                request.setAttribute("error", "Vui lòng điền đầy đủ tất cả các trường!");
                request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
                return;
            }

            // 1. Kiểm tra mã OTP nhập vào có khớp không
            if (!inputOtp.trim().equals(sessionOtp)) {
                request.setAttribute("error", "Mã xác thực OTP không chính xác!");
                request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
                return;
            }

            // 2. Kiểm tra độ dài mật khẩu mới
            if (newPassword.length() < 6) {
                request.setAttribute("error", "Mật khẩu mới phải có độ dài tối thiểu từ 6 ký tự!");
                request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
                return;
            }

            // 3. Kiểm tra mật khẩu gõ lại có khớp không
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "Xác nhận mật khẩu mới không trùng khớp!");
                request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
                return;
            }

            // 4. Mã hóa mật khẩu mới theo chuẩn SHA-256 giống trang Đăng ký
            String encryptedPassword = PasswordUtil.hashPassword(newPassword);

            // 5. Cập nhật vào DB
            boolean isUpdated = employeeDAO.updatePasswordByEmail(sessionEmail, encryptedPassword);

            if (isUpdated) {
                // Xóa các thông tin OTP tạm thời trong session
                session.removeAttribute("resetOtp");
                session.removeAttribute("resetEmail");

                request.setAttribute("successMessage", "Đổi mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Lỗi hệ thống không thể cập nhật cơ sở dữ liệu!");
                request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
            }
        }
    }
}