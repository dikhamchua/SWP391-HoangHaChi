Hệ Thống Quản Lý Chuỗi Cửa Hàng

Business Context

&#x20;Đề tài: Xây dựng phần mềm quản lý cửa hàng cho chuỗi cửa hàng + website giới thiệu SEO

Tham khảo: KiotViet

Tech stack: JSP Servlet (chung 1 project cho cả website + admin)

Loại hàng: Tổng hợp (generic, không gắn ngành cụ thể)

Loyalty: Phân hạng khách hàng (Silver/Gold/VIP) theo tổng chi tiêu

Thanh toán: Tích hợp VNPay

Scale: 5-20 cửa hàng

Team: 5 người, deadline 8-10 tuần

Phân loại ưu tiên

P0 (Must-have): Bắt buộc có theo đề bài, core business

P1 (Should-have): Nghiệp vụ quan trọng, nên có để hệ thống hoàn chỉnh

P2 (Nice-to-have): Nâng cao, làm nếu còn thời gian

1\.   Admin (Quản trị hệ thống) 



UC ID

Tên Use Case

Priority

Mô tả nghiệp vụ

UC-1.1

Đăng nhập

P0

Xác thực bằng username/password, xác định role và cửa hàng được phân công

UC-1.2

Đăng xuất

P0

Kết thúc phiên làm việc và ghi log thời gian

UC-1.3

Quản lý tài khoản nhân viên

P0

Tạo/sửa/khoá tài khoản, gán role và cửa hàng

UC-1.4

Phân quyền theo vai trò

P0

Tạo role, gán permission cho từng module

UC-1.5

Quản lý cửa hàng/chi nhánh

P0

Thêm/sửa/đóng điểm bán, quản lý thông tin chi nhánh

UC-1.6

Cấu hình hệ thống

P1

Thiết lập thông tin công ty, VAT, tiền tệ, mẫu hoá đơn

UC-1.7

Xem nhật ký hoạt động

P1

Theo dõi log thao tác của người dùng

UC-9.8

Quản lý nội dung website

P0

Cập nhật banner, bài viết, thông tin hiển thị trên website





Quy tắc nghiệp vụ

Admin có quyền cao nhất trong hệ thống.

Admin được phép tạo/sửa/khoá tài khoản người dùng.

Mỗi tài khoản phải có ít nhất 1 role.

Một tài khoản có thể được gán nhiều role.

Admin được phép gán tài khoản cho nhiều cửa hàng.

Chỉ Admin mới được phân quyền role và permission.

Chỉ Admin mới được cấu hình hệ thống:

VAT

Tiền tệ

Thông tin công ty

Mẫu hoá đơn

Khi tài khoản bị khóa, người dùng không thể đăng nhập.

Mọi thao tác quan trọng của Admin phải được ghi log hệ thống.

Admin không được trực tiếp thực hiện nghiệp vụ bán hàng hoặc nhập hàng.

2\.   Owner (Chủ chuỗi cửa hàng) 



UC ID

Tên Use Case

Priority

Mô tả nghiệp vụ

UC-1.1

Đăng nhập

P0

Đăng nhập vào hệ thống quản trị

UC-1.2

Đăng xuất

P0

Kết thúc phiên làm việc

UC-1.8

Xem nhật ký hoạt động

P2

Xem lịch sử thao tác của hệ thống

UC-2.3

Quản lý giá bán

P1

Thiết lập giá bán theo từng chi nhánh

UC-4.5

Thanh toán nhà cung cấp

P1

Duyệt và thanh toán công nợ NCC

UC-7.5

Xem sổ quỹ

P0

Theo dõi dòng tiền toàn hệ thống

UC-8.1

Báo cáo doanh số theo thời gian

P0

Xem doanh thu theo ngày/tháng/năm

UC-8.2

Báo cáo doanh số theo cửa hàng

P0

So sánh doanh thu giữa các chi nhánh

UC-8.3

Báo cáo doanh số theo nhân viên

P0

Theo dõi KPI và hiệu suất nhân viên

UC-8.4

Báo cáo hàng tồn kho

P0

Xem tồn kho toàn hệ thống

UC-8.5

Báo cáo lãi lỗ

P1

