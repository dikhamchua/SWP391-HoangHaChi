package com.kiotretail.purchase.model;

import java.math.BigDecimal;

/**
 * PurchaseOrderDetail POJO mapping the PurchaseOrderDetail table.
 *
 * <p>DB columns: PODetailID, PurchaseOrderID, ProductID, Quantity, UnitCost,
 * Subtotal, ReceivedQuantity (default 0).</p>
 *
 * <p>Extra (join) fields: productName, productSku.</p>
 */
public class PurchaseOrderDetail {

    private int poDetailId;
    private int purchaseOrderId;
    private int productId;
    private int quantity;
    private int receivedQuantity;
    private BigDecimal unitCost;
    private BigDecimal subtotal;

    private String productName;
    private String productSku;

    public PurchaseOrderDetail() {
    }

    public int getPoDetailId() {
        return poDetailId;
    }

    public void setPoDetailId(int poDetailId) {
        this.poDetailId = poDetailId;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    /**
     * Quantity still outstanding (ordered - received).
     * Always non-negative; clamps when received already meets or exceeds order.
     */
    public int getOutstandingQuantity() {
        int outstanding = quantity - receivedQuantity;
        return outstanding < 0 ? 0 : outstanding;
    }

    /**
     * True when at least part of this line has been received.
     */
    public boolean isPartiallyReceived() {
        return receivedQuantity > 0 && receivedQuantity < quantity;
    }

    /**
     * True when the line is fully received.
     */
    public boolean isFullyReceived() {
        return quantity > 0 && receivedQuantity >= quantity;
    }
}
