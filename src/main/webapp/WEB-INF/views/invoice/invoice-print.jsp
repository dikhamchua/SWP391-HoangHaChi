<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Hóa đơn <c:out value="${order.orderCode}" /></title>
    <style>
        * { box-sizing: border-box; }
        html, body {
            margin: 0;
            padding: 0;
            background: #f3f4f6;
            font-family: "Courier New", Courier, monospace;
            font-size: 12px;
            color: #000;
        }
        .receipt {
            width: 280px;
            margin: 12px auto;
            padding: 10px 12px;
            background: #fff;
        }
        .center { text-align: center; }
        .right { text-align: right; }
        .bold { font-weight: 700; }
        .sep {
            border: 0;
            border-top: 1px dashed #000;
            margin: 6px 0;
        }
        .company { font-size: 14px; font-weight: 700; }
        .small { font-size: 11px; }
        .row {
            display: flex;
            justify-content: space-between;
            gap: 8px;
            line-height: 1.5;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 12px;
        }
        thead th {
            border-top: 1px dashed #000;
            border-bottom: 1px dashed #000;
            padding: 3px 0;
            text-align: left;
            font-weight: 700;
        }
        tbody td {
            padding: 2px 0;
            vertical-align: top;
        }
        .num { text-align: right; font-variant-numeric: tabular-nums; }
        .item-name { font-weight: 600; }
        .footer-note {
            text-align: center;
            margin-top: 8px;
            font-size: 11px;
            font-style: italic;
        }
        .toolbar {
            width: 280px;
            margin: 12px auto 0;
            text-align: center;
        }
        .toolbar button {
            padding: 8px 14px;
            border: 1px solid #2563eb;
            background: #2563eb;
            color: #fff;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
            margin: 0 4px;
        }
        .toolbar button.secondary {
            background: #fff;
            color: #2563eb;
        }
        @media print {
            html, body { background: #fff; }
            .toolbar { display: none; }
            .receipt {
                width: 80mm;
                margin: 0;
                padding: 4mm;
            }
            @page {
                size: 80mm auto;
                margin: 0;
            }
        }
    </style>
</head>
<body>
    <div class="toolbar">
        <button type="button" onclick="window.print();">In hóa đơn</button>
        <button type="button" class="secondary" onclick="window.close();">Đóng</button>
    </div>

    <div class="receipt">
        <div class="center company">KIOTRETAIL</div>
        <div class="center small">Hệ thống quản lý cửa hàng bán lẻ</div>
        <div class="center small"><c:out value="${order.branchName}" /></div>
        <div class="center small">ĐT: 0000.000.000</div>

        <hr class="sep" />

        <div class="center bold">HÓA ĐƠN BÁN HÀNG</div>
        <c:if test="${order.status == 'cancelled'}">
            <div class="center bold" style="border:2px solid #000;padding:4px;margin:4px 0;font-size:13px;">ĐÃ HỦY</div>
        </c:if>
        <div class="center small">Số: <c:out value="${order.orderCode}" /></div>
        <div class="center small">
            Ngày:
            <c:if test="${not empty order.createdAt}">
                <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" />
            </c:if>
        </div>

        <hr class="sep" />

        <div class="row"><span>Khách hàng:</span><span><c:out value="${order.customerName}" /></span></div>
        <div class="row"><span>Nhân viên:</span><span><c:out value="${order.employeeName}" /></span></div>
        <div class="row"><span>Loại đơn:</span><span><c:out value="${order.orderType}" /></span></div>

        <table>
            <thead>
                <tr>
                    <th>Mặt hàng</th>
                    <th class="num">SL</th>
                    <th class="num">T.Tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty orderDetails}">
                        <tr><td colspan="3" class="center">(Không có sản phẩm)</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="item" items="${orderDetails}">
                            <tr>
                                <td colspan="3" class="item-name"><c:out value="${item.productName}" /></td>
                            </tr>
                            <tr>
                                <td class="small">
                                    <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true" />
                                </td>
                                <td class="num">
                                    <fmt:formatNumber value="${item.quantity}" type="number" groupingUsed="true" />
                                </td>
                                <td class="num">
                                    <fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true" />
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <hr class="sep" />

        <div class="row">
            <span>Tạm tính:</span>
            <span><fmt:formatNumber value="${order.subtotal}" type="number" groupingUsed="true" /></span>
        </div>
        <div class="row">
            <span>Giảm giá:</span>
            <span>- <fmt:formatNumber value="${order.discountAmount}" type="number" groupingUsed="true" /></span>
        </div>
        <div class="row bold" style="font-size:13px;">
            <span>TỔNG CỘNG:</span>
            <span><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true" /></span>
        </div>

        <hr class="sep" />

        <div class="bold">Thanh toán:</div>
        <c:choose>
            <c:when test="${empty payments}">
                <div class="small">(Chưa thanh toán)</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="pay" items="${payments}">
                    <div class="row small">
                        <span>
                            <c:choose>
                                <c:when test="${pay.paymentMethod == 'cash'}">Tiền mặt</c:when>
                                <c:when test="${pay.paymentMethod == 'transfer'}">Chuyển khoản</c:when>
                                <c:when test="${pay.paymentMethod == 'card'}">Thẻ</c:when>
                                <c:otherwise><c:out value="${pay.paymentMethod}" /></c:otherwise>
                            </c:choose>
                        </span>
                        <span><fmt:formatNumber value="${pay.amount}" type="number" groupingUsed="true" /></span>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <hr class="sep" />

        <div class="footer-note">Cảm ơn quý khách!</div>
        <div class="footer-note">Hẹn gặp lại lần sau.</div>
    </div>

    <script>
        window.addEventListener("load", function () {
            setTimeout(function () { window.print(); }, 250);
        });
    </script>
</body>
</html>
