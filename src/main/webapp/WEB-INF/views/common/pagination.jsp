<%--
    Reusable pagination fragment.
    Required request attributes:
        pageResult : com.kiotretail.common.PageResult (page, size, totalPages, totalItems, hasNext, hasPrevious)
        baseUrl    : String  (e.g. "${pageContext.request.contextPath}/products")

    URLs are built as: ${baseUrl}?page=X&size=Y
    Existing query parameters are not preserved by this fragment; pass them via baseUrl
    if needed (e.g. baseUrl="${ctx}/products?keyword=abc").
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty pageResult}">
    <c:set var="page" value="${pageResult.page}" />
    <c:set var="size" value="${pageResult.size}" />
    <c:set var="totalPages" value="${pageResult.totalPages}" />
    <c:set var="totalItems" value="${pageResult.totalItems}" />
    <c:set var="hasNext" value="${pageResult.hasNext}" />
    <c:set var="hasPrev" value="${pageResult.hasPrevious}" />

    <%-- Compute "showing X-Y of Z" range --%>
    <c:choose>
        <c:when test="${totalItems == 0}">
            <c:set var="fromItem" value="0" />
            <c:set var="toItem" value="0" />
        </c:when>
        <c:otherwise>
            <c:set var="fromItem" value="${(page - 1) * size + 1}" />
            <c:set var="toItem" value="${page * size}" />
            <c:if test="${toItem > totalItems}">
                <c:set var="toItem" value="${totalItems}" />
            </c:if>
        </c:otherwise>
    </c:choose>

    <%-- Detect "?" already in baseUrl so we can append with & instead --%>
    <c:set var="qSep" value="${fn:contains(baseUrl, '?') ? '&' : '?'}" />

    <style>
        .kr-pagination {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 12px 0;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 14px;
            color: #333;
        }
        .kr-pagination .kr-info { color: #555; }
        .kr-pagination .kr-controls {
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .kr-pagination .kr-size {
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .kr-pagination .kr-size select {
            padding: 4px 8px;
            border: 1px solid #cfd8dc;
            border-radius: 4px;
            background: #fff;
            font-size: 14px;
        }
        .kr-pagination a.kr-link,
        .kr-pagination span.kr-link {
            display: inline-block;
            padding: 6px 12px;
            border: 1px solid #cfd8dc;
            border-radius: 4px;
            text-decoration: none;
            color: #1976d2;
            background: #fff;
            min-width: 36px;
            text-align: center;
        }
        .kr-pagination a.kr-link:hover { background: #e3f2fd; }
        .kr-pagination span.kr-link.kr-disabled {
            color: #b0bec5;
            background: #f5f5f5;
            cursor: not-allowed;
        }
        .kr-pagination .kr-current {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 999px;
            background: #1976d2;
            color: #fff;
            font-weight: 600;
            min-width: 36px;
            text-align: center;
        }
    </style>

    <div class="kr-pagination">
        <div class="kr-info">
            <c:choose>
                <c:when test="${totalItems == 0}">
                    No items to display
                </c:when>
                <c:otherwise>
                    Showing <strong>${fromItem}</strong>-<strong>${toItem}</strong>
                    of <strong>${totalItems}</strong>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="kr-controls">
            <%-- First --%>
            <c:choose>
                <c:when test="${hasPrev}">
                    <a class="kr-link" href="${baseUrl}${qSep}page=1&size=${size}" title="First page">&laquo; First</a>
                </c:when>
                <c:otherwise>
                    <span class="kr-link kr-disabled">&laquo; First</span>
                </c:otherwise>
            </c:choose>

            <%-- Prev --%>
            <c:choose>
                <c:when test="${hasPrev}">
                    <a class="kr-link" href="${baseUrl}${qSep}page=${page - 1}&size=${size}" title="Previous page">&lsaquo; Prev</a>
                </c:when>
                <c:otherwise>
                    <span class="kr-link kr-disabled">&lsaquo; Prev</span>
                </c:otherwise>
            </c:choose>

            <%-- Current page badge --%>
            <span class="kr-current">
                ${page}<c:if test="${totalPages > 0}"> / ${totalPages}</c:if>
            </span>

            <%-- Next --%>
            <c:choose>
                <c:when test="${hasNext}">
                    <a class="kr-link" href="${baseUrl}${qSep}page=${page + 1}&size=${size}" title="Next page">Next &rsaquo;</a>
                </c:when>
                <c:otherwise>
                    <span class="kr-link kr-disabled">Next &rsaquo;</span>
                </c:otherwise>
            </c:choose>

            <%-- Last --%>
            <c:choose>
                <c:when test="${hasNext}">
                    <a class="kr-link" href="${baseUrl}${qSep}page=${totalPages}&size=${size}" title="Last page">Last &raquo;</a>
                </c:when>
                <c:otherwise>
                    <span class="kr-link kr-disabled">Last &raquo;</span>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="kr-size">
            <label for="kr-page-size">Per page:</label>
            <select id="kr-page-size"
                    onchange="window.location.href='${baseUrl}${qSep}page=1&size=' + this.value;">
                <c:forEach var="opt" items="15,30,50,100">
                    <option value="${opt}" <c:if test="${size == opt}">selected</c:if>>${opt}</option>
                </c:forEach>
            </select>
        </div>
    </div>
</c:if>
