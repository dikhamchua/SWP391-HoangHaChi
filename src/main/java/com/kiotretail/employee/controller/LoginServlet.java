package com.kiotretail.employee.controller;

import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.service.AuthService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, ViewPaths.LOGIN);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = getStringParam(req, AppConstants.PARAM_EMAIL, "");
        String password = req.getParameter(AppConstants.PARAM_PASSWORD);

        try {
            Employee employee = authService.login(email, password);

            HttpSession session = req.getSession(true);
            SessionUtil.setLoginSession(
                    session,
                    employee,
                    employee.getRoleName(),
                    employee.getBranchId()
            );

            redirect(req, resp, ViewPaths.REDIRECT_ROLE_SELECTION);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            req.setAttribute(AppConstants.ATTR_EMAIL, email);
            forward(req, resp, ViewPaths.LOGIN);
        }
    }
}