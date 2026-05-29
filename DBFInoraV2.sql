CREATE DATABASE DBFinoraV2

use DBFinoraV2
-- ============================================================
--  1. role
-- ============================================================
CREATE TABLE role (
    role_id    INT           IDENTITY(1,1) PRIMARY KEY,
    role_name  NVARCHAR(100) NOT NULL,
    discription NVARCHAR(255),
    created_at DATETIME      DEFAULT GETDATE(),
    updated_at DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  2. branch
-- ============================================================
CREATE TABLE branch (
    branch_id    INT           IDENTITY(1,1) PRIMARY KEY,
    branch_name  NVARCHAR(150) NOT NULL,
    branch_code  NVARCHAR(50)  UNIQUE,
    address      NVARCHAR(300),
    phone        NVARCHAR(20),
    email        NVARCHAR(150),
    opening_time NVARCHAR(10),   -- HH:mm
    closing_time NVARCHAR(10),   -- HH:mm
    status       NVARCHAR(20)    DEFAULT 'active'
                                 CHECK (status IN ('active','locked')),
    created_at   DATETIME        DEFAULT GETDATE(),
    update_at    DATETIME        DEFAULT GETDATE()
);
GO

-- ============================================================
--  3. employee
-- ============================================================
CREATE TABLE employee (
    emp_id        INT           IDENTITY(1,1) PRIMARY KEY,
    branch_id     INT           NOT NULL
                                REFERENCES branch(branch_id),
    full_name     NVARCHAR(150) NOT NULL,
    gender        NVARCHAR(10),
    bod           DATE,
    address       NVARCHAR(300),
    email         NVARCHAR(150) UNIQUE,
    phone         NVARCHAR(20),
    password_hash NVARCHAR(255),
    status        NVARCHAR(20)  DEFAULT 'ACTIVE'
                                CHECK (status IN ('ACTIVE','INACTIVE','ON_LEAVE')),
    created_at    DATETIME      DEFAULT GETDATE(),
    update_at     DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  4. employee_role  (bảng trung gian Employee ↔ Role)
-- ============================================================
CREATE TABLE employee_role (
    emp_role_id INT      IDENTITY(1,1) PRIMARY KEY,
    emp_id      INT      NOT NULL REFERENCES employee(emp_id),
    role_id     INT      NOT NULL REFERENCES role(role_id),
    assigned_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================================
--  5. customer
-- ============================================================
CREATE TABLE customer (
    cus_id      INT           IDENTITY(1,1) PRIMARY KEY,
    full_name   NVARCHAR(150) NOT NULL,
    gender      NVARCHAR(10),
    bod         DATE,
    address     NVARCHAR(300),
    email       NVARCHAR(150),
    phone       NVARCHAR(20),
    cus_type    NVARCHAR(50),
    total_spent DECIMAL(18,2) DEFAULT 0,
    created_at  DATETIME      DEFAULT GETDATE(),
    updated_at  DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  6. customer_point
-- ============================================================
CREATE TABLE customer_point (
    cus_point_id   INT           IDENTITY(1,1) PRIMARY KEY,
    cus_id         INT           NOT NULL REFERENCES customer(cus_id),
    current_points INT           DEFAULT 0,
    lifetime_points INT          DEFAULT 0,
    level_name     NVARCHAR(50),
    updated_at     DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  7. voucher
-- ============================================================
CREATE TABLE voucher (
    voucher_id     INT            IDENTITY(1,1) PRIMARY KEY,
    voucher_code   NVARCHAR(50)   UNIQUE NOT NULL,
    voucher_name   NVARCHAR(150),
    discount_type  NVARCHAR(20)   CHECK (discount_type IN ('PERCENT','FIXED')),
    discount_value DECIMAL(18,2),
    used_quantity  INT            DEFAULT 0,
    start_date     DATE,
    end_date       DATE,
    status         NVARCHAR(20)   DEFAULT 'active',
    created_at     DATETIME       DEFAULT GETDATE()
);
GO

-- ============================================================
--  8. supplier
-- ============================================================
CREATE TABLE supplier (
    supplier_id   INT           IDENTITY(1,1) PRIMARY KEY,
    supplier_name NVARCHAR(150) NOT NULL,
    phone_number  NVARCHAR(20),
    address       NVARCHAR(300),
    status        NVARCHAR(20)  DEFAULT 'active',
    created_at    DATETIME      DEFAULT GETDATE(),
    updated_at    DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  9. warehouse
-- ============================================================
CREATE TABLE warehouse (
    warehouse_id   INT           IDENTITY(1,1) PRIMARY KEY,
    warehouse_name NVARCHAR(150) NOT NULL,
    branch_id      INT           NOT NULL REFERENCES branch(branch_id),
    address        NVARCHAR(300),
    status         NVARCHAR(20)  DEFAULT 'active',
    created_at     DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  10. unit
-- ============================================================
CREATE TABLE unit (
    unit_id     INT           IDENTITY(1,1) PRIMARY KEY,
    unit_name   NVARCHAR(50)  NOT NULL,
    description NVARCHAR(255)
);
GO

-- ============================================================
--  11. category
-- ============================================================
CREATE TABLE category (
    category_id        INT           IDENTITY(1,1) PRIMARY KEY,
    category_name      NVARCHAR(150) NOT NULL,
    description        NVARCHAR(255),
    parent_category_id INT           REFERENCES category(category_id),
    status             NVARCHAR(20)  DEFAULT 'active',
    created_at         DATETIME      DEFAULT GETDATE(),
    update_at          DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  12. product
-- ============================================================
CREATE TABLE product (
    product_id   INT            IDENTITY(1,1) PRIMARY KEY,
    product_name NVARCHAR(200)  NOT NULL,
    quantity     INT            DEFAULT 0,
    category_id  INT            REFERENCES category(category_id),
    unit_id      INT            REFERENCES unit(unit_id),
    selling_price DECIMAL(18,2) DEFAULT 0,
    status       NVARCHAR(20)   DEFAULT 'active',
    created_at   DATETIME       DEFAULT GETDATE(),
    update_at    DATETIME       DEFAULT GETDATE()
);
GO

-- ============================================================
--  13. inventory
-- ============================================================
CREATE TABLE inventory (
    inventory_id       INT      IDENTITY(1,1) PRIMARY KEY,
    warehouse_id       INT      NOT NULL REFERENCES warehouse(warehouse_id),
    product_id         INT      NOT NULL REFERENCES product(product_id),
    quantity_in_stock  INT      DEFAULT 0,
    updated_at         DATETIME DEFAULT GETDATE()
);
GO

-- ============================================================
--  14. order  (dùng [] vì ORDER là từ khóa SQL)
-- ============================================================
CREATE TABLE [order] (
    order_id        INT            IDENTITY(1,1) PRIMARY KEY,
    order_code      NVARCHAR(50)   UNIQUE,
    order_type      NVARCHAR(30),
    customer_id     INT            REFERENCES customer(cus_id),
    branch_id       INT            REFERENCES branch(branch_id),
    supplier_id     INT            REFERENCES supplier(supplier_id),
    emp_id          INT            REFERENCES employee(emp_id),
    voucher_id      INT            REFERENCES voucher(voucher_id),
    warehouse_id    INT            REFERENCES warehouse(warehouse_id),
    subtotal        DECIMAL(18,2)  DEFAULT 0,
    discount_amount DECIMAL(18,2)  DEFAULT 0,
    total_amount    DECIMAL(18,2)  DEFAULT 0,
    payment_method  NVARCHAR(50),
    status          NVARCHAR(30)   DEFAULT 'PENDING',
    created_at      DATETIME       DEFAULT GETDATE()
);
GO

-- ============================================================
--  15. order_detail
-- ============================================================
CREATE TABLE order_detail (
    order_detail_id INT            IDENTITY(1,1) PRIMARY KEY,
    order_id        INT            NOT NULL REFERENCES [order](order_id),
    product_id      INT            NOT NULL REFERENCES product(product_id),
    quantity        INT            DEFAULT 1,
    unit_price      DECIMAL(18,2)  DEFAULT 0,
    total_price     DECIMAL(18,2)  DEFAULT 0
);
GO

-- ============================================================
--  16. payment
-- ============================================================
CREATE TABLE payment (
    payment_id       INT            IDENTITY(1,1) PRIMARY KEY,
    order_id         INT            NOT NULL REFERENCES [order](order_id),
    payment_method   NVARCHAR(50),
    payment_amount   DECIMAL(18,2)  DEFAULT 0,
    payment_date     DATETIME       DEFAULT GETDATE(),
    payment_status   NVARCHAR(30),
    transaction_code NVARCHAR(100)
);
GO

-- ============================================================
--  17. point_transaction
-- ============================================================
CREATE TABLE point_transaction (
    point_transaction_id INT           IDENTITY(1,1) PRIMARY KEY,
    cus_point_id         INT           NOT NULL REFERENCES customer_point(cus_point_id),
    order_id             INT           REFERENCES [order](order_id),
    before_points        INT           DEFAULT 0,
    after_points         INT           DEFAULT 0,
    description          NVARCHAR(255),
    created_at           DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  18. stock_transfer
-- ============================================================
CREATE TABLE stock_transfer (
    stock_transfer_id  INT           IDENTITY(1,1) PRIMARY KEY,
    from_warehouse_id  INT           NOT NULL REFERENCES warehouse(warehouse_id),
    to_warehouse_id    INT           NOT NULL REFERENCES warehouse(warehouse_id),
    transfer_code      NVARCHAR(50),
    transfer_date      DATETIME      DEFAULT GETDATE(),
    status             NVARCHAR(30),
    note               NVARCHAR(500),
    created_by         INT           REFERENCES employee(emp_id)
);
GO

-- ============================================================
--  19. stock_transfer_detail
-- ============================================================
CREATE TABLE stock_transfer_detail (
    stock_transfer_detail_id INT  IDENTITY(1,1) PRIMARY KEY,
    stock_transfer_id        INT  NOT NULL REFERENCES stock_transfer(stock_transfer_id),
    product_id               INT  NOT NULL REFERENCES product(product_id),
    quantity                 INT  DEFAULT 0
);
GO

-- ============================================================
--  20. stock_transaction
-- ============================================================
CREATE TABLE stock_transaction (
    stock_transaction_id INT           IDENTITY(1,1) PRIMARY KEY,
    warehouse_id         INT           NOT NULL REFERENCES warehouse(warehouse_id),
    product_id           INT           NOT NULL REFERENCES product(product_id),
    reference_type       NVARCHAR(50),  -- ORDER / TRANSFER / ADJUSTMENT
    reference_id         INT,
    transaction_type     NVARCHAR(20),  -- IN / OUT
    quantity             INT           DEFAULT 0,
    before_quantity      INT           DEFAULT 0,
    after_quantity       INT           DEFAULT 0,
    note                 NVARCHAR(500),
    created_by           INT           REFERENCES employee(emp_id),
    created_at           DATETIME      DEFAULT GETDATE()
);
GO

-- ============================================================
--  21. audit_log
-- ============================================================
CREATE TABLE audit_log (
    audit_log_id INT           IDENTITY(1,1) PRIMARY KEY,
    emp_id       INT           REFERENCES employee(emp_id),
    action_name  NVARCHAR(100),
    table_name   NVARCHAR(100),
    record_id    INT,
    old_data     NVARCHAR(MAX),
    new_data     NVARCHAR(MAX),
    created_at   DATETIME      DEFAULT GETDATE()
);
GO

