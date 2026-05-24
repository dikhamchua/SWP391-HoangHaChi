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
            <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(135deg, rgba(109, 0, 8, 0.94) 0%, rgba(147, 0, 11, 0.97) 100%); z-index: 2;"></div>
            <div style="position: relative; z-index: 3; width: 100%; max-width: 460px;">
                <h1 class="display-5 fw-bold text-white mb-3">Khôi phục mật khẩu.</h1>
                <p class="fs-6 text-white opacity-75 fw-light lh-base m-0">
                    Đừng lo lắng, hãy cung cấp Email đã đăng ký. Hệ thống sẽ giúp bạn thiết lập lại mật khẩu an toàn trong vài bước.
                </p>
            </div>
        </div>

        <div class="col-12 col-md-6" style="display: flex; align-items: center; justify-content: center; padding: 3rem 2rem;">
            <div style="width: 100%; max-width: 420px;">
                
                <div class="text-center mb-4">
                    <div class="mb-3" style="width: 56px; height: 56px; background-color: #fff1f2; border-radius: 12px; display: inline-flex; align-items: center; justify-content: center; border: 1px solid #ffe4e6; margin: 0 auto;">
                        <span class="material-icons" style="font-size: 32px; color: #93000b !important;">lock_reset</span>
                    </div>
                    <h2 class="fw-bold text-dark mb-1 h3">Quên mật khẩu?</h2>
                    <p class="text-muted small">Nhập email tài khoản của bạn để nhận mã xác thực OTP</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger border-0 shadow-sm mb-3 py-2" role="alert" style="background-color: #ffebee; color: #c62828; border-radius: 8px;">
                        <span class="small fw-medium">${error}</span>
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="send-otp">
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