-- =========================================
-- CREATE DATABASE
-- =========================================
CREATE DATABASE DBFinora;
GO

USE DBFinora;
GO

-- =========================================
-- ROLE
-- =========================================
CREATE TABLE Role (
    RoleID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) UNIQUE NOT NULL,
    Description NVARCHAR(MAX)
);

-- =========================================
-- BRANCH
-- =========================================
CREATE TABLE Branch (
    BranchID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(255) NOT NULL,
    Address NVARCHAR(MAX),
    Phone VARCHAR(20),
    Status VARCHAR(20) DEFAULT 'active',
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- =========================================
-- EMPLOYEE
-- =========================================
CREATE TABLE Employee (
    EmployeeID INT IDENTITY(1,1) PRIMARY KEY,
    RoleID INT NOT NULL,
    BranchID INT NOT NULL,

    FullName NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) UNIQUE,
    Phone VARCHAR(20),

    PasswordHash NVARCHAR(255) NOT NULL,

    Status VARCHAR(20) DEFAULT 'active',

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Employee_Role
        FOREIGN KEY (RoleID)
        REFERENCES Role(RoleID),

    CONSTRAINT FK_Employee_Branch
        FOREIGN KEY (BranchID)
        REFERENCES Branch(BranchID)
);

-- =========================================
-- CUSTOMER
-- =========================================
CREATE TABLE Customer (
    CustomerID INT IDENTITY(1,1) PRIMARY KEY,

    FullName NVARCHAR(255) NOT NULL,
    Phone VARCHAR(20) UNIQUE,
    Email NVARCHAR(255),
    Address NVARCHAR(MAX),

    DoB DATE,

    Gender VARCHAR(20),

    MembershipTier NVARCHAR(50),

    Points INT DEFAULT 0,

    CreatedAt DATETIME DEFAULT GETDATE()
);

-- =========================================
-- SUPPLIER
-- =========================================
CREATE TABLE Supplier (
    SupplierID INT IDENTITY(1,1) PRIMARY KEY,

    Name NVARCHAR(255) NOT NULL,
    Phone VARCHAR(20),
    Email NVARCHAR(255),
    Address NVARCHAR(MAX),

    Status VARCHAR(20) DEFAULT 'active',

    CreatedAt DATETIME DEFAULT GETDATE()
);

-- =========================================
-- CATEGORY
-- =========================================
CREATE TABLE Category (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,

    Name NVARCHAR(255) NOT NULL,

    Description NVARCHAR(MAX),

    ParentID INT NULL,

    Status VARCHAR(20) DEFAULT 'active',

    CONSTRAINT FK_Category_Parent
        FOREIGN KEY (ParentID)
        REFERENCES Category(CategoryID)
);

-- =========================================
-- PRODUCT
-- =========================================
CREATE TABLE Product (
    ProductID INT IDENTITY(1,1) PRIMARY KEY,

    CategoryID INT NOT NULL,

    Name NVARCHAR(255) NOT NULL,

    SKU VARCHAR(100) UNIQUE NOT NULL,

    Price DECIMAL(18,2) DEFAULT 0,

    CostPrice DECIMAL(18,2) DEFAULT 0,

    StockAlertQty INT DEFAULT 0,

    Status VARCHAR(20) DEFAULT 'active',

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Product_Category
        FOREIGN KEY (CategoryID)
        REFERENCES Category(CategoryID)
);

-- =========================================
-- WAREHOUSE
-- =========================================
CREATE TABLE Warehouse (
    WarehouseID INT IDENTITY(1,1) PRIMARY KEY,

    BranchID INT NOT NULL,
    EmployeeID INT NOT NULL,
    ProductID INT NOT NULL,

    Name NVARCHAR(255) NOT NULL,

    Address NVARCHAR(MAX),

    Status VARCHAR(20) DEFAULT 'active',

    Quantity INT DEFAULT 0,

    AvailableQuantity INT DEFAULT 0,

    MinQuantity INT DEFAULT 0,

    MaxQuantity INT DEFAULT 0,

    UpdatedAt DATETIME DEFAULT GETDATE(),

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Warehouse_Branch
        FOREIGN KEY (BranchID)
        REFERENCES Branch(BranchID),

    CONSTRAINT FK_Warehouse_Employee
        FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID),

    CONSTRAINT FK_Warehouse_Product
        FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
);

