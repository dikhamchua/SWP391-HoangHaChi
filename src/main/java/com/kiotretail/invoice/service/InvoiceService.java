package com.kiotretail.invoice.service;

import com.kiotretail.invoice.dao.OrderDAO;
import com.kiotretail.invoice.dao.OrderDetailDAO;
import com.kiotretail.invoice.dao.PaymentDAO;
import com.kiotretail.invoice.dto.InvoiceFilterDTO;
import com.kiotretail.invoice.model.Order;
import com.kiotretail.invoice.model.OrderDetail;
import com.kiotretail.invoice.model.Payment;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.exception.NotFoundException;
import com.kiotretail.shared.exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service that orchestrates Order + OrderDetail + Payment operations for the invoice module.
 *
 * Validation rules:
 * - Orders must have a branchId, employeeId, orderType, and at least one detail line.
 * - Subtotal is recomputed from details; totalAmount = subtotal - discountAmount.
 * - Payments must reference an existing order.
 */
public class InvoiceService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public PageResult<Order> listOrders(InvoiceFilterDTO filter, Pagination pagination) {
        List<Order> items = orderDAO.getOrders(filter, pagination);
        int total = orderDAO.countOrders(filter);
        return PageResult.of(items, total, pagination);
    }

    public Order getOrderById(int id) {
        Order order = orderDAO.getById(id);
        if (order == null) {
            throw new NotFoundException("Order", id);
        }
        return order;
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        return orderDetailDAO.getByOrderId(orderId);
    }

    public List<Payment> getOrderPayments(int orderId) {
        return paymentDAO.getByOrderId(orderId);
    }

    public int createOrder(Order order, List<OrderDetail> details) {
        validateOrder(order, details);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderDetail detail : details) {
            BigDecimal lineSubtotal = detail.getSubtotal();
            if (lineSubtotal == null) {
                BigDecimal unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : BigDecimal.ZERO;
                lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
                detail.setSubtotal(lineSubtotal);
            }
            subtotal = subtotal.add(lineSubtotal);
        }

        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTotalAmount(subtotal.subtract(discount));

        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus(AppConstants.STATUS_PENDING);
        }

        int orderId = orderDAO.insert(order);
        if (orderId <= 0) {
            throw new ValidationException(String.format(ErrorMessages.CREATE_FAILED, "don hang"));
        }

        for (OrderDetail detail : details) {
            detail.setOrderId(orderId);
        }
        orderDetailDAO.insertBatch(orderId, details);

        return orderId;
    }

    public boolean cancelOrder(int orderId) {
        Order order = orderDAO.getById(orderId);
        if (order == null) {
            throw new NotFoundException("Order", orderId);
        }
        return orderDAO.updateStatus(orderId, AppConstants.STATUS_CANCELLED);
    }

    public boolean addPayment(Payment payment) {
        if (payment == null) {
            throw new ValidationException(String.format(ErrorMessages.NOT_FOUND, "Payment"));
        }

        Order order = orderDAO.getById(payment.getOrderId());
        if (order == null) {
            throw new NotFoundException("Order", payment.getOrderId());
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(ErrorMessages.PAYMENT_INVALID);
        }

        boolean inserted = paymentDAO.insert(payment);
        if (!inserted) {
            return false;
        }

        BigDecimal totalPaid = BigDecimal.ZERO;
        List<Payment> payments = paymentDAO.getByOrderId(payment.getOrderId());
        for (Payment p : payments) {
            if (p.getAmount() != null) {
                totalPaid = totalPaid.add(p.getAmount());
            }
        }

        BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        if (totalPaid.compareTo(totalAmount) >= 0) {
            orderDAO.updateStatus(payment.getOrderId(), AppConstants.STATUS_COMPLETED);
        }

        return true;
    }

    private void validateOrder(Order order, List<OrderDetail> details) {
        if (order == null) {
            throw new ValidationException("Order must not be null");
        }
        if (order.getBranchId() <= 0) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Chi nhanh"));
        }
        if (order.getEmployeeId() <= 0) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Nhan vien"));
        }
        if (order.getOrderType() == null || order.getOrderType().trim().isEmpty()) {
            throw new ValidationException(String.format(ErrorMessages.FIELD_REQUIRED, "Loai don hang"));
        }
        if (details == null || details.isEmpty()) {
            throw new ValidationException(ErrorMessages.ORDER_EMPTY);
        }
    }
}
