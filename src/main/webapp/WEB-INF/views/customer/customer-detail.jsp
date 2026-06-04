<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="customers" scope="request" />
<c:set var="detailTab" value="${param.tab}" />
<c:if test="${empty detailTab}">
    <c:set var="detailTab" value="general" />
</c:if>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    .cd-shell { background:#f4f6f9; min-height:calc(100vh - 64px); padding:16px 24px 32px; }
    .cd-top { margin-bottom:18px; }
    .cd-breadcrumb { display:flex; align-items:center; gap:8px; width:max-content; max-width:100%; background:#fff; border:1px solid #eef0f3; border-radius:8px; padding:8px 12px; box-shadow:0 1px 2px rgba(15,23,42,.04); font-size:13px; color:#5f6368; margin-bottom:12px; }
    .cd-breadcrumb a { color:#0033a0; text-decoration:none; font-weight:600; }
    .cd-code { background:#0033a0; color:#fff; padding:6px 14px; border-radius:6px; font-weight:700; box-shadow:0 2px 6px rgba(0,51,160,.18); }
    .cd-actions { display:flex; flex-wrap:wrap; gap:8px; }
    .cd-btn { display:inline-flex; align-items:center; gap:8px; padding:9px 18px; border-radius:6px; border:1px solid #d1d5db; background:#fff; color:#374151; font-weight:700; font-size:13px; text-decoration:none; box-shadow:0 1px 2px rgba(15,23,42,.06); transition:background .15s,border-color .15s,opacity .15s; }
    .cd-btn:hover { background:#f9fafb; border-color:#9ca3af; }
    .cd-btn.primary { background:#0033a0; border-color:#0033a0; color:#fff; box-shadow:0 4px 10px rgba(0,51,160,.2); }
    .cd-btn.primary:hover { opacity:.92; }
    .cd-btn.danger { color:#dc2626; border-color:#fecaca; }
    .cd-btn.danger:hover { background:#fef2f2; border-color:#fca5a5; }
    .cd-title { display:flex; align-items:center; gap:12px; margin-bottom:14px; }
    .cd-title h1 { margin:0; color:#374151; font-size:22px; line-height:1.2; font-weight:800; }
    .cd-label { display:inline-flex; align-items:center; height:24px; padding:0 9px; border-radius:5px; border:1px solid #d1d5db; background:#e5e7eb; color:#4b5563; font-weight:700; font-size:12px; }
    .cd-tabs { display:flex; gap:24px; padding:0 16px; background:#fff; border:1px solid #e5e7eb; border-bottom:0; border-radius:10px 10px 0 0; box-shadow:0 1px 2px rgba(15,23,42,.05); }
    .cd-tab { padding:14px 2px 12px; border-bottom:2px solid transparent; color:#6b7280; font-weight:700; text-decoration:none; font-size:14px; }
    .cd-tab.active { border-color:#0033a0; color:#0033a0; }
    .cd-card { background:#fff; border:1px solid #e5e7eb; border-radius:0 0 10px 10px; box-shadow:0 1px 2px rgba(15,23,42,.05); padding:24px; }
    .cd-section { margin-bottom:30px; }
    .cd-section:last-child { margin-bottom:0; }
    .cd-section-title { color:#0033a0; font-size:18px; font-weight:800; margin:0 0 20px; padding-bottom:10px; border-bottom:1px solid #e5e7eb; }
    .cd-grid { display:grid; grid-template-columns:minmax(0,1fr) minmax(0,1fr); gap:18px; }
    .cd-field { background:#f9fafb; border:1px solid #e5e7eb; border-radius:10px; padding:14px 16px; min-height:76px; }
    .cd-field.wide { grid-column:1 / -1; }
    .cd-field-label { display:block; color:#6b7280; font-size:12px; font-weight:700; margin-bottom:8px; text-transform:uppercase; letter-spacing:.02em; }
    .cd-field-value { color:#111827; font-size:15px; font-weight:700; overflow-wrap:anywhere; }
    .cd-muted { color:#9ca3af; font-weight:600; }
    .cd-history-list { display:flex; flex-direction:column; gap:12px; }
    .cd-history-item { display:grid; grid-template-columns:160px minmax(0,1fr); gap:16px; background:#f9fafb; border:1px solid #e5e7eb; border-radius:10px; padding:16px; }
    .cd-history-time { color:#6b7280; font-size:13px; font-weight:700; }
    .cd-history-title { color:#111827; font-weight:800; margin-bottom:4px; }
    .cd-history-desc { color:#6b7280; line-height:1.5; }
    @media (max-width: 900px) {
        .cd-shell { padding:12px; }
        .cd-grid, .cd-history-item { grid-template-columns:1fr; }
        .cd-field.wide { grid-column:auto; }
        .cd-tabs { overflow-x:auto; gap:18px; }
    }
</style>

<div class="cd-shell">
    <jsp:include page="../common/toast.jsp" />

    <div class="cd-top">
        <div class="cd-breadcrumb">
            <span>Khách hàng: <a href="${ctx}/admin/customers">Danh sách</a></span>
            <span class="cd-code">KH<fmt:formatNumber value="${customer.customerId}" pattern="00000" /></span>
        </div>
        <div class="cd-actions">
            <a class="cd-btn" href="${ctx}/admin/customers">← Quay lại</a>
            <a class="cd-btn primary" href="${ctx}/admin/customers?action=edit&id=${customer.customerId}">✎ Chỉnh sửa</a>
            <a class="cd-btn danger" href="${ctx}/admin/customers?action=delete&id=${customer.customerId}" onclick="return confirm('Bạn có chắc chắn muốn xóa khách hàng này?');">✕ Xóa</a>
        </div>
    </div>

    <div class="cd-title">
        <h1><c:out value="${customer.fullName}" /></h1>
        <c:if test="${not empty customer.membershipTier}">
            <c:set var="tierLower" value="${fn:toLowerCase(customer.membershipTier)}" />
            <span class="cd-label kr-tier ${tierLower}"><c:out value="${customer.membershipTier}" /></span>
        </c:if>
    </div>

    <div class="cd-tabs">
        <a class="cd-tab ${detailTab == 'general' ? 'active' : ''}" href="${ctx}/admin/customers?action=detail&id=${customer.customerId}&tab=general">Thông tin chung</a>
        <a class="cd-tab ${detailTab == 'history' ? 'active' : ''}" href="${ctx}/admin/customers?action=detail&id=${customer.customerId}&tab=history">Lịch sử hoạt động</a>
    </div>

    <div class="cd-card">
        <c:choose>
            <c:when test="${detailTab == 'history'}">
                <section class="cd-section">
                    <h2 class="cd-section-title">Lịch sử hoạt động</h2>
                    <div class="cd-history-list">
                        <div class="cd-history-item">
                            <div class="cd-history-time">
                                <c:choose>
                                    <c:when test="${not empty customer.createdAt}">
                                        <fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </c:when>
                                    <c:otherwise>Chưa có thời gian</c:otherwise>
                                </c:choose>
                            </div>
                            <div>
                                <div class="cd-history-title">Tạo hồ sơ khách hàng</div>
                                <div class="cd-history-desc">Hồ sơ khách hàng KH<fmt:formatNumber value="${customer.customerId}" pattern="00000" /> được ghi nhận trong hệ thống.</div>
                            </div>
                        </div>
                        <div class="cd-history-item">
                            <div class="cd-history-time">Hiện tại</div>
                            <div>
                                <div class="cd-history-title">Trạng thái hồ sơ</div>
                                <div class="cd-history-desc">Khách hàng đang thuộc hạng <strong><c:out value="${customer.membershipTier}" default="member" /></strong> với <strong><fmt:formatNumber value="${customer.points}" type="number" maxFractionDigits="0" /></strong> điểm tích lũy.</div>
                            </div>
                        </div>
                    </div>
                </section>
            </c:when>
            <c:otherwise>
                <section class="cd-section">
                    <h2 class="cd-section-title">Thông tin khách hàng</h2>
                    <div class="cd-grid">
                        <div class="cd-field">
                            <span class="cd-field-label">Mã khách hàng</span>
                            <div class="cd-field-value">KH<fmt:formatNumber value="${customer.customerId}" pattern="00000" /></div>
                        </div>
                        <div class="cd-field">
                            <span class="cd-field-label">Họ tên</span>
                            <div class="cd-field-value"><c:out value="${customer.fullName}" /></div>
                        </div>
                        <div class="cd-field">
                            <span class="cd-field-label">Số điện thoại</span>
                            <div class="cd-field-value"><c:out value="${customer.phone}" /></div>
                        </div>
                        <div class="cd-field">
                            <span class="cd-field-label">Email</span>
                            <div class="cd-field-value"><c:out value="${customer.email}" default="Chưa cập nhật" /></div>
                        </div>
                        <div class="cd-field wide">
                            <span class="cd-field-label">Địa chỉ</span>
                            <div class="cd-field-value"><c:out value="${customer.address}" default="Chưa cập nhật" /></div>
                        </div>
                    </div>
                </section>

                <section class="cd-section">
                    <h2 class="cd-section-title">Thông tin thành viên</h2>
                    <div class="cd-grid">
                        <div class="cd-field">
                            <span class="cd-field-label">Ngày sinh</span>
                            <div class="cd-field-value">
                                <c:choose>
                                    <c:when test="${not empty customer.dateOfBirth}">
                                        <fmt:formatDate value="${customer.dateOfBirth}" pattern="dd/MM/yyyy" />
                                    </c:when>
                                    <c:otherwise><span class="cd-muted">Chưa cập nhật</span></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="cd-field">
                            <span class="cd-field-label">Giới tính</span>
                            <div class="cd-field-value">
                                <c:choose>
                                    <c:when test="${customer.gender == 'Male' || customer.gender == 'M'}">Nam</c:when>
                                    <c:when test="${customer.gender == 'Female' || customer.gender == 'F'}">Nữ</c:when>
                                    <c:otherwise><c:out value="${customer.gender}" default="Chưa cập nhật" /></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="cd-field">
                            <span class="cd-field-label">Hạng thành viên</span>
                            <div class="cd-field-value"><c:out value="${customer.membershipTier}" default="member" /></div>
                        </div>
                        <div class="cd-field">
                            <span class="cd-field-label">Điểm tích lũy</span>
                            <div class="cd-field-value"><fmt:formatNumber value="${customer.points}" type="number" maxFractionDigits="0" /></div>
                        </div>
                        <div class="cd-field wide">
                            <span class="cd-field-label">Ngày tạo</span>
                            <div class="cd-field-value">
                                <c:choose>
                                    <c:when test="${not empty customer.createdAt}">
                                        <fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </c:when>
                                    <c:otherwise><span class="cd-muted">Chưa cập nhật</span></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </section>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
