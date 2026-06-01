package com.kiotretail.product.controller;

import com.kiotretail.product.model.Supplier;
import com.kiotretail.product.service.SupplierService;
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

@WebServlet("/admin/suppliers")
public class SupplierServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private SupplierService supplierService;

    @Override
    public void init() {
        supplierService = new SupplierService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "list");
        try {
            switch (action) {
                case "view":
                    handleView(request, response);
                    break;
                case "create":
                    forward(request, response, ViewPaths.SUPPLIER_CREATE);
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
            redirect(request, response, ViewPaths.REDIRECT_SUPPLIERS);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");
        try {
            switch (action) {
                case AppConstants.ACTION_ADD:
                    supplierService.createSupplier(buildSupplierFromRequest(request, false));
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.CREATE_SUCCESS, ErrorMessages.ENTITY_SUPPLIER));
                    break;
                case AppConstants.ACTION_UPDATE:
                    supplierService.updateSupplier(buildSupplierFromRequest(request, true));
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.UPDATE_SUCCESS, ErrorMessages.ENTITY_SUPPLIER));
                    break;
                case AppConstants.ACTION_DELETE:
                    int supplierId = getIntParam(request, "supplierId", 0);
                    supplierService.deleteSupplier(supplierId);
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.DELETE_SUCCESS, ErrorMessages.ENTITY_SUPPLIER));
                    break;
                default:
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ErrorMessages.INVALID_ACTION);
                    break;
            }
        } catch (ServiceException ex) {
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ex.getMessage());
        }
        redirect(request, response, ViewPaths.REDIRECT_SUPPLIERS);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = Math.max(getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE), 1);
        int size = Math.max(Math.min(getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE), AppConstants.MAX_PAGE_SIZE), 1);
        Pagination pagination = Pagination.of(page, size);

        String keyword = getStringParam(request, AppConstants.PARAM_KEYWORD, null);

        PageResult<Supplier> pageResult = supplierService.listSuppliers(keyword, pagination);
        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.PARAM_KEYWORD, keyword == null ? "" : keyword);
        forward(request, response, ViewPaths.SUPPLIER_LIST);
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Supplier supplier = supplierService.getSupplierById(id);
        request.setAttribute("supplier", supplier);
        forward(request, response, ViewPaths.SUPPLIER_DETAIL);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Supplier supplier = supplierService.getSupplierById(id);
        request.setAttribute("supplier", supplier);
        forward(request, response, ViewPaths.SUPPLIER_EDIT);
    }

    private Supplier buildSupplierFromRequest(HttpServletRequest request, boolean includeId) {
        Supplier supplier = new Supplier();
        if (includeId) {
            supplier.setSupplierId(getIntParam(request, "supplierId", 0));
        }
        supplier.setName(getStringParam(request, "name", null));
        supplier.setPhone(getStringParam(request, AppConstants.PARAM_PHONE, null));
        supplier.setEmail(getStringParam(request, AppConstants.PARAM_EMAIL, null));
        supplier.setAddress(getStringParam(request, "address", null));
        supplier.setStatus(getStringParam(request, AppConstants.PARAM_STATUS, AppConstants.STATUS_ACTIVE));
        return supplier;
    }
}
