package com.kiotretail.controller;

import com.kiotretail.dao.EmployeeDAO;
import com.kiotretail.util.EmailUtil;
import jakarta.servlet.ServletException;
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
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("send-otp".equals(action)) {
            // Đổi từ username sang fullName
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");

            if (fullName == null || fullName.trim().isEmpty()
                    || email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            // Gọi hàm kiểm tra khớp Họ tên và Email trong EmployeeDAO
            if (!employeeDAO.checkFullNameAndEmailMatch(fullName.trim(), email.trim())) {
                request.setAttribute("error", "Thông tin họ tên hoặc email không chính xác!");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            // Tạo OTP ngẫu nhiên
            Random random = new Random();
            int otpValue = 100000 + random.nextInt(900000);
            String strOtp = String.valueOf(otpValue);

            session.setAttribute("resetOtp", strOtp);
            session.setAttribute("resetEmail", email.trim());

            boolean emailSent = EmailUtil.sendOTP(email.trim(), strOtp);

            if (!emailSent) {
                request.setAttribute("error", "Không thể gửi OTP qua email!");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp")
                        .forward(request, response);
                return;
            }

            request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);

        } else if ("reset-password".equals(action)) {
            String inputOtp = request.getParameter("otp");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            String sessionOtp = (String) session.getAttribute("resetOtp");
            String sessionEmail = (String) session.getAttribute("resetEmail");

            if (sessionOtp == null || sessionEmail == null) {
                request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }

            if (!inputOtp.trim().equals(sessionOtp)) {
                request.setAttribute("error", "Mã xác thực OTP không chính xác!");
                request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
    request.setAttribute("error",
            "Xác nhận mật khẩu mới không trùng khớp!");

    request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp")
            .forward(request, response);
    return;
}

// Validate độ mạnh mật khẩu
String passwordRegex =
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&._-])[A-Za-z\\d@$!%*#?&._-]{6,}$";

if (!newPassword.matches(passwordRegex)) {

    request.setAttribute("error",
            "Mật khẩu phải có ít nhất 6 ký tự, gồm chữ cái, chữ số và ký tự đặc biệt!");

    request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp")
            .forward(request, response);

    return;
}

// Cập nhật mật khẩu thật xuống DB
boolean isUpdated = employeeDAO.updatePasswordByEmail(
        sessionEmail,
        newPassword
);

if (isUpdated) {

    session.removeAttribute("resetOtp");
    session.removeAttribute("resetEmail");

    request.setAttribute("successMessage",
            "Đổi mật khẩu thành công! Vui lòng đăng nhập.");

    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
            .forward(request, response);

} else {

    request.setAttribute("error",
            "Lỗi hệ thống không thể cập nhật cơ sở dữ liệu!");

    request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp")
            .forward(request, response);
}
        }
    }
}
