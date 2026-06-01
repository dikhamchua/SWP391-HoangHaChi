<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${product.productName}"/> - Chi tiết hàng hóa</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #f3f4f6; color: #1f2937; }
        .container { max-width: 960px; margin: 24px auto; padding: 0 16px; }
        .back-link { display: inline-flex; align-items: center; gap: 6px; color: #2563eb; text-decoration: none; font-size: 14px; margin-bottom: 16px; }
        .back-link:hover { text-decoration: underline; }
        .page-title { font-size: 24px; font-weight: 700; margin: 0 0 4px 0; color: #111827; }
        .page-subtitle { color: #6b7280; font-size: 14px; margin: 0 0 20px 0; }
        .card { background: #ffffff; border: 1px solid #e5e7eb; border-radius: 8px; padding: 24px; box-shadow: 0 1px 2px rgba(0,0,0,0.04); }
        .info-grid { display: grid; grid-template-columns: 200px 1fr; gap: 12px 24px; }
        .info-label { color: #6b7280; font-size: 14px; padding: 8px 0; border-bottom: 1px solid #f3f4f6; }
        .info-value { color: #111827; font-size: 14px; font-weight: 500; padding: 8px 0; border-bottom: 1px solid #f3f4f6; }
        .price { color: #dc2626; font-weight: 700; }
        .cost-price { color: #059669; font-weight: 600; }
        .badge { display: inline-block; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
        .badge-active { background: #d1fae5; color: #065f46; }
        .badge-inactive { background: #fee2e2; color: #991b1b; }
        .badge-stock-low { background: #fef3c7; color: #92400e; }
        .badge-stock-ok { background: #dbeafe; color: #1e40af; }
        .actions { display: flex; gap: 8px; margin-top: 24px; padding-top: 20px; border-top: 1px solid #e5e7eb; justify-content: flex-end; }
        .btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 16px; font-size: 14px; font-weight: 500; border-radius: 6px; border: none; cursor: pointer; text-decoration: none; transition: all 0.15s; }
        .btn-primary { background: #2563eb; color: #ffffff; }
        .btn-primary:hover { background: #1d4ed8; }
        .btn-danger { background: #dc2626; color: #ffffff; }
        .btn-danger:hover { background: #b91c1c; }
        .btn-secondary { background: #ffffff; color: #374151; border: 1px solid #d1d5db; }
        .btn-secondary:hover { background: #f9fafb; }
        .delete-form { display: inline; }
    </style>
</head>
<body>

<jsp:include page="../common/header.jsp"/>
<c:set var="activeTab" value="products" scope="request"/>
<jsp:include page="../common/navbar.jsp"/>

<div class="container">
    <a href="${pageContext.request.contextPath}/admin/products" class="back-link">
        &larr; Quay lại danh sách hàng hóa
    </a>

    <h1 class="page-title"><c:out value="${product.productName}"/></h1>
    <p class="page-subtitle">Thông tin chi tiết hàng hóa</p>

    <div class="card">
        <div class="info-grid">
            <div class="info-label">Mã hàng</div>
            <div class="info-value"><c:out value="${product.sku}"/></div>

            <div class="info-label">Tên hàng hóa</div>
            <div class="info-value"><c:out value="${product.productName}"/></div>

            <div class="info-label">Nhóm hàng</div>
            <div class="info-value">
                <c:choose>
                    <c:when test="${not empty product.categoryName}">
                        <c:out value="${product.categoryName}"/>
                    </c:when>
                    <c:otherwise>
                        <span style="color:#9ca3af;">Chưa phân loại</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="info-label">Giá bán</div>
            <div class="info-value">
                <span class="price">
                    <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> d
                </span>
            </div>

            <div class="info-label">Giá vốn</div>
            <div class="info-value">
                <span class="cost-price">
                    <fmt:formatNumber value="${product.costPrice}" type="number" groupingUsed="true"/> d
                </span>
            </div>

            <div class="info-label">Tồn kho cảnh báo</div>
            <div class="info-value">
                <c:choose>
                    <c:when test="${product.stockAlertQty > 0}">
                        <span class="badge badge-stock-low">${product.stockAlertQty}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-stock-ok">${product.stockAlertQty}</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="info-label">Trạng thái</div>
            <div class="info-value">
                <c:choose>
                    <c:when test="${product.status == 'active'}">
                        <span class="badge badge-active">Đang kinh doanh</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-inactive">Ngừng kinh doanh</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="info-label">Ngày tạo</div>
            <div class="info-value">
                <c:choose>
                    <c:when test="${not empty product.createdAt}">
                        <fmt:formatDate value="${product.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                    </c:when>
                    <c:otherwise>
                        <span style="color:#9ca3af;">--</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">
                Quay lại
            </a>
            <a href="${pageContext.request.contextPath}/admin/products?action=edit&id=${product.productId}" class="btn btn-primary">
                Chỉnh sửa
            </a>
            <form method="post" action="${pageContext.request.contextPath}/admin/products" class="delete-form"
                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa hàng hóa này?');">
                <input type="hidden" name="action" value="delete"/>
                <input type="hidden" name="productId" value="${product.productId}"/>
                <button type="submit" class="btn btn-danger">Xóa</button>
            </form>
        </div>
    </div>
</div>

</body>
</html>
