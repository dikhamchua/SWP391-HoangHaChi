HẦN I: QUY TRÌNH NGHIỆP VỤ BÁN HÀNG



Khởi tạo và Thiết lập Giá (Pricing Strategy)

Trước khi một đơn hàng được tạo, hệ thống xử lý logic giá dựa trên:

Bảng giá (PriceBook): giá sản phẩm không cố định. Đơn hàng sẽ ưu tiên PriceBookId.bảng giá có thể được áp dụng riêng biệt cho từng Nhóm khách hàng (PriceBookCustomerGroup) hoặc từng Chi nhánh (PriceBookBranch).



Nhóm khách hàng (Customer Groups):khách hàng được phân nhóm (CustomerGroupDetails). cho phép báo cáo doanh thu và công nợ (debt) theo từng tệp khách hàng mục tiêu.



Đơn vị tính (Product Units): T interface ProductUnit cho phép một sản phẩm có nhiều đơn vị (ví dụ: Thùng/Chai).



Mã sản phẩm con: Mỗi đơn vị có một Code và Barcode riêng.



Tỷ lệ quy đổi (Value): Code xử lý việc nhân/chia số lượng dựa trên tỷ lệ này để tính toán giá bán tương ứng.



Cấu trúc Đơn hàng và Trạng thái Giao dịch

đơn hàng vận hành theo trình tự:

Giai đoạn Draft/Processing/Cancelled (Trạng thái 1, 2 \& 4):



Trạng thái 1 (Draft - Phiếu tạm): Đơn hàng mới khởi tạo.

Trạng thái 2 (Processing - Đang xử lý): Đơn hàng đang được xử lý hoặc chờ giao

Trạng thái 4 (Cancelled - Đã hủy):. Khi hủy, lượng hàng đang bị tạm giữ (Reserved) sẽ được cộng lại vào tồn khả dụng (Available).



Reserved Logic: Khi đơn hàng ở trạng thái này, hệ thống thực hiện lệnh "tạm giữ". Khi ở trạng thái 1 hoặc 2, hệ thống tăng trường reserved.



Voucher \& Promotion: Flag isApplyVoucher được ghi nhận. Voucher chỉ chuyển sang trạng thái Used khi đơn hàng thành Công (Status 3).



Lối tắt makeInvoice:nếu makeInvoice: true , hệ thống bỏ qua trạng thái tạm giữ, trừ thẳng onHand và tạo Hóa đơn ngay lập tức.



Giai đoạn Thu phí bổ sung (Surcharges):



hệ thống cho phép áp dụng nhiều loại phí (SurchargeValue). Code phân biệt giữa phí thu hộ và phí tính vào doanh thu chi nhánh.



Chuyển đổi sang Hóa đơn và Thanh toán (Invoicing \& Payment)

Khi Order chuyển thành Invoice

Xử lý Tài chính: Hóa đơn ghi nhận Total, TotalPayment, và quan trọng nhất là CustomerPayment.



Phương thức thanh toán: hỗ trợ đa phương thức (Tiền mặt, Chuyển khoản, Thẻ).

Tích điểm (RewardPoints): Code ghi nhận Point dựa trên tổng giá trị hóa đơn sau chiết khấu.

Dữ liệu đầu vào:

\- branchId: Chi nhánh thực hiện bán hàng.

\- invoiceDetails: Danh sách sản phẩm bán ra (gồm productId, quantity, price).

\- totalPayment: Số tiền khách đã thanh toán.

\- customerId: Liên kết với khách hàng (không bắt buộc).

\- soldById: ID nhân viên bán hàng.





uznplay

closed this as completedlast week

uznplay

uznplay commented 16 hours ago

uznplay

16 hours ago

Đơn hàng

1\. Orders



1.1 Đăng đơn hàng mới



1.2 Duyệt đơn hàng



1.3 Hủy đơn hàng



1.4 Hoàn thành đơn hàng



1.5 Cập nhật đơn hàng



1.6 Lấy danh sách đơn hàng



