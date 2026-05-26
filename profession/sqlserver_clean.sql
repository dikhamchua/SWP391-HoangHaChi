-- ============================================
-- RETAIL CHAIN MANAGEMENT SYSTEM - SQL SERVER SCHEMA
-- Complete Database Schema with English Comments
-- ============================================

-- Create database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'RetailChainManagement')
BEGIN
    CREATE DATABASE RetailChainManagement;
END
GO

USE RetailChainManagement;
GO

-- ============================================
-- 1. USER MANAGEMENT & PERMISSIONS
-- ============================================

-- Roles table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Roles')
BEGIN
    CREATE TABLE Roles (
        RoleID INT PRIMARY KEY IDENTITY(1,1),
        RoleName NVARCHAR(50) UNIQUE NOT NULL,
        Description NVARCHAR(255),
        CreatedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Stores table (Branches)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Stores')
BEGIN
    CREATE TABLE Stores (
        StoreID INT PRIMARY KEY IDENTITY(1,1),
        StoreCode NVARCHAR(20) UNIQUE NOT NULL,
        StoreName NVARCHAR(100) NOT NULL,
        Address NVARCHAR(255),
        Phone NVARCHAR(20),
        Email NVARCHAR(100),
        Latitude DECIMAL(10, 8) NULL,
        Longitude DECIMAL(11, 8) NULL,
        WorkingHours NVARCHAR(100),
        ManagerID INT NULL,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE()
    );
    CREATE INDEX idx_store_code ON Stores(StoreCode);
END
GO

-- Users table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Users')
BEGIN
    CREATE TABLE Users (
        UserID INT PRIMARY KEY IDENTITY(1,1),
        Username NVARCHAR(50) UNIQUE NOT NULL,
        Password NVARCHAR(255) NOT NULL,
        FullName NVARCHAR(100) NOT NULL,
        Email NVARCHAR(100) UNIQUE NOT NULL,
        Phone NVARCHAR(20),
        RoleID INT NOT NULL,
        StoreID INT NULL,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Locked', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Users_Roles FOREIGN KEY (RoleID) REFERENCES Roles(RoleID),
        CONSTRAINT FK_Users_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID)
    );
    CREATE INDEX idx_username ON Users(Username);
    CREATE INDEX idx_email ON Users(Email);
    CREATE INDEX idx_role_store ON Users(RoleID, StoreID);
END
GO

-- Add foreign key for Stores.ManagerID
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_Stores_Manager')
BEGIN
    ALTER TABLE Stores 
    ADD CONSTRAINT FK_Stores_Manager 
    FOREIGN KEY (ManagerID) REFERENCES Users(UserID);
END
GO

-- Trigger to auto-update ModifiedDate for Users
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Users_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Users_UpdateModifiedDate
    ON Users
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Users
        SET ModifiedDate = GETDATE()
        FROM Users u
        INNER JOIN inserted i ON u.UserID = i.UserID;
    END
    ');
END
GO
-- Trigger to auto-update ModifiedDate for Stores
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Stores_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Stores_UpdateModifiedDate
    ON Stores
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Stores
        SET ModifiedDate = GETDATE()
        FROM Stores s
        INNER JOIN inserted i ON s.StoreID = i.StoreID;
    END
    ');
END
GO

