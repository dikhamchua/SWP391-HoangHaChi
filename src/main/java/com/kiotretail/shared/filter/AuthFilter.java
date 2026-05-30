package com.kiotretail.shared.filter;

import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication Filter
 *
 * Bao ve cac trang yeu cau dang nhap. Cho phep di qua khong can xac thuc:
 *  - Static resources (.css, .js, /assets/)
 *  - Trang login va register
 *
 * Neu chua dang nhap -> redirect ve trang login.
 * Neu da dang nhap -> set no-cache headers va cho di tiep.
 *
 * Ngoai ra, pattern /api/* cung duoc bao ve (yeu cau session ton tai).
 */
public class AuthFilter implements Filter {

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String ASSETS_PREFIX = "/assets/";
    private static final String API_PREFIX = "/api/";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No init params required.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String requestURI = httpRequest.getRequestURI();
        String path = requestURI.substring(contextPath.length());

        HttpSession session = httpRequest.getSession(false);
        boolean loggedIn = SessionUtil.isLoggedIn(session);

        // 1) Cho phep static resources va auth pages di qua khong can xac thuc.
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Bao ve API endpoints: yeu cau session da dang nhap.
        if (path.startsWith(API_PREFIX)) {
            if (!loggedIn) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }
            applyNoCache(httpResponse);
            chain.doFilter(request, response);
            return;
        }

        // 3) Cac request khac: redirect ve login neu chua dang nhap.
        if (!loggedIn) {
            httpResponse.sendRedirect(contextPath + LOGIN_PATH);
            return;
        }

        // 4) Da dang nhap -> set no-cache headers va cho di tiep.
        applyNoCache(httpResponse);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nothing to release.
    }

    /**
     * Cac duong dan duoc cho phep truy cap khong can dang nhap:
     * static resources (.css, .js, /assets/), login va register.
     */
    private boolean isPublicResource(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (path.equals(LOGIN_PATH) || path.startsWith(LOGIN_PATH + "/")) {
            return true;
        }
        if (path.equals(REGISTER_PATH) || path.startsWith(REGISTER_PATH + "/")) {
            return true;
        }
        if (path.startsWith(ASSETS_PREFIX)) {
            return true;
        }
        String lower = path.toLowerCase();
        return lower.endsWith(".css") || lower.endsWith(".js");
    }

    /**
     * Set no-cache headers de tranh trinh duyet cache trang da xac thuc.
     */
    private void applyNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
