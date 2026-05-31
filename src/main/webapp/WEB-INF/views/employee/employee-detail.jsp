<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="employees" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-page-header">
        <h1 class="kr-page-title">Chi tiết nhân viên</h1>
        <div style="display:flex;gap:8px;">
            <a href="${ctx}/admin/employees?action=edit&id=${employee.employeeId}" class="kr-btn kr-btn-primary">Chỉnh sửa</a>
            <a href="${ctx}/admin/employees" class="kr-btn">Quay lại</a>
        </div>
    </div>

    <div class="kr-content" style="max-width:600px;padding:24px;">
        <table class="kr-table" style="font-size:14px;">
            <tbody>
                <tr><td style="font-weight:600;width:160px;">Mã NV</td><td>NV<fmt:formatNumber value="${employee.employeeId}" pattern="00000" /></td></tr>
                <tr><td style="font-weight:600;">Họ tên</td><td><c:out value="${employee.fullName}" /></td></tr>
                <tr><td style="font-weight:600;">Email</td><td><c:out value="${employee.email}" /></td></tr>
                <tr><td style="font-weight:600;">Số điện thoại</td><td><c:out value="${employee.phone}" /></td></tr>
                <tr><td style="font-weight:600;">Vai trò</td><td><c:out value="${employee.roleName}" /></td></tr>
                <tr><td style="font-weight:600;">Chi nhánh</td><td><c:out value="${employee.branchName}" /></td></tr>
                <tr>
                    <td style="font-weight:600;">Trạng thái</td>
                    <td>
                        <c:choose>
                            <c:when test="${employee.status == 'active'}">
                                <span class="kr-status active">Đang làm việc</span>
                            </c:when>
                            <c:otherwise>
                                <span class="kr-status inactive">Đã nghỉ</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td style="font-weight:600;">Ngày tạo</td>
                    <td>
                        <c:if test="${not empty employee.createdAt}">
                            <fmt:formatDate value="${employee.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                        </c:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
