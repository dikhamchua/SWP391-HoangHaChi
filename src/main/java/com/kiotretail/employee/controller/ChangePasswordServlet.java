package com.kiotretail.employee.controller;

import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.service.AuthService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class ChangePasswordServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private AuthService authService;

    @Override
    public void init() {
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, ViewPaths.CHANGE_PASSWORD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
        int employeeId = employee.getEmployeeId();

        try {
            authService.changePassword(employeeId, currentPassword, newPassword);
            session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ErrorMessages.CHANGE_PASSWORD_SUCCESS);
            redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            forward(req, resp, ViewPaths.CHANGE_PASSWORD);
        }
    }
}