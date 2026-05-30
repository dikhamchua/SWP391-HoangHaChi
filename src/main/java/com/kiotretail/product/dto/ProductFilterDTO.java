package com.kiotretail.product.dto;

/**
 * Filter DTO for product search/list operations.
 * Carries query parameters from the controller layer down to DAO/service.
 * No validation is performed here; callers are responsible for sanitising input.
 */
public class ProductFilterDTO {

    private String keyword;
    private Integer categoryId;
    private String status;
    private Integer supplierId;
    private String productType;
    private String sortBy = "createdAt";
    private String sortDir = "DESC";

    public ProductFilterDTO() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
