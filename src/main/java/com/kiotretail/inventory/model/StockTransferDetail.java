package com.kiotretail.inventory.model;

/**
 * StockTransferDetail POJO mapping the StockTransferDetail table.
 *
 * <p>DB columns: TransferDetailID, TransferID, ProductID, Quantity,
 * ReceivedQuantity, Note.</p>
 *
 * <p>Joined fields: productName, productSku.</p>
 */
public class StockTransferDetail {

    private int transferDetailId;
    private int transferId;
    private int productId;
    private int quantity;
    private int receivedQuantity;
    private String note;

    private String productName;
    private String productSku;

    public StockTransferDetail() {}

    public int getTransferDetailId() { return transferDetailId; }
    public void setTransferDetailId(int transferDetailId) { this.transferDetailId = transferDetailId; }

    public int getTransferId() { return transferId; }
    public void setTransferId(int transferId) { this.transferId = transferId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(int receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public int getOutstandingQuantity() {
        return quantity - receivedQuantity;
    }
}
