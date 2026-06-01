-- =========================================
-- KiotRetail - MySQL Schema (converted from T-SQL)
-- Target: MySQL 8.0+
-- =========================================
DROP DATABASE IF EXISTS DBFinora;
CREATE DATABASE DBFinora CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE DBFinora;

-- =========================================
-- ROLE
-- =========================================
CREATE TABLE Role (
    RoleID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) UNIQUE NOT NULL,
    Description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- BRANCH
-- =========================================
CREATE TABLE Branch (
    BranchID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Address TEXT,
    Phone VARCHAR(20),
    Status VARCHAR(20) DEFAULT 'active',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- EMPLOYEE
-- =========================================
CREATE TABLE Employee (
    EmployeeID INT AUTO_INCREMENT PRIMARY KEY,
    RoleID INT NOT NULL,
    BranchID INT NOT NULL,
    FullName VARCHAR(255) NOT NULL,
    Email VARCHAR(255) UNIQUE,
    Phone VARCHAR(20),
    PasswordHash VARCHAR(255) NOT NULL,
    Status VARCHAR(20) DEFAULT 'active',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Employee_Role FOREIGN KEY (RoleID) REFERENCES Role(RoleID),
    CONSTRAINT FK_Employee_Branch FOREIGN KEY (BranchID) REFERENCES Branch(BranchID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- CUSTOMER
-- =========================================
CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(255) NOT NULL,
    Phone VARCHAR(20) UNIQUE,
    Email VARCHAR(255),
    Address TEXT,
    DoB DATE,
    Gender VARCHAR(20),
    MembershipTier VARCHAR(50),
    Points INT DEFAULT 0,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- SUPPLIER
-- =========================================
CREATE TABLE Supplier (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(255),
    Address TEXT,
    Status VARCHAR(20) DEFAULT 'active',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- CATEGORY
-- =========================================
CREATE TABLE Category (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description TEXT,
    ParentID INT NULL,
    Status VARCHAR(20) DEFAULT 'active',
    CONSTRAINT FK_Category_Parent FOREIGN KEY (ParentID) REFERENCES Category(CategoryID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- PRODUCT
-- =========================================
CREATE TABLE Product (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    CategoryID INT NOT NULL,
    Name VARCHAR(255) NOT NULL,
    SKU VARCHAR(100) UNIQUE NOT NULL,
    Price DECIMAL(18,2) DEFAULT 0,
    CostPrice DECIMAL(18,2) DEFAULT 0,
    StockAlertQty INT DEFAULT 0,
    StockQuantity INT DEFAULT 0,
    Status VARCHAR(20) DEFAULT 'active',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Product_Category FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- WAREHOUSE
-- =========================================
CREATE TABLE Warehouse (
    WarehouseID INT AUTO_INCREMENT PRIMARY KEY,
    BranchID INT NOT NULL,
    EmployeeID INT NOT NULL,
    ProductID INT NOT NULL,
    Name VARCHAR(255) NOT NULL,
    Address TEXT,
    Status VARCHAR(20) DEFAULT 'active',
    Quantity INT DEFAULT 0,
    AvailableQuantity INT DEFAULT 0,
    MinQuantity INT DEFAULT 0,
    MaxQuantity INT DEFAULT 0,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Warehouse_Branch FOREIGN KEY (BranchID) REFERENCES Branch(BranchID),
    CONSTRAINT FK_Warehouse_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_Warehouse_Product FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- ORDERS
-- =========================================
CREATE TABLE Orders (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    BranchID INT NOT NULL,
    EmployeeID INT NOT NULL,
    CustomerID INT NULL,
    SupplierID INT NULL,
    OrderCode VARCHAR(100) UNIQUE NOT NULL,
    OrderType VARCHAR(50) NOT NULL,
    Subtotal DECIMAL(18,2) DEFAULT 0,
    DiscountAmount DECIMAL(18,2) DEFAULT 0,
    TotalAmount DECIMAL(18,2) DEFAULT 0,
    Status VARCHAR(20) DEFAULT 'pending',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Orders_Branch FOREIGN KEY (BranchID) REFERENCES Branch(BranchID),
    CONSTRAINT FK_Orders_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_Orders_Customer FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    CONSTRAINT FK_Orders_Supplier FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- ORDER DETAIL
-- =========================================
CREATE TABLE OrderDetail (
    OrderDetailID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,
    UnitPrice DECIMAL(18,2) NOT NULL DEFAULT 0,
    Subtotal DECIMAL(18,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_OrderDetail_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    CONSTRAINT FK_OrderDetail_Product FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- PAYMENTS
-- =========================================
CREATE TABLE Payments (
    PaymentsID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    PaymentMethod VARCHAR(50) NOT NULL,
    Amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    PaidAt DATETIME NULL,
    Reference VARCHAR(255),
    Status VARCHAR(20) DEFAULT 'pending',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Payments_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- FINANCE TRANSACTION
-- =========================================
CREATE TABLE FinanceTransaction (
    TransactionID INT AUTO_INCREMENT PRIMARY KEY,
    BranchID INT NOT NULL,
    EmployeeID INT NOT NULL,
    TransactionCode VARCHAR(100) UNIQUE NOT NULL,
    TransactionDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    TransactionType VARCHAR(50) NOT NULL,
    Amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    ReferenceID INT NULL,
    ReferenceType VARCHAR(100) NULL,
    Note TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_FinanceTransaction_Branch FOREIGN KEY (BranchID) REFERENCES Branch(BranchID),
    CONSTRAINT FK_FinanceTransaction_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- STOCK TRANSFER
-- =========================================
CREATE TABLE StockTransfer (
    StockTransferID INT AUTO_INCREMENT PRIMARY KEY,
    BranchID INT NOT NULL,
    EmployeeID INT NOT NULL,
    ProductID INT NOT NULL,
    FromWarehouseID INT NOT NULL,
    ToWarehouseID INT NOT NULL,
    TransferCode VARCHAR(100) UNIQUE NOT NULL,
    TransferDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    Quantity INT NOT NULL DEFAULT 0,
    Status VARCHAR(20) DEFAULT 'pending',
    Note TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_StockTransfer_Branch FOREIGN KEY (BranchID) REFERENCES Branch(BranchID),
    CONSTRAINT FK_StockTransfer_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockTransfer_Product FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    CONSTRAINT FK_StockTransfer_FromWarehouse FOREIGN KEY (FromWarehouseID) REFERENCES Warehouse(WarehouseID),
    CONSTRAINT FK_StockTransfer_ToWarehouse FOREIGN KEY (ToWarehouseID) REFERENCES Warehouse(WarehouseID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- WAREHOUSE TRANSACTION
-- =========================================
CREATE TABLE WarehouseTransaction (
    WarehouseTransactionID INT AUTO_INCREMENT PRIMARY KEY,
    WarehouseID INT NOT NULL,
    ProductID INT NOT NULL,
    BeforeQuantity INT DEFAULT 0,
    Quantity INT NOT NULL,
    TransactionType VARCHAR(50) NOT NULL,
    AfterQuantity INT DEFAULT 0,
    UnitCost DECIMAL(18,2) DEFAULT 0,
    ReferenceType VARCHAR(100),
    ReferenceID INT,
    CreatedBy INT NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_WarehouseTransaction_Warehouse FOREIGN KEY (WarehouseID) REFERENCES Warehouse(WarehouseID),
    CONSTRAINT FK_WarehouseTransaction_Product FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    CONSTRAINT FK_WarehouseTransaction_Employee FOREIGN KEY (CreatedBy) REFERENCES Employee(EmployeeID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- AUDIT LOG
-- =========================================
CREATE TABLE AuditLog (
    AuditLogID INT AUTO_INCREMENT PRIMARY KEY,
    EmployeeID INT NOT NULL,
    Action VARCHAR(255) NOT NULL,
    EntityName VARCHAR(255) NOT NULL,
    EntityID INT NOT NULL,
    OldData TEXT,
    NewData TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_AuditLog_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- INDEXES
-- =========================================
CREATE INDEX IDX_Product_Name ON Product(Name);
CREATE INDEX IDX_Product_SKU ON Product(SKU);
CREATE INDEX IDX_Orders_OrderCode ON Orders(OrderCode);
CREATE INDEX IDX_Customer_Phone ON Customer(Phone);
CREATE INDEX IDX_Employee_Email ON Employee(Email);

-- =========================================
-- DEMO ROLES
-- =========================================
INSERT INTO Role (Name, Description) VALUES
('Owner', 'Full system access'),
('StoreManager', 'Manage branch and employees'),
('SalesStaff', 'Sales and cashier staff'),
('WarehouseStaff', 'Warehouse management');

-- =========================================
-- DEMO BRANCHES
-- =========================================
INSERT INTO Branch (Name, Address, Phone, Status) VALUES
('Chi Nhánh Hà Nội',  '123 Hoàn Kiếm, Hà Nội', '0900000001', 'active'),
('Chi Nhánh TP.HCM',  '456 Quận 1, TP.HCM',    '0900000002', 'active');

-- =========================================
-- DEMO EMPLOYEES (Password = 123456 - plaintext, sẽ tự upgrade BCrypt khi login)
-- =========================================
INSERT INTO Employee (RoleID, BranchID, FullName, Email, Phone, PasswordHash, Status) VALUES
(1, 1, 'Nguyễn Hoàng Owner',  'owner@retail.com',     '0911111111', '123456', 'active'),
(2, 1, 'Trần Văn Manager',    'manager@retail.com',   '0922222222', '123456', 'active'),
(3, 1, 'Lê Minh Sales',       'sales1@retail.com',    '0933333333', '123456', 'active'),
(3, 2, 'Phạm Lan Sales',      'sales2@retail.com',    '0933333344', '123456', 'active'),
(4, 1, 'Đặng Kho Staff',      'warehouse@retail.com', '0944444444', '123456', 'active');

-- =========================================
-- DEMO CATEGORIES
-- =========================================
INSERT INTO Category (Name, Description, Status) VALUES
('Nước Giải Khát', 'Đồ uống có gas và nước ngọt', 'active'),
('Bánh Kẹo',       'Bánh snack và kẹo',           'active'),
('Mì Gói',         'Mì ăn liền',                  'active');

-- =========================================
-- DEMO PRODUCTS
-- =========================================
INSERT INTO Product (CategoryID, Name, SKU, Price, CostPrice, StockAlertQty, StockQuantity, Status) VALUES
(1, 'Coca Cola',       'COCA001',    10000, 7000,  20, 100, 'active'),
(1, 'Pepsi',           'PEPSI001',    9000, 6500,  20, 100, 'active'),
(1, '7Up',             '7UP001',      9000, 6000,  20, 100, 'active'),
(2, 'Oreo Chocolate',  'OREO001',    15000, 10000, 10,  50, 'active'),
(2, 'KitKat',          'KITKAT001',  12000, 8000,  10,  50, 'active'),
(3, 'Mì Hảo Hảo',      'HAOHAO001',   5000, 3500,  50, 200, 'active'),
(3, 'Mì Omachi',       'OMACHI001',   8000, 5500,  30, 100, 'active');

-- =========================================
-- DEMO WAREHOUSE
-- =========================================
INSERT INTO Warehouse (BranchID, EmployeeID, ProductID, Name, Address, Status, Quantity, AvailableQuantity, MinQuantity, MaxQuantity) VALUES
(1, 5, 1, 'Kho Tổng Hà Nội',    'Khu Công Nghiệp Long Biên - Hà Nội', 'active', 1000,  950, 100, 5000),
(1, 5, 2, 'Kho Đồ Uống Hà Nội', 'Hoàng Mai - Hà Nội',                 'active',  700,  650,  50, 3000),
(2, 5, 4, 'Kho TP.HCM',         'Quận 12 - TP.HCM',                   'active', 1200, 1150, 100, 6000);

-- =========================================
-- DEMO SUPPLIERS
-- =========================================
INSERT INTO Supplier (Name, Phone, Email, Address, Status) VALUES
('Công Ty Coca Cola Việt Nam', '0909999999', 'coca@supplier.com',    'Hà Nội',     'active'),
('Công Ty Pepsi Việt Nam',     '0918888888', 'pepsi@supplier.com',   'TP.HCM',     'active'),
('Công Ty Acecook',            '0927777777', 'acecook@supplier.com', 'Bình Dương', 'active');

-- =========================================
-- DEMO CUSTOMERS
-- =========================================
INSERT INTO Customer (FullName, Phone, Email, Address, Gender, MembershipTier, Points) VALUES
('Nguyễn Văn A', '0988888888', 'customer1@gmail.com', 'Hà Nội',  'male',   'silver', 120),
('Trần Thị B',   '0977777777', 'customer2@gmail.com', 'TP.HCM',  'female', 'gold',   450),
('Lê Văn C',     '0966666666', 'customer3@gmail.com', 'Đà Nẵng', 'male',   'member',  50);

-- =========================================
-- DEMO LOGIN INFO
-- Email: owner@retail.com / manager@retail.com / sales1@retail.com / sales2@retail.com / warehouse@retail.com
-- Password (chung): 123456
-- =========================================
