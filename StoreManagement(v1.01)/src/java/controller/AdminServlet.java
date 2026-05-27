/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author PCQN
 */
import dao.AdminOwnerDao;
import model.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class AdminServlet extends HttpServlet {


    private AdminOwnerDao adminOwnerDAO;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        adminOwnerDAO = new AdminOwnerDao();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Nếu chưa làm login thì để true để test.
        // Sau này đổi lại thành: if (!isSystemAdmin(request)) { ... }
        if (!isSystemAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "addOwner":
                request.setAttribute("formMode", "addOwner");
                loadData(request);
                break;

            case "editOwner":
                String ownerName = request.getParameter("name");

                request.setAttribute("formMode", "editOwner");
                request.setAttribute("editingOwner", adminOwnerDAO.getOwnerByName(ownerName));

                loadData(request);
                break;

            default:
                request.setAttribute("formMode", "list");
                loadData(request);
                break;
        }

        request.getRequestDispatcher("/view/admin/AdminDashboard.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        adminOwnerDAO = new AdminOwnerDao();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!isSystemAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("createOwner".equals(action)) {
            createOwner(request);
        } else if ("updateOwner".equals(action)) {
            updateOwner(request);
        } else if ("lockOwner".equals(action)) {
            lockOwner(request);
        } else if ("unlockOwner".equals(action)) {
            unlockOwner(request);
        }

        response.sendRedirect(request.getContextPath() + "/admin");
    }

    private boolean isSystemAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        // TEMP TEST: chưa có login thì return true
        if (session == null) {
            return true;
        }

        Object roleName = session.getAttribute("roleName");

        if (roleName == null) {
            return true;
        }

        return "SystemAdmin".equalsIgnoreCase(roleName.toString())
                || "Admin".equalsIgnoreCase(roleName.toString());
    }

    private void loadData(HttpServletRequest request) {

        List<Employee> owners = adminOwnerDAO.getShopOwnersOnly();

        request.setAttribute("owners", owners);

        request.setAttribute(
                "branches",
                adminOwnerDAO.getAllBranches()
        );
    }

    private void createOwner(HttpServletRequest request) {
        Employee e = new Employee();

        String branchIDRaw = request.getParameter("branchID");

        if (branchIDRaw == null || branchIDRaw.trim().isEmpty()) {
            branchIDRaw = "1";
        }

        e.setBranchID(Integer.parseInt(branchIDRaw));
        e.setFullName(request.getParameter("fullName"));
        e.setEmail(request.getParameter("email"));
        e.setPhone(request.getParameter("phone"));
        e.setPasswordHash(request.getParameter("password"));
        e.setStatus("active");

        adminOwnerDAO.addOwner(e);
    }

    private void updateOwner(HttpServletRequest request) {
        Employee e = new Employee();

        e.setEmployeeID(Integer.parseInt(request.getParameter("employeeID")));

        String branchIDRaw = request.getParameter("branchID");

        if (branchIDRaw == null || branchIDRaw.trim().isEmpty()) {
            branchIDRaw = "1";
        }

        e.setBranchID(Integer.parseInt(branchIDRaw));
        e.setFullName(request.getParameter("fullName"));
        e.setEmail(request.getParameter("email"));
        e.setPhone(request.getParameter("phone"));
        e.setStatus(request.getParameter("status"));

        adminOwnerDAO.updateOwner(e);
    }

    private void lockOwner(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("employeeID"));
        adminOwnerDAO.updateOwnerStatus(id, "locked");
    }

    private void unlockOwner(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("employeeID"));
        adminOwnerDAO.updateOwnerStatus(id, "active");
    }
}
