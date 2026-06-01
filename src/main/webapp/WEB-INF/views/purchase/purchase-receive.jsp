<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="purchases" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    .kr-receive-bar {
        position: sticky; bottom: 0; left: 0; right: 0;
        background: #fff; border-top: 1px solid #e5e7eb;
        padding: 14px 24px; display: flex; justify-content: space-between;
        align-items: center; box-shadow: 0 -2px 8px rgba(0,0,0,0.05);
        z-index: 10;
    }
    .kr-line-received { color: #6b7280; font-size: 13px; }
    .kr-line-outstanding { font-weight: 600; color: #111827; }
</style>

<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <div>
            <div style="font-size:13px;color:#6b7280;margin-bottom:4px;">
                <a href="${ctx}/admin/purchases" style="color:#2563eb;text-decoration:none;">Phieu nhap hang</a>
                <span style="margin:0 6px;">/</span>
                <a href="${ctx}/admin/purchases?action=view&id=${order.purchaseOrderId}"
                   style="color:#2563eb;text-decoration:none;"><c:out value="${order.orderCode}" /></a>
                <span style="margin:0 6px;">/</span>
                <span>Nhan hang</span>
            </div>
            <h1 class="kr-page-title">Nhan hang - <c:out value="${order.orderCode}" /></h1>
        </div>
        <div>
            <a href="${ctx}/admin/purchases?action=view&id=${order.purchaseOrderId}" class="kr-btn">Quay lai</a>
        </div>
    </div>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <div class="kr-filter-section">
                <div class="kr-filter-label" style="font-weight:700;color:#111827;">QUAN LY NHAP HANG</div>
            </div>
        </aside>

        <section class="kr-content" style="padding:24px;">
            <form method="post" action="${ctx}/admin/purchases?action=receiveSubmit&id=${order.purchaseOrderId}"
                  id="receiveForm">
                <input type="hidden" name="action" value="receiveSubmit" />
                <input type="hidden" name="id" value="${order.purchaseOrderId}" />

                <!-- Order info card -->
                <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                    <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Thong tin phieu</h3>
                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;font-size:14px;">
                        <div><span style="font-weight:600;">Ma phieu:</span> <c:out value="${order.orderCode}" /></div>
                        <div><span style="font-weight:600;">Nha cung cap:</span> <c:out value="${order.supplierName}" /></div>
                        <div><span style="font-weight:600;">Chi nhanh:</span> <c:out value="${order.branchName}" /></div>
                        <div><span style="font-weight:600;">Ghi chu:</span> <c:out value="${order.note}" /></div>
                    </div>
                </div>

                <!-- Receive lines -->
                <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                    <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Danh sach san pham</h3>
                    <div class="kr-table-wrap">
                        <table class="kr-table" style="width:100%;">
                            <thead>
                                <tr>
                                    <th style="width:50px;text-align:center;">#</th>
                                    <th style="width:140px;">SKU</th>
                                    <th>Ten san pham</th>
                                    <th class="kr-col-num" style="width:100px;">Dat hang</th>
                                    <th class="kr-col-num" style="width:100px;">Da nhan</th>
                                    <th class="kr-col-num" style="width:100px;">Con lai</th>
                                    <th class="kr-col-num" style="width:160px;">Nhan lan nay</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty orderDetails}">
                                        <tr><td colspan="7" class="kr-empty">Khong co san pham nao.</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="d" items="${orderDetails}" varStatus="loop">
                                            <tr>
                                                <td style="text-align:center;">${loop.index + 1}</td>
                                                <td><c:out value="${d.productSku}" /></td>
                                                <td><c:out value="${d.productName}" /></td>
                                                <td class="num">
                                                    <fmt:formatNumber value="${d.quantity}" type="number" groupingUsed="true" />
                                                </td>
                                                <td class="num kr-line-received">
                                                    <fmt:formatNumber value="${d.receivedQuantity}" type="number" groupingUsed="true" />
                                                </td>
                                                <td class="num kr-line-outstanding">
                                                    <fmt:formatNumber value="${d.outstandingQuantity}" type="number" groupingUsed="true" />
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${d.outstandingQuantity > 0}">
                                                            <input type="number"
                                                                   name="receivedQty[${d.poDetailId}]"
                                                                   min="0" max="${d.outstandingQuantity}"
                                                                   value="${d.outstandingQuantity}"
                                                                   class="kr-filter-input"
                                                                   style="width:100%;text-align:right;" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="kr-line-received">Da nhan du</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Sticky bottom action bar -->
                <div class="kr-receive-bar">
                    <div style="font-size:13px;color:#6b7280;">
                        Nhap so luong thuc nhan cho tung dong, sau do bam "Luu nhan hang".
                    </div>
                    <div>
                        <a href="${ctx}/admin/purchases?action=view&id=${order.purchaseOrderId}"
                           class="kr-btn" style="margin-right:8px;">Huy</a>
                        <button type="submit" class="kr-btn kr-btn-primary"
                                style="background:#10b981;color:#fff;border-color:#10b981;">
                            Luu nhan hang
                        </button>
                    </div>
                </div>
            </form>
        </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
