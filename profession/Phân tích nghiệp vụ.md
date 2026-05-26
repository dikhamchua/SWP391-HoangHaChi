Phân tích nghiệp vụ

1\. Phần quyền truy cập và quản lý hệ thống

1.1. Mục tiêu nghiệp vụ

Chức năng này giúp:

Kiểm soát người dùng truy cập hệ thống

Phân quyền theo vai trò

Đảm bảo bảo mật dữ liệu

Quản lý hoạt động của nhân viên trong chuỗi cửa hàng

Theo dõi lịch sử thao tác để kiểm tra và xử lý sai sót

1.2. Các đối tượng sử dụng hệ thống

Vai trò

Mô tả

Quản trị viên (Admin)

Quản lý toàn bộ hệ thống

Quản lý cửa hàng

Quản lý hoạt động tại chi nhánh

Nhân viên bán hàng

Thực hiện bán hàng

Nhân viên kho

Quản lý nhập/xuất kho

Khách hàng

Chỉ xem thông tin hoặc mua hàng



1.3. Các chức năng nghiệp vụ chính

A. Đăng ký hệ thống

Người dùng tạo tài khoản mới để sử dụng hệ thống.

Quy trình nghiệp vụ

Người dùng nhập:

Họ và tên

Username

Email

Password

Confirm Password

Hệ thống kiểm tra:

Username đã tồn tại?

Email đã được sử dụng?

Password và Confirm Password có khớp?

Password có đủ độ mạnh?

Các trường có được nhập đầy đủ?

Nếu đúng:

Tạo tài khoản mới

Mã hóa mật khẩu (ASCII/Hash)

Lưu thông tin người dùng

Thông báo đăng ký thành công

Chuyển tới màn hình đăng nhập

Nếu sai:

Hiển thị lỗi tương ứng

Dữ liệu cần lưu

Thuộc tính

Ý nghĩa

UserID

Mã người dùng

FullName

Họ và tên

Username

Tên đăng nhập

Email

Email người dùng

Password

Mật khẩu mã hóa

RoleID

Quyền mặc định

Status

Hoạt động / chưa kích hoạt

CreatedDate

Ngày tạo tài khoản





B. Đăng nhập hệ thống

Người dùng sử dụng tài khoản và mật khẩu để truy cập hệ thống.

Quy trình nghiệp vụ

Người dùng nhập:

Username / Email

Password (mã hoá ASHII)

Hệ thống kiểm tra:

Tài khoản tồn tại?

Mật khẩu đúng?

Tài khoản còn hoạt động?

Nếu đúng:

Cho phép đăng nhập

Chuyển tới giao diện theo quyền

Nếu sai:

Hiển thị lỗi

Dữ liệu cần lưu

Thuộc tính

Ý nghĩa

UserID

Mã người dùng

Username

Tên đăng nhập

Password

Mật khẩu mã hóa

RoleID

Quyền người dùng

Status

Hoạt động / khóa





B. Quản lý tài khoản người dùng

Admin tạo và quản lý tài khoản.

Chức năng

Thêm tài khoản

Sửa thông tin

Khóa / mở khóa tài khoản

Đổi mật khẩu

Reset mật khẩu

Quy tắc nghiệp vụ

Username không được trùng

Email hợp lệ

Mật khẩu tối thiểu 8 ký tự

Chỉ Admin/Owner mới được phân quyền

C. Phân quyền hệ thống

Mỗi vai trò chỉ được sử dụng các chức năng được cấp phép.

Chức năng

Owner

Quản lý

Nhân viên bán hàng

Nhân viên kho

Quản lý sản phẩm

x

x









Xem báo cáo

x

x









Quản lý tài khoản

x













Bán hàng

x

x

x





Quản lý sản phẩm

x

x





x



Quy tắc nghiệp vụ

Không được truy cập trái quyền

Menu hiển thị theo quyền

API/backend cũng phải kiểm tra quyền

D. Quản lý chi nhánh/cửa hàng

Quản lý thông tin các cửa hàng trong chuỗi.

Chức năng

Thêm cửa hàng

Cập nhật thông tin

Gán nhân viên cho cửa hàng

Xoá cửa hàng ngừng hoạt động

Dữ liệu

Thuộc tính

Ý nghĩa

StoreID

Mã cửa hàng

StoreName

Tên cửa hàng

Address

Địa chỉ

Phone

SĐT

ManagerID

Người quản lý





E. Nhật ký hệ thống (Log)

Mục tiêu

Theo dõi lịch sử thao tác.

Hệ thống ghi nhận

Đăng nhập

Đăng xuất

Thêm/sửa/xóa dữ liệu

Dữ liệu log

