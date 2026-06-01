<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KiotRetail - Bán hàng</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        html, body { height: 100%; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            font-size: 14px;
            color: #15171a;
            background: #f0f1f3;
        }
        a { text-decoration: none; color: inherit; }
        button { font-family: inherit; cursor: pointer; }
        input, select, textarea { font-family: inherit; font-size: 14px; outline: none; }

        .pos-wrapper { display: flex; flex-direction: column; height: 100vh; }
        .pos-content { flex: 1; display: flex; min-height: 0; overflow: hidden; }

        .pos-left {
            flex: 0 0 60%;
            display: flex; flex-direction: column;
            background: #f0f1f3;
            border-right: 1px solid #c2c7ce;
            min-width: 0;
        }
        .search-bar {
            flex: 0 0 auto;
            background: #ffffff;
            border-bottom: 1px solid #e5e7eb;
            padding: 12px 16px;
            display: flex; align-items: center; gap: 8px;
        }
        .search-bar input[name="keyword"] {
            flex: 1; height: 38px;
            border: 1px solid #c2c7ce;
            border-radius: 6px;
            padding: 0 12px;
            background: #fff;
        }
        .search-bar input[name="keyword"]:focus { border-color: #0070f4; }
        .search-bar button {
            height: 38px; padding: 0 18px;
            background: #0070f4; color: #fff;
            border: none; border-radius: 6px;
            font-weight: 500; font-size: 14px;
        }
        .search-bar button:hover { background: #005fcf; }

        .product-results { flex: 1; overflow-y: auto; padding: 14px 16px; }
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 12px;
        }
        .product-card {
            background: #ffffff;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 12px;
            display: flex; flex-direction: column; gap: 6px;
            transition: box-shadow 0.15s ease, border-color 0.15s ease;
        }
        .product-card:hover {
            border-color: #0070f4;
            box-shadow: 0 2px 8px rgba(0, 112, 244, 0.08);
        }
        .product-card .p-name {
            font-size: 14px; font-weight: 500; color: #15171a;
            line-height: 18px; min-height: 36px;
            display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .product-card .p-sku { font-size: 12px; color: #85909d; }
        .product-card .p-price { font-size: 15px; font-weight: 600; color: #0070f4; margin-top: 2px; }
        .product-card form { margin-top: auto; }
        .product-card .add-btn {
            width: 100%; height: 34px;
            background: #0070f4; color: #fff;
            border: none; border-radius: 6px;
            font-size: 13px; font-weight: 500;
        }
        .product-card .add-btn:hover { background: #005fcf; }

        .empty-state {
            text-align: center; color: #85909d;
            padding: 40px 20px; font-size: 14px;
        }

        .pos-right {
            flex: 0 0 40%;
            display: flex; flex-direction: column;
            background: #ffffff;
            min-width: 0;
        }
        .cart-header {
            flex: 0 0 auto;
            padding: 14px 16px;
            border-bottom: 1px solid #e5e7eb;
            display: flex; align-items: center; justify-content: space-between;
        }
        .cart-header h3 { font-size: 16px; font-weight: 600; color: #15171a; }
        .cart-clear-btn {
            height: 30px; padding: 0 12px;
            background: #fff; color: #ed232f;
            border: 1px solid #ed232f; border-radius: 6px;
            font-size: 12px; font-weight: 500;
        }
        .cart-clear-btn:hover { background: #fff5f5; }

        .cart-table-wrap { flex: 1; overflow-y: auto; padding: 0 8px; }
        table.cart-table { width: 100%; border-collapse: collapse; font-size: 13px; }
        table.cart-table thead th {
            position: sticky; top: 0;
            background: #f7f8fa;
            text-align: left; padding: 8px;
            border-bottom: 1px solid #e5e7eb;
            color: #525d6a; font-weight: 500; font-size: 12px;
        }
        table.cart-table thead th.num,
        table.cart-table tbody td.num { text-align: right; }
        table.cart-table tbody td {
            padding: 8px;
            border-bottom: 1px solid #f0f1f3;
            vertical-align: middle;
        }
        table.cart-table tbody tr:hover { background: #f7faff; }
        td.col-name { color: #15171a; }
        td.col-name .meta { color: #85909d; font-size: 11px; margin-top: 2px; }
        .qty-form { display: inline-flex; align-items: center; gap: 4px; }
        .qty-form input[type="number"] {
            width: 56px; height: 28px;
            border: 1px solid #c2c7ce; border-radius: 4px;
            text-align: center; padding: 0 4px;
        }
        .qty-form input[type="number"]:focus { border-color: #0070f4; }
        .qty-form button {
            height: 28px; padding: 0 8px;
            background: #f0f1f3; color: #525d6a;
            border: 1px solid #c2c7ce; border-radius: 4px;
            font-size: 11px;
        }
        .qty-form button:hover { background: #e0e3e7; }
        .remove-btn {
            background: transparent; border: none;
            color: #85909d;
            width: 26px; height: 26px;
            border-radius: 50%;
            display: inline-flex; align-items: center; justify-content: center;
            font-size: 16px; line-height: 1;
        }
        .remove-btn:hover { background: #fff0f0; color: #ed232f; }

        .cart-footer {
            flex: 0 0 auto;
            border-top: 1px solid #e5e7eb;
            background: #ffffff;
            padding: 14px 16px;
            display: flex; flex-direction: column; gap: 10px;
        }
        .summary-row {
            display: flex; justify-content: space-between; align-items: center;
            font-size: 14px;
        }
        .summary-row .label { color: #525d6a; }
        .summary-row .value { color: #15171a; font-weight: 500; }
        .summary-total {
            display: flex; justify-content: space-between; align-items: center;
            padding-top: 8px;
            border-top: 1px dashed #e5e7eb;
        }
        .summary-total .label { color: #15171a; font-size: 15px; font-weight: 600; }
        .summary-total .value { color: #0070f4; font-size: 22px; font-weight: 700; }

        .pay-row { display: flex; align-items: center; gap: 10px; }
        .pay-row label { color: #525d6a; font-size: 13px; flex: 0 0 110px; }
        .pay-row select {
            flex: 1; height: 36px;
            border: 1px solid #c2c7ce; border-radius: 6px;
            padding: 0 10px; background: #fff;
        }
        .pay-row select:focus { border-color: #0070f4; }

        .checkout-btn {
            width: 100%; height: 50px;
            background: #16a34a; color: #ffffff;
            border: none; border-radius: 8px;
            font-size: 16px; font-weight: 600;
            letter-spacing: 0.4px; text-transform: uppercase;
            transition: background 0.15s ease;
        }
        .checkout-btn:hover { background: #15803d; }
        .checkout-btn:disabled { background: #9ca3af; cursor: not-allowed; }

        .alert {
            padding: 10px 14px; border-radius: 6px;
            font-size: 13px; margin: 12px 16px 0;
        }
        .alert-error { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }
        .alert-success { background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }

        ::-webkit-scrollbar { width: 8px; height: 8px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background: #c2c7ce; border-radius: 4px; }
        ::-webkit-scrollbar-thumb:hover { background: #85909d; }
    </style>
</head>
<body>
<div class="pos-wrapper">

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <jsp:include page="/WEB-INF/views/common/toast.jsp" />
    <c:if test="${not empty errorMessage}">
        <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${errorMessage}" escapeXml="true"/>', 'danger'); });</script>
    </c:if>
    <c:if test="${not empty successMessage}">
        <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${successMessage}" escapeXml="true"/>', 'success'); });</script>
    </c:if>

    <div class="pos-content">

        <section class="pos-left">
            <div class="search-bar">
                <form method="post" action="${ctx}/pos/sale" style="display:flex; flex:1; gap:8px;">
                    <input type="hidden" name="action" value="search" />
                    <input type="text" name="keyword" placeholder="Tìm sản phẩm theo tên hoặc SKU..." value="<c:out value='${param.keyword}'/>" autofocus />
                    <button type="submit">Tìm kiếm</button>
                </form>
            </div>

            <div class="product-results">
                <c:choose>
                    <c:when test="${not empty products}">
                        <div class="product-grid">
                            <c:forEach items="${products}" var="p">
                                <div class="product-card">
                                    <div class="p-name"><c:out value="<c:out value='${p.name}'/>" /></div>
                                    <div class="p-sku">SKU: <c:out value="<c:out value='${p.sku}'/>" /></div>
                                    <div class="p-price">
                                        <fmt:formatNumber value="<c:out value='${p.unitPrice}'/>" type="number" groupingUsed="true" /> d
                                    </div>
                                    <form method="post" action="${ctx}/pos/sale">
                                        <input type="hidden" name="action" value="addItem" />
                                        <input type="hidden" name="productId" value="${p.id}" />
                                        <input type="hidden" name="productName" value="<c:out value='${p.name}'/>" />
                                        <input type="hidden" name="sku" value="<c:out value='${p.sku}'/>" />
                                        <input type="hidden" name="unitPrice" value="<c:out value='${p.unitPrice}'/>" />
                                        <input type="hidden" name="quantity" value="1" />
                                        <button type="submit" class="add-btn">+ Thêm vào giỏ</button>
                                    </form>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <c:choose>
                                <c:when test="${not empty param.keyword}">
                                    Không tìm thấy sản phẩm nào.
                                </c:when>
                                <c:otherwise>
                                    Nhập từ khóa và bấm Tìm kiếm để hiển thị sản phẩm.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <aside class="pos-right">
            <div class="cart-header">
                <h3>Giỏ hàng
                    <c:if test="${not empty cart and not empty cart.items}">
                        (<c:out value="${fn:length(cart.items)}" />)
                    </c:if>
                </h3>
                <form method="post" action="${ctx}/pos/sale" onsubmit="return confirm('Xóa toàn bộ giỏ hàng?');" style="margin:0;">
                    <input type="hidden" name="action" value="clear" />
                    <button type="submit" class="cart-clear-btn">Xóa giỏ</button>
                </form>
            </div>

            <div class="cart-table-wrap">
                <c:choose>
                    <c:when test="${not empty cart and not empty cart.items}">
                        <table class="cart-table">
                            <thead>
                                <tr>
                                    <th>Sản phẩm</th>
                                    <th class="num">SL</th>
                                    <th class="num">Đơn giá</th>
                                    <th class="num">Thành tiền</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${cart.items}" var="item">
                                    <tr>
                                        <td class="col-name">
                                            <div><c:out value="${item.productName}" /></div>
                                            <c:if test="${not empty item.sku}">
                                                <div class="meta">SKU: <c:out value="${item.sku}" /></div>
                                            </c:if>
                                        </td>
                                        <td class="num">
                                            <form method="post" action="${ctx}/pos/sale" class="qty-form">
                                                <input type="hidden" name="action" value="updateQty" />
                                                <input type="hidden" name="productId" value="${item.productId}" />
                                                <input type="number" name="quantity" min="1" value="<c:out value='${item.quantity}'/>" />
                                                <button type="submit" title="Cập nhật">OK</button>
                                            </form>
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true" />
                                        </td>
                                        <td class="num">
                                            <strong><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true" /></strong>
                                        </td>
                                        <td>
                                            <form method="post" action="${ctx}/pos/sale" style="margin:0;">
                                                <input type="hidden" name="action" value="removeItem" />
                                                <input type="hidden" name="productId" value="${item.productId}" />
                                                <button type="submit" class="remove-btn" title="Xoa">x</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">Giỏ hàng trống. Thêm sản phẩm từ danh sách bên trái.</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="cart-footer">
                <div class="summary-row">
                    <span class="label">Tạm tính</span>
                    <span class="value">
                        <fmt:formatNumber value="${cart.subtotal != null ? cart.subtotal : 0}" type="number" groupingUsed="true" /> d
                    </span>
                </div>
                <div class="summary-row">
                    <span class="label">Giảm giá</span>
                    <span class="value">
                        - <fmt:formatNumber value="${cart.discount != null ? cart.discount : 0}" type="number" groupingUsed="true" /> d
                    </span>
                </div>
                <div class="summary-total">
                    <span class="label">Tổng tiền</span>
                    <span class="value">
                        <fmt:formatNumber value="${cart.total != null ? cart.total : 0}" type="number" groupingUsed="true" /> d
                    </span>
                </div>

                <form method="post" action="${ctx}/pos/sale" style="display:flex; flex-direction:column; gap:10px; margin:0;">
                    <input type="hidden" name="action" value="checkout" />
                    <div class="pay-row">
                        <label for="paymentMethod">Thanh toán</label>
                        <select name="paymentMethod" id="paymentMethod">
                            <option value="cash" ${cart.paymentMethod == 'cash' ? 'selected' : ''}>Tiền mặt</option>
                            <option value="card" ${cart.paymentMethod == 'card' ? 'selected' : ''}>Thẻ</option>
                            <option value="transfer" ${cart.paymentMethod == 'transfer' ? 'selected' : ''}>Chuyển khoản</option>
                        </select>
                    </div>
                    <button type="submit" class="checkout-btn"
                        <c:if test="${empty cart or empty cart.items}">disabled</c:if>>
                        Thanh toán
                    </button>
                </form>
            </div>
        </aside>

    </div>
</div>
<script src="${ctx}/assets/js/main.js"></script>
<link rel="stylesheet" href="${ctx}/assets/css/kr-common.css" />
</body>
</html>
