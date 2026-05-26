MODULE 5 — QUẢN LÝ THU CHI

1\. BUSINESS REQUIREMENT

1.1 Business Context

Hệ thống quản lý thu chi được xây dựng nhằm hỗ trợ quản lý các khoản thu và chi phát sinh trong hoạt động kinh doanh của chuỗi cửa hàng bán lẻ.

Hệ thống giúp:

theo dõi dòng tiền tại từng cửa hàng

kiểm soát chi phí vận hành

quản lý giao dịch tài chính

hỗ trợ theo dõi lợi nhuận cơ bản

hỗ trợ tổng hợp báo cáo tài chính

Hiện tại việc quản lý tài chính đang thực hiện thủ công hoặc thiếu đồng bộ giữa các cửa hàng, gây ra:

khó kiểm soát dòng tiền

dễ thất thoát tài chính

khó theo dõi chi phí

khó tổng hợp báo cáo tài chính

mất nhiều thời gian kiểm tra dữ liệu



1.2 Business Objective

Mã

Mục tiêu

BO-TC-01

Theo dõi dòng tiền thu và chi

BO-TC-02

Kiểm soát chi phí vận hành

BO-TC-03

Giảm thất thoát tài chính

BO-TC-04

Hỗ trợ theo dõi lợi nhuận

BO-TC-05

Quản lý tài chính theo cửa hàng

BO-TC-06

Hỗ trợ tổng hợp báo cáo tài chính





2\. SCOPE

2.1 In Scope

Quản lý khoản thu

Quản lý khoản chi

Theo dõi giao dịch theo cửa hàng

Quản lý hàng ký gửi (Phần trăm bán được từ mặt hàng ký gửi)

Theo dõi VAT cơ bản

Theo dõi lợi nhuận cơ bản

Dashboard thu chi

Export báo cáo thu chi



3\. ACTOR

Actor

Vai trò

Admin

Quản lý toàn bộ hệ thống 

Owner

Theo dõi tình hình tài chính toàn chuỗi và phân quyền

Store Manager

Quản lý thu chi tại cửa hàng

Sales Staff

Tạo giao dịch thu cơ bản

Warehouse Staff

Tạo giao dịch chi liên quan nhập hàng/kho













4\. ROLE PERMISSION

Chức năng

Owner

Store Manager

Sales Staff

Warehouse Staff

Xem toàn bộ thu chi

✅

❌

❌

❌

Xem thu chi cửa hàng mình

✅

✅

❌

❌

Tạo phiếu thu

✅

✅

✅

❌

Tạo phiếu chi

✅

✅

❌

✅

Xem danh sách giao dịch

✅

✅

✅

✅

Tìm kiếm giao dịch

✅

✅

✅

✅

Lọc giao dịch theo thời gian

✅

✅

✅

✅

Xóa giao dịch

❌

❌

❌

❌

Export báo cáo

✅

✅

❌

❌

Xem dashboard tài chính

✅

✅

❌

❌





5\. THU CHI

5.1 Các khoản thu

Loại thu

Mô tả

Thu bán hàng

Doanh thu từ bán sản phẩm

Thu công nợ khách hàng

Khách hàng thanh toán công nợ







5.2 Các khoản chi

Loại chi

Mô tả

Chi nhập hàng

Nhập sản phẩm từ nhà cung cấp

Chi lương nhân viên

Trả lương/thưởng cho nhân viên

Chi vận chuyển

Chi phí vận chuyển hàng hóa

Chi marketing

Chi phí quảng cáo, khuyến mãi

Chi hoàn tiền

Refund khách hàng

Chi vận hành

Điện, nước, internet, mặt bằng

Chi khác

Các khoản chi khác





5.3 Phương thức thanh toán

Phương thức

&#x20;- Tiền mặt

&#x20;- Chuyển khoản





6\. VAT

6.1 Business Description

Hệ thống hỗ trợ:

tính VAT cơ bản

