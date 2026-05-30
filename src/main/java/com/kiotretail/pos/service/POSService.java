package com.kiotretail.pos.service;

import com.kiotretail.invoice.model.Order;
import com.kiotretail.invoice.model.OrderDetail;
import com.kiotretail.invoice.model.Payment;
import com.kiotretail.invoice.service.InvoiceService;
import com.kiotretail.pos.dto.CartItem;
import com.kiotretail.pos.dto.CartSession;
import com.kiotretail.product.dao.ProductDAO;
import com.kiotretail.product.model.Product;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.ServiceException;
import com.kiotretail.shared.exception.ValidationException;
import com.kiotretail.shared.util.CodeGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling POS sale operations.
 * Uses ProductDAO for product search and InvoiceService for checkout.
 */
public class POSService {

    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Search products by keyword (name/SKU/barcode), limited by the given count.
     *
     * @param keyword search term
     * @param limit   maximum number of results, defaults to 10 when not positive
     * @return matching products
     */
    public List<Product> searchProducts(String keyword, int limit) {
        int effectiveLimit = (limit > 0) ? limit : 10;
        return productDAO.searchByKeyword(keyword, effectiveLimit);
    }

    /**
     * Checkout the current cart: persist the order, its line items, and payment record.
     *
     * @param cart          current cart session
     * @param employeeId    cashier id
     * @param branchId      branch id where sale occurs
     * @param paymentMethod payment method (cash, card, transfer, etc.)
     * @return generated orderId
     * @throws ValidationException if cart is null or empty
     * @throws ServiceException    if order creation fails
     */
    public int checkout(CartSession cart, int employeeId, int branchId, String paymentMethod) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ValidationException(ErrorMessages.CART_EMPTY);
        }

        // Build Order header
        Order order = new Order();
        order.setBranchId(branchId);
        order.setEmployeeId(employeeId);
        order.setCustomerId(cart.getCustomerId());
        order.setOrderType(AppConstants.ORDER_TYPE_SALE);
        order.setOrderCode(CodeGenerator.generateOrderCode());
        order.setStatus(AppConstants.STATUS_COMPLETED);
        order.setSubtotal(cart.getSubtotal());
        order.setDiscountAmount(cart.getDiscount());
        order.setTotalAmount(cart.getTotal());

        // Build OrderDetail list from cart items
        List<OrderDetail> details = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            OrderDetail od = new OrderDetail();
            od.setProductId(ci.getProductId());
            od.setQuantity(ci.getQuantity());
            od.setUnitPrice(ci.getUnitPrice());
            od.setSubtotal(ci.getSubtotal());
            od.setProductName(ci.getProductName());
            od.setProductSku(ci.getSku());
            details.add(od);
        }

        // Persist order + details via InvoiceService
        InvoiceService invoiceService = new InvoiceService();
        int orderId = invoiceService.createOrder(order, details);
        if (orderId <= 0) {
            throw new ServiceException(String.format(ErrorMessages.CREATE_FAILED, "don hang"));
        }

        // Build and persist Payment record
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentMethod(paymentMethod);
        BigDecimal amount = cart.getTotal();
        payment.setAmount(amount == null ? BigDecimal.ZERO : amount);
        payment.setStatus(AppConstants.STATUS_COMPLETED);
        payment.setPaidAt(new Timestamp(System.currentTimeMillis()));

        invoiceService.addPayment(payment);

        return orderId;
    }
}
