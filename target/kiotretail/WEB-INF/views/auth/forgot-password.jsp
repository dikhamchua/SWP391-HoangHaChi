<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Quên mật khẩu"/>
</jsp:include>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

<div class="container-fluid p-0 m-0" style="overflow-x: hidden; background-color: #ffffff;">
    <div class="row g-0 min-vh-100 align-items-stretch">
        
        <div class="col-12 col-md-6 d-none d-md-flex" 
             style="position: relative; background: url('https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=1200') no-repeat center center; background-size: cover; display: flex; align-items: center; justify-content: center; padding: 5rem; z-index: 1;">
            <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(135deg, rgba(147, 0, 11, 0.9) 0%, rgba(109, 0, 8, 0.95) 100%); z-index: -1;"></div>
            <div class="text-white">
                <div class="d-flex align-items-center gap-2 mb-4">
                    <span class="material-icons" style="font-size: 40px;">store</span>
                    <h1 class="fw-bold m-0" style="font-family: 'Manrope', sans-serif;">KiotRetail</h1>
                </div>
                <h2 class="fw-bold mb-3" style="font-family: 'Manrope', sans-serif; font-size: 2.5rem; line-height: 1.2;">Giải pháp quản lý<br>bán hàng thông minh</h2>
                <p class="opacity-80 lead" style="font-size: 1.1rem;">Hệ thống tối ưu quy trình vận hành, quản lý kho hàng, nhân viên và doanh thu một cách hiệu quả và chính xác nhất.</p>
            </div>
        </div>

        <div class="col-12 col-md-6 d-flex align-items-center justify-content-center p-4 p-sm-5" style="position: relative; z-index: 2;">
            <div class="w-100" style="max-width: 420px;">
                <div class="mb-4">
                    <h3 class="fw-bold text-dark mb-2" style="font-family: 'Manrope', sans-serif; font-size: 1.75rem;">Xác nhận tài khoản</h3>
                    <p class="text-secondary small">Vui lòng nhập chính xác Tên nhân viên và email để nhận mã xác thực OTP.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger d-flex align-items-center py-2 px-3 mb-4" role="alert" style="border-radius: 8px;">
                        <span class="material-icons me-2" style="font-size: 20px;">error_outline</span>
                        <span class="small fw-medium">${error}</span>
                    </div>
                </c:if>

                <form method="POST" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="send-otp">
                    
                    <div class="mb-3">
                        <label for="username" class="form-label small fw-semibold text-secondary mb-1">Họ và tên nhân viên</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60">account_circle</i></span>
                            <input type="text" class="form-control border-0 ps-0 shadow-none text-dark" name="fullName" placeholder="Nhập họ và tên nhân viên" required />
                                   
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="email" class="form-label small fw-semibold text-secondary mb-1">Địa chỉ Email đã đăng ký</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60">mail_outline</i></span>
                            <input type="email" class="form-control border-0 ps-0 shadow-none text-dark" id="email" name="email"
                                   placeholder="example@gmail.com" style="padding: 11px 0;" required>
                        </div>
                    </div>

                    <button type="submit" class="btn w-100 fw-semibold shadow-sm text-white"
                            style="background-color: #93000b; border: 1px solid #93000b; border-radius: 8px; padding: 11px;">
                        Gửi mã xác thực OTP
                    </button>
                </form>

                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/login" class="fw-semibold text-decoration-none small" style="color: #93000b;">Quay lại Đăng nhập</a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>