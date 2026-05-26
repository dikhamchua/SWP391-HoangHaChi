MODULE — QUẢN LÝ MUA HÀNG (NHẬP HÀNG)

1\. BUSINESS REQUIREMENT

1.1 Business Context

Hệ thống quản lý mua hàng được xây dựng nhằm hỗ trợ:

·        Quản lý nhà cung cấp

·        Tạo đơn đặt hàng từ nhà cung cấp

·        Quản lý nhập hàng vào kho

·        Theo dõi thanh toán cho nhà cung cấp

·        Theo dõi công nợ nhà cung cấp

·        Báo cáo nhập hàng

1.2 Business Objective

Mã

Mục tiêu

BO-MH-01

Quản lý thông tin nhà cung cấp

BO-MH-02

Quản lý đơn đặt hàng

BO-MH-03

Quản lý nhập hàng vào kho

BO-MH-04

Theo dõi thanh toán nhà cung cấp

BO-MH-05

Theo dõi công nợ



2\. SCOPE

2.1 In Scope

·        Quản lý nhà cung cấp

·        Tạo đơn đặt hàng (Purchase Order)

·        Nhập hàng vào kho

·        Thanh toán cho nhà cung cấp

·        Theo dõi công nợ

·        Báo cáo nhập hàng

·        Dashboard mua hàng







3\. ACTOR

Actor

Vai trò

Admin

Quản lý toàn hệ thống

Owner

Quản lý mua hàng toàn chi nhánh

Store Manager

Quản lý mua hàng chi nhánh

Warehouse Staff

Nhập hàng vào kho



4\. ROLE PERMISSION

Chức năng

Owner

Store Manager

Warehouse Staff

Quản lý nhà cung cấp

✓

✓

✗

Tạo đơn đặt hàng

✓

✓

✓

Nhập hàng

✓

✓

✓

Thanh toán NCC

✓

✓

✗

Xem dư nợ

✓

✓

✗

Xem báo cáo

✓

✓

✗



5\. QUẢN LÝ NHÀ CUNG CẤP

5.1 Thông tin nhà cung cấp

Thuộc tính

Ý nghĩa

SupplierID

Mã nhà cung cấp

SupplierName

Tên nhà cung cấp

Phone

Số điện thoại

Email

Email

Address

Địa chỉ

TaxCode

Mã số thuế

ContactPerson

Người liên hệ

PaymentTerm

Điều khoản thanh toán

Status

Trạng thái (Hoạt động/Ngừng)



5.2 Chức năng

·        Thêm nhà cung cấp

·        Cập nhật thông tin

·        Khóa/mở khóa nhà cung cấp

·        Xem lịch sử giao dịch

·        Xem công nợ

6\. QUẢN LÝ ĐƠN ĐẶT HÀNG

6.1 Thông tin đơn đặt hàng

Thuộc tính

Ý nghĩa

PurchaseOrderID

Mã đơn đặt hàng

SupplierID

Nhà cung cấp

OrderDate

Ngày đặt hàng

ExpectedDate

Ngày dự kiến nhận

TotalAmount

Tổng tiền

StatusType

Trạng thái (Chờ duyệt/Đã duyệt/Đã nhận/Hủy)

CreatedBy

Người tạo

Note

Ghi chú



6.2 Chi tiết đơn đặt hàng

Thuộc tính

Ý nghĩa

ProductID

Mã sản phẩm

PurchaseOrderID

Mã đơn đặt hàng

ProductName

Tên sản phẩm

Quantity

Số lượng đặt

UnitPrice

Đơn giá

TotalPrice

Thành tiền



6.3 Trạng thái đơn hàng

Trạng thái

Mô tả

Chờ duyệt

Đơn hàng mới tạo

Đã duyệt

Đã xác nhận với NCC

Đang giao

NCC đang giao hàng

Đã nhận

Đã nhập kho

Hủy

Đơn hàng bị hủy



7\. QUẢN LÝ NHẬP HÀNG

7.1 Thông tin phiếu nhập

Thuộc tính

Ý nghĩa

ImportID

Mã phiếu nhập

PurchaseOrderID

Mã đơn đặt hàng

SupplierID

Nhà cung cấp

ImportDate

Ngày nhập

WarehouseID

Kho nhập

TotalAmount

Tổng tiền

Status

Trạng thái

CreatedBy

Người tạo

Note

Ghi chú



