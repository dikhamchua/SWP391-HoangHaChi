<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Chọn vai trò"/>
</jsp:include>

<div class="min-vh-100 d-flex align-items-center justify-content-center bg-light">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="text-center mb-5">
                    <h2 class="fw-bold mb-3">Chào mừng, ${sessionScope.employeeName}!</h2>
                    <p class="text-muted">Vui lòng chọn vai trò để tiếp tục</p>
                </div>

                <div class="row g-4">
                    <!-- POS Role -->
                    <c:if test="${sessionScope.canAccessPos}">
                    <div class="col-md-6">
                        <form method="post" action="${pageContext.request.contextPath}/role-selection">
                            <input type="hidden" name="role" value="pos">
                            <button type="submit" class="card h-100 border-0 shadow-sm text-decoration-none w-100 p-0" style="background: none; border: none;">
                                <div class="card-body text-center p-5">
                                    <div class="mb-4">
                                        <div class="d-inline-flex align-items-center justify-content-center bg-danger bg-opacity-10 rounded-circle"
                                             style="width: 100px; height: 100px;">
                                            <span class="material-icons text-danger" style="font-size: 56px;">point_of_sale</span>
                                        </div>
                                    </div>
                                    <h4 class="fw-bold mb-3">Bán hàng (POS)</h4>
                                    <p class="text-muted mb-0">
                                        Giao diện bán hàng nhanh chóng, xử lý đơn hàng và thanh toán
                                    </p>
                                </div>
                            </button>
                        </form>
                    </div>
                    </c:if>

                    <!-- Admin Role -->
                    <c:if test="${sessionScope.canAccessManagementArea}">
                    <div class="col-md-6">
                        <form method="post" action="${pageContext.request.contextPath}/role-selection">
                            <input type="hidden" name="role" value="admin">
                            <button type="submit" class="card h-100 border-0 shadow-sm text-decoration-none w-100 p-0" style="background: none; border: none;">
                                <div class="card-body text-center p-5">
                                    <div class="mb-4">
                                        <div class="d-inline-flex align-items-center justify-content-center bg-primary bg-opacity-10 rounded-circle"
                                             style="width: 100px; height: 100px;">
                                            <span class="material-icons text-primary" style="font-size: 56px;">dashboard</span>
                                        </div>
                                    </div>
                                    <h4 class="fw-bold mb-3">Quản lý</h4>
                                    <p class="text-muted mb-0">
                                        Quản lý hàng hóa, nhân viên, báo cáo và các chức năng quản trị
                                    </p>
                                </div>
                            </button>
                        </form>
                    </div>
                    </c:if>
                </div>

                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/logout" class="text-muted text-decoration-none">
                        <span class="material-icons align-middle" style="font-size: 20px;">logout</span>
                        Đăng xuất
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.card:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.15) !important;
    transition: all 0.3s ease;
}
</style>

<jsp:include page="../common/footer.jsp"/>
