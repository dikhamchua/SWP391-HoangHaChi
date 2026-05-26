package com.kiotretail.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private int product_id;
    private String product_code;
    private String product_name;
    private int categoryId;
    private String unit;
    private BigDecimal costPrice;     // Kiểu DECIMAL trong SQL ứng với BigDecimal trong Java
    private BigDecimal sellingPrice;  // Hoặc dùng double cũng được, nhưng BigDecimal là chuẩn nhất cho tiền tệ
    private int stockQuantity;
    private int minStock;
    private int maxStock;
    private String imageUrl;
    private String status;

    public Product(int product_id, String product_code, String product_name, int categoryId, String unit, BigDecimal costPrice, BigDecimal sellingPrice, int stockQuantity, int minStock, int maxStock, String imageUrl, String status) {
        this.product_id = product_id;
        this.product_code = product_code;
        this.product_name = product_name;
        this.categoryId = categoryId;
        this.unit = unit;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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
}
