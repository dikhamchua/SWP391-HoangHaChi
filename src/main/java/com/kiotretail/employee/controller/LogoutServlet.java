package com.kiotretail.employee.controller;

import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.ViewPaths;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Invalidates the current HTTP session (if any) and redirects the user
 * back to the login page. Supports both GET and POST so the action can be
 * triggered via a navigation link or a form submission.
 */
public class LogoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleLogout(req, resp);
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect(req, resp, ViewPaths.REDIRECT_LOGIN);
    }
}
