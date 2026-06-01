# Approval Workflow Specification - KiotRetail

> Cap nhat: 2026-05-31
> Nguon: RDS-main.txt (Section 5: Non-UI Functions) + Authorization Matrix (Section 2.3)

---

## 1. Tong Quan

### Muc dich

Tai lieu nay dinh nghia day du workflow phe duyet (approval flow) cho tung loai chung tu trong he thong KiotRetail. Moi chung tu co vong doi (lifecycle) voi cac trang thai, dieu kien chuyen trang thai, va phan quyen cu the.

### Nguyen tac chung

1. **Separation of Duties**: Nguoi tao chung tu KHONG duoc tu phe duyet chung tu do (tru POS direct sale)
2. **Least Privilege**: Moi role chi co quyen toi thieu can thiet
3. **Audit Trail**: Moi thay doi trang thai phai ghi log (who, when, from_status, to_status, reason)
4. **Monetary Threshold**: Don gia tri cao can cap phe duyet cao hon
5. **Draft Safety**: Chung tu o trang thai DRAFT co the sua/xoa tu do; sau khi submit thi khong sua duoc nua
6. **Rejection = Back to Draft**: Khi bi tu choi, chung tu quay ve DRAFT de sua lai (khong bi xoa)
7. **Cancellation Audit**: Huy chung tu phai co ly do, va chi huy duoc truoc khi COMPLETED

### Phan cap phe duyet (Approval Hierarchy)

| Cap | Role | Pham vi phe duyet |
|-----|------|-------------------|
| Cap 0 | SALES_STAFF / WAREHOUSE_STAFF | Tao chung tu, khong duyet |
| Cap 1 | STORE_MANAGER | Duyet chung tu thuong (< 50 trieu VND) |
| Cap 2 | OWNER | Duyet chung tu gia tri cao (>= 50 trieu VND) |
| N/A | ADMIN | Khong tham gia phe duyet nghiep vu (chi technical) |

### Nguong gia tri (Monetary Thresholds)

| Nguong | Ap dung cho | Yeu cau |
|--------|-------------|---------|
| < 10 trieu VND | Purchase Order, Payment Voucher | Manager duyet |
| 10-50 trieu VND | Purchase Order, Payment Voucher | Manager duyet |
| >= 50 trieu VND | Purchase Order, Payment Voucher | Owner duyet |
| Bat ky | Stock Transfer, Stock Adjustment | Manager duyet (khong phan biet gia tri) |
| Bat ky | POS Sales Order | Khong can duyet (direct sale) |
| Bat ky | Credit Sales Order | Manager duyet |

---

## 2. Quy Tac Chung (Cross-Module Rules)

### 2.1 Status Enum Chuan

Tat ca chung tu trong he thong su dung tap trang thai sau (co the khong dung het):

`
DRAFT -> PENDING_APPROVAL -> APPROVED -> IN_PROGRESS -> COMPLETED
                          -> REJECTED (-> quay ve DRAFT)
     -> CANCELLED (tu DRAFT hoac PENDING_APPROVAL)
`

### 2.2 Quy tac chuyen trang thai

- **DRAFT**: Chung tu moi tao, co the edit/delete tu do
- **PENDING_APPROVAL**: Da submit, cho duyet. KHONG the edit nua
- **APPROVED**: Da duyet, bat dau thuc hien. KHONG the edit
- **REJECTED**: Bi tu choi, quay ve DRAFT de sua. Phai co reject reason
- **IN_PROGRESS**: Dang thuc hien (vd: dang van chuyen, dang nhan hang)
- **COMPLETED**: Hoan tat, khong the thay doi. Final state
- **CANCELLED**: Bi huy. Phai co cancel reason. Final state

### 2.3 Notification Rules

| Su kien | Gui cho ai |
|---------|-----------|
| Chung tu duoc submit | Nguoi co quyen duyet (Manager/Owner) |
| Chung tu duoc duyet | Nguoi tao |
| Chung tu bi tu choi | Nguoi tao (kem ly do) |
| Chung tu bi huy | Nguoi tao + nguoi duyet (neu da duyet) |
| Low stock alert | Manager + Warehouse Staff |

### 2.4 Rejection Handling

