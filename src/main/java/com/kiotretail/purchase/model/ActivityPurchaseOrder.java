package com.kiotretail.purchase.model;

public class ActivityPurchaseOrder {
    private int id;
    private int fkId;
    private String type;
    private Integer createdBy;
    private String createdByName;
    private String description;

    public ActivityPurchaseOrder() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFkId() { return fkId; }
    public void setFkId(int fkId) { this.fkId = fkId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
