package com.kiotretail.pos.controller;

import com.kiotretail.pos.dto.CartItem;
import com.kiotretail.pos.dto.CartSession;
import com.kiotretail.pos.service.POSService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * POS sale screen controller.
 * Handles cart management actions and product search backed by {@link POSService}.
 */
public class POSServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final String CART_KEY = AppConstants.ATTR_CART;

    private POSService posService;

    @Override
    public void init() {
        this.posService = new POSService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        CartSession cart = getCart(req);
        req.setAttribute(CART_KEY, cart);
        forward(req, resp, ViewPaths.POS_SALE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = getStringParam(req, AppConstants.PARAM_ACTION, "");

        try {
            switch (action) {
                case "search":
                    handleSearch(req, resp);
                    return;
                case "addItem":
                    handleAddItem(req, resp);
                    return;
                case "updateQty":
                    handleUpdateQty(req, resp);
                    return;
                case "removeItem":
                    handleRemoveItem(req, resp);
                    return;
                case "setCustomer":
                    handleSetCustomer(req, resp);
                    return;
                case "setDiscount":
                    handleSetDiscount(req, resp);
                    return;
                case AppConstants.ACTION_CHECKOUT:
                    handleCheckout(req, resp);
                    return;
                case AppConstants.ACTION_CLEAR:
                    handleClear(req, resp);
                    return;
                default:
                    redirect(req, resp, ViewPaths.REDIRECT_POS);
            }
        } catch (ServiceException ex) {
            HttpSession session = req.getSession();
            session.setAttribute(AppConstants.SESSION_FLASH_ERROR, ex.getMessage());
            redirect(req, resp, ViewPaths.REDIRECT_POS);
        }
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = getStringParam(req, AppConstants.PARAM_KEYWORD, "");
        sendJson(resp, posService.searchProducts(keyword, 20));
    }

    private void handleAddItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = getIntParam(req, "productId", 0);
        String productName = getStringParam(req, "productName", "");
        String sku = getStringParam(req, "sku", "");
        BigDecimal unitPrice = parseBigDecimal(req.getParameter(AppConstants.PARAM_UNIT_PRICE), BigDecimal.ZERO);
        int quantity = getIntParam(req, "quantity", 1);
        if (quantity <= 0) {
            quantity = 1;
        }

        CartItem item = new CartItem(productId, productName, sku, unitPrice, quantity);
        CartSession cart = getCart(req);
        cart.addItem(item);

        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    private void handleUpdateQty(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = getIntParam(req, "productId", 0);
        int quantity = getIntParam(req, "quantity", 0);
        CartSession cart = getCart(req);
        cart.updateQuantity(productId, quantity);
        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    private void handleRemoveItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = getIntParam(req, "productId", 0);
        CartSession cart = getCart(req);
        cart.removeItem(productId);
        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    private void handleSetCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int customerId = getIntParam(req, "customerId", 0);
        String customerName = getStringParam(req, "customerName", "");
        CartSession cart = getCart(req);
        cart.setCustomerId(customerId > 0 ? customerId : null);
        cart.setCustomerName(customerName.isEmpty() ? null : customerName);
        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    private void handleSetDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BigDecimal discount = parseBigDecimal(req.getParameter(AppConstants.PARAM_DISCOUNT), BigDecimal.ZERO);
        CartSession cart = getCart(req);
        cart.setDiscount(discount);
        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        CartSession cart = getCart(req);

        Object employeeObj = SessionUtil.getEmployee(session);
        Integer employeeId = extractEmployeeId(employeeObj);
        Integer branchId = SessionUtil.getBranchId(session);
        String paymentMethod = getStringParam(req, "paymentMethod", AppConstants.PAYMENT_CASH);

        if (employeeId == null) {
            throw new ServiceException("Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", 401);
        }
        if (branchId == null) {
            throw new ServiceException("Không xác định được chi nhánh bán hàng.", 400);
        }

        posService.checkout(cart, employeeId, branchId, paymentMethod);
        cart.clear();

        session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, "Thanh toán thành công");
        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    private void handleClear(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CartSession cart = getCart(req);
        cart.clear();
        redirect(req, resp, ViewPaths.REDIRECT_POS);
    }

    /**
     * Resolve the cart from the current session, creating one if absent.
     */
    private CartSession getCart(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        Object existing = session.getAttribute(CART_KEY);
        CartSession cart;
        if (existing instanceof CartSession) {
            cart = (CartSession) existing;
        } else {
            cart = new CartSession();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    private BigDecimal parseBigDecimal(String raw, BigDecimal defaultValue) {
        if (raw == null) {
            return defaultValue;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return defaultValue;
        }
        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private Integer extractEmployeeId(Object employeeObj) {
        if (employeeObj == null) {
            return null;
        }
        try {
            java.lang.reflect.Method m = employeeObj.getClass().getMethod("getEmployeeId");
            Object value = m.invoke(employeeObj);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            java.lang.reflect.Method m = employeeObj.getClass().getMethod("getId");
            Object value = m.invoke(employeeObj);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return null;
    }
}
