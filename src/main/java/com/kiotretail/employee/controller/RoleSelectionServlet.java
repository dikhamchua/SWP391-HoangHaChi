package com.kiotretail.employee.controller;

import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Lets a logged-in employee choose which view (admin vs. POS) to enter
 * after authentication. Owners and store managers default to the admin
 * dashboard, sales staff go straight to the POS, and warehouse staff land
 * on the products screen. Anyone else sees the role-selection page.
 */
public class RoleSelectionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (!SessionUtil.isLoggedIn(session)) {
            redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
            return;
        }

        String roleName = SessionUtil.getRoleName(session);
        if (AppConstants.ROLE_OWNER.equals(roleName) || AppConstants.ROLE_STORE_MANAGER.equals(roleName)) {
            redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
            return;
        }
        if (AppConstants.ROLE_SALES_STAFF.equals(roleName)) {
            redirect(req, resp, ViewPaths.REDIRECT_POS);
            return;
        }
        if (AppConstants.ROLE_WAREHOUSE_STAFF.equals(roleName)) {
            redirect(req, resp, ViewPaths.REDIRECT_PRODUCTS);
            return;
        }

        forward(req, resp, ViewPaths.ROLE_SELECTION);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String role = getStringParam(req, "role", "");

        if ("admin".equals(role)) {
            redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
            return;
        }
        if ("pos".equals(role)) {
            redirect(req, resp, ViewPaths.REDIRECT_POS);
            return;
        }

        redirect(req, resp, ViewPaths.REDIRECT_DASHBOARD);
    }
}
