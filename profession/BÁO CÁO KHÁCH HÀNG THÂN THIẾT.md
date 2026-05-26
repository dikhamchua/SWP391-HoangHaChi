MODULE — BÁO CÁO KHÁCH HÀNG THÂN THIẾT

1\. BUSINESS REQUIREMENT

1.1 Business Context

Hệ thống báo cáo khách hàng thân thiết được xây dựng nhằm hỗ trợ:

·        Xác định khách hàng mua nhiều để chăm sóc

·        Xếp hạng khách hàng theo mức độ thân thiết

·        Hỗ trợ chính sách chăm sóc khách hàng VIP

·        Theo dõi điểm tích lũy

1.2 Business Objective

Mã

Mục tiêu

BO-KHTT-01

Xác định khách hàng thân thiết

BO-KHTT-02

Xếp hạng khách hàng theo giá trị

BO-KHTT-03

Hỗ trợ chính sách chăm sóc khách hàng

BO-KHTT-04

Theo dõi điểm tích lũy



2\. SCOPE

2.1 In Scope

·        Báo cáo xếp hạng khách hàng thân thiết

·        Thống kê tổng chi tiêu

·        Thống kê số đơn hàng

·        Theo dõi điểm tích lũy

·        Phân loại: Silver/Gold/Platinum

·        Dashboard khách hàng thân thiết

3\. ACTOR

Actor

Vai trò

System Admin

Xem toàn bộ báo cáo

Owner

Xem khách hàng thân thiết toàn chi nhánh

Store Manager

Xem khách hàng thân thiết chi nhánh



4\. ROLE PERMISSION



Chức năng

Owner

Store Manager

Xem toàn bộ khách hàng thân thiết

✓

✗

Xem khách hàng thân thiết chi nhánh

✓

✓

Xem chi tiết khách hàng

✓

✓

Export báo cáo

✓

✓

Xem dashboard

✓

✓



5\. LOYAL CUSTOMER REPORT

5.1 Thông tin báo cáo

Thuộc tính

Ý nghĩa

CustomerID

Mã khách hàng

CustomerName

Tên khách hàng

Phone

Số điện thoại

Email

Email (null)

TotalOrder

Tổng số đơn hàng

TotalSpent

Tổng chi tiêu

Point

Điểm tích lũy

MembershipTier

Hạng thành viên



5.2 Phân hạng khách hàng

Hạng

Điều kiện

Silver

Tổng chi tiêu: 1.000.000 - 5.000.000 VNĐ

Gold

Tổng chi tiêu: 5.000.001 - 20.000.000 VNĐ

Platinum

Tổng chi tiêu: > 20.000.000 VNĐ



5.3 Business Formula — Loyalty Point

Điểm tích lũy = Giá trị đơn hàng / 1000

6\. BUSINESS FLOW

6.1 Business Flow — Báo cáo khách hàng thân thiết

Khách hàng mua hàng

↓

Hệ thống ghi nhận hóa đơn

↓

Cập nhật tổng chi tiêu và số đơn

↓

Tính điểm tích lũy

↓

Cập nhật hạng thành viên

↓

Hiển thị trong báo cáo

7\. FUNCTIONAL REQUIREMENT

ID

Functional Requirement

FR-KHTT-01

Hệ thống hiển thị danh sách khách hàng thân thiết

FR-KHTT-02

Hệ thống xếp hạng khách hàng theo tổng chi tiêu

FR-KHTT-03

Hệ thống hiển thị tổng số đơn hàng

FR-KHTT-04

Hệ thống hiển thị điểm tích lũy

FR-KHTT-05

Hệ thống phân loại: Silver/Gold/Platinum

FR-KHTT-06

Hệ thống cho phép lọc theo hạng thành viên

FR-KHTT-07

Hệ thống hỗ trợ export báo cáo



8\. BUSINESS RULE

Rule ID

Business Rule

BR-KHTT-01

Chỉ tính đơn hàng đã hoàn thành

BR-KHTT-02

Không tính đơn hàng bị hủy

BR-KHTT-03

Điểm tích lũy không được âm

BR-KHTT-04

Hạng thành viên tự động cập nhật khi đạt ngưỡng

BR-KHTT-05

Tổng chi tiêu tính từ tất cả chi nhánh



9\. USE CASE

9.1 Use Case — Xem báo cáo khách hàng thân thiết

Thành phần

Nội dung

Use Case ID

UC-KHTT-01

Use Case Name

Xem báo cáo khách hàng thân thiết

Actor

Admin, Branch Owner, Manager

Preconditions

Người dùng đã đăng nhập

Main Flow

Chọn bộ lọc → Hệ thống tổng hợp → Hiển thị báo cáo

Postconditions

Báo cáo được hiển thị



10\. DASHBOARD

Widget

·        Tổng số khách hàng thân thiết

·        Khách hàng Platinum

·        Khách hàng Gold

·        Khách hàng Silver

·        Top 10 khách hàng chi tiêu cao

·        Tổng điểm tích lũy