-- Modules table (System modules)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Modules')
BEGIN
    CREATE TABLE Modules (
        ModuleID INT PRIMARY KEY IDENTITY(1,1),
        ModuleName NVARCHAR(50) UNIQUE NOT NULL,
        Description NVARCHAR(255),
        DisplayOrder INT DEFAULT 0,
        CreatedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Actions table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Actions')
BEGIN
    CREATE TABLE Actions (
        ActionID INT PRIMARY KEY IDENTITY(1,1),
        ActionName NVARCHAR(50) UNIQUE NOT NULL,
        Description NVARCHAR(255),
        CreatedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Permissions table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Permissions')
BEGIN
    CREATE TABLE Permissions (
        PermissionID INT PRIMARY KEY IDENTITY(1,1),
        ModuleID INT NOT NULL,
        ActionID INT NOT NULL,
        PermissionCode NVARCHAR(100) UNIQUE NOT NULL,
        Description NVARCHAR(255),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Permissions_Modules FOREIGN KEY (ModuleID) REFERENCES Modules(ModuleID),
        CONSTRAINT FK_Permissions_Actions FOREIGN KEY (ActionID) REFERENCES Actions(ActionID),
        CONSTRAINT UK_Module_Action UNIQUE (ModuleID, ActionID)
    );
END
GO
-- RolePermissions table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'RolePermissions')
BEGIN
    CREATE TABLE RolePermissions (
        RolePermissionID INT PRIMARY KEY IDENTITY(1,1),
        RoleID INT NOT NULL,
        PermissionID INT NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_RolePermissions_Roles FOREIGN KEY (RoleID) REFERENCES Roles(RoleID) ON DELETE CASCADE,
        CONSTRAINT FK_RolePermissions_Permissions FOREIGN KEY (PermissionID) REFERENCES Permissions(PermissionID) ON DELETE CASCADE,
        CONSTRAINT UK_Role_Permission UNIQUE (RoleID, PermissionID)
    );
END
GO

-- ActivityLogs table (Activity logs)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ActivityLogs')
BEGIN
    CREATE TABLE ActivityLogs (
        LogID BIGINT PRIMARY KEY IDENTITY(1,1),
        UserID INT NOT NULL,
        Action NVARCHAR(100) NOT NULL,
        Module NVARCHAR(50),
        Description NVARCHAR(500),
        IPAddress NVARCHAR(45),
        Timestamp DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_ActivityLogs_Users FOREIGN KEY (UserID) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_user_timestamp ON ActivityLogs(UserID, Timestamp);
    CREATE INDEX idx_module_action ON ActivityLogs(Module, Action);
END
GO

-- ============================================
-- 2. PRODUCT MANAGEMENT
-- ============================================

-- Categories table (Product categories)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Categories')
BEGIN
    CREATE TABLE Categories (
        CategoryID INT PRIMARY KEY IDENTITY(1,1),
        CategoryCode NVARCHAR(20) UNIQUE NOT NULL,
        CategoryName NVARCHAR(100) NOT NULL,
        Description NVARCHAR(500),
        ParentCategoryID INT NULL,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Categories_Parent FOREIGN KEY (ParentCategoryID) REFERENCES Categories(CategoryID)
    );
    CREATE INDEX idx_parent ON Categories(ParentCategoryID);
END
GO
-- Products table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Products')
BEGIN
    CREATE TABLE Products (
        ProductID INT PRIMARY KEY IDENTITY(1,1),
        SKU NVARCHAR(50) UNIQUE NOT NULL,
        Barcode NVARCHAR(50) UNIQUE,
        ProductName NVARCHAR(200) NOT NULL,
        CategoryID INT NOT NULL,
        Description NVARCHAR(1000),
        Cost DECIMAL(18, 2) NOT NULL DEFAULT 0,
        Price DECIMAL(18, 2) NOT NULL DEFAULT 0,
        Unit NVARCHAR(20),
        ImageURL NVARCHAR(500),
        Weight DECIMAL(10, 2),
        Brand NVARCHAR(100),
        HasVariants BIT DEFAULT 0,
        MasterProductID INT NULL,
        TrackBySerial BIT DEFAULT 0,
        Attributes NVARCHAR(MAX), -- JSON
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Products_Categories FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID),
        CONSTRAINT FK_Products_Master FOREIGN KEY (MasterProductID) REFERENCES Products(ProductID)
    );
    CREATE INDEX idx_sku ON Products(SKU);
    CREATE INDEX idx_barcode ON Products(Barcode);
    CREATE INDEX idx_category ON Products(CategoryID);
    CREATE INDEX idx_master ON Products(MasterProductID);
END
GO

-- Trigger to auto-update ModifiedDate for Products
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Products_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Products_UpdateModifiedDate
    ON Products
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Products
        SET ModifiedDate = GETDATE()
        FROM Products p
        INNER JOIN inserted i ON p.ProductID = i.ProductID;
    END
    ');
END
GO

-- CustomerGroups table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'CustomerGroups')
BEGIN
    CREATE TABLE CustomerGroups (
        CustomerGroupID INT PRIMARY KEY IDENTITY(1,1),
        GroupName NVARCHAR(50) UNIQUE NOT NULL,
        Description NVARCHAR(255),
        DiscountPercent DECIMAL(5, 2) DEFAULT 0,
        CreatedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO
-- PriceBooks table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PriceBooks')
BEGIN
    CREATE TABLE PriceBooks (
        PriceBookID INT PRIMARY KEY IDENTITY(1,1),
        PriceBookName NVARCHAR(100) NOT NULL,
        Description NVARCHAR(500),
        EffectiveDate DATE NOT NULL,
        ExpiryDate DATE NULL,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Trigger for PriceBooks
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_PriceBooks_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_PriceBooks_UpdateModifiedDate
    ON PriceBooks
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE PriceBooks
        SET ModifiedDate = GETDATE()
        FROM PriceBooks p
        INNER JOIN inserted i ON p.PriceBookID = i.PriceBookID;
    END
    ');
END
GO

-- PriceBookDetails table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PriceBookDetails')
BEGIN
    CREATE TABLE PriceBookDetails (
        PriceBookDetailID INT PRIMARY KEY IDENTITY(1,1),
        PriceBookID INT NOT NULL,
        ProductID INT NOT NULL,
        Price DECIMAL(18, 2) NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_PriceBookDetails_PriceBooks FOREIGN KEY (PriceBookID) REFERENCES PriceBooks(PriceBookID) ON DELETE CASCADE,
        CONSTRAINT FK_PriceBookDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE,
        CONSTRAINT UK_PriceBook_Product UNIQUE (PriceBookID, ProductID)
    );
END
GO

-- PriceBookStores table (Price books by branch)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PriceBookStores')
BEGIN
    CREATE TABLE PriceBookStores (
        ID INT PRIMARY KEY IDENTITY(1,1),
        PriceBookID INT NOT NULL,
        StoreID INT NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_PriceBookStores_PriceBooks FOREIGN KEY (PriceBookID) REFERENCES PriceBooks(PriceBookID) ON DELETE CASCADE,
        CONSTRAINT FK_PriceBookStores_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID) ON DELETE CASCADE,
        CONSTRAINT UK_PriceBook_Store UNIQUE (PriceBookID, StoreID)
    );
END
GO
-- PriceBookCustomerGroups table (Price books by customer group)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PriceBookCustomerGroups')
BEGIN
    CREATE TABLE PriceBookCustomerGroups (
        ID INT PRIMARY KEY IDENTITY(1,1),
        PriceBookID INT NOT NULL,
        CustomerGroupID INT NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_PriceBookCustomerGroups_PriceBooks FOREIGN KEY (PriceBookID) REFERENCES PriceBooks(PriceBookID) ON DELETE CASCADE,
        CONSTRAINT FK_PriceBookCustomerGroups_CustomerGroups FOREIGN KEY (CustomerGroupID) REFERENCES CustomerGroups(CustomerGroupID) ON DELETE CASCADE,
        CONSTRAINT UK_PriceBook_Group UNIQUE (PriceBookID, CustomerGroupID)
    );
END
GO

-- DiscountConfigs table (Discount configuration)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'DiscountConfigs')
BEGIN
    CREATE TABLE DiscountConfigs (
        ConfigID INT PRIMARY KEY IDENTITY(1,1),
        ProductID INT NULL,
        CustomerGroupID INT NULL,
        MaxDiscountPercent DECIMAL(5, 2) NOT NULL DEFAULT 0,
        RequireApproval BIT DEFAULT 0,
        ApproverRoleID INT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_DiscountConfigs_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE,
        CONSTRAINT FK_DiscountConfigs_CustomerGroups FOREIGN KEY (CustomerGroupID) REFERENCES CustomerGroups(CustomerGroupID) ON DELETE CASCADE,
        CONSTRAINT FK_DiscountConfigs_Roles FOREIGN KEY (ApproverRoleID) REFERENCES Roles(RoleID)
    );
END
GO

-- Trigger for DiscountConfigs
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_DiscountConfigs_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_DiscountConfigs_UpdateModifiedDate
    ON DiscountConfigs
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE DiscountConfigs
        SET ModifiedDate = GETDATE()
        FROM DiscountConfigs d
        INNER JOIN inserted i ON d.ConfigID = i.ConfigID;
    END
    ');
END
GO

-- ============================================
-- 3. WAREHOUSE MANAGEMENT
-- ============================================

-- Warehouses table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Warehouses')
BEGIN
    CREATE TABLE Warehouses (
        WarehouseID INT PRIMARY KEY IDENTITY(1,1),
        WarehouseCode NVARCHAR(20) UNIQUE NOT NULL,
        WarehouseName NVARCHAR(100) NOT NULL,
        StoreID INT NOT NULL,
        Address NVARCHAR(255),
        ManagerID INT NULL,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Warehouses_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_Warehouses_Manager FOREIGN KEY (ManagerID) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_store ON Warehouses(StoreID);
END
GO
-- Inventories table (Stock inventory)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Inventories')
BEGIN
    CREATE TABLE Inventories (
        InventoryID INT PRIMARY KEY IDENTITY(1,1),
        ProductID INT NOT NULL,
        StoreID INT NOT NULL,
        WarehouseID INT NOT NULL,
        OnHand INT NOT NULL DEFAULT 0,
        Reserved INT NOT NULL DEFAULT 0,
        Available AS (OnHand - Reserved) PERSISTED,
        OnOrder INT NOT NULL DEFAULT 0,
        MinQuantity INT DEFAULT 0,
        MaxQuantity INT DEFAULT 0,
        SerialNumbers NVARCHAR(MAX), -- JSON
        LastUpdated DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Inventories_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT FK_Inventories_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_Inventories_Warehouses FOREIGN KEY (WarehouseID) REFERENCES Warehouses(WarehouseID),
        CONSTRAINT UK_Product_Store_Warehouse UNIQUE (ProductID, StoreID, WarehouseID),
        CONSTRAINT CHK_OnHand CHECK (OnHand >= 0),
        CONSTRAINT CHK_Available CHECK (OnHand - Reserved >= 0)
    );
    CREATE INDEX idx_product_store ON Inventories(ProductID, StoreID);
END
GO

-- Trigger to auto-update LastUpdated for Inventories
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Inventories_UpdateLastUpdated')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Inventories_UpdateLastUpdated
    ON Inventories
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Inventories
        SET LastUpdated = GETDATE()
        FROM Inventories inv
        INNER JOIN inserted i ON inv.InventoryID = i.InventoryID;
    END
    ');
END
GO

-- StockTransfers table (Stock transfers)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'StockTransfers')
BEGIN
    CREATE TABLE StockTransfers (
        TransferID INT PRIMARY KEY IDENTITY(1,1),
        TransferCode NVARCHAR(20) UNIQUE NOT NULL,
        FromWarehouseID INT NOT NULL,
        ToWarehouseID INT NOT NULL,
        TransferDate DATE NOT NULL,
        Status NVARCHAR(20) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Completed', 'Cancelled')),
        CreatedBy INT NOT NULL,
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_StockTransfers_FromWarehouse FOREIGN KEY (FromWarehouseID) REFERENCES Warehouses(WarehouseID),
        CONSTRAINT FK_StockTransfers_ToWarehouse FOREIGN KEY (ToWarehouseID) REFERENCES Warehouses(WarehouseID),
        CONSTRAINT FK_StockTransfers_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_from_warehouse ON StockTransfers(FromWarehouseID);
    CREATE INDEX idx_to_warehouse ON StockTransfers(ToWarehouseID);
    CREATE INDEX idx_transfer_date ON StockTransfers(TransferDate);
END
GO
-- StockTransferDetails table (Stock transfer details)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'StockTransferDetails')
BEGIN
    CREATE TABLE StockTransferDetails (
        TransferDetailID INT PRIMARY KEY IDENTITY(1,1),
        TransferID INT NOT NULL,
        ProductID INT NOT NULL,
        Quantity INT NOT NULL,
        SerialNumbers NVARCHAR(MAX), -- JSON
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_StockTransferDetails_Transfers FOREIGN KEY (TransferID) REFERENCES StockTransfers(TransferID) ON DELETE CASCADE,
        CONSTRAINT FK_StockTransferDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT CHK_Transfer_Quantity CHECK (Quantity > 0)
    );
