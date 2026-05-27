<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Đặt lại mật khẩu"/>
</jsp:include>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/register-custom.css">

<div class="container-fluid p-0 m-0" style="overflow-x: hidden; background-color: #ffffff;">
    <div class="row g-0 min-vh-100 align-items-stretch">
        
        <div class="col-12 col-md-6 d-none d-md-flex" 
             style="position: relative; background: url('https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=1200') no-repeat center center; background-size: cover; display: flex; align-items: center; justify-content: center; padding: 5rem; z-index: 1;">
            <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(135deg, rgba(109, 0, 8, 0.94) 0%, rgba(147, 0, 11, 0.97) 100%); z-index: 2;"></div>
            <div style="position: relative; z-index: 3; width: 100%; max-width: 460px;">
                <h1 class="display-5 fw-bold text-white mb-3">Xác thực tài khoản.</h1>
                <p class="fs-6 text-white opacity-75 fw-light lh-base m-0">
                    Mã xác thực đã được gửi. Vui lòng điền đúng mã OTP cùng mật khẩu mới để hoàn tất quy trình đổi mật khẩu.
                </p>
            </div>
        </div>

        <div class="col-12 col-md-6" style="display: flex; align-items: center; justify-content: center; padding: 3rem 2rem;">
            <div style="width: 100%; max-width: 420px;">
                
                <div class="text-center mb-4">
                    <h2 class="fw-bold text-dark mb-1 h3">Thiết lập mật khẩu mới</h2>
                    <p class="text-muted small">Hệ thống đã gửi mã xác thực tới: <strong>${sessionScope.resetEmail}</strong></p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger border-0 shadow-sm mb-3 py-2" role="alert" style="background-color: #ffebee; color: #c62828; border-radius: 8px;">
                        <span class="small fw-medium">${error}</span>
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="reset-password">
                    
                    <div class="mb-2.5">
                        <label for="otp" class="form-label small fw-semibold text-secondary mb-1">Mã xác thực OTP (6 số)</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60">pin</i></span>
                            <input type="text" class="form-control border-0 ps-0 shadow-none text-dark" id="otp" name="otp"
                                   placeholder="Nhập 6 số OTP" maxlength="6" style="padding: 10px 0;" required>
                        </div>
                    </div>

                    <div class="mb-2.5">
                        <label for="newPassword" class="form-label small fw-semibold text-secondary mb-1">Mật khẩu mới</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60">lock_open</i></span>
                            <input type="password" class="form-control border-0 ps-0 shadow-none text-dark" id="newPassword" name="newPassword"
                                   placeholder="Tối thiểu 6 ký tự" style="padding: 10px 0;" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label small fw-semibold text-secondary mb-1">Xác nhận mật khẩu mới</label>
                        <div class="input-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted"><i class="material-icons opacity-60">enhanced_encryption</i></span>
                            <input type="password" class="form-control border-0 ps-0 shadow-none text-dark" id="confirmPassword" name="confirmPassword"
                                   placeholder="Nhập lại mật khẩu mới" style="padding: 10px 0;" required>
                        </div>
                    </div>

                    <button type="submit" class="btn w-100 fw-semibold shadow-sm text-white"
                            style="background-color: #93000b; border: 1px solid #93000b; border-radius: 8px; padding: 11px;">
                        Xác nhận đổi mật khẩu
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp"/>