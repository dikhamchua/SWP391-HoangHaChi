package com.kiotretail.pos.dto;

import java.math.BigDecimal;

public class CartItem {
    private int productId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    public CartItem() {
    }

    public CartItem(int productId, String productName, String sku, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        recalculate();
    }

    public void recalculate() {
        if (unitPrice == null) {
            this.subtotal = BigDecimal.ZERO;
        } else {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
