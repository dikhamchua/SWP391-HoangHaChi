<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="dashboard" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    body { background: #f5f5f5; font-family: Inter, Roboto, Helvetica, Arial, sans-serif; font-size: 14px; color: #15171a; margin: 0; }
    .kr-page { padding: 16px; max-width: 1600px; margin: 0 auto; }
    .kr-page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
    .kr-page-title { font-size: 20px; font-weight: 700; color: #15171a; margin: 0; }

    .kr-summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
    .kr-card { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); padding: 16px 18px; border-left: 4px solid #0070f4; }
    .kr-card.green  { border-left-color: #00b882; }
    .kr-card.orange { border-left-color: #f59e0b; }
    .kr-card.purple { border-left-color: #8b5cf6; }
    .kr-card-label { font-size: 13px; color: #5f6368; margin-bottom: 8px; font-weight: 500; }
    .kr-card-value { font-size: 24px; font-weight: 700; color: #15171a; line-height: 1.2; }
    .kr-card-suffix { font-size: 13px; color: #5f6368; font-weight: 500; margin-left: 4px; }

    .kr-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .kr-panel { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); overflow: hidden; }
    .kr-panel-header { padding: 14px 18px; border-bottom: 1px solid #e8eaed; }
    .kr-panel-title { font-size: 15px; font-weight: 600; color: #15171a; margin: 0; }
    .kr-panel-body { padding: 0; }

    table.kr-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #15171a; }
    table.kr-table thead th { background: #e6f1fe; border-bottom: 1px solid #b3d4fc; font-size: 12px; font-weight: 600; text-align: left; padding: 10px 14px; white-space: nowrap; color: #15171a; }
    table.kr-table thead th.num { text-align: right; }
    table.kr-table tbody td { padding: 10px 14px; border-bottom: 1px solid #e8eaed; vertical-align: middle; }
    table.kr-table tbody td.num { text-align: right; }
    table.kr-table tbody tr:hover { background: #f8fafc; }
    table.kr-table tbody tr:last-child td { border-bottom: none; }
    .kr-empty-row td { text-align: center; padding: 32px 16px; color: #5f6368; font-size: 13px; }

    @media (max-width: 1100px) {
        .kr-summary { grid-template-columns: repeat(2, 1fr); }
        .kr-row { grid-template-columns: 1fr; }
    }
    @media (max-width: 600px) {
        .kr-summary { grid-template-columns: 1fr; }
    }
</style>

<div class="kr-page">
    <div class="kr-page-header">
        <h1 class="kr-page-title">Tổng quan</h1>
    </div>

    <div class="kr-summary">
        <div class="kr-card green">
            <div class="kr-card-label">Doanh thu hôm nay</div>
            <div class="kr-card-value">
                <fmt:formatNumber value="${summary.todayRevenue != null ? summary.todayRevenue : 0}" type="number" maxFractionDigits="0" />
                <span class="kr-card-suffix">VND</span>
            </div>
        </div>
        <div class="kr-card">
            <div class="kr-card-label">Đơn hàng hôm nay</div>
            <div class="kr-card-value">
                <fmt:formatNumber value="${summary.todayOrders != null ? summary.todayOrders : 0}" type="number" maxFractionDigits="0" />
            </div>
        </div>
        <div class="kr-card purple">
            <div class="kr-card-label">Tổng khách hàng</div>
            <div class="kr-card-value">
                <fmt:formatNumber value="${summary.totalCustomers != null ? summary.totalCustomers : 0}" type="number" maxFractionDigits="0" />
            </div>
        </div>
        <div class="kr-card orange">
            <div class="kr-card-label">Tổng sản phẩm</div>
            <div class="kr-card-value">
                <fmt:formatNumber value="${summary.totalProducts != null ? summary.totalProducts : 0}" type="number" maxFractionDigits="0" />
            </div>
        </div>
    </div>

    <div class="kr-row">
        <div class="kr-panel">
            <div class="kr-panel-header">
                <h2 class="kr-panel-title">Top 5 sản phẩm bán chạy</h2>
            </div>
            <div class="kr-panel-body">
                <table class="kr-table">
                    <thead>
                        <tr>
                            <th>Tên SP</th>
                            <th class="num">Số lượng bán</th>
                            <th class="num">Doanh thu</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty topProducts}">
                                <c:forEach var="item" items="${topProducts}">
                                    <tr>
                                        <td><c:out value="${item.productName}" /></td>
                                        <td class="num">
                                            <fmt:formatNumber value="${item.quantitySold != null ? item.quantitySold : 0}" type="number" maxFractionDigits="0" />
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${item.revenue != null ? item.revenue : 0}" type="number" maxFractionDigits="0" /> VND
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr class="kr-empty-row"><td colspan="3">Chưa có dữ liệu</td></tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="kr-panel">
            <div class="kr-panel-header">
                <h2 class="kr-panel-title">Biểu đồ doanh thu</h2>
            </div>
            <div class="kr-panel-body">
                <table class="kr-table">
                    <thead>
                        <tr>
                            <th>Ngày</th>
                            <th class="num">Doanh thu</th>
                            <th class="num">Số đơn</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty revenueChart}">
                                <c:forEach var="row" items="${revenueChart}">
                                    <tr>
                                        <td><c:out value="${row.date}" /></td>
                                        <td class="num">
                                            <fmt:formatNumber value="${row.revenue != null ? row.revenue : 0}" type="number" maxFractionDigits="0" /> VND
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${row.orders != null ? row.orders : 0}" type="number" maxFractionDigits="0" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr class="kr-empty-row"><td colspan="3">Chưa có dữ liệu</td></tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