END
GO

-- StockCheckSessions table (Stock check sessions)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'StockCheckSessions')
BEGIN
    CREATE TABLE StockCheckSessions (
        SessionID INT PRIMARY KEY IDENTITY(1,1),
        SessionCode NVARCHAR(20) UNIQUE NOT NULL,
        WarehouseID INT NOT NULL,
        Status NVARCHAR(20) DEFAULT 'Draft' CHECK (Status IN ('Draft', 'InProgress', 'PendingApproval', 'Approved', 'Rejected')),
        CreatedBy INT NOT NULL,
        ApprovedBy INT NULL,
        ApprovedDate DATETIME2 NULL,
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_StockCheckSessions_Warehouses FOREIGN KEY (WarehouseID) REFERENCES Warehouses(WarehouseID),
        CONSTRAINT FK_StockCheckSessions_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
        CONSTRAINT FK_StockCheckSessions_ApprovedBy FOREIGN KEY (ApprovedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_warehouse ON StockCheckSessions(WarehouseID);
    CREATE INDEX idx_status ON StockCheckSessions(Status);
END
GO

-- Trigger for StockCheckSessions
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_StockCheckSessions_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_StockCheckSessions_UpdateModifiedDate
    ON StockCheckSessions
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE StockCheckSessions
        SET ModifiedDate = GETDATE()
        FROM StockCheckSessions s
        INNER JOIN inserted i ON s.SessionID = i.SessionID;
    END
    ');
END
GO

-- StockCheckDetails table (Stock check details)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'StockCheckDetails')
BEGIN
    CREATE TABLE StockCheckDetails (
        CheckDetailID INT PRIMARY KEY IDENTITY(1,1),
        SessionID INT NOT NULL,
        ProductID INT NOT NULL,
        SystemQuantity INT NOT NULL,
        ActualQuantity INT NOT NULL,
        Difference AS (ActualQuantity - SystemQuantity) PERSISTED,
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_StockCheckDetails_Sessions FOREIGN KEY (SessionID) REFERENCES StockCheckSessions(SessionID) ON DELETE CASCADE,
        CONSTRAINT FK_StockCheckDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT UK_Session_Product UNIQUE (SessionID, ProductID)
    );
END
GO
-- StockCheckParticipants table (Stock check participants)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'StockCheckParticipants')
BEGIN
    CREATE TABLE StockCheckParticipants (
        ParticipantID INT PRIMARY KEY IDENTITY(1,1),
        SessionID INT NOT NULL,
        UserID INT NOT NULL,
        Role NVARCHAR(20) DEFAULT 'Checker' CHECK (Role IN ('Checker', 'Supervisor')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_StockCheckParticipants_Sessions FOREIGN KEY (SessionID) REFERENCES StockCheckSessions(SessionID) ON DELETE CASCADE,
        CONSTRAINT FK_StockCheckParticipants_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
        CONSTRAINT UK_Session_User UNIQUE (SessionID, UserID)
    );
END
GO

-- StockAdjustments table (Stock adjustments)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'StockAdjustments')
BEGIN
    CREATE TABLE StockAdjustments (
        AdjustmentID INT PRIMARY KEY IDENTITY(1,1),
        AdjustmentCode NVARCHAR(20) UNIQUE NOT NULL,
        WarehouseID INT NOT NULL,
        ProductID INT NOT NULL,
        OldQuantity INT NOT NULL,
        NewQuantity INT NOT NULL,
        Difference AS (NewQuantity - OldQuantity) PERSISTED,
        Reason NVARCHAR(500) NOT NULL,
        SessionID INT NULL,
        ApprovedBy INT NULL,
        AdjustmentDate DATE NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_StockAdjustments_Warehouses FOREIGN KEY (WarehouseID) REFERENCES Warehouses(WarehouseID),
        CONSTRAINT FK_StockAdjustments_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT FK_StockAdjustments_Sessions FOREIGN KEY (SessionID) REFERENCES StockCheckSessions(SessionID),
        CONSTRAINT FK_StockAdjustments_ApprovedBy FOREIGN KEY (ApprovedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_warehouse_product ON StockAdjustments(WarehouseID, ProductID);
    CREATE INDEX idx_adjustment_date ON StockAdjustments(AdjustmentDate);
END
GO

-- ============================================
-- 4. CUSTOMER MANAGEMENT
-- ============================================

-- CustomerTiers table (Membership tiers)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'CustomerTiers')
BEGIN
    CREATE TABLE CustomerTiers (
        TierID INT PRIMARY KEY IDENTITY(1,1),
        TierName NVARCHAR(20) UNIQUE NOT NULL CHECK (TierName IN ('Normal', 'Silver', 'Gold', 'VIP')),
        MinSpending DECIMAL(18, 2) NOT NULL,
        MaxSpending DECIMAL(18, 2) NULL,
        DiscountPercent DECIMAL(5, 2) DEFAULT 0,
        Benefits NVARCHAR(MAX),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Trigger for CustomerTiers
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_CustomerTiers_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_CustomerTiers_UpdateModifiedDate
    ON CustomerTiers
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE CustomerTiers
        SET ModifiedDate = GETDATE()
        FROM CustomerTiers t
        INNER JOIN inserted i ON t.TierID = i.TierID;
    END
    ');
END
GO
-- Customers table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Customers')
BEGIN
    CREATE TABLE Customers (
        CustomerID INT PRIMARY KEY IDENTITY(1,1),
        CustomerCode NVARCHAR(20) UNIQUE NOT NULL,
        FullName NVARCHAR(100) NOT NULL,
        Phone NVARCHAR(20) UNIQUE NOT NULL,
        Email NVARCHAR(100) UNIQUE NULL,
        Address NVARCHAR(255),
        DateOfBirth DATE NULL,
        Gender NVARCHAR(10) NULL CHECK (Gender IN ('Male', 'Female', 'Other')),
        CustomerGroupID INT NULL,
        TotalSpent DECIMAL(18, 2) DEFAULT 0,
        LoyaltyPoints INT DEFAULT 0,
        MembershipTier NVARCHAR(20) DEFAULT 'Normal' CHECK (MembershipTier IN ('Normal', 'Silver', 'Gold', 'VIP')),
        FirstPurchaseStoreID INT NULL,
        LastPurchaseDate DATE NULL,
        TotalOrders INT DEFAULT 0,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Customers_CustomerGroups FOREIGN KEY (CustomerGroupID) REFERENCES CustomerGroups(CustomerGroupID),
        CONSTRAINT FK_Customers_FirstPurchaseStore FOREIGN KEY (FirstPurchaseStoreID) REFERENCES Stores(StoreID),
        CONSTRAINT CHK_LoyaltyPoints CHECK (LoyaltyPoints >= 0),
        CONSTRAINT CHK_TotalSpent CHECK (TotalSpent >= 0)
    );
    CREATE INDEX idx_phone ON Customers(Phone);
    CREATE INDEX idx_email ON Customers(Email);
    CREATE INDEX idx_tier ON Customers(MembershipTier);
END
GO

-- Trigger for Customers
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Customers_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Customers_UpdateModifiedDate
    ON Customers
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Customers
        SET ModifiedDate = GETDATE()
        FROM Customers c
        INNER JOIN inserted i ON c.CustomerID = i.CustomerID;
    END
    ');
END
GO

-- LoyaltyTransactions table (Loyalty points history)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'LoyaltyTransactions')
BEGIN
    CREATE TABLE LoyaltyTransactions (
        TransactionID INT PRIMARY KEY IDENTITY(1,1),
        CustomerID INT NOT NULL,
        InvoiceID INT NULL,
        PointsEarned INT DEFAULT 0,
        PointsUsed INT DEFAULT 0,
        Balance INT NOT NULL,
        Description NVARCHAR(255),
        TransactionDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_LoyaltyTransactions_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
    );
    CREATE INDEX idx_customer_date ON LoyaltyTransactions(CustomerID, TransactionDate);
END
GO

-- ============================================
-- 5. SALES MANAGEMENT
-- ============================================

-- PaymentMethods table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PaymentMethods')
BEGIN
    CREATE TABLE PaymentMethods (
        PaymentMethodID INT PRIMARY KEY IDENTITY(1,1),
        MethodName NVARCHAR(50) UNIQUE NOT NULL,
        Description NVARCHAR(255),
        IsActive BIT DEFAULT 1,
        CreatedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO
-- Vouchers table (Discount vouchers)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Vouchers')
BEGIN
    CREATE TABLE Vouchers (
        VoucherID INT PRIMARY KEY IDENTITY(1,1),
        VoucherCode NVARCHAR(20) UNIQUE NOT NULL,
        VoucherName NVARCHAR(100) NOT NULL,
        DiscountType NVARCHAR(20) NOT NULL CHECK (DiscountType IN ('Percent', 'FixedAmount')),
        DiscountValue DECIMAL(18, 2) NOT NULL,
        MinOrderValue DECIMAL(18, 2) DEFAULT 0,
        MaxDiscountAmount DECIMAL(18, 2) NULL,
        StartDate DATE NOT NULL,
        EndDate DATE NOT NULL,
        UsageLimit INT DEFAULT 1,
        UsedCount INT DEFAULT 0,
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive', 'Expired')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT CHK_Voucher_Dates CHECK (EndDate >= StartDate)
    );
    CREATE INDEX idx_code ON Vouchers(VoucherCode);
    CREATE INDEX idx_dates ON Vouchers(StartDate, EndDate);
END
GO

-- Invoices table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Invoices')
BEGIN
    CREATE TABLE Invoices (
        InvoiceID INT PRIMARY KEY IDENTITY(1,1),
        InvoiceCode NVARCHAR(20) UNIQUE NOT NULL,
        StoreID INT NOT NULL,
        CustomerID INT NULL,
        SoldByID INT NOT NULL,
        InvoiceDate DATE NOT NULL,
        PriceBookID INT NULL,
        
        -- Price calculation
        SubTotal DECIMAL(18, 2) NOT NULL DEFAULT 0,
        DiscountAmount DECIMAL(18, 2) DEFAULT 0,
        VATPercent DECIMAL(5, 2) DEFAULT 0,
        VATAmount DECIMAL(18, 2) DEFAULT 0,
        TotalAmount DECIMAL(18, 2) NOT NULL DEFAULT 0,
        
        -- Payment
        PaidAmount DECIMAL(18, 2) DEFAULT 0,
        CustomerPayment DECIMAL(18, 2) DEFAULT 0,
        ChangeAmount DECIMAL(18, 2) DEFAULT 0,
        PaymentMethodID INT NOT NULL,
        
        -- Voucher
        VoucherID INT NULL,
        IsApplyVoucher BIT DEFAULT 0,
        
        -- Status
        PaymentStatus NVARCHAR(20) DEFAULT 'Paid' CHECK (PaymentStatus IN ('Unpaid', 'Partial', 'Paid')),
        Status NVARCHAR(20) DEFAULT 'Completed' CHECK (Status IN ('Draft', 'Processing', 'Completed', 'Cancelled')),
        
        -- Exchange
        ExchangeFromInvoiceID INT NULL,
        
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        
        CONSTRAINT FK_Invoices_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_Invoices_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
        CONSTRAINT FK_Invoices_SoldBy FOREIGN KEY (SoldByID) REFERENCES Users(UserID),
        CONSTRAINT FK_Invoices_PaymentMethods FOREIGN KEY (PaymentMethodID) REFERENCES PaymentMethods(PaymentMethodID),
        CONSTRAINT FK_Invoices_Vouchers FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID),
        CONSTRAINT FK_Invoices_PriceBooks FOREIGN KEY (PriceBookID) REFERENCES PriceBooks(PriceBookID),
        CONSTRAINT FK_Invoices_ExchangeFrom FOREIGN KEY (ExchangeFromInvoiceID) REFERENCES Invoices(InvoiceID),
        CONSTRAINT CHK_TotalAmount CHECK (TotalAmount >= 0)
    );
    CREATE INDEX idx_invoice_code ON Invoices(InvoiceCode);
    CREATE INDEX idx_store_date ON Invoices(StoreID, InvoiceDate);
    CREATE INDEX idx_customer ON Invoices(CustomerID);
    CREATE INDEX idx_soldby ON Invoices(SoldByID);
    CREATE INDEX idx_date ON Invoices(InvoiceDate);
END
GO
-- Trigger for Invoices
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Invoices_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Invoices_UpdateModifiedDate
    ON Invoices
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Invoices
        SET ModifiedDate = GETDATE()
        FROM Invoices inv
        INNER JOIN inserted i ON inv.InvoiceID = i.InvoiceID;
    END
    ');
END
GO

-- Add foreign key for LoyaltyTransactions.InvoiceID
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_LoyaltyTransactions_Invoices')
BEGIN
    ALTER TABLE LoyaltyTransactions 
    ADD CONSTRAINT FK_LoyaltyTransactions_Invoices 
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID);
END
GO

