<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="reports" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<style>
    body { background: #f5f5f5; font-family: Inter, Roboto, Helvetica, Arial, sans-serif; font-size: 14px; color: #15171a; margin: 0; }
    .kr-page { padding: 16px 20px; }
    .kr-page-title { font-size: 20px; line-height: 28px; font-weight: 700; color: #15171a; margin: 0 0 16px 0; }

    .kr-filter-card { background: #fff; border: 1px solid #e8eaed; border-radius: 8px; padding: 16px 20px; margin-bottom: 16px; }
    .kr-filter-form { display: flex; align-items: flex-end; gap: 16px; flex-wrap: wrap; }
    .kr-filter-group { display: flex; flex-direction: column; gap: 6px; }
    .kr-filter-label { font-size: 13px; font-weight: 600; color: #15171a; }
    .kr-filter-input { height: 36px; padding: 0 10px; border: 1px solid #e8eaed; border-radius: 6px; background: #fff; font-size: 14px; color: #15171a; min-width: 180px; }
    .kr-filter-input:focus { outline: none; border-color: #0070f4; }
    .kr-btn { height: 36px; padding: 0 18px; border-radius: 8px; font-size: 14px; font-weight: 600; background: #fff; color: #0070f4; border: 1px solid #e8eaed; cursor: pointer; }
    .kr-btn-primary { background: #0070f4; color: #fff; border-color: #0070f4; }
    .kr-btn-primary:hover { background: #005bd1; }

    .kr-summary { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 16px; }
    .kr-summary-card { background: #fff; border: 1px solid #e8eaed; border-radius: 8px; padding: 18px 20px; }
    .kr-summary-label { font-size: 13px; font-weight: 500; color: #5f6368; margin-bottom: 8px; }
    .kr-summary-value { font-size: 24px; font-weight: 700; color: #15171a; line-height: 32px; }
    .kr-summary-value.revenue { color: #0070f4; }
    .kr-summary-value.orders { color: #00b882; }
    .kr-summary-value.average { color: #ff9500; }
    .kr-summary-unit { font-size: 13px; font-weight: 500; color: #5f6368; margin-left: 4px; }

    .kr-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .kr-card { background: #fff; border: 1px solid #e8eaed; border-radius: 8px; overflow: hidden; }
    .kr-card-header { padding: 14px 20px; border-bottom: 1px solid #e8eaed; }
    .kr-card-title { font-size: 14px; font-weight: 600; color: #15171a; margin: 0; }
    .kr-table-wrap { overflow-x: auto; }
    table.kr-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #15171a; }
    table.kr-table thead th { background: #e6f1fe; border-bottom: 1px solid #b3d4fc; font-size: 12px; font-weight: 600; text-align: left; padding: 10px 14px; white-space: nowrap; color: #15171a; }
    table.kr-table thead th.num { text-align: right; }
    table.kr-table tbody tr:hover { background: #f8fafc; }
    table.kr-table tbody td { padding: 10px 14px; border-bottom: 1px solid #e8eaed; vertical-align: middle; }
    table.kr-table tbody td.num { text-align: right; font-variant-numeric: tabular-nums; }
    table.kr-table tbody tr:last-child td { border-bottom: none; }
    .kr-empty { padding: 32px; text-align: center; color: #5f6368; font-size: 13px; }

    @media (max-width: 980px) {
        .kr-summary { grid-template-columns: 1fr; }
        .kr-grid { grid-template-columns: 1fr; }
    }
</style>

<div class="kr-page">
    <h1 class="kr-page-title">Báo cáo bán hàng</h1>

    <div class="kr-filter-card">
        <form method="get" action="${ctx}/admin/reports" class="kr-filter-form">
            <div class="kr-filter-group">
                <label class="kr-filter-label" for="dateFrom">Từ ngày</label>
                <input type="date" id="dateFrom" name="dateFrom" class="kr-filter-input"
                       value="${dateFrom}" />
            </div>
            <div class="kr-filter-group">
                <label class="kr-filter-label" for="dateTo">Đến ngày</label>
                <input type="date" id="dateTo" name="dateTo" class="kr-filter-input"
                       value="${dateTo}" />
            </div>
            <div class="kr-filter-group">
                <button type="submit" class="kr-btn kr-btn-primary">Xem báo cáo</button>
            </div>
        </form>
    </div>

    <div class="kr-summary">
        <div class="kr-summary-card">
            <div class="kr-summary-label">Tổng doanh thu</div>
            <div class="kr-summary-value revenue">
                <fmt:formatNumber value="${report.revenue}" type="number" maxFractionDigits="0" />
                <span class="kr-summary-unit">VND</span>
            </div>
        </div>
        <div class="kr-summary-card">
            <div class="kr-summary-label">Tổng đơn hàng</div>
            <div class="kr-summary-value orders">
                <fmt:formatNumber value="${report.orderCount}" type="number" maxFractionDigits="0" />
                <span class="kr-summary-unit">đơn</span>
            </div>
        </div>
        <div class="kr-summary-card">
            <div class="kr-summary-label">Trung bình / đơn</div>
            <div class="kr-summary-value average">
                <fmt:formatNumber value="${report.averageOrderValue}" type="number" maxFractionDigits="0" />
                <span class="kr-summary-unit">VND</span>
            </div>
        </div>
    </div>

    <div class="kr-grid">
        <div class="kr-card">
            <div class="kr-card-header">
                <h2 class="kr-card-title">Doanh thu theo ngày</h2>
            </div>
            <div class="kr-table-wrap">
                <c:choose>
                    <c:when test="${empty report.revenueChart}">
                        <div class="kr-empty">Không có dữ liệu trong khoảng thời gian này.</div>
                    </c:when>
                    <c:otherwise>
                        <table class="kr-table">
                            <thead>
                                <tr>
                                    <th>Ngày</th>
                                    <th class="num">Doanh thu (VND)</th>
                                    <th class="num">Số đơn</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${report.revenueChart}">
                                    <tr>
                                        <td>${row.date}</td>
                                        <td class="num">
                                            <fmt:formatNumber value="${row.revenue}" type="number" maxFractionDigits="0" />
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${row.orderCount}" type="number" maxFractionDigits="0" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="kr-card">
            <div class="kr-card-header">
                <h2 class="kr-card-title">Top 10 sản phẩm bán chạy</h2>
            </div>
            <div class="kr-table-wrap">
                <c:choose>
                    <c:when test="${empty report.topProducts}">
                        <div class="kr-empty">Không có dữ liệu trong khoảng thời gian này.</div>
                    </c:when>
                    <c:otherwise>
                        <table class="kr-table">
                            <thead>
                                <tr>
                                    <th>Tên sản phẩm</th>
                                    <th class="num">Số lượng</th>
                                    <th class="num">Doanh thu (VND)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="product" items="${report.topProducts}">
                                    <tr>
                                        <td>${product.productName}</td>
                                        <td class="num">
                                            <fmt:formatNumber value="${product.quantity}" type="number" maxFractionDigits="0" />
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${product.revenue}" type="number" maxFractionDigits="0" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
