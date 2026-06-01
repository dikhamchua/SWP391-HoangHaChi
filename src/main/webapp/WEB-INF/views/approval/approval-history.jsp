<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="approvals" scope="request" />
<c:set var="pageTitle" value="Lịch sử phê duyệt" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    .ap-page { padding: 20px 24px; background: #f5f7fa; min-height: calc(100vh - 100px); }
    .ap-breadcrumb { font-size: 13px; color: #6b7280; margin-bottom: 6px; }
    .ap-breadcrumb a { color: #003399; text-decoration: none; font-weight: 500; }
    .ap-breadcrumb a:hover { text-decoration: underline; }

    .ap-page-header {
        margin-bottom: 16px; padding-bottom: 14px; border-bottom: 1px solid #E5E7EB;
    }
    .ap-page-title { font-size: 22px; font-weight: 700; color: #111827; margin: 0; letter-spacing: -0.3px; }
    .ap-page-subtitle { font-size: 13px; color: #6b7280; margin-top: 4px; }

    /* Action toolbar — upper-left action group above search/status */
    .ap-toolbar {
        display: flex; align-items: center; gap: 8px;
        margin-bottom: 12px; flex-wrap: wrap; justify-content: flex-start;
    }

    /* Stats cards */
    .ap-stats-grid {
        display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px;
    }
    .ap-stat-card {
        background: #fff; border: 1px solid #E5E7EB; border-radius: 8px; padding: 14px 16px;
        display: flex; align-items: center; gap: 12px;
    }
    .ap-stat-icon {
        width: 40px; height: 40px; border-radius: 8px;
        display: flex; align-items: center; justify-content: center; flex-shrink: 0;
    }
    .ap-stat-icon.total { background: #e0e7ff; color: #003399; }
    .ap-stat-icon.approved { background: #dcfce7; color: #15803d; }
    .ap-stat-icon.rejected { background: #fee2e2; color: #991b1b; }
    .ap-stat-icon.cancelled { background: #f1f3f4; color: #6b7280; }
    .ap-stat-label { font-size: 12px; color: #6b7280; font-weight: 500; }
    .ap-stat-value { font-size: 22px; color: #111827; font-weight: 700; line-height: 1.2; }

    .ap-status-bar {
        display: flex; align-items: center; gap: 16px;
        background: #fff; border: 1px solid #E5E7EB; border-radius: 6px;
        padding: 10px 16px; margin-bottom: 16px; font-size: 13px; color: #4b5563;
    }
    .ap-status-bar strong { color: #003399; font-weight: 600; }
    .ap-status-bar .ap-divider { width: 1px; height: 18px; background: #E5E7EB; }

    .ap-search-row {
        background: #fff; border: 1px solid #E5E7EB; border-radius: 8px;
        padding: 12px 16px; margin-bottom: 12px;
        display: flex; align-items: center; gap: 12px;
    }
    .ap-search-input {
        flex: 1; height: 40px; padding: 0 14px; box-sizing: border-box;
        border: 1px solid #E5E7EB; border-radius: 6px;
        font-size: 14px; color: #111827; outline: none;
        transition: border-color .15s, box-shadow .15s;
    }
    .ap-search-input:focus { border-color: #003399; box-shadow: 0 0 0 3px rgba(0,51,153,.08); }
    .ap-btn {
        height: 38px; padding: 0 18px; border-radius: 6px;
        font-size: 14px; font-weight: 600; display: inline-flex; align-items: center; gap: 6px;
        cursor: pointer; text-decoration: none; border: 1px solid transparent;
        transition: background .15s, border-color .15s;
    }
    .ap-btn-primary { background: #003399; color: #fff; border-color: #003399; }
    .ap-btn-primary:hover { background: #002266; }
    .ap-btn-secondary { background: #6b7280; color: #fff; border-color: #6b7280; }
    .ap-btn-secondary:hover { background: #4b5563; }
    .ap-btn-outline { background: #fff; color: #003399; border-color: #003399; }
    .ap-btn-outline:hover { background: #f0f4ff; }
    .ap-btn-ghost { background: #fff; color: #4b5563; border-color: #E5E7EB; }
    .ap-btn-ghost:hover { background: #f9fafb; }

    .ap-layout { display: grid; grid-template-columns: 240px 1fr; gap: 16px; align-items: flex-start; }
    .ap-sidebar {
        background: #fff; border: 1px solid #E5E7EB; border-radius: 8px;
        padding: 16px; position: sticky; top: 16px;
    }
    .ap-sidebar-title { font-size: 13px; font-weight: 700; color: #111827; margin: 0 0 12px; letter-spacing: .3px; text-transform: uppercase; }
    .ap-filter-block { margin-bottom: 16px; }
    .ap-filter-block:last-child { margin-bottom: 0; }
    .ap-filter-label { display: block; font-size: 12px; font-weight: 600; color: #4b5563; margin-bottom: 6px; }
    .ap-filter-select, .ap-filter-date {
        width: 100%; height: 36px; padding: 0 10px; box-sizing: border-box;
        border: 1px solid #E5E7EB; border-radius: 6px;
        background: #fff; font-size: 13px; color: #111827; outline: none;
    }
    .ap-filter-select:focus, .ap-filter-date:focus { border-color: #003399; }
    .ap-filter-actions { display: flex; gap: 6px; margin-top: 8px; }
    .ap-filter-actions .ap-btn { flex: 1; height: 32px; font-size: 12px; padding: 0 8px; justify-content: center; }

    .ap-content {
        background: #fff; border: 1px solid #E5E7EB; border-radius: 8px;
        overflow: hidden;
    }
    .ap-content-header {
        padding: 14px 16px; border-bottom: 1px solid #E5E7EB;
        display: flex; align-items: center; justify-content: space-between;
        background: #fafbfc;
    }
    .ap-content-title { font-size: 15px; font-weight: 600; color: #111827; margin: 0; }

    .ap-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #111827; }
    .ap-table thead th {
        background: #f9fafb; border-bottom: 1px solid #E5E7EB;
        font-size: 12px; font-weight: 600; text-transform: uppercase;
        text-align: left; padding: 10px 14px; color: #4b5563; letter-spacing: .3px;
    }
    .ap-table tbody td { padding: 12px 14px; border-bottom: 1px solid #f3f4f6; vertical-align: middle; }
    .ap-table tbody tr:hover { background: #f9fafb; }
    .ap-table tbody tr:last-child td { border-bottom: none; }
    .ap-doc-link { color: #003399; font-weight: 600; text-decoration: none; }
    .ap-doc-link:hover { text-decoration: underline; }

    .ap-action-pill {
        display: inline-flex; align-items: center; gap: 6px;
        padding: 3px 10px; border-radius: 12px;
        font-size: 12px; font-weight: 600;
    }
    .ap-action-pill::before { content: ""; width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
    .ap-action-approve { background: #dcfce7; color: #15803d; }
    .ap-action-reject { background: #fee2e2; color: #991b1b; }
    .ap-action-cancel { background: #f1f3f4; color: #4b5563; }
    .ap-action-submit { background: #e0e7ff; color: #003399; }
    .ap-action-default { background: #f1f3f4; color: #4b5563; }

    .ap-chip {
        display: inline-block; padding: 3px 10px; border-radius: 4px;
        font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: .3px;
    }
    .ap-chip-purchase { background: #e0e7ff; color: #003399; }
    .ap-chip-transfer { background: #fef3c7; color: #92400e; }
    .ap-chip-invoice { background: #dcfce7; color: #15803d; }
    .ap-chip-default { background: #f1f3f4; color: #4b5563; }

    .ap-reason-cell {
        max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
        color: #4b5563; font-size: 13px;
    }
    .ap-reason-cell.muted { color: #9ca3af; font-style: italic; }

    .ap-empty { padding: 60px 20px; text-align: center; color: #6b7280; }
    .ap-empty svg { width: 48px; height: 48px; color: #d1d5db; margin-bottom: 12px; }
    .ap-empty-title { font-size: 15px; font-weight: 600; color: #4b5563; margin-bottom: 4px; }

    .ap-pagination-wrap { padding: 0 16px; border-top: 1px solid #E5E7EB; }

    @media (max-width: 1024px) {
        .ap-layout { grid-template-columns: 1fr; }
        .ap-stats-grid { grid-template-columns: repeat(2, 1fr); }
        .ap-sidebar { position: static; }
    }
</style>

<div class="ap-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="ap-breadcrumb">
        <a href="${ctx}/admin/dashboard">Tổng quan</a>
        <span style="margin:0 6px;">/</span>
        <a href="${ctx}/admin/approvals?action=pending">Phê duyệt</a>
        <span style="margin:0 6px;">/</span>
        <span>Lịch sử</span>
    </div>

    <div class="ap-page-header">
        <h1 class="ap-page-title">Lịch sử phê duyệt</h1>
        <div class="ap-page-subtitle">Tất cả các quyết định duyệt và từ chối phiếu trong hệ thống</div>
    </div>

    <div class="ap-toolbar">
        <button type="submit" form="apHistorySearchForm" class="ap-btn ap-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            Tìm kiếm
        </button>
        <a href="${ctx}/admin/approvals?action=history" class="ap-btn ap-btn-secondary">Đặt lại</a>
        <a href="${ctx}/admin/approvals?action=pending" class="ap-btn ap-btn-outline">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 12a9 9 0 1 1-9-9c2.52 0 4.93 1 6.74 2.74L21 8"/><polyline points="21 3 21 8 16 8"/>
            </svg>
            Hộp thư chờ duyệt
        </a>
    </div>

    <!-- Stats summary -->
    <div class="ap-stats-grid">
        <div class="ap-stat-card">
            <div class="ap-stat-icon total">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
                </svg>
            </div>
            <div>
                <div class="ap-stat-label">Tổng quyết định</div>
                <div class="ap-stat-value">${stats.total != null ? stats.total : 0}</div>
            </div>
        </div>
        <div class="ap-stat-card">
            <div class="ap-stat-icon approved">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="20 6 9 17 4 12"/>
                </svg>
            </div>
            <div>
                <div class="ap-stat-label">Đã duyệt</div>
                <div class="ap-stat-value">${stats.approved != null ? stats.approved : 0}</div>
            </div>
        </div>
        <div class="ap-stat-card">
            <div class="ap-stat-icon rejected">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
            </div>
            <div>
                <div class="ap-stat-label">Đã từ chối</div>
                <div class="ap-stat-value">${stats.rejected != null ? stats.rejected : 0}</div>
            </div>
        </div>
        <div class="ap-stat-card">
            <div class="ap-stat-icon cancelled">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
                </svg>
            </div>
            <div>
                <div class="ap-stat-label">Đã hủy</div>
                <div class="ap-stat-value">${stats.cancelled != null ? stats.cancelled : 0}</div>
            </div>
        </div>
    </div>

    <!-- Status bar -->
    <div class="ap-status-bar">
        <span>Hiển thị: <strong>${pageResult.totalItems != null ? pageResult.totalItems : 0}</strong> bản ghi</span>
        <span class="ap-divider"></span>
        <span>Khoảng thời gian:
            <strong>
                <c:choose>
                    <c:when test="${not empty fromDate or not empty toDate}">
                        <c:out value="<c:out value='${fromDate}'/>"/> - <c:out value="<c:out value='${toDate}'/>"/>
                    </c:when>
                    <c:otherwise>Toàn bộ</c:otherwise>
                </c:choose>
            </strong>
        </span>
        <span class="ap-divider"></span>
        <span>Người duyệt: <strong><c:out value="${approverName != null ? approverName : 'Tất cả'}"/></strong></span>
    </div>

    <!-- Search bar -->
    <form id="apHistorySearchForm" method="get" action="${ctx}/admin/approvals" class="ap-search-row">
        <input type="hidden" name="action" value="history" />
        <input type="text" name="keyword" value="<c:out value='${keyword}'/>"
               class="ap-search-input"
               placeholder="Tìm theo mã phiếu, người gửi, người duyệt, lý do..." />
    </form>

    <div class="ap-layout">

        <!-- Left filter sidebar -->
        <aside class="ap-sidebar">
            <h3 class="ap-sidebar-title">Bộ lọc</h3>
            <form method="get" action="${ctx}/admin/approvals">
                <input type="hidden" name="action" value="history" />
                <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>" />

                <div class="ap-filter-block">
                    <label class="ap-filter-label" for="f-action">Hành động</label>
                    <select id="f-action" name="historyAction" class="ap-filter-select">
                        <option value="">Tất cả hành động</option>
                        <option value="APPROVE" ${historyAction == 'APPROVE' ? 'selected' : ''}>Phê duyệt</option>
                        <option value="REJECT" ${historyAction == 'REJECT' ? 'selected' : ''}>Từ chối</option>
                        <option value="CANCEL" ${historyAction == 'CANCEL' ? 'selected' : ''}>Hủy phiếu</option>
                        <option value="SUBMIT" ${historyAction == 'SUBMIT' ? 'selected' : ''}>Gửi duyệt</option>
                    </select>
                </div>

                <div class="ap-filter-block">
                    <label class="ap-filter-label" for="f-doc-type">Loại chứng từ</label>
                    <select id="f-doc-type" name="documentType" class="ap-filter-select">
                        <option value="">Tất cả loại</option>
                        <option value="PURCHASE_ORDER" ${documentType == 'PURCHASE_ORDER' ? 'selected' : ''}>Phiếu nhập kho</option>
                        <option value="STOCK_TRANSFER" ${documentType == 'STOCK_TRANSFER' ? 'selected' : ''}>Phiếu chuyển kho</option>
                        <option value="INVOICE" ${documentType == 'INVOICE' ? 'selected' : ''}>Hóa đơn</option>
                    </select>
                </div>

                <div class="ap-filter-block">
                    <label class="ap-filter-label" for="f-approver">Người duyệt</label>
                    <select id="f-approver" name="performedBy" class="ap-filter-select">
                        <option value="">Tất cả</option>
                        <c:forEach var="emp" items="${approvers}">
                            <option value="${emp.employeeId}" ${performedBy == emp.employeeId ? 'selected' : ''}>
                                <c:out value="${emp.fullName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="ap-filter-block">
                    <label class="ap-filter-label" for="f-from">Từ ngày</label>
                    <input id="f-from" type="date" name="fromDate" value="<c:out value='${fromDate}'/>" class="ap-filter-date" />
                </div>

                <div class="ap-filter-block">
                    <label class="ap-filter-label" for="f-to">Đến ngày</label>
                    <input id="f-to" type="date" name="toDate" value="<c:out value='${toDate}'/>" class="ap-filter-date" />
                </div>

                <div class="ap-filter-actions">
                    <button type="submit" class="ap-btn ap-btn-primary">Áp dụng</button>
                    <a href="${ctx}/admin/approvals?action=history" class="ap-btn ap-btn-ghost">Xóa lọc</a>
                </div>
            </form>
        </aside>

        <!-- Main grid -->
        <section class="ap-content">
            <div class="ap-content-header">
                <h3 class="ap-content-title">Danh sách quyết định</h3>
                <span style="font-size:12px;color:#6b7280;">Sắp xếp: Mới nhất</span>
            </div>

            <div class="kr-table-wrap">
                <table class="ap-table">
                    <thead>
                        <tr>
                            <th style="width:160px;">Thời gian</th>
                            <th style="width:140px;">Mã phiếu</th>
                            <th style="width:130px;">Loại</th>
                            <th style="width:130px;">Hành động</th>
                            <th>Người duyệt</th>
                            <th>Lý do / Ghi chú</th>
                            <th style="width:160px;">Chuyển trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="7">
                                        <div class="ap-empty">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                                                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                                            </svg>
                                            <div class="ap-empty-title">Không có lịch sử</div>
                                            <div>Chưa có quyết định nào khớp với điều kiện lọc.</div>
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="h" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <div style="font-size:13px; color:#111827; font-weight:500;">
                                                <fmt:formatDate value="${h.createdAt}" pattern="dd/MM/yyyy"/>
                                            </div>
                                            <div style="font-size:12px; color:#6b7280;">
                                                <fmt:formatDate value="${h.createdAt}" pattern="HH:mm"/>
                                            </div>
                                        </td>
                                        <td>
                                            <a class="ap-doc-link"
                                               href="${ctx}/admin/approvals?action=detail&type=${h.documentType}&id=${h.documentId}">
                                                #<c:out value="${h.documentId}"/>
                                            </a>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${h.documentType == 'PURCHASE_ORDER'}">
                                                    <span class="ap-chip ap-chip-purchase">Nhập kho</span>
                                                </c:when>
                                                <c:when test="${h.documentType == 'STOCK_TRANSFER'}">
                                                    <span class="ap-chip ap-chip-transfer">Chuyển kho</span>
                                                </c:when>
                                                <c:when test="${h.documentType == 'INVOICE'}">
                                                    <span class="ap-chip ap-chip-invoice">Hóa đơn</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="ap-chip ap-chip-default"><c:out value="${h.documentType}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${h.action == 'APPROVE'}">
                                                    <span class="ap-action-pill ap-action-approve">Phê duyệt</span>
                                                </c:when>
                                                <c:when test="${h.action == 'REJECT'}">
                                                    <span class="ap-action-pill ap-action-reject">Từ chối</span>
                                                </c:when>
                                                <c:when test="${h.action == 'CANCEL'}">
                                                    <span class="ap-action-pill ap-action-cancel">Hủy phiếu</span>
                                                </c:when>
                                                <c:when test="${h.action == 'SUBMIT'}">
                                                    <span class="ap-action-pill ap-action-submit">Gửi duyệt</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="ap-action-pill ap-action-default"><c:out value="${h.action}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:out value="${h.performedByName}"/></td>
                                        <td>
                                            <div class="ap-reason-cell ${empty h.reason ? 'muted' : ''}"
                                                 title="<c:out value='${h.reason}'/>">
                                                <c:choose>
                                                    <c:when test="${not empty h.reason}"><c:out value="${h.reason}"/></c:when>
                                                    <c:otherwise>Không có ghi chú</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                        <td style="font-size:12px; color:#6b7280;">
                                            <c:if test="${not empty h.fromStatus}">
                                                <c:out value="${h.fromStatus}"/> → <c:out value="${h.toStatus}"/>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="ap-pagination-wrap">
                <c:url var="baseUrl" value="/admin/approvals" scope="request">
                    <c:param name="action" value="history"/>
                    <c:param name="keyword" value="${keyword}"/>
                    <c:param name="historyAction" value="${historyAction}"/>
                    <c:param name="documentType" value="${documentType}"/>
                    <c:param name="performedBy" value="${performedBy}"/>
                    <c:param name="fromDate" value="<c:out value='${fromDate}'/>"/>
                    <c:param name="toDate" value="<c:out value='${toDate}'/>"/>
                </c:url>
                <jsp:include page="../common/pagination.jsp"/>
            </div>
        </section>

    </div>
</div>

<jsp:include page="../common/footer.jsp" />