-- InvoiceDetails table (Invoice details)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'InvoiceDetails')
BEGIN
    CREATE TABLE InvoiceDetails (
        InvoiceDetailID INT PRIMARY KEY IDENTITY(1,1),
        InvoiceID INT NOT NULL,
        ProductID INT NOT NULL,
        ProductName NVARCHAR(200) NOT NULL,
        Quantity INT NOT NULL,
        UnitPrice DECIMAL(18, 2) NOT NULL,
        DiscountPercent DECIMAL(5, 2) DEFAULT 0,
        DiscountAmount DECIMAL(18, 2) DEFAULT 0,
        TotalPrice AS (Quantity * UnitPrice - DiscountAmount) PERSISTED,
        SerialNumbers NVARCHAR(MAX), -- JSON
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_InvoiceDetails_Invoices FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID) ON DELETE CASCADE,
        CONSTRAINT FK_InvoiceDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT CHK_Quantity CHECK (Quantity > 0),
        CONSTRAINT CHK_UnitPrice CHECK (UnitPrice >= 0)
    );
END
GO

-- OrderReturns table (Return orders)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'OrderReturns')
BEGIN
    CREATE TABLE OrderReturns (
        ReturnID INT PRIMARY KEY IDENTITY(1,1),
        ReturnCode NVARCHAR(20) UNIQUE NOT NULL,
        InvoiceID INT NOT NULL,
        NewInvoiceID INT NULL,
        CustomerID INT NULL,
        StoreID INT NOT NULL,
        ProcessedBy INT NOT NULL,
        ReturnDate DATE NOT NULL,
        TotalAmount DECIMAL(18, 2) NOT NULL,
        RefundStatus NVARCHAR(20) DEFAULT 'Pending' CHECK (RefundStatus IN ('Pending', 'Partial', 'Completed')),
        Status NVARCHAR(20) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Approved', 'Rejected')),
        Reason NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_OrderReturns_Invoices FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID),
        CONSTRAINT FK_OrderReturns_NewInvoices FOREIGN KEY (NewInvoiceID) REFERENCES Invoices(InvoiceID),
        CONSTRAINT FK_OrderReturns_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
        CONSTRAINT FK_OrderReturns_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_OrderReturns_ProcessedBy FOREIGN KEY (ProcessedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_invoice ON OrderReturns(InvoiceID);
    CREATE INDEX idx_return_date ON OrderReturns(ReturnDate);
END
GO
-- Trigger for OrderReturns
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_OrderReturns_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_OrderReturns_UpdateModifiedDate
    ON OrderReturns
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE OrderReturns
        SET ModifiedDate = GETDATE()
        FROM OrderReturns o
        INNER JOIN inserted i ON o.ReturnID = i.ReturnID;
    END
    ');
END
GO

-- OrderReturnDetails table (Return order details)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'OrderReturnDetails')
BEGIN
    CREATE TABLE OrderReturnDetails (
        ReturnDetailID INT PRIMARY KEY IDENTITY(1,1),
        ReturnID INT NOT NULL,
        ProductID INT NOT NULL,
        Quantity INT NOT NULL,
        UnitPrice DECIMAL(18, 2) NOT NULL,
        TotalPrice AS (Quantity * UnitPrice) PERSISTED,
        Reason NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_OrderReturnDetails_Returns FOREIGN KEY (ReturnID) REFERENCES OrderReturns(ReturnID) ON DELETE CASCADE,
        CONSTRAINT FK_OrderReturnDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT CHK_Return_Quantity CHECK (Quantity > 0)
    );
