<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Đăng ký tài khoản"/>
</jsp:include>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

<div class="container-fluid p-0 m-0" style="overflow-x: hidden; background-color: #ffffff;">
    <div class="row g-0 min-vh-100 align-items-stretch">
        
        <div class="col-12 col-md-6 d-none d-md-flex" 
             style="position: relative; background: url('https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=1200') no-repeat center center; background-size: cover; display: flex; align-items: center; justify-content: center; padding: 5rem; z-index: 1;">
            <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(135deg, rgba(109, 0, 8, 0.94) 0%, rgba(147, 0, 11, 0.97) 100%); z-index: 2;"></div>
            <div style="position: relative; z-index: 3; width: 100%; max-width: 460px;">
                <h1 class="display-5 fw-bold text-white mb-3" style="line-height: 1.25 !important; letter-spacing: -0.5px;">
                    Tạo tài khoản mới<br>
                </h1>
                <p class="fs-6 text-white opacity-75 fw-light lh-base m-0">
                    Gia nhập hệ thống quản lý bán hàng KiotRetail để tối ưu hóa quy trình vận hành và bứt phá doanh thu ngay hôm nay.
                </p>
            </div>
        </div>

        <div class="col-12 col-md-6" style="display: flex; align-items: center; justify-content: center; padding: 2rem 2rem; background-color: #ffffff;">
            <div style="width: 100%; max-width: 420px;">

                <div class="text-center mb-3">
                    <div class="mb-2" style="width: 56px; height: 56px; background-color: #fff1f2; border-radius: 12px; display: inline-flex; align-items: center; justify-content: center; border: 1px solid #ffe4e6; margin: 0 auto;">
                        <span class="material-icons" style="font-size: 32px; color: #93000b !important;">person_add_alt_1</span>
                    </div>
                    <h2 class="fw-bold text-dark mb-1 h3" style="font-family: 'Manrope', sans-serif;">Đăng ký tài khoản nhân viên</h2>
                    <p class="text-muted small mb-2">Vui lòng nhập đầy đủ thông tin bên dưới</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mb-3 py-2" role="alert" style="background-color: #ffebee; color: #c62828; border-radius: 8px;">
                        <div class="d-flex align-items-center">
                            <i class="material-icons me-2" style="font-size: 18px;">error</i>
                            <span class="small fw-medium">${error}</span>
                        </div>
                        <button type="button" class="btn-close shadow-none p-2.5" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/register" method="POST" class="mt-4">

                    <%-- 1. Họ và tên --%>
                    <div class="mb-3">
                        <label for="fullName" class="form-label small fw-semibold text-secondary mb-1">Họ tên nhân viên</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60" style="font-size: 20px;">person_outline</i></span>
                            <input type="text" class="form-control border-0 ps-0 shadow-none text-dark" id="fullName" name="fullName"
                                   placeholder="Nhập đầy đủ họ tên nhân viên" style="padding: 9px 0;" required>
                        </div>
                    </div>

                    <%-- 2. Email --%>
                    <div class="mb-3">
                        <label for="email" class="form-label small fw-semibold text-secondary mb-1">Địa chỉ Email</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60" style="font-size: 20px;">mail_outline</i></span>
                            <input type="email" class="form-control border-0 ps-0 shadow-none text-dark" id="email" name="email"
                                   placeholder="nhanvien@domain.com" style="padding: 9px 0;" required>
                        </div>
                    </div>

                    <%-- 3. Số điện thoại (Thay thế cho Username cũ) --%>
                    <div class="mb-3">
                        <label for="phone" class="form-label small fw-semibold text-secondary mb-1">Số điện thoại</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60" style="font-size: 20px;">phone</i></span>
                            <input type="tel" class="form-control border-0 ps-0 shadow-none text-dark" id="phone" name="phone"
                                   placeholder="Nhập số điện thoại liên hệ" style="padding: 9px 0;" required>
                        </div>
                    </div>

                    <%-- 4. Chức vụ (RoleID - Dropdown list) --%>
                    <div class="mb-3">
                        <label for="roleId" class="form-label small fw-semibold text-secondary mb-1">Chức vụ hệ thống</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60" style="font-size: 20px;">badge</i></span>
                            <select class="form-select border-0 ps-0 shadow-none text-dark" id="roleId" name="roleId" style="padding: 9px 0; background-color: #fff;" required>
                                <option value="" disabled selected>-- Chọn chức vụ --</option>
                                <option value="2">Store Manager (Quản lý cửa hàng)</option>
                                <option value="3">Sales Staff (Nhân viên bán hàng)</option>
                                <option value="4">Warehouse Staff (Nhân viên kho hàng)</option>
                            </select>
                        </div>
                    </div>

                    
                    <%-- 5. Chi nhánh (BranchID - Dropdown list) --%>
                    <div class="mb-4">
                        <label for="branchId" class="form-label small fw-semibold text-secondary mb-1">Chi nhánh làm việc</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60" style="font-size: 20px;">storefront</i></span>
                            <select class="form-select border-0 ps-0 shadow-none text-dark" id="branchId" name="branchId" style="padding: 9px 0; background-color: #fff;" required>
                                <option value="" disabled selected>-- Chọn chi nhánh --</option>
                                <%-- Đoạn cứng dữ liệu mẫu tương ứng với database DBFinora của bạn --%>
                                <option value="1">Chi nhánh chính Miền Bắc</option>
                                <option value="2">Chi nhánh Miền Nam</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" class="btn w-100 fw-semibold shadow-sm d-flex align-items-center justify-content-center"
                            style="background-color: #93000b !important; border: 1px solid #93000b !important; color: #ffffff !important; border-radius: 8px !important; padding: 11px; font-size: 15px;">
                        Kích hoạt & Cấp tài khoản nhân viên
                    </button>
                </form>

            </div>
        </div>

    </div>
</div>

<jsp:include page="../common/footer.jsp"/>