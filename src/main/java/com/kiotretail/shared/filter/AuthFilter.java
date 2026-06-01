package com.kiotretail.shared.filter;

import com.kiotretail.employee.model.Employee;
import com.kiotretail.purchase.dao.PurchaseOrderDAO;
import com.kiotretail.shared.constant.AppConstants;
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
 *
 * Voi moi request da xac thuc phuc vu trang (page render), filter cap nhat
 * session attribute {@code pendingApprovalCount} de navbar hien thi badge so
 * phieu dang cho duyet. Chi role duyet ({@link AppConstants#ROLE_OWNER},
 * {@link AppConstants#ROLE_STORE_MANAGER}) moi chay COUNT query; role khac = 0.
 */
public class AuthFilter implements Filter {

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String ASSETS_PREFIX = "/assets/";
    private static final String API_PREFIX = "/api/";

    private static final String PENDING_APPROVAL_COUNT_KEY = "pendingApprovalCount";

    private PurchaseOrderDAO purchaseOrderDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        purchaseOrderDAO = new PurchaseOrderDAO();
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

        // 4) Da dang nhap -> cap nhat badge, set no-cache headers va cho di tiep.
        updatePendingApprovalBadge(session);
        applyNoCache(httpResponse);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nothing to release.
    }

    /**
     * Cap nhat session attribute {@code pendingApprovalCount} cho navbar badge.
     *
     * <p>Chi user co role duyet (Owner / Store Manager) moi chay COUNT query
     * (1 cau COUNT re tien); cac role khac set 0. Loi DAO khong duoc lam hong
     * request -> nuot exception va de count = 0.</p>
     */
    private void updatePendingApprovalBadge(HttpSession session) {
        int count = 0;
        try {
            String roleName = SessionUtil.getRoleName(session);
            boolean isApprover = AppConstants.ROLE_OWNER.equals(roleName)
                    || AppConstants.ROLE_STORE_MANAGER.equals(roleName);
            Object employee = SessionUtil.getEmployee(session);
            if (isApprover && employee instanceof Employee) {
                int employeeId = ((Employee) employee).getEmployeeId();
                count = purchaseOrderDAO.countPendingForApprover(roleName, employeeId);
            }
        } catch (RuntimeException e) {
            // Badge la phu tro: khong chan request khi DAO loi.
            e.printStackTrace();
            count = 0;
        }
        session.setAttribute(PENDING_APPROVAL_COUNT_KEY, count);
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
