<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="branches" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Chi nhánh</h1>
    </div>

    <form method="get" action="${ctx}/admin/branches" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="Tìm theo tên, địa chỉ, số điện thoại" />
            <button type="submit" title="Tìm kiếm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>

        <a href="${ctx}/admin/branches?action=create" class="kr-btn kr-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Tạo mới
        </a>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUẢN LÝ CHI NHÁNH</div>
            <a href="${ctx}/admin/branches" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#0070f4; font-weight:600; background:#e6f1fe; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                Danh sách chi nhánh
            </a>
            <a href="${ctx}/admin/branches?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới chi nhánh
            </a>
        </aside>

        <section class="kr-content">
            <div class="kr-table-wrap">
                <table class="kr-table">
                    <thead>
                        <tr>
                            <th class="kr-col-code">Mã CN</th>
                            <th>Tên chi nhánh</th>
                            <th>Địa chỉ</th>
                            <th>Điện thoại</th>
                            <th>Trạng thái</th>
                            <th class="kr-col-time">Ngày tạo</th>
                            <th style="width:120px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="7" class="kr-empty">Không có chi nhánh nào.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="branch" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <a class="kr-code-link" href="${ctx}/admin/branches?action=view&id=${branch.branchId}">
                                                CN<fmt:formatNumber value="${branch.branchId}" pattern="00000" />
                                            </a>
                                        </td>
                                        <td><c:out value="${branch.name}" /></td>
                                        <td><c:out value="${branch.address}" /></td>
                                        <td><c:out value="${branch.phone}" /></td>
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
                                        <td class="kr-col-time">
                                            <c:if test="${not empty branch.createdAt}">
                                                <fmt:formatDate value="${branch.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="${ctx}/admin/branches?action=edit&id=${branch.branchId}" style="color:#0070f4;text-decoration:none;font-size:13px;margin-right:8px;">Sửa</a>
                                            <form method="post" action="${ctx}/admin/branches" style="display:inline;" onsubmit="return confirm('Xóa chi nhánh này?');">
                                                <input type="hidden" name="action" value="delete" />
                                                <input type="hidden" name="branchId" value="${branch.branchId}" />
                                                <button type="submit" style="background:none;border:none;color:#d93025;cursor:pointer;font-size:13px;padding:0;">Xóa</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="kr-pagination-wrap">
                <c:url var="baseUrl" value="/admin/branches" scope="request">
                    <c:param name="keyword" value="${keyword}" />
                </c:url>
                <jsp:include page="../common/pagination.jsp" />
            </div>
        </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