Theo dõi doanh thu, chi phí, lợi nhuận

UC-8.6

Báo cáo công nợ

P1

Theo dõi công nợ khách hàng và NCC

UC-8.7

Báo cáo khách hàng thân thiết

P0

Phân tích khách hàng VIP, Gold

UC-8.8

Báo cáo sản phẩm bán chạy

P1

Thống kê sản phẩm bán chạy/bán chậm



Quy tắc nghiệp vụ

Owner được xem dữ liệu của toàn bộ chuỗi cửa hàng.

Owner không bị giới hạn theo chi nhánh.

Owner được xem:

Doanh thu

Lãi lỗ

Công nợ

Tồn kho

KPI nhân viên

Owner có quyền duyệt các khoản chi lớn.

Owner được thay đổi ngưỡng phân hạng khách hàng.

Owner được cấu hình:

Silver

Gold

VIP

Owner không trực tiếp thao tác POS bán hàng.

Owner không được sửa log hệ thống.

3\.   Store Manager (Quản lý cửa hàng)



UC ID

Tên Use Case

Priority

Mô tả nghiệp vụ

UC-1.1

Đăng nhập

P0

Đăng nhập hệ thống theo cửa hàng được phân công

UC-1.2

Đăng xuất

P0

Kết thúc phiên làm việc

UC-2.1

Quản lý danh mục sản phẩm

P0

Tạo/sửa/xóa category sản phẩm

UC-2.2

Quản lý sản phẩm

P0

Quản lý thông tin sản phẩm

UC-2.3

Quản lý giá bán

P1

Thiết lập giá bán theo cửa hàng

UC-2.5

Quản lý đơn vị tính

P2

Quản lý quy đổi đơn vị tính

UC-3.6

Trả hàng/Đổi hàng

P1

Duyệt đổi trả và cập nhật tồn kho

UC-3.7

Huỷ đơn hàng

P1

Huỷ đơn đã tạo và ghi nhận lý do

UC-3.9

Thu nợ khách hàng

P2

Ghi nhận thanh toán công nợ

UC-3.10

Xem lịch sử bán hàng

P0

Theo dõi các đơn hàng đã bán

UC-4.1

Quản lý nhà cung cấp

P0

Thêm/sửa thông tin NCC

UC-4.4

Trả hàng nhà cung cấp

P2

Trả hàng lỗi/hết hạn cho NCC

UC-4.5

Thanh toán nhà cung cấp

P1

Thanh toán công nợ NCC

UC-4.6

Xem lịch sử nhập hàng

P0

Theo dõi phiếu nhập kho

UC-5.2

Điều chỉnh tồn kho

P1

Duyệt kết quả kiểm kho và cập nhật tồn

UC-5.4

Xem tồn kho

P0

Xem tồn kho theo sản phẩm/kho

UC-5.5

Cảnh báo hàng sắp hết

P2

Nhận cảnh báo tồn kho thấp

UC-6.2

Sửa thông tin khách hàng

P0

Cập nhật dữ liệu khách hàng

UC-6.4

Quản lý nhóm khách hàng

P0

Phân hạng Silver/Gold/VIP

UC-6.7

Xem công nợ khách hàng

P2

Theo dõi khách hàng đang nợ

UC-7.1

Tạo phiếu thu

P0

Ghi nhận các khoản thu

UC-7.2

Tạo phiếu chi

P0

Ghi nhận các khoản chi

UC-7.3

Quản lý loại thu chi

P1

Tạo danh mục thu/chi

UC-7.4

Đối soát cuối ngày

P2

Kiểm tra tiền mặt thực tế

UC-7.5

Xem sổ quỹ

P0

Theo dõi dòng tiền cửa hàng

UC-8.1

Báo cáo doanh số theo thời gian

P0

Theo dõi doanh thu cửa hàng

UC-8.3

Báo cáo doanh số theo nhân viên

P0

Xem hiệu suất nhân viên

UC-8.4

Báo cáo hàng tồn kho

P0

Theo dõi tồn kho cửa hàng

UC-8.6

Báo cáo công nợ

P1

Theo dõi công nợ

UC-8.7

Báo cáo khách hàng thân thiết

P0

Thống kê khách hàng thân thiết

UC-8.8

