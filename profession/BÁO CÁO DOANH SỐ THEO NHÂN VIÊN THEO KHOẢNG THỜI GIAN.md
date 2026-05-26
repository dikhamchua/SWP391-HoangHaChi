MODULE 6 — BÁO CÁO DOANH SỐ THEO NHÂN VIÊN THEO KHOẢNG THỜI GIAN



1\. BUSINESS REQUIREMENT

1.1 Business Context

Hệ thống báo cáo doanh số nhân viên được xây dựng nhằm hỗ trợ cho chuỗi các cửa hàng bán lẻ:

theo dõi doanh số bán hàng

đánh giá hiệu suất nhân viên

hỗ trợ quản lý KPI

hỗ trợ theo dõi doanh thu theo chi nhánh





1.2 Business Objective

Mã

Mục tiêu

BO-BC-01

Theo dõi doanh số nhân viên

BO-BC-02

Đánh giá hiệu suất bán hàng

BO-BC-03

Theo dõi KPI

BO-BC-04

Hỗ trợ quản lý doanh thu theo chi nhánh

BO-BC-05

Hỗ trợ chính sách thưởng/phạt



2\. SCOPE

2.1 In Scope

Báo cáo doanh số theo nhân viên

Báo cáo theo khoảng thời gian

Báo cáo theo chi nhánh

Dashboard doanh số

Top nhân viên bán hàng

3\. ACTOR

Actor

Vai trò

Owner

Theo dõi doanh số chi nhánh

Store Manager

Theo dõi hiệu suất nhân viên

Sales Staff

Xem doanh số cá nhân





4\. ROLE PERMISSION

Chức năng

Owner

StoreManager

Sales Staff

Xem toàn hệ thống

✅❌

❌

❌

Xem doanh số chi nhánh

✅

✅

❌

Xem doanh số nhân viên

✅

✅

Cá nhân

Export báo cáo

✅

✅

❌

Xem dashboard

✅

✅

❌





5\. REPORT LOGIC

5.1 Logic tính doanh số

Điều kiện

Chỉ tính đơn hàng hoàn thành

Không tính đơn bị hủy

Mỗi hóa đơn thuộc một nhân viên

Doanh số thuộc đúng chi nhánh







5.2 Công thức doanh số

Tổng doanh số = ∑ Giá trị hóa đơn hợp lệ



5.3 Công thức doanh số trung bình

Doanh số trung bình trên đơn hàng =  Tổng doanh số /Tổng đơn hàng



6\. BUSINESS FLOW

6.1 Business Flow — Báo cáo doanh số nhân viên

Nhân viên tạo hóa đơn bán hàng

↓

Hệ thống ghi nhận doanh số

↓

Quản lý chọn khoảng thời gian

↓

Hệ thống tổng hợp dữ liệu

↓

Hiển thị báo cáo doanh số nhân viên





7\. THÔNG TIN BÁO CÁO

Thông tin

Tên nhân viên

Chi nhánh

Tổng doanh số

Tổng đơn hàng

Tổng sản phẩm bán

Doanh số trung bình

Đơn hủy



8\. BỘ LỌC BÁO CÁO

Bộ lọc

Theo ngày

Theo tuần

Theo tháng

Theo khoảng thời gian

Theo chi nhánh

Theo nhân viên



9\. FUNCTIONAL REQUIREMENT

ID

Functional Requirement

FR-BC-01

Hệ thống cho phép xem doanh số theo nhân viên

FR-BC-02

Hệ thống cho phép lọc theo ngày

FR-BC-03

Hệ thống cho phép lọc theo tuần

FR-BC-04

Hệ thống cho phép lọc theo tháng

FR-BC-05

Hệ thống cho phép lọc theo khoảng thời gian

FR-BC-06

Hệ thống cho phép lọc theo chi nhánh

FR-BC-07

Hệ thống hiển thị tổng doanh số

FR-BC-08

Hệ thống hiển thị số lượng đơn hàng

FR-BC-09

Hệ thống hiển thị top nhân viên bán tốt

FR-BC-10

Hệ thống hỗ trợ export báo cáo









10\. BUSINESS RULE

Rule ID

Business Rule

BR-BC-01

Chỉ tính đơn hàng hoàn thành

BR-BC-02

Không tính đơn hàng bị hủy

BR-BC-03

Mỗi hóa đơn chỉ thuộc một nhân viên

BR-BC-04

Báo cáo phải hỗ trợ lọc theo thời gian

BR-BC-05

Nhân viên chỉ được xem doanh số cá nhân





11\. USE CASE

11.1 Use Case — Xem Báo Cáo Doanh Số Nhân Viên

Thành phần

Nội dung

Use Case ID

UC-BC-01

Use Case Name

Xem báo cáo doanh số nhân viên

Actor

Admin, Branch Owner, Manager

Preconditions

Có dữ liệu doanh số

Main Flow

Chọn thời gian → Hệ thống tổng hợp → Hiển thị báo cáo

Postconditions

Báo cáo được hiển thị



12\. DASHBOARD

Widget

Tổng doanh số

Tổng số đơn hàng

Top nhân viên bán tốt

Doanh số theo thời gian

Doanh số theo chi nhánh

Doanh số trung bình nhân viên









