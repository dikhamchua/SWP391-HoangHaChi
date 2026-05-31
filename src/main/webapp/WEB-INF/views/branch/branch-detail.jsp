<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="branches" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-page-header">
        <h1 class="kr-page-title">Chi tiết chi nhánh</h1>
        <div style="display:flex;gap:8px;">
            <a href="${ctx}/admin/branches?action=edit&id=${branch.branchId}" class="kr-btn kr-btn-primary">Chỉnh sửa</a>
            <a href="${ctx}/admin/branches" class="kr-btn">Quay lại</a>
        </div>
    </div>

    <div class="kr-content" style="max-width:600px;padding:24px;">
        <table class="kr-table" style="font-size:14px;">
            <tbody>
                <tr><td style="font-weight:600;width:160px;">Mã CN</td><td>CN<fmt:formatNumber value="${branch.branchId}" pattern="00000" /></td></tr>
                <tr><td style="font-weight:600;">Tên chi nhánh</td><td><c:out value="${branch.name}" /></td></tr>
                <tr><td style="font-weight:600;">Địa chỉ</td><td><c:out value="${branch.address}" /></td></tr>
                <tr><td style="font-weight:600;">Số điện thoại</td><td><c:out value="${branch.phone}" /></td></tr>
                <tr>
                    <td style="font-weight:600;">Trạng thái</td>
                    <td>
                        <c:choose>
                            <c:when test="${branch.status == 'active'}">
                                <span style="display:inline-block;padding:2px 8px;border-radius:10px;background:#e6f4ea;color:#137333;font-size:12px;font-weight:600;">Hoạt động</span>
                            </c:when>
                            <c:otherwise>
                                <span style="display:inline-block;padding:2px 8px;border-radius:10px;background:#fce8e6;color:#a50e0e;font-size:12px;font-weight:600;">Ngừng</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td style="font-weight:600;">Ngày tạo</td>
                    <td>
                        <c:if test="${not empty branch.createdAt}">
                            <fmt:formatDate value="${branch.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                        </c:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
