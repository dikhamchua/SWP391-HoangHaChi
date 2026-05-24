<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Đăng nhập"/>
</jsp:include>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login-custom.css">

<div class="container-fluid p-0 m-0 login-global-container" style="overflow-x: hidden; background-color: #ffffff;">
    <div class="row g-0 min-vh-100 align-items-stretch">

        <div class="col-12 col-md-6 d-none d-md-flex login-split-left" 
             style="position: relative; background: url('https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=1200') no-repeat center center; background-size: cover; display: flex; align-items: center; justify-content: center; padding: 5rem; z-index: 1;">

            <div class="login-left-mask" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(135deg, rgba(109, 0, 8, 0.94) 0%, rgba(147, 0, 11, 0.97) 100%); z-index: 2;"></div>

            <div class="login-left-inner-content" style="position: relative; z-index: 3; width: 100%; max-width: 460px;">
                <h1 class="display-5 fw-bold text-white mb-3 text-headline" style="line-height: 1.25 !important; letter-spacing: -0.5px;">
                    Quản trị toàn diện.<br>Vận hành tối ưu.
                </h1>
                <p class="fs-6 text-white opacity-75 fw-light lh-base m-0">
                    Hệ thống bán lẻ chuyên nghiệp dành cho doanh nghiệp hiện đại. Bảo mật, nhanh chóng và chính xác.
                </p>
            </div>
        </div>

        <div class="col-12 col-md-6 login-split-right" style="display: flex; align-items: center; justify-content: center; padding: 3rem 2rem; background-color: #ffffff;">
            <div class="login-box-holder" style="width: 100%; max-width: 380px;">

                <div class="text-center mb-4">
                    <div class="login-brand-icon-box mb-3" style="width: 56px; height: 56px; background-color: #fff1f2; border-radius: 12px; display: inline-flex; align-items: center; justify-content: center; border: 1px solid #ffe4e6;">
                        <span class="material-icons" style="font-size: 32px; color: #93000b !important;">store</span>
                    </div>
                    <h2 class="fw-bold text-dark mb-1 h3" style="font-family: 'Manrope', sans-serif;">KiotRetail</h2>
                    <p class="text-muted small">Đăng nhập để truy cập hệ thống quản trị</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mb-4 py-2.5" role="alert" style="background-color: #ffebee; color: #c62828; border-radius: 8px;">
                        <div class="d-flex align-items-center">
                            <i class="material-icons me-2" style="font-size: 18px;">error</i>
                            <span class="small fw-medium">${error}</span>
                        </div>
                        <button type="button" class="btn-close shadow-none p-2.5" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">

                    <div class="mb-3">
                        <label for="username" class="form-label small fw-semibold text-secondary">Tên đăng nhập</label>
                        <div class="input-group login-custom-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted">
                                <i class="material-icons opacity-60">person_outline</i>
                            </span>
                            <input type="text" class="form-control border-0 ps-0 shadow-none text-dark" id="username" name="username"
                                   placeholder="Nhập tên đăng nhập" value="${username}" style="padding-top: 11px; padding-bottom: 11px;" required autofocus>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label small fw-semibold text-secondary">Mật khẩu</label>
                        <div class="input-group login-custom-group" style="border-radius: 8px; overflow: hidden; border: 1px solid #d1d5db;">
                            <span class="input-group-text bg-white border-0 text-muted">
                                <i class="material-icons opacity-60">lock_open</i>
                            </span>
                            <input type="password" class="form-control border-0 border-end-0 ps-0 shadow-none text-dark" id="password" name="password"
                                   placeholder="Nhập mật khẩu" style="padding-top: 11px; padding-bottom: 11px;" required>
                            <button class="btn btn-outline-light border-0 text-muted bg-white shadow-none px-3" type="button" id="togglePassword">
                                <i class="material-icons opacity-60">visibility</i>
                            </button>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div class="form-check m-0 d-flex align-items-center">
                            <input type="checkbox" class="form-check-input shadow-none cursor-pointer" id="remember-me" name="remember-me">
                            <label class="form-check-label small text-muted cursor-pointer ms-2" style="user-select: none;" for="remember-me">Ghi nhớ đăng nhập</label>
                        </div>
                        <div class="mb-3 text-end">
                            <a href="${pageContext.request.contextPath}/forgot-password" class="text-decoration-none small" style="color: #93000b; font-weight: 500;">
                                Quên mật khẩu?
                            </a>
                        </div>
                    </div>

                    <button type="submit" class="btn w-100 fw-semibold shadow-sm d-flex align-items-center justify-content-center btn-kiot-submit-fixed"
                            style="background-color: #93000b !important; border: 1px solid #93000b !important; color: #ffffff !important; border-radius: 8px !important; padding-top: 11px; padding-bottom: 11px; font-size: 15px;">
                        Đăng nhập
                    </button>
                </form>

                <div class="text-center mt-4">
                    <p class="text-muted opacity-60 mb-0" style="font-size: 13px;">
                        Hệ thống nội bộ. Yêu cầu quyền truy cập hợp lệ.
                    </p>
                </div>

            </div>
        </div>

    </div>
</div>

<script>
// Đoạn script xử lý ẩn/hiện văn bản mật khẩu
    document.getElementById('togglePassword').addEventListener('click', function () {
        const passwordInput = document.getElementById('password');
        const icon = this.querySelector('i');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            icon.textContent = 'visibility_off';
        } else {
            passwordInput.type = 'password';
            icon.textContent = 'visibility';
        }
    });
</script>

<jsp:include page="../common/footer.jsp"/>