Cơ chế Kiểm soát Sản phẩm đặc thù

(Quản lý theo Lô/Serial): Nếu thuộc tính này là true, mọi giao dịch bán hàng hoặc nhập hàng đều bắt buộc phải kèm theo mảng SerialNumbers. Báo cáo kho không chỉ báo số lượng mà phải báo chi tiết từng số Serial/Số lô.



Cơ chế Biến thể - Variant: Code hỗ trợ phân cấp sản phẩm để báo cáo tồn kho linh hoạt:



Sản phẩm cha (Master): Có flag hasVariants: true. Đây là bản ghi gốc (ví dụ: "Áo sơ mi nam").

Sản phẩm con (Variant): Chứa masterProductId trỏ về ID của sản phẩm cha. Mỗi biến thể là một SKU riêng biệt (ví dụ: "Áo sơ mi nam - Trắng - L").



Logic tồn kho:

Quản lý tách biệt: Mỗi biến thể có mảng inventories riêng, cho phép theo dõi tồn kho chính xác đến từng size/màu tại từng chi nhánh.

Báo cáo gộp: hệ thống có thể truy vấn và cộng dồn dữ liệu của tất cả biến thể để đưa ra báo cáo tổng tồn kho của dòng sản phẩm cha.

Thuộc tính : Các biến thể được phân biệt qua mảng attributes (tên thuộc tính và giá trị), giúp báo cáo kho theo từng thuộc tính (ví dụ: tổng tồn các sản phẩm màu "Đỏ").



Công thức tính toán báo cáo tồn kho

Tồn thực tế (OnHand): Tổng lượng hàng đang có tại kệ.

Tồn khả dụng (Available): OnHand - Reserved (Hàng khách đã đặt nhưng chưa lấy).

Giá trị kho (Inventory Value): OnHand \* Cost (Giá vốn trung bình )

3\. Quản lý tồn kho đa kho (Multi-warehouse Reporting)

Code hỗ trợ báo cáo chi tiết cho từng kho và tổng kho:



Cấu trúc Branch: cho phép nhóm các kho theo khu vực hoặc cấp bậc để làm báo cáo tổng hợp.

Chi tiết tồn theo kho: Mảng inventories trong Product chứa dữ liệu riêng biệt cho từng branchId (bao gồm cả onOrder - hàng đang về).

Định mức tồn: minQuantity và maxQuantity hỗ trợ báo cáo "Hàng dưới định mức" hoặc "Hàng vượt định mức" để tối ưu vốn lưu động.

