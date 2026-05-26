MODULE — QUẢN LÝ KHÁCH HÀNG

1\. BUSINESS REQUIREMENT

1.1 Business Context

Hệ thống quản lý khách hàng được xây dựng nhằm hỗ trợ:

quản lý thông tin khách hàng

lưu lịch sử mua hàng

theo dõi mức độ thân thiết của khách hàng

hỗ trợ chăm sóc khách hàng

hỗ trợ chương trình tích điểm và ưu đãi

Hiện tại việc quản lý khách hàng đang thực hiện thủ công hoặc phân tán giữa các chi nhánh, gây:

khó theo dõi lịch sử mua hàng

khó chăm sóc khách hàng thân thiết

thất thoát dữ liệu khách hàng

khó triển khai chương trình marketing

khó phân tích hành vi mua hàng



1.2 Business Objective

Mã

Mục tiêu

BO-KH-01

Quản lý tập trung dữ liệu khách hàng

BO-KH-02

Theo dõi lịch sử mua hàng

BO-KH-03

Hỗ trợ chăm sóc khách hàng

BO-KH-04

Quản lý khách hàng thân thiết

BO-KH-05

Hỗ trợ chương trình tích điểm





2\. SCOPE

2.1 In Scope

Quản lý thông tin khách hàng

Quản lý lịch sử mua hàng

Quản lý điểm tích lũy

Phân loại khách hàng

Theo dõi khách hàng thân thiết

Tìm kiếm khách hành



3\. ACTOR

Actor

Vai trò

Admin

Quản lý toàn hệ thống

Owner

Quản lý khách hàng toàn chi nhánh

Store Manager

Quản lý khách hàng tại chi nhánh

Sales Staff

Tạo và cập nhật khách hàng

Warehouse Staff

Quản lý kho hàng





4\. ROLE PERMISSION

Chức năng

Owner

Store Manager

Sales Staff

Xem toàn bộ khách hàng

✅

❌

❌

Quản lý khách hàng chi nhánh

✅

✅

✅

Tạo khách hàng

✅

✅

✅

Cập nhật khách hàng

✅

✅

✅

Xóa khách hàng

❌

❌

❌

Xem lịch sử mua hàng

✅

✅

✅

Export báo cáo

✅

✅

❌





5\. CUSTOMER MANAGEMENT

5.1 Thông tin khách hàng

Thông tin

Mô tả

Mã khách hàng

Mã định danh khách hàng

Họ tên

Tên khách hàng

Số điện thoại

Liên hệ khách hàng

Email

Email khách hàng

Địa chỉ

Địa chỉ khách hàng

Ngày sinh

Ngày sinh khách hàng

Giới tính

Nam/Nữ/Khác

Điểm tích lũy

Điểm thưởng khách hàng

Hạng thành viên

Silver/Gold/Platinum





5.2 Phân loại khách hàng

Loại

Mô tả

Khách mới

Mới phát sinh giao dịch

Silver

Có giao dịch định kỳ

Gold

Doanh số cao

Platinum

Tích lũy nhiều điểm





5.3 Điểm tích lũy

Business Description

Hệ thống hỗ trợ:

cộng điểm khi mua hàng

sử dụng điểm đổi ưu đãi

nâng hạng khách hàng

theo dõi điểm tích lũy



Business Formula — Loyalty Point

Điểm tích lũy = Giá trị đơn hàng​/ đơn vị tích điểm



6\. BUSINESS FLOW

6.1 Business Flow — Tạo khách hàng

Khách hàng phát sinh giao dịch

↓

Nhân viên nhập thông tin khách hàng

↓

Hệ thống lưu dữ liệu khách hàng

↓

Khách hàng được tạo thành công





6.2 Business Flow — Tích điểm khách hàng

Khách hàng mua hàng

↓

Hệ thống ghi nhận hóa đơn

↓

Hệ thống cộng điểm tích lũy

↓

Cập nhật hạng khách hàng

↓

Hiển thị lịch sử tích điểm





7\. FUNCTIONAL REQUIREMENT

ID

Functional Requirement

FR-KH-01

Hệ thống cho phép tạo khách hàng

FR-KH-02

Hệ thống cho phép cập nhật khách hàng

FR-KH-03

Hệ thống cho phép xem danh sách khách hàng

FR-KH-04

Hệ thống cho phép tìm kiếm khách hàng

FR-KH-05

Hệ thống cho phép xem lịch sử mua hàng

FR-KH-06

Hệ thống hỗ trợ tích điểm

FR-KH-07

Hệ thống hỗ trợ phân loại khách hàng

FR-KH-08

Hệ thống hiển thị khách hàng thân thiết

FR-KH-09

Hệ thống hỗ trợ export báo cáo

FR-KH-10

Hệ thống quản lý khách hàng theo chi nhánh





8\. BUSINESS RULE

Rule ID

Business Rule

BR-KH-01

Mỗi khách hàng phải có mã khách hàng

BR-KH-02

Số điện thoại, email khách hàng không được trùng

BR-KH-03

Mọi khách hàng phải có thời gian tạo

BR-KH-04

Điểm tích lũy không được âm

BR-KH-05

Hệ thống phải lưu lịch sử mua hàng

BR-KH-06

Hệ thống phải hỗ trợ phân loại khách hàng

BR-KH-07

Hệ thống phải quản lý khách hàng theo chi nhánh





9\. USE CASE

9.1 Use Case — Tạo khách hàng

Thành phần

Nội dung

Use Case ID

UC-KH-01

Use Case Name

Tạo khách hàng

Actor

Sales Staff, Store Manager

Preconditions

Người dùng đã đăng nhập

Main Flow

Nhập thông tin → Lưu khách hàng

Postconditions

Khách hàng được tạo





9.2 Use Case — Xem lịch sử mua hàng

Thành phần

Nội dung

Use Case ID

UC-KH-02

Use Case Name

Xem lịch sử mua hàng

Actor

Sales Staff, Store Manager, Owner

Preconditions

Khách hàng tồn tại

Main Flow

Chọn khách hàng → Hiển thị lịch sử

Postconditions

Hiển thị danh sách hóa đơn





10\. DASHBOARD

Widget

Tổng số khách hàng

Khách hàng mới hôm nay

Khách hàng thân thiết

Tổng điểm tích lũy

Top chi tieu khach hang    

Tần suất mua hàng



















