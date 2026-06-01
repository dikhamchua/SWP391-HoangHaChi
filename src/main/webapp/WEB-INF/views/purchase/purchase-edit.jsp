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
        <div>
            <div style="font-size:13px;color:#6b7280;margin-bottom:4px;">
                <a href="${ctx}/admin/purchases" style="color:#2563eb;text-decoration:none;">Phieu nhap hang</a>
                <span style="margin:0 6px;">/</span>
                <a href="${ctx}/admin/purchases?action=view&id=${order.purchaseOrderId}"
                   style="color:#2563eb;text-decoration:none;"><c:out value="${order.orderCode}" /></a>
                <span style="margin:0 6px;">/</span>
                <span>Sua phieu nhap</span>
            </div>
            <h1 class="kr-page-title">Sua phieu nhap <c:out value="${order.orderCode}" /></h1>
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
            <form method="post" action="${ctx}/admin/purchases?action=update&id=${order.purchaseOrderId}"
                  id="poForm" onsubmit="return validatePO();">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="id" value="${order.purchaseOrderId}" />

                <!-- Header info card -->
                <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;
                            padding:20px;margin-bottom:20px;">
                    <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">
                        Thong tin chung
                    </h3>
                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;">
                        <div>
                            <label style="display:block;font-size:13px;font-weight:600;
                                          margin-bottom:6px;color:#374151;">Nha cung cap *</label>
                            <select name="supplierId" class="kr-filter-select" style="width:100%;" required>
                                <option value="">-- Chon nha cung cap --</option>
                                <c:forEach var="sup" items="${suppliers}">
                                    <option value="${sup.supplierId}"
                                            <c:if test="${sup.supplierId == order.supplierId}">selected</c:if>>
                                        <c:out value="${sup.name}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <label style="display:block;font-size:13px;font-weight:600;
                                          margin-bottom:6px;color:#374151;">Ghi chu</label>
                            <input type="text" name="note" class="kr-filter-input" style="width:100%;"
                                   value="<c:out value='${order.note}' />"
                                   placeholder="Ghi chu cho phieu nhap" />
                        </div>
                    </div>
                </div>

                <!-- Detail rows card -->
                <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;
                            padding:20px;margin-bottom:20px;">
                    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
                        <h3 style="margin:0;font-size:16px;font-weight:600;color:#111827;">San pham</h3>
                        <button type="button" onclick="addRow()" class="kr-btn"
                                style="background:#10b981;color:#fff;border-color:#10b981;">+ Them dong</button>
                    </div>

                    <div class="kr-table-wrap">
                        <table class="kr-table" id="detailsTable" style="width:100%;">
                            <thead>
                                <tr>
                                    <th style="width:40%;">San pham</th>
                                    <th class="kr-col-num" style="width:120px;">So luong</th>
                                    <th class="kr-col-num" style="width:160px;">Don gia</th>
                                    <th class="kr-col-num" style="width:160px;">Thanh tien</th>
                                    <th style="width:80px;"></th>
                                </tr>
                            </thead>
                            <tbody id="detailsBody">
                                <c:forEach var="d" items="${orderDetails}">
                                    <tr>
                                        <td>
                                            <select name="productId" class="kr-filter-select"
                                                    style="width:100%;" onchange="onProductChange(this)" required>
                                                <option value="">-- Chon san pham --</option>
                                                <c:forEach var="p" items="${products}">
                                                    <option value="${p.productId}"
                                                            data-cost="<fmt:formatNumber value='${p.costPrice}' groupingUsed='false' maxFractionDigits='2' minFractionDigits='0' />"
                                                            <c:if test="${p.productId == d.productId}">selected</c:if>>
                                                        <c:out value="${p.sku}" /> - <c:out value="${p.productName}" />
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                        <td>
                                            <input type="number" name="quantity" min="1"
                                                   value="${d.quantity}"
                                                   class="kr-filter-input" style="width:100%;text-align:right;"
                                                   oninput="recalc(this)" required />
                                        </td>
                                        <td>
                                            <input type="number" name="unitCost" min="0" step="1000"
                                                   value="<fmt:formatNumber value='${d.unitCost}' groupingUsed='false' maxFractionDigits='0' />"
                                                   class="kr-filter-input" style="width:100%;text-align:right;"
                                                   oninput="recalc(this)" required />
                                        </td>
                                        <td class="num lineTotal" style="font-weight:600;">0</td>
                                        <td style="text-align:center;">
                                            <button type="button" onclick="removeRow(this)" class="kr-btn"
                                                    style="padding:4px 10px;background:#ef4444;color:#fff;border-color:#ef4444;">X</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="3" style="text-align:right;font-weight:600;">Tong cong</td>
                                    <td class="num" id="totalCell" style="font-weight:700;color:#111827;">0</td>
                                    <td></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

                <div style="text-align:right;">
                    <a href="${ctx}/admin/purchases?action=view&id=${order.purchaseOrderId}"
                       class="kr-btn" style="margin-right:8px;">Huy</a>
                    <button type="submit" class="kr-btn kr-btn-primary"
                            style="background:#2563eb;color:#fff;border-color:#2563eb;">
                        Luu thay doi
                    </button>
                </div>
            </form>
        </section>
    </div>
