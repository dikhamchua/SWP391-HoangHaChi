<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - KiotRetail</title>
    <style>
        :root {
            --bg: oklch(98% 0.005 250);
            --surface: oklch(100% 0 0);
            --fg: oklch(22% 0.02 240);
            --muted: oklch(50% 0.018 240);
            --border: oklch(90% 0.008 240);
            --accent: oklch(45% 0.2 145);
            --accent-light: oklch(95% 0.03 145);
            --danger: oklch(55% 0.2 25);
            --danger-light: oklch(95% 0.03 25);
            --font: -apple-system, BlinkMacSystemFont, 'Inter', 'Segoe UI', system-ui, sans-serif;
            --radius: 8px;
        }
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: var(--font);
            background: var(--bg);
            color: var(--fg);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
        }
        .login-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 40px;
            width: 100%;
            max-width: 400px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.03);
        }
        .login-header {
            text-align: center;
            margin-bottom: 32px;
        }
        .login-logo {
            width: 44px;
            height: 44px;
            background: var(--accent-light);
            border-radius: 10px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 16px;
        }
        .login-logo svg {
            width: 24px;
            height: 24px;
            color: var(--accent);
        }
        .login-header h1 {
            font-size: 20px;
            font-weight: 700;
            letter-spacing: -0.02em;
            margin-bottom: 4px;
        }
        .login-header p {
            font-size: 13px;
            color: var(--muted);
        }
        .form-group {
            margin-bottom: 16px;
        }
        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 500;
            color: var(--fg);
            margin-bottom: 6px;
        }
        .form-input {
            width: 100%;
            padding: 10px 12px;
            font-size: 14px;
            font-family: var(--font);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            background: var(--surface);
            color: var(--fg);
            outline: none;
            transition: border-color 0.15s, box-shadow 0.15s;
        }
        .form-input:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px oklch(45% 0.2 145 / 0.08);
        }
        .form-input::placeholder {
            color: oklch(65% 0.01 240);
        }
        .password-wrapper {
            position: relative;
        }
        .password-wrapper .form-input {
            padding-right: 40px;
        }
        .password-toggle {
            position: absolute;
            right: 8px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            color: var(--muted);
            padding: 4px;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .password-toggle:hover {
            color: var(--fg);
            background: oklch(96% 0.005 240);
        }
        .password-toggle svg {
            width: 18px;
            height: 18px;
        }
        .form-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 24px;
        }
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
        }
        .checkbox-group input[type="checkbox"] {
            width: 16px;
            height: 16px;
            accent-color: var(--accent);
            cursor: pointer;
        }
        .checkbox-group span {
            font-size: 13px;
            color: var(--muted);
            user-select: none;
        }
        .forgot-link {
            font-size: 13px;
            color: var(--accent);
            text-decoration: none;
            font-weight: 500;
        }
        .forgot-link:hover {
            text-decoration: underline;
        }
        .btn-submit {
            width: 100%;
            padding: 11px 16px;
            font-size: 14px;
            font-weight: 600;
            font-family: var(--font);
            color: white;
            background: var(--accent);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: background 0.15s, transform 0.1s;
        }
        .btn-submit:hover {
            background: oklch(40% 0.2 145);
        }
        .btn-submit:active {
            transform: scale(0.98);
        }
        .alert-error {
            background: var(--danger-light);
            border: 1px solid oklch(88% 0.04 25);
            border-radius: var(--radius);
            padding: 10px 14px;
            margin-bottom: 20px;
            font-size: 13px;
            color: var(--danger);
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .alert-error svg {
            width: 16px;
            height: 16px;
            flex-shrink: 0;
        }
        .alert-success {
            background: var(--accent-light);
            border: 1px solid oklch(88% 0.04 145);
            border-radius: var(--radius);
            padding: 10px 14px;
            margin-bottom: 20px;
            font-size: 13px;
            color: var(--accent);
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .alert-success svg {
            width: 16px;
            height: 16px;
            flex-shrink: 0;
        }
        .login-footer {
            text-align: center;
            margin-top: 24px;
            font-size: 12px;
            color: oklch(65% 0.01 240);
        }
        .login-footer a {
            color: var(--accent);
            text-decoration: none;
            font-weight: 500;
        }
        .login-footer a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-card">
        <div class="login-header">
            <div class="login-logo">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
            </div>
            <h1>KiotRetail</h1>
            <p>Đăng nhập để truy cập hệ thống</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert-error">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                <span>${error}</span>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.flashMessage}">
            <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${sessionScope.flashMessage}" escapeXml="true"/>', 'success'); });</script>
            <c:remove var="flashMessage" scope="session"/>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
            <div class="form-group">
                <label for="username">Email hoặc số điện thoại</label>
                <input type="text" class="form-input" id="username" name="username"
                       placeholder="Nhập email hoặc số điện thoại" value="${email}" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="password-wrapper">
                    <input type="password" class="form-input" id="password" name="password"
                           placeholder="Nhập mật khẩu" required>
                    <button type="button" class="password-toggle" id="togglePassword" aria-label="Hiện mật khẩu">
                        <svg id="eyeIcon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                        </svg>
                    </button>
                </div>
            </div>

            <div class="form-row">
                <label class="checkbox-group">
                    <input type="checkbox" name="remember-me">
                    <span>Ghi nhớ đăng nhập</span>
                </label>
                <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-link">Quên mật khẩu?</a>
            </div>

            <button type="submit" class="btn-submit">Đăng nhập</button>
        </form>

        <div class="login-footer">
            <p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a></p>
        </div>
    </div>

    <script>
        document.getElementById('togglePassword').addEventListener('click', function() {
            var input = document.getElementById('password');
            var icon = document.getElementById('eyeIcon');
            if (input.type === 'password') {
                input.type = 'text';
                icon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>';
            } else {
                input.type = 'password';
                icon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>';
            }
        });
        (function() { window.history.forward(); })();
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/kr-common.css" />
</body>
</html>
