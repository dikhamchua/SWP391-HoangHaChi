package com.kiotretail.pos.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartSession {
    private List<CartItem> items = new ArrayList<>();
    private Integer customerId;
    private String customerName;
    private BigDecimal discount = BigDecimal.ZERO;

    public void addItem(CartItem item) {
        if (item == null) {
            return;
        }
        for (CartItem existing : items) {
            if (existing.getProductId() == item.getProductId()) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                existing.recalculate();
                return;
            }
        }
        item.recalculate();
        items.add(item);
    }

    public void removeItem(int productId) {
        Iterator<CartItem> it = items.iterator();
        while (it.hasNext()) {
            CartItem ci = it.next();
            if (ci.getProductId() == productId) {
                it.remove();
                return;
            }
        }
    }

    public void updateQuantity(int productId, int qty) {
        if (qty <= 0) {
            removeItem(productId);
            return;
        }
        for (CartItem ci : items) {
            if (ci.getProductId() == productId) {
                ci.setQuantity(qty);
                ci.recalculate();
                return;
            }
        }
    }

    public BigDecimal getSubtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem ci : items) {
            if (ci.getSubtotal() != null) {
                sum = sum.add(ci.getSubtotal());
            }
        }
        return sum;
    }

    public BigDecimal getTotal() {
        BigDecimal d = (discount == null) ? BigDecimal.ZERO : discount;
        return getSubtotal().subtract(d);
    }

    public void clear() {
        items.clear();
        customerId = null;
        customerName = null;
        discount = BigDecimal.ZERO;
    }

    public int getItemCount() {
        int total = 0;
        for (CartItem ci : items) {
            total += ci.getQuantity();
        }
        return total;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = (items == null) ? new ArrayList<CartItem>() : items;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = (discount == null) ? BigDecimal.ZERO : discount;
    }
}