-- =========================================
-- ORDER
-- =========================================
CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,

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

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Orders_Branch
        FOREIGN KEY (BranchID)
        REFERENCES Branch(BranchID),

    CONSTRAINT FK_Orders_Employee
        FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID),

    CONSTRAINT FK_Orders_Customer
        FOREIGN KEY (CustomerID)
        REFERENCES Customer(CustomerID),

    CONSTRAINT FK_Orders_Supplier
        FOREIGN KEY (SupplierID)
        REFERENCES Supplier(SupplierID)
);

-- =========================================
-- ORDER DETAIL
-- =========================================
CREATE TABLE OrderDetail (
    OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,

    OrderID INT NOT NULL,

    ProductID INT NOT NULL,

    Quantity INT NOT NULL DEFAULT 1,

    UnitPrice DECIMAL(18,2) NOT NULL DEFAULT 0,

    Subtotal DECIMAL(18,2) NOT NULL DEFAULT 0,

    CONSTRAINT FK_OrderDetail_Order
        FOREIGN KEY (OrderID)
        REFERENCES Orders(OrderID),

    CONSTRAINT FK_OrderDetail_Product
        FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
);

-- =========================================
-- PAYMENTS
-- =========================================
CREATE TABLE Payments (
    PaymentsID INT IDENTITY(1,1) PRIMARY KEY,

    OrderID INT NOT NULL,

    PaymentMethod VARCHAR(50) NOT NULL,

    Amount DECIMAL(18,2) NOT NULL DEFAULT 0,

    PaidAt DATETIME NULL,

    Reference NVARCHAR(255),

    Status VARCHAR(20) DEFAULT 'pending',

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Payments_Order
        FOREIGN KEY (OrderID)
        REFERENCES Orders(OrderID)
);

-- =========================================
-- FINANCE TRANSACTION
-- =========================================
CREATE TABLE FinanceTransaction (
    TransactionID INT IDENTITY(1,1) PRIMARY KEY,

    BranchID INT NOT NULL,

    EmployeeID INT NOT NULL,

    TransactionCode VARCHAR(100) UNIQUE NOT NULL,

    TransactionDate DATETIME DEFAULT GETDATE(),

    TransactionType VARCHAR(50) NOT NULL,

    Amount DECIMAL(18,2) NOT NULL DEFAULT 0,

    ReferenceID INT NULL,

    ReferenceType VARCHAR(100) NULL,

    Note NVARCHAR(MAX),

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_FinanceTransaction_Branch
        FOREIGN KEY (BranchID)
        REFERENCES Branch(BranchID),

    CONSTRAINT FK_FinanceTransaction_Employee
        FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID)
);

-- =========================================
-- STOCK TRANSFER
-- =========================================
CREATE TABLE StockTransfer (
    StockTransferID INT IDENTITY(1,1) PRIMARY KEY,

    BranchID INT NOT NULL,

    EmployeeID INT NOT NULL,

    ProductID INT NOT NULL,

    FromWarehouseID INT NOT NULL,

    ToWarehouseID INT NOT NULL,

    TransferCode VARCHAR(100) UNIQUE NOT NULL,

    TransferDate DATETIME DEFAULT GETDATE(),

    Quantity INT NOT NULL DEFAULT 0,

    Status VARCHAR(20) DEFAULT 'pending',

    Note NVARCHAR(MAX),

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_StockTransfer_Branch
        FOREIGN KEY (BranchID)
        REFERENCES Branch(BranchID),

    CONSTRAINT FK_StockTransfer_Employee
        FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID),

    CONSTRAINT FK_StockTransfer_Product
        FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID),

    CONSTRAINT FK_StockTransfer_FromWarehouse
        FOREIGN KEY (FromWarehouseID)
        REFERENCES Warehouse(WarehouseID),

    CONSTRAINT FK_StockTransfer_ToWarehouse
        FOREIGN KEY (ToWarehouseID)
        REFERENCES Warehouse(WarehouseID)
);

