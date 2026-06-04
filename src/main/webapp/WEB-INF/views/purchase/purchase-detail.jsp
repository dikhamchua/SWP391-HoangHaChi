<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="purchases" scope="request" />

<%-- Resolve current role + creator flags once (session stores roleName + employee). --%>
<c:set var="userRole" value="${sessionScope.roleName != null ? sessionScope.roleName : sessionScope.user.role}" />
<c:set var="isCreator" value="${not empty order.createdBy and order.createdBy == sessionScope.employee.employeeId}" />
<c:set var="isApprover" value="${userRole == 'Owner' or userRole == 'StoreManager'}" />
<c:set var="poId" value="${order.purchaseOrderId}" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    /* Full DocumentStatus pill for purchase detail */
    .pd-status-pill {
        display: inline-flex; align-items: center; gap: 6px;
        padding: 4px 12px; border-radius: 12px;
        font-size: 12px; font-weight: 600; margin-left: 8px;
    }
    .pd-status-pill::before { content: ""; width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
    .pd-status-draft        { background: #f1f3f4; color: #4b5563; }
    .pd-status-pending      { background: #fef3c7; color: #92400e; }
    .pd-status-approved     { background: #dcfce7; color: #15803d; }
    .pd-status-receiving    { background: #dbeafe; color: #1d4ed8; }
    .pd-status-completed    { background: #d1fae5; color: #065f46; }
    .pd-status-rejected     { background: #fee2e2; color: #991b1b; }
    .pd-status-cancelled    { background: #e2e8f0; color: #475569; }

    /* Threshold hint badge */
    .pd-threshold-badge {
        display: inline-flex; align-items: center; gap: 6px;
        padding: 4px 10px; border-radius: 12px; margin-left: 8px;
        font-size: 12px; font-weight: 600; background: #fff7ed; color: #c2410c;
        border: 1px solid #fed7aa;
    }

    .pd-submit-btn { background: #003399 !important; color: #fff !important; border-color: #003399 !important; }
    .pd-submit-btn:hover { background: #002266 !important; border-color: #002266 !important; }
    .pd-approve-btn { background: #16a34a !important; color: #fff !important; border-color: #16a34a !important; }
    .pd-approve-btn:hover { background: #15803d !important; border-color: #15803d !important; }
    .pd-reject-btn { background: #dc2626 !important; color: #fff !important; border-color: #dc2626 !important; }
    .pd-reject-btn:hover { background: #b91c1c !important; border-color: #b91c1c !important; }
    .pd-receive-btn { background: #2563eb !important; color: #fff !important; border-color: #2563eb !important; }
    .pd-receive-btn:hover { background: #1d4ed8 !important; border-color: #1d4ed8 !important; }
    .pd-cancel-btn { background: #fff !important; color: #b91c1c !important; border-color: #fca5a5 !important; }
    .pd-cancel-btn:hover { background: #fef2f2 !important; }

    /* Approval timeline */
    .pd-timeline { list-style: none; padding: 0; margin: 0; position: relative; }
    .pd-timeline::before {
        content: ""; position: absolute; left: 7px; top: 6px; bottom: 6px;
        width: 2px; background: #e5e7eb;
    }
    .pd-timeline-item { position: relative; padding-left: 28px; padding-bottom: 18px; }
    .pd-timeline-item:last-child { padding-bottom: 0; }
    .pd-timeline-dot {
        position: absolute; left: 0; top: 4px; width: 16px; height: 16px;
        border-radius: 50%; background: #fff; border: 3px solid #003399;
    }
    .pd-timeline-dot.approve { border-color: #15803d; }
    .pd-timeline-dot.reject  { border-color: #dc2626; }
    .pd-timeline-dot.cancel  { border-color: #6b7280; }
    .pd-timeline-action { font-size: 13px; font-weight: 600; color: #111827; }
    .pd-timeline-meta { font-size: 12px; color: #6b7280; margin-top: 2px; }
    .pd-timeline-reason {
        margin-top: 6px; padding: 8px 10px; border-left: 3px solid #e5e7eb;
        background: #f9fafb; border-radius: 4px; font-size: 13px; color: #4b5563;
    }

    /* Reason modal */
    .pd-modal-overlay {
        display: none; position: fixed; inset: 0; z-index: 1000;
        background: rgba(17,24,39,.45); align-items: center; justify-content: center;
    }
    .pd-modal-overlay.open { display: flex; }
    .pd-modal {
        background: #fff; border-radius: 10px; width: 100%; max-width: 460px;
        box-shadow: 0 20px 40px rgba(0,0,0,.2); overflow: hidden;
    }
    .pd-modal-header { padding: 16px 20px; border-bottom: 1px solid #e5e7eb; font-size: 16px; font-weight: 600; color: #111827; }
    .pd-modal-body { padding: 20px; }
    .pd-modal-label { font-size: 13px; font-weight: 600; color: #111827; display: block; margin-bottom: 6px; }
    .pd-modal-label::after { content: " *"; color: #dc2626; }
    .pd-modal-textarea {
        width: 100%; min-height: 96px; padding: 10px 12px; box-sizing: border-box;
        border: 1px solid #e5e7eb; border-radius: 6px; font-size: 14px; color: #111827;
        font-family: inherit; resize: vertical; outline: none;
    }
    .pd-modal-textarea:focus { border-color: #003399; box-shadow: 0 0 0 3px rgba(0,51,153,.08); }
    .pd-modal-help { font-size: 12px; color: #6b7280; margin-top: 4px; }
    .pd-modal-footer { padding: 14px 20px; border-top: 1px solid #e5e7eb; display: flex; gap: 8px; justify-content: flex-end; }
</style>

<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <div>
            <div style="font-size:13px;color:#6b7280;margin-bottom:4px;">
                <a href="${ctx}/admin/purchases" style="color:#003399;text-decoration:none;font-weight:500;">Phiếu nhập hàng</a>
                <span style="margin:0 6px;">/</span>
                <span>Chi tiết phiếu</span>
            </div>
            <h1 class="kr-page-title" style="display:inline-flex; align-items:center; flex-wrap:wrap;">
                Phiếu nhập <c:out value="${order.orderCode}" />
                <%-- 3.1 Full DocumentStatus pill from order.approvalStatus --%>
                <c:choose>
                    <c:when test="${order.approvalStatus == 'DRAFT'}">
                        <span class="pd-status-pill pd-status-draft">Nháp</span>
                    </c:when>
                    <c:when test="${order.approvalStatus == 'PENDING_APPROVAL'}">
                        <span class="pd-status-pill pd-status-pending">Chờ duyệt</span>
                    </c:when>
                    <c:when test="${order.approvalStatus == 'APPROVED'}">
                        <span class="pd-status-pill pd-status-approved">Đã duyệt</span>
                    </c:when>
                    <c:when test="${order.approvalStatus == 'RECEIVING'}">
                        <span class="pd-status-pill pd-status-receiving">Đang nhận hàng</span>
                    </c:when>
                    <c:when test="${order.approvalStatus == 'COMPLETED'}">
                        <span class="pd-status-pill pd-status-completed">Hoàn tất</span>
                    </c:when>
                    <c:when test="${order.approvalStatus == 'REJECTED'}">
                        <span class="pd-status-pill pd-status-rejected">Đã từ chối</span>
                    </c:when>
                    <c:when test="${order.approvalStatus == 'CANCELLED'}">
                        <span class="pd-status-pill pd-status-cancelled">Đã hủy</span>
                    </c:when>
                </c:choose>
                <%-- 3.4 Threshold hint badge --%>
                <c:if test="${not empty order.totalAmount and not empty ownerThreshold and order.totalAmount >= ownerThreshold}">
                    <span class="pd-threshold-badge" title="Tổng giá trị vượt ngưỡng, cần chủ cửa hàng phê duyệt">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                        </svg>
                        Cần Owner duyệt
                    </span>
                </c:if>
            </h1>
        </div>

        <%-- 3.3/3.5 Conditional action buttons by approvalStatus + role --%>
        <div style="display:flex;gap:8px;flex-wrap:wrap;">
            <c:choose>
                <%-- DRAFT (creator): Sửa / Gửi duyệt / Hủy --%>
                <c:when test="${order.approvalStatus == 'DRAFT'}">
                    <c:if test="${isCreator}">
                        <a href="${ctx}/admin/purchases?action=edit&id=${poId}" class="kr-btn">Sửa</a>
                        <form method="post" action="${ctx}/admin/purchases" style="display:inline;"
                              onsubmit="return confirm('Gửi phiếu này lên cấp quản lý để phê duyệt?');">
                            <input type="hidden" name="action" value="submit" />
                            <input type="hidden" name="id" value="<c:out value='${poId}'/>" />
                            <button type="submit" class="kr-btn pd-submit-btn">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
                                </svg>
                                Gửi duyệt
                            </button>
                        </form>
                        <button type="button" class="kr-btn pd-cancel-btn" onclick="pdOpenModal('cancel');">Hủy</button>
                    </c:if>
                </c:when>

                <%-- PENDING_APPROVAL (approver): Duyệt / Từ chối / Hủy --%>
                <c:when test="${order.approvalStatus == 'PENDING_APPROVAL'}">
                    <c:if test="${isApprover}">
                        <form method="post" action="${ctx}/admin/purchases" style="display:inline;"
                              onsubmit="return confirm('Xác nhận phê duyệt phiếu nhập này?');">
                            <input type="hidden" name="action" value="approve" />
                            <input type="hidden" name="id" value="<c:out value='${poId}'/>" />
                            <button type="submit" class="kr-btn pd-approve-btn">Duyệt</button>
                        </form>
                        <button type="button" class="kr-btn pd-reject-btn" onclick="pdOpenModal('reject');">Từ chối</button>
                        <button type="button" class="kr-btn pd-cancel-btn" onclick="pdOpenModal('cancel');">Hủy</button>
                    </c:if>
                </c:when>

                <%-- APPROVED: Nhận hàng / Hủy --%>
                <c:when test="${order.approvalStatus == 'APPROVED'}">
                    <a href="${ctx}/admin/purchases?action=receive&id=${poId}" class="kr-btn pd-receive-btn">Nhận hàng</a>
                    <button type="button" class="kr-btn pd-cancel-btn" onclick="pdOpenModal('cancel');">Hủy</button>
                </c:when>

                <%-- RECEIVING: Nhận tiếp / Hủy --%>
                <c:when test="${order.approvalStatus == 'RECEIVING'}">
                    <a href="${ctx}/admin/purchases?action=receive&id=${poId}" class="kr-btn pd-receive-btn">Nhận tiếp</a>
                    <button type="button" class="kr-btn pd-cancel-btn" onclick="pdOpenModal('cancel');">Hủy</button>
                </c:when>
            </c:choose>

            <%-- In phiếu always available (read-only states only get this + back) --%>
            <a href="${ctx}/admin/purchases?action=print&id=${poId}" class="kr-btn" target="_blank" rel="noopener">In phiếu</a>
            <a href="${ctx}/admin/purchases" class="kr-btn">Quay lại</a>
        </div>
    </div>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <div class="kr-filter-section">
                <div class="kr-filter-label" style="font-weight:700;color:#111827;">QUAN LY NHAP HANG</div>
            </div>
        </aside>

        <section class="kr-content" style="padding:24px;">

            <!-- Tab navigation -->
            <div style="display:flex;gap:0;border-bottom:2px solid #e5e7eb;margin-bottom:20px;">
                <button type="button" class="pd-tab active" onclick="pdSwitchTab('info')" id="pd-tab-info"
                        style="padding:10px 20px;font-size:14px;font-weight:600;border:none;background:none;cursor:pointer;color:#003399;border-bottom:2px solid #003399;margin-bottom:-2px;">
                    Thong tin chung
                </button>
                <button type="button" class="pd-tab" onclick="pdSwitchTab('history')" id="pd-tab-history"
                        style="padding:10px 20px;font-size:14px;font-weight:600;border:none;background:none;cursor:pointer;color:#6b7280;border-bottom:2px solid transparent;margin-bottom:-2px;">
                    Lich su hoat dong
                    <c:if test="${not empty activities}">
                        <span style="background:#e0e7ff;color:#003399;font-size:11px;padding:2px 6px;border-radius:10px;margin-left:4px;">${activities.size()}</span>
                    </c:if>
                </button>
            </div>

            <!-- Tab 1: Thong tin chung -->
            <div id="pd-panel-info">

            <!-- Order info card -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Thong tin phieu</h3>
                <table class="kr-table" style="font-size:14px;width:100%;">
                    <tbody>
                        <tr>
                            <td style="font-weight:600;width:180px;">Ma phieu</td>
                            <td><c:out value="${order.orderCode}" /></td>
                            <td style="font-weight:600;width:160px;">Trang thai</td>
                            <td>
                                <%-- Legacy order.status cell kept untouched. --%>
                                <c:choose>
                                    <c:when test="${order.status == 'draft'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                     font-size:12px;font-weight:600;background:#e5e7eb;color:#374151;">
                                            Nhap
                                        </span>
                                    </c:when>
                                    <c:when test="${order.status == 'confirmed'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                     font-size:12px;font-weight:600;background:#dbeafe;color:#1d4ed8;">
                                            Da xac nhan
                                        </span>
                                    </c:when>
                                    <c:when test="${order.status == 'received'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                     font-size:12px;font-weight:600;background:#d1fae5;color:#059669;">
                                            Da nhap kho
                                        </span>
                                    </c:when>
                                    <c:when test="${order.status == 'cancelled'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                     font-size:12px;font-weight:600;background:#fee2e2;color:#dc2626;">
                                            Da huy
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span><c:out value="${order.status}" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td style="font-weight:600;">Nha cung cap</td>
                            <td><c:out value="${order.supplierName}" /></td>
                            <td style="font-weight:600;">Chi nhanh</td>
                            <td><c:out value="${order.branchName}" /></td>
                        </tr>
                        <tr>
                            <td style="font-weight:600;">Nhan vien</td>
                            <td><c:out value="${order.employeeName}" /></td>
                            <td style="font-weight:600;">Ngay tao</td>
                            <td>
                                <c:if test="${not empty order.createdAt}">
                                    <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td style="font-weight:600;">Ghi chu</td>
                            <td colspan="3"><c:out value="${order.note}" /></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Details table -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Danh sach san pham</h3>
                <div class="kr-table-wrap">
                    <table class="kr-table" style="width:100%;">
                        <thead>
                            <tr>
                                <th style="width:50px;text-align:center;">#</th>
                                <th style="width:140px;">SKU</th>
                                <th>Ten san pham</th>
                                <th class="kr-col-num" style="width:100px;">So luong</th>
                                <th class="kr-col-num" style="width:140px;">Don gia</th>
                                <th class="kr-col-num" style="width:160px;">Thanh tien</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty orderDetails}">
                                    <tr><td colspan="6" class="kr-empty">Khong co san pham nao.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="d" items="${orderDetails}" varStatus="loop">
                                        <tr>
                                            <td style="text-align:center;">${loop.index + 1}</td>
                                            <td><c:out value="${d.productSku}" /></td>
                                            <td><c:out value="${d.productName}" /></td>
                                            <td class="num">
                                                <fmt:formatNumber value="${d.quantity}" type="number"
                                                                  groupingUsed="true" />
                                            </td>
                                            <td class="num">
                                                <fmt:formatNumber value="${d.unitCost}" type="number"
                                                                  groupingUsed="true" maxFractionDigits="0" />
                                            </td>
                                            <td class="num">
                                                <fmt:formatNumber value="${d.subtotal}" type="number"
                                                                  groupingUsed="true" maxFractionDigits="0" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Summary -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Tong ket</h3>
                <table style="width:100%;max-width:420px;margin-left:auto;font-size:14px;">
                    <tbody>
                        <tr style="border-top:1px solid #e5e7eb;">
                            <td style="padding:10px 0;font-weight:700;font-size:16px;">Tong cong</td>
                            <td style="padding:10px 0;text-align:right;font-weight:700;font-size:16px;color:#111827;">
                                <fmt:formatNumber value="${order.totalAmount}" type="number"
                                                  groupingUsed="true" maxFractionDigits="0" />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <%-- 3.2/3.3 Approval timeline (Tab 2: Lich su hoat dong) --%>
            </div><!-- end pd-panel-info -->

            <div id="pd-panel-history" style="display:none;">
            <c:choose>
                <c:when test="${empty activities}">
                    <div style="padding:40px 20px;text-align:center;color:#6b7280;">
                        <div style="font-size:15px;font-weight:600;color:#4b5563;margin-bottom:4px;">Chua co lich su hoat dong</div>
                        <p>Phieu nay chua co thao tac nao duoc ghi nhan.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;">
                        <c:forEach var="activity" items="${activities}">
                            <div style="padding:12px 0;border-bottom:1px solid #f3f4f6;display:flex;align-items:flex-start;gap:12px;">
                                <span style="display:inline-block;padding:3px 10px;border-radius:12px;font-size:11px;font-weight:600;text-transform:uppercase;
                                    <c:choose>
                                        <c:when test="${activity.type == 'add'}">background:#dcfce7;color:#15803d;</c:when>
                                        <c:when test="${activity.type == 'update'}">background:#e0e7ff;color:#003399;</c:when>
                                        <c:when test="${activity.type == 'delete'}">background:#fee2e2;color:#991b1b;</c:when>
                                        <c:otherwise>background:#f1f3f4;color:#4b5563;</c:otherwise>
                                    </c:choose>
                                "><c:out value="${activity.type}" /></span>
                                <div style="flex:1;">
                                    <div style="font-size:14px;color:#111827;"><c:out value="${activity.description}" /></div>
                                    <div style="font-size:12px;color:#6b7280;margin-top:2px;">
                                        Người thực hiện: <c:out value="${activity.createdByName}" default="Hệ thống" />
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
            </div><!-- end pd-panel-history -->

        </section>
    </div>
</div>

<%-- 3.6 Reject modal --%>
<div class="pd-modal-overlay" id="pd-modal-reject">
    <div class="pd-modal" role="dialog" aria-modal="true" aria-labelledby="pd-reject-title">
        <div class="pd-modal-header" id="pd-reject-title">Tu choi phieu nhap</div>
        <form method="post" action="${ctx}/admin/purchases" onsubmit="return pdValidateReason(this);">
            <input type="hidden" name="action" value="reject" />
            <input type="hidden" name="id" value="<c:out value='${poId}'/>" />
            <div class="pd-modal-body">
                <label class="pd-modal-label" for="pd-reject-reason">Ly do tu choi</label>
                <textarea id="pd-reject-reason" name="reason" class="pd-modal-textarea"
                          required minlength="5" placeholder="Nhap ly do tu choi (toi thieu 5 ky tu)..."></textarea>
                <div class="pd-modal-help">Ly do la bat buoc, toi thieu 5 ky tu.</div>
            </div>
            <div class="pd-modal-footer">
                <button type="button" class="kr-btn" onclick="pdCloseModal('reject');">Dong</button>
                <button type="submit" class="kr-btn pd-reject-btn">Xac nhan tu choi</button>
            </div>
        </form>
    </div>
</div>

<%-- 3.6 Cancel modal --%>
<div class="pd-modal-overlay" id="pd-modal-cancel">
    <div class="pd-modal" role="dialog" aria-modal="true" aria-labelledby="pd-cancel-title">
        <div class="pd-modal-header" id="pd-cancel-title">Huy phieu nhap</div>
        <form method="post" action="${ctx}/admin/purchases" onsubmit="return pdValidateReason(this);">
            <input type="hidden" name="action" value="cancel" />
            <input type="hidden" name="id" value="<c:out value='${poId}'/>" />
            <div class="pd-modal-body">
                <label class="pd-modal-label" for="pd-cancel-reason">Ly do huy</label>
                <textarea id="pd-cancel-reason" name="reason" class="pd-modal-textarea"
                          required minlength="5" placeholder="Nhap ly do huy phieu (toi thieu 5 ky tu)..."></textarea>
                <div class="pd-modal-help">Ly do la bat buoc, toi thieu 5 ky tu.</div>
            </div>
            <div class="pd-modal-footer">
                <button type="button" class="kr-btn" onclick="pdCloseModal('cancel');">Dong</button>
                <button type="submit" class="kr-btn pd-reject-btn">Xac nhan huy</button>
            </div>
        </form>
    </div>
</div>

<script>
    function pdOpenModal(name) {
        var el = document.getElementById('pd-modal-' + name);
        if (el) {
            el.classList.add('open');
            var ta = el.querySelector('textarea');
            if (ta) { ta.focus(); }
        }
    }
    function pdCloseModal(name) {
        var el = document.getElementById('pd-modal-' + name);
        if (el) { el.classList.remove('open'); }
    }
    function pdValidateReason(form) {
        var ta = form.querySelector('textarea[name="reason"]');
        var val = ta ? ta.value.trim() : '';
        if (val.length < 5) {
            alert('Vui long nhap ly do (toi thieu 5 ky tu).');
            if (ta) { ta.focus(); }
            return false;
        }
        return true;
    }
    // Close on overlay backdrop click.
    document.addEventListener('click', function (e) {
        if (e.target && e.target.classList && e.target.classList.contains('pd-modal-overlay')) {
            e.target.classList.remove('open');
        }
    });
    // Close on Escape.
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            var open = document.querySelectorAll('.pd-modal-overlay.open');
            for (var i = 0; i < open.length; i++) { open[i].classList.remove('open'); }
        }
    });
    // Tab switching
    function pdSwitchTab(tab) {
        document.getElementById('pd-panel-info').style.display = (tab === 'info') ? '' : 'none';
        document.getElementById('pd-panel-history').style.display = (tab === 'history') ? '' : 'none';
        document.getElementById('pd-tab-info').style.color = (tab === 'info') ? '#003399' : '#6b7280';
        document.getElementById('pd-tab-info').style.borderBottomColor = (tab === 'info') ? '#003399' : 'transparent';
        document.getElementById('pd-tab-history').style.color = (tab === 'history') ? '#003399' : '#6b7280';
        document.getElementById('pd-tab-history').style.borderBottomColor = (tab === 'history') ? '#003399' : 'transparent';
    }
</script>

<jsp:include page="../common/footer.jsp" />
