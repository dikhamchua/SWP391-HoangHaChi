<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Quản lý hàng hóa"/>
</jsp:include>

<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="active" value="products"/>
    </jsp:include>

    <div class="main-content flex-grow-1">
        <!-- Top Bar -->
        <div class="top-bar">
            <div>
                <h4 class="mb-1 fw-bold">Danh mục hàng hóa</h4>
                <p class="text-muted mb-0">Quản lý thông tin sản phẩm và tồn kho</p>
            </div>
            <div class="d-flex gap-2">
                <button class="btn btn-outline-secondary" onclick="window.print()">
                    <span class="material-icons">print</span>
                    In danh sách
                </button>
                <button class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#addProductModal">
                    <span class="material-icons">add</span>
                    Thêm hàng hóa
                </button>
            </div>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert">
                <i class="material-icons align-middle me-2" style="font-size: 20px;">
                    ${sessionScope.messageType == 'success' ? 'check_circle' : 'error'}
                </i>
                ${sessionScope.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="message" scope="session"/>
            <c:remove var="messageType" scope="session"/>
        </c:if>

        <!-- Filters & Search -->
        <div class="card">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-4">
                        <div class="search-box">
                            <span class="material-icons">search</span>
                            <input type="text" class="form-control" id="searchInput"
                                   placeholder="Tìm theo mã, tên hàng hóa..." value="${keyword}">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" id="categoryFilter">
                            <option value="">Tất cả nhóm hàng</option>
                            <option value="1">Điện thoại & Máy tính bảng</option>
                            <option value="2">Phụ kiện công nghệ</option>
                            <option value="3">Thiết bị âm thanh</option>
                            <option value="4">Gia dụng thông minh</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" id="statusFilter">
                            <option value="">Tất cả trạng thái</option>
                            <option value="active">Đang kinh doanh</option>
                            <option value="inactive">Ngừng kinh doanh</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button class="btn btn-outline-secondary w-100" onclick="resetFilters()">
                            <span class="material-icons">refresh</span>
                            Làm mới
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Products Table -->
        <div class="card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th style="width: 50px;">
                                    <input type="checkbox" class="form-check-input" id="selectAll">
                                </th>
                                <th>Mã hàng</th>
                                <th>Tên hàng hóa</th>
                                <th>Nhóm hàng</th>
                                <th>Giá bán</th>
                                <th>Tồn kho</th>
                                <th>Trạng thái</th>
                                <th style="width: 120px;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty products}">
                                    <tr>
                                        <td colspan="8">
                                            <div class="empty-state">
                                                <span class="material-icons">inventory_2</span>
                                                <h5>Chưa có sản phẩm nào</h5>
                                                <p class="text-muted">Nhấn "Thêm hàng hóa" để bắt đầu</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td>
                                                <input type="checkbox" class="form-check-input row-checkbox">
                                            </td>
                                            <td>
                                                <span class="fw-semibold">${product.productCode}</span>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <c:choose>
                                                        <c:when test="${not empty product.imageUrl}">
                                                            <img src="${pageContext.request.contextPath}${product.imageUrl}"
                                                                 alt="${product.productName}"
                                                                 style="width: 40px; height: 40px; object-fit: cover; border-radius: 6px;">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="bg-light d-flex align-items-center justify-content-center"
                                                                 style="width: 40px; height: 40px; border-radius: 6px;">
                                                                <span class="material-icons text-muted">image</span>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <div>
                                                        <div class="fw-semibold">${product.productName}</div>
                                                        <small class="text-muted">${product.unit}</small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${product.categoryName}</td>
                                            <td>
                                                <span class="fw-semibold text-danger">
                                                    <fmt:formatNumber value="${product.sellingPrice}" type="currency"
                                                                      currencySymbol="" pattern="#,##0"/>₫
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.stockQuantity == 0}">
                                                        <span class="badge badge-danger">Hết hàng</span>
                                                    </c:when>
                                                    <c:when test="${product.stockQuantity < product.minStock}">
                                                        <span class="badge badge-warning">${product.stockQuantity}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-success">${product.stockQuantity}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.status == 'active'}">
                                                        <span class="badge badge-success">Đang bán</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-danger">Ngừng bán</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="d-flex gap-1">
                                                    <button class="btn btn-sm btn-outline-primary btn-icon"
                                                            onclick="editProduct(${product.productId})"
                                                            title="Sửa">
                                                        <span class="material-icons" style="font-size: 18px;">edit</span>
                                                    </button>
                                                    <button class="btn btn-sm btn-outline-danger btn-icon"
                                                            onclick="deleteProduct(${product.productId}, '${product.productName}')"
                                                            title="Xóa">
                                                        <span class="material-icons" style="font-size: 18px;">delete</span>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Pagination -->
        <c:if test="${not empty products}">
            <div class="d-flex justify-content-between align-items-center mt-3">
                <div class="text-muted">
                    Hiển thị <strong>${products.size()}</strong> sản phẩm
                </div>
                <nav>
                    <ul class="pagination mb-0">
                        <li class="page-item disabled">
                            <a class="page-link" href="#">Trước</a>
                        </li>
                        <li class="page-item active">
                            <a class="page-link" href="#">1</a>
                        </li>
                        <li class="page-item">
                            <a class="page-link" href="#">2</a>
                        </li>
                        <li class="page-item">
                            <a class="page-link" href="#">3</a>
                        </li>
                        <li class="page-item">
                            <a class="page-link" href="#">Sau</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </c:if>
    </div>