1. Khi reject, bat buoc nhap ejectReason (min 10 ky tu)
2. Chung tu quay ve DRAFT, nguoi tao co the sua roi submit lai
3. Ghi log: reject count, reject history
4. Sau 3 lan reject lien tiep -> flag cho Owner review

### 2.5 Cancellation Rules

- Chi cancel duoc khi status la DRAFT hoac PENDING_APPROVAL
- Sau khi APPROVED: chi Owner moi co quyen cancel (voi ly do)
- Sau khi COMPLETED: KHONG the cancel (phai tao chung tu doi/tra)
- Cancel phai co cancelReason
- Cancel APPROVED document phai reverse side effects (tra lai ton kho, huy reservation)

---

## 3. Module: Purchase (Nhap Hang)

### 3.1 Purchase Order (Don Nhap Hang)

**Mo ta**: Phieu yeu cau nhap hang tu nha cung cap.

**Status Flow:**
`
DRAFT --> PENDING_APPROVAL --> APPROVED --> RECEIVING --> COMPLETED
  |              |                |
  v              v                v
CANCELLED    REJECTED         CANCELLED (Owner only)
             (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | STORE_MANAGER, WAREHOUSE_STAFF | - | Generate PO code |
| DRAFT | DRAFT | Edit | Creator | status = DRAFT | - |
| DRAFT | PENDING_APPROVAL | Submit | Creator | Co it nhat 1 line item, supplier da chon | Lock editing |
| PENDING_APPROVAL | APPROVED | Approve | STORE_MANAGER (< 50M), OWNER (>= 50M) | Approver != Creator | Reserve budget |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER, OWNER | Phai co rejectReason | Notify creator |
| APPROVED | RECEIVING | Start Receive | WAREHOUSE_STAFF | Co hang ve thuc te | - |
| RECEIVING | COMPLETED | Complete Receive | WAREHOUSE_STAFF | Tat ca line items da nhan du | Update Product.stockQuantity += qty |
| APPROVED | CANCELLED | Cancel | OWNER | Phai co cancelReason | Release budget reservation |
| DRAFT | CANCELLED | Cancel | Creator, STORE_MANAGER | - | - |
| PENDING_APPROVAL | CANCELLED | Cancel | Creator, STORE_MANAGER, OWNER | - | - |

**Business Rules:**
- Partial receiving: Cho phep nhan 1 phan (qty received < qty ordered). Status = RECEIVING cho den khi nhan het hoac dong PO
- Auto-close: Neu sau 30 ngay APPROVED ma chua nhan hang -> canh bao Manager
- Supplier bill: Sau khi COMPLETED, tu dong tao Supplier Invoice (cong no NCC)

**Edge Cases:**
- PO co 5 items, nhan duoc 3 -> status RECEIVING, ghi nhan partial
- Supplier gui du hang nhung 2 item bi loi -> nhan 3, tao Return Order cho 2
- PO da APPROVED nhung supplier thong bao het hang -> Owner cancel + tao PO moi

---

### 3.2 Inventory Receipt Voucher (Phieu Nhap Kho)

**Mo ta**: Xac nhan hang da nhan vao kho vat ly. Lien ket voi Purchase Order.

**Status Flow:**
`
DRAFT --> CONFIRMED --> COMPLETED
  |
  v
CANCELLED
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | WAREHOUSE_STAFF | Phai lien ket PO (status APPROVED/RECEIVING) | - |
| DRAFT | CONFIRMED | Confirm | WAREHOUSE_STAFF | Nhap so luong thuc nhan | Update PO received qty |
| CONFIRMED | COMPLETED | Complete | STORE_MANAGER | Kiem tra so luong khop | Update Product.stockQuantity |
| DRAFT | CANCELLED | Cancel | WAREHOUSE_STAFF, STORE_MANAGER | - | - |

**Note**: Phieu nhap kho don gian hon PO vi no la buoc thuc hien cua PO da duyet.

---

### 3.3 Supplier Return Order (Phieu Tra Hang NCC)