1.7 Lấy danh sách đơn hàng theo id



1.8 Lấy danh sách đơn hàng theo bộ lọc



2\. Thanh Toán



2.1 Tạo phiếu thanh toán



2.2 Hủy phiếu thanh toán



3\. In



3.1 Lấy mẫu in đơn hàng/hóa đơn





1\. Order



1.1 Đăng đơn hàng mới

Hệ thống hỗ trợ tạo đơn hàng bán lẻ trực tiếp thông qua APIs. Sau khi đơn hàng được lưu thành công vào hệ thống, API sẽ trả về thông tin chi tiết của đơn hàng vừa tạo bao gồm cả trạng thái thanh toán và tồn kho được cập nhật ngay lập tức.



Bạn nên lưu ý rằng Order có thể được tạo qua API, nhưng thông tin thanh toán sẽ không được lưu trữ và không có một Transaction nào cả. Bạn có thể đánh dấu Order với bất kì trạng thái thanh toán nào.



Bạn cũng nên chú ý rằng bạn chỉ có thể thay đổi một vài thuộc tính của Order khi sử dụng API. Bạn không thể thay đổi item hay số lượng của item trong Order.



Các tham số



Tham số	Bắt buộc	Mô tả	Lý do

code	yes	string - Mã đơn hàng	Cần thiết để định danh và truy xuất đơn hàng duy nhất trong hệ thống.

issued\_on	yes	date - Thời gian đơn hàng được tạo	Xác định thời điểm phát sinh giao dịch, quan trọng cho kế toán và báo cáo doanh thu .Thời gian Order được tạo. API trả về kết quả theo định dạng chuẩn ISO 8601. Thuộc tính này được tạo tự động và không thể chỉnh sửa. Nếu bạn import Order từ một hệ thống khác vào group5 thì hãy sử dụng thuộc tính có thể ghi processed\_on để xác định thời gian Order được xử lý.

account\_id	yes	int - ID tài khoản nhân viên tạo đơn hàng	Ghi nhận nhân viên bán hàng để quản lý ca trực và doanh số.

assignee\_id	yes	int - ID nhân viên phụ trách đơn hàng	Phục vụ việc phân quyền và giám sát xử lý đơn hàng tại quầy.

customer\_id	yes	int - ID định danh khách hàng mua hàng	Gắn đơn hàng với khách hàng để tích điểm thành viên hoặc theo dõi lịch sử mua sắm.

phone\_number	yes	string - Số điện thoại khách hàng	Thông tin liên lạc cơ bản để chăm sóc khách hàng và tích điểm.

email	no	string - Email khách hàng	Dùng để gửi hóa đơn điện tử hoặc chương trình hậu mãi nếu khách hàng yêu cầu.

status	yes	string - Trạng thái đơn hàng	Trạng thái hiện tại của đơn hàng tại quầy (ví dụ: đã hoàn thành, chờ duyệt).

note	no	string - Ghi chú cho đơn hàng	Lưu trữ các yêu cầu đặc biệt của khách hàng tại quầy.

tags	no	string - Nhãn gắn cho đơn hàng	Phục vụ việc phân loại và tìm kiếm đơn hàng nội bộ.

discount\_items	no	objects - Danh sách giảm giá/chiết khấu	Ghi nhận chi tiết các khoản chiết khấu, giảm giá áp dụng trực tiếp cho đơn hàng.

promotion\_items	no	objects - Danh sách hàng khuyến mại	Theo dõi các sản phẩm tặng kèm theo chương trình khuyến mại trực tiếp tại cửa hàng.

payment\_method\_id	yes	int - ID phương thức thanh toán	Xác định hình thức thanh toán trực tiếp tại quầy (Tiền mặt, Thẻ, Chuyển khoản).

amount	yes	bigdecimal - Số tiền khách thanh toán	Tổng số tiền thực tế khách hàng đã thanh toán tại quầy.

paid\_on	yes	date - Thời gian thanh toán	Xác nhận thời điểm dòng tiền thực tế đi vào hệ thống.

OrderReturn

