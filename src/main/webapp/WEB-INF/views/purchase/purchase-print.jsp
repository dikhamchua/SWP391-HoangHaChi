<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Phieu nhap <c:out value="${order.orderCode}" /></title>
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            color: #111827; margin: 0; padding: 24px; background: #f3f4f6;
        }
        .print-sheet {
            background: #fff; max-width: 800px; margin: 0 auto;
            padding: 32px; border: 1px solid #e5e7eb;
        }
        .print-header { text-align: center; margin-bottom: 24px; }
        .print-header h1 { margin: 0 0 4px; font-size: 22px; }
        .print-header .code { font-size: 14px; color: #6b7280; }
        .meta-grid {
            display: grid; grid-template-columns: 1fr 1fr; gap: 8px 24px;
            font-size: 14px; margin-bottom: 24px;
        }
        .meta-grid .label { font-weight: 600; }
        table.items { width: 100%; border-collapse: collapse; font-size: 14px; }
        table.items th, table.items td {
            border: 1px solid #d1d5db; padding: 8px 10px; text-align: left;
        }
        table.items th { background: #f9fafb; }
        table.items td.num, table.items th.num { text-align: right; }
        .totals { margin-top: 16px; display: flex; justify-content: flex-end; }
        .totals table { font-size: 15px; }
        .totals td { padding: 6px 12px; }
        .totals .grand { font-weight: 700; font-size: 17px; }
        .signatures {
            display: grid; grid-template-columns: 1fr 1fr; gap: 24px;
            margin-top: 48px; text-align: center; font-size: 14px;
        }
        .signatures .sig-line { margin-top: 64px; border-top: 1px solid #9ca3af; padding-top: 6px; }
        .toolbar { text-align: center; margin-bottom: 16px; }
        .toolbar button {
            padding: 8px 18px; font-size: 14px; cursor: pointer;
            border: 1px solid #2563eb; background: #2563eb; color: #fff;
            border-radius: 6px; margin: 0 4px;
        }
        .toolbar button.secondary {
            background: #fff; color: #374151; border-color: #d1d5db;
        }
        @media print {
            body { background: #fff; padding: 0; }
            .print-sheet { border: none; max-width: none; padding: 0; }
            .toolbar { display: none; }
        }
    </style>
</head>
<body onload="window.print();">

    <div class="toolbar">
        <button type="button" onclick="window.print();">In phieu</button>
        <button type="button" class="secondary" onclick="window.close();">Dong</button>
    </div>

    <div class="print-sheet">
        <div class="print-header">
            <h1>PHIEU NHAP HANG</h1>
            <div class="code">So phieu: <c:out value="${order.orderCode}" /></div>
        </div>

        <div class="meta-grid">
            <div><span class="label">Nha cung cap:</span> <c:out value="${order.supplierName}" /></div>
            <div><span class="label">Chi nhanh:</span> <c:out value="${order.branchName}" /></div>
            <div><span class="label">Nhan vien:</span> <c:out value="${order.employeeName}" /></div>
            <div>
                <span class="label">Ngay tao:</span>
                <c:if test="${not empty order.createdAt}">
                    <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                </c:if>
            </div>
            <div style="grid-column:1 / -1;"><span class="label">Ghi chu:</span> <c:out value="${order.note}" /></div>
        </div>

        <table class="items">
            <thead>
                <tr>
                    <th style="width:40px;text-align:center;">#</th>
                    <th style="width:120px;">SKU</th>
                    <th>Ten san pham</th>
                    <th class="num" style="width:80px;">SL</th>
                    <th class="num" style="width:120px;">Don gia</th>
                    <th class="num" style="width:140px;">Thanh tien</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty orderDetails}">
                        <tr><td colspan="6" style="text-align:center;color:#6b7280;">Khong co san pham nao.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="d" items="${orderDetails}" varStatus="loop">
                            <tr>
                                <td style="text-align:center;">${loop.index + 1}</td>
                                <td><c:out value="${d.productSku}" /></td>
                                <td><c:out value="${d.productName}" /></td>
                                <td class="num"><fmt:formatNumber value="${d.quantity}" type="number" groupingUsed="true" /></td>
                                <td class="num"><fmt:formatNumber value="${d.unitCost}" type="number" groupingUsed="true" maxFractionDigits="0" /></td>
                                <td class="num"><fmt:formatNumber value="${d.subtotal}" type="number" groupingUsed="true" maxFractionDigits="0" /></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <div class="totals">
            <table>
                <tbody>
                    <tr>
                        <td class="grand">Tong cong</td>
                        <td class="grand" style="text-align:right;">
                            <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0" />
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="signatures">
            <div>
                Nguoi giao hang
                <div class="sig-line">(Ky, ghi ro ho ten)</div>
            </div>
            <div>
                Nguoi nhan hang
                <div class="sig-line">(Ky, ghi ro ho ten)</div>
            </div>
        </div>
    </div>

</body>
</html>
