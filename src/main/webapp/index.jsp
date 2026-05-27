<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty sessionScope.employee}">
    <c:redirect url="/admin/dashboard"/>
</c:if>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KiotRetail - Hệ thống quản lý bán hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Manrope:wght@700;800&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
        h1, h2, h3 {
            font-family: 'Manrope', sans-serif;
        }
        .hero-section {
            background: linear-gradient(135deg, #93000b 0%, #c62828 100%);
            color: white;
            padding: 100px 0;
        }
        .feature-card {
            transition: transform 0.3s;
            border: none;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .feature-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.15);
        }
        .feature-icon {
            width: 64px;
            height: 64px;
            background: #fff3f3;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;
        }
        .feature-icon .material-icons {
            font-size: 32px;
            color: #93000b;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
        <div class="container">
            <a class="navbar-brand d-flex align-items-center gap-2" href="#">
                <span class="material-icons text-danger" style="font-size: 32px;">store</span>
                <span class="fw-bold fs-4">KiotRetail</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="#features">Tính năng</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#about">Giới thiệu</a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-danger ms-3 px-3 fw-medium d-inline-flex align-items-center justify-content-center" 
                           href="${pageContext.request.contextPath}/login" 
                           style="height: 38px; min-width: 120px;">
                            <span class="material-icons me-1" style="font-size: 18px;">login</span>
                            Đăng nhập
                        </a>
                    </li>
<!--                    <li class="nav-item">
                        <a class="btn btn-outline-danger ms-2 px-3 fw-medium d-inline-flex align-items-center justify-content-center" 
                           href="${pageContext.request.contextPath}/register" 
                           style="height: 38px; min-width: 120px;">
                            <span class="material-icons me-1" style="font-size: 18px;">person_add</span>
                            Đăng ký
                        </a>
                    </li>-->
                </ul>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section text-center">
        <div class="container">
            <h1 class="display-3 fw-bold mb-4">Giải pháp quản lý bán hàng toàn diện</h1>
            <p class="lead mb-5">Hệ thống POS hiện đại, dễ sử dụng, giúp bạn quản lý cửa hàng hiệu quả</p>
            <div class="d-flex gap-3 justify-content-center">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-light btn-lg px-5">
                    Bắt đầu ngay
                </a>
                <a href="#features" class="btn btn-outline-light btn-lg px-5">
                    Tìm hiểu thêm
                </a>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="py-5">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="fw-bold mb-3">Tính năng nổi bật</h2>
                <p class="text-muted">Tất cả những gì bạn cần để quản lý cửa hàng</p>
            </div>

            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card feature-card h-100 p-4">
                        <div class="feature-icon">
                            <span class="material-icons">point_of_sale</span>
                        </div>
                        <h5 class="fw-bold mb-3">Bán hàng nhanh chóng</h5>
                        <p class="text-muted">Giao diện POS trực quan, xử lý đơn hàng nhanh chóng, hỗ trợ nhiều phương thức thanh toán</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card feature-card h-100 p-4">
                        <div class="feature-icon">
                            <span class="material-icons">inventory_2</span>
                        </div>
                        <h5 class="fw-bold mb-3">Quản lý kho hàng</h5>
                        <p class="text-muted">Theo dõi tồn kho thời gian thực, cảnh báo hàng sắp hết, quản lý nhập xuất tự động</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card feature-card h-100 p-4">
                        <div class="feature-icon">
                            <span class="material-icons">analytics</span>
                        </div>
                        <h5 class="fw-bold mb-3">Báo cáo chi tiết</h5>
                        <p class="text-muted">Thống kê doanh thu, lợi nhuận, sản phẩm bán chạy với biểu đồ trực quan</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card feature-card h-100 p-4">
                        <div class="feature-icon">
                            <span class="material-icons">group</span>
                        </div>
                        <h5 class="fw-bold mb-3">Quản lý khách hàng</h5>
                        <p class="text-muted">Lưu trữ thông tin khách hàng, lịch sử mua hàng, chương trình khách hàng thân thiết</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card feature-card h-100 p-4">
                        <div class="feature-icon">
                            <span class="material-icons">badge</span>
                        </div>
                        <h5 class="fw-bold mb-3">Quản lý nhân viên</h5>
                        <p class="text-muted">Phân quyền chi tiết, theo dõi hiệu suất bán hàng của từng nhân viên</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card feature-card h-100 p-4">
                        <div class="feature-icon">
                            <span class="material-icons">account_balance_wallet</span>
                        </div>
                        <h5 class="fw-bold mb-3">Sổ quỹ</h5>
                        <p class="text-muted">Quản lý thu chi, đối soát cuối ngày, báo cáo tài chính rõ ràng</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- About Section -->
    <section id="about" class="py-5 bg-light">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h2 class="fw-bold mb-4">Tại sao chọn KiotRetail?</h2>
                    <ul class="list-unstyled">
                        <li class="mb-3 d-flex align-items-start">
                            <span class="material-icons text-danger me-3">check_circle</span>
                            <div>
                                <strong>Dễ sử dụng:</strong> Giao diện thân thiện, không cần đào tạo phức tạp
                            </div>
                        </li>
                        <li class="mb-3 d-flex align-items-start">
                            <span class="material-icons text-danger me-3">check_circle</span>
                            <div>
                                <strong>Đa nền tảng:</strong> Hoạt động trên máy tính, tablet, điện thoại
                            </div>
                        </li>
                        <li class="mb-3 d-flex align-items-start">
                            <span class="material-icons text-danger me-3">check_circle</span>
                            <div>
                                <strong>Bảo mật cao:</strong> Dữ liệu được mã hóa và sao lưu tự động
                            </div>
                        </li>
                        <li class="mb-3 d-flex align-items-start">
                            <span class="material-icons text-danger me-3">check_circle</span>
                            <div>
                                <strong>Hỗ trợ 24/7:</strong> Đội ngũ kỹ thuật sẵn sàng hỗ trợ mọi lúc
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="col-md-6">
                    <img src="https://via.placeholder.com/600x400/93000b/ffffff?text=KiotRetail+Dashboard"
                         alt="Dashboard" class="img-fluid rounded shadow">
                </div>
            </div>
        </div>
    </section>

    <!-- CTA Section -->
    <section class="py-5 bg-danger text-white text-center">
        <div class="container">
            <h2 class="fw-bold mb-4">Sẵn sàng bắt đầu?</h2>
            <p class="lead mb-4">Đăng nhập ngay để trải nghiệm hệ thống</p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-light btn-lg px-5">
                Đăng nhập ngay
            </a>
        </div>
    </section>

    <!-- Footer -->
    <footer class="py-4 bg-dark text-white text-center">
        <div class="container">
            <p class="mb-0">© 2024 KiotRetail. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
