package com.kiotretail.invoice.controller;

import com.kiotretail.invoice.dto.InvoiceFilterDTO;
import com.kiotretail.invoice.model.Order;
import com.kiotretail.invoice.model.OrderDetail;
import com.kiotretail.invoice.model.Payment;
import com.kiotretail.invoice.service.InvoiceService;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * Invoice Servlet
 * Service-layer based controller for invoice management.
 * Handles listing, viewing, creating orders and adding payments.
 */
public class InvoiceServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final String REDIRECT_LIST = ViewPaths.REDIRECT_INVOICES;
    private static final String VIEW_LIST = ViewPaths.INVOICE_LIST;
    private static final String VIEW_DETAIL = ViewPaths.INVOICE_DETAIL;
    private static final String VIEW_CREATE = ViewPaths.INVOICE_CREATE;

    private InvoiceService invoiceService;

    @Override
    public void init() throws ServletException {
        this.invoiceService = new InvoiceService();
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
                    handleCreateForm(request, response);
                    break;
                case "list":
                default:
                    handleList(request, response);
                    break;
            }
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");

        switch (action) {
            case "cancel":
                handleCancel(request, response);
                break;
            case "addPayment":
                handleAddPayment(request, response);
                break;
            default:
                redirect(request, response, REDIRECT_LIST);
                break;
        }
    }

    // -----------------------------------------------------------------------
    // GET handlers
    // -----------------------------------------------------------------------

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE);
        int size = getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE);
        Pagination pagination = Pagination.of(Math.max(page, 1), Math.max(Math.min(size, AppConstants.MAX_PAGE_SIZE), 1));

        InvoiceFilterDTO filter = new InvoiceFilterDTO();
        filter.setKeyword(getStringParam(request, AppConstants.PARAM_KEYWORD, null));
        filter.setOrderType(getStringParam(request, "orderType", null));
        filter.setStatus(getStringParam(request, AppConstants.PARAM_STATUS, null));
        filter.setDateFrom(getStringParam(request, "dateFrom", null));
        filter.setDateTo(getStringParam(request, "dateTo", null));

        PageResult<Order> pageResult = invoiceService.listOrders(filter, pagination);

        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.ATTR_FILTER, filter);

        forward(request, response, VIEW_LIST);
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        Order order = invoiceService.getOrderById(id);
        List<OrderDetail> details = invoiceService.getOrderDetails(id);
        List<Payment> payments = invoiceService.getOrderPayments(id);

        request.setAttribute(AppConstants.ATTR_ORDER, order);
        request.setAttribute(AppConstants.ATTR_ORDER_DETAILS, details);
        request.setAttribute(AppConstants.ATTR_PAYMENTS, payments);

        forward(request, response, VIEW_DETAIL);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        forward(request, response, VIEW_CREATE);
    }

    // -----------------------------------------------------------------------
    // POST handlers
    // -----------------------------------------------------------------------

    private void handleCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int orderId = getIntParam(request, "orderId", 0);
            invoiceService.cancelOrder(orderId);
            setFlashMessage(request, String.format(ErrorMessages.UPDATE_SUCCESS, ErrorMessages.ENTITY_ORDER), AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirect(request, response, REDIRECT_LIST);
    }

    private void handleAddPayment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int orderId = getIntParam(request, "orderId", 0);
        try {
            String paymentMethod = getStringParam(request, "paymentMethod", "");
            BigDecimal amount = parseBigDecimal(request.getParameter(AppConstants.PARAM_AMOUNT));

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(amount);

            invoiceService.addPayment(payment);
            setFlashMessage(request, String.format(ErrorMessages.CREATE_SUCCESS, "thanh toán"), AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirect(request, response, REDIRECT_LIST + "?action=view&id=" + orderId);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private void setFlashMessage(HttpServletRequest request, String message, String type) {
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, message);
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, type);
    }
}