Trả hàng cho khách hàng được thực hiện trực tiếp tại quầy khi một đơn hàng đã được tạo thành công trên hệ thống và khách hàng có yêu cầu trả sản phẩm, đổi sản phẩm hoặc hoàn tiền.

OrderReturn chỉ được thực hiện khi có đơn đặt hàng thành công trước đó.



1\. Thêm 1 đơn trả hàng



2\. Tạo mới code cho đơn trả hàng



3\. Lấy một đơn trả hàng theo id



4\. Lấy đơn trả hàng theo bộ lọc



5\. Thêm đơn hàng hoàn tiền theo id



6\. Lấy 1 đơn hàng hoàn tiền theo id





1\. Thêm 1 đơn trả hàng

Các tham số



Tham số	Bắt buộc	Mô tả	Lý do

OrderReturn.id	no	string - Mã định danh đơn trả hàng trên hệ thống	Hệ thống tự sinh mã nếu không cung cấp.

OrderReturn.tenant\_id	no	int - ID định danh chủ cửa hàng/doanh nghiệp	Tự động lấy theo tài khoản đăng nhập hiện tại.

OrderReturn.location\_id	no	int - ID chi nhánh/kho hàng nhận sản phẩm trả lại	Tự động lấy theo chi nhánh mặc định của tài khoản đăng nhập.

OrderReturn.code	no	string - Mã tham chiếu của đơn trả hàng	Có thể tự sinh theo cấu hình định dạng mã của hệ thống.

OrderReturn.account\_id	no	int - ID tài khoản nhân viên tiếp nhận đơn trả hàng	Tự động ghi nhận theo tài khoản nhân viên đang thực hiện thao tác.

OrderReturn.order\_id	yes	int - ID định danh đơn đặt hàng gốc	Bắt buộc phải cung cấp để đối soát đơn hàng gốc, hoàn tiền và cộng lại tồn kho.

OrderReturn.order\_code	no	string - Mã tham chiếu của đơn đặt hàng gốc	Thông tin bổ sung để đối soát nhanh, không bắt buộc nếu đã có order\_id.

OrderReturn.customer\_id	no	int - ID định danh khách hàng trả hàng	Kế thừa trực tiếp từ thông tin của đơn hàng gốc.

OrderReturn.contact\_id	no	int - ID định danh cho thông tin liên hệ của khách hàng	Kế thừa trực tiếp từ thông tin của đơn hàng gốc.

OrderReturn.reference	no	string - Ghi chú hoặc thông tin tham chiếu bổ sung	Thông tin bổ sung tùy chọn để nhân viên ghi nhận thêm lý do trả hàng.

OrderReturn.status	no	string - Trạng thái đơn trả hàng	Mặc định là trạng thái mới tạo nếu không được chỉ định cụ thể.

OrderReturn.refund\_status	no	string - Trạng thái hoàn tiền cho khách hàng	Hệ thống tự động cập nhật dựa trên giá trị của các phiếu hoàn tiền đi kèm.

OrderReturn.total\_amount	no	bigdecimal - Tổng giá trị của hàng hóa được hoàn trả	Hệ thống tự động tính toán dựa trên danh sách line\_items.

OrderReturn.issued\_on	no	date - Thời gian đơn trả hàng được tạo	Định dạng chuẩn ISO 8601. Hệ thống tự động ghi nhận thời gian thực tế phát sinh đơn trả hàng.

OrderReturn.created\_on	no	datetime - Thời gian ghi nhận bản ghi đơn trả trên hệ thống	Hệ thống tự động lưu thời điểm tạo bản ghi.

OrderReturn.modified\_on	no	datetime - Thời gian cập nhật đơn trả hàng gần nhất	Hệ thống tự động cập nhật khi có thay đổi thông tin.

OrderReturn.line\_items	yes	objects - Danh sách chi tiết các mặt hàng hoàn trả	Bắt buộc phải cung cấp danh sách sản phẩm và số lượng tương ứng để cập nhật lại tồn kho của chi nhánh.

OrderReturn

