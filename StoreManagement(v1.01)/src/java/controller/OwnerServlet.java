/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author PCQN
 */

import dao.OwnerEmployeeDao;
import model.Employee;
import java.util.List;

public class OwnerServlet extends HttpServlet {

    private OwnerEmployeeDao ownerEmployeeDAO;

    @Override
    public void init() {
        ownerEmployeeDAO = new OwnerEmployeeDao();
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {

            case "add":
                request.setAttribute("formMode", "add");
                loadData(request);
                break;

            case "edit":

                int employeeID = Integer.parseInt(
                        request.getParameter("id")
                );

                Employee employee =
                        ownerEmployeeDAO.getEmployeeById(employeeID);

                request.setAttribute(
                        "editingEmployee",
                        employee
                );

                request.setAttribute("formMode", "edit");

                loadData(request);

                break;

            case "reset":

                int resetID = Integer.parseInt(
                        request.getParameter("id")
                );

                Employee resetEmployee =
                        ownerEmployeeDAO.getEmployeeById(resetID);

                request.setAttribute(
                        "resetEmployee",
                        resetEmployee
                );

                request.setAttribute("formMode", "reset");

                loadData(request);

                break;

            default:

                request.setAttribute("formMode", "list");

                loadData(request);

                break;
        }

        request.getRequestDispatcher(
                "/view/owner/OwnerDashboard.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        switch (action) {

            case "create":
                createEmployee(request);
                break;

            case "update":
                updateEmployee(request);
                break;

            case "lock":
                lockEmployee(request);
                break;

            case "unlock":
                unlockEmployee(request);
                break;

            case "delete":
                deleteEmployee(request);
                break;

            case "resetPassword":
                resetPassword(request);
                break;
        }

        response.sendRedirect(
                request.getContextPath() + "/owner"
        );
    }

    private void loadData(HttpServletRequest request) {

        String keyword = request.getParameter("keyword");
        String role = request.getParameter("role");
        String branch = request.getParameter("branch");
        String status = request.getParameter("status");

        List<Employee> employees =
                ownerEmployeeDAO.getEmployees(
                        keyword,
                        role,
                        branch,
                        status
                );

        request.setAttribute(
                "employees",
                employees
        );

        request.setAttribute(
                "roles",
                ownerEmployeeDAO.getAllowedRoles()
        );

        request.setAttribute(
                "branches",
                ownerEmployeeDAO.getAllBranches()
        );

        request.setAttribute("keyword", keyword);
        request.setAttribute("roleFilter", role);
        request.setAttribute("branchFilter", branch);
        request.setAttribute("statusFilter", status);
    }

    private void createEmployee(HttpServletRequest request) {

        Employee e = new Employee();

        e.setRoleID(
                Integer.parseInt(
                        request.getParameter("roleID")
                )
        );

        e.setBranchID(
                Integer.parseInt(
                        request.getParameter("branchID")
                )
        );

        e.setFullName(
                request.getParameter("fullName")
        );

        e.setEmail(
                request.getParameter("email")
        );

        e.setPhone(
                request.getParameter("phone")
        );

        e.setPasswordHash(
                request.getParameter("password")
        );

        e.setStatus("active");

        ownerEmployeeDAO.addEmployee(e);
    }

    private void updateEmployee(HttpServletRequest request) {

        Employee e = new Employee();

        e.setEmployeeID(
                Integer.parseInt(
                        request.getParameter("employeeID")
                )
        );

        e.setRoleID(
                Integer.parseInt(
                        request.getParameter("roleID")
                )
        );

        e.setBranchID(
                Integer.parseInt(
                        request.getParameter("branchID")
                )
        );

        e.setFullName(
                request.getParameter("fullName")
        );

        e.setEmail(
                request.getParameter("email")
        );

        e.setPhone(
                request.getParameter("phone")
        );

        e.setStatus(
                request.getParameter("status")
        );

        ownerEmployeeDAO.updateEmployee(e);
    }

    private void lockEmployee(HttpServletRequest request) {

        int employeeID = Integer.parseInt(
                request.getParameter("employeeID")
        );

        ownerEmployeeDAO.updateStatus(
                employeeID,
                "locked"
        );
    }

    private void unlockEmployee(HttpServletRequest request) {

        int employeeID = Integer.parseInt(
                request.getParameter("employeeID")
        );

        ownerEmployeeDAO.updateStatus(
                employeeID,
                "active"
        );
    }

    private void deleteEmployee(HttpServletRequest request) {

        int employeeID = Integer.parseInt(
                request.getParameter("employeeID")
        );

        ownerEmployeeDAO.deleteEmployee(employeeID);
    }

    private void resetPassword(HttpServletRequest request) {

        int employeeID = Integer.parseInt(
                request.getParameter("employeeID")
        );

        String newPassword =
                request.getParameter("newPassword");

        ownerEmployeeDAO.resetPassword(
                employeeID,
                newPassword
        );
    }
}