**Status Flow:**
`
DRAFT --> PENDING_APPROVAL --> APPROVED --> COMPLETED
  |              |
  v              v
CANCELLED    REJECTED (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | STORE_MANAGER, WAREHOUSE_STAFF | Phai co PO goc | - |
| DRAFT | PENDING_APPROVAL | Submit | Creator | Co line items + ly do tra | - |
| PENDING_APPROVAL | APPROVED | Approve | STORE_MANAGER, OWNER | - | - |
| APPROVED | COMPLETED | Complete | WAREHOUSE_STAFF | Hang da gui tra NCC | Giam stockQuantity, giam cong no NCC |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER, OWNER | Phai co reason | -> DRAFT |

---

## 4. Module: Sales (Ban Hang)

### 4.1 POS Direct Sale (Ban Truc Tiep Tai Quay)

**Mo ta**: Don ban hang tao truc tiep tai POS. KHONG can phe duyet.

**Status Flow:**
`
(POS checkout) --> COMPLETED
                      |
                      v
                  CANCELLED (Manager only, trong ngay)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | COMPLETED | Checkout | SALES_STAFF, STORE_MANAGER | Payment received | Giam stockQuantity, tao Invoice |
| COMPLETED | CANCELLED | Cancel | STORE_MANAGER | Trong ngay, co cancelReason | Hoan stockQuantity, void Invoice |

**Business Rules:**
- POS sale KHONG co draft/approval vi la giao dich truc tiep
- Cancel chi duoc trong ngay (truoc khi dong ca)
- Sau khi dong ca -> phai dung Return Order de xu ly

---

### 4.2 Credit Sales Order (Don Ban No)

**Mo ta**: Don ban hang cho khach tra sau (cong no). CAN phe duyet vi co rui ro tai chinh.

**Status Flow:**
`
DRAFT --> PENDING_APPROVAL --> APPROVED --> DELIVERING --> COMPLETED
  |              |                |
  v              v                v
CANCELLED    REJECTED         CANCELLED (Owner only)
             (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | SALES_STAFF, STORE_MANAGER | Phai co customer (khong ban no cho khach vang lai) | - |
| DRAFT | PENDING_APPROVAL | Submit | Creator | Customer khong vuot han muc no | - |
| PENDING_APPROVAL | APPROVED | Approve | STORE_MANAGER | Kiem tra credit limit cua KH | Reserve stock |
| APPROVED | DELIVERING | Deliver | SALES_STAFF, WAREHOUSE_STAFF | - | Giam stockQuantity |
| DELIVERING | COMPLETED | Complete | STORE_MANAGER | Xac nhan KH da nhan hang | Tao CustomerDebt record |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER | KH vuot han muc no, hoac ly do khac | -> DRAFT |

**Business Rules:**
- Han muc no toi da cua KH: Silver = 5M, Gold = 20M, VIP = 50M
- Neu KH da no >= han muc -> khong cho tao don ban no moi
- Sau 30 ngay chua thanh toan -> canh bao Manager
- Sau 60 ngay -> flag KH, khong cho mua no tiep

---

### 4.3 Order Return (Tra Hang)

**Status Flow:**
`
DRAFT --> PENDING_APPROVAL --> APPROVED --> COMPLETED (refund processed)
  |              |
  v              v
