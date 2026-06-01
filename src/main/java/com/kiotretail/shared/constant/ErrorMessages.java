package com.kiotretail.shared.constant;

public final class ErrorMessages {
    private ErrorMessages() {}

    // Common
    public static final String FIELD_REQUIRED = "%s không được để trống";
    public static final String ALREADY_EXISTS = "%s đã tồn tại";
    public static final String NOT_FOUND = "%s không tồn tại";
    public static final String INVALID_VALUE = "%s không hợp lệ";
    public static final String INVALID_ACTION = "Hành động không hợp lệ";
    public static final String SYSTEM_ERROR = "Lỗi hệ thống: %s";

    // Auth
    // Generic message used for login failures to avoid leaking whether the email exists (user enumeration).
    public static final String INVALID_CREDENTIALS = "Email hoặc mật khẩu không đúng";
    public static final String EMAIL_NOT_FOUND = "Email không tồn tại";
    public static final String WRONG_PASSWORD = "Mật khẩu không đúng";
    public static final String ACCOUNT_LOCKED = "Tài khoản đã bị khóa";
    public static final String EMAIL_EXISTS = "Email đã tồn tại";
    public static final String PHONE_EXISTS = "Số điện thoại đã tồn tại";
    public static final String PASSWORD_MISMATCH = "Mật khẩu xác nhận không khớp";
    public static final String OLD_PASSWORD_WRONG = "Mật khẩu cũ không đúng";
    public static final String REGISTER_SUCCESS = "Đăng ký thành công, vui lòng đăng nhập";
    public static final String CHANGE_PASSWORD_SUCCESS = "Đổi mật khẩu thành công";
    public static final String RESET_PASSWORD_SUCCESS = "Đặt lại mật khẩu thành công";

    // Product
    public static final String SKU_EXISTS = "Mã SKU đã tồn tại";
    public static final String CATEGORY_NOT_FOUND = "Nhóm hàng không tồn tại";
    public static final String CATEGORY_NAME_EXISTS = "Tên nhóm hàng đã tồn tại";
    public static final String CIRCULAR_PARENT = "Không thể gán nhóm cha tạo vòng lặp";

    // Invoice
    public static final String ORDER_EMPTY = "Đơn hàng phải có ít nhất 1 sản phẩm";
    public static final String PAYMENT_INVALID = "Số tiền thanh toán phải lớn hơn 0";
    public static final String CART_EMPTY = "Giỏ hàng trống";

    // CRUD success
    public static final String CREATE_SUCCESS = "Thêm %s thành công";
    public static final String UPDATE_SUCCESS = "Cập nhật %s thành công";
    public static final String DELETE_SUCCESS = "Xóa %s thành công";
    public static final String CREATE_FAILED = "Thêm %s thất bại";
    public static final String UPDATE_FAILED = "Cập nhật %s thất bại";

    // Entity names (for format strings)
    public static final String ENTITY_CUSTOMER = "khách hàng";
    public static final String ENTITY_EMPLOYEE = "nhân viên";
    public static final String ENTITY_PRODUCT = "sản phẩm";
    public static final String ENTITY_CATEGORY = "nhóm hàng";
    public static final String ENTITY_ORDER = "đơn hàng";
    public static final String ENTITY_SUPPLIER = "nhà cung cấp";
    public static final String ENTITY_BRANCH = "chi nhánh";
    public static final String ENTITY_PURCHASE_ORDER = "phiếu nhập";

    // Purchase Order workflow
    public static final String PO_NOT_FOUND = "Phiếu nhập không tồn tại";
    public static final String PO_INVALID_STATUS = "Trạng thái phiếu nhập không hợp lệ cho thao tác này";
    public static final String PO_REASON_REQUIRED = "Vui lòng nhập lý do";
    public static final String PO_CREATOR_CANNOT_APPROVE = "Người tạo phiếu không thể tự duyệt phiếu của mình";
    public static final String PO_OWNER_REQUIRED = "Phiếu giá trị lớn cần chủ cửa hàng phê duyệt";
    public static final String PO_NO_PERMISSION = "Bạn không có quyền thực hiện thao tác này";
    public static final String PO_EMPTY_LINES = "Phiếu nhập phải có ít nhất 1 sản phẩm";
    public static final String PO_RECEIVE_INVALID_QTY = "Số lượng nhận không hợp lệ";
    public static final String PO_RECEIVE_OVER_ORDERED = "Số lượng nhận vượt quá số lượng đặt";
}