Trả hàng cho khách hàng được thực hiện trực tiếp tại quầy khi một đơn hàng đã được tạo thành công trên hệ thống và khách hàng có yêu cầu trả sản phẩm, đổi sản phẩm hoặc hoàn tiền.



OrderReturn chỉ được thực hiện khi có đơn đặt hàng thành công trước đó.



1\. Thêm 1 đơn trả hàng



2\. Tạo mới code cho đơn trả hàng



3\. Lấy một đơn trả hàng theo id



4\. Lấy đơn trả hàng theo bộ lọc



5\. Thêm đơn hàng hoàn tiền theo id



6\. Lấy 1 đơn hàng hoàn tiền theo id





1\. Thêm 1 đơn trả hàng

Các tham số



Tham số	Bắt buộc	Mô tả	Lý do

OrderReturn.id	no	string - Mã định danh đơn trả hàng trên hệ thống	Hệ thống tự sinh mã nếu không cung cấp.

OrderReturn.tenant\_id	no	int - ID định danh chủ cửa hàng/doanh nghiệp	Tự động lấy theo tài khoản đăng nhập hiện tại.

OrderReturn.location\_id	no	int - ID chi nhánh/kho hàng nhận sản phẩm trả lại	Tự động lấy theo chi nhánh mặc định của tài khoản đăng nhập.

OrderReturn.code	no	string - Mã tham chiếu của đơn trả hàng	Có thể tự sinh theo cấu hình định dạng mã của hệ thống.

OrderReturn.account\_id	no	int - ID tài khoản nhân viên tiếp nhận đơn trả hàng	Tự động ghi nhận theo tài khoản nhân viên đang thực hiện thao tác.

OrderReturn.order\_id	yes	int - ID định danh đơn đặt hàng gốc	Bắt buộc phải cung cấp để đối soát đơn hàng gốc, hoàn tiền và cộng lại tồn kho.

OrderReturn.order\_code	no	string - Mã tham chiếu của đơn đặt hàng gốc	Thông tin bổ sung để đối soát nhanh, không bắt buộc nếu đã có order\_id.

OrderReturn.customer\_id	no	int - ID định danh khách hàng trả hàng	Kế thừa trực tiếp từ thông tin của đơn hàng gốc.

OrderReturn.contact\_id	no	int - ID định danh cho thông tin liên hệ của khách hàng	Kế thừa trực tiếp từ thông tin của đơn hàng gốc.

OrderReturn.reference	no	string - Ghi chú hoặc thông tin tham chiếu bổ sung	Thông tin bổ sung tùy chọn để nhân viên ghi nhận thêm lý do trả hàng.

OrderReturn.status	no	string - Trạng thái đơn trả hàng	Mặc định là trạng thái mới tạo nếu không được chỉ định cụ thể.

OrderReturn.refund\_status	no	string - Trạng thái hoàn tiền cho khách hàng	Hệ thống tự động cập nhật dựa trên giá trị của các phiếu hoàn tiền đi kèm.

OrderReturn.total\_amount	no	bigdecimal - Tổng giá trị của hàng hóa được hoàn trả	Hệ thống tự động tính toán dựa trên danh sách line\_items.

OrderReturn.issued\_on	no	date - Thời gian đơn trả hàng được tạo	Định dạng chuẩn ISO 8601. Hệ thống tự động ghi nhận thời gian thực tế phát sinh đơn trả hàng.

OrderReturn.created\_on	no	datetime - Thời gian ghi nhận bản ghi đơn trả trên hệ thống	Hệ thống tự động lưu thời điểm tạo bản ghi.

OrderReturn.modified\_on	no	datetime - Thời gian cập nhật đơn trả hàng gần nhất	Hệ thống tự động cập nhật khi có thay đổi thông tin.

OrderReturn.line\_items	yes	objects - Danh sách chi tiết các mặt hàng hoàn trả	Bắt buộc phải cung cấp danh sách sản phẩm và số lượng tương ứng để cập nhật lại tồn kho của chi nhánh.

