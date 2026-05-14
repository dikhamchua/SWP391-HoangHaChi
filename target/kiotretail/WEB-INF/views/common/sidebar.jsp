<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- SideNavBar Component -->
<nav class="sidebar bg-light border-end shadow-sm" id="sidebar">
    <div class="sidebar-header p-4 border-bottom">
        <div class="d-flex align-items-center gap-3 mb-3">
            <div class="avatar-circle bg-primary text-white d-flex align-items-center justify-center" style="width: 40px; height: 40px; border-radius: 50%;">
                <span class="material-icons">store</span>
            </div>
            <div>
                <h6 class="mb-0 fw-bold text-primary">Hệ thống Quản lý</h6>
                <small class="text-muted">${sessionScope.branchName}</small>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/pos/sale" class="btn btn-danger w-100 d-flex align-items-center justify-content-center gap-2">
            <span class="material-icons">point_of_sale</span>
            Vào POS
        </a>
    </div>

    <div class="sidebar-menu flex-grow-1 overflow-auto py-2">
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
                    <span class="material-icons">dashboard</span>
                    <span>Tổng quan</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.active == 'products' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">
                    <span class="material-icons">inventory_2</span>
                    <span>Hàng hóa</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.active == 'invoices' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/invoices">
                    <span class="material-icons">receipt_long</span>
                    <span>Giao dịch</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.active == 'customers' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/customers">
                    <span class="material-icons">group</span>
                    <span>Đối tác</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.active == 'employees' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/employees">
                    <span class="material-icons">badge</span>
                    <span>Nhân viên</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.active == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reports">
                    <span class="material-icons">analytics</span>
                    <span>Báo cáo</span>
                </a>
            </li>
        </ul>
    </div>

    <div class="sidebar-footer border-top p-3">
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/role-selection">
                    <span class="material-icons">settings</span>
                    <span>Chuyển vai trò</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <span class="material-icons">logout</span>
                    <span>Đăng xuất</span>
                </a>
            </li>
        </ul>
    </div>
</nav>
