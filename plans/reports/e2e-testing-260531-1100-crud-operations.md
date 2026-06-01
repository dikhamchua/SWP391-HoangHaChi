# Bao Cao Kiem Thu E2E - KiotRetail

**Ngay:** 2026-05-31 | **Port:** 9999 | **Account:** owner@retail.com

## 1. Tong Quan

| Chi so | So luong | Ty le |
|---|---|---|
| Tong test | 47 | 100% |
| PASS | 38 | 80.85% |
| FAIL | 9 | 19.15% |
| Blocked | 0 | 0% |

| Module | Tong | PASS | FAIL |
|---|---|---|---|
| Login & Session | 1 | 1 | 0 |
| Customer CRUD | 15 | 10 | 5 |
| Employee CRUD | 14 | 12 | 2 |
| Invoice Operations | 18 | 16 | 2 |

## 2. Chi Tiet

### Customer (10 PASS / 5 FAIL)

| ID | Ten | Status | Ghi chu |
|---|---|---|---|
| 1 | List GET | PASS | Render OK |
| 2 | Create valid | PASS | Redirect ve list |
| 3 | Duplicate phone | **FAIL** | Khong hien thi loi trung SDT |
| 4 | Empty required fields | **FAIL** | Khong validate required |
| 5 | Invalid phone format | **FAIL** | Chap nhan abcdefg |
| 6 | Invalid email format | **FAIL** | Chap nhan not-valid-email |
| 7 | XSS in name | PASS | Output escaping OK |
| 8 | Very long name | PASS | Graceful |
| 9 | Update | PASS | OK |
| 10 | Delete non-existent | PASS | Graceful |
| 11 | View invalid ID | PASS | Graceful |
| 12 | Pagination page=0 | **FAIL** | HTTP 500 |
| 13 | Pagination negative | **FAIL** | HTTP 500 |
| 14 | Filter gender | PASS | OK |
| 15 | Search keyword | PASS | OK |

### Employee (12 PASS / 2 FAIL)

| ID | Ten | Status | Ghi chu |
|---|---|---|---|
| 1 | List GET | PASS | OK |
| 2 | Create valid | PASS | OK |
| 3 | Duplicate email | PASS | Reject |
| 4 | Empty fields | PASS | Reject |
| 5 | Invalid email | PASS | Reject |
| 6 | Weak password | PASS | Reject |
| 7 | XSS in name | PASS | Sanitized |
| 8 | Update | PASS | OK |
| 9 | Delete non-existent | PASS | Graceful |
| 10 | View invalid ID | PASS | OK |
| 11 | Pagination page=0 | **FAIL** | HTTP 500 |
| 12 | Pagination negative | **FAIL** | HTTP 500 |
| 13 | Pagination very large | PASS | Empty result |
| 14 | SQL injection | PASS | Parameterized query |

### Invoice (16 PASS / 2 FAIL)

| ID | Ten | Status | Ghi chu |
|---|---|---|---|
| 1 | List GET | PASS | OK |
| 2-4 | Filter status | PASS | OK |
| 5 | Filter date range | PASS | OK |
| 6 | Filter keyword | PASS | OK |
| 7 | View id=1 | PASS | OK |
| 8 | View invalid id | PASS | Graceful |
| 9 | Cancel order | PASS | OK |
| 10 | Cancel non-existent | PASS | Graceful |
| 11 | AddPayment valid | PASS | OK |
| 12 | AddPayment amount=0 | **FAIL** | Chap nhan silently |
| 13 | AddPayment negative | **FAIL** | Chap nhan silently |
| 14 | AddPayment non-numeric | PASS | Default 0 |
| 15-18 | Pagination/Filter | PASS | OK |

## 3. Logic Bugs

| # | Module | Bug | Muc do |
|---|---|---|---|
| 1 | Customer | Thieu validation dinh dang SDT | Cao |
| 2 | Customer | Thieu validation dinh dang email | Cao |
| 3 | Customer | Thieu validation required field | Cao |
| 4 | Customer | Khong hien thi loi trung SDT | Trung binh |
| 5 | Customer/Employee | Pagination 500 voi page<=0 | Cao |
| 6 | Invoice | AddPayment chap nhan amount=0 | Cao |
| 7 | Invoice | AddPayment chap nhan amount am | Nghiem trong |
| 8 | Invoice | AddPayment non-numeric default 0 | Trung binh |

## 4. Bao Mat

| Hang muc | Ket qua |
|---|---|
| XSS (Customer/Employee) | An toan - escaping OK |
| SQL Injection | An toan - parameterized query |
| Stack trace leak | An toan |
| Session/Auth | An toan |
| Input validation | **Yeu** - Customer thieu validation |
| Business rule (Invoice) | **Yeu** - cho phep thanh toan am/0 |

## 5. De Xuat Sua Loi

### P0 - Khan cap
1. Invoice: validate amount > 0 trong addPayment
2. Customer/Employee: clamp page = Math.max(1, page) truoc query
3. Customer: them check required fullName/phone

### P1 - Quan trong
1. Customer: regex validate SDT (^[0-9]{10,11}$)
2. Customer: validate email format
3. Customer: hien thi flash message khi trung SDT
4. Invoice: throw error thay vi silent default 0

### P2 - Cai thien
1. Tao ValidationUtil dung chung giua modules
2. Customer: max length constraint
3. Fix JSTL javax/jakarta cho Tomcat 10

## 6. Cau Hoi Chua Giai Quyet

1. Customer duplicate phone: silent reject hay accept? Can check DB
2. Pagination 500: NumberFormatException hay SQLException?
3. Invoice payment am: da co record am trong DB chua?
4. Chua test phan quyen (cashier/manager roles)