7.2 Chi tiết phiếu nhập

Thuộc tính

Ý nghĩa

ProductID

Mã sản phẩm

ImportID

Mã phiếu nhập

Quantity

Số lượng nhập

UnitPrice

Đơn giá

TotalPrice

Thành tiền

ExpiryDate

Hạn sử dụng (nếu có)



7.3 Trạng thái phiếu

Trạng thái

Mô tả

Thành công

Nhân viên kho đã kiểm đủ số lượng 

Thất bại

Hàng bị thiếu, thừa ,....







8\. THANH TOÁN NHÀ CUNG CẤP

8.1 Thông tin thanh toán

Thuộc tính

Ý nghĩa

PaymentID

Mã thanh toán

SupplierID

Nhà cung cấp

ImportID

Phiếu nhập

PaymentDate

Ngày thanh toán

Amount

Số tiền thanh toán

PaymentMethod

Phương thức (Tiền mặt/Chuyển khoản)

Note

Ghi chú



8.2 Công nợ nhà cung cấp

Công nợ = Tổng tiền nhập - Tổng tiền đã thanh toán

9\. BUSINESS FLOW

9.1 Business Flow — Đặt hàng và nhập kho

Tạo đơn đặt hàng

↓

Gửi đơn cho store manager/ower

↓

Gửi đơn cho nhà cung cấp( nghiệp vụ ngoài hệ thống)

↓

Nhà cung cấp xác nhận

↓

Nhà cung cấp giao hàng

↓

Nhân viên kho kiểm tra và nhập kho

↓

Cập nhật tồn kho

↓

Tạo phiếu thanh toán

9.2 Business Flow — Thanh toán nhà cung cấp

Nhập hàng hoàn tất

↓

Hệ thống ghi nhận công nợ

↓

Store manager/owner tạo phiếu thanh toán

↓

Thanh toán cho nhà cung cấp

↓

Cập nhật công nợ

10\. FUNCTIONAL REQUIREMENT

ID

Functional Requirement

FR-MH-01

Hệ thống cho phép quản lý nhà cung cấp

FR-MH-02

Hệ thống cho phép tạo đơn đặt hàng

FR-MH-03

Hệ thống cho phép nhập hàng vào kho

FR-MH-04

Hệ thống cho phép thanh toán nhà cung cấp

FR-MH-05

Hệ thống hiển thị công nợ nhà cung cấp

FR-MH-06

Hệ thống cho phép tìm kiếm đơn hàng

FR-MH-07

Hệ thống hỗ trợ export báo cáo

FR-MH-08

Hệ thống cập nhật tồn kho tự động



11\. BUSINESS RULE

Rule ID

Business Rule

BR-MH-01

Mọi đơn đặt hàng phải có nhà cung cấp

BR-MH-02

Số lượng nhập phải > 0

BR-MH-03

Đơn giá phải >= 0

BR-MH-04

Phải kiểm tra hàng trước khi nhập kho

BR-MH-05

Tự động cập nhật tồn kho sau khi nhập

BR-MH-06

Công nợ không được âm

BR-MH-07

Chỉ nhập hàng từ đơn đã duyệt



12\. USE CASE

12.1 Use Case — Tạo đơn đặt hàng

Thành phần

Nội dung

Use Case ID

UC-MH-01

Use Case Name

Tạo đơn đặt hàng

Actor

Branch Manager, Warehouse Staff,Owner

Preconditions

Người dùng đã đăng nhập

Main Flow

Chọn sản phẩm → Chọn NCC → Nhập số lượng → Tạo đơn

Postconditions

Đơn đặt hàng được tạo



12.2 Use Case — Nhập hàng vào kho

Thành phần

Nội dung

Use Case ID

UC-MH-02

Use Case Name

Nhập hàng vào kho

Actor

Warehouse Staff

Preconditions

Có đơn đặt hàng đã duyệt

Main Flow

Chọn đơn hàng → Kiểm tra hàng → Kiểm tra số lượng → Lưu phiếu nhập

Postconditions

Hàng được nhập kho, tồn kho cập nhật



13\. DASHBOARD

Widget

·        Tổng đơn đặt hàng hôm nay

·        Tổng tiền nhập hàng tháng

·        Tổng công nợ nhà cung cấp

·        Đơn hàng chờ duyệt

·        Đơn hàng đang giao

·        Top nhà cung cấp

·        Sản phẩm sắp hết hàng





