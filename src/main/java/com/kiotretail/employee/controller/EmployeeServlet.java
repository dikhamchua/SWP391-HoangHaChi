package com.kiotretail.employee.controller;

import com.kiotretail.employee.dto.EmployeeFilterDTO;
import com.kiotretail.employee.model.Branch;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.model.Role;
import com.kiotretail.employee.service.EmployeeService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/employees")
public class EmployeeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private EmployeeService employeeService;

    @Override
    public void init() {
        employeeService = new EmployeeService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "list");
        try {
            switch (action) {
                case "create":
                    handleCreate(request, response);
                    break;
                case "edit":
                    handleEdit(request, response);
                    break;
                case "list":
                default:
                    handleList(request, response);
                    break;
            }
        } catch (ServiceException ex) {
            request.setAttribute(AppConstants.ATTR_ERROR_MESSAGE, ex.getMessage());
            forward(request, response, ViewPaths.EMPLOYEE_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");
        String redirectUrl = ViewPaths.REDIRECT_EMPLOYEES;
        try {
            switch (action) {
                case AppConstants.ACTION_ADD:
                    handleAdd(request);
                    break;
                case AppConstants.ACTION_UPDATE:
                    int updatedEmployeeId = handleUpdate(request);
                    redirectUrl = ViewPaths.REDIRECT_EMPLOYEES + "?action=edit&id=" + updatedEmployeeId;
                    break;
                case AppConstants.ACTION_DELETE:
                    handleDelete(request);
                    break;
                default:
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ErrorMessages.INVALID_ACTION);
                    break;
            }
        } catch (ServiceException ex) {
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ex.getMessage());
        }
        redirect(request, response, redirectUrl);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = Math.max(getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE), 1);
        int size = Math.max(Math.min(getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE), AppConstants.MAX_PAGE_SIZE), 1);
        Pagination pagination = Pagination.of(page, size);

        EmployeeFilterDTO filter = EmployeeFilterDTO.from(request);
        PageResult<Employee> pageResult = employeeService.listEmployees(filter, pagination);
        List<Role> roles = employeeService.getAllRoles();
        List<Branch> branches = employeeService.getActiveBranches();

        request.setAttribute("filter", filter);
        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.ATTR_ROLES, roles);
        request.setAttribute(AppConstants.ATTR_BRANCHES, branches);
        forward(request, response, ViewPaths.EMPLOYEE_LIST);
    }


    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_ROLES, employeeService.getAllRoles());
        request.setAttribute(AppConstants.ATTR_BRANCHES, employeeService.getActiveBranches());
        forward(request, response, ViewPaths.EMPLOYEE_CREATE);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Employee employee = employeeService.getEmployeeById(id);
        request.setAttribute(AppConstants.ATTR_EMPLOYEE, employee);
        request.setAttribute(AppConstants.ATTR_ROLES, employeeService.getAllRoles());
        request.setAttribute(AppConstants.ATTR_BRANCHES, employeeService.getActiveBranches());
        request.setAttribute("activities", employeeService.getActivitiesByEmployeeId(id));
        forward(request, response, ViewPaths.EMPLOYEE_EDIT);
    }

    private void handleAdd(HttpServletRequest request) {
        Employee employee = buildEmployeeFromRequest(request, false);
        String password = getStringParam(request, AppConstants.PARAM_PASSWORD, null);
        employeeService.createEmployee(employee, password, getCurrentEmployeeId(request));
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                String.format(ErrorMessages.CREATE_SUCCESS, ErrorMessages.ENTITY_EMPLOYEE));
    }

    private int handleUpdate(HttpServletRequest request) {
        Employee employee = buildEmployeeFromRequest(request, true);
        employeeService.updateEmployee(employee, getCurrentEmployeeId(request));
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                String.format(ErrorMessages.UPDATE_SUCCESS, ErrorMessages.ENTITY_EMPLOYEE));
        return employee.getEmployeeId();
    }

    private void handleDelete(HttpServletRequest request) {
        int employeeId = getIntParam(request, "employeeId", 0);
        employeeService.deleteEmployee(employeeId, getCurrentEmployeeId(request));
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                String.format(ErrorMessages.DELETE_SUCCESS, ErrorMessages.ENTITY_EMPLOYEE));
    }

    private Integer getCurrentEmployeeId(HttpServletRequest request) {
        Object employee = request.getSession().getAttribute(AppConstants.SESSION_EMPLOYEE);
        if (employee instanceof Employee) {
            return ((Employee) employee).getEmployeeId();
        }
        return null;
    }

    private Employee buildEmployeeFromRequest(HttpServletRequest request, boolean includeId) {
        Employee employee = new Employee();
        if (includeId) {
            employee.setEmployeeId(getIntParam(request, "employeeId", 0));
        }
        employee.setFullName(getStringParam(request, AppConstants.PARAM_FULL_NAME, null));
        employee.setEmail(getStringParam(request, AppConstants.PARAM_EMAIL, null));
        employee.setPhone(getStringParam(request, AppConstants.PARAM_PHONE, null));
        employee.setRoleId(getIntParam(request, "roleId", 0));
        employee.setBranchId(getIntParam(request, "branchId", 0));
        employee.setStatus(getStringParam(request, "status", AppConstants.STATUS_ACTIVE));
        return employee;
    }
}
