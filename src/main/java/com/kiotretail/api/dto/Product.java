package com.kiotretail.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private int productId;
    private String productCode;
    private String productName;
    private int categoryId;
    private String categoryName;
    private String unit;
    private BigDecimal costPrice;     // Kiểu DECIMAL trong SQL ứng với BigDecimal trong Java
    private BigDecimal sellingPrice;  // Hoặc dùng double cũng được, nhưng BigDecimal là chuẩn nhất cho tiền tệ
    private int stockQuantity;
    private int minStock;
    private int maxStock;
    private String imageUrl;
    private String status;

    public Product(int productId, String productCode, String productName, int categoryId, String categoryName, String unit, BigDecimal costPrice, BigDecimal sellingPrice, int stockQuantity, int minStock, int maxStock, String imageUrl, String status) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.unit = unit;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinStock() {
        return minStock;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
