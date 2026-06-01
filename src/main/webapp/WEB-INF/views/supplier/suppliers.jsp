<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="suppliers" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Nhà cung cấp</h1>
    </div>

    <form method="get" action="${ctx}/admin/suppliers" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="Tìm theo tên, số điện thoại, email, địa chỉ" />
            <button type="submit" title="Tìm kiếm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>

        <a href="${ctx}/admin/suppliers?action=create" class="kr-btn kr-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Tạo mới
        </a>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUAN LY NHA CUNG CAP</div>
            <a href="${ctx}/admin/suppliers" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#0070f4; font-weight:600; background:#e6f1fe; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 3h5v5"/><path d="M8 21H3v-5"/><path d="M21 3l-7 7"/><path d="M3 21l7-7"/></svg>
                Danh sách nhà cung cấp
            </a>
            <a href="${ctx}/admin/suppliers?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới nhà cung cấp
            </a>
        </aside>

        <section class="kr-content">
            <div class="kr-table-wrap">
                <table class="kr-table">
                    <thead>
                        <tr>
                            <th class="kr-col-code">Mã NCC</th>
                            <th>Tên nhà cung cấp</th>
                            <th>Điện thoại</th>
                            <th>Email</th>
                            <th>Địa chỉ</th>
                            <th>Trạng thái</th>
                            <th class="kr-col-time">Ngày tạo</th>
                            <th style="width:120px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="8" class="kr-empty">Không có nhà cung cấp nào.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="supplier" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <a class="kr-code-link" href="${ctx}/admin/suppliers?action=view&id=${supplier.supplierId}">
                                                NCC<fmt:formatNumber value="${supplier.supplierId}" pattern="00000" />
                                            </a>
                                        </td>
                                        <td><c:out value="${supplier.name}" /></td>
                                        <td><c:out value="${supplier.phone}" /></td>
                                        <td><c:out value="${supplier.email}" /></td>
                                        <td><c:out value="${supplier.address}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${supplier.status == 'active'}">
                                                    <span style="display:inline-block;padding:2px 8px;border-radius:10px;background:#e6f4ea;color:#137333;font-size:12px;font-weight:600;">Hoạt động</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:inline-block;padding:2px 8px;border-radius:10px;background:#fce8e6;color:#a50e0e;font-size:12px;font-weight:600;">Ngừng</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="kr-col-time">
                                            <c:if test="${not empty supplier.createdAt}">
                                                <fmt:formatDate value="${supplier.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="${ctx}/admin/suppliers?action=edit&id=${supplier.supplierId}" style="color:#0070f4;text-decoration:none;font-size:13px;margin-right:8px;">Sửa</a>
                                            <form method="post" action="${ctx}/admin/suppliers" style="display:inline;" onsubmit="return confirm('Xóa nhà cung cấp này?');">
                                                <input type="hidden" name="action" value="delete" />
                                                <input type="hidden" name="supplierId" value="${supplier.supplierId}" />
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
                <c:url var="baseUrl" value="/admin/suppliers" scope="request">
                    <c:param name="keyword" value="${keyword}" />
                </c:url>
                <jsp:include page="../common/pagination.jsp" />
            </div>
        </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