hiển thị VAT trên hóa đơn

theo dõi doanh thu có VAT

hỗ trợ thống kê VAT trong báo cáo





6.2 Business Formula — VAT

Tổng thanh toán = Gtri đơn hàng +(Gtri đơn hàng x %VAT)

7\. PROFIT

7.1 Business Description

Hệ thống hỗ trợ:

thống kê tổng thu

thống kê tổng chi

tính lợi nhuận cơ bản

theo dõi chênh lệch thu chi



7.2 Business Formula — Profit

Lợi nhuận = Tổng thu - Tổng chi



8\. BUSINESS FLOW

8.1 Business Flow — Thu Chi



Tạo phiếu chi

Tạo phiếu thu

Giao dịch phát sinh

↓

Nhân viên tạo phiếu chi

↓

Quản lý cửa hàng kiểm tra giao dịch

↓

Hệ thống ghi nhận dữ liệu tài chính

↓

Cập nhật dashboard và báo cáo

↓

Owner theo dõi tình hình tài chính





Giao dịch phát sinh

↓

Nhân viên tạo phiếu thu

↓

Hệ thống ghi nhận dữ liệu tài chính

↓

Cập nhật dashboard và báo cáo

↓

Quản lý cửa hàng kiểm tra giao dịch

↓

Owner theo dõi tình hình tài chính



9\. FUNCTIONAL REQUIREMENT

ID

Functional Requirement

FR-TC-01

Hệ thống cho phép tạo phiếu thu

FR-TC-02

Hệ thống cho phép tạo phiếu chi

FR-TC-03

Hệ thống cho phép xem danh sách giao dịch

FR-TC-04

Hệ thống cho phép tìm kiếm giao dịch

FR-TC-05

Hệ thống cho phép lọc giao dịch theo thời gian

FR-TC-06

Hệ thống hiển thị tổng thu

FR-TC-07

Hệ thống hiển thị tổng chi

FR-TC-08

Hệ thống hiển thị chênh lệch thu chi

FR-TC-09

Hệ thống hỗ trợ export báo cáo

FR-TC-10

Hệ thống quản lý giao dịch theo cửa hàng

FR-TC-12

Hệ thống cho phép quản lý xác nhận giao dịch

FR-TC-13

Hệ thống lưu lịch sử chỉnh sửa giao dịch





10\. BUSINESS RULE

Rule ID

Business Rule

BR-TC-01

Mọi giao dịch phải có số tiền

BR-TC-02

Mọi giao dịch phải có loại giao dịch

BR-TC-03

Mọi giao dịch phải có thời gian tạo

BR-TC-04

Mọi giao dịch phải thuộc một cửa hàng cụ thể

BR-TC-05

Hệ thống phải lưu lịch sử giao dịch

BR-TC-08

Chỉ người có quyền mới được xóa giao dịch

BR-TC-09

Hệ thống phải lưu thông tin người tạo giao dịch





11\. USE CASE

11.1 Use Case — Tạo Phiếu Thu

Thành phần

Nội dung

Use Case ID

UC-TC-01

Use Case Name

Tạo phiếu thu

Actor

Sales Staff, Store Manager

Preconditions

Người dùng đã đăng nhập

Main Flow

Nhập thông tin → Lưu giao dịch

Postconditions

Phiếu thu được lưu vào hệ thống



11.2 Use Case — Tạo Phiếu Chi

Thành phần

Nội dung

Use Case ID

UC-TC-02

Use Case Name

Tạo phiếu chi

Actor

Warehouse Staff, Store Manager

Preconditions

Người dùng đã đăng nhập

Main Flow

Nhập thông tin → Lưu giao dịch

Postconditions

Phiếu chi được lưu vào hệ thống





12\. DASHBOARD

Widget

Tổng thu hôm nay

Tổng chi hôm nay

Tổng doanh thu

Tổng chi phí

Chênh lệch thu chi

Tổng VAT

Lợi nhuận tạm tính







