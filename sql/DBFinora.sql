-- MySQL dump 10.13  Distrib 8.0.36, for Linux (x86_64)
--
-- Host: localhost    Database: DBFinora
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ActivityBranch`
--

DROP TABLE IF EXISTS `ActivityBranch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ActivityBranch` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `FK_ID` int NOT NULL,
  `Type` varchar(20) NOT NULL,
  `CreatedBy` int DEFAULT NULL,
  `Description` text NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ActivityBranch_CreatedBy` (`CreatedBy`),
  KEY `IDX_ActivityBranch_FK_ID` (`FK_ID`,`ID` DESC),
  CONSTRAINT `FK_ActivityBranch_CreatedBy` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `CK_ActivityBranch_Type` CHECK ((`Type` in (_utf8mb4'add',_utf8mb4'update',_utf8mb4'delete',_utf8mb4'other')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ActivityCustomer`
--

DROP TABLE IF EXISTS `ActivityCustomer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ActivityCustomer` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `FK_ID` int NOT NULL,
  `Type` varchar(20) NOT NULL,
  `CreatedBy` int DEFAULT NULL,
  `Description` text NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ActivityCustomer_CreatedBy` (`CreatedBy`),
  KEY `IDX_ActivityCustomer_FK_ID` (`FK_ID`,`ID` DESC),
  CONSTRAINT `FK_ActivityCustomer_CreatedBy` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `CK_ActivityCustomer_Type` CHECK ((`Type` in (_utf8mb4'add',_utf8mb4'update',_utf8mb4'delete',_utf8mb4'other')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ActivityEmployee`
--

DROP TABLE IF EXISTS `ActivityEmployee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ActivityEmployee` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `FK_ID` int NOT NULL,
  `Type` varchar(20) NOT NULL,
  `CreatedBy` int DEFAULT NULL,
  `Description` text NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ActivityEmployee_CreatedBy` (`CreatedBy`),
  KEY `IDX_ActivityEmployee_FK_ID` (`FK_ID`,`ID` DESC),
  CONSTRAINT `FK_ActivityEmployee_CreatedBy` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `CK_ActivityEmployee_Type` CHECK ((`Type` in (_utf8mb4'add',_utf8mb4'update',_utf8mb4'delete',_utf8mb4'other')))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ApprovalHistory`
--

DROP TABLE IF EXISTS `ApprovalHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ApprovalHistory` (
  `HistoryID` int NOT NULL AUTO_INCREMENT,
  `DocumentType` varchar(50) NOT NULL,
  `DocumentID` int NOT NULL,
  `FromStatus` varchar(30) DEFAULT NULL,
  `ToStatus` varchar(30) NOT NULL,
  `Action` varchar(30) NOT NULL,
  `PerformedBy` int NOT NULL,
  `Reason` varchar(500) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`HistoryID`),
  KEY `FK_ApprovalHistory_Employee` (`PerformedBy`),
  KEY `IX_ApprovalHistory_Document` (`DocumentType`,`DocumentID`),
  CONSTRAINT `FK_ApprovalHistory_Employee` FOREIGN KEY (`PerformedBy`) REFERENCES `Employee` (`EmployeeID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AuditLog`
--

DROP TABLE IF EXISTS `AuditLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AuditLog` (
  `AuditLogID` int NOT NULL AUTO_INCREMENT,
  `EmployeeID` int NOT NULL,
  `Action` varchar(255) NOT NULL,
  `EntityName` varchar(255) NOT NULL,
  `EntityID` int NOT NULL,
  `OldData` text,
  `NewData` text,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AuditLogID`),
  KEY `FK_AuditLog_Employee` (`EmployeeID`),
  CONSTRAINT `FK_AuditLog_Employee` FOREIGN KEY (`EmployeeID`) REFERENCES `Employee` (`EmployeeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Branch`
--

DROP TABLE IF EXISTS `Branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Branch` (
  `BranchID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Address` text,
  `Phone` varchar(20) DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'active',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`BranchID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Category`
--

DROP TABLE IF EXISTS `Category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Category` (
  `CategoryID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Description` text,
  `ParentID` int DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'active',
  PRIMARY KEY (`CategoryID`),
  KEY `FK_Category_Parent` (`ParentID`),
  CONSTRAINT `FK_Category_Parent` FOREIGN KEY (`ParentID`) REFERENCES `Category` (`CategoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Customer`
--

DROP TABLE IF EXISTS `Customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customer` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `FullName` varchar(255) NOT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `Address` text,
  `DoB` date DEFAULT NULL,
  `Gender` varchar(20) DEFAULT NULL,
  `MembershipTier` varchar(50) DEFAULT NULL,
  `Points` int DEFAULT '0',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CustomerID`),
  UNIQUE KEY `Phone` (`Phone`),
  KEY `IDX_Customer_Phone` (`Phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Employee`
--

DROP TABLE IF EXISTS `Employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Employee` (
  `EmployeeID` int NOT NULL AUTO_INCREMENT,
  `RoleID` int NOT NULL,
  `BranchID` int NOT NULL,
  `FullName` varchar(255) NOT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `PasswordHash` varchar(255) NOT NULL,
  `Status` varchar(20) DEFAULT 'active',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`EmployeeID`),
  UNIQUE KEY `Email` (`Email`),
  KEY `FK_Employee_Role` (`RoleID`),
  KEY `FK_Employee_Branch` (`BranchID`),
  KEY `IDX_Employee_Email` (`Email`),
  CONSTRAINT `FK_Employee_Branch` FOREIGN KEY (`BranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_Employee_Role` FOREIGN KEY (`RoleID`) REFERENCES `Role` (`RoleID`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FinanceTransaction`
--

DROP TABLE IF EXISTS `FinanceTransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FinanceTransaction` (
  `TransactionID` int NOT NULL AUTO_INCREMENT,
  `BranchID` int NOT NULL,
  `EmployeeID` int NOT NULL,
  `TransactionCode` varchar(100) NOT NULL,
  `TransactionDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `TransactionType` varchar(50) NOT NULL,
  `Amount` decimal(18,2) NOT NULL DEFAULT '0.00',
  `ReferenceID` int DEFAULT NULL,
  `ReferenceType` varchar(100) DEFAULT NULL,
  `Note` text,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`TransactionID`),
  UNIQUE KEY `TransactionCode` (`TransactionCode`),
  KEY `FK_FinanceTransaction_Branch` (`BranchID`),
  KEY `FK_FinanceTransaction_Employee` (`EmployeeID`),
  CONSTRAINT `FK_FinanceTransaction_Branch` FOREIGN KEY (`BranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_FinanceTransaction_Employee` FOREIGN KEY (`EmployeeID`) REFERENCES `Employee` (`EmployeeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OrderDetail`
--

DROP TABLE IF EXISTS `OrderDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `OrderDetail` (
  `OrderDetailID` int NOT NULL AUTO_INCREMENT,
  `OrderID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Quantity` int NOT NULL DEFAULT '1',
  `UnitPrice` decimal(18,2) NOT NULL DEFAULT '0.00',
  `Subtotal` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`OrderDetailID`),
  KEY `FK_OrderDetail_Order` (`OrderID`),
  KEY `FK_OrderDetail_Product` (`ProductID`),
  CONSTRAINT `FK_OrderDetail_Order` FOREIGN KEY (`OrderID`) REFERENCES `Orders` (`OrderID`),
  CONSTRAINT `FK_OrderDetail_Product` FOREIGN KEY (`ProductID`) REFERENCES `Product` (`ProductID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Orders` (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `BranchID` int NOT NULL,
  `EmployeeID` int NOT NULL,
  `CustomerID` int DEFAULT NULL,
  `SupplierID` int DEFAULT NULL,
  `OrderCode` varchar(100) NOT NULL,
  `OrderType` varchar(50) NOT NULL,
  `Subtotal` decimal(18,2) DEFAULT '0.00',
  `DiscountAmount` decimal(18,2) DEFAULT '0.00',
  `TotalAmount` decimal(18,2) DEFAULT '0.00',
  `Status` varchar(20) DEFAULT 'pending',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OrderID`),
  UNIQUE KEY `OrderCode` (`OrderCode`),
  KEY `FK_Orders_Branch` (`BranchID`),
  KEY `FK_Orders_Employee` (`EmployeeID`),
  KEY `FK_Orders_Customer` (`CustomerID`),
  KEY `FK_Orders_Supplier` (`SupplierID`),
  KEY `IDX_Orders_OrderCode` (`OrderCode`),
  CONSTRAINT `FK_Orders_Branch` FOREIGN KEY (`BranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_Orders_Customer` FOREIGN KEY (`CustomerID`) REFERENCES `Customer` (`CustomerID`),
  CONSTRAINT `FK_Orders_Employee` FOREIGN KEY (`EmployeeID`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_Orders_Supplier` FOREIGN KEY (`SupplierID`) REFERENCES `Supplier` (`SupplierID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Payments`
--

DROP TABLE IF EXISTS `Payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Payments` (
  `PaymentsID` int NOT NULL AUTO_INCREMENT,
  `OrderID` int NOT NULL,
  `PaymentMethod` varchar(50) NOT NULL,
  `Amount` decimal(18,2) NOT NULL DEFAULT '0.00',
  `PaidAt` datetime DEFAULT NULL,
  `Reference` varchar(255) DEFAULT NULL,
  `Status` varchar(20) DEFAULT 'pending',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PaymentsID`),
  KEY `FK_Payments_Order` (`OrderID`),
  CONSTRAINT `FK_Payments_Order` FOREIGN KEY (`OrderID`) REFERENCES `Orders` (`OrderID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Product`
--

DROP TABLE IF EXISTS `Product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Product` (
  `ProductID` int NOT NULL AUTO_INCREMENT,
  `CategoryID` int NOT NULL,
  `Name` varchar(255) NOT NULL,
  `SKU` varchar(100) NOT NULL,
  `Price` decimal(18,2) DEFAULT '0.00',
  `CostPrice` decimal(18,2) DEFAULT '0.00',
  `StockAlertQty` int DEFAULT '0',
  `StockQuantity` int DEFAULT '0',
  `Status` varchar(20) DEFAULT 'active',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ProductID`),
  UNIQUE KEY `SKU` (`SKU`),
  KEY `FK_Product_Category` (`CategoryID`),
  KEY `IDX_Product_Name` (`Name`),
  KEY `IDX_Product_SKU` (`SKU`),
  CONSTRAINT `FK_Product_Category` FOREIGN KEY (`CategoryID`) REFERENCES `Category` (`CategoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PurchaseOrder`
--

DROP TABLE IF EXISTS `PurchaseOrder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PurchaseOrder` (
  `PurchaseOrderID` int NOT NULL AUTO_INCREMENT,
  `SupplierID` int NOT NULL,
  `BranchID` int NOT NULL,
  `EmployeeID` int NOT NULL,
  `OrderCode` varchar(100) NOT NULL,
  `Status` varchar(30) NOT NULL DEFAULT 'DRAFT',
  `TotalAmount` decimal(18,2) NOT NULL DEFAULT '0.00',
  `Note` text,
  `CreatedBy` int DEFAULT NULL,
  `SubmittedAt` datetime DEFAULT NULL,
  `ApprovedBy` int DEFAULT NULL,
  `ApprovedAt` datetime DEFAULT NULL,
  `RejectedBy` int DEFAULT NULL,
  `RejectedAt` datetime DEFAULT NULL,
  `RejectedReason` varchar(500) DEFAULT NULL,
  `CancelledBy` int DEFAULT NULL,
  `CancelledAt` datetime DEFAULT NULL,
  `CancelledReason` varchar(500) DEFAULT NULL,
  `CompletedAt` datetime DEFAULT NULL,
  `UpdatedAt` datetime DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`PurchaseOrderID`),
  UNIQUE KEY `OrderCode` (`OrderCode`),
  KEY `FK_PurchaseOrder_Employee` (`EmployeeID`),
  KEY `FK_PurchaseOrder_ApprovedBy` (`ApprovedBy`),
  KEY `FK_PurchaseOrder_RejectedBy` (`RejectedBy`),
  KEY `FK_PurchaseOrder_CancelledBy` (`CancelledBy`),
  KEY `IDX_PurchaseOrder_Supplier` (`SupplierID`),
  KEY `IDX_PurchaseOrder_Status` (`Status`),
  KEY `IDX_PurchaseOrder_Branch` (`BranchID`),
  KEY `IDX_PurchaseOrder_CreatedBy` (`CreatedBy`),
  CONSTRAINT `FK_PurchaseOrder_ApprovedBy` FOREIGN KEY (`ApprovedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_PurchaseOrder_Branch` FOREIGN KEY (`BranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_PurchaseOrder_CancelledBy` FOREIGN KEY (`CancelledBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_PurchaseOrder_CreatedBy` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_PurchaseOrder_Employee` FOREIGN KEY (`EmployeeID`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_PurchaseOrder_RejectedBy` FOREIGN KEY (`RejectedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_PurchaseOrder_Supplier` FOREIGN KEY (`SupplierID`) REFERENCES `Supplier` (`SupplierID`),
  CONSTRAINT `CK_PurchaseOrder_Status` CHECK ((`Status` in (_utf8mb4'DRAFT',_utf8mb4'PENDING_APPROVAL',_utf8mb4'APPROVED',_utf8mb4'REJECTED',_utf8mb4'IN_PROGRESS',_utf8mb4'RECEIVING',_utf8mb4'COMPLETED',_utf8mb4'CANCELLED')))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PurchaseOrderDetail`
--

DROP TABLE IF EXISTS `PurchaseOrderDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PurchaseOrderDetail` (
  `PODetailID` int NOT NULL AUTO_INCREMENT,
  `PurchaseOrderID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Quantity` int NOT NULL DEFAULT '1',
  `ReceivedQuantity` int NOT NULL DEFAULT '0',
  `UnitCost` decimal(18,2) NOT NULL DEFAULT '0.00',
  `Subtotal` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`PODetailID`),
  KEY `IDX_PODetail_Order` (`PurchaseOrderID`),
  KEY `IDX_PODetail_Product` (`ProductID`),
  CONSTRAINT `FK_PODetail_Product` FOREIGN KEY (`ProductID`) REFERENCES `Product` (`ProductID`),
  CONSTRAINT `FK_PODetail_PurchaseOrder` FOREIGN KEY (`PurchaseOrderID`) REFERENCES `PurchaseOrder` (`PurchaseOrderID`) ON DELETE CASCADE,
  CONSTRAINT `CK_PODetail_Quantity` CHECK ((`Quantity` > 0)),
  CONSTRAINT `CK_PODetail_ReceivedQty` CHECK (((`ReceivedQuantity` >= 0) and (`ReceivedQuantity` <= `Quantity`))),
  CONSTRAINT `CK_PODetail_UnitCost` CHECK ((`UnitCost` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PurchaseOrderHistory`
--

DROP TABLE IF EXISTS `PurchaseOrderHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PurchaseOrderHistory` (
  `HistoryID` int NOT NULL AUTO_INCREMENT,
  `PurchaseOrderID` int NOT NULL,
  `FromStatus` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ToStatus` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Action` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `PerformedBy` int NOT NULL,
  `Reason` text COLLATE utf8mb4_unicode_ci,
  `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`HistoryID`),
  KEY `FK_POHistory_Employee` (`PerformedBy`),
  KEY `IX_POHistory_OrderID` (`PurchaseOrderID`),
  KEY `IX_POHistory_CreatedAt` (`CreatedAt`),
  CONSTRAINT `FK_POHistory_Employee` FOREIGN KEY (`PerformedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_POHistory_PurchaseOrder` FOREIGN KEY (`PurchaseOrderID`) REFERENCES `PurchaseOrder` (`PurchaseOrderID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Role`
--

DROP TABLE IF EXISTS `Role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Role` (
  `RoleID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Description` text,
  PRIMARY KEY (`RoleID`),
  UNIQUE KEY `Name` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StockAdjustment`
--

DROP TABLE IF EXISTS `StockAdjustment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StockAdjustment` (
  `AdjustmentID` int NOT NULL AUTO_INCREMENT,
  `AdjustmentCode` varchar(50) NOT NULL,
  `BranchID` int NOT NULL,
  `Status` varchar(30) NOT NULL DEFAULT 'DRAFT',
  `Reason` varchar(500) NOT NULL,
  `Note` varchar(500) DEFAULT NULL,
  `TotalVarianceValue` decimal(18,2) DEFAULT '0.00',
  `CreatedBy` int NOT NULL,
  `SubmittedAt` datetime DEFAULT NULL,
  `ApprovedBy` int DEFAULT NULL,
  `ApprovedAt` datetime DEFAULT NULL,
  `RejectedBy` int DEFAULT NULL,
  `RejectedAt` datetime DEFAULT NULL,
  `RejectedReason` varchar(500) DEFAULT NULL,
  `CancelledBy` int DEFAULT NULL,
  `CancelledAt` datetime DEFAULT NULL,
  `CancelledReason` varchar(500) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`AdjustmentID`),
  UNIQUE KEY `UQ_StockAdjustment_Code` (`AdjustmentCode`),
  KEY `FK_StockAdjustment_CreatedBy` (`CreatedBy`),
  KEY `FK_StockAdjustment_ApprovedBy` (`ApprovedBy`),
  KEY `FK_StockAdjustment_RejectedBy` (`RejectedBy`),
  KEY `FK_StockAdjustment_CancelledBy` (`CancelledBy`),
  KEY `IDX_StockAdjustment_Branch` (`BranchID`),
  KEY `IDX_StockAdjustment_Status` (`Status`),
  CONSTRAINT `FK_StockAdjustment_ApprovedBy` FOREIGN KEY (`ApprovedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockAdjustment_Branch` FOREIGN KEY (`BranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_StockAdjustment_CancelledBy` FOREIGN KEY (`CancelledBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockAdjustment_CreatedBy` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockAdjustment_RejectedBy` FOREIGN KEY (`RejectedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `CK_StockAdjustment_Status` CHECK ((`Status` in (_utf8mb4'DRAFT',_utf8mb4'PENDING_APPROVAL',_utf8mb4'APPROVED',_utf8mb4'REJECTED',_utf8mb4'CANCELLED')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StockAdjustmentDetail`
--

DROP TABLE IF EXISTS `StockAdjustmentDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StockAdjustmentDetail` (
  `AdjustmentDetailID` int NOT NULL AUTO_INCREMENT,
  `AdjustmentID` int NOT NULL,
  `ProductID` int NOT NULL,
  `SystemQuantity` int NOT NULL,
  `ActualQuantity` int NOT NULL,
  `Variance` int NOT NULL,
  `VarianceValue` decimal(18,2) NOT NULL,
  `Reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`AdjustmentDetailID`),
  UNIQUE KEY `UQ_StockAdjustmentDetail_AdjustmentProduct` (`AdjustmentID`,`ProductID`),
  KEY `IDX_StockAdjustmentDetail_Product` (`ProductID`),
  CONSTRAINT `FK_StockAdjustmentDetail_Adjustment` FOREIGN KEY (`AdjustmentID`) REFERENCES `StockAdjustment` (`AdjustmentID`) ON DELETE CASCADE,
  CONSTRAINT `FK_StockAdjustmentDetail_Product` FOREIGN KEY (`ProductID`) REFERENCES `Product` (`ProductID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StockTransfer`
--

DROP TABLE IF EXISTS `StockTransfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StockTransfer` (
  `TransferID` int NOT NULL AUTO_INCREMENT,
  `TransferCode` varchar(50) NOT NULL,
  `FromBranchID` int NOT NULL,
  `ToBranchID` int NOT NULL,
  `Status` varchar(30) NOT NULL DEFAULT 'DRAFT',
  `Note` varchar(500) DEFAULT NULL,
  `TotalItems` int NOT NULL DEFAULT '0',
  `TotalQuantity` int NOT NULL DEFAULT '0',
  `CreatedBy` int NOT NULL,
  `SubmittedAt` datetime DEFAULT NULL,
  `ApprovedBy` int DEFAULT NULL,
  `ApprovedAt` datetime DEFAULT NULL,
  `RejectedBy` int DEFAULT NULL,
  `RejectedAt` datetime DEFAULT NULL,
  `RejectedReason` varchar(500) DEFAULT NULL,
  `ShippedBy` int DEFAULT NULL,
  `ShippedAt` datetime DEFAULT NULL,
  `ReceivedBy` int DEFAULT NULL,
  `ReceivedAt` datetime DEFAULT NULL,
  `CancelledBy` int DEFAULT NULL,
  `CancelledAt` datetime DEFAULT NULL,
  `CancelledReason` varchar(500) DEFAULT NULL,
  `CreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`TransferID`),
  UNIQUE KEY `UQ_StockTransfer_Code` (`TransferCode`),
  KEY `FK_StockTransfer_CreatedBy` (`CreatedBy`),
  KEY `FK_StockTransfer_ApprovedBy` (`ApprovedBy`),
  KEY `FK_StockTransfer_RejectedBy` (`RejectedBy`),
  KEY `FK_StockTransfer_ShippedBy` (`ShippedBy`),
  KEY `FK_StockTransfer_ReceivedBy` (`ReceivedBy`),
  KEY `FK_StockTransfer_CancelledBy` (`CancelledBy`),
  KEY `IDX_StockTransfer_FromBranch` (`FromBranchID`),
  KEY `IDX_StockTransfer_ToBranch` (`ToBranchID`),
  KEY `IDX_StockTransfer_Status` (`Status`),
  CONSTRAINT `FK_StockTransfer_ApprovedBy` FOREIGN KEY (`ApprovedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockTransfer_CancelledBy` FOREIGN KEY (`CancelledBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockTransfer_CreatedBy` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockTransfer_FromBranch` FOREIGN KEY (`FromBranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_StockTransfer_ReceivedBy` FOREIGN KEY (`ReceivedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockTransfer_RejectedBy` FOREIGN KEY (`RejectedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockTransfer_ShippedBy` FOREIGN KEY (`ShippedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_StockTransfer_ToBranch` FOREIGN KEY (`ToBranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `CK_StockTransfer_DifferentBranches` CHECK ((`FromBranchID` <> `ToBranchID`)),
  CONSTRAINT `CK_StockTransfer_Status` CHECK ((`Status` in (_utf8mb4'DRAFT',_utf8mb4'PENDING_APPROVAL',_utf8mb4'APPROVED',_utf8mb4'REJECTED',_utf8mb4'IN_TRANSIT',_utf8mb4'COMPLETED',_utf8mb4'CANCELLED')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StockTransferDetail`
--

DROP TABLE IF EXISTS `StockTransferDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StockTransferDetail` (
  `TransferDetailID` int NOT NULL AUTO_INCREMENT,
  `TransferID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Quantity` int NOT NULL,
  `ReceivedQuantity` int NOT NULL DEFAULT '0',
  `Note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`TransferDetailID`),
  UNIQUE KEY `UQ_StockTransferDetail_TransferProduct` (`TransferID`,`ProductID`),
  KEY `IDX_StockTransferDetail_Product` (`ProductID`),
  CONSTRAINT `FK_StockTransferDetail_Product` FOREIGN KEY (`ProductID`) REFERENCES `Product` (`ProductID`),
  CONSTRAINT `FK_StockTransferDetail_Transfer` FOREIGN KEY (`TransferID`) REFERENCES `StockTransfer` (`TransferID`) ON DELETE CASCADE,
  CONSTRAINT `CK_StockTransferDetail_QtyPositive` CHECK ((`Quantity` > 0)),
  CONSTRAINT `CK_StockTransferDetail_ReceivedNotExceed` CHECK ((`ReceivedQuantity` <= `Quantity`)),
  CONSTRAINT `CK_StockTransferDetail_ReceivedQtyNonNeg` CHECK ((`ReceivedQuantity` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Supplier`
--

DROP TABLE IF EXISTS `Supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supplier` (
  `SupplierID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `Address` text,
  `Status` varchar(20) DEFAULT 'active',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`SupplierID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `V_PurchaseOrderList`
--

DROP TABLE IF EXISTS `V_PurchaseOrderList`;
/*!50001 DROP VIEW IF EXISTS `V_PurchaseOrderList`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `V_PurchaseOrderList` AS SELECT 
 1 AS `PurchaseOrderID`,
 1 AS `OrderCode`,
 1 AS `Status`,
 1 AS `TotalAmount`,
 1 AS `Note`,
 1 AS `CreatedAt`,
 1 AS `SubmittedAt`,
 1 AS `ApprovedAt`,
 1 AS `RejectedAt`,
 1 AS `RejectedReason`,
 1 AS `CancelledAt`,
 1 AS `CancelledReason`,
 1 AS `CompletedAt`,
 1 AS `UpdatedAt`,
 1 AS `SupplierID`,
 1 AS `SupplierName`,
 1 AS `BranchID`,
 1 AS `BranchName`,
 1 AS `EmployeeID`,
 1 AS `EmployeeName`,
 1 AS `CreatedBy`,
 1 AS `CreatedByName`,
 1 AS `ApprovedBy`,
 1 AS `ApprovedByName`,
 1 AS `RejectedBy`,
 1 AS `RejectedByName`,
 1 AS `CancelledBy`,
 1 AS `CancelledByName`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `V_StockAdjustmentList`
--

DROP TABLE IF EXISTS `V_StockAdjustmentList`;
/*!50001 DROP VIEW IF EXISTS `V_StockAdjustmentList`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `V_StockAdjustmentList` AS SELECT 
 1 AS `AdjustmentID`,
 1 AS `AdjustmentCode`,
 1 AS `BranchID`,
 1 AS `BranchName`,
 1 AS `Status`,
 1 AS `Reason`,
 1 AS `Note`,
 1 AS `TotalVarianceValue`,
 1 AS `CreatedBy`,
 1 AS `CreatedByName`,
 1 AS `SubmittedAt`,
 1 AS `ApprovedBy`,
 1 AS `ApprovedByName`,
 1 AS `ApprovedAt`,
 1 AS `RejectedBy`,
 1 AS `RejectedByName`,
 1 AS `RejectedAt`,
 1 AS `RejectedReason`,
 1 AS `CancelledBy`,
 1 AS `CancelledByName`,
 1 AS `CancelledAt`,
 1 AS `CancelledReason`,
 1 AS `CreatedAt`,
 1 AS `UpdatedAt`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `V_StockTransferList`
--

DROP TABLE IF EXISTS `V_StockTransferList`;
/*!50001 DROP VIEW IF EXISTS `V_StockTransferList`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `V_StockTransferList` AS SELECT 
 1 AS `TransferID`,
 1 AS `TransferCode`,
 1 AS `Status`,
 1 AS `Note`,
 1 AS `TotalItems`,
 1 AS `TotalQuantity`,
 1 AS `FromBranchID`,
 1 AS `FromBranchName`,
 1 AS `ToBranchID`,
 1 AS `ToBranchName`,
 1 AS `CreatedBy`,
 1 AS `CreatedByName`,
 1 AS `SubmittedAt`,
 1 AS `ApprovedBy`,
 1 AS `ApprovedByName`,
 1 AS `ApprovedAt`,
 1 AS `RejectedBy`,
 1 AS `RejectedByName`,
 1 AS `RejectedAt`,
 1 AS `RejectedReason`,
 1 AS `ShippedBy`,
 1 AS `ShippedByName`,
 1 AS `ShippedAt`,
 1 AS `ReceivedBy`,
 1 AS `ReceivedByName`,
 1 AS `ReceivedAt`,
 1 AS `CancelledBy`,
 1 AS `CancelledByName`,
 1 AS `CancelledAt`,
 1 AS `CancelledReason`,
 1 AS `CreatedAt`,
 1 AS `UpdatedAt`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `Warehouse`
--

DROP TABLE IF EXISTS `Warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Warehouse` (
  `WarehouseID` int NOT NULL AUTO_INCREMENT,
  `BranchID` int NOT NULL,
  `EmployeeID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Address` text,
  `Status` varchar(20) DEFAULT 'active',
  `Quantity` int DEFAULT '0',
  `AvailableQuantity` int DEFAULT '0',
  `MinQuantity` int DEFAULT '0',
  `MaxQuantity` int DEFAULT '0',
  `UpdatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`WarehouseID`),
  KEY `FK_Warehouse_Branch` (`BranchID`),
  KEY `FK_Warehouse_Employee` (`EmployeeID`),
  KEY `FK_Warehouse_Product` (`ProductID`),
  CONSTRAINT `FK_Warehouse_Branch` FOREIGN KEY (`BranchID`) REFERENCES `Branch` (`BranchID`),
  CONSTRAINT `FK_Warehouse_Employee` FOREIGN KEY (`EmployeeID`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_Warehouse_Product` FOREIGN KEY (`ProductID`) REFERENCES `Product` (`ProductID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WarehouseTransaction`
--

DROP TABLE IF EXISTS `WarehouseTransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WarehouseTransaction` (
  `WarehouseTransactionID` int NOT NULL AUTO_INCREMENT,
  `WarehouseID` int NOT NULL,
  `ProductID` int NOT NULL,
  `BeforeQuantity` int DEFAULT '0',
  `Quantity` int NOT NULL,
  `TransactionType` varchar(50) NOT NULL,
  `AfterQuantity` int DEFAULT '0',
  `UnitCost` decimal(18,2) DEFAULT '0.00',
  `ReferenceType` varchar(100) DEFAULT NULL,
  `ReferenceID` int DEFAULT NULL,
  `CreatedBy` int NOT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`WarehouseTransactionID`),
  KEY `FK_WarehouseTransaction_Warehouse` (`WarehouseID`),
  KEY `FK_WarehouseTransaction_Product` (`ProductID`),
  KEY `FK_WarehouseTransaction_Employee` (`CreatedBy`),
  CONSTRAINT `FK_WarehouseTransaction_Employee` FOREIGN KEY (`CreatedBy`) REFERENCES `Employee` (`EmployeeID`),
  CONSTRAINT `FK_WarehouseTransaction_Product` FOREIGN KEY (`ProductID`) REFERENCES `Product` (`ProductID`),
  CONSTRAINT `FK_WarehouseTransaction_Warehouse` FOREIGN KEY (`WarehouseID`) REFERENCES `Warehouse` (`WarehouseID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `_backup_charset_recovery_260602`
--

DROP TABLE IF EXISTS `_backup_charset_recovery_260602`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `_backup_charset_recovery_260602` (
  `table_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `col_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id` int DEFAULT NULL,
  `original_value` text COLLATE utf8mb4_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'DBFinora'
--

--
-- Final view structure for view `V_PurchaseOrderList`
--

/*!50001 DROP VIEW IF EXISTS `V_PurchaseOrderList`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_PurchaseOrderList` AS select `po`.`PurchaseOrderID` AS `PurchaseOrderID`,`po`.`OrderCode` AS `OrderCode`,`po`.`Status` AS `Status`,`po`.`TotalAmount` AS `TotalAmount`,`po`.`Note` AS `Note`,`po`.`CreatedAt` AS `CreatedAt`,`po`.`SubmittedAt` AS `SubmittedAt`,`po`.`ApprovedAt` AS `ApprovedAt`,`po`.`RejectedAt` AS `RejectedAt`,`po`.`RejectedReason` AS `RejectedReason`,`po`.`CancelledAt` AS `CancelledAt`,`po`.`CancelledReason` AS `CancelledReason`,`po`.`CompletedAt` AS `CompletedAt`,`po`.`UpdatedAt` AS `UpdatedAt`,`po`.`SupplierID` AS `SupplierID`,`s`.`Name` AS `SupplierName`,`po`.`BranchID` AS `BranchID`,`b`.`Name` AS `BranchName`,`po`.`EmployeeID` AS `EmployeeID`,`e`.`FullName` AS `EmployeeName`,`po`.`CreatedBy` AS `CreatedBy`,`cby`.`FullName` AS `CreatedByName`,`po`.`ApprovedBy` AS `ApprovedBy`,`aby`.`FullName` AS `ApprovedByName`,`po`.`RejectedBy` AS `RejectedBy`,`rby`.`FullName` AS `RejectedByName`,`po`.`CancelledBy` AS `CancelledBy`,`xby`.`FullName` AS `CancelledByName` from (((((((`PurchaseOrder` `po` left join `Supplier` `s` on((`s`.`SupplierID` = `po`.`SupplierID`))) left join `Branch` `b` on((`b`.`BranchID` = `po`.`BranchID`))) left join `Employee` `e` on((`e`.`EmployeeID` = `po`.`EmployeeID`))) left join `Employee` `cby` on((`cby`.`EmployeeID` = `po`.`CreatedBy`))) left join `Employee` `aby` on((`aby`.`EmployeeID` = `po`.`ApprovedBy`))) left join `Employee` `rby` on((`rby`.`EmployeeID` = `po`.`RejectedBy`))) left join `Employee` `xby` on((`xby`.`EmployeeID` = `po`.`CancelledBy`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_StockAdjustmentList`
--

/*!50001 DROP VIEW IF EXISTS `V_StockAdjustmentList`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_StockAdjustmentList` AS select `sa`.`AdjustmentID` AS `AdjustmentID`,`sa`.`AdjustmentCode` AS `AdjustmentCode`,`sa`.`BranchID` AS `BranchID`,`b`.`Name` AS `BranchName`,`sa`.`Status` AS `Status`,`sa`.`Reason` AS `Reason`,`sa`.`Note` AS `Note`,`sa`.`TotalVarianceValue` AS `TotalVarianceValue`,`sa`.`CreatedBy` AS `CreatedBy`,`ec`.`FullName` AS `CreatedByName`,`sa`.`SubmittedAt` AS `SubmittedAt`,`sa`.`ApprovedBy` AS `ApprovedBy`,`ea`.`FullName` AS `ApprovedByName`,`sa`.`ApprovedAt` AS `ApprovedAt`,`sa`.`RejectedBy` AS `RejectedBy`,`er`.`FullName` AS `RejectedByName`,`sa`.`RejectedAt` AS `RejectedAt`,`sa`.`RejectedReason` AS `RejectedReason`,`sa`.`CancelledBy` AS `CancelledBy`,`ex`.`FullName` AS `CancelledByName`,`sa`.`CancelledAt` AS `CancelledAt`,`sa`.`CancelledReason` AS `CancelledReason`,`sa`.`CreatedAt` AS `CreatedAt`,`sa`.`UpdatedAt` AS `UpdatedAt` from (((((`StockAdjustment` `sa` join `Branch` `b` on((`sa`.`BranchID` = `b`.`BranchID`))) join `Employee` `ec` on((`sa`.`CreatedBy` = `ec`.`EmployeeID`))) left join `Employee` `ea` on((`sa`.`ApprovedBy` = `ea`.`EmployeeID`))) left join `Employee` `er` on((`sa`.`RejectedBy` = `er`.`EmployeeID`))) left join `Employee` `ex` on((`sa`.`CancelledBy` = `ex`.`EmployeeID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_StockTransferList`
--

/*!50001 DROP VIEW IF EXISTS `V_StockTransferList`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_StockTransferList` AS select `st`.`TransferID` AS `TransferID`,`st`.`TransferCode` AS `TransferCode`,`st`.`Status` AS `Status`,`st`.`Note` AS `Note`,`st`.`TotalItems` AS `TotalItems`,`st`.`TotalQuantity` AS `TotalQuantity`,`st`.`FromBranchID` AS `FromBranchID`,`fb`.`Name` AS `FromBranchName`,`st`.`ToBranchID` AS `ToBranchID`,`tb`.`Name` AS `ToBranchName`,`st`.`CreatedBy` AS `CreatedBy`,`creator`.`FullName` AS `CreatedByName`,`st`.`SubmittedAt` AS `SubmittedAt`,`st`.`ApprovedBy` AS `ApprovedBy`,`approver`.`FullName` AS `ApprovedByName`,`st`.`ApprovedAt` AS `ApprovedAt`,`st`.`RejectedBy` AS `RejectedBy`,`rejecter`.`FullName` AS `RejectedByName`,`st`.`RejectedAt` AS `RejectedAt`,`st`.`RejectedReason` AS `RejectedReason`,`st`.`ShippedBy` AS `ShippedBy`,`shipper`.`FullName` AS `ShippedByName`,`st`.`ShippedAt` AS `ShippedAt`,`st`.`ReceivedBy` AS `ReceivedBy`,`receiver`.`FullName` AS `ReceivedByName`,`st`.`ReceivedAt` AS `ReceivedAt`,`st`.`CancelledBy` AS `CancelledBy`,`canceller`.`FullName` AS `CancelledByName`,`st`.`CancelledAt` AS `CancelledAt`,`st`.`CancelledReason` AS `CancelledReason`,`st`.`CreatedAt` AS `CreatedAt`,`st`.`UpdatedAt` AS `UpdatedAt` from ((((((((`StockTransfer` `st` join `Branch` `fb` on((`fb`.`BranchID` = `st`.`FromBranchID`))) join `Branch` `tb` on((`tb`.`BranchID` = `st`.`ToBranchID`))) join `Employee` `creator` on((`creator`.`EmployeeID` = `st`.`CreatedBy`))) left join `Employee` `approver` on((`approver`.`EmployeeID` = `st`.`ApprovedBy`))) left join `Employee` `rejecter` on((`rejecter`.`EmployeeID` = `st`.`RejectedBy`))) left join `Employee` `shipper` on((`shipper`.`EmployeeID` = `st`.`ShippedBy`))) left join `Employee` `receiver` on((`receiver`.`EmployeeID` = `st`.`ReceivedBy`))) left join `Employee` `canceller` on((`canceller`.`EmployeeID` = `st`.`CancelledBy`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-04  5:18:05
