package com.kiotretail.api.dto;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;
    private int parentId;

    public Category(int categoryId, String categoryName, String description, int parentId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.parentId = parentId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
