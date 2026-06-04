CREATE TABLE IF NOT EXISTS ActivityCustomer (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    FK_ID INT NOT NULL,
    Type VARCHAR(20) NOT NULL,
    CreatedBy INT NULL,
    Description TEXT NOT NULL,
    CONSTRAINT FK_ActivityCustomer_CreatedBy
        FOREIGN KEY (CreatedBy) REFERENCES Employee(EmployeeID),
    CONSTRAINT CK_ActivityCustomer_Type
        CHECK (Type IN ('add', 'update', 'delete', 'other'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_ActivityCustomer_FK_ID
    ON ActivityCustomer(FK_ID, ID DESC);

