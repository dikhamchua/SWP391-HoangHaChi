# Fix Report - Issue #37 Mojibake + Console Errors

- DateTime: 2026-06-02 07:36 (Asia/Bangkok)
- Issue: https://github.com/dikhamchua/SWP391-HoangHaChi/issues/37
- Branch: main
- Status: PASS - cả mojibake và console errors đã fix

## 1. Tóm tắt

| Bug | Severity | Status |
|-----|----------|--------|
| UTF-8 mojibake tiếng Việt trên mọi screen | High | FIXED |
| Console error: `/favicon.ico 404` | Low | FIXED |
| Console error: `/kiotretail/home 404` | Low | FIXED |
| Console error: `via.placeholder.com ERR_CONNECTION_CLOSED` | Low | FIXED |

Verify Playwright: home page sau fix → **0 console errors**, navbar/dropdown employee names hiển thị chuẩn tiếng Việt.

## 2. Root cause - Mojibake (quan trọng)

Ban đầu nghi ngờ là tầng JDBC encoding sai. Kiểm tra raw bytes trong DB:

```
SELECT FullName, HEX(FullName) FROM Employee WHERE EmployeeID=1;
> Nguyá»…n HoÃ ng Owner | 4E677579C3A1C2BBE280A66E20486FC383C2A06E67204F776E6572
```

**Dữ liệu trong DB đã bị double-encoded từ trước**. Bytes `C3 A1 C2 BB E2 80 A6` là UTF-8 encode của 3 ký tự `á»…` (windows-1252 view của bytes UTF-8 `e1 bb 85` của ký tự "ễ"). Nguyên nhân: file `sql/DBFinora.sql` đã được load vào MySQL với client charset latin1 trong khi file là UTF-8 → MySQL Convert sai khi insert.

JDBC chỉ là "nạn nhân" - đọc đúng bytes đã bị corrupt.

## 3. Fix

### 3.1 Recovery data đã corrupt (DB-side)

Apply trên 6 bảng có tiếng Việt:

```sql
SET NAMES utf8mb4;
UPDATE Employee SET FullName = CONVERT(CAST(FullName AS BINARY) USING utf8mb4) WHERE FullName IS NOT NULL;
UPDATE Branch SET Name = CONVERT(CAST(Name AS BINARY) USING utf8mb4) WHERE Name IS NOT NULL;
UPDATE Branch SET Address = CONVERT(CAST(Address AS BINARY) USING utf8mb4) WHERE Address IS NOT NULL;
UPDATE Supplier SET Name = CONVERT(CAST(Name AS BINARY) USING utf8mb4) WHERE Name IS NOT NULL;
UPDATE Supplier SET Address = CONVERT(CAST(Address AS BINARY) USING utf8mb4) WHERE Address IS NOT NULL;
UPDATE Customer SET FullName = CONVERT(CAST(FullName AS BINARY) USING utf8mb4) WHERE FullName IS NOT NULL;
UPDATE Customer SET Address = CONVERT(CAST(Address AS BINARY) USING utf8mb4) WHERE Address IS NOT NULL;
UPDATE Product SET Name = CONVERT(CAST(Name AS BINARY) USING utf8mb4) WHERE Name IS NOT NULL;
UPDATE Category SET Name = CONVERT(CAST(Name AS BINARY) USING utf8mb4) WHERE Name IS NOT NULL;
UPDATE Category SET Description = CONVERT(CAST(Description AS BINARY) USING utf8mb4) WHERE Description IS NOT NULL;
```

Verify sau update:
```
Nguyễn Hoàng Owner / Trần Văn Manager / Lê Minh Sales / Phạm Lan Sales / Đặng Kho Staff
Chi Nhánh Hà Nội / Chi Nhánh TP.HCM
Công Ty Coca Cola Việt Nam / Công Ty Pepsi Việt Nam
```

