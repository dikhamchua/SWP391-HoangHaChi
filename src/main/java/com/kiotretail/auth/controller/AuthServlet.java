package com.kiotretail.auth.controller;

import com.kiotretail.employee.dao.BranchDAO;
import com.kiotretail.employee.dao.RoleDAO;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.service.AuthService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout", "/register", "/forgot-password", "/change-password", "/role-selection"})
public class AuthServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private AuthService authService;
    private RoleDAO roleDAO;
    private BranchDAO branchDAO;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
        this.roleDAO = new RoleDAO();
        this.branchDAO = new BranchDAO();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        String method = req.getMethod();

        switch (path) {
            case "/login":
                if ("GET".equals(method)) showLogin(req, resp);
                else doLogin(req, resp);
                break;
            case "/logout":
                doLogout(req, resp);
                break;
            case "/register":
                if ("GET".equals(method)) showRegister(req, resp);
                else doRegister(req, resp);
                break;
            case "/forgot-password":
                if ("GET".equals(method)) showForgotPassword(req, resp);
                else doForgotPassword(req, resp);
                break;
            case "/change-password":
                if ("GET".equals(method)) showChangePassword(req, resp);
                else doChangePassword(req, resp);
                break;
            case "/role-selection":
                if ("GET".equals(method)) showRoleSelection(req, resp);
                else doRoleSelection(req, resp);
                break;
            default:
                resp.sendError(AppConstants.HTTP_NOT_FOUND);
                break;
        }
    }

    private void showLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, ViewPaths.LOGIN);
    }

    private void doLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = getStringParam(req, "username", "");
        String password = req.getParameter(AppConstants.PARAM_PASSWORD);

        try {
            Employee employee = authService.login(email, password);

            HttpSession session = req.getSession(true);
            SessionUtil.setLoginSession(session, employee, employee.getRoleName(), employee.getBranchId());

            redirect(req, resp, ViewPaths.REDIRECT_ROLE_SELECTION);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            req.setAttribute(AppConstants.ATTR_EMAIL, email);
            forward(req, resp, ViewPaths.LOGIN);
        }
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
    }

    private void showRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute(AppConstants.ATTR_ROLES, roleDAO.getAll());
        req.setAttribute(AppConstants.ATTR_BRANCHES, branchDAO.getActive());
        forward(req, resp, ViewPaths.REGISTER);
    }

    private void doRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fullName = getStringParam(req, AppConstants.PARAM_FULL_NAME, "");
        String email = getStringParam(req, AppConstants.PARAM_EMAIL, "");
        String phone = getStringParam(req, AppConstants.PARAM_PHONE, "");
        String password = req.getParameter(AppConstants.PARAM_PASSWORD);
        String confirmPassword = req.getParameter(AppConstants.PARAM_CONFIRM_PASSWORD);
        String roleId = getStringParam(req, "roleId", "");
        String branchId = getStringParam(req, "branchId", "");

        if (password == null || !password.equals(confirmPassword)) {
            req.setAttribute(AppConstants.ATTR_ERROR, ErrorMessages.PASSWORD_MISMATCH);
            setRegisterFormAttributes(req, fullName, email, phone);
            forward(req, resp, ViewPaths.REGISTER);
            return;
        }

        try {
            authService.register(fullName, email, phone, password,
                    Integer.parseInt(roleId), Integer.parseInt(branchId));

            HttpSession session = req.getSession(true);
            session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ErrorMessages.REGISTER_SUCCESS);
            redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            setRegisterFormAttributes(req, fullName, email, phone);
            forward(req, resp, ViewPaths.REGISTER);
        } catch (NumberFormatException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, String.format(ErrorMessages.INVALID_VALUE, "vai trò/chi nhánh"));
            setRegisterFormAttributes(req, fullName, email, phone);
            forward(req, resp, ViewPaths.REGISTER);
        }
    }

    private void showForgotPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, ViewPaths.FORGOT_PASSWORD);
    }

    private void doForgotPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = getStringParam(req, AppConstants.PARAM_EMAIL, "");
        String newPassword = req.getParameter(AppConstants.PARAM_NEW_PASSWORD);
        String confirmPassword = req.getParameter(AppConstants.PARAM_CONFIRM_PASSWORD);

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            req.setAttribute(AppConstants.ATTR_ERROR, ErrorMessages.PASSWORD_MISMATCH);
            req.setAttribute(AppConstants.ATTR_EMAIL, email);
            forward(req, resp, ViewPaths.FORGOT_PASSWORD);
            return;
        }

        try {
            authService.resetPassword(email, newPassword);
            HttpSession session = req.getSession(true);
            session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ErrorMessages.RESET_PASSWORD_SUCCESS);
            redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            req.setAttribute(AppConstants.ATTR_EMAIL, email);
            forward(req, resp, ViewPaths.FORGOT_PASSWORD);
        }
    }

    private void showChangePassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, ViewPaths.CHANGE_PASSWORD);
    }

    private void doChangePassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Object loggedIn = SessionUtil.getEmployee(session);
        if (loggedIn == null) {
            redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
            return;
        }

        String currentPassword = req.getParameter(AppConstants.PARAM_CURRENT_PASSWORD);
        String newPassword = req.getParameter(AppConstants.PARAM_NEW_PASSWORD);
        String confirmPassword = req.getParameter(AppConstants.PARAM_CONFIRM_PASSWORD);

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            req.setAttribute(AppConstants.ATTR_ERROR, ErrorMessages.PASSWORD_MISMATCH);
            forward(req, resp, ViewPaths.CHANGE_PASSWORD);
            return;
        }

        Employee employee = (Employee) loggedIn;
        try {
            authService.changePassword(employee.getEmployeeId(), currentPassword, newPassword);
            session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ErrorMessages.CHANGE_PASSWORD_SUCCESS);
            redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            forward(req, resp, ViewPaths.CHANGE_PASSWORD);
        }
    }

    private void showRoleSelection(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (!SessionUtil.isLoggedIn(session)) {
            redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
            return;
        }

        String roleName = SessionUtil.getRoleName(session);
        if (AppConstants.ROLE_OWNER.equals(roleName) || AppConstants.ROLE_STORE_MANAGER.equals(roleName)) {
            redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
        } else if (AppConstants.ROLE_SALES_STAFF.equals(roleName)) {
            redirect(req, resp, ViewPaths.REDIRECT_POS);
        } else if (AppConstants.ROLE_WAREHOUSE_STAFF.equals(roleName)) {
            redirect(req, resp, ViewPaths.REDIRECT_PRODUCTS);
        } else {
            forward(req, resp, ViewPaths.ROLE_SELECTION);
        }
    }

    private void doRoleSelection(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String role = getStringParam(req, "role", "");

        if ("pos".equals(role)) {
            redirect(req, resp, ViewPaths.REDIRECT_POS);
        } else {
            redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
        }
    }

    private void setRegisterFormAttributes(HttpServletRequest req, String fullName, String email, String phone) {
        req.setAttribute(AppConstants.ATTR_ROLES, roleDAO.getAll());
        req.setAttribute(AppConstants.ATTR_BRANCHES, branchDAO.getActive());
        req.setAttribute(AppConstants.ATTR_FULL_NAME, fullName);
        req.setAttribute(AppConstants.ATTR_EMAIL, email);
        req.setAttribute(AppConstants.ATTR_PHONE, phone);
    }
}