CANCELLED    REJECTED (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | SALES_STAFF, STORE_MANAGER | Phai co Invoice goc, trong thoi han doi tra | - |
| DRAFT | PENDING_APPROVAL | Submit | Creator | Co line items tra + ly do | - |
| PENDING_APPROVAL | APPROVED | Approve | STORE_MANAGER | Kiem tra dieu kien doi tra (thoi han, tinh trang hang) | - |
| APPROVED | COMPLETED | Complete + Refund | STORE_MANAGER | - | Tang stockQuantity, tao Refund voucher |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER | Hang khong du dieu kien tra | -> DRAFT |

**Business Rules:**
- Thoi han doi tra: 7 ngay ke tu ngay mua (configurable)
- Hang da mo seal/su dung -> chi doi, khong tra
- Refund method: cung phuong thuc thanh toan goc (tien mat -> tra tien mat)

---

## 5. Module: Inventory (Ton Kho)

### 5.1 Stock Transfer (Chuyen Kho)

**Mo ta**: Chuyen hang giua cac chi nhanh/kho.

**Status Flow (tu RDS line 1395):**
`
DRAFT --> PENDING_APPROVAL --> APPROVED --> IN_TRANSIT --> COMPLETED
  |              |                              |
  v              v                              v
CANCELLED    REJECTED (-> DRAFT)          CANCELLED (Owner, hang mat)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | WAREHOUSE_STAFF, STORE_MANAGER | Chon from/to branch, products | Generate transfer code |
| DRAFT | DRAFT | Edit | Creator | status = DRAFT | - |
| DRAFT | PENDING_APPROVAL | Submit | Creator | Co it nhat 1 item, from != to branch | - |
| PENDING_APPROVAL | APPROVED | Approve | STORE_MANAGER (of source branch) | Stock du de chuyen | - |
| APPROVED | IN_TRANSIT | Ship | WAREHOUSE_STAFF (source) | Xac nhan da gui hang | Giam stock tai source branch |
| IN_TRANSIT | COMPLETED | Receive | WAREHOUSE_STAFF (destination) | Xac nhan da nhan hang | Tang stock tai destination branch |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER | Stock khong du, hoac ly do khac | -> DRAFT |
| DRAFT/PENDING | CANCELLED | Cancel | Creator, STORE_MANAGER | - | - |
| IN_TRANSIT | CANCELLED | Cancel | OWNER | Hang mat/hu tren duong | Ghi nhan loss, khong tang stock destination |

**Business Rules:**
- Chi Manager cua chi nhanh NGUON moi duyet (vi ho chiu trach nhiem stock)
- Warehouse Staff chi nhanh DICH xac nhan nhan hang
- Neu nhan thieu so voi gui -> ghi nhan chenh lech, bao Manager

---

### 5.2 Stock Adjustment (Kiem Ke / Dieu Chinh Ton Kho)

**Mo ta**: Dieu chinh so luong ton kho sau kiem ke vat ly.

**Status Flow (tu RDS line 1411-1423):**
`
DRAFT --> PENDING_APPROVAL --> APPROVED (= FINALIZED, apply to stock)
  |              |
  v              v
CANCELLED    REJECTED (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | WAREHOUSE_STAFF | Chon branch, bat dau kiem ke | Generate adjustment code |
| DRAFT | DRAFT | Edit counts | WAREHOUSE_STAFF | Nhap so luong thuc te cho tung SP | - |
| DRAFT | PENDING_APPROVAL | Submit | WAREHOUSE_STAFF | Co it nhat 1 item co chenh lech | Tinh variance (actual - system) |
| PENDING_APPROVAL | APPROVED | Approve (Finalize) | STORE_MANAGER | Review variance, dong y dieu chinh | Update Product.stockQuantity = actual count |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER | Nghi ngo sai so, yeu cau kiem lai | -> DRAFT, phai kiem ke lai |
| DRAFT | CANCELLED | Cancel | WAREHOUSE_STAFF, STORE_MANAGER | - | - |

**Business Rules:**
- Variance > 10% cua gia tri hang -> bat buoc Owner review (nang cap len 2-level approval)
- Ghi nhan ly do chenh lech cho tung item (mat mat, hu hong, dem sai lan truoc, ...)
- Lich su kiem ke: luu snapshot truoc va sau dieu chinh

---

## 6. Module: Finance (Tai Chinh)

### 6.1 Payment Voucher (Phieu Chi)

**Mo ta**: Ghi nhan chi tien (tra NCC, chi phi van hanh, luong, ...).

**Status Flow:**
`
DRAFT --> PENDING_APPROVAL --> APPROVED (= da chi)
  |              |
  v              v
CANCELLED    REJECTED (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | STORE_MANAGER, SALES_STAFF, WAREHOUSE_STAFF | Nhap thong tin chi | Generate voucher code |
| DRAFT | PENDING_APPROVAL | Submit | Creator | Co so tien, nguoi nhan, ly do | - |
| PENDING_APPROVAL | APPROVED | Approve | STORE_MANAGER (< 50M), OWNER (>= 50M) | Approver != Creator | Ghi nhan chi, giam so du quy |
| PENDING_APPROVAL | REJECTED | Reject | STORE_MANAGER, OWNER | - | -> DRAFT |
| DRAFT | CANCELLED | Cancel | Creator, STORE_MANAGER | - | - |

**Business Rules:**
- Admin KHONG tao phieu chi (theo RDS authorization matrix)
- Phieu chi >= 50M bat buoc Owner duyet
- Phieu chi lien ket PO: tu dong tao khi PO COMPLETED (tra NCC)

---

### 6.2 Receipt Voucher (Phieu Thu)

**Mo ta**: Ghi nhan thu tien (thu cong no KH, thu tien mat, ...).

**Status Flow:**
`
DRAFT --> CONFIRMED (= da thu)
  |
  v
CANCELLED
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | STORE_MANAGER, SALES_STAFF | Nhap thong tin thu | Generate voucher code |
| DRAFT | CONFIRMED | Confirm | STORE_MANAGER | Xac nhan da nhan tien | Tang so du quy, giam cong no KH (neu co) |
| DRAFT | CANCELLED | Cancel | Creator, STORE_MANAGER | - | - |
| CONFIRMED | CANCELLED | Void | OWNER | Sai sot, phai co ly do | Reverse: giam quy, tang lai cong no |

**Business Rules:**
- Phieu thu don gian hon phieu chi (1 cap xac nhan, khong can Owner duyet)
- Ly do: phieu thu la ghi nhan tien DA nhan, khong co rui ro chi sai
- Lien ket voi CustomerDebt: khi thu cong no, tu dong giam debt balance

---

### 6.3 Price Adjustment (Dieu Chinh Gia)

**Mo ta**: Thay doi gia von (cost price) cua hang ton kho.

**Status Flow (tu RDS line 1319):**
`
DRAFT --> PENDING_APPROVAL --> FINALIZED
  |              |
  v              v
CANCELLED    REJECTED (-> DRAFT)
`

**Transition Table:**

| From | To | Action | Allowed Roles | Conditions | Side Effects |
|------|----|--------|---------------|------------|--------------|
| (new) | DRAFT | Create | STORE_MANAGER | Chon SP, nhap gia moi | - |
| DRAFT | PENDING_APPROVAL | Submit | STORE_MANAGER | Co it nhat 1 item | - |
| PENDING_APPROVAL | FINALIZED | Approve | OWNER | Review chenh lech gia | Update Product.costPrice |
| PENDING_APPROVAL | REJECTED | Reject | OWNER | - | -> DRAFT |
| DRAFT | CANCELLED | Cancel | Creator | - | - |

**Business Rules:**
- Chi OWNER duyet dieu chinh gia (anh huong truc tiep den bao cao lai lo)
- Ghi nhan gia cu va gia moi cho audit
- Khong anh huong den gia ban (sellingPrice), chi anh huong costPrice

---

## 7. Database Schema Implications

### 7.1 Bang ApprovalHistory (Lich su phe duyet)

`sql
CREATE TABLE ApprovalHistory (
    HistoryID INT IDENTITY(1,1) PRIMARY KEY,
    DocumentType NVARCHAR(50) NOT NULL,    -- PurchaseOrder, StockTransfer, etc.
    DocumentID INT NOT NULL,                -- FK to document table
    FromStatus NVARCHAR(30) NOT NULL,
    ToStatus NVARCHAR(30) NOT NULL,
    Action NVARCHAR(30) NOT NULL,           -- SUBMIT, APPROVE, REJECT, CANCEL
    PerformedBy INT NOT NULL REFERENCES Employee(EmployeeID),
    Reason NVARCHAR(500),                   -- Required for REJECT, CANCEL
    CreatedAt DATETIME DEFAULT GETDATE()
);
`

### 7.2 Cot Status trong cac bang chung tu

`sql
-- Them vao moi bang chung tu:
ALTER TABLE PurchaseOrder ADD
    Status NVARCHAR(30) DEFAULT 'DRAFT',
    CreatedBy INT REFERENCES Employee(EmployeeID),
    ApprovedBy INT REFERENCES Employee(EmployeeID),
    ApprovedAt DATETIME,
    SubmittedAt DATETIME,
    CompletedAt DATETIME,
    CancelledAt DATETIME,
    CancelReason NVARCHAR(500),
    RejectCount INT DEFAULT 0;
`

### 7.3 Status Enum (dung chung)

`java
public enum DocumentStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    IN_PROGRESS,    // for Stock Transfer (IN_TRANSIT)
    RECEIVING,      // for Purchase Order
    COMPLETED,
    FINALIZED,      // for Price Adjustment
    CANCELLED
}
`

### 7.4 Bang CustomerDebt (Cong no KH)

`sql
CREATE TABLE CustomerDebt (
    DebtID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL REFERENCES Customer(CustomerID),
    OrderID INT REFERENCES [Order](OrderID),
    Amount DECIMAL(18,2) NOT NULL,
    PaidAmount DECIMAL(18,2) DEFAULT 0,
    DueDate DATE,
    Status NVARCHAR(20) DEFAULT 'UNPAID', -- UNPAID, PARTIAL, PAID, OVERDUE
    CreatedAt DATETIME DEFAULT GETDATE()
);
`

---

## 8. Implementation Priority

### Phase 1 (Sprint 1): Foundation

| Task | Effort | Ghi chu |
|------|--------|---------|
| Tao DocumentStatus enum | 1h | Dung chung cho tat ca module |
| Tao ApprovalHistory table + DAO | 4h | Foundation cho moi workflow |
| Tao ApprovalService (generic) | 4h | canApprove(), submit(), approve(), reject() |
| Integrate vao POS (direct sale, cancel) | 2h | Don gian nhat, khong co approval |

### Phase 2 (Sprint 2): Purchase + Inventory

| Task | Effort | Ghi chu |
|------|--------|---------|
| Purchase Order workflow | 12h | Full DRAFT -> COMPLETED flow |
| Inventory Receipt Voucher | 6h | Lien ket voi PO |
| Stock Transfer workflow | 8h | DRAFT -> IN_TRANSIT -> COMPLETED |
| Stock Adjustment workflow | 6h | DRAFT -> FINALIZED |

### Phase 3 (Sprint 3): Sales + Finance

| Task | Effort | Ghi chu |
|------|--------|---------|
| Credit Sales Order workflow | 8h | Approval + debt creation |
| Order Return workflow | 6h | Approval + refund |
| Payment Voucher workflow | 6h | Approval theo threshold |
| Receipt Voucher | 4h | Don gian, 1 cap |

### Phase 4 (Sprint 4): Advanced

| Task | Effort | Ghi chu |
|------|--------|---------|
| Price Adjustment workflow | 4h | Owner-only approval |
| Notification system | 8h | Email/in-app khi can duyet |
| Dashboard: pending approvals widget | 4h | Manager thay don cho duyet |

---

## 9. Simplified Version (Neu Thieu Thoi Gian)

Neu khong du thoi gian implement day du, co the don gian hoa:

| Full Version | Simplified Version |
|--------------|-------------------|
| 2-level approval (Manager + Owner) | 1-level (Manager only) |
| Monetary thresholds | Khong phan biet gia tri |
| Notification system | Chi hien tren dashboard |
| Reject count tracking | Khong track |
| Partial receiving | Nhan het hoac khong nhan |
| Auto-close after 30 days | Manual close |

**Minimum Viable Workflow:**
`
DRAFT --> APPROVED --> COMPLETED
  |          |
  v          v
CANCELLED  CANCELLED
`

Khong co PENDING_APPROVAL (Manager duyet truc tiep tu DRAFT). Giam do phuc tap nhung van the hien duoc nghiep vu phe duyet.

---

## 10. Quyet Dinh (Da Resolved - 2026-06-01)

> Chi tiet ly do: Obsidian/Working/SWP391/Hoang Ha Chi/approval-workflow-decisions.md

| # | Van de | Quyet dinh | Ly do chinh |
|---|--------|-----------|-------------|
| 1 | POS co can draft khong? | KHONG. POS = direct sale | Ban truc tiep, khong can cho duyet |
| 2 | Ai duyet Stock Transfer? | Manager chi nhanh NGUON | Ho mat hang, ho quyet dinh |
| 3 | Nguong tien phe duyet? | 50M VND (hardcode cho SWP391) | Du lon de Owner can biet |
| 4 | Partial receiving? | CO, don gian (1 truong receivedQty) | NCC thuong giao nhieu dot |
| 5 | Thoi han doi tra? | 7 ngay | Tieu chuan nganh ban le VN |
| 6 | Credit limit theo tier? | Standard 0 / Silver 5M / Gold 20M / VIP 50M | Ty le ~50% credit/doanh so |
| 7 | Price Adjustment approver? | Chi OWNER | Quyet dinh tai chinh, anh huong P&L |
| 8 | Cancel sau APPROVED? | Chi OWNER | Side effects lon, can cap cao nhat |
