<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Tổng quan"/>
</jsp:include>

<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="active" value="dashboard"/>
    </jsp:include>

    <div class="main-content flex-grow-1">
        <!-- Top Bar -->
        <div class="top-bar">
            <div>
                <h4 class="mb-1 fw-bold">Tổng quan</h4>
                <p class="text-muted mb-0">Chào mừng trở lại, ${sessionScope.employeeName}!</p>
            </div>
            <div class="d-flex align-items-center gap-3">
                <div class="text-end">
                    <small class="text-muted d-block">Chi nhánh</small>
                    <strong>${sessionScope.branchName}</strong>
                </div>
                <div class="text-end">
                    <small class="text-muted d-block">Vai trò</small>
                    <strong>${sessionScope.roleName}</strong>
                </div>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="row g-4 mb-4">
            <div class="col-md-3">
                <div class="stats-card">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <div class="stats-label mb-2">Doanh thu hôm nay</div>
                            <div class="stats-value">45.2M</div>
                            <small class="text-success">
                                <i class="material-icons align-middle" style="font-size: 16px;">trending_up</i>
                                +12.5%
                            </small>
                        </div>
                        <div class="icon-wrapper bg-success bg-opacity-10">
                            <span class="material-icons text-success">payments</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stats-card">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <div class="stats-label mb-2">Đơn hàng</div>
                            <div class="stats-value">128</div>
                            <small class="text-success">
                                <i class="material-icons align-middle" style="font-size: 16px;">trending_up</i>
                                +8.2%
                            </small>
                        </div>
                        <div class="icon-wrapper bg-info bg-opacity-10">
                            <span class="material-icons text-info">receipt_long</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stats-card">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <div class="stats-label mb-2">Sản phẩm</div>
                            <div class="stats-value">${totalProducts}</div>
                            <small class="text-warning">
                                <i class="material-icons align-middle" style="font-size: 16px;">warning</i>
                                ${lowStockProducts} sắp hết
                            </small>
                        </div>
                        <div class="icon-wrapper bg-warning bg-opacity-10">
                            <span class="material-icons text-warning">inventory_2</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stats-card">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <div class="stats-label mb-2">Khách hàng</div>
                            <div class="stats-value">1,245</div>
                            <small class="text-success">
                                <i class="material-icons align-middle" style="font-size: 16px;">trending_up</i>
                                +15 mới
                            </small>
                        </div>
                        <div class="icon-wrapper bg-primary bg-opacity-10">
                            <span class="material-icons text-primary">group</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts Row -->
        <div class="row g-4 mb-4">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h6 class="mb-0 fw-bold">Doanh thu 7 ngày qua</h6>
                        <select class="form-select form-select-sm" style="width: auto;">
                            <option>7 ngày qua</option>
                            <option>30 ngày qua</option>
                            <option>Tháng này</option>
                        </select>
                    </div>
                    <div class="card-body">
                        <div class="text-center py-5">
                            <span class="material-icons text-muted" style="font-size: 64px;">show_chart</span>
                            <p class="text-muted mt-3">Biểu đồ doanh thu sẽ hiển thị ở đây</p>
                            <small class="text-muted">Tích hợp Chart.js hoặc thư viện biểu đồ khác</small>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h6 class="mb-0 fw-bold">Sản phẩm bán chạy</h6>
                    </div>
                    <div class="card-body">
                        <div class="d-flex align-items-center mb-3 pb-3 border-bottom">
                            <div class="flex-grow-1">
                                <div class="fw-semibold">iPhone 15 Pro Max</div>
                                <small class="text-muted">45 đã bán</small>
                            </div>
                            <span class="badge badge-success">Top 1</span>
                        </div>
                        <div class="d-flex align-items-center mb-3 pb-3 border-bottom">
                            <div class="flex-grow-1">
                                <div class="fw-semibold">Tai nghe Sony WH-1000XM5</div>
                                <small class="text-muted">32 đã bán</small>
                            </div>
                            <span class="badge badge-info">Top 2</span>
                        </div>
                        <div class="d-flex align-items-center">
                            <div class="flex-grow-1">
                                <div class="fw-semibold">Cáp sạc nhanh 20W</div>
                                <small class="text-muted">28 đã bán</small>
                            </div>
                            <span class="badge badge-warning">Top 3</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Activities -->
        <div class="row g-4">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h6 class="mb-0 fw-bold">Giao dịch gần đây</h6>
                        <a href="${pageContext.request.contextPath}/admin/invoices" class="text-decoration-none">
                            Xem tất cả
                        </a>
                    </div>
                    <div class="card-body">
                        <div class="d-flex align-items-center mb-3 pb-3 border-bottom">
                            <div class="icon-wrapper bg-success bg-opacity-10 me-3" style="width: 40px; height: 40px;">
                                <span class="material-icons text-success" style="font-size: 20px;">shopping_cart</span>
                            </div>
                            <div class="flex-grow-1">
                                <div class="fw-semibold">HD00124</div>
                                <small class="text-muted">Nguyễn Văn An - 2 phút trước</small>
                            </div>
                            <div class="text-end">
                                <div class="fw-semibold text-success">+2.5M</div>
                            </div>
                        </div>
                        <div class="d-flex align-items-center mb-3 pb-3 border-bottom">
                            <div class="icon-wrapper bg-success bg-opacity-10 me-3" style="width: 40px; height: 40px;">
                                <span class="material-icons text-success" style="font-size: 20px;">shopping_cart</span>
                            </div>
                            <div class="flex-grow-1">
                                <div class="fw-semibold">HD00123</div>
                                <small class="text-muted">Trần Thị Bích - 15 phút trước</small>
                            </div>
                            <div class="text-end">
                                <div class="fw-semibold text-success">+1.2M</div>
                            </div>
                        </div>
                        <div class="d-flex align-items-center">
                            <div class="icon-wrapper bg-danger bg-opacity-10 me-3" style="width: 40px; height: 40px;">
                                <span class="material-icons text-danger" style="font-size: 20px;">keyboard_return</span>
                            </div>
                            <div class="flex-grow-1">
                                <div class="fw-semibold">HD00122</div>
                                <small class="text-muted">Lê Văn Cường - 1 giờ trước</small>
                            </div>
                            <div class="text-end">
                                <div class="fw-semibold text-danger">-850K</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h6 class="mb-0 fw-bold">Cảnh báo tồn kho</h6>
                        <a href="${pageContext.request.contextPath}/admin/products" class="text-decoration-none">
                            Xem tất cả
                        </a>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${lowStockProducts > 0 || outOfStockProducts > 0}">
                                <div class="alert alert-warning d-flex align-items-center mb-3">
                                    <span class="material-icons me-2">warning</span>
                                    <div>
                                        <strong>${lowStockProducts}</strong> sản phẩm sắp hết hàng
                                    </div>
                                </div>
                                <c:if test="${outOfStockProducts > 0}">
                                    <div class="alert alert-danger d-flex align-items-center">
                                        <span class="material-icons me-2">error</span>
                                        <div>
                                            <strong>${outOfStockProducts}</strong> sản phẩm đã hết hàng
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-4">
                                    <span class="material-icons text-success" style="font-size: 48px;">check_circle</span>
                                    <p class="text-muted mt-2 mb-0">Tất cả sản phẩm đều còn hàng</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
