package com.kiotretail.employee.controller;

import com.kiotretail.employee.service.AuthService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class ForgotPasswordServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, ViewPaths.FORGOT_PASSWORD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
}