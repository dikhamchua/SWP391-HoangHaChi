<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>500 - Lỗi hệ thống</title>
    <style>
        body { font-family: Inter, sans-serif; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; background: #f5f5f5; }
        .error-box { text-align: center; }
        .error-box h1 { font-size: 72px; color: #ef4444; margin: 0; }
        .error-box p { font-size: 18px; color: #555; margin: 12px 0; }
        .error-box a { color: #0070f4; text-decoration: none; font-weight: 600; }
    </style>
</head>
<body>
    <div class="error-box">
        <h1>500</h1>
        <p>Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.</p>
        <a href="${pageContext.request.contextPath}/login">Quay về trang chủ</a>
    </div>
</body>
</html>