END
GO

-- Refunds table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Refunds')
BEGIN
    CREATE TABLE Refunds (
        RefundID INT PRIMARY KEY IDENTITY(1,1),
        RefundCode NVARCHAR(20) UNIQUE NOT NULL,
        ReturnID INT NOT NULL,
        RefundAmount DECIMAL(18, 2) NOT NULL,
        PaymentMethodID INT NOT NULL,
        RefundDate DATE NOT NULL,
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Refunds_Returns FOREIGN KEY (ReturnID) REFERENCES OrderReturns(ReturnID),
        CONSTRAINT FK_Refunds_PaymentMethods FOREIGN KEY (PaymentMethodID) REFERENCES PaymentMethods(PaymentMethodID),
        CONSTRAINT CHK_RefundAmount CHECK (RefundAmount > 0)
    );
    CREATE INDEX idx_return ON Refunds(ReturnID);
END
GO

-- ============================================
-- 6. SUPPLIER & PURCHASE MANAGEMENT
-- ============================================

-- Suppliers table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Suppliers')
BEGIN
    CREATE TABLE Suppliers (
        SupplierID INT PRIMARY KEY IDENTITY(1,1),
        SupplierCode NVARCHAR(20) UNIQUE NOT NULL,
        SupplierName NVARCHAR(100) NOT NULL,
        Phone NVARCHAR(20),
        Email NVARCHAR(100),
        Address NVARCHAR(255),
        TaxCode NVARCHAR(20) UNIQUE,
        ContactPerson NVARCHAR(100),
        PaymentTerm NVARCHAR(255),
        Status NVARCHAR(20) DEFAULT 'Active' CHECK (Status IN ('Active', 'Inactive')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE()
    );
    CREATE INDEX idx_supplier_code ON Suppliers(SupplierCode);
END
GO
-- Trigger for Suppliers
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_Suppliers_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_Suppliers_UpdateModifiedDate
    ON Suppliers
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE Suppliers
        SET ModifiedDate = GETDATE()
        FROM Suppliers s
        INNER JOIN inserted i ON s.SupplierID = i.SupplierID;
    END
    ');
END
GO

-- PurchaseOrders table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PurchaseOrders')
BEGIN
    CREATE TABLE PurchaseOrders (
        PurchaseOrderID INT PRIMARY KEY IDENTITY(1,1),
        OrderCode NVARCHAR(20) UNIQUE NOT NULL,
        SupplierID INT NOT NULL,
        StoreID INT NOT NULL,
        OrderDate DATE NOT NULL,
        ExpectedDate DATE NULL,
        TotalAmount DECIMAL(18, 2) NOT NULL,
        Status NVARCHAR(20) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Approved', 'Shipping', 'Received', 'Cancelled')),
        CreatedBy INT NOT NULL,
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_PurchaseOrders_Suppliers FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
        CONSTRAINT FK_PurchaseOrders_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_PurchaseOrders_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_supplier ON PurchaseOrders(SupplierID);
    CREATE INDEX idx_store ON PurchaseOrders(StoreID);
    CREATE INDEX idx_order_date ON PurchaseOrders(OrderDate);
END
GO

-- Trigger for PurchaseOrders
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_PurchaseOrders_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_PurchaseOrders_UpdateModifiedDate
    ON PurchaseOrders
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE PurchaseOrders
        SET ModifiedDate = GETDATE()
        FROM PurchaseOrders p
        INNER JOIN inserted i ON p.PurchaseOrderID = i.PurchaseOrderID;
    END
    ');
END
GO

-- PurchaseOrderDetails table (Purchase order details)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PurchaseOrderDetails')
BEGIN
    CREATE TABLE PurchaseOrderDetails (
        OrderDetailID INT PRIMARY KEY IDENTITY(1,1),
        PurchaseOrderID INT NOT NULL,
        ProductID INT NOT NULL,
        ProductName NVARCHAR(200) NOT NULL,
        Quantity INT NOT NULL,
        UnitPrice DECIMAL(18, 2) NOT NULL,
        TotalPrice AS (Quantity * UnitPrice) PERSISTED,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_PurchaseOrderDetails_Orders FOREIGN KEY (PurchaseOrderID) REFERENCES PurchaseOrders(PurchaseOrderID) ON DELETE CASCADE,
        CONSTRAINT FK_PurchaseOrderDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT CHK_PO_Quantity CHECK (Quantity > 0),
        CONSTRAINT CHK_PO_UnitPrice CHECK (UnitPrice >= 0)
    );
END
GO
-- Imports table (Import receipts)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Imports')
BEGIN
    CREATE TABLE Imports (
        ImportID INT PRIMARY KEY IDENTITY(1,1),
        ImportCode NVARCHAR(20) UNIQUE NOT NULL,
        PurchaseOrderID INT NULL,
        SupplierID INT NOT NULL,
        StoreID INT NOT NULL,
        WarehouseID INT NOT NULL,
        ImportDate DATE NOT NULL,
        TotalAmount DECIMAL(18, 2) NOT NULL,
        Status NVARCHAR(20) DEFAULT 'Success' CHECK (Status IN ('Success', 'Failed')),
        CreatedBy INT NOT NULL,
        Note NVARCHAR(500),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Imports_PurchaseOrders FOREIGN KEY (PurchaseOrderID) REFERENCES PurchaseOrders(PurchaseOrderID),
        CONSTRAINT FK_Imports_Suppliers FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
        CONSTRAINT FK_Imports_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_Imports_Warehouses FOREIGN KEY (WarehouseID) REFERENCES Warehouses(WarehouseID),
        CONSTRAINT FK_Imports_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_supplier ON Imports(SupplierID);
    CREATE INDEX idx_store ON Imports(StoreID);
    CREATE INDEX idx_warehouse ON Imports(WarehouseID);
    CREATE INDEX idx_import_date ON Imports(ImportDate);
END
GO

-- ImportDetails table (Import receipt details)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ImportDetails')
BEGIN
    CREATE TABLE ImportDetails (
        ImportDetailID INT PRIMARY KEY IDENTITY(1,1),
        ImportID INT NOT NULL,
        ProductID INT NOT NULL,
        Quantity INT NOT NULL,
        UnitPrice DECIMAL(18, 2) NOT NULL,
        TotalPrice AS (Quantity * UnitPrice) PERSISTED,
        ExpiryDate DATE NULL,
        SerialNumbers NVARCHAR(MAX), -- JSON
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_ImportDetails_Imports FOREIGN KEY (ImportID) REFERENCES Imports(ImportID) ON DELETE CASCADE,
        CONSTRAINT FK_ImportDetails_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
        CONSTRAINT CHK_Import_Quantity CHECK (Quantity > 0),
        CONSTRAINT CHK_Import_UnitPrice CHECK (UnitPrice >= 0)
    );
END
GO

-- SupplierPayments table (Supplier payments)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'SupplierPayments')
BEGIN
    CREATE TABLE SupplierPayments (
        PaymentID INT PRIMARY KEY IDENTITY(1,1),
        PaymentCode NVARCHAR(20) UNIQUE NOT NULL,
        SupplierID INT NOT NULL,
        ImportID INT NULL,
        PaymentDate DATE NOT NULL,
        Amount DECIMAL(18, 2) NOT NULL,
        PaymentMethod NVARCHAR(20) NOT NULL CHECK (PaymentMethod IN ('Cash', 'BankTransfer')),
        Note NVARCHAR(500),
        CreatedBy INT NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_SupplierPayments_Suppliers FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
        CONSTRAINT FK_SupplierPayments_Imports FOREIGN KEY (ImportID) REFERENCES Imports(ImportID),
        CONSTRAINT FK_SupplierPayments_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
        CONSTRAINT CHK_Payment_Amount CHECK (Amount > 0)
    );
    CREATE INDEX idx_supplier ON SupplierPayments(SupplierID);
    CREATE INDEX idx_payment_date ON SupplierPayments(PaymentDate);
END
GO
-- SupplierDebts table (Supplier debts)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'SupplierDebts')
BEGIN
    CREATE TABLE SupplierDebts (
        DebtID INT PRIMARY KEY IDENTITY(1,1),
        SupplierID INT NOT NULL,
        ImportID INT NULL,
        DebtAmount DECIMAL(18, 2) NOT NULL,
        PaidAmount DECIMAL(18, 2) DEFAULT 0,
        RemainingAmount AS (DebtAmount - PaidAmount) PERSISTED,
        DueDate DATE NULL,
        Status NVARCHAR(20) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Partial', 'Paid')),
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_SupplierDebts_Suppliers FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
        CONSTRAINT FK_SupplierDebts_Imports FOREIGN KEY (ImportID) REFERENCES Imports(ImportID),
        CONSTRAINT CHK_DebtAmount CHECK (DebtAmount >= 0),
        CONSTRAINT CHK_RemainingAmount CHECK (DebtAmount - PaidAmount >= 0)
    );
    CREATE INDEX idx_supplier ON SupplierDebts(SupplierID);
    CREATE INDEX idx_status ON SupplierDebts(Status);
END
GO

-- Trigger for SupplierDebts
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_SupplierDebts_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_SupplierDebts_UpdateModifiedDate
    ON SupplierDebts
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE SupplierDebts
        SET ModifiedDate = GETDATE()
        FROM SupplierDebts d
        INNER JOIN inserted i ON d.DebtID = i.DebtID;
    END
    ');
END
GO

-- ============================================
-- 7. FINANCE MANAGEMENT
-- ============================================

-- TransactionTypes table (Transaction types)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'TransactionTypes')
BEGIN
    CREATE TABLE TransactionTypes (
        TypeID INT PRIMARY KEY IDENTITY(1,1),
        TypeName NVARCHAR(50) UNIQUE NOT NULL,
        Category NVARCHAR(20) NOT NULL CHECK (Category IN ('Income', 'Expense')),
        Description NVARCHAR(255),
        CreatedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Transactions table (Income/Expense transactions)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Transactions')
BEGIN
    CREATE TABLE Transactions (
        TransactionID INT PRIMARY KEY IDENTITY(1,1),
        TransactionCode NVARCHAR(20) UNIQUE NOT NULL,
        StoreID INT NOT NULL,
        TypeID INT NOT NULL,
        Category NVARCHAR(20) NOT NULL CHECK (Category IN ('Income', 'Expense')),
        Amount DECIMAL(18, 2) NOT NULL,
        PaymentMethod NVARCHAR(20) NOT NULL CHECK (PaymentMethod IN ('Cash', 'BankTransfer')),
        TransactionDate DATE NOT NULL,
        CreatedBy INT NOT NULL,
        Note NVARCHAR(500),
        RelatedID INT NULL,
        RelatedType NVARCHAR(50) NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_Transactions_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
        CONSTRAINT FK_Transactions_Types FOREIGN KEY (TypeID) REFERENCES TransactionTypes(TypeID),
        CONSTRAINT FK_Transactions_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID),
        CONSTRAINT CHK_Transaction_Amount CHECK (Amount > 0)
    );
    CREATE INDEX idx_store_date ON Transactions(StoreID, TransactionDate);
    CREATE INDEX idx_category ON Transactions(Category);
