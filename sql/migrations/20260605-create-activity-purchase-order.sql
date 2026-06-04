CREATE TABLE IF NOT EXISTS ActivityPurchaseOrder (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    FK_ID INT NOT NULL,
    Type VARCHAR(20) NOT NULL,
    CreatedBy INT NULL,
    Description TEXT NOT NULL,
    CONSTRAINT FK_ActivityPurchaseOrder_CreatedBy
        FOREIGN KEY (CreatedBy) REFERENCES Employee(EmployeeID),
    CONSTRAINT CK_ActivityPurchaseOrder_Type
        CHECK (Type IN ('add', 'update', 'delete', 'other'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_ActivityPurchaseOrder_FK_ID
    ON ActivityPurchaseOrder(FK_ID, ID DESC);
