<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="approvals" scope="request" />
<c:set var="pageTitle" value="Phiếu chờ duyệt" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    /* ===== Approval workflow styling (deep navy #003399 primary, light borders #E5E7EB) ===== */
    .ap-page { padding: 20px 24px; background: #f5f7fa; min-height: calc(100vh - 100px); }
    .ap-breadcrumb { font-size: 13px; color: #6b7280; margin-bottom: 6px; }
    .ap-breadcrumb a { color: #003399; text-decoration: none; font-weight: 500; }
    .ap-breadcrumb a:hover { text-decoration: underline; }

    .ap-page-header {
        margin-bottom: 16px; padding-bottom: 14px; border-bottom: 1px solid #E5E7EB;
    }
    .ap-page-title { font-size: 22px; font-weight: 700; color: #111827; margin: 0; letter-spacing: -0.3px; }
    .ap-page-subtitle { font-size: 13px; color: #6b7280; margin-top: 4px; }

    /* Action toolbar — sits at the UPPER-LEFT of the content area, separate from search */
    .ap-toolbar {
        display: flex; align-items: center; gap: 8px;
        margin-bottom: 12px; flex-wrap: wrap; justify-content: flex-start;
    }

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

    .ap-status-bar {
        display: flex; align-items: center; gap: 16px;
        background: #fff; border: 1px solid #E5E7EB; border-radius: 6px;
        padding: 10px 16px; margin-bottom: 16px; font-size: 13px; color: #4b5563;
    }
    .ap-status-bar strong { color: #003399; font-weight: 600; }
    .ap-status-bar .ap-divider { width: 1px; height: 18px; background: #E5E7EB; }
    .ap-btn {
        height: 38px; padding: 0 18px; border-radius: 6px;
        font-size: 14px; font-weight: 600; display: inline-flex; align-items: center; gap: 6px;
        cursor: pointer; text-decoration: none; border: 1px solid transparent;
        transition: background .15s, border-color .15s, color .15s;
    }
    .ap-btn-primary { background: #003399; color: #fff; border-color: #003399; }
    .ap-btn-primary:hover { background: #002266; border-color: #002266; }
    .ap-btn-secondary { background: #6b7280; color: #fff; border-color: #6b7280; }
    .ap-btn-secondary:hover { background: #4b5563; border-color: #4b5563; }
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
    .ap-count-badge {
        display: inline-flex; align-items: center; justify-content: center;
        background: #003399; color: #fff; font-size: 12px; font-weight: 700;
        padding: 2px 10px; border-radius: 12px; margin-left: 8px;
    }

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

    .ap-chip {
        display: inline-block; padding: 3px 10px; border-radius: 4px;
        font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: .3px;
    }
    .ap-chip-purchase { background: #e0e7ff; color: #003399; }
    .ap-chip-transfer { background: #fef3c7; color: #92400e; }
    .ap-chip-invoice { background: #dcfce7; color: #15803d; }
    .ap-chip-default { background: #f1f3f4; color: #4b5563; }

    .ap-status-pill {
        display: inline-flex; align-items: center; gap: 6px;
        padding: 3px 10px; border-radius: 12px;
        font-size: 12px; font-weight: 600;
    }
    .ap-status-pill::before { content: ""; width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
    .ap-status-pending { background: #fef3c7; color: #92400e; }

    .ap-empty { padding: 60px 20px; text-align: center; color: #6b7280; }
    .ap-empty svg { width: 48px; height: 48px; color: #d1d5db; margin-bottom: 12px; }
    .ap-empty-title { font-size: 15px; font-weight: 600; color: #4b5563; margin-bottom: 4px; }

    .ap-pagination-wrap { padding: 0 16px; border-top: 1px solid #E5E7EB; }

    .ap-row-actions { display: flex; gap: 6px; }
    .ap-row-btn {
        height: 30px; padding: 0 12px; border-radius: 4px;
        font-size: 12px; font-weight: 600; border: 1px solid transparent;
        cursor: pointer; text-decoration: none; display: inline-flex; align-items: center;
        transition: background .15s;
    }
    .ap-row-btn-view { background: #fff; color: #003399; border-color: #003399; }
    .ap-row-btn-view:hover { background: #f0f4ff; }

    @media (max-width: 1024px) {
        .ap-layout { grid-template-columns: 1fr; }
        .ap-sidebar { position: static; }
    }
</style>

<div class="ap-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="ap-breadcrumb">
        <a href="${ctx}/admin/dashboard">Tổng quan</a>
        <span style="margin:0 6px;">/</span>
        <span>Phê duyệt</span>
        <span style="margin:0 6px;">/</span>
        <span>Chờ duyệt</span>
    </div>

    <div class="ap-page-header">
        <h1 class="ap-page-title">Hộp thư phiếu chờ duyệt</h1>
        <div class="ap-page-subtitle">Danh sách các phiếu đang chờ phê duyệt từ các bộ phận</div>
    </div>

    <div class="ap-toolbar">
        <button type="submit" form="apSearchForm" class="ap-btn ap-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            Tìm kiếm
        </button>
        <a href="${ctx}/admin/approvals?action=pending" class="ap-btn ap-btn-secondary">Đặt lại</a>
        <a href="${ctx}/admin/approvals?action=history" class="ap-btn ap-btn-outline">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
            </svg>
            Lịch sử phê duyệt
        </a>
    </div>

    <form id="apSearchForm" method="get" action="${ctx}/admin/approvals" class="ap-search-row">
        <input type="hidden" name="action" value="pending" />
        <input type="text" name="keyword" value="<c:out value='${keyword}'/>"
               class="ap-search-input"
               placeholder="Tìm theo mã phiếu, tên người gửi, nhà cung cấp..." />
    </form>

    <div class="ap-status-bar">
        <span>Tổng phiếu chờ: <strong>${pageResult.totalItems != null ? pageResult.totalItems : 0}</strong></span>
        <span class="ap-divider"></span>
        <span>Người duyệt: <strong><c:out value="${sessionScope.employeeName != null ? sessionScope.employeeName : sessionScope.user.fullName}"/></strong></span>
        <span class="ap-divider"></span>
        <span>Cập nhật: <strong><fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm"/></strong></span>
    </div>

    <div class="ap-layout">

        <aside class="ap-sidebar">
            <h3 class="ap-sidebar-title">Bộ lọc</h3>
            <form method="get" action="${ctx}/admin/approvals">
                <input type="hidden" name="action" value="pending" />
                <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>" />

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
                    <label class="ap-filter-label" for="f-submitter">Người gửi</label>
                    <select id="f-submitter" name="submittedBy" class="ap-filter-select">
                        <option value="">Tất cả nhân viên</option>
                        <c:forEach var="emp" items="${submitters}">
                            <option value="${emp.employeeId}" ${submittedBy == emp.employeeId ? 'selected' : ''}>
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
                    <a href="${ctx}/admin/approvals?action=pending" class="ap-btn ap-btn-ghost">Xóa lọc</a>
                </div>
            </form>
        </aside>

        <section class="ap-content">
            <div class="ap-content-header">
                <h3 class="ap-content-title">
                    Phiếu đang chờ duyệt
                    <span class="ap-count-badge">${pageResult.totalItems != null ? pageResult.totalItems : 0}</span>
                </h3>
                <span style="font-size:12px;color:#6b7280;">Sắp xếp: Mới nhất</span>
            </div>

            <div class="kr-table-wrap">
                <table class="ap-table">
                    <thead>
                        <tr>
                            <th style="width:140px;">Mã phiếu</th>
                            <th style="width:140px;">Loại chứng từ</th>
                            <th>Người gửi</th>
                            <th>Mô tả</th>
                            <th style="width:160px;">Ngày gửi</th>
                            <th style="width:130px;">Trạng thái</th>
                            <th style="width:110px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="7">
                                        <div class="ap-empty">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                                                <polyline points="22 4 12 14.01 9 11.01"/>
                                            </svg>
                                            <div class="ap-empty-title">Không có phiếu chờ duyệt</div>
                                            <div>Tất cả phiếu đã được xử lý hoặc chưa có phiếu nào được gửi lên.</div>
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <a class="ap-doc-link"
                                               href="${ctx}/admin/approvals?action=detail&type=${item.documentType}&id=${item.documentId}">
                                                <c:out value="${item.documentCode}"/>
                                            </a>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.documentType == 'PURCHASE_ORDER'}">
                                                    <span class="ap-chip ap-chip-purchase">Nhập kho</span>
                                                </c:when>
                                                <c:when test="${item.documentType == 'STOCK_TRANSFER'}">
                                                    <span class="ap-chip ap-chip-transfer">Chuyển kho</span>
                                                </c:when>
                                                <c:when test="${item.documentType == 'INVOICE'}">
                                                    <span class="ap-chip ap-chip-invoice">Hóa đơn</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="ap-chip ap-chip-default"><c:out value="${item.documentType}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:out value="${item.submitterName}"/></td>
                                        <td><c:out value="${item.description}"/></td>
                                        <td>
                                            <c:if test="${not empty item.submittedAt}">
                                                <fmt:formatDate value="${item.submittedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </c:if>
                                        </td>
                                        <td>
                                            <span class="ap-status-pill ap-status-pending">Chờ duyệt</span>
                                        </td>
                                        <td>
                                            <div class="ap-row-actions">
                                                <a class="ap-row-btn ap-row-btn-view"
                                                   href="${ctx}/admin/approvals?action=detail&type=${item.documentType}&id=${item.documentId}">
                                                    Xem
                                                </a>
                                            </div>
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
                    <c:param name="action" value="pending"/>
                    <c:param name="keyword" value="${keyword}"/>
                    <c:param name="documentType" value="${documentType}"/>
                    <c:param name="submittedBy" value="${submittedBy}"/>
                    <c:param name="fromDate" value="<c:out value='${fromDate}'/>"/>
                    <c:param name="toDate" value="<c:out value='${toDate}'/>"/>
                </c:url>
                <jsp:include page="../common/pagination.jsp"/>
            </div>
        </section>

    </div>
</div>

<jsp:include page="../common/footer.jsp" />