END
GO
-- ============================================
-- 8. REPORTS
-- ============================================

-- SalesReports table (Sales reports)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'SalesReports')
BEGIN
    CREATE TABLE SalesReports (
        ReportID INT PRIMARY KEY IDENTITY(1,1),
        StoreID INT NULL,
        UserID INT NULL,
        ReportDate DATE NOT NULL,
        Revenue DECIMAL(18, 2) NOT NULL,
        Profit DECIMAL(18, 2) DEFAULT 0,
        TotalInvoices INT DEFAULT 0,
        TotalProducts INT DEFAULT 0,
        AverageOrderValue DECIMAL(18, 2) DEFAULT 0,
        GeneratedAt DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_SalesReports_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID) ON DELETE CASCADE,
        CONSTRAINT FK_SalesReports_Users FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
    );
    CREATE INDEX idx_store_date ON SalesReports(StoreID, ReportDate);
    CREATE INDEX idx_user_date ON SalesReports(UserID, ReportDate);
END
GO

-- LoyaltyReports table (Loyalty customer reports)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'LoyaltyReports')
BEGIN
    CREATE TABLE LoyaltyReports (
        ReportID INT PRIMARY KEY IDENTITY(1,1),
        CustomerID INT NOT NULL,
        MembershipTier NVARCHAR(20) NOT NULL CHECK (MembershipTier IN ('Normal', 'Silver', 'Gold', 'VIP')),
        TotalSpent DECIMAL(18, 2) NOT NULL,
        TotalOrders INT NOT NULL,
        LoyaltyPoints INT NOT NULL,
        ReportDate DATE NOT NULL,
        GeneratedAt DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_LoyaltyReports_Customers FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE
    );
    CREATE INDEX idx_customer_date ON LoyaltyReports(CustomerID, ReportDate);
    CREATE INDEX idx_tier ON LoyaltyReports(MembershipTier);
END
GO

-- InventoryReports table (Inventory reports)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'InventoryReports')
BEGIN
    CREATE TABLE InventoryReports (
        ReportID INT PRIMARY KEY IDENTITY(1,1),
        ProductID INT NOT NULL,
        StoreID INT NULL,
        WarehouseID INT NULL,
        OnHand INT NOT NULL,
        Reserved INT NOT NULL,
        Available INT NOT NULL,
        InventoryValue DECIMAL(18, 2),
        ReportDate DATE NOT NULL,
        GeneratedAt DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_InventoryReports_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE,
        CONSTRAINT FK_InventoryReports_Stores FOREIGN KEY (StoreID) REFERENCES Stores(StoreID) ON DELETE CASCADE,
        CONSTRAINT FK_InventoryReports_Warehouses FOREIGN KEY (WarehouseID) REFERENCES Warehouses(WarehouseID) ON DELETE CASCADE
    );
    CREATE INDEX idx_product_date ON InventoryReports(ProductID, ReportDate);
    CREATE INDEX idx_store_date ON InventoryReports(StoreID, ReportDate);
END
GO

-- ExportLogs table (Report export history)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ExportLogs')
BEGIN
    CREATE TABLE ExportLogs (
        ExportID INT PRIMARY KEY IDENTITY(1,1),
        UserID INT NOT NULL,
        ReportType NVARCHAR(50) NOT NULL,
        ExportFormat NVARCHAR(20) NOT NULL CHECK (ExportFormat IN ('Excel', 'PDF', 'CSV')),
        FilterCriteria NVARCHAR(MAX), -- JSON
        FilePath NVARCHAR(500),
        Status NVARCHAR(20) DEFAULT 'Success' CHECK (Status IN ('Success', 'Failed')),
        ExportDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_ExportLogs_Users FOREIGN KEY (UserID) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_user_date ON ExportLogs(UserID, ExportDate);
    CREATE INDEX idx_report_type ON ExportLogs(ReportType);
END
GO
-- ============================================
-- 9. WEBSITE
-- ============================================

-- WebsiteContents table (Website content)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'WebsiteContents')
BEGIN
    CREATE TABLE WebsiteContents (
        ContentID INT PRIMARY KEY IDENTITY(1,1),
        ContentType NVARCHAR(20) NOT NULL CHECK (ContentType IN ('Banner', 'Article', 'About', 'Product')),
        Title NVARCHAR(255) NOT NULL,
        Content NVARCHAR(MAX),
        ImageURL NVARCHAR(500),
        DisplayOrder INT DEFAULT 0,
        IsActive BIT DEFAULT 1,
        CreatedBy INT NOT NULL,
        CreatedDate DATETIME2 DEFAULT GETDATE(),
        ModifiedDate DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_WebsiteContents_CreatedBy FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_type_active ON WebsiteContents(ContentType, IsActive);
END
GO

-- Trigger for WebsiteContents
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_WebsiteContents_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_WebsiteContents_UpdateModifiedDate
    ON WebsiteContents
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE WebsiteContents
        SET ModifiedDate = GETDATE()
        FROM WebsiteContents w
        INNER JOIN inserted i ON w.ContentID = i.ContentID;
    END
    ');
END
GO

-- ContactForms table (Contact forms)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ContactForms')
BEGIN
    CREATE TABLE ContactForms (
        ContactID INT PRIMARY KEY IDENTITY(1,1),
        FullName NVARCHAR(100) NOT NULL,
        Email NVARCHAR(100) NOT NULL,
        Phone NVARCHAR(20),
        Message NVARCHAR(MAX) NOT NULL,
        Status NVARCHAR(20) DEFAULT 'New' CHECK (Status IN ('New', 'Processing', 'Resolved')),
        SubmittedDate DATETIME2 DEFAULT GETDATE(),
        ProcessedBy INT NULL,
        ProcessedDate DATETIME2 NULL,
        CONSTRAINT FK_ContactForms_ProcessedBy FOREIGN KEY (ProcessedBy) REFERENCES Users(UserID)
    );
    CREATE INDEX idx_status ON ContactForms(Status);
    CREATE INDEX idx_submitted_date ON ContactForms(SubmittedDate);
END
GO

-- ============================================
-- 10. SYSTEM SETTINGS
-- ============================================

-- SystemSettings table (System configuration)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'SystemSettings')
BEGIN
    CREATE TABLE SystemSettings (
        SettingID INT PRIMARY KEY IDENTITY(1,1),
        SettingKey NVARCHAR(100) UNIQUE NOT NULL,
        SettingValue NVARCHAR(MAX) NOT NULL,
        DataType NVARCHAR(20) DEFAULT 'String' CHECK (DataType IN ('String', 'Number', 'Boolean', 'JSON')),
        Category NVARCHAR(50),
        Description NVARCHAR(255),
        ModifiedDate DATETIME2 DEFAULT GETDATE()
    );
END
GO

