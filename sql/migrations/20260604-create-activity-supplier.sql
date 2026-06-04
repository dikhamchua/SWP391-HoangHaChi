CREATE TABLE IF NOT EXISTS ActivitySupplier (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    FK_ID INT NOT NULL,
    Type VARCHAR(20) NOT NULL,
    CreatedBy INT NULL,
    Description TEXT NOT NULL,
    CONSTRAINT FK_ActivitySupplier_CreatedBy
        FOREIGN KEY (CreatedBy) REFERENCES Employee(EmployeeID),
    CONSTRAINT CK_ActivitySupplier_Type
        CHECK (Type IN ('add', 'update', 'delete', 'other'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_ActivitySupplier_FK_ID
    ON ActivitySupplier(FK_ID, ID DESC);
