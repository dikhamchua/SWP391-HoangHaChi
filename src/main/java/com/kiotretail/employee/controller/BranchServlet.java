package com.kiotretail.employee.controller;

import com.kiotretail.employee.model.Branch;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.employee.service.BranchService;
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

@WebServlet("/admin/branches")
public class BranchServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private BranchService branchService;

    @Override
    public void init() {
        branchService = new BranchService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "list");
        try {
            switch (action) {
                case "view":
                    // Redirect thẳng sang trang edit, không dùng trang detail nữa
                    int viewId = getIntParam(request, AppConstants.PARAM_ID, 0);
                    redirect(request, response, ViewPaths.REDIRECT_BRANCHES + "?action=edit&id=" + viewId);
                    return;
                case "create":
                    forward(request, response, ViewPaths.BRANCH_CREATE);
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
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ex.getMessage());
            redirect(request, response, ViewPaths.REDIRECT_BRANCHES);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");
        String redirectUrl = ViewPaths.REDIRECT_BRANCHES;
        try {
            switch (action) {
                case AppConstants.ACTION_ADD:
                    branchService.createBranch(buildBranchFromRequest(request, false), getCurrentEmployeeId(request));
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.CREATE_SUCCESS, ErrorMessages.ENTITY_BRANCH));
                    break;
                case AppConstants.ACTION_UPDATE:
                    int updatedBranchId = handleUpdate(request);
                    redirectUrl = ViewPaths.REDIRECT_BRANCHES + "?action=edit&id=" + updatedBranchId;
                    break;
                case AppConstants.ACTION_DELETE:
                    int branchId = getIntParam(request, "branchId", 0);
                    branchService.deleteBranch(branchId, getCurrentEmployeeId(request));
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.DELETE_SUCCESS, ErrorMessages.ENTITY_BRANCH));
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

        String keyword = getStringParam(request, AppConstants.PARAM_KEYWORD, null);

        PageResult<Branch> pageResult = branchService.listBranches(keyword, pagination);
        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.PARAM_KEYWORD, keyword == null ? "" : keyword);
        forward(request, response, ViewPaths.BRANCH_LIST);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Branch branch = branchService.getBranchById(id);
        request.setAttribute(AppConstants.ATTR_BRANCH, branch);
        request.setAttribute("activities", branchService.getActivitiesByBranchId(id));
        forward(request, response, ViewPaths.BRANCH_EDIT);
    }

    private int handleUpdate(HttpServletRequest request) {
        Branch branch = buildBranchFromRequest(request, true);
        branchService.updateBranch(branch, getCurrentEmployeeId(request));
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                String.format(ErrorMessages.UPDATE_SUCCESS, ErrorMessages.ENTITY_BRANCH));
        return branch.getBranchId();
    }

    private Integer getCurrentEmployeeId(HttpServletRequest request) {
        Object employee = request.getSession().getAttribute(AppConstants.SESSION_EMPLOYEE);
        if (employee instanceof Employee) {
            return ((Employee) employee).getEmployeeId();
        }
        return null;
    }

    private Branch buildBranchFromRequest(HttpServletRequest request, boolean includeId) {
        Branch branch = new Branch();
        if (includeId) {
            branch.setBranchId(getIntParam(request, "branchId", 0));
        }
        branch.setName(getStringParam(request, "name", null));
        branch.setAddress(getStringParam(request, "address", null));
        branch.setPhone(getStringParam(request, AppConstants.PARAM_PHONE, null));
        branch.setStatus(getStringParam(request, AppConstants.PARAM_STATUS, AppConstants.STATUS_ACTIVE));
        return branch;
    }
}