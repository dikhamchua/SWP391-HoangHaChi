-- Migration: Create PurchaseOrderHistory table (module-owned audit trail)
-- Replaces polymorphic ApprovalHistory for the purchase module.

CREATE TABLE IF NOT EXISTS PurchaseOrderHistory (
    HistoryID       INT AUTO_INCREMENT PRIMARY KEY,
    PurchaseOrderID INT NOT NULL,
    FromStatus      VARCHAR(30) NULL,
    ToStatus        VARCHAR(30) NOT NULL,
    Action          VARCHAR(30) NOT NULL,
    PerformedBy     INT NOT NULL,
    Reason          TEXT NULL,
    CreatedAt       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_POHistory_PurchaseOrder
        FOREIGN KEY (PurchaseOrderID) REFERENCES PurchaseOrder(PurchaseOrderID)
        ON DELETE CASCADE,

    CONSTRAINT FK_POHistory_Employee
        FOREIGN KEY (PerformedBy) REFERENCES Employee(EmployeeID)
);

CREATE INDEX IX_POHistory_OrderID ON PurchaseOrderHistory(PurchaseOrderID);
CREATE INDEX IX_POHistory_CreatedAt ON PurchaseOrderHistory(CreatedAt);