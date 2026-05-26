MODULE — BÁO CÁO DOANH SỐ BÁN HÀNG

1\. BUSINESS REQUIREMENT

1.1 Business Context

Hệ thống báo cáo doanh số được xây dựng nhằm hỗ trợ:

theo dõi doanh số bán hàng

thống kê doanh thu theo chi nhánh

đánh giá hiệu quả kinh doanh

theo dõi hiệu suất nhân viên

hỗ trợ ra quyết định quản lý

Hiện tại việc tổng hợp doanh số còn thủ công hoặc rời rạc giữa các chi nhánh, gây:

khó theo dõi hiệu quả kinh doanh

chậm tổng hợp báo cáo

khó đánh giá hiệu suất nhân viên

khó so sánh doanh số giữa các chi nhánh



1.2 Business Objective

Mã

Mục tiêu

BO-BC-01

Theo dõi doanh số toàn hệ thống

BO-BC-02

Theo dõi doanh số từng chi nhánh

BO-BC-03

Đánh giá hiệu quả bán hàng

BO-BC-04

Hỗ trợ phân tích doanh thu

BO-BC-05

Hỗ trợ ra quyết định quản lý





2\. SCOPE

2.1 In Scope

Báo cáo doanh số toàn hệ thống

Báo cáo doanh số theo chi nhánh

Báo cáo doanh số theo thời gian

Báo cáo doanh số theo nhân viên

Dashboard doanh thu

Export báo cáo Excel/PDF









3\. ACTOR

Actor

Vai trò

Owner

Xem báo cáo toàn chi nhánh

Store Manager

Xem báo cáo chi nhánh

Sales Staff

Xem doanh số cá nhân





4\. ROLE PERMISSION

Chức năng

Admin

Owner

Store Manager

Sales Staff

Xem toàn bộ doanh số

✅

✅

❌

❌

Xem doanh số chi nhánh

✅

✅

✅

❌

Xem doanh số nhân viên

✅

✅

✅

✅

Export báo cáo

✅

✅

✅

❌

Xem dashboard doanh thu

✅

✅

✅

❌





5\. SALES REPORT

5.1 Báo cáo doanh số

Loại báo cáo

Mô tả

Doanh số toàn hệ thống

Tổng doanh thu tất cả chi nhánh

Doanh số theo chi nhánh

Doanh thu từng điểm bán

Doanh số theo nhân viên

Hiệu suất bán hàng

Doanh số theo thời gian

Theo ngày/tháng/năm





5.2 Theo khoảng thời gian

Khoảng thời gian

Hôm nay

7 ngày gần nhất

Tháng hiện tại

Quý hiện tại

Năm hiện tại

Tùy chọn khoảng thời gian





5.3 KPI doanh số

Doanh thu= ∑ (Giá trị hóa đơn)

Business Formula — Average Revenue

Doanh thu trung bình = Tổng doanh thu trong 1 khoảng thời gian / Khoảng thời gian



6\. BUSINESS FLOW

6.1 Business Flow — Báo cáo doanh số

Hệ thống ghi nhận hóa đơn bán hàng

↓

Lưu doanh thu theo chi nhánh

↓

Tổng hợp dữ liệu doanh số

↓

Sinh báo cáo theo thời gian

↓

Hiển thị dashboard và biểu đồ





7\. FUNCTIONAL REQUIREMENT

ID

Functional Requirement

FR-BC-01

Hệ thống hiển thị doanh số toàn hệ thống

FR-BC-02

Hệ thống hiển thị doanh số theo chi nhánh

FR-BC-03

Hệ thống hiển thị doanh số theo nhân viên

FR-BC-04

Hệ thống hỗ trợ lọc theo thời gian

FR-BC-05

Hệ thống hiển thị biểu đồ doanh thu

FR-BC-06

Hệ thống hỗ trợ export báo cáo

FR-BC-07

Hệ thống hỗ trợ tìm kiếm báo cáo

FR-BC-08

Hệ thống hỗ trợ dashboard doanh thu





8\. BUSINESS RULE

Rule ID

Business Rule

BR-BC-01

Mọi hóa đơn phải thuộc một chi nhánh

BR-BC-02

Mọi hóa đơn phải có thời gian tạo

BR-BC-03

Doanh thu chỉ tính hóa đơn đã thanh toán

BR-BC-04

Hệ thống phải lưu lịch sử doanh số

BR-BC-05

Hệ thống phải hỗ trợ lọc theo thời gian

BR-BC-06

Hệ thống phải hỗ trợ export báo cáo





9\. USE CASE

9.1 Use Case — Xem doanh số toàn hệ thống

Thành phần

Nội dung

Use Case ID

UC-BC-01

Use Case Name

Xem doanh số toàn hệ thống

Actor

Owner

Preconditions

Người dùng đã đăng nhập

Main Flow

Chọn khoảng thời gian → Hiển thị báo cáo

Postconditions

Hiển thị doanh thu tổng





9.2 Use Case — Xem doanh số theo chi nhánh

Thành phần

Nội dung

Use Case ID

UC-BC-02

Use Case Name

Xem doanh số theo chi nhánh

Actor

Admin, Manager

Preconditions

Người dùng đã đăng nhập

Main Flow

Chọn chi nhánh → Chọn thời gian → Xem báo cáo

Postconditions

Hiển thị doanh số chi nhánh





10\. DASHBOARD

Widget

Tổng doanh thu hôm nay

Doanh thu theo ngày

Doanh thu theo chi nhánh

Top chi nhánh doanh số cao

Top nhân viên bán hàng

Tổng số hóa đơn

Giá trị đơn hàng trung bình









