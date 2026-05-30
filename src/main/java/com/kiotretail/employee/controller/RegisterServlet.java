package com.kiotretail.employee.controller;

import com.kiotretail.employee.dao.BranchDAO;
import com.kiotretail.employee.dao.RoleDAO;
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

public class RegisterServlet extends BaseServlet {

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute(AppConstants.ATTR_ROLES, roleDAO.getAll());
        req.setAttribute(AppConstants.ATTR_BRANCHES, branchDAO.getActive());
        forward(req, resp, ViewPaths.REGISTER);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
            setFormAttributes(req, fullName, email, phone);
            forward(req, resp, ViewPaths.REGISTER);
            return;
        }

        try {
            authService.register(
                    fullName, email, phone, password,
                    Integer.parseInt(roleId),
                    Integer.parseInt(branchId)
            );

            HttpSession session = req.getSession(true);
            session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, ErrorMessages.REGISTER_SUCCESS);
            redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
        } catch (ServiceException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, ex.getMessage());
            setFormAttributes(req, fullName, email, phone);
            forward(req, resp, ViewPaths.REGISTER);
        } catch (NumberFormatException ex) {
            req.setAttribute(AppConstants.ATTR_ERROR, String.format(ErrorMessages.INVALID_VALUE, "vai trò/chi nhánh"));
            setFormAttributes(req, fullName, email, phone);
            forward(req, resp, ViewPaths.REGISTER);
        }
    }

    private void setFormAttributes(HttpServletRequest req, String fullName, String email, String phone) {
        req.setAttribute(AppConstants.ATTR_ROLES, roleDAO.getAll());
        req.setAttribute(AppConstants.ATTR_BRANCHES, branchDAO.getActive());
        req.setAttribute(AppConstants.ATTR_FULL_NAME, fullName);
        req.setAttribute(AppConstants.ATTR_EMAIL, email);
        req.setAttribute(AppConstants.ATTR_PHONE, phone);
    }
}