Recovery formula: `CONVERT(CAST(col AS BINARY) USING utf8mb4)` - bỏ qua charset interpretation, lấy bytes raw rồi decode lại bằng utf8mb4.

### 3.2 Phòng ngừa tương lai - JDBC `SET NAMES`

Trong `DatabaseUtil.getConnection()` thêm:
```java
try (Statement stmt = conn.createStatement()) {
    stmt.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
    stmt.execute("SET character_set_client = utf8mb4");
    stmt.execute("SET character_set_connection = utf8mb4");
    stmt.execute("SET character_set_results = utf8mb4");
}
```

Lý do: MySQL container dùng `character_set_client/connection/results = latin1` mặc định (dù characterEncoding=UTF-8 ở URL). `SET NAMES` đảm bảo writes/reads tương lai không tái sinh mojibake.

### 3.3 Console errors

`src/main/webapp/WEB-INF/views/common/header.jsp` (cho admin pages):
```html
<link rel="icon" type="image/svg+xml" href="data:image/svg+xml,..." />
```

`src/main/webapp/index.jsp` (cho landing page):
- Thêm favicon inline SVG → fix `/favicon.ico 404`
- Thay `<a href="#">` bằng `<a href="${pageContext.request.contextPath}/">` → tránh request fantôm `/kiotretail/home`
- Thay `https://via.placeholder.com/...` bằng inline SVG `data:image/svg+xml,...` → loại bỏ external service phụ thuộc

## 4. Files thay đổi

| File | Loại | Note |
|------|------|------|
| `src/main/java/com/kiotretail/shared/util/DatabaseUtil.java` | EDIT | Thêm SET NAMES utf8mb4 + URL params characterSetResults/connectionCollation |
| `src/main/webapp/WEB-INF/views/common/header.jsp` | EDIT | Inline SVG favicon |
| `src/main/webapp/index.jsp` | EDIT | Inline SVG favicon + replace placeholder.com + fix `<a href="#">` |
| DB | UPDATE | Recovery 10 cột tiếng Việt trên 6 bảng |

## 5. Verification

### 5.1 Curl raw bytes
```
Trước fix: 4e67 7579 c3a1 c2bb e280 a66e ...   → "Nguyá»…n..."
Sau fix:  4e67 7579 e1bb 856e 2048 6fc3 a06e... → "Nguyễn Hoàng..."
Reference: 4e67 7579 e1bb 856e                  → "Nguyễn"
```

### 5.2 Playwright
Pending Inbox employee dropdown:
```
[Nguyễn Hoàng Owner, Trần Văn Manager, Lê Minh Sales, Phạm Lan Sales, Đặng Kho Staff]
```

History page audit log:
```
"Phê duyệt Nguyễn Hoàng Owner Không có ghi chú PENDING_APPROVAL → APPROVED"
"Gửi duyệt Lê Minh Sales Không có ghi chú DRAFT → PENDING_APPROVAL"
```

Home page console: `0 errors, 0 warnings`.

## 6. Screenshots

`plans/reports/screenshots/issue-37-fixes/`:
- `01-home-no-errors.png`
- `02-pending-utf8.png` - Pending inbox với tên tiếng Việt chuẩn
- `03-history-utf8.png` - History audit log đúng chữ Việt
- `04-home-with-favicon.png` - Home page sạch console

## 7. Câu hỏi mở

- File `sql/DBFinora.sql` có cần ghi rõ `SET NAMES utf8mb4` ở đầu để tránh tái phát khi import lại?
- Có nên thêm migration script `sql/migrations/20260602-recovery-mojibake.sql` để document fix này (tham chiếu rule sql-schema.md - nhưng đây là data fix, không phải schema)?
- Test này chưa cover UAT case "user nhập tiếng Việt mới qua form" - ApprovalService write-flow đã thử với reason "Vuot ngan sach quy 2" (ASCII), chưa test "Vượt ngân sách quý 2".
