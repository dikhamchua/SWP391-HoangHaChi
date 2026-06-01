package com.kiotretail.inventory.dto;

/**
 * Filter parameters for the stock adjustment list page.
 *
 * <p>All fields are optional; null/blank values are treated as "no filter".
 * Date strings use ISO format (yyyy-MM-dd) to match HTML &lt;input type="date"&gt;.</p>
 */
public class StockAdjustmentFilterDTO {

    private String keyword;
    private Integer branchId;
    private String status;
    private String dateFrom;
    private String dateTo;
    private Integer createdBy;

    public StockAdjustmentFilterDTO() {
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }

    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean hasDateFrom() {
        return dateFrom != null && !dateFrom.trim().isEmpty();
    }

    public boolean hasDateTo() {
        return dateTo != null && !dateTo.trim().isEmpty();
    }
}
