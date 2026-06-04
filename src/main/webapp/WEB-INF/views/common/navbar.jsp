<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="canApprove" value="${sessionScope.roleName == 'Owner' or sessionScope.roleName == 'StoreManager'}" />
<c:set var="pendingApprovalCount" value="${sessionScope.pendingApprovalCount != null ? sessionScope.pendingApprovalCount : 0}" />

<nav class="kr-navbar">
    <ul class="nav-tabs">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-tab <c:if test='${activeTab == "dashboard"}'>active</c:if>">Tổng quan</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/products" class="nav-tab <c:if test='${activeTab == "products"}'>active</c:if>">Hàng hóa</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/invoices" class="nav-tab <c:if test='${activeTab == "invoices"}'>active</c:if>">Đơn hàng</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/customers" class="nav-tab <c:if test='${activeTab == "customers"}'>active</c:if>">Khách hàng</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/employees" class="nav-tab <c:if test='${activeTab == "employees"}'>active</c:if>">Nhân viên</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/branches" class="nav-tab <c:if test='${activeTab == "branches"}'>active</c:if>">Chi nhánh</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/suppliers" class="nav-tab <c:if test='${activeTab == "suppliers"}'>active</c:if>">Nhà cung cấp</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/purchases" class="nav-tab <c:if test='${activeTab == "purchases"}'>active</c:if>">Nhập hàng</a></li>
        <c:if test="${canApprove}">
            <li>
                <a href="${pageContext.request.contextPath}/admin/purchases?approvalStatus=PENDING_APPROVAL" class="nav-tab <c:if test='${activeTab == "approvals"}'>active</c:if>">
                    Phê duyệt
                    <c:if test="${pendingApprovalCount > 0}">
                        <span class="ap-nav-badge" title="${pendingApprovalCount} phiếu đang chờ duyệt">${pendingApprovalCount}</span>
                    </c:if>
                </a>
            </li>
        </c:if>
        <li><a href="${pageContext.request.contextPath}/admin/reports" class="nav-tab <c:if test='${activeTab == "reports"}'>active</c:if>">Báo cáo</a></li>
    </ul>
    <a href="${pageContext.request.contextPath}/pos/sale" class="sell-btn">
        <svg class="ico" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="9" cy="21" r="1"/>
            <circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
        </svg>
        Bán hàng
    </a>
</nav>