-- =========================================
-- WAREHOUSE TRANSACTION
-- =========================================
CREATE TABLE WarehouseTransaction (
    WarehouseTransactionID INT IDENTITY(1,1) PRIMARY KEY,

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

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_WarehouseTransaction_Warehouse
        FOREIGN KEY (WarehouseID)
        REFERENCES Warehouse(WarehouseID),

    CONSTRAINT FK_WarehouseTransaction_Product
        FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID),

    CONSTRAINT FK_WarehouseTransaction_Employee
        FOREIGN KEY (CreatedBy)
        REFERENCES Employee(EmployeeID)
);

-- =========================================
-- AUDIT LOG
-- =========================================
CREATE TABLE AuditLog (
    AuditLogID INT IDENTITY(1,1) PRIMARY KEY,

    EmployeeID INT NOT NULL,

    Action NVARCHAR(255) NOT NULL,

    EntityName NVARCHAR(255) NOT NULL,

    EntityID INT NOT NULL,

    OldData NVARCHAR(MAX),

    NewData NVARCHAR(MAX),

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_AuditLog_Employee
        FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID)
);

-- =========================================
-- INDEXES
-- =========================================

CREATE INDEX IDX_Product_Name
ON Product(Name);

CREATE INDEX IDX_Product_SKU
ON Product(SKU);

CREATE INDEX IDX_Orders_OrderCode
ON Orders(OrderCode);

CREATE INDEX IDX_Customer_Phone
ON Customer(Phone);

CREATE INDEX IDX_Employee_Email
ON Employee(Email);










-- =========================================
-- DEMO ROLES
-- =========================================
INSERT INTO Role (Name, Description)
VALUES
('Owner', 'Full system access'),
('StoreManager', 'Manage branch and employees'),
('SalesStaff', 'Sales and cashier staff'),
('WarehouseStaff', 'Warehouse management');
GO

-- =========================================
-- DEMO BRANCHES
-- =========================================
INSERT INTO Branch (
    Name,
    Address,
    Phone,
    Status
)
VALUES
(
    N'Chi Nhánh Hà Nội',
    N'123 Hoàn Kiếm, Hà Nội',
    '0900000001',
    'active'
),
(
    N'Chi Nhánh TP.HCM',
    N'456 Quận 1, TP.HCM',
    '0900000002',
    'active'
);
GO

-- =========================================
-- DEMO EMPLOYEES
-- Password demo: 123456
-- =========================================

-- OWNER
INSERT INTO Employee (
    RoleID,
    BranchID,
    FullName,
    Email,
    Phone,
    PasswordHash,
    Status
)
VALUES
(
    1,
    1,
    N'Nguyễn Hoàng Owner',
    'owner@retail.com',
    '0911111111',
    '123456',
    'active'
);

-- STORE MANAGER
INSERT INTO Employee (
    RoleID,
    BranchID,
    FullName,
    Email,
    Phone,
    PasswordHash,
    Status
)
VALUES
(
    2,
    1,
    N'Trần Văn Manager',
    'manager@retail.com',
    '0922222222',
    '123456',
    'active'
);

-- SALES STAFF 1
INSERT INTO Employee (
    RoleID,
    BranchID,
    FullName,
    Email,
    Phone,
    PasswordHash,
    Status
)
VALUES
(
    3,
    1,
    N'Lê Minh Sales',
    'sales1@retail.com',
    '0933333333',
    '123456',
    'active'
);

-- SALES STAFF 2
INSERT INTO Employee (
    RoleID,
    BranchID,
    FullName,
    Email,
    Phone,
    PasswordHash,
    Status
)
VALUES
(
    3,
    2,
    N'Phạm Lan Sales',
    'sales2@retail.com',
    '0933333344',
    '123456',
    'active'
);

-- WAREHOUSE STAFF
INSERT INTO Employee (
    RoleID,
    BranchID,
    FullName,
    Email,
    Phone,
    PasswordHash,
    Status
)
VALUES
(
    4,
    1,
    N'Đặng Kho Staff',
    'warehouse@retail.com',
    '0944444444',
    '123456',
    'active'
);
GO

