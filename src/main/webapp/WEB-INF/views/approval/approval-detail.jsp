<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="approvals" scope="request" />
<c:set var="pageTitle" value="Chi tiết phê duyệt" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    .ap-page { padding: 20px 24px; background: #f5f7fa; min-height: calc(100vh - 100px); }
    .ap-breadcrumb { font-size: 13px; color: #6b7280; margin-bottom: 6px; }
    .ap-breadcrumb a { color: #003399; text-decoration: none; font-weight: 500; }
    .ap-breadcrumb a:hover { text-decoration: underline; }

    .ap-page-header {
        display: flex; align-items: flex-start; justify-content: space-between;
        margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #E5E7EB;
        gap: 16px; flex-wrap: wrap;
    }
    .ap-page-title { font-size: 22px; font-weight: 700; color: #111827; margin: 0; letter-spacing: -0.3px; }
    .ap-page-subtitle { font-size: 13px; color: #6b7280; margin-top: 4px; }

    .ap-btn {
        height: 38px; padding: 0 18px; border-radius: 6px;
        font-size: 14px; font-weight: 600; display: inline-flex; align-items: center; gap: 6px;
        cursor: pointer; text-decoration: none; border: 1px solid transparent;
        transition: background .15s, border-color .15s;
    }
    .ap-btn-primary { background: #003399; color: #fff; border-color: #003399; }
    .ap-btn-primary:hover { background: #002266; border-color: #002266; }
    .ap-btn-danger { background: #dc2626; color: #fff; border-color: #dc2626; }
    .ap-btn-danger:hover { background: #b91c1c; border-color: #b91c1c; }
    .ap-btn-outline { background: #fff; color: #003399; border-color: #003399; }
    .ap-btn-outline:hover { background: #f0f4ff; }
    .ap-btn-ghost { background: #fff; color: #4b5563; border-color: #E5E7EB; }
    .ap-btn-ghost:hover { background: #f9fafb; }

    /* 2/3 + 1/3 layout per UI prompt */
    .ap-detail-layout { display: grid; grid-template-columns: 2fr 1fr; gap: 16px; align-items: flex-start; }
    .ap-section-stack { display: flex; flex-direction: column; gap: 16px; }

    .ap-card {
        background: #fff; border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden;
    }
    .ap-card-header {
        padding: 14px 16px; border-bottom: 1px solid #E5E7EB; background: #fafbfc;
        display: flex; align-items: center; justify-content: space-between;
    }
    .ap-card-title { font-size: 15px; font-weight: 600; color: #111827; margin: 0; }
    .ap-card-body { padding: 16px; }

    /* Status bar */
    .ap-status-bar {
        display: flex; align-items: center; gap: 16px;
        background: #fff; border: 1px solid #E5E7EB; border-radius: 6px;
        padding: 10px 16px; margin-bottom: 16px; font-size: 13px; color: #4b5563; flex-wrap: wrap;
    }
    .ap-status-bar strong { color: #111827; font-weight: 600; }
    .ap-status-bar .ap-divider { width: 1px; height: 18px; background: #E5E7EB; }

    .ap-status-pill {
        display: inline-flex; align-items: center; gap: 6px;
        padding: 4px 12px; border-radius: 12px;
        font-size: 12px; font-weight: 600;
    }
    .ap-status-pill::before { content: ""; width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
    .ap-status-pending { background: #fef3c7; color: #92400e; }
    .ap-status-approved { background: #dcfce7; color: #15803d; }
    .ap-status-rejected { background: #fee2e2; color: #991b1b; }
    .ap-status-draft { background: #f1f3f4; color: #4b5563; }

    /* Info grid */
    .ap-info-grid {
        display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px 24px;
    }
    .ap-info-item { display: flex; flex-direction: column; gap: 4px; }
    .ap-info-label {
        font-size: 11px; color: #6b7280; font-weight: 600;
        text-transform: uppercase; letter-spacing: .5px;
    }
    .ap-info-value { font-size: 14px; color: #111827; font-weight: 500; }
    .ap-info-value.muted { color: #6b7280; font-style: italic; }

    /* Items table */
    .ap-items-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #111827; }
    .ap-items-table thead th {
        background: #f9fafb; border-bottom: 1px solid #E5E7EB;
        font-size: 12px; font-weight: 600; text-transform: uppercase;
        text-align: left; padding: 10px 14px; color: #4b5563; letter-spacing: .3px;
    }
    .ap-items-table tbody td { padding: 10px 14px; border-bottom: 1px solid #f3f4f6; }
    .ap-items-table tbody tr:last-child td { border-bottom: none; }
    .ap-items-table .num { text-align: right; font-variant-numeric: tabular-nums; }
    .ap-items-table tfoot td {
        padding: 12px 14px; font-weight: 700; background: #f9fafb;
        border-top: 2px solid #E5E7EB; color: #003399; font-size: 15px;
    }

    /* Decision form */
    .ap-decision-form { display: flex; flex-direction: column; gap: 14px; }
    .ap-form-label { font-size: 13px; font-weight: 600; color: #111827; margin-bottom: 6px; display: block; }
    .ap-form-required::after { content: " *"; color: #dc2626; }
    .ap-textarea {
        width: 100%; min-height: 100px; padding: 10px 12px; box-sizing: border-box;
        border: 1px solid #E5E7EB; border-radius: 6px;
        font-size: 14px; color: #111827; font-family: inherit; resize: vertical;
        outline: none; transition: border-color .15s, box-shadow .15s;
    }
    .ap-textarea:focus { border-color: #003399; box-shadow: 0 0 0 3px rgba(0,51,153,.08); }
    .ap-form-help { font-size: 12px; color: #6b7280; margin-top: 4px; }

    .ap-decision-actions { display: flex; gap: 8px; }
    .ap-decision-actions .ap-btn { flex: 1; height: 42px; justify-content: center; font-size: 14px; }

    /* Timeline */
    .ap-timeline { list-style: none; padding: 0; margin: 0; position: relative; }
    .ap-timeline::before {
        content: ""; position: absolute; left: 7px; top: 6px; bottom: 6px;
        width: 2px; background: #E5E7EB;
    }
    .ap-timeline-item { position: relative; padding-left: 28px; padding-bottom: 18px; }
    .ap-timeline-item:last-child { padding-bottom: 0; }
    .ap-timeline-dot {
        position: absolute; left: 0; top: 4px;
        width: 16px; height: 16px; border-radius: 50%;
        background: #fff; border: 3px solid #003399;
    }
    .ap-timeline-dot.approve { border-color: #15803d; }
    .ap-timeline-dot.reject { border-color: #dc2626; }
    .ap-timeline-dot.cancel { border-color: #6b7280; }
    .ap-timeline-action { font-size: 13px; font-weight: 600; color: #111827; }
    .ap-timeline-meta { font-size: 12px; color: #6b7280; margin-top: 2px; }
    .ap-timeline-reason {
        margin-top: 6px; padding: 8px 10px; border-left: 3px solid #E5E7EB;
        background: #f9fafb; border-radius: 4px;
        font-size: 13px; color: #4b5563;
    }

    .ap-warning-banner {
        background: #fffbeb; border: 1px solid #fde68a; border-radius: 6px;
        padding: 10px 14px; margin-bottom: 12px;
        display: flex; align-items: flex-start; gap: 10px;
        font-size: 13px; color: #92400e;
    }
    .ap-warning-banner svg { flex-shrink: 0; margin-top: 2px; }

    @media (max-width: 1024px) {
        .ap-detail-layout { grid-template-columns: 1fr; }
        .ap-info-grid { grid-template-columns: 1fr; }
    }
</style>

<div class="ap-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="ap-breadcrumb">
        <a href="${ctx}/admin/approvals?action=pending">Phê duyệt</a>
        <span style="margin:0 6px;">/</span>
        <a href="${ctx}/admin/approvals?action=pending">Chờ duyệt</a>
        <span style="margin:0 6px;">/</span>
        <span>Chi tiết phiếu</span>
    </div>

    <div class="ap-page-header">
        <div>
            <h1 class="ap-page-title">Chi tiết phiếu <c:out value="${document.documentCode}"/></h1>
            <div class="ap-page-subtitle">
                <c:choose>
                    <c:when test="${document.documentType == 'PURCHASE_ORDER'}">Phiếu nhập kho</c:when>
                    <c:when test="${document.documentType == 'STOCK_TRANSFER'}">Phiếu chuyển kho</c:when>
                    <c:when test="${document.documentType == 'INVOICE'}">Hóa đơn</c:when>
                    <c:otherwise><c:out value="<c:out value='${document.documentType}'/>"/></c:otherwise>
                </c:choose>
                &middot; Gửi bởi <c:out value="${document.submitterName}"/>
            </div>
        </div>
        <div style="display:flex; gap:8px;">
            <a href="${ctx}/admin/approvals?action=pending" class="ap-btn ap-btn-ghost">Quay lại danh sách</a>
        </div>
    </div>

    <!-- Status summary bar -->
    <div class="ap-status-bar">
        <span>Trạng thái:
            <c:choose>
                <c:when test="${document.status == 'PENDING_APPROVAL'}">
                    <span class="ap-status-pill ap-status-pending">Chờ duyệt</span>
                </c:when>
                <c:when test="${document.status == 'APPROVED'}">
                    <span class="ap-status-pill ap-status-approved">Đã duyệt</span>
                </c:when>
                <c:when test="${document.status == 'REJECTED'}">
                    <span class="ap-status-pill ap-status-rejected">Đã từ chối</span>
                </c:when>
                <c:when test="${document.status == 'DRAFT'}">
                    <span class="ap-status-pill ap-status-draft">Nháp</span>
                </c:when>
                <c:otherwise>
                    <span class="ap-status-pill ap-status-draft"><c:out value="${document.status}"/></span>
                </c:otherwise>
            </c:choose>
        </span>
        <span class="ap-divider"></span>
        <span>Mã phiếu: <strong><c:out value="${document.documentCode}"/></strong></span>
        <span class="ap-divider"></span>
        <span>Ngày gửi: <strong><fmt:formatDate value="${document.submittedAt}" pattern="dd/MM/yyyy HH:mm"/></strong></span>
        <span class="ap-divider"></span>
        <span>Tổng giá trị: <strong style="color:#003399;"><fmt:formatNumber value="${document.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/></strong></span>
    </div>

    <!-- Two-column layout: 2/3 details + 1/3 decision panel -->
    <div class="ap-detail-layout">

        <!-- Left: details (2/3 width) -->
        <div class="ap-section-stack">

            <!-- Document info -->
            <div class="ap-card">
                <div class="ap-card-header">
                    <h3 class="ap-card-title">Thông tin chứng từ</h3>
                </div>
                <div class="ap-card-body">
                    <div class="ap-info-grid">
                        <div class="ap-info-item">
                            <span class="ap-info-label">Mã phiếu</span>
                            <span class="ap-info-value"><c:out value="${document.documentCode}"/></span>
                        </div>
                        <div class="ap-info-item">
                            <span class="ap-info-label">Loại chứng từ</span>
                            <span class="ap-info-value">
                                <c:choose>
                                    <c:when test="${document.documentType == 'PURCHASE_ORDER'}">Phiếu nhập kho</c:when>
                                    <c:when test="${document.documentType == 'STOCK_TRANSFER'}">Phiếu chuyển kho</c:when>
                                    <c:when test="${document.documentType == 'INVOICE'}">Hóa đơn</c:when>
                                    <c:otherwise><c:out value="<c:out value='${document.documentType}'/>"/></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="ap-info-item">
                            <span class="ap-info-label">Người gửi</span>
                            <span class="ap-info-value"><c:out value="${document.submitterName}"/></span>
                        </div>
                        <div class="ap-info-item">
                            <span class="ap-info-label">Chi nhánh</span>
                            <span class="ap-info-value"><c:out value="${document.branchName}"/></span>
                        </div>
                        <div class="ap-info-item">
                            <span class="ap-info-label">Đối tác / NCC</span>
                            <span class="ap-info-value"><c:out value="${document.partnerName}"/></span>
                        </div>
                        <div class="ap-info-item">
                            <span class="ap-info-label">Ngày tạo</span>
                            <span class="ap-info-value">
                                <c:if test="${not empty document.createdAt}">
                                    <fmt:formatDate value="${document.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:if>
                            </span>
                        </div>
                        <div class="ap-info-item" style="grid-column: 1 / -1;">
                            <span class="ap-info-label">Ghi chú</span>
                            <span class="ap-info-value ${empty document.note ? 'muted' : ''}">
                                <c:choose>
                                    <c:when test="${not empty document.note}"><c:out value="${document.note}"/></c:when>
                                    <c:otherwise>Không có ghi chú</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Items list -->
            <div class="ap-card">
                <div class="ap-card-header">
                    <h3 class="ap-card-title">Danh sách sản phẩm</h3>
                    <span style="font-size:12px;color:#6b7280;">${not empty document.items ? fn:length(document.items) : 0} dòng</span>
                </div>
                <div class="kr-table-wrap">
                    <table class="ap-items-table">
                        <thead>
                            <tr>
                                <th style="width:50px; text-align:center;">#</th>
                                <th style="width:140px;">SKU</th>
                                <th>Tên sản phẩm</th>
                                <th class="num" style="width:100px;">Số lượng</th>
                                <th class="num" style="width:140px;">Đơn giá</th>
                                <th class="num" style="width:160px;">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty document.items}">
                                    <tr>
                                        <td colspan="6" style="padding:32px; text-align:center; color:#6b7280;">
                                            Không có sản phẩm trong phiếu này.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="line" items="${document.items}" varStatus="loop">
                                        <tr>
                                            <td style="text-align:center;">${loop.index + 1}</td>
                                            <td><c:out value="${line.productSku}"/></td>
                                            <td><c:out value="${line.productName}"/></td>
                                            <td class="num"><fmt:formatNumber value="${line.quantity}" type="number" groupingUsed="true"/></td>
                                            <td class="num"><fmt:formatNumber value="${line.unitCost}" type="number" groupingUsed="true" maxFractionDigits="0"/></td>
                                            <td class="num"><fmt:formatNumber value="${line.subtotal}" type="number" groupingUsed="true" maxFractionDigits="0"/></td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                        <c:if test="${not empty document.items}">
                            <tfoot>
                                <tr>
                                    <td colspan="5" style="text-align:right;">Tổng cộng</td>
                                    <td class="num">
                                        <fmt:formatNumber value="${document.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                                    </td>
                                </tr>
                            </tfoot>
                        </c:if>
                    </table>
                </div>
            </div>

            <!-- Approval timeline -->
            <div class="ap-card">
                <div class="ap-card-header">
                    <h3 class="ap-card-title">Lịch sử thao tác</h3>
                </div>
                <div class="ap-card-body">
                    <c:choose>
                        <c:when test="${empty histories}">
                            <div style="color:#6b7280; font-size:13px;">Chưa có thao tác nào trên phiếu này.</div>
                        </c:when>
                        <c:otherwise>
                            <ul class="ap-timeline">
                                <c:forEach var="h" items="${histories}">
                                    <li class="ap-timeline-item">
                                        <span class="ap-timeline-dot
                                            <c:choose>
                                                <c:when test='${h.action == "APPROVE"}'>approve</c:when>
                                                <c:when test='${h.action == "REJECT"}'>reject</c:when>
                                                <c:when test='${h.action == "CANCEL"}'>cancel</c:when>
                                            </c:choose>
                                        "></span>
                                        <div class="ap-timeline-action">
                                            <c:choose>
                                                <c:when test="${h.action == 'CREATE'}">Tạo phiếu</c:when>
                                                <c:when test="${h.action == 'SUBMIT'}">Gửi duyệt</c:when>
                                                <c:when test="${h.action == 'APPROVE'}">Phê duyệt</c:when>
                                                <c:when test="${h.action == 'REJECT'}">Từ chối</c:when>
                                                <c:when test="${h.action == 'CANCEL'}">Hủy phiếu</c:when>
                                                <c:when test="${h.action == 'COMPLETE'}">Hoàn tất</c:when>
                                                <c:when test="${h.action == 'RECEIVE'}">Nhập kho</c:when>
                                                <c:otherwise><c:out value="${h.action}"/></c:otherwise>
                                            </c:choose>
                                            &middot; <c:out value="${h.performedByName}"/>
                                        </div>
                                        <div class="ap-timeline-meta">
                                            <fmt:formatDate value="${h.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            <c:if test="${not empty h.fromStatus}">
                                                &middot; <c:out value="${h.fromStatus}"/> → <c:out value="${h.toStatus}"/>
                                            </c:if>
                                        </div>
                                        <c:if test="${not empty h.reason}">
                                            <div class="ap-timeline-reason"><c:out value="${h.reason}"/></div>
                                        </c:if>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Right: decision panel (1/3 width) -->
        <aside class="ap-card" style="position: sticky; top: 16px;">
            <div class="ap-card-header">
                <h3 class="ap-card-title">Quyết định phê duyệt</h3>
            </div>
            <div class="ap-card-body">

                <c:choose>
                    <c:when test="${document.status != 'PENDING_APPROVAL'}">
                        <div class="ap-warning-banner">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                            </svg>
                            <div>Phiếu đã được xử lý. Không thể duyệt thêm lần nữa.</div>
                        </div>
                    </c:when>
                    <c:when test="${not canApprove}">
                        <div class="ap-warning-banner">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                            </svg>
                            <div>Bạn không có quyền duyệt phiếu này hoặc bạn là người gửi phiếu.</div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${ctx}/admin/approvals" class="ap-decision-form" id="ap-decision-form">
                            <input type="hidden" name="documentType" value="<c:out value='${document.documentType}'/>" />
                            <input type="hidden" name="documentId" value="${document.documentId}" />
                            <input type="hidden" name="action" id="ap-action-input" value="approve" />

                            <div>
                                <label class="ap-form-label" for="ap-reason">Lý do / Ghi chú</label>
                                <textarea id="ap-reason" name="reason" class="ap-textarea"
                                          placeholder="Nhập lý do (bắt buộc khi từ chối)..."></textarea>
                                <div class="ap-form-help">Lý do là bắt buộc khi từ chối phiếu. Tối thiểu 5 ký tự.</div>
                            </div>

                            <div class="ap-decision-actions">
                                <button type="submit" class="ap-btn ap-btn-primary"
                                        onclick="return apSubmit('approve');">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="20 6 9 17 4 12"/>
                                    </svg>
                                    Phê duyệt
                                </button>
                                <button type="submit" class="ap-btn ap-btn-danger"
                                        onclick="return apSubmit('reject');">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                    </svg>
                                    Từ chối
                                </button>
                            </div>
                        </form>
                        <script>
                            function apSubmit(action) {
                                var input = document.getElementById('ap-action-input');
                                var reason = document.getElementById('ap-reason').value.trim();
                                if (action === 'reject' && reason.length < 5) {
                                    alert('Vui lòng nhập lý do từ chối (tối thiểu 5 ký tự).');
                                    return false;
                                }
                                if (action === 'approve' && !confirm('Xác nhận phê duyệt phiếu này?')) {
                                    return false;
                                }
                                if (action === 'reject' && !confirm('Xác nhận từ chối phiếu này?')) {
                                    return false;
                                }
                                input.value = action;
                                return true;
                            }
                        </script>
                    </c:otherwise>
                </c:choose>

                <hr style="border:none; border-top:1px solid #E5E7EB; margin:16px 0;" />

                <div style="font-size:13px; color:#4b5563;">
                    <div style="display:flex; justify-content:space-between; padding:6px 0;">
                        <span>Người gửi:</span>
                        <strong style="color:#111827;"><c:out value="${document.submitterName}"/></strong>
                    </div>
                    <div style="display:flex; justify-content:space-between; padding:6px 0;">
                        <span>Người duyệt:</span>
                        <strong style="color:#111827;"><c:out value="${sessionScope.employeeName != null ? sessionScope.employeeName : sessionScope.user.fullName}"/></strong>
                    </div>
                    <div style="display:flex; justify-content:space-between; padding:6px 0;">
                        <span>Tổng giá trị:</span>
                        <strong style="color:#003399;">
                            <fmt:formatNumber value="${document.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                        </strong>
                    </div>
                </div>
            </div>
        </aside>

    </div>
</div>

<jsp:include page="../common/footer.jsp" />
