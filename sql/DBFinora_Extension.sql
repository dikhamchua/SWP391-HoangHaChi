-- =====================================================================
-- KiotRetail - MySQL Schema Extension
-- Combines: purchase-order-schema, V003 approval_history,
--           V004 purchase_order_approval, V005 stock_adjustment, V006 stock_transfer
--
-- Run AFTER DBFinora.sql.
-- All idempotent (drop + create); safe to re-run.
-- =====================================================================
USE DBFinora;

-- ---------------------------------------------------------------------
-- DROP existing tables (in dependency order)
-- ---------------------------------------------------------------------
DROP VIEW IF EXISTS V_StockTransferList;
DROP VIEW IF EXISTS V_StockAdjustmentList;
DROP VIEW IF EXISTS V_PurchaseOrderList;
DROP TABLE IF EXISTS StockTransferDetail;
DROP TABLE IF EXISTS StockTransfer;
DROP TABLE IF EXISTS StockAdjustmentDetail;
DROP TABLE IF EXISTS StockAdjustment;
DROP TABLE IF EXISTS ApprovalHistory;
DROP TABLE IF EXISTS PurchaseOrderDetail;
DROP TABLE IF EXISTS PurchaseOrder;

-- =====================================================================
-- PURCHASE ORDER (header) - approval workflow
-- =====================================================================
CREATE TABLE PurchaseOrder (
    PurchaseOrderID INT AUTO_INCREMENT PRIMARY KEY,
    SupplierID      INT NOT NULL,
    BranchID        INT NOT NULL,
    EmployeeID      INT NOT NULL,
    OrderCode       VARCHAR(100) UNIQUE NOT NULL,
    Status          VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    TotalAmount     DECIMAL(18,2) NOT NULL DEFAULT 0,
    Note            TEXT NULL,
    CreatedBy       INT NULL,
    SubmittedAt     DATETIME NULL,
    ApprovedBy      INT NULL,
    ApprovedAt      DATETIME NULL,
    RejectedBy      INT NULL,
    RejectedAt      DATETIME NULL,
    RejectedReason  VARCHAR(500) NULL,
    CancelledBy     INT NULL,
    CancelledAt     DATETIME NULL,
    CancelledReason VARCHAR(500) NULL,
    CompletedAt     DATETIME NULL,
    UpdatedAt       DATETIME NULL,
    CreatedAt       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_PurchaseOrder_Supplier   FOREIGN KEY (SupplierID)  REFERENCES Supplier(SupplierID),
    CONSTRAINT FK_PurchaseOrder_Branch     FOREIGN KEY (BranchID)    REFERENCES Branch(BranchID),
    CONSTRAINT FK_PurchaseOrder_Employee   FOREIGN KEY (EmployeeID)  REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_PurchaseOrder_CreatedBy  FOREIGN KEY (CreatedBy)   REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_PurchaseOrder_ApprovedBy FOREIGN KEY (ApprovedBy)  REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_PurchaseOrder_RejectedBy FOREIGN KEY (RejectedBy)  REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_PurchaseOrder_CancelledBy FOREIGN KEY (CancelledBy) REFERENCES Employee(EmployeeID),
    CONSTRAINT CK_PurchaseOrder_Status CHECK (Status IN
        ('DRAFT','PENDING_APPROVAL','APPROVED','REJECTED','IN_PROGRESS','RECEIVING','COMPLETED','CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_PurchaseOrder_Supplier  ON PurchaseOrder(SupplierID);
CREATE INDEX IDX_PurchaseOrder_Status    ON PurchaseOrder(Status);
CREATE INDEX IDX_PurchaseOrder_Branch    ON PurchaseOrder(BranchID);
CREATE INDEX IDX_PurchaseOrder_CreatedBy ON PurchaseOrder(CreatedBy);

-- =====================================================================
-- PURCHASE ORDER DETAIL
-- =====================================================================
CREATE TABLE PurchaseOrderDetail (
    PODetailID       INT AUTO_INCREMENT PRIMARY KEY,
    PurchaseOrderID  INT NOT NULL,
    ProductID        INT NOT NULL,
    Quantity         INT NOT NULL DEFAULT 1,
    ReceivedQuantity INT NOT NULL DEFAULT 0,
    UnitCost         DECIMAL(18,2) NOT NULL DEFAULT 0,
    Subtotal         DECIMAL(18,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_PODetail_PurchaseOrder FOREIGN KEY (PurchaseOrderID) REFERENCES PurchaseOrder(PurchaseOrderID) ON DELETE CASCADE,
    CONSTRAINT FK_PODetail_Product       FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    CONSTRAINT CK_PODetail_Quantity      CHECK (Quantity > 0),
    CONSTRAINT CK_PODetail_UnitCost      CHECK (UnitCost >= 0),
    CONSTRAINT CK_PODetail_ReceivedQty   CHECK (ReceivedQuantity >= 0 AND ReceivedQuantity <= Quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_PODetail_Order   ON PurchaseOrderDetail(PurchaseOrderID);
CREATE INDEX IDX_PODetail_Product ON PurchaseOrderDetail(ProductID);

-- =====================================================================
-- APPROVAL HISTORY (audit log for all approvable documents)
-- =====================================================================
CREATE TABLE ApprovalHistory (
    HistoryID    INT AUTO_INCREMENT PRIMARY KEY,
    DocumentType VARCHAR(50)  NOT NULL,
    DocumentID   INT          NOT NULL,
    FromStatus   VARCHAR(30)  NOT NULL,
    ToStatus     VARCHAR(30)  NOT NULL,
    Action       VARCHAR(30)  NOT NULL,
    PerformedBy  INT          NOT NULL,
    Reason       VARCHAR(500) NULL,
    CreatedAt    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_ApprovalHistory_Employee FOREIGN KEY (PerformedBy) REFERENCES Employee(EmployeeID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IX_ApprovalHistory_Document ON ApprovalHistory(DocumentType, DocumentID);

-- =====================================================================
-- STOCK ADJUSTMENT
-- =====================================================================
CREATE TABLE StockAdjustment (
    AdjustmentID       INT AUTO_INCREMENT PRIMARY KEY,
    AdjustmentCode     VARCHAR(50)   NOT NULL,
    BranchID           INT           NOT NULL,
    Status             VARCHAR(30)   NOT NULL DEFAULT 'DRAFT',
    Reason             VARCHAR(500)  NOT NULL,
    Note               VARCHAR(500)  NULL,
    TotalVarianceValue DECIMAL(18,2) DEFAULT 0,
    CreatedBy          INT           NOT NULL,
    SubmittedAt        DATETIME      NULL,
    ApprovedBy         INT           NULL,
    ApprovedAt         DATETIME      NULL,
    RejectedBy         INT           NULL,
    RejectedAt         DATETIME      NULL,
    RejectedReason     VARCHAR(500)  NULL,
    CancelledBy        INT           NULL,
    CancelledAt        DATETIME      NULL,
    CancelledReason    VARCHAR(500)  NULL,
    CreatedAt          DATETIME      DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt          DATETIME      NULL,
    CONSTRAINT UQ_StockAdjustment_Code UNIQUE (AdjustmentCode),
    CONSTRAINT CK_StockAdjustment_Status CHECK (Status IN ('DRAFT','PENDING_APPROVAL','APPROVED','REJECTED','CANCELLED')),
    CONSTRAINT FK_StockAdjustment_Branch       FOREIGN KEY (BranchID)    REFERENCES Branch(BranchID),
    CONSTRAINT FK_StockAdjustment_CreatedBy    FOREIGN KEY (CreatedBy)   REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockAdjustment_ApprovedBy   FOREIGN KEY (ApprovedBy)  REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockAdjustment_RejectedBy   FOREIGN KEY (RejectedBy)  REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockAdjustment_CancelledBy  FOREIGN KEY (CancelledBy) REFERENCES Employee(EmployeeID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_StockAdjustment_Branch ON StockAdjustment(BranchID);
CREATE INDEX IDX_StockAdjustment_Status ON StockAdjustment(Status);

CREATE TABLE StockAdjustmentDetail (
    AdjustmentDetailID INT AUTO_INCREMENT PRIMARY KEY,
    AdjustmentID       INT           NOT NULL,
    ProductID          INT           NOT NULL,
    SystemQuantity     INT           NOT NULL,
    ActualQuantity     INT           NOT NULL,
    Variance           INT           NOT NULL,
    VarianceValue      DECIMAL(18,2) NOT NULL,
    Reason             VARCHAR(255)  NULL,
    CONSTRAINT FK_StockAdjustmentDetail_Adjustment FOREIGN KEY (AdjustmentID) REFERENCES StockAdjustment(AdjustmentID) ON DELETE CASCADE,
    CONSTRAINT FK_StockAdjustmentDetail_Product    FOREIGN KEY (ProductID)    REFERENCES Product(ProductID),
    CONSTRAINT UQ_StockAdjustmentDetail_AdjustmentProduct UNIQUE (AdjustmentID, ProductID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_StockAdjustmentDetail_Product ON StockAdjustmentDetail(ProductID);

-- =====================================================================
-- STOCK TRANSFER (UC-5.3 schema with approval)
-- =====================================================================
CREATE TABLE StockTransfer (
    TransferID      INT AUTO_INCREMENT PRIMARY KEY,
    TransferCode    VARCHAR(50)  NOT NULL,
    FromBranchID    INT          NOT NULL,
    ToBranchID      INT          NOT NULL,
    Status          VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    Note            VARCHAR(500) NULL,
    TotalItems      INT          NOT NULL DEFAULT 0,
    TotalQuantity   INT          NOT NULL DEFAULT 0,
    CreatedBy       INT          NOT NULL,
    SubmittedAt     DATETIME     NULL,
    ApprovedBy      INT          NULL,
    ApprovedAt      DATETIME     NULL,
    RejectedBy      INT          NULL,
    RejectedAt      DATETIME     NULL,
    RejectedReason  VARCHAR(500) NULL,
    ShippedBy       INT          NULL,
    ShippedAt       DATETIME     NULL,
    ReceivedBy      INT          NULL,
    ReceivedAt      DATETIME     NULL,
    CancelledBy     INT          NULL,
    CancelledAt     DATETIME     NULL,
    CancelledReason VARCHAR(500) NULL,
    CreatedAt       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt       DATETIME     NULL,
    CONSTRAINT UQ_StockTransfer_Code UNIQUE (TransferCode),
    CONSTRAINT CK_StockTransfer_Status CHECK (Status IN
        ('DRAFT','PENDING_APPROVAL','APPROVED','REJECTED','IN_TRANSIT','COMPLETED','CANCELLED')),
    CONSTRAINT CK_StockTransfer_DifferentBranches CHECK (FromBranchID <> ToBranchID),
    CONSTRAINT FK_StockTransfer_FromBranch  FOREIGN KEY (FromBranchID) REFERENCES Branch(BranchID),
    CONSTRAINT FK_StockTransfer_ToBranch    FOREIGN KEY (ToBranchID)   REFERENCES Branch(BranchID),
    CONSTRAINT FK_StockTransfer_CreatedBy   FOREIGN KEY (CreatedBy)    REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockTransfer_ApprovedBy  FOREIGN KEY (ApprovedBy)   REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockTransfer_RejectedBy  FOREIGN KEY (RejectedBy)   REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockTransfer_ShippedBy   FOREIGN KEY (ShippedBy)    REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockTransfer_ReceivedBy  FOREIGN KEY (ReceivedBy)   REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_StockTransfer_CancelledBy FOREIGN KEY (CancelledBy)  REFERENCES Employee(EmployeeID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_StockTransfer_FromBranch ON StockTransfer(FromBranchID);
CREATE INDEX IDX_StockTransfer_ToBranch   ON StockTransfer(ToBranchID);
CREATE INDEX IDX_StockTransfer_Status     ON StockTransfer(Status);

CREATE TABLE StockTransferDetail (
    TransferDetailID INT AUTO_INCREMENT PRIMARY KEY,
    TransferID       INT          NOT NULL,
    ProductID        INT          NOT NULL,
    Quantity         INT          NOT NULL,
    ReceivedQuantity INT          NOT NULL DEFAULT 0,
    Note             VARCHAR(255) NULL,
    CONSTRAINT CK_StockTransferDetail_QtyPositive       CHECK (Quantity > 0),
    CONSTRAINT CK_StockTransferDetail_ReceivedQtyNonNeg CHECK (ReceivedQuantity >= 0),
    CONSTRAINT CK_StockTransferDetail_ReceivedNotExceed CHECK (ReceivedQuantity <= Quantity),
    CONSTRAINT UQ_StockTransferDetail_TransferProduct   UNIQUE (TransferID, ProductID),
    CONSTRAINT FK_StockTransferDetail_Transfer FOREIGN KEY (TransferID) REFERENCES StockTransfer(TransferID) ON DELETE CASCADE,
    CONSTRAINT FK_StockTransferDetail_Product  FOREIGN KEY (ProductID)  REFERENCES Product(ProductID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_StockTransferDetail_Product ON StockTransferDetail(ProductID);

-- =====================================================================
-- VIEWS for list pages
-- =====================================================================
CREATE VIEW V_PurchaseOrderList AS
SELECT
    po.PurchaseOrderID, po.OrderCode, po.Status, po.TotalAmount, po.Note,
    po.CreatedAt, po.SubmittedAt, po.ApprovedAt, po.RejectedAt, po.RejectedReason,
    po.CancelledAt, po.CancelledReason, po.CompletedAt, po.UpdatedAt,
    po.SupplierID, s.Name        AS SupplierName,
    po.BranchID,   b.Name        AS BranchName,
    po.EmployeeID, e.FullName    AS EmployeeName,
    po.CreatedBy,  cby.FullName  AS CreatedByName,
    po.ApprovedBy, aby.FullName  AS ApprovedByName,
    po.RejectedBy, rby.FullName  AS RejectedByName,
    po.CancelledBy,xby.FullName  AS CancelledByName
FROM PurchaseOrder po
LEFT JOIN Supplier s   ON s.SupplierID  = po.SupplierID
LEFT JOIN Branch   b   ON b.BranchID    = po.BranchID
LEFT JOIN Employee e   ON e.EmployeeID  = po.EmployeeID
LEFT JOIN Employee cby ON cby.EmployeeID = po.CreatedBy
LEFT JOIN Employee aby ON aby.EmployeeID = po.ApprovedBy
LEFT JOIN Employee rby ON rby.EmployeeID = po.RejectedBy
LEFT JOIN Employee xby ON xby.EmployeeID = po.CancelledBy;

CREATE VIEW V_StockAdjustmentList AS
SELECT
    sa.AdjustmentID, sa.AdjustmentCode, sa.BranchID, b.Name AS BranchName,
    sa.Status, sa.Reason, sa.Note, sa.TotalVarianceValue,
    sa.CreatedBy,   ec.FullName AS CreatedByName,
    sa.SubmittedAt,
    sa.ApprovedBy,  ea.FullName AS ApprovedByName, sa.ApprovedAt,
    sa.RejectedBy,  er.FullName AS RejectedByName, sa.RejectedAt, sa.RejectedReason,
    sa.CancelledBy, ex.FullName AS CancelledByName, sa.CancelledAt, sa.CancelledReason,
    sa.CreatedAt, sa.UpdatedAt
FROM StockAdjustment sa
INNER JOIN Branch    b  ON sa.BranchID    = b.BranchID
INNER JOIN Employee  ec ON sa.CreatedBy   = ec.EmployeeID
LEFT  JOIN Employee  ea ON sa.ApprovedBy  = ea.EmployeeID
LEFT  JOIN Employee  er ON sa.RejectedBy  = er.EmployeeID
LEFT  JOIN Employee  ex ON sa.CancelledBy = ex.EmployeeID;

CREATE VIEW V_StockTransferList AS
SELECT
    st.TransferID, st.TransferCode, st.Status, st.Note, st.TotalItems, st.TotalQuantity,
    st.FromBranchID, fb.Name AS FromBranchName,
    st.ToBranchID,   tb.Name AS ToBranchName,
    st.CreatedBy,    creator.FullName  AS CreatedByName,
    st.SubmittedAt,
    st.ApprovedBy,   approver.FullName AS ApprovedByName, st.ApprovedAt,
    st.RejectedBy,   rejecter.FullName AS RejectedByName, st.RejectedAt, st.RejectedReason,
    st.ShippedBy,    shipper.FullName  AS ShippedByName,  st.ShippedAt,
    st.ReceivedBy,   receiver.FullName AS ReceivedByName, st.ReceivedAt,
    st.CancelledBy,  canceller.FullName AS CancelledByName, st.CancelledAt, st.CancelledReason,
    st.CreatedAt, st.UpdatedAt
FROM StockTransfer st
INNER JOIN Branch   fb        ON fb.BranchID         = st.FromBranchID
INNER JOIN Branch   tb        ON tb.BranchID         = st.ToBranchID
INNER JOIN Employee creator   ON creator.EmployeeID  = st.CreatedBy
LEFT  JOIN Employee approver  ON approver.EmployeeID = st.ApprovedBy
LEFT  JOIN Employee rejecter  ON rejecter.EmployeeID = st.RejectedBy
LEFT  JOIN Employee shipper   ON shipper.EmployeeID  = st.ShippedBy
LEFT  JOIN Employee receiver  ON receiver.EmployeeID = st.ReceivedBy
LEFT  JOIN Employee canceller ON canceller.EmployeeID = st.CancelledBy;