</div>

<script>
    var PRODUCTS = [
        <c:forEach var="p" items="${products}" varStatus="loop">
            { id: ${p.productId}, name: '<c:out value="${p.productName}" />',
              sku: '<c:out value="${p.sku}" />',
              cost: <fmt:formatNumber value="${p.costPrice}" maxFractionDigits="2" minFractionDigits="0" pattern="0.##"/> || 0 }<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];

    function buildProductOptions() {
        var html = '<option value="">-- Chon san pham --</option>';
        for (var i = 0; i < PRODUCTS.length; i++) {
            var p = PRODUCTS[i];
            html += '<option value="' + p.id + '" data-cost="' + p.cost + '">'
                  + p.sku + ' - ' + p.name + '</option>';
        }
        return html;
    }

    function addRow() {
        var body = document.getElementById('detailsBody');
        var tr = document.createElement('tr');
        tr.innerHTML =
            '<td>' +
                '<select name="productId" class="kr-filter-select" style="width:100%;" ' +
                        'onchange="onProductChange(this)" required>' +
                buildProductOptions() +
                '</select>' +
            '</td>' +
            '<td><input type="number" name="quantity" min="1" value="1" ' +
                       'class="kr-filter-input" style="width:100%;text-align:right;" ' +
                       'oninput="recalc(this)" required /></td>' +
            '<td><input type="number" name="unitCost" min="0" step="1000" value="0" ' +
                       'class="kr-filter-input" style="width:100%;text-align:right;" ' +
                       'oninput="recalc(this)" required /></td>' +
            '<td class="num lineTotal" style="font-weight:600;">0</td>' +
            '<td style="text-align:center;">' +
                '<button type="button" onclick="removeRow(this)" class="kr-btn" ' +
                        'style="padding:4px 10px;background:#ef4444;color:#fff;border-color:#ef4444;">X</button>' +
            '</td>';
        body.appendChild(tr);
    }

    function removeRow(btn) {
        var tr = btn.closest('tr');
        tr.parentNode.removeChild(tr);
        recalcAll();
    }

    function onProductChange(sel) {
        var opt = sel.options[sel.selectedIndex];
        var cost = parseFloat(opt.getAttribute('data-cost') || '0');
        var tr = sel.closest('tr');
        var costInput = tr.querySelector('input[name="unitCost"]');
        if (costInput && (!costInput.value || costInput.value === '0')) {
            costInput.value = cost || 0;
        }
        recalc(sel);
    }

    function recalc(input) {
        var tr = input.closest('tr');
        var qty = parseFloat(tr.querySelector('input[name="quantity"]').value || '0');
        var cost = parseFloat(tr.querySelector('input[name="unitCost"]').value || '0');
        var line = qty * cost;
        tr.querySelector('.lineTotal').textContent = line.toLocaleString('vi-VN');
        recalcAll();
    }

    function recalcAll() {
        var rows = document.querySelectorAll('#detailsBody tr');
        var total = 0;
        for (var i = 0; i < rows.length; i++) {
            var qty = parseFloat(rows[i].querySelector('input[name="quantity"]').value || '0');
            var cost = parseFloat(rows[i].querySelector('input[name="unitCost"]').value || '0');
            total += qty * cost;
        }
        document.getElementById('totalCell').textContent = total.toLocaleString('vi-VN');
    }

    function validatePO() {
        var rows = document.querySelectorAll('#detailsBody tr');
        if (rows.length === 0) {
            alert('Vui long them it nhat 1 san pham');
            return false;
        }
        return true;
    }

    document.addEventListener('DOMContentLoaded', function () {
        var rows = document.querySelectorAll('#detailsBody tr');
        if (rows.length === 0) {
            addRow();
        } else {
            recalcAll();
        }
    });
</script>

<jsp:include page="../common/footer.jsp" />
