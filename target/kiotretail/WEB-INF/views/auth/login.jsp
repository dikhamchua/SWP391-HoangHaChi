<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Đăng nhập"/>
</jsp:include>

<div class="login-container min-vh-100 d-flex align-items-center justify-content-center bg-light">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-5 col-lg-4">
                <div class="card shadow-lg border-0 rounded-4">
                    <div class="card-body p-5">
                        <!-- Logo & Title -->
                        <div class="text-center mb-4">
                            <div class="mb-3">
                                <span class="material-icons text-danger" style="font-size: 64px;">store</span>
                            </div>
                            <h3 class="fw-bold text-dark mb-2">KiotRetail</h3>
                            <p class="text-muted">Hệ thống quản lý bán hàng</p>
                        </div>

                        <!-- Error Message -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="material-icons align-middle me-2" style="font-size: 20px;">error</i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Login Form -->
                        <form method="post" action="${pageContext.request.contextPath}/login">
                            <div class="mb-3">
                                <label for="username" class="form-label fw-semibold">Tên đăng nhập</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-white">
                                        <i class="material-icons text-muted">person</i>
                                    </span>
                                    <input type="text" class="form-control" id="username" name="username"
                                           placeholder="Nhập tên đăng nhập" value="${username}" required autofocus>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label fw-semibold">Mật khẩu</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-white">
                                        <i class="material-icons text-muted">lock</i>
                                    </span>
                                    <input type="password" class="form-control" id="password" name="password"
                                           placeholder="Nhập mật khẩu" required>
                                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                        <i class="material-icons">visibility</i>
                                    </button>
                                </div>
                            </div>

                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="remember-me" name="remember-me">
                                <label class="form-check-label" for="remember-me">
                                    Ghi nhớ đăng nhập
                                </label>
                            </div>

                            <button type="submit" class="btn btn-danger w-100 py-2 fw-semibold">
                                Đăng nhập
                            </button>
                        </form>

                        <!-- Demo Credentials -->
                        <div class="mt-4 p-3 bg-light rounded">
                            <small class="text-muted d-block mb-2 fw-semibold">Tài khoản demo:</small>
                            <small class="text-muted d-block">Username: <code>admin</code></small>
                            <small class="text-muted d-block">Password: <code>123456</code></small>
                        </div>
                    </div>
                </div>

                <!-- Footer -->
                <div class="text-center mt-4">
                    <small class="text-muted">© 2024 KiotRetail. All rights reserved.</small>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Toggle password visibility
document.getElementById('togglePassword').addEventListener('click', function() {
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
