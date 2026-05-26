# 📊 HỆ THỐNG QUẢN LÝ CHUỖI CỬA HÀNG - SQL SERVER DATABASE SCHEMA

## 📁 Danh sách file SQL Server

### ✅ File chính (Khuyến nghị sử dụng):
- **`sqlserver_complete.sql`** - File hoàn chỉnh chứa toàn bộ schema + dữ liệu mẫu

### 📦 Các file riêng lẻ (nếu muốn chạy từng phần):
1. `sqlserver_schema_part1.sql` - Quản lý người dùng & phân quyền
2. `sqlserver_schema_part2.sql` - Quản lý sản phẩm
3. `sqlserver_schema_part3.sql` - Quản lý kho
4. `sqlserver_schema_part4.sql` - Quản lý khách hàng
5. `sqlserver_schema_part5.sql` - Quản lý bán hàng
6. `sqlserver_schema_part6.sql` - Quản lý nhà cung cấp & nhập hàng
7. `sqlserver_schema_part7.sql` - Quản lý thu chi, báo cáo, website & cấu hình
8. `sqlserver_schema_part8_sample_data.sql` - Dữ liệu mẫu

---

## 🚀 Hướng dẫn sử dụng

### **Cách 1: Sử dụng SQL Server Management Studio (SSMS)**
1. Mở SQL Server Management Studio
2. Kết nối đến SQL Server
3. File → Open → File → Chọn `sqlserver_complete.sql`
4. Click Execute (F5) hoặc nút Execute

### **Cách 2: Sử dụng sqlcmd (Command Line)**
```cmd
sqlcmd -S localhost -U sa -P YourPassword -i "D:\Thangdev\SWP\kiotretail\profession\sqlserver_complete.sql"
```

### **Cách 3: Sử dụng Azure Data Studio**
1. Mở Azure Data Studio
2. Kết nối đến SQL Server
3. File → Open File → Chọn `sqlserver_complete.sql`
4. Click Run hoặc nhấn F5

---

## 📋 Tổng quan Database

### Database name: **`RetailChainManagement`**

### Tổng số bảng: **60 bảng**

#### 1. Quản lý người dùng & phân quyền (8 bảng)
- Users
- Roles
- Modules
- Actions
- Permissions
- RolePermissions
- ActivityLogs
- Stores

#### 2. Quản lý sản phẩm (9 bảng)
- Categories
- Products
- CustomerGroups
- PriceBooks
- PriceBookDetails
- PriceBookStores
- PriceBookCustomerGroups
- DiscountConfigs

#### 3. Quản lý kho (8 bảng)
- Warehouses
- Inventories
- StockTransfers
- StockTransferDetails
- StockCheckSessions
- StockCheckDetails
- StockCheckParticipants
- StockAdjustments

#### 4. Quản lý khách hàng (4 bảng)
- Customers
- CustomerTiers
- LoyaltyTransactions

#### 5. Quản lý bán hàng (8 bảng)
- PaymentMethods
- Vouchers
- Invoices
- InvoiceDetails
- OrderReturns
- OrderReturnDetails
- Refunds

#### 6. Quản lý nhà cung cấp & nhập hàng (7 bảng)
- Suppliers
- PurchaseOrders
- PurchaseOrderDetails
- Imports
- ImportDetails
- SupplierPayments
- SupplierDebts

#### 7. Quản lý thu chi (2 bảng)
- TransactionTypes
- Transactions

#### 8. Báo cáo (4 bảng)
- SalesReports
- LoyaltyReports
- InventoryReports
- ExportLogs

#### 9. Website (2 bảng)
- WebsiteContents
- ContactForms

#### 10. Cấu hình hệ thống (1 bảng)
- SystemSettings

---

## 🔑 Dữ liệu mẫu đã được tạo sẵn

### Roles (Vai trò):
- Admin
- Owner
- StoreManager
- SalesStaff
- WarehouseStaff

### Modules (Module hệ thống):
- Product, Sales, Inventory, Customer, Supplier, Report, System

### Actions (Hành động):
- View, Create, Update, Delete, Export, Approve, Cancel

### PaymentMethods (Phương thức thanh toán):
- Cash, BankTransfer, VNPay, Card

### CustomerTiers (Hạng thành viên):
- Normal: < 5.000.000đ (0%)
- Silver: 5.000.000đ - 20.000.000đ (3%)
- Gold: 20.000.000đ - 50.000.000đ (5%)
- VIP: > 50.000.000đ (10%)

### TransactionTypes (Loại giao dịch):
**Thu:**
- Thu bán hàng
- Thu công nợ khách hàng

**Chi:**
- Chi nhập hàng
- Chi lương nhân viên
- Chi vận chuyển
- Chi marketing
- Chi hoàn tiền
- Chi vận hành
- Chi khác

### SystemSettings (Cấu hình hệ thống):
- return_days: 7 ngày
- loyalty_points_rate: 1000đ = 1 điểm
- vat_percent: 10%
- tier_silver_min: 5.000.000đ
- tier_gold_min: 20.000.000đ
- tier_vip_min: 50.000.000đ
- allow_negative_inventory: false

### Permissions:
✅ Tự động tạo tất cả permissions từ Modules × Actions (49 permissions)
✅ Admin role đã được cấp toàn quyền

---

## ⚙️ Các bước tiếp theo sau khi chạy SQL