Báo cáo sản phẩm bán chạy

P1

Xem top sản phẩm bán chạy



Quy tắc nghiệp vụ

Store Manager chỉ được thao tác trong cửa hàng được phân công.

Store Manager được quản lý:

Sản phẩm

Giá bán

Tồn kho

Thu chi

Nhân viên cửa hàng

Store Manager được phép:

Huỷ đơn hàng

Duyệt đổi trả

Điều chỉnh tồn kho

Khi điều chỉnh tồn kho bắt buộc phải nhập lý do.

Store Manager chỉ được xem báo cáo của cửa hàng mình.

Store Manager không được xem dữ liệu cửa hàng khác.

Store Manager được tạo phiếu chi vận hành.

Store Manager được duyệt kiểm kho trước khi cập nhật tồn kho.

Store Manager được phép xem công nợ khách hàng.

4\.   Sales Staff (Nhân viên bán hàng) 



UC ID

Tên Use Case

Priority

Mô tả nghiệp vụ

UC-1.1

Đăng nhập

P0

Đăng nhập hệ thống bán hàng

UC-1.2

Đăng xuất

P0

Kết thúc phiên làm việc

UC-2.4

Tra cứu sản phẩm

P0

Tìm kiếm sản phẩm và xem tồn kho

UC-3.1

Tạo đơn bán hàng

P0

Tạo đơn bán tại quầy POS

UC-3.2

Áp dụng giảm giá

P1

Áp dụng giảm giá theo quyền

UC-3.3

Thanh toán tiền mặt

P0

Thanh toán bằng tiền mặt

UC-3.4

Thanh toán VNPay

P0

Thanh toán qua QR VNPay

UC-3.5

In hoá đơn

P1

In hoặc in lại hoá đơn

UC-3.6

Trả hàng/Đổi hàng

P1

Tiếp nhận yêu cầu đổi trả

UC-3.8

Bán hàng ghi nợ

P2

Tạo đơn bán nợ cho khách VIP

UC-3.9

Thu nợ khách hàng

P2

Thu tiền công nợ

UC-3.10

Xem lịch sử bán hàng

P0

Xem đơn hàng đã bán

UC-6.1

Thêm khách hàng

P0

Tạo khách hàng mới

UC-6.2

Sửa thông tin khách hàng

P0

Cập nhật thông tin khách

UC-6.3

Tra cứu khách hàng

P0

Tìm kiếm khách hàng

UC-6.6

Áp dụng chiết khấu theo hạng

P0

Tự động áp dụng ưu đãi theo hạng

UC-7.1

Tạo phiếu thu

P0

Ghi nhận các khoản thu



Quy tắc nghiệp vụ

Sales Staff chỉ được bán hàng tại cửa hàng được phân công.

Sales Staff được phép:

Tạo đơn hàng

Thanh toán

In hoá đơn

Tra cứu sản phẩm

Khi bán hàng, tồn kho phải được kiểm tra realtime.

Không được bán vượt số lượng tồn kho.

Sales Staff không được huỷ đơn hàng đã thanh toán.

Sales Staff chỉ được giảm giá trong mức cho phép.

Nếu vượt mức giảm giá quy định:

Cần Manager xác nhận.

Khi chọn khách hàng thành viên:

Hệ thống tự áp dụng chiết khấu theo hạng.

Sales Staff không được sửa doanh thu.

Sales Staff không được xem báo cáo toàn hệ thống.

Mọi giao dịch bán hàng phải lưu lịch sử thao tác.

5\.   Warehouse Staff (Nhân viên kho) 



UC ID

Tên Use Case

Priority

Mô tả nghiệp vụ

UC-1.1

Đăng nhập

P0

Đăng nhập hệ thống kho

UC-1.2

Đăng xuất

P0

Kết thúc phiên làm việc

UC-2.4

Tra cứu sản phẩm

P0

Tìm sản phẩm và xem tồn kho

UC-4.1

Quản lý nhà cung cấp

P0

Quản lý thông tin NCC

UC-4.2

Tạo đơn đặt hàng

P1

Đặt hàng từ NCC

UC-4.3

Nhập hàng

P0

Nhận hàng và cập nhật tồn kho

UC-4.4

