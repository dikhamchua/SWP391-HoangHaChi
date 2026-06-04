# Activity History Implementation Pattern

## Mục đích

Ghi lại cách triển khai tab lịch sử hoạt động cho các màn hình quản trị như Customer, Employee, Branch trong KiotRetail.

Pattern này dùng khi cần lưu lại các thao tác CRUD của một entity và hiển thị trên trang chỉnh sửa hoặc chi tiết.

## Phạm vi áp dụng

- Entity có màn hình admin edit/detail.
- Cần ghi lịch sử các hành động `add`, `update`, `delete`, `other`.
- Người thực hiện lấy từ `AppConstants.SESSION_EMPLOYEE` nếu có.
- Không đưa SQL vào servlet hoặc JSP.

## Trạng thái triển khai

| Entity | Migration | Model | DAO | Service | Servlet | JSP |
|---|---|---|---|---|---|---|
| Branch | Done | Done | Done | Done | Done | Done |
| Employee | Done | Done | Done | Done | Done | Done |
| Customer | Done | Done | Done | Done | Done | Done |

## Cấu trúc triển khai

### 1. Migration database

Tạo bảng activity riêng cho từng entity:

```sql
CREATE TABLE IF NOT EXISTS ActivityBranch (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    FK_ID INT NOT NULL,
    Type VARCHAR(20) NOT NULL,
    CreatedBy INT NULL,
    Description TEXT NOT NULL,
    CONSTRAINT FK_ActivityBranch_CreatedBy
        FOREIGN KEY (CreatedBy) REFERENCES Employee(EmployeeID),
    CONSTRAINT CK_ActivityBranch_Type
        CHECK (Type IN ('add', 'update', 'delete', 'other'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IDX_ActivityBranch_FK_ID
    ON ActivityBranch(FK_ID, ID DESC);
```

Luôn tạo rollback tương ứng:

```sql
DROP TABLE IF EXISTS ActivityBranch;
```

## 2. Model

Tạo model activity theo entity:

- `id`
- `fkId`
- `type`
- `createdBy`
- `description`

Ví dụ: `ActivityBranch`, `ActivityEmployee`, `ActivityCustomer`.

## 3. DAO

Tạo DAO riêng, ví dụ `ActivityBranchDAO`:

- `insert(ActivityBranch activity)` để ghi activity.
- `getByFkId(int fkId)` để lấy lịch sử theo entity id.
- Dùng try-with-resources.
- Dùng parameterized query.
- Không xử lý business logic trong DAO.

Nếu entity cần ghi activity ngay sau khi tạo, DAO insert chính phải lấy generated key và set lại id cho model.

## 4. Service

Trong service chính của entity:

- Khai báo activity DAO.
- Thêm overload method nhận `Integer createdBy`.
- Sau khi thao tác CRUD thành công thì gọi `recordActivity(...)`.
- Thêm method public `getActivitiesBy<Entity>Id(int id)`.

Ví dụ:

```java
private void recordActivity(int fkId, String type, Integer createdBy, String description) {
    if (fkId <= 0) {
        return;
    }
    ActivityBranch activity = new ActivityBranch();
    activity.setFkId(fkId);
    activity.setType(type);
    activity.setCreatedBy(createdBy);
    activity.setDescription(description);
    activityBranchDAO.insert(activity);
}
```

## 5. Servlet

Trong servlet:

- Khi mở edit/detail, set attribute `activities`.
- Khi add/update/delete, truyền `createdBy` từ session vào service.
- Sau update, redirect lại trang edit để người dùng thấy activity mới.

Ví dụ:

```java
private Integer getCurrentEmployeeId(HttpServletRequest request) {
    Object employee = request.getSession().getAttribute(AppConstants.SESSION_EMPLOYEE);
    if (employee instanceof Employee) {
        return ((Employee) employee).getEmployeeId();
    }
    return null;
}
```

## 6. JSP

Trang edit/detail nên có hai tab:

- `Thông tin chung`
- `Lịch sử hoạt động`

Dữ liệu lịch sử đọc từ `${activities}` bằng JSTL:

```jsp
<c:choose>
    <c:when test="${empty activities}">
        <div>Chưa có lịch sử hoạt động</div>
    </c:when>
    <c:otherwise>
        <c:forEach var="activity" items="${activities}">
            <div>
                <div><c:out value="${activity.description}" /></div>
                <div>Người thực hiện: <c:out value="${activity.createdBy}" default="Hệ thống" /></div>
                <span><c:out value="${activity.type}" /></span>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>
```

## Validation

Sau khi triển khai:

1. Chạy compile nhanh:

```bash
mvn compile -q -fae
```

2. Chạy package vì có servlet/JSP:

```bash
mvn package
```

3. Áp dụng migration vào MySQL local nếu được phép:

```powershell
Get-Content -Path "sql\migrations\20260604-create-activity-branch.sql" | docker exec -i mysql-8.0.36 mysql -uroot -proot DBFinora
```

4. Kiểm tra bảng:

```powershell
"SHOW TABLES LIKE 'ActivityBranch';" | docker exec -i mysql-8.0.36 mysql -uroot -proot DBFinora
```

## Lưu ý

- Schema change cần hỏi xác nhận trước khi tạo migration.
- Không dùng `mvn clean package` nếu Tomcat/Cargo đang khóa file trong `target`; có thể dùng `mvn package` để validate build khi clean bị lỗi do file lock.
- Nếu dùng PowerShell `Set-Content`, cần ghi UTF-8 không BOM cho Java để tránh lỗi `illegal character: '﻿'`.
- Không dùng scriptlet trong JSP.
- Không gọi DAO từ servlet/JSP.
- Không ghi SQL trong servlet/JSP.