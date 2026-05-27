package com.kiotretail.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication Filter
 * Kiểm tra đăng nhập cho các trang yêu cầu xác thực và cấu hình ngăn bộ nhớ đệm (Cache)
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    HttpSession session = httpRequest.getSession(false);

    boolean isLoggedIn = (session != null && session.getAttribute("employee") != null);
    String loginURI = httpRequest.getContextPath() + "/login";
    
    // ĐỊNH NGHĨA THÊM ĐƯỜNG DẪN TRANG ĐĂNG KÝ (Tùy theo cấu hình URL Servlet của bạn)
    String registerURI = httpRequest.getContextPath() + "/register"; // Hoặc "/admin/register"

    boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
    boolean isLoginPage = httpRequest.getRequestURI().endsWith("login.jsp");
    
    // THÊM BIẾN KIỂM TRA XEM CÓ PHẢI LÀ REQUEST VÀO TRANG ĐĂNG KÝ KHÔNG
    boolean isRegisterRequest = httpRequest.getRequestURI().equals(registerURI);

    boolean isResourceRequest = httpRequest.getRequestURI().contains("/assets/") 
            || httpRequest.getRequestURI().endsWith(".css") 
            || httpRequest.getRequestURI().endsWith(".js");

    // BỔ SUNG "isRegisterRequest" VÀO ĐIỀU KIỆN CHO PHÉP ĐI QUA KHÔNG CẦN ĐĂNG NHẬP
    if (isLoggedIn || isLoginRequest || isLoginPage || isResourceRequest || isRegisterRequest) {
        
        if (isLoggedIn && !isLoginRequest && !isLoginPage && !isResourceRequest && !isRegisterRequest) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
            httpResponse.setHeader("Pragma", "no-cache"); 
            httpResponse.setDateHeader("Expires", 0); 
        }
        
        chain.doFilter(request, response);
    } else {
        httpResponse.sendRedirect(loginURI);
    }
}

    @Override
    public void destroy() {
    }
}