Trả hàng nhà cung cấp

P2

Trả hàng lỗi/hết hạn

UC-4.6

Xem lịch sử nhập hàng

P0

Theo dõi lịch sử nhập kho

UC-5.1

Kiểm kho

P1

Kiểm kê số lượng hàng thực tế

UC-5.3

Chuyển kho

P1

Chuyển hàng giữa các kho/cửa hàng

UC-5.4

Xem tồn kho

P0

Theo dõi tồn kho



Quy tắc nghiệp vụ

Warehouse Staff được quản lý nhập/xuất kho.

Warehouse Staff được tạo:

Phiếu nhập

Phiếu xuất

Phiếu chuyển kho

Khi nhập hàng:

Phải chọn nhà cung cấp.

Khi chuyển kho:

Kho nguồn phải đủ tồn kho.

Warehouse Staff không được điều chỉnh tồn kho trực tiếp.

Kiểm kho chỉ có hiệu lực sau khi Manager duyệt.

Warehouse Staff được xem lịch sử nhập/xuất kho.

Warehouse Staff không được xem báo cáo tài chính.

Hệ thống phải lưu lịch sử thay đổi tồn kho.

6\.   Guest (Khách truy cập website)



UC ID

Tên Use Case

Priority

Mô tả nghiệp vụ

UC-9.1

Xem trang chủ

P0

Xem banner và sản phẩm nổi bật

UC-9.2

Xem giới thiệu công ty

P0

Xem lịch sử và thông tin công ty

UC-9.3

Xem danh sách sản phẩm

P0

Duyệt sản phẩm theo danh mục

UC-9.4

Xem chi tiết sản phẩm

P0

Xem thông tin sản phẩm

UC-9.5

Tìm kiếm sản phẩm

P0

Tìm sản phẩm theo từ khoá

UC-9.6

Xem hệ thống cửa hàng

P1

Xem danh sách chi nhánh

UC-9.7

Liên hệ

P1

Gửi form liên hệ tới công ty



Quy tắc nghiệp vụ

Guest không cần đăng nhập.

Guest chỉ được:

Xem sản phẩm

Tìm kiếm sản phẩm

Xem thông tin công ty

Gửi liên hệ

Guest không được mua hàng online.

Guest không được truy cập hệ thống quản trị.

Guest không được xem tồn kho thực tế.

Form liên hệ bắt buộc:

Họ tên

Email

Nội dung

Website phải hỗ trợ SEO:

URL thân thiện

Meta title

Meta description

7\. Hệ thống (System Rules)

Quy tắc nghiệp vụ chung

Mọi dữ liệu phải gắn với store\_id.

Hệ thống phải phân quyền theo RBAC.

Hệ thống phải ghi log các thao tác quan trọng.

Tồn kho phải cập nhật realtime sau:

Bán hàng

Nhập hàng

Chuyển kho

Đổi trả

Khách hàng được tự động nâng/hạ hạng theo tổng chi tiêu.

Mỗi đơn hàng phải có mã duy nhất.

Mỗi phiếu nhập/xuất phải có mã duy nhất.

Không cho phép xoá dữ liệu phát sinh giao dịch.

Dữ liệu doanh thu và công nợ phải được lưu lịch sử.

Hệ thống phải hỗ trợ multi-store.

VNPay chỉ xác nhận đơn hàng khi callback thành công.

Đơn huỷ phải lưu lý do huỷ.

Điều chỉnh tồn kho bắt buộc ghi nhận người thực hiện và thời gian thực hiện.





Tổng Hợp Theo Priority

Priority

Số lượng UC

Ghi chú

P0 (Must-have)

30

Core features, bắt buộc hoàn thành

P1 (Should-have)

18

Nên có, làm sau khi P0 xong

P2 (Nice-to-have)

8

Bonus, làm nếu còn thời gian

Tổng

56

&#x20;



Ghi Chú Nghiệp Vụ Quan Trọng

Luồng đăng nhập: Nhập username/password → Hệ thống xác thực → Xác định role + cửa hàng được phân công → Truy cập hệ thống

Luồng quản lý tài khoản: Admin tạo tài khoản → Gán role → Gán cửa hàng → Nhân viên sử dụng tài khoản để đăng nhập

