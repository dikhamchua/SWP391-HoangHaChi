# KiotRetail - Hệ thống Quản lý Bán hàng

Hệ thống quản lý bán hàng toàn diện được xây dựng bằng JSP, Servlet, JDBC và Bootstrap 5.

## 🚀 Công nghệ sử dụng

- **Backend**: Java Servlet, JSP, JDBC
- **Frontend**: Bootstrap 5.3.0, Material Icons
- **Database**: SQL Server 2019+
- **Server**: Apache Tomcat 10.1
- **Build Tool**: Maven 3.6+
- **Java**: JDK 17

## 📋 Yêu cầu hệ thống

- **JDK 17** trở lên
- **Apache Tomcat 10.1** (Jakarta EE 9+)
- **SQL Server 2019** trở lên
- **Maven 3.6+**

## 📁 Cấu trúc thư mục

```
kiotretail/
├── sql/
│   ├── schema_sqlserver.sql       # SQL Server schema và dữ liệu mẫu
│   └── schema.sql                 # MySQL schema (deprecated)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/kiotretail/
│       │       ├── controller/    # Servlet controllers
│       │       ├── dao/           # Data Access Objects
│       │       ├── model/         # Entity models
│       │       ├── util/          # Utilities
│       │       └── filter/        # Filters
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── web.xml        # Web configuration
│           │   └── views/         # JSP pages
│           │       ├── common/    # Reusable components
│           │       ├── auth/      # Authentication pages
│           │       ├── admin/     # Admin pages
│           │       └── pos/       # POS pages
│           ├── assets/
│           │   ├── css/           # Stylesheets
│           │   │   ├── theme.css      # Theme variables
│           │   │   ├── components.css # Component styles
│           │   │   └── README.md      # CSS documentation
│           │   ├── js/            # JavaScript files
│           │   └── images/        # Images
│           └── index.jsp          # Landing page
├── pom.xml                        # Maven configuration
├── README.md                      # This file
├── QUICKSTART.md                  # Quick start guide
├── PROJECT_STRUCTURE.md           # Project structure details
└── SQLSERVER_MIGRATION.md         # SQL Server migration guide
```

## 🔧 Hướng dẫn cài đặt

### 1. Cài đặt SQL Server Database

**Cách 1: Sử dụng SQL Server Management Studio (SSMS)**
```sql
-- Mở SSMS và connect với:
-- Server: localhost
-- Authentication: SQL Server Authentication
-- Login: sa
-- Password: 123456

-- Mở file sql/schema_sqlserver.sql và Execute (F5)
```

**Cách 2: Sử dụng sqlcmd**
```bash
sqlcmd -S localhost -U sa -P 123456 -i "D:\code\kiotretail\sql\schema_sqlserver.sql"
```

### 2. Cấu hình Database Connection

File đã được cấu hình sẵn trong `DatabaseUtil.java`:

```java
URL: jdbc:sqlserver://localhost:1433;databaseName=SamplePE;trustServerCertificate=true
Username: sa
Password: 123456
```

**Nếu cần thay đổi**, mở file:
```
src/main/java/com/kiotretail/util/DatabaseUtil.java
```

### 3. Cài đặt Dependencies

Project sử dụng Maven để quản lý dependencies:

```bash
cd D:\code\kiotretail
mvn clean install
```

**Dependencies chính:**
- Jakarta Servlet API 5.0.0 (Tomcat 10+)
- Jakarta JSP API 3.0.0
- Jakarta JSTL 2.0.0
- SQL Server JDBC Driver 12.4.2

### 4. Build Project

```bash
# Build WAR file
mvn clean package

# File WAR sẽ được tạo tại:
# target/kiotretail.war
```

### 5. Deploy lên Tomcat 10.1

#### Cách 1: Sử dụng IDE (NetBeans/IntelliJ/Eclipse)

**NetBeans:**
1. Tools → Servers → Add Server
2. Chọn Apache Tomcat
3. Server Location: `C:\Tomcat 10.1_Tomcat`
4. Right-click project → Run

**IntelliJ IDEA:**
1. File → Settings → Application Servers
2. Add Tomcat Server
3. Tomcat Home: `C:\Tomcat 10.1_Tomcat`
4. Run → Edit Configurations → Add Tomcat Server

#### Cách 2: Deploy thủ công

```bash
# Copy WAR file vào Tomcat
copy target\kiotretail.war "C:\Tomcat 10.1_Tomcat\webapps\"

# Start Tomcat
cd "C:\Tomcat 10.1_Tomcat\bin"
startup.bat

# Xem log
tail -f "C:\Tomcat 10.1_Tomcat\logs\catalina.out"
```

### 6. Truy cập ứng dụng

Mở trình duyệt và truy cập:
```
http://localhost:8080/kiotretail/
```

## 🔐 Tài khoản đăng nhập mặc định

### Admin
- **Username**: `admin`
- **Password**: `123456`
- **Vai trò**: Quản trị hệ thống
- **Mã NV**: NV00124

### Nhân viên
- **Username**: `thungan01`
- **Password**: `123456`
- **Vai trò**: Nhân viên bán hàng
- **Mã NV**: NV00125

## ✨ Tính năng chính

### 1. Quản lý Bán hàng (POS)
- Giao diện bán hàng nhanh
- Tìm kiếm sản phẩm
- Tính toán tự động
- In hóa đơn

### 2. Quản lý Hàng hóa
- Thêm/sửa/xóa sản phẩm
- Quản lý danh mục
- Theo dõi tồn kho
- Cảnh báo hàng sắp hết

### 3. Quản lý Giao dịch
- Danh sách hóa đơn
- Chi tiết giao dịch
- Trả hàng/Hoàn tiền
- Xuất báo cáo

### 4. Quản lý Khách hàng
- Thông tin khách hàng
- Lịch sử mua hàng
- Công nợ
- Nhóm khách hàng

### 5. Quản lý Nhân viên
- Thông tin nhân viên
- Phân quyền
- Theo dõi hiệu suất

### 6. Báo cáo
- Báo cáo doanh thu
- Báo cáo lợi nhuận
- Báo cáo tồn kho
- Báo cáo cuối ngày

### 7. Sổ quỹ
- Thu/Chi
- Đối soát
- Báo cáo tài chính

## 🏗️ Kiến trúc MVC

### Model (Entity)
```
com.kiotretail.model
├── Employee.java
├── Product.java
├── Customer.java
├── Invoice.java
└── ...
```

### DAO (Data Access Layer)
```
com.kiotretail.dao
├── EmployeeDAO.java
├── ProductDAO.java
├── CustomerDAO.java
└── ...
```

### Controller (Servlet)
```
com.kiotretail.controller
├── LoginServlet.java
├── ProductServlet.java
├── InvoiceServlet.java
└── ...
```

### View (JSP)
```
WEB-INF/views/
├── common/
│   ├── header.jsp
│   ├── footer.jsp
│   └── sidebar.jsp
├── auth/
│   ├── login.jsp
│   └── role-selection.jsp
├── admin/
│   ├── dashboard.jsp
│   ├── products.jsp
│   └── ...
└── pos/
    └── sale.jsp
```

## 🎨 CSS Structure

Project sử dụng cấu trúc CSS module hóa:

- **theme.css** - CSS Variables, colors, fonts, base styles
- **components.css** - Layout, components, sizing, spacing

Xem chi tiết tại: `src/main/webapp/assets/css/README.md`

## 🔍 Troubleshooting

### Lỗi kết nối SQL Server

**Triệu chứng:**
```
java.sql.SQLException: Cannot open database
```

**Giải pháp:**
1. Kiểm tra SQL Server service đang chạy
2. Enable TCP/IP trong SQL Server Configuration Manager
3. Enable SQL Server Authentication (Mixed Mode)
4. Restart SQL Server service

```bash
# Test connection
sqlcmd -S localhost -U sa -P 123456
```

### Lỗi "javax.servlet.Filter not found"

**Triệu chứng:**
```
java.lang.NoClassDefFoundError: javax/servlet/Filter
```

**Nguyên nhân:** Đang dùng Tomcat 10+ nhưng code dùng `javax.servlet`

**Giải pháp:** Project đã được update sang Jakarta EE. Chỉ cần:
```bash
mvn clean package
```

### Lỗi 404 Not Found

**Kiểm tra:**
- Context path: `/kiotretail`
- URL đúng: `http://localhost:8080/kiotretail/`
- Tomcat đã start thành công

### Lỗi encoding tiếng Việt

**Giải pháp:**
- Database đã dùng `NVARCHAR` (Unicode)
- EncodingFilter đã được cấu hình trong web.xml
- Connection string có `trustServerCertificate=true`

### Lỗi JSTL không hoạt động

**Kiểm tra pom.xml có:**
```xml
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 📚 Tài liệu bổ sung

- **QUICKSTART.md** - Hướng dẫn cài đặt nhanh trong 5 phút
- **PROJECT_STRUCTURE.md** - Chi tiết cấu trúc project
- **SQLSERVER_MIGRATION.md** - Hướng dẫn migration SQL Server
- **assets/css/README.md** - Tài liệu CSS structure

## 🔄 Phát triển thêm

### Thêm module mới

1. Tạo Model trong `com.kiotretail.model`
2. Tạo DAO trong `com.kiotretail.dao`
3. Tạo Servlet trong `com.kiotretail.controller`
4. Tạo JSP trong `WEB-INF/views`
5. Cập nhật web.xml với servlet mapping

### Thêm tính năng bảo mật

- Sử dụng BCrypt để hash password
- Thêm CSRF token
- Implement session timeout
- Thêm HTTPS

## 📦 Dependencies

```xml
<!-- Jakarta EE (Tomcat 10+) -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
</dependency>

<!-- SQL Server JDBC Driver -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.4.2.jre8</version>
</dependency>

<!-- JSTL -->
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 🌐 Browser Support

- Chrome 90+
- Firefox 88+
- Edge 90+
- Safari 14+

## 📝 Version History

- **v1.0.0** (2024-05-15)
  - Initial release
  - SQL Server support
  - Jakarta EE (Tomcat 10+)
  - Bootstrap 5.3.0
  - Material Design 3

## 👥 Team

- **Project**: SWP391_Group_5
- **Repository**: https://github.com/hoanghachi12082005-ops/SWP391_Group_5

## 📄 License

© 2024 KiotRetail. All rights reserved.

## 📞 Liên hệ & Hỗ trợ

- **GitHub Issues**: https://github.com/hoanghachi12082005-ops/SWP391_Group_5/issues
- **Email**: support@kiotretail.vn
- **Documentation**: See README.md, QUICKSTART.md, PROJECT_STRUCTURE.md

---

**Lưu ý quan trọng:**
- Project này sử dụng **Jakarta EE** (không phải Java EE)
- Yêu cầu **Tomcat 10.1+** (không tương thích với Tomcat 9)
- Database: **SQL Server** (không phải MySQL)
- Java: **JDK 17** (không phải JDK 8)