-- Trigger for SystemSettings
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_SystemSettings_UpdateModifiedDate')
BEGIN
    EXEC('
    CREATE TRIGGER trg_SystemSettings_UpdateModifiedDate
    ON SystemSettings
    AFTER UPDATE
    AS
    BEGIN
        SET NOCOUNT ON;
        UPDATE SystemSettings
        SET ModifiedDate = GETDATE()
        FROM SystemSettings s
        INNER JOIN inserted i ON s.SettingID = i.SettingID;
    END
    ');
END
GO
-- ============================================
-- SAMPLE DATA
-- ============================================

-- 1. Roles
IF NOT EXISTS (SELECT * FROM Roles WHERE RoleName = 'Admin')
BEGIN
    INSERT INTO Roles (RoleName, Description) VALUES
    ('Admin', N'System administrator'),
    ('Owner', N'Retail chain owner'),
    ('StoreManager', N'Store manager'),
    ('SalesStaff', N'Sales staff'),
    ('WarehouseStaff', N'Warehouse staff');
    PRINT 'Inserted Roles';
END
GO

-- 2. Modules
IF NOT EXISTS (SELECT * FROM Modules WHERE ModuleName = 'Product')
BEGIN
    INSERT INTO Modules (ModuleName, Description, DisplayOrder) VALUES
    ('Product', N'Product management', 1),
    ('Sales', N'Sales management', 2),
    ('Inventory', N'Inventory management', 3),
    ('Customer', N'Customer management', 4),
    ('Supplier', N'Supplier management', 5),
    ('Report', N'Reports', 6),
    ('System', N'System management', 7);
    PRINT 'Inserted Modules';
END
GO

-- 3. Actions
IF NOT EXISTS (SELECT * FROM Actions WHERE ActionName = 'View')
BEGIN
    INSERT INTO Actions (ActionName, Description) VALUES
    ('View', N'View data'),
    ('Create', N'Create new'),
    ('Update', N'Update'),
    ('Delete', N'Delete'),
    ('Export', N'Export report'),
    ('Approve', N'Approve'),
    ('Cancel', N'Cancel');
    PRINT 'Inserted Actions';
END
GO

-- 4. PaymentMethods
IF NOT EXISTS (SELECT * FROM PaymentMethods WHERE MethodName = 'Cash')
BEGIN
    INSERT INTO PaymentMethods (MethodName, Description) VALUES
    ('Cash', N'Cash payment'),
    ('BankTransfer', N'Bank transfer'),
    ('VNPay', N'VNPay payment'),
    ('Card', N'Credit/Debit card');
    PRINT 'Inserted PaymentMethods';
END
GO

-- 5. CustomerTiers
IF NOT EXISTS (SELECT * FROM CustomerTiers WHERE TierName = 'Normal')
BEGIN
    INSERT INTO CustomerTiers (TierName, MinSpending, MaxSpending, DiscountPercent, Benefits) VALUES
    ('Normal', 0, 4999999, 0, N'No special benefits'),
    ('Silver', 5000000, 19999999, 3, N'Priority returns'),
    ('Gold', 20000000, 49999999, 5, N'Priority returns + birthday gift'),
    ('VIP', 50000000, NULL, 10, N'All benefits + credit purchase');
    PRINT 'Inserted CustomerTiers';
END
GO

-- 6. TransactionTypes - Income
IF NOT EXISTS (SELECT * FROM TransactionTypes WHERE TypeName = N'Sales revenue')
BEGIN
    INSERT INTO TransactionTypes (TypeName, Category, Description) VALUES
    (N'Sales revenue', 'Income', N'Revenue from product sales'),
    (N'Customer debt collection', 'Income', N'Customer debt payment');
    PRINT 'Inserted TransactionTypes - Income';
END
GO
-- 7. TransactionTypes - Expense
IF NOT EXISTS (SELECT * FROM TransactionTypes WHERE TypeName = N'Purchase expense')
BEGIN
    INSERT INTO TransactionTypes (TypeName, Category, Description) VALUES
    (N'Purchase expense', 'Expense', N'Product purchase from suppliers'),
    (N'Employee salary', 'Expense', N'Employee salary/bonus payment'),
    (N'Shipping expense', 'Expense', N'Goods shipping cost'),
    (N'Marketing expense', 'Expense', N'Advertising and promotion cost'),
    (N'Refund expense', 'Expense', N'Customer refund'),
    (N'Operating expense', 'Expense', N'Electricity, water, internet, rent'),
    (N'Other expense', 'Expense', N'Other expenses');
    PRINT 'Inserted TransactionTypes - Expense';
END
GO

-- 8. SystemSettings
IF NOT EXISTS (SELECT * FROM SystemSettings WHERE SettingKey = 'return_days')
BEGIN
    INSERT INTO SystemSettings (SettingKey, SettingValue, DataType, Category, Description) VALUES
    ('return_days', '7', 'Number', 'Return', N'Number of days allowed for returns'),
    ('loyalty_points_rate', '1000', 'Number', 'Loyalty', N'1000 VND = 1 point'),
    ('vat_percent', '10', 'Number', 'Tax', N'VAT percentage'),
    ('tier_silver_min', '5000000', 'Number', 'Loyalty', N'Minimum threshold for Silver tier'),
    ('tier_gold_min', '20000000', 'Number', 'Loyalty', N'Minimum threshold for Gold tier'),
    ('tier_vip_min', '50000000', 'Number', 'Loyalty', N'Minimum threshold for VIP tier'),
    ('allow_negative_inventory', 'false', 'Boolean', 'General', N'Allow negative inventory sales'),
    ('company_name', N'ABC Retail Chain', 'String', 'General', N'Company name'),
    ('company_phone', '0123456789', 'String', 'General', N'Company phone number'),
    ('company_email', 'contact@company.com', 'String', 'General', N'Company email'),
    ('company_address', N'123 ABC Street, District 1, Ho Chi Minh City', 'String', 'General', N'Company address');
    PRINT 'Inserted SystemSettings';
END
GO

-- ============================================
-- AUTO-CREATE PERMISSIONS
-- ============================================

-- Create all permissions from Modules and Actions
IF NOT EXISTS (SELECT * FROM Permissions)
BEGIN
    INSERT INTO Permissions (ModuleID, ActionID, PermissionCode, Description)
    SELECT 
        m.ModuleID, 
        a.ActionID, 
        m.ModuleName + '.' + a.ActionName,
        a.Description + N' ' + m.ModuleName
    FROM Modules m
    CROSS JOIN Actions a;
    PRINT 'Created all Permissions';
END
GO

-- ============================================
-- GRANT ALL PERMISSIONS TO ADMIN ROLE
-- ============================================

IF NOT EXISTS (SELECT * FROM RolePermissions WHERE RoleID = 1)
BEGIN
    INSERT INTO RolePermissions (RoleID, PermissionID)
    SELECT 1, PermissionID FROM Permissions;
    PRINT 'Granted all permissions to Admin role';
END
GO

-- ============================================
-- IMPORTANT NOTES
-- ============================================

PRINT '';
PRINT '============================================';
PRINT 'DATABASE SCHEMA CREATED SUCCESSFULLY!';
PRINT '============================================';
PRINT '';
PRINT 'Next steps:';
PRINT '1. Create first Admin user';
PRINT '2. Create Stores (branches)';
PRINT '3. Create Warehouses';
PRINT '4. Create Categories and Products';
PRINT '5. Assign permissions to other Roles (Owner, StoreManager, SalesStaff, WarehouseStaff)';
PRINT '';
PRINT 'Example to create Admin user:';
PRINT 'INSERT INTO Users (Username, Password, FullName, Email, RoleID, Status)';
PRINT 'VALUES (''admin'', ''$2a$10$...'', N''Administrator'', ''admin@company.com'', 1, ''Active'');';
PRINT '';
PRINT 'Note: Password must be encrypted using bcrypt or similar';
PRINT '============================================';
GO