Luồng quản lý sản phẩm: Tạo danh mục → Thêm sản phẩm → Nhập thông tin (SKU, mã vạch, giá, hình ảnh...) → Lưu sản phẩm → Hiển thị trong hệ thống

Luồng bán hàng: Chọn SP → Chọn KH (tuỳ chọn, auto áp chiết khấu hạng) → Áp giảm giá → Thanh toán (mặt/VNPay) → In hoá đơn → Cập nhật tồn kho + doanh thu + tổng chi tiêu KH

Luồng thanh toán VNPay: Tạo đơn hàng → Chọn VNPay → Sinh QR/Link thanh toán → KH thanh toán → VNPay callback → Xác nhận giao dịch → Hoàn tất đơn hàng

Luồng đổi/trả hàng: Tìm hoá đơn cũ → Kiểm tra điều kiện đổi trả → Nhập lý do → Hoàn tiền/đổi SP → Cập nhật tồn kho + doanh thu

Luồng bán hàng ghi nợ: Chọn KH thân thiết → Tạo đơn bán → Ghi nhận công nợ → Lưu lịch sử nợ khách hàng

Luồng thu nợ khách hàng: Tìm KH đang nợ → Nhập số tiền thanh toán → Cập nhật công nợ → Lưu phiếu thu

Luồng nhập hàng: Chọn NCC → Chọn SP + số lượng → Tạo đơn nhập → Nhận hàng thực tế → Cập nhật tồn kho + công nợ NCC

Luồng trả hàng NCC: Chọn NCC → Chọn SP lỗi/hết hạn → Tạo phiếu trả hàng → Giảm công nợ NCC → Cập nhật tồn kho

Luồng kiểm kho: Tạo phiên kiểm kho → Đếm hàng thực tế → So sánh tồn kho hệ thống → Ghi nhận chênh lệch → Chờ duyệt điều chỉnh

Luồng điều chỉnh tồn kho: Quản lý duyệt kết quả kiểm kho → Nhập lý do điều chỉnh → Cập nhật số lượng tồn kho mới

Luồng chuyển kho: Chọn kho nguồn → Chọn kho đích → Chọn SP + số lượng → Tạo phiếu chuyển → Trừ kho nguồn + cộng kho đích

Luồng quản lý khách hàng thân thiết: KH mua hàng → Cộng dồn chi tiêu → Hệ thống tự nâng/hạ hạng → Áp ưu đãi theo hạng trong lần mua tiếp theo

Luồng tạo phiếu thu: Chọn loại thu → Nhập số tiền + ghi chú → Lưu phiếu thu → Cập nhật sổ quỹ

Luồng tạo phiếu chi: Chọn loại chi → Nhập số tiền + lý do → Lưu phiếu chi → Cập nhật sổ quỹ

Luồng đối soát cuối ngày: Kiểm tra tiền mặt thực tế → So sánh với hệ thống → Ghi nhận chênh lệch → Chốt sổ cuối ngày

Luồng báo cáo doanh thu: Chọn khoảng thời gian → Hệ thống tổng hợp dữ liệu → Hiển thị doanh thu theo ngày/tháng/năm/cửa hàng/nhân viên

Luồng báo cáo tồn kho: Chọn kho/cửa hàng → Hệ thống thống kê tồn kho → Hiển thị SP tồn thấp/tồn lâu

Luồng quản lý website: Admin cập nhật banner/bài viết/sản phẩm hiển thị → Website tự cập nhật nội dung cho Guest

Luồng website SEO: Guest truy cập website → Xem sản phẩm/bài viết → Tìm kiếm sản phẩm → Gửi form liên hệ với công ty



Ngưỡng Phân Hạng Khách Hàng

Hạng

Tổng chi tiêu tích luỹ

Chiết khấu %

Quyền lợi khác

Thường

< 5.000.000đ

0%

Không

Silver

5.000.000đ - 20.000.000đ

3%

Ưu tiên đổi trả

Gold

20.000.000đ - 50.000.000đ

5%

Ưu tiên đổi trả + quà sinh nhật

VIP

> 50.000.000đ

10%

Tất cả + mua nợ



(Ngưỡng và % có thể cấu hình bởi Owner trong hệ thống)