-- =========================================
-- DEMO CATEGORIES
-- =========================================
INSERT INTO Category (
    Name,
    Description,
    Status
)
VALUES
(
    N'Nước Giải Khát',
    N'Đồ uống có gas và nước ngọt',
    'active'
),
(
    N'Bánh Kẹo',
    N'Bánh snack và kẹo',
    'active'
),
(
    N'Mì Gói',
    N'Mì ăn liền',
    'active'
);
GO

-- =========================================
-- DEMO PRODUCTS
-- =========================================
INSERT INTO Product (
    CategoryID,
    Name,
    SKU,
    Price,
    CostPrice,
    StockAlertQty,
    Status
)
VALUES
(
    1,
    N'Coca Cola',
    'COCA001',
    10000,
    7000,
    20,
    'active'
),
(
    1,
    N'Pepsi',
    'PEPSI001',
    9000,
    6500,
    20,
    'active'
),
(
    1,
    N'7Up',
    '7UP001',
    9000,
    6000,
    20,
    'active'
),
(
    2,
    N'Oreo Chocolate',
    'OREO001',
    15000,
    10000,
    10,
    'active'
),
(
    2,
    N'KitKat',
    'KITKAT001',
    12000,
    8000,
    10,
    'active'
),
(
    3,
    N'Mì Hảo Hảo',
    'HAOHAO001',
    5000,
    3500,
    50,
    'active'
),
(
    3,
    N'Mì Omachi',
    'OMACHI001',
    8000,
    5500,
    30,
    'active'
);
GO

-- =========================================
-- DEMO WAREHOUSE
-- =========================================
INSERT INTO Warehouse (
    BranchID,
    EmployeeID,
    ProductID,
    Name,
    Address,
    Status,
    Quantity,
    AvailableQuantity,
    MinQuantity,
    MaxQuantity
)
VALUES
(
    1,
    5,
    1,
    N'Kho Tổng Hà Nội',
    N'Khu Công Nghiệp Long Biên - Hà Nội',
    'active',
    1000,
    950,
    100,
    5000
),
(
    1,
    5,
    2,
    N'Kho Đồ Uống Hà Nội',
    N'Hoàng Mai - Hà Nội',
    'active',
    700,
    650,
    50,
    3000
),
(
    2,
    5,
    4,
    N'Kho TP.HCM',
    N'Quận 12 - TP.HCM',
    'active',
    1200,
    1150,
    100,
    6000
);
GO

-- =========================================
-- DEMO SUPPLIERS
-- =========================================
INSERT INTO Supplier (
    Name,
    Phone,
    Email,
    Address,
    Status
)
VALUES
(
    N'Công Ty Coca Cola Việt Nam',
    '0909999999',
    'coca@supplier.com',
    N'Hà Nội',
    'active'
),
(
    N'Công Ty Pepsi Việt Nam',
    '0918888888',
    'pepsi@supplier.com',
    N'TP.HCM',
    'active'
),
(
    N'Công Ty Acecook',
    '0927777777',
    'acecook@supplier.com',
    N'Bình Dương',
    'active'
);
GO

-- =========================================
-- DEMO CUSTOMERS
-- =========================================
INSERT INTO Customer (
    FullName,
    Phone,
    Email,
    Address,
    Gender,
    MembershipTier,
    Points
)
VALUES
(
    N'Nguyễn Văn A',
    '0988888888',
    'customer1@gmail.com',
    N'Hà Nội',
    'male',
    'silver',
    120
),
(
    N'Trần Thị B',
    '0977777777',
    'customer2@gmail.com',
    N'TP.HCM',
    'female',
    'gold',
    450
),
(
    N'Lê Văn C',
    '0966666666',
    'customer3@gmail.com',
    N'Đà Nẵng',
    'male',
    'member',
    50
);
GO

-- =========================================
-- DEMO LOGIN INFO
-- =========================================
/*
=========================================
OWNER
=========================================
Email: owner@retail.com
Password: 123456

=========================================
STORE MANAGER
=========================================
Email: manager@retail.com
Password: 123456

=========================================
SALES STAFF 1
=========================================
Email: sales1@retail.com
Password: 123456

=========================================
SALES STAFF 2
=========================================
Email: sales2@retail.com
Password: 123456

=========================================
WAREHOUSE STAFF
=========================================
Email: warehouse@retail.com
Password: 123456
*/