Thuộc tính

Ý nghĩa

LogID

Mã log

UserID

Người thao tác

Action

Hành động

Time

Thời gian

IPAddress

Địa chỉ IP





F. Cấu hình nghiệp vụ

Chức năng

Cấu hình thuế VAT

Đơn vị tiền tệ

Logo công ty

Thông tin công ty

Chính sách tích điểm

1.4. Các yêu cầu phi chức năng

Yêu cầu

Mô tả

Bảo mật

Mật khẩu mã hóa…

Hiệu năng

Đăng nhập nhanh…

Khả năng mở rộng

Thêm vai trò dễ dàng

Backup 

Sao lưu dữ liệu (khi nào sao lưu)



1.5. Use Case chính

Use Case

Actor

Đăng nhập

Tất cả người dùng

Quản lý tài khoản

Admin

Phân quyền

Admin

Quản lý chi nhánh

Admin

Xem log hệ thống

Admin





F. Quản lý tài khoản

1\. Mục tiêu nghiệp vụ

Chức năng quản lý tài khoản giúp:

Quản lý thông tin người dùng trong hệ thống

Kiểm soát quyền truy cập

Đảm bảo bảo mật tài khoản

Hỗ trợ theo dõi nhân viên theo từng chi nhánh

Hạn chế truy cập trái phép

2\. Đối tượng sử dụng

Vai trò

Quyền

Admin

Toàn quyền quản lý tài khoản

Quản lý cửa hàng

Quản lý tài khoản nhân viên thuộc cửa hàng

Nhân viên

Chỉ được cập nhật thông tin cá nhân





3\. Các chức năng nghiệp vụ chính

A. Tạo tài khoản

Mô tả

Admin hoặc quản lý tạo tài khoản cho nhân viên sử dụng hệ thống.

Quy trình nghiệp vụ

Người quản trị chọn “Thêm tài khoản”

Nhập thông tin:

Username

Password

Họ tên

Email

SĐT

Vai trò

Chi nhánh

Hệ thống kiểm tra:

Username đã tồn tại chưa

Email hợp lệ không

Vai trò hợp lệ không

Nếu hợp lệ:

Tạo tài khoản

Gửi thông báo thành công

Quy tắc nghiệp vụ

Username là duy nhất

Password tối thiểu 8 ký tự

Password phải được mã hóa

Mỗi tài khoản thuộc ít nhất 1 vai trò

Nhân viên chỉ thuộc một chi nhánh

B. Cập nhật tài khoản

Chức năng

Sửa thông tin cá nhân

Đổi email

Đổi số điện thoại

Cập nhật vai trò

Quy tắc nghiệp vụ

Không được sửa Username

Chỉ Admin/Owner được đổi quyền

Email không được trùng

Không được sửa tài khoản đã khóa

C. Khóa / mở khóa tài khoản

Mô tả

Admin có thể vô hiệu hóa tài khoản khi:

Nhân viên nghỉ việc

Tài khoản vi phạm

Phát hiện truy cập bất thường

Quy trình nghiệp vụ

Admin chọn tài khoản

Chọn:

Khóa

Mở khóa

Hệ thống cập nhật trạng thái

Trạng thái tài khoản

Trạng thái

Ý nghĩa

Active

Đang hoạt động

Locked

Bị khóa

Inactive

Ngừng sử dụng





D. Đổi mật khẩu

Mô tả

Người dùng thay đổi mật khẩu cá nhân.

Quy trình nghiệp vụ

Nhập:

Mật khẩu cũ

Mật khẩu mới

Xác nhận mật khẩu

Hệ thống kiểm tra:

Mật khẩu cũ đúng?

Mật khẩu mới đủ mạnh?

Cập nhật mật khẩu mới

Quy tắc nghiệp vụ

Mật khẩu mới không trùng mật khẩu cũ

Có chữ hoa, chữ thường, số

Hết phiên đăng nhập sau khi đổi mật khẩu

E. Quên mật khẩu

Mô tả

Hỗ trợ người dùng lấy lại tài khoản.

Quy trình nghiệp vụ

Người dùng xác thực -> báo lên hệ thống -> Admin/Owner duyệt

Đặt mật khẩu mới

F. Tìm kiếm và lọc tài khoản

Chức năng

Tìm theo:

Username

Họ tên

Vai trò

Chi nhánh

Trạng thái

4\. Dữ liệu quản lý tài khoản

Thuộc tính

Ý nghĩa

UserID

Mã tài khoản

Username

Tên đăng nhập

Password

Mật khẩu mã hóa

FullName

Họ tên

Phone

SĐT

RoleID

Quyền

StoreID

Chi nhánh

Status

Trạng thái

CreatedDate

Ngày tạo





6\. Use Case quản lý tài khoản

Use Case

Actor

Đăng nhập

Tất cả người dùng

Tạo tài khoản

Admin/Owner

Khóa tài khoản

Admin/Owner

Phân quyền

Admin/Owner

Đổi mật khẩu

Người dùng

Reset mật khẩu

Admin/Owner







2\. Phân tích nghiệp vụ hệ thống báo cáo

2.1. Mục tiêu nghiệp vụ

Hệ thống báo cáo giúp:

Theo dõi hoạt động kinh doanh

Đánh giá hiệu quả bán hàng

Quản lý tồn kho

Theo dõi nhân viên

Hỗ trợ ra quyết địnhc

2.2. Các loại báo cáo chính

A. Báo cáo doanh số bán hàng

Mục tiêu

Theo dõi doanh thu của:

Toàn hệ thống

Từng chi nhánh

Theo thời gian

Chức năng

Lọc theo:

Ngày

Tháng

Năm

Khoảng thời gian

Chi nhánh

Xuất Excel/PDF

Biểu đồ doanh thu

Dữ liệu báo cáo

Thuộc tính

Ý nghĩa

ReportID

Báo cáo của doanh thu

StoreName

Chi nhánh

Revenue

Doanh thu

Profit

Lợi nhuận

Date

Ngày bán









B. Báo cáo doanh số theo nhân viên

Đánh giá hiệu quả bán hàng của nhân viên.

Chức năng

Xem doanh số theo:

Nhân viên

Thời gian

Cửa hàng

Xếp hạng nhân viên bán tốt

Dữ liệu báo cáo

Thuộc tính

Ý nghĩa

EmployeeID

Mã nhân viên

EmployeeName

Tên nhân viên

TotalInvoice

Số hóa đơn

Revenue

Doanh số





Quy tắc nghiệp vụ

Doanh số được tính theo hóa đơn đã thanh toán

Hóa đơn hủy không tính

Có thể tính KPI/thưởng

C. Báo cáo hàng tồn kho

Theo dõi số lượng hàng hóa còn trong kho.

Chức năng

Xem tồn kho:

Toàn hệ thống

Theo kho

Theo sản phẩm

Cảnh báo sắp hết hàng



Dữ liệu báo cáo

Thuộc tính

Ý nghĩa

ProductID

Mã sản phẩm

ProductName

Tên sản phẩm

Warehouse

Kho

ImportQty

Nhập

ExportQty

Xuất

InventoryQty

Tồn





Quy tắc nghiệp vụ

Không cho tồn kho âm

Đồng bộ dữ liệu realtime

Tự động cập nhật sau bán hàng

D. Báo cáo khách hàng thân thiết

Xác định khách hàng mua nhiều để chăm sóc.

Chức năng

Xếp hạng khách hàng

Thống kê:

Tổng chi tiêu

Số đơn hàng

Điểm tích lũy

Dữ liệu báo cáo

Thuộc tính

Ý nghĩa

CustomerID

Mã khách hàng

CustomerName

Tên khách

TotalOrder

Số đơn

TotalSpent

Tổng chi tiêu

Point

Điểm tích lũy





Quy tắc nghiệp vụ

Chỉ tính đơn hoàn thành

Có thể phân hạng:

Ngưỡng Phân Hạng Khách Hàng:

Hạng

Tổng chi tiêu tích luỹ

Quyền lợi khác

Thường

< 5.000.000đ

Không

Silver

5.000.000đ - 20.000.000đ

Ưu tiên đổi trả

Gold

20.000.000đ - 50.000.000đ

Ưu tiên đổi trả + quà sinh nhật

VIP

> 50.000.000đ

Tất cả + mua nợ



(Ngưỡng và % có thể cấu hình bởi Owner trong hệ thống)

2.3. Quy trình tạo báo cáo

Người dùng chọn loại báo cáo

Chọn điều kiện lọc

Hệ thống truy vấn dữ liệu

Tổng hợp thống kê

Hiển thị:

Bảng dữ liệu

Biểu đồ

Cho phép xuất file PDF/Word

2.4. Yêu cầu phi chức năng

Yêu cầu

Mô tả

Chính xác

Dữ liệu realtime

Nhanh

Truy vấn tối ưu..

Dễ đọc

Có biểu đồ

Bảo mật

Phân quyền xem báo cáo





2.5. Use Case hệ thống báo cáo

Use Case

Actor

Xem báo cáo doanh thu

Admin, Quản lý

Xem báo cáo tồn kho

Admin, Kho

Xem báo cáo nhân viên

Admin

Xuất báo cáo

Admin, Quản lý









