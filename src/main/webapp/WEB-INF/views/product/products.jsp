<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="products" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />


<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Hàng hóa</h1>
    </div>

    <form method="get" action="${ctx}/admin/products" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${filter.keyword}'/>" placeholder="Tìm kiếm theo mã, tên hàng" />
            <button type="submit" title="Tìm kiếm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>

        <input type="hidden" name="categoryId" value="${filter.categoryId}" />
        <input type="hidden" name="status" value="<c:out value='${filter.status}'/>" />

        <a href="${ctx}/admin/products?action=create" class="kr-btn kr-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Tạo mới
        </a>
        <a href="${ctx}/admin/products?action=import" class="kr-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#0070f4" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="17 8 12 3 7 8"/>
                <line x1="12" y1="3" x2="12" y2="15"/>
            </svg>
            Import file
        </a>
        <a href="${ctx}/admin/products?action=export" class="kr-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#0070f4" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            Xuất file
        </a>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <form method="get" action="${ctx}/admin/products">
                <input type="hidden" name="keyword" value="<c:out value='${filter.keyword}'/>" />

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Nhóm hàng</div>
                    <select class="kr-filter-select" name="categoryId" onchange="this.form.submit()">
                        <option value="">Tất cả nhóm hàng</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.categoryId}"
                                <c:if test="${filter.categoryId == category.categoryId}">selected</c:if>>
                                <c:out value="${category.name}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Trạng thái</div>
                    <select class="kr-filter-select" name="status" onchange="this.form.submit()">
                        <option value="">Tất cả</option>
                        <option value="active"   <c:if test="${filter.status == 'active'}">selected</c:if>>Đang kinh doanh</option>
                        <option value="inactive" <c:if test="${filter.status == 'inactive'}">selected</c:if>>Ngừng kinh doanh</option>
                    </select>
                </div>
            </form>
        </aside>

        <section class="kr-content">
            <div class="kr-table-wrap">
                <table class="kr-table">
                    <thead>
                        <tr>
                            <th class="kr-col-checkbox"><input type="checkbox" id="kr-check-all" /></th>
                            <th class="kr-col-code">Mã hàng</th>
                            <th>Tên hàng</th>
                            <th class="kr-col-num">Giá bán</th>
                            <th class="kr-col-num">Giá vốn</th>
                            <th class="kr-col-num">Tồn kho</th>
                            <th>Trạng thái</th>
                            <th class="kr-col-time">Thời gian tạo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="8" class="kr-empty">Không có sản phẩm nào.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="product" items="${pageResult.items}">
                                    <tr>
                                        <td><input type="checkbox" name="selected" value="${product.productId}" /></td>
                                        <td>
                                            <a class="kr-code-link" href="${ctx}/admin/products?action=view&id=${product.productId}">
                                                <c:out value="${product.sku}" />
                                            </a>
                                        </td>
                                        <td><c:out value="${product.productName}" /></td>
                                        <td class="num">
                                            <fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="0" />
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${product.costPrice}" type="number" maxFractionDigits="0" />
                                        </td>
                                        <td class="num">
                                            <fmt:formatNumber value="${product.stockAlertQty}" type="number" maxFractionDigits="0" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${product.status == 'active'}">
                                                    <span class="kr-status active">Đang kinh doanh</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="kr-status inactive">Ngừng kinh doanh</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="kr-col-time">
                                            <c:if test="${not empty product.createdAt}">
                                                <fmt:formatDate value="${product.createdAt}" pattern="dd/MM/yyyy HH:mm" />
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
                <c:url var="baseUrl" value="/admin/products" scope="request">
    <c:param name="keyword" value="${filter.keyword}"/>
    <c:param name="categoryId" value="${filter.categoryId}"/>
    <c:param name="status" value="${filter.status}"/>
</c:url>
                <jsp:include page="../common/pagination.jsp" />
            </div>
        </section>
    </div>
</div>

<script>
    (function () {
        var master = document.getElementById('kr-check-all');
        if (!master) return;
        master.addEventListener('change', function () {
            var boxes = document.querySelectorAll('input[name="selected"]');
            for (var i = 0; i < boxes.length; i++) boxes[i].checked = master.checked;
        });
    })();
</script>

<jsp:include page="../common/footer.jsp" />