### 1. Tạo user Admin đầu tiên
```sql
INSERT INTO Users (Username, Password, FullName, Email, RoleID, Status)
VALUES ('admin', '$2a$10$...', N'Administrator', 'admin@company.com', 1, 'Active');
-- Lưu ý: Password cần được mã hóa bằng bcrypt
```

### 2. Tạo Stores (Cửa hàng)
```sql
INSERT INTO Stores (StoreCode, StoreName, Address, Phone, Status)
VALUES 
('ST001', N'Chi nhánh Quận 1', N'123 Nguyễn Huệ, Q1, TP.HCM', '0901234567', 'Active'),
('ST002', N'Chi nhánh Quận 3', N'456 Lê Văn Sỹ, Q3, TP.HCM', '0901234568', 'Active');
```

### 3. Tạo Warehouses (Kho)
```sql
INSERT INTO Warehouses (WarehouseCode, WarehouseName, StoreID, Status)
VALUES 
('WH001', N'Kho Quận 1', 1, 'Active'),
('WH002', N'Kho Quận 3', 2, 'Active');
```

### 4. Phân quyền cho các Role khác
```sql
-- Ví dụ: Owner có quyền xem tất cả
INSERT INTO RolePermissions (RoleID, PermissionID)
SELECT 2, PermissionID 
FROM Permissions 
WHERE PermissionCode LIKE '%.View' OR PermissionCode LIKE 'Report.%';
```

---

## 🎯 Điểm khác biệt so với MySQL

### ✅ Đã chuyển đổi:
1. **AUTO_INCREMENT** → **IDENTITY(1,1)**
2. **DATETIME** → **DATETIME2**
3. **ENUM** → **NVARCHAR + CHECK constraint**
4. **BOOLEAN** → **BIT**
5. **TEXT** → **NVARCHAR(MAX)**
6. **JSON** → **NVARCHAR(MAX)** (SQL Server 2016+ hỗ trợ JSON functions)
7. **ON UPDATE CURRENT_TIMESTAMP** → **TRIGGER**
8. **GENERATED ALWAYS AS** → **AS ... PERSISTED**
9. **IF NOT EXISTS** → Kiểm tra sys.tables, sys.foreign_keys, sys.triggers
10. **NVARCHAR** cho tất cả text fields (hỗ trợ Unicode/Tiếng Việt)

### 🔧 Triggers đã tạo:
- Tự động update ModifiedDate cho: Users, Stores, Products, PriceBooks, DiscountConfigs, Inventories, StockCheckSessions, CustomerTiers, Customers, Invoices, OrderReturns, Suppliers, PurchaseOrders, SupplierDebts, WebsiteContents, SystemSettings

---

## 📝 Ghi chú quan trọng

### Yêu cầu hệ thống:
- **SQL Server version**: 2016 trở lên (để hỗ trợ JSON và computed columns)
- **Collation**: SQL_Latin1_General_CP1_CI_AS (mặc định, hỗ trợ tiếng Việt)
- **Recovery Model**: FULL (khuyến nghị cho production)

### Business Rules được enforce:
- ✅ Không bán âm kho: `CHECK (OnHand >= 0)`
- ✅ Điểm tích lũy không âm: `CHECK (LoyaltyPoints >= 0)`
- ✅ Số tiền không âm: `CHECK (Amount > 0)`
- ✅ Ngày kết thúc >= Ngày bắt đầu: `CHECK (EndDate >= StartDate)`
- ✅ Username, Email, Phone unique
- ✅ Tồn kho Available = OnHand - Reserved (auto calculated)
- ✅ TotalPrice = Quantity × UnitPrice - DiscountAmount (auto calculated)

### Indexes đã tạo:
- ✅ Primary Keys (clustered)
- ✅ Unique Keys
- ✅ Foreign Keys
- ✅ Composite indexes cho queries thường dùng
- ✅ Indexes cho date ranges và searches

---

## 🔍 Kiểm tra sau khi chạy

```sql
-- Kiểm tra số lượng bảng
SELECT COUNT(*) AS TotalTables FROM sys.tables;
-- Kết quả: 60 bảng

-- Kiểm tra dữ liệu mẫu
SELECT * FROM Roles;
SELECT * FROM Modules;
SELECT * FROM Actions;
SELECT * FROM PaymentMethods;
SELECT * FROM CustomerTiers;
SELECT * FROM TransactionTypes;
SELECT * FROM SystemSettings;

-- Kiểm tra Permissions
SELECT COUNT(*) AS TotalPermissions FROM Permissions;
-- Kết quả: 49 permissions (7 modules × 7 actions)

-- Kiểm tra phân quyền Admin
SELECT COUNT(*) AS AdminPermissions 
FROM RolePermissions 
WHERE RoleID = 1;
-- Kết quả: 49 permissions
```

---

## 🆘 Troubleshooting

### Lỗi: "Database already exists"
```sql
-- Xóa database cũ (cẩn thận!)
DROP DATABASE IF EXISTS RetailChainManagement;
```

### Lỗi: "Cannot drop database because it is currently in use"
```sql
USE master;
GO
ALTER DATABASE RetailChainManagement SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO
DROP DATABASE RetailChainManagement;
GO
```

### Lỗi: Foreign key constraint
- Đảm bảo chạy đúng thứ tự các file (part1 → part8)
- Hoặc sử dụng file `sqlserver_complete.sql`

---

**Chúc bạn triển khai thành công trên SQL Server! 🎉**
