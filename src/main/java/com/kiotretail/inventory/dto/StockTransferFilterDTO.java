package com.kiotretail.inventory.dto;

/**
 * Filter parameters for the stock transfer list page.
 */
public class StockTransferFilterDTO {

    private String keyword;
    private Integer fromBranchId;
    private Integer toBranchId;
    private String status;
    private String dateFrom;
    private String dateTo;
    private Integer createdBy;

    public StockTransferFilterDTO() {}

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Integer getFromBranchId() { return fromBranchId; }
    public void setFromBranchId(Integer fromBranchId) { this.fromBranchId = fromBranchId; }

    public Integer getToBranchId() { return toBranchId; }
    public void setToBranchId(Integer toBranchId) { this.toBranchId = toBranchId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }

    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public boolean hasKeyword() { return keyword != null && !keyword.trim().isEmpty(); }
    public boolean hasStatus() { return status != null && !status.trim().isEmpty(); }
    public boolean hasDateFrom() { return dateFrom != null && !dateFrom.trim().isEmpty(); }
    public boolean hasDateTo() { return dateTo != null && !dateTo.trim().isEmpty(); }
}
