<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="customers" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-page-header">
        <h1 class="kr-page-title">Chi tiết khách hàng</h1>
        <div style="display:flex;gap:8px;">
            <a href="${ctx}/admin/customers?action=edit&id=${customer.customerId}" class="kr-btn kr-btn-primary">Chỉnh sửa</a>
            <a href="${ctx}/admin/customers" class="kr-btn">Quay lại</a>
        </div>
    </div>

    <div class="kr-content" style="max-width:600px;padding:24px;">
        <table class="kr-table" style="font-size:14px;">
            <tbody>
                <tr><td style="font-weight:600;width:160px;">Mã KH</td><td>KH<fmt:formatNumber value="${customer.customerId}" pattern="00000" /></td></tr>
                <tr><td style="font-weight:600;">Họ tên</td><td><c:out value="${customer.fullName}" /></td></tr>
                <tr><td style="font-weight:600;">Số điện thoại</td><td><c:out value="${customer.phone}" /></td></tr>
                <tr><td style="font-weight:600;">Email</td><td><c:out value="${customer.email}" /></td></tr>
                <tr><td style="font-weight:600;">Địa chỉ</td><td><c:out value="${customer.address}" /></td></tr>
                <tr>
                    <td style="font-weight:600;">Ngày sinh</td>
                    <td>
                        <c:if test="${not empty customer.dateOfBirth}">
                            <fmt:formatDate value="${customer.dateOfBirth}" pattern="dd/MM/yyyy" />
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td style="font-weight:600;">Giới tính</td>
                    <td>
                        <c:choose>
                            <c:when test="${customer.gender == 'Male'}">Nam</c:when>
                            <c:when test="${customer.gender == 'Female'}">Nữ</c:when>
                            <c:otherwise><c:out value="${customer.gender}" /></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td style="font-weight:600;">Hạng thành viên</td>
                    <td>
                        <c:if test="${not empty customer.membershipTier}">
                            <span class="kr-tier ${customer.membershipTier}"><c:out value="${customer.membershipTier}" /></span>
                        </c:if>
                    </td>
                </tr>
                <tr><td style="font-weight:600;">Điểm tích lũy</td><td><fmt:formatNumber value="${customer.points}" type="number" maxFractionDigits="0" /></td></tr>
                <tr>
                    <td style="font-weight:600;">Ngày tạo</td>
                    <td>
                        <c:if test="${not empty customer.createdAt}">
                            <fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                        </c:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