</div>

<!-- Add Product Modal -->
<div class="modal fade" id="addProductModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/products">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Thêm hàng hóa mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Mã hàng <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="productCode" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tên hàng hóa <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="productName" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Nhóm hàng</label>
                            <select class="form-select" name="categoryId">
                                <option value="1">Điện thoại & Máy tính bảng</option>
                                <option value="2">Phụ kiện công nghệ</option>
                                <option value="3">Thiết bị âm thanh</option>
                                <option value="4">Gia dụng thông minh</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Đơn vị tính</label>
                            <input type="text" class="form-control" name="unit" value="Chiếc">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Giá vốn</label>
                            <input type="number" class="form-control" name="costPrice" value="0" step="1000">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Giá bán <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" name="sellingPrice" required step="1000">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tồn kho ban đầu</label>
                            <input type="number" class="form-control" name="stockQuantity" value="0">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tồn kho tối thiểu</label>
                            <input type="number" class="form-control" name="minStock" value="0">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-danger">
                        <span class="material-icons">save</span>
                        Lưu hàng hóa
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
// Search functionality
document.getElementById('searchInput').addEventListener('keyup', function(e) {
    if (e.key === 'Enter') {
        const keyword = this.value.trim();
        if (keyword) {
            window.location.href = '${pageContext.request.contextPath}/admin/products?action=search&keyword=' + encodeURIComponent(keyword);
        } else {
            window.location.href = '${pageContext.request.contextPath}/admin/products';
        }
    }
});

// Select all checkboxes
document.getElementById('selectAll').addEventListener('change', function() {
    const checkboxes = document.querySelectorAll('.row-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
});

// Reset filters
function resetFilters() {
    window.location.href = '${pageContext.request.contextPath}/admin/products';
}

// Delete product
function deleteProduct(productId, productName) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm "' + productName + '"?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/admin/products';

        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'delete';

        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'productId';
        idInput.value = productId;

        form.appendChild(actionInput);
        form.appendChild(idInput);
        document.body.appendChild(form);
        form.submit();
    }
}

// Edit product (placeholder)
function editProduct(productId) {
    alert('Chức năng chỉnh sửa sẽ được triển khai trong phiên bản tiếp theo');
}
</script>

<jsp:include page="../common/footer.jsp"/>
