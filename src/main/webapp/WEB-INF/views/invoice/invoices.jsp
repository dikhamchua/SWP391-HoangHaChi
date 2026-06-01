<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="invoices" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />


<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Hóa đơn</h1>
    </div>

    <form method="get" action="${ctx}/admin/invoices" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${filter.keyword}'/>" placeholder="Tìm theo mã hóa đơn, khách hàng" />
            <button type="submit" title="Tìm kiếm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <form method="get" action="${ctx}/admin/invoices">
                <input type="hidden" name="keyword" value="<c:out value='${filter.keyword}'/>" />

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Trạng thái</div>
                    <select class="kr-filter-select" name="status" onchange="this.form.submit()">
                        <option value="">Tất cả</option>
                        <option value="pending" <c:if test="${filter.status == 'pending'}">selected</c:if>>Đang xử lý</option>
                        <option value="completed" <c:if test="${filter.status == 'completed'}">selected</c:if>>Hoàn thành</option>
                        <option value="cancelled" <c:if test="${filter.status == 'cancelled'}">selected</c:if>>Đã hủy</option>
                    </select>
                </div>

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Từ ngày</div>
                    <input type="date" class="kr-filter-input" name="dateFrom" value="<c:out value='${filter.dateFrom}'/>" onchange="this.form.submit()" />
                </div>

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Đến ngày</div>
                    <input type="date" class="kr-filter-input" name="dateTo" value="<c:out value='${filter.dateTo}'/>" onchange="this.form.submit()" />
                </div>
            </form>
        </aside>

        <section class="kr-content">
        <div class="kr-table-wrap">
            <table class="kr-table">
                <thead>
                    <tr>
                        <th class="kr-col-code">Mã đơn</th>
                        <th>Khách hàng</th>
                        <th>Nhân viên</th>
                        <th class="kr-col-type">Loại</th>
                        <th class="kr-col-num">Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th class="kr-col-time">Ngày tạo</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pageResult.items}">
                            <tr>
                                <td colspan="7" class="kr-empty">Không có hóa đơn nào.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="invoice" items="${pageResult.items}">
                                <tr>
                                    <td>
                                        <a class="kr-code-link" href="${ctx}/admin/invoices?action=view&id=${invoice.orderId}">
                                            <c:out value="${invoice.orderCode}" />
                                        </a>
                                    </td>
                                    <td><c:out value="${invoice.customerName}" /></td>
                                    <td><c:out value="${invoice.employeeName}" /></td>
                                    <td><c:out value="${invoice.orderType}" /></td>
                                    <td class="num">
                                        <fmt:formatNumber value="${invoice.totalAmount}" type="number" maxFractionDigits="0" />
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${invoice.status == 'pending'}">
                                                <span class="kr-status pending">Đang xử lý</span>
                                            </c:when>
                                            <c:when test="${invoice.status == 'completed'}">
                                                <span class="kr-status completed">Hoàn thành</span>
                                            </c:when>
                                            <c:when test="${invoice.status == 'cancelled'}">
                                                <span class="kr-status cancelled">Đã hủy</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="kr-status default"><c:out value="${invoice.status}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="kr-col-time">
                                        <c:if test="${not empty invoice.createdAt}">
                                            <fmt:formatDate value="${invoice.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <div class="kr-pagination-wrap">
            <c:url var="baseUrl" value="/admin/invoices" scope="request">
                <c:param name="keyword" value="${filter.keyword}" />
                <c:param name="status" value="${filter.status}" />
                <c:param name="dateFrom" value="${filter.dateFrom}" />
                <c:param name="dateTo" value="${filter.dateTo}" />
            </c:url>
            <jsp:include page="../common/pagination.jsp" />
        </div>
    </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
