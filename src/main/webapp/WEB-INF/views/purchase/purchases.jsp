<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="purchases" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Phieu nhap hang</h1>
        <a href="${ctx}/admin/purchases?action=create" class="kr-btn kr-btn-primary"
           style="background:#2563eb;color:#fff;border-color:#2563eb;">+ Tao phieu nhap</a>
    </div>

    <form method="get" action="${ctx}/admin/purchases" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="Tim theo ma phieu, nha cung cap" />
            <button type="submit" title="Tim kiem">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <div class="kr-filter-section">
                <div class="kr-filter-label" style="font-weight:700;color:#111827;">QUAN LY NHAP HANG</div>
            </div>
            <form method="get" action="${ctx}/admin/purchases">
                <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>" />

                <%-- Legacy status filter (draft/confirmed/received). Kept as-is per USER DECISION 1. --%>
                <div class="kr-filter-section">
                    <div class="kr-filter-label">Trang thai</div>
                    <select class="kr-filter-select" name="status" onchange="this.form.submit()">
                        <option value="">Tat ca</option>
                        <option value="draft"     <c:if test="${status == 'draft'}">selected</c:if>>Nhap (draft)</option>
                        <option value="confirmed" <c:if test="${status == 'confirmed'}">selected</c:if>>Da xac nhan</option>
                        <option value="received"  <c:if test="${status == 'received'}">selected</c:if>>Da nhap kho</option>
                        <option value="cancelled" <c:if test="${status == 'cancelled'}">selected</c:if>>Da huy</option>
                    </select>
                </div>

                <%-- Approval-workflow status filter (uppercase DocumentStatus). Param: approvalStatus. --%>
                <div class="kr-filter-section">
                    <div class="kr-filter-label">Trạng thái duyệt</div>
                    <select class="kr-filter-select" name="approvalStatus" onchange="this.form.submit()">
                        <option value="">Tat ca</option>
                        <option value="DRAFT"            <c:if test="${param.approvalStatus == 'DRAFT'}">selected</c:if>>Nháp</option>
                        <option value="PENDING_APPROVAL" <c:if test="${param.approvalStatus == 'PENDING_APPROVAL'}">selected</c:if>>Chờ duyệt</option>
                        <option value="APPROVED"         <c:if test="${param.approvalStatus == 'APPROVED'}">selected</c:if>>Đã duyệt</option>
                        <option value="REJECTED"         <c:if test="${param.approvalStatus == 'REJECTED'}">selected</c:if>>Từ chối</option>
                        <option value="RECEIVING"        <c:if test="${param.approvalStatus == 'RECEIVING'}">selected</c:if>>Đang nhận</option>
                        <option value="COMPLETED"        <c:if test="${param.approvalStatus == 'COMPLETED'}">selected</c:if>>Hoàn thành</option>
                        <option value="CANCELLED"        <c:if test="${param.approvalStatus == 'CANCELLED'}">selected</c:if>>Đã hủy</option>
                    </select>
                </div>
            </form>
        </aside>

        <section class="kr-content">
            <div class="kr-table-wrap">
                <table class="kr-table">
                    <thead>
                        <tr>
                            <th class="kr-col-code">Ma phieu</th>
                            <th>Nha cung cap</th>
                            <th class="kr-col-num">Tong tien</th>
                            <th>Trang thai</th>
                            <th class="kr-col-time">Ngay tao</th>
                            <th style="width:100px;">Thao tac</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="6" class="kr-empty">Chua co phieu nhap nao.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="po" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <a class="kr-code-link"
                                               href="${ctx}/admin/purchases?action=view&id=${po.purchaseOrderId}">
                                                <c:out value="${po.orderCode}" />
                                            </a>
                                        </td>
                                        <td><c:out value="${po.supplierName}" /></td>
                                        <td class="num">
                                            <fmt:formatNumber value="${po.totalAmount}" type="number"
                                                              maxFractionDigits="0" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${po.status == 'draft'}">
                                                    <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                                 font-size:12px;font-weight:600;background:#e5e7eb;color:#374151;">
                                                        Nhap
                                                    </span>
                                                </c:when>
                                                <c:when test="${po.status == 'confirmed'}">
                                                    <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                                 font-size:12px;font-weight:600;background:#dbeafe;color:#1d4ed8;">
                                                        Da xac nhan
                                                    </span>
                                                </c:when>
                                                <c:when test="${po.status == 'received'}">
                                                    <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                                 font-size:12px;font-weight:600;background:#d1fae5;color:#059669;">
                                                        Da nhap kho
                                                    </span>
                                                </c:when>
                                                <c:when test="${po.status == 'cancelled'}">
                                                    <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                                 font-size:12px;font-weight:600;background:#fee2e2;color:#dc2626;">
                                                        Da huy
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="display:inline-block;padding:4px 10px;border-radius:12px;
                                                                 font-size:12px;font-weight:600;background:#e5e7eb;color:#374151;">
                                                        <c:out value="${po.status}" />
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="kr-col-time">
                                            <c:if test="${not empty po.createdAt}">
                                                <fmt:formatDate value="${po.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="${ctx}/admin/purchases?action=view&id=${po.purchaseOrderId}"
                                               class="kr-btn" style="padding:4px 10px;font-size:13px;">Xem</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="kr-pagination-wrap">
                <c:set var="baseUrl"
                       value="${ctx}/admin/purchases?keyword=${keyword}&status=${status}&approvalStatus=${param.approvalStatus}"
                       scope="request" />
                <jsp:include page="../common/pagination.jsp" />
            </div>
        </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
