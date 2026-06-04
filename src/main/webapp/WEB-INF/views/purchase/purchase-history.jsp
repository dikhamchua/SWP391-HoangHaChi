<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="purchases" scope="request" />
<c:set var="pageTitle" value="Lich su phieu nhap" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    .ph-page { padding: 20px 24px; background: #f5f7fa; min-height: calc(100vh - 100px); }
    .ph-breadcrumb { font-size: 13px; color: #6b7280; margin-bottom: 6px; }
    .ph-breadcrumb a { color: #003399; text-decoration: none; font-weight: 500; }
    .ph-page-header { margin-bottom: 16px; padding-bottom: 14px; border-bottom: 1px solid #E5E7EB; }
    .ph-page-title { font-size: 22px; font-weight: 700; color: #111827; margin: 0; }
    .ph-page-subtitle { font-size: 13px; color: #6b7280; margin-top: 4px; }
    .ph-stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px; }
    .ph-stat-card { background: #fff; border: 1px solid #E5E7EB; border-radius: 8px; padding: 14px 16px; display: flex; align-items: center; gap: 12px; }
    .ph-stat-icon { width: 40px; height: 40px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 18px; }
    .ph-stat-icon.total { background: #e0e7ff; color: #003399; }
    .ph-stat-icon.approved { background: #dcfce7; color: #15803d; }
    .ph-stat-icon.rejected { background: #fee2e2; color: #991b1b; }
    .ph-stat-icon.cancelled { background: #f1f3f4; color: #6b7280; }
    .ph-stat-label { font-size: 12px; color: #6b7280; font-weight: 500; }
    .ph-stat-value { font-size: 22px; color: #111827; font-weight: 700; line-height: 1.2; }
    .ph-layout { display: grid; grid-template-columns: 220px 1fr; gap: 16px; align-items: flex-start; }
    .ph-sidebar { background: #fff; border: 1px solid #E5E7EB; border-radius: 8px; padding: 16px; position: sticky; top: 16px; }
    .ph-sidebar-title { font-size: 13px; font-weight: 700; color: #111827; margin: 0 0 12px; text-transform: uppercase; }
    .ph-filter-block { margin-bottom: 16px; }
    .ph-filter-label { display: block; font-size: 12px; font-weight: 600; color: #4b5563; margin-bottom: 6px; }
    .ph-filter-select, .ph-filter-date { width: 100%; height: 36px; padding: 0 10px; box-sizing: border-box; border: 1px solid #E5E7EB; border-radius: 6px; background: #fff; font-size: 13px; color: #111827; outline: none; }
    .ph-filter-select:focus, .ph-filter-date:focus { border-color: #003399; }
    .ph-filter-actions { display: flex; gap: 6px; margin-top: 8px; }
    .ph-btn { height: 38px; padding: 0 18px; border-radius: 6px; font-size: 14px; font-weight: 600; display: inline-flex; align-items: center; gap: 6px; cursor: pointer; text-decoration: none; border: 1px solid transparent; }
    .ph-btn-primary { background: #003399; color: #fff; }
    .ph-btn-ghost { background: #fff; color: #4b5563; border-color: #E5E7EB; }
    .ph-btn-outline { background: #fff; color: #003399; border-color: #003399; }
    .ph-content { background: #fff; border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
    .ph-content-header { padding: 14px 16px; border-bottom: 1px solid #E5E7EB; display: flex; align-items: center; justify-content: space-between; background: #fafbfc; }
    .ph-content-title { font-size: 15px; font-weight: 600; color: #111827; margin: 0; }
    .ph-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #111827; }
    .ph-table thead th { background: #f9fafb; border-bottom: 1px solid #E5E7EB; font-size: 12px; font-weight: 600; text-transform: uppercase; text-align: left; padding: 10px 14px; color: #4b5563; }
    .ph-table tbody td { padding: 12px 14px; border-bottom: 1px solid #f3f4f6; vertical-align: middle; }
    .ph-table tbody tr:hover { background: #f9fafb; }
    .ph-doc-link { color: #003399; font-weight: 600; text-decoration: none; }
    .ph-doc-link:hover { text-decoration: underline; }
    .ph-action-pill { display: inline-flex; align-items: center; gap: 6px; padding: 3px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
    .ph-action-approve { background: #dcfce7; color: #15803d; }
    .ph-action-reject { background: #fee2e2; color: #991b1b; }
    .ph-action-cancel { background: #f1f3f4; color: #4b5563; }
    .ph-action-submit { background: #e0e7ff; color: #003399; }
    .ph-action-default { background: #f1f3f4; color: #4b5563; }
    .ph-reason-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #4b5563; font-size: 13px; }
    .ph-reason-cell.muted { color: #9ca3af; font-style: italic; }
    .ph-empty { padding: 60px 20px; text-align: center; color: #6b7280; }
    .ph-empty-title { font-size: 15px; font-weight: 600; color: #4b5563; margin-bottom: 4px; }
    .ph-pagination-wrap { padding: 0 16px; border-top: 1px solid #E5E7EB; }
    @media (max-width: 1024px) { .ph-layout { grid-template-columns: 1fr; } .ph-stats-grid { grid-template-columns: repeat(2, 1fr); } .ph-sidebar { position: static; } }
</style>

<div class="ph-page">
    <jsp:include page="../common/toast.jsp" />

    <div class="ph-breadcrumb">
        <a href="${ctx}/admin/dashboard">Tong quan</a> <span style="margin:0 6px;">/</span>
        <a href="${ctx}/admin/purchases">Nhap hang</a> <span style="margin:0 6px;">/</span>
        <span>Lich su</span>
    </div>

    <div class="ph-page-header">
        <h1 class="ph-page-title">Lich su phieu nhap</h1>
        <div class="ph-page-subtitle">Tat ca cac thao tac tren phieu nhap hang</div>
    </div>

    <div class="ph-stats-grid">
        <div class="ph-stat-card"><div class="ph-stat-icon total">&#9776;</div><div><div class="ph-stat-label">Tong</div><div class="ph-stat-value">${stats.total}</div></div></div>
        <div class="ph-stat-card"><div class="ph-stat-icon approved">&#10004;</div><div><div class="ph-stat-label">Da duyet</div><div class="ph-stat-value">${stats.approved}</div></div></div>
        <div class="ph-stat-card"><div class="ph-stat-icon rejected">&#10006;</div><div><div class="ph-stat-label">Tu choi</div><div class="ph-stat-value">${stats.rejected}</div></div></div>
        <div class="ph-stat-card"><div class="ph-stat-icon cancelled">&#9940;</div><div><div class="ph-stat-label">Da huy</div><div class="ph-stat-value">${stats.cancelled}</div></div></div>
    </div>

    <div class="ph-layout">
        <div class="ph-sidebar">
            <div class="ph-sidebar-title">Bo loc</div>
            <form method="get" action="${ctx}/admin/purchases">
                <input type="hidden" name="action" value="history" />
                <div class="ph-filter-block">
                    <label class="ph-filter-label">Hanh dong</label>
                    <select name="historyAction" class="ph-filter-select">
                        <option value="">Tat ca</option>
                        <option value="CREATE" ${historyAction == 'CREATE' ? 'selected' : ''}>Tao phieu</option>
                        <option value="SUBMIT" ${historyAction == 'SUBMIT' ? 'selected' : ''}>Gui duyet</option>
                        <option value="APPROVE" ${historyAction == 'APPROVE' ? 'selected' : ''}>Duyet</option>
                        <option value="REJECT" ${historyAction == 'REJECT' ? 'selected' : ''}>Tu choi</option>
                        <option value="CANCEL" ${historyAction == 'CANCEL' ? 'selected' : ''}>Huy</option>
                        <option value="RECEIVE" ${historyAction == 'RECEIVE' ? 'selected' : ''}>Nhan hang</option>
                        <option value="COMPLETE" ${historyAction == 'COMPLETE' ? 'selected' : ''}>Hoan thanh</option>
                    </select>
                </div>
                <div class="ph-filter-block">
                    <label class="ph-filter-label">Tu ngay</label>
                    <input type="date" name="fromDate" class="ph-filter-date" value="<c:out value='${fromDate}'/>" />
                </div>
                <div class="ph-filter-block">
                    <label class="ph-filter-label">Den ngay</label>
                    <input type="date" name="toDate" class="ph-filter-date" value="<c:out value='${toDate}'/>" />
                </div>
                <div class="ph-filter-actions">
                    <button type="submit" class="ph-btn ph-btn-primary" style="flex:1;height:32px;font-size:12px;padding:0 8px;justify-content:center;">Loc</button>
                    <a href="${ctx}/admin/purchases?action=history" class="ph-btn ph-btn-ghost" style="flex:1;height:32px;font-size:12px;padding:0 8px;justify-content:center;">Xoa loc</a>
                </div>
            </form>
        </div>

        <div class="ph-content">
            <div class="ph-content-header">
                <h2 class="ph-content-title">Ket qua (${pageResult.totalItems})</h2>
                <a href="${ctx}/admin/purchases" class="ph-btn ph-btn-outline" style="height:32px;font-size:12px;">Danh sach phieu</a>
            </div>

            <c:choose>
                <c:when test="${empty pageResult.items}">
                    <div class="ph-empty"><div class="ph-empty-title">Khong co du lieu</div><p>Chua co lich su nao phu hop.</p></div>
                </c:when>
                <c:otherwise>
                    <table class="ph-table">
                        <thead><tr><th>Phieu</th><th>Hanh dong</th><th>Tu</th><th>Sang</th><th>Nguoi thuc hien</th><th>Ly do</th><th>Thoi gian</th></tr></thead>
                        <tbody>
                            <c:forEach var="h" items="${pageResult.items}">
                                <tr>
                                    <td><a class="ph-doc-link" href="${ctx}/admin/purchases?action=view&id=${h.purchaseOrderId}">PO-${h.purchaseOrderId}</a></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${h.action == 'APPROVE'}"><span class="ph-action-pill ph-action-approve">Duyet</span></c:when>
                                            <c:when test="${h.action == 'REJECT'}"><span class="ph-action-pill ph-action-reject">Tu choi</span></c:when>
                                            <c:when test="${h.action == 'CANCEL'}"><span class="ph-action-pill ph-action-cancel">Huy</span></c:when>
                                            <c:when test="${h.action == 'SUBMIT'}"><span class="ph-action-pill ph-action-submit">Gui duyet</span></c:when>
                                            <c:otherwise><span class="ph-action-pill ph-action-default"><c:out value="${h.action}"/></span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${h.fromStatus}" default="-"/></td>
                                    <td><c:out value="${h.toStatus}"/></td>
                                    <td><c:out value="${h.performedByName}" default="#${h.performedBy}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty h.reason}"><span class="ph-reason-cell" title="${h.reason}"><c:out value="${h.reason}"/></span></c:when>
                                            <c:otherwise><span class="ph-reason-cell muted">--</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatDate value="${h.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

            <c:if test="${pageResult.totalPages > 1}">
                <div class="ph-pagination-wrap">
                    <c:set var="page" value="${pageResult.currentPage}" scope="request" />
                    <c:set var="totalPages" value="${pageResult.totalPages}" scope="request" />
                    <c:url var="baseUrl" value="/admin/purchases" scope="request">
                        <c:param name="action" value="history"/>
                        <c:param name="historyAction" value="${historyAction}"/>
                        <c:param name="performedBy" value="${performedBy}"/>
                        <c:param name="fromDate" value="${fromDate}"/>
                        <c:param name="toDate" value="${toDate}"/>
                    </c:url>
                    <jsp:include page="../common/pagination.jsp" />
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
