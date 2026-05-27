<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kích hoạt & Đổi mật khẩu nhân viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm border-0 rounded-3">
                <div class="card-body p-4">
                    <h4 class="text-center mb-3" style="color: #93000b;">Cập nhật mật khẩu lần đầu</h4>
                    
                    <% if(request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger text-center small"><%= request.getAttribute("error") %></div>
                    <% } %>

                    <% if(request.getAttribute("successMessage") != null) { %>
                        <div class="alert alert-success text-center small"><%= request.getAttribute("successMessage") %></div>
                    <% } %>

                    <% if(request.getAttribute("isChanged") != null && (Boolean)request.getAttribute("isChanged")) { %>
                        <div class="text-center mt-4">
                            <p class="text-muted small">Tài khoản nhân viên đã sẵn sàng hoạt động với mật khẩu mới.</p>
                            <a href="${pageContext.request.contextPath}/login" class="btn text-white w-100" style="background-color: #28a745;">
                                Quay về trang Đăng nhập
                            </a>
                        </div>
                    <% } else { %>
                        <p class="text-muted text-center small">Vui lòng kiểm tra hộp thư Gmail để lấy mật khẩu tạm thời được cấp và tiến hành đổi mật khẩu mới.</p>
                        
                        <form action="${pageContext.request.contextPath}/change-password" method="POST" id="changePassForm">
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Mật khẩu tạm thời (Lấy từ Gmail)</label>
                                <input type="password" name="currentPassword" class="form-control shadow-none" placeholder="Nhập mật khẩu từ email gửi về" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Mật khẩu mới</label>
                                <input type="password" id="newPassword" name="newPassword" class="form-control shadow-none" placeholder="Tối thiểu 6 ký tự (Chữ, số, ký tự đặc biệt)" required>
                            </div>
                            <div class="mb-4">
                                <label class="form-label small fw-bold">Xác nhận lại mật khẩu mới</label>
                                <input type="password" id="confirmPassword" class="form-control shadow-none" placeholder="Nhập lại mật khẩu mới giống phía trên" required>
                                <div class="invalid-feedback" id="errorText">Mật khẩu không khớp hoặc không đủ điều kiện bảo mật.</div>
                            </div>
                            <button type="submit" class="btn w-100 text-white" style="background-color: #93000b;">Xác nhận kích hoạt tài khoản</button>
                        </form>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById('changePassForm')?.addEventListener('submit', function(e) {
    const newPass = document.getElementById('newPassword').value;
    const confirmPass = document.getElementById('confirmPassword').value;
    const errorText = document.getElementById('errorText');
    const confirmInput = document.getElementById('confirmPassword');
    
    // Biểu thức Regex: Tối thiểu 6 ký tự, có 1 chữ, 1 số, 1 ký tự đặc biệt
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$/;

    if (!passwordRegex.test(newPass)) {
        e.preventDefault();
        confirmInput.classList.add('is-invalid');
        errorText.innerText = "Mật khẩu mới phải từ 6 ký tự trở lên, gồm đầy đủ chữ cái, chữ số và ít nhất 1 ký tự đặc biệt (!@#$...).";
        return false;
    }

    if (newPass !== confirmPass) {
        e.preventDefault();
        confirmInput.classList.add('is-invalid');
        errorText.innerText = "Xác nhận mật khẩu mới không trùng khớp với nhau!";
        return false;
    }

    confirmInput.classList.remove('is-invalid');
});
</script>
</body>
</html>