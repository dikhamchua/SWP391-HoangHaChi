package com.kiotretail.inventory.model;

import java.math.BigDecimal;

/**
 * StockAdjustmentDetail POJO mapping the StockAdjustmentDetail table.
 *
 * <p>DB columns: AdjustmentDetailID, AdjustmentID, ProductID, SystemQuantity,
 * ActualQuantity, Variance, VarianceValue, Reason.</p>
 *
 * <p>Joined fields: productName, productSku.</p>
 */
public class StockAdjustmentDetail {

    private int adjustmentDetailId;
    private int adjustmentId;
    private int productId;
    private int systemQuantity;
    private int actualQuantity;
    private int variance;
    private BigDecimal varianceValue;
    private String reason;

    private String productName;
    private String productSku;

    public StockAdjustmentDetail() {
    }

    public int getAdjustmentDetailId() { return adjustmentDetailId; }
    public void setAdjustmentDetailId(int adjustmentDetailId) { this.adjustmentDetailId = adjustmentDetailId; }

    public int getAdjustmentId() { return adjustmentId; }
    public void setAdjustmentId(int adjustmentId) { this.adjustmentId = adjustmentId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSystemQuantity() { return systemQuantity; }
    public void setSystemQuantity(int systemQuantity) { this.systemQuantity = systemQuantity; }

    public int getActualQuantity() { return actualQuantity; }
    public void setActualQuantity(int actualQuantity) { this.actualQuantity = actualQuantity; }

    public int getVariance() { return variance; }
    public void setVariance(int variance) { this.variance = variance; }

    public BigDecimal getVarianceValue() { return varianceValue; }
    public void setVarianceValue(BigDecimal varianceValue) { this.varianceValue = varianceValue; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
}
