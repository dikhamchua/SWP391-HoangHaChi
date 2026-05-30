package com.kiotretail.product.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product POJO mapping the Product table.
 *
 * DB columns: ProductID, CategoryID, Name, SKU, Price, CostPrice,
 *             StockAlertQty, Status, CreatedAt
 *
 * Extra fields (not in DB):
 * - categoryName: populated by JOIN with Category table
 * - imageUrl, unit: reserved for future use
 */
public class Product {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";

    private int productId;
    private int categoryId;
    private String categoryName;
    private String productName;
    private String sku;
    private BigDecimal price;
    private BigDecimal costPrice;
    private int stockAlertQty;
    private String status;
    private Timestamp createdAt;
    private String imageUrl;
    private String unit;

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public int getStockAlertQty() {
        return stockAlertQty;
    }

    public void setStockAlertQty(int stockAlertQty) {
        this.stockAlertQty = stockAlertQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return true if status equals "active" (case-insensitive)
     */
    public boolean isActive() {
        return STATUS_ACTIVE.equalsIgnoreCase(status);
    }

    /**
     * Check whether the supplied warehouse quantity is below the alert threshold.
     *
     * @param warehouseQty current stock on hand
     * @return true if warehouseQty is strictly less than stockAlertQty
     */
    public boolean isLowStock(int warehouseQty) {
        return warehouseQty < stockAlertQty;
    }
}
