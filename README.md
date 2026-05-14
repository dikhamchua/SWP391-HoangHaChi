<<<<<<< HEAD
# KiotRetail - Hệ thống Quản lý Bán hàng

Hệ thống quản lý bán hàng toàn diện được xây dựng bằng JSP, Servlet, JDBC và Bootstrap 5.

## Công nghệ sử dụng

- **Backend**: Java Servlet, JSP, JDBC
- **Frontend**: Bootstrap 5, Material Icons
- **Database**: MySQL 8.0+
- **Server**: Apache Tomcat 9.0+
- **Build Tool**: Maven (optional)

## Yêu cầu hệ thống

- JDK 8 trở lên
- Apache Tomcat 9.0 trở lên
- MySQL 8.0 trở lên
- Maven 3.6+ (nếu sử dụng)

## Cấu trúc thư mục

```
kiotretail/
├── sql/
│   └── schema.sql                 # Database schema và dữ liệu mẫu
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
│           │   ├── js/            # JavaScript files
│           │   └── images/        # Images
│           └── index.jsp          # Landing page
└── README.md
```

## Hướng dẫn cài đặt

### 1. Cài đặt Database

```bash
# Đăng nhập MySQL
mysql -u root -p

# Tạo database và import schema
source D:/code/kiotretail/sql/schema.sql
```

Hoặc sử dụng MySQL Workbench để import file `schema.sql`.

### 2. Cấu hình Database Connection

Mở file `src/main/java/com/kiotretail/util/DatabaseUtil.java` và cập nhật thông tin kết nối:

```java
private static final String URL = "jdbc:mysql://localhost:3306/kiotretail?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

### 3. Thêm MySQL Connector

#### Cách 1: Sử dụng Maven

Thêm dependency vào `pom.xml`:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

#### Cách 2: Thêm JAR thủ công

1. Download MySQL Connector/J từ: https://dev.mysql.com/downloads/connector/j/
2. Copy file `mysql-connector-java-x.x.xx.jar` vào thư mục `WEB-INF/lib/`

### 4. Thêm JSTL Library

Download và copy các file JAR sau vào `WEB-INF/lib/`:
- `jstl-1.2.jar`
- `standard-1.1.2.jar`

Hoặc thêm vào Maven:

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

### 5. Deploy lên Tomcat

#### Cách 1: Sử dụng IDE (Eclipse/IntelliJ)

1. Import project vào IDE
2. Configure Tomcat server
3. Add project to server
4. Start server

#### Cách 2: Deploy thủ công

1. Build project thành file WAR
2. Copy file WAR vào thư mục `tomcat/webapps/`
3. Start Tomcat server

```bash
# Windows
cd C:\apache-tomcat-9.0.xx\bin
startup.bat

# Linux/Mac
cd /opt/tomcat/bin
./startup.sh
```

### 6. Truy cập ứng dụng

Mở trình duyệt và truy cập:
```
http://localhost:8080/kiotretail/
```

## Tài khoản đăng nhập mặc định

### Admin
- **Username**: `admin`
- **Password**: `123456`
- **Vai trò**: Quản trị hệ thống

### Nhân viên
- **Username**: `thungan01`
- **Password**: `123456`
- **Vai trò**: Nhân viên bán hàng

## Tính năng chính

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

## Cấu trúc MVC

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
│   └── login.jsp
├── admin/
│   ├── products.jsp
│   ├── invoices.jsp
│   └── ...
└── pos/
    └── sale.jsp
```

## Troubleshooting

### Lỗi kết nối Database
- Kiểm tra MySQL service đã chạy chưa
- Kiểm tra username/password trong DatabaseUtil.java
- Kiểm tra MySQL Connector JAR đã được thêm chưa

### Lỗi 404 Not Found
- Kiểm tra context path trong web.xml
- Kiểm tra URL mapping trong servlet

### Lỗi encoding tiếng Việt
- Đảm bảo database charset là utf8mb4
- Kiểm tra EncodingFilter đã được cấu hình
- Thêm `?useUnicode=true&characterEncoding=UTF-8` vào connection string

### Lỗi JSTL không hoạt động
- Kiểm tra JSTL JAR đã được thêm vào WEB-INF/lib/
- Kiểm tra taglib directive trong JSP

## Phát triển thêm

### Thêm module mới
1. Tạo Model trong `com.kiotretail.model`
2. Tạo DAO trong `com.kiotretail.dao`
3. Tạo Servlet trong `com.kiotretail.controller`
4. Tạo JSP trong `WEB-INF/views`
5. Cập nhật web.xml

### Thêm tính năng bảo mật
- Sử dụng BCrypt để hash password
- Thêm CSRF token
- Implement session timeout
- Thêm HTTPS

## License

© 2024 KiotRetail. All rights reserved.

## Liên hệ

- Email: support@kiotretail.vn
- Website: https://kiotretail.vn
- Hotline: 1900-xxxx
=======
# SWP391_Group_5
>>>>>>> 6f4e3325732fdebed0a184faf3ed4ad162041f2f
