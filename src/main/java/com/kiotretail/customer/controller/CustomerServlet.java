package com.kiotretail.customer.controller;

import com.kiotretail.customer.dto.CustomerFilterDTO;
import com.kiotretail.customer.model.Customer;
import com.kiotretail.customer.service.CustomerService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;

public class CustomerServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private CustomerService customerService;

    @Override
    public void init() {
        customerService = new CustomerService();
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
                    forward(request, response, ViewPaths.CUSTOMER_CREATE);
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
            forward(request, response, ViewPaths.CUSTOMER_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");
        try {
            switch (action) {
                case AppConstants.ACTION_ADD:
                    customerService.createCustomer(buildCustomerFromRequest(request, false));
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.CREATE_SUCCESS, ErrorMessages.ENTITY_CUSTOMER));
                    break;
                case AppConstants.ACTION_UPDATE:
                    customerService.updateCustomer(buildCustomerFromRequest(request, true));
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.UPDATE_SUCCESS, ErrorMessages.ENTITY_CUSTOMER));
                    break;
                case AppConstants.ACTION_DELETE:
                    int customerId = getIntParam(request, "customerId", 0);
                    customerService.deleteCustomer(customerId);
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE,
                            String.format(ErrorMessages.DELETE_SUCCESS, ErrorMessages.ENTITY_CUSTOMER));
                    break;
                default:
                    request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ErrorMessages.INVALID_ACTION);
                    break;
            }
        } catch (ServiceException ex) {
            request.getSession().setAttribute(AppConstants.SESSION_FLASH_ERROR, ex.getMessage());
        }
        redirect(request, response, ViewPaths.REDIRECT_CUSTOMERS);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE);
        int size = getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE);
        Pagination pagination = Pagination.of(page, size);

        CustomerFilterDTO filter = new CustomerFilterDTO();
        filter.setKeyword(getStringParam(request, AppConstants.PARAM_KEYWORD, null));
        filter.setGender(getStringParam(request, "gender", null));
        filter.setMembershipTier(getStringParam(request, "membershipTier", null));

        PageResult<Customer> pageResult = customerService.listCustomers(filter, pagination);
        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.ATTR_FILTER, filter);
        forward(request, response, ViewPaths.CUSTOMER_LIST);
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Customer customer = customerService.getCustomerById(id);
        request.setAttribute(AppConstants.ATTR_CUSTOMER, customer);
        forward(request, response, ViewPaths.CUSTOMER_DETAIL);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Customer customer = customerService.getCustomerById(id);
        request.setAttribute(AppConstants.ATTR_CUSTOMER, customer);
        forward(request, response, ViewPaths.CUSTOMER_EDIT);
    }

    private Customer buildCustomerFromRequest(HttpServletRequest request, boolean includeId) {
        Customer customer = new Customer();
        if (includeId) {
            customer.setCustomerId(getIntParam(request, "customerId", 0));
        }
        customer.setFullName(getStringParam(request, AppConstants.PARAM_FULL_NAME, null));
        customer.setPhone(getStringParam(request, AppConstants.PARAM_PHONE, null));
        customer.setEmail(getStringParam(request, AppConstants.PARAM_EMAIL, null));
        customer.setAddress(getStringParam(request, "address", null));
        customer.setGender(getStringParam(request, "gender", null));
        customer.setMembershipTier(getStringParam(request, "membershipTier", null));

        String dob = request.getParameter(AppConstants.PARAM_DATE_OF_BIRTH);
        if (dob != null && !dob.trim().isEmpty()) {
            try {
                customer.setDateOfBirth(Date.valueOf(dob.trim()));
            } catch (IllegalArgumentException ignored) {
                customer.setDateOfBirth(null);
            }
        }
        return customer;
    }
}
