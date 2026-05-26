<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Quản lý nhóm hàng"/>
</jsp:include>

<div class="d-flex">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="active" value="categories"/>
    </jsp:include>

    <div class="main-content flex-grow-1">
        <div class="category-action-bar">
            <button class="btn btn-outline-secondary btn-action" onclick="window.print()">
                <span class="material-icons">print</span>
                In danh sách
            </button>
            <c:if test="${sessionScope.canManageCategory}">
                <button class="btn btn-danger btn-action" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
                    <span class="material-icons">add</span>
                    Thêm nhóm hàng
                </button>
            </c:if>
        </div>

        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert">
                <span class="material-icons">${sessionScope.messageType == 'success' ? 'check_circle' : 'error'}</span>
                <span>${sessionScope.message}</span>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="message" scope="session"/>
            <c:remove var="messageType" scope="session"/>
        </c:if>

        <div class="card category-filter-card">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/admin/categories" class="row g-3 align-items-end">
                    <div class="col-md-4">
                        <label class="form-label">Tìm kiếm</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <span class="material-icons">search</span>
                            </span>
                            <input type="text" class="form-control" name="keyword" placeholder="Tên nhóm hoặc mô tả" value="${keyword}">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Trạng thái</label>
                        <select class="form-select" name="status">
                            <option value="" ${empty selectedStatus ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="active" ${selectedStatus == 'active' ? 'selected' : ''}>Đang sử dụng</option>
                            <option value="inactive" ${selectedStatus == 'inactive' ? 'selected' : ''}>Ngừng sử dụng</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Nhóm cha</label>
                        <input type="text" class="form-control" name="parentName" value="${parentNameFilter}" placeholder="Nhập tên nhóm cha">
                    </div>
                    <div class="col-md-2 d-flex gap-2">
                        <button type="submit" class="btn btn-danger flex-fill btn-filter">
                            <span class="material-icons">filter_alt</span>
                            Lọc
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-outline-secondary btn-icon btn-filter" title="Làm mới">
                            <span class="material-icons">refresh</span>
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <div class="card category-table-card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th style="width: 90px; white-space: nowrap;">Mã</th>
                                <th>Tên nhóm hàng</th>
                                <th>Nhóm cha</th>
                                <th>Mô tả</th>
                                <th style="width: 120px; text-align: center;">Sản phẩm</th>
                                <th style="width: 140px;">Trạng thái</th>
                                <c:if test="${sessionScope.canManageCategory}">
                                    <th style="width: 120px;">Thao tác</th>
                                </c:if>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty categories}">
                                    <tr>
                                        <td colspan="7">
                                            <div class="empty-state">
                                                <span class="material-icons">category</span>
                                                <h5>Không tìm thấy nhóm hàng</h5>
                                                <p>Thử thay đổi bộ lọc hoặc thêm nhóm hàng mới.</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="category" items="${categories}">
                                        <tr>
                                            <td><span class="fw-semibold">#${category.categoryId}</span></td>
                                            <td>
                                                <div class="fw-semibold"><c:out value="${category.name}"/></div>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty category.parentName}">
                                                        <span class="badge bg-light text-dark">Nhóm gốc</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-light text-dark"><c:out value="${category.parentName}"/></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty category.description}">
                                                        <span class="text-muted">Chưa có mô tả</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${category.description}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align: center;">
                                                <span class="badge bg-light text-dark">${category.productCount}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${category.status == 'active'}">
                                                        <span class="badge bg-success" style="color:white">Đang sử dụng</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary" style="color:white">Ngừng sử dụng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <c:if test="${sessionScope.canManageCategory}">
                                                <td>
                                                    <button type="button"
                                                            class="btn btn-sm btn-outline-primary btn-icon"
                                                            title="Sửa"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#editCategoryModal"
                                                            data-category-id="${category.categoryId}"
                                                            data-category-name="${fn:escapeXml(category.name)}"
                                                            data-category-description="${fn:escapeXml(category.description)}"
                                                        data-category-parent-name="${fn:escapeXml(category.parentName)}"
                                                            data-category-status="${category.status}"
                                                            onclick="prepareEditCategory(this)">
                                                        <span class="material-icons" style="font-size: 18px;">edit</span>
                                                    </button>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <c:if test="${totalPages > 1}">
            <div class="d-flex justify-content-between align-items-center mt-3 p-3 bg-light rounded">
                <div class="text-muted">Hiển thị 10 nhóm / trang</div>
                <nav>
                    <ul class="pagination mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/categories?page=${currentPage - 1}&keyword=${keyword}&status=${selectedStatus}&parentName=${parentNameFilter}">Trước</a>
                        </li>
                        <c:forEach var="pageIndex" begin="1" end="${totalPages}">
                            <li class="page-item ${currentPage == pageIndex ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/categories?page=${pageIndex}&keyword=${keyword}&status=${selectedStatus}&parentName=${parentNameFilter}">
                                    ${pageIndex}
                                </a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/categories?page=${currentPage + 1}&keyword=${keyword}&status=${selectedStatus}&parentName=${parentNameFilter}">Sau</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </c:if>
    </div>
</div>

<div class="modal fade" id="addCategoryModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/categories">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Thêm nhóm hàng mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tên nhóm hàng <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" maxlength="255" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Nhóm cha</label>
                            <input type="text" class="form-control" name="parentName" placeholder="Nhập tên nhóm cha hoặc để trống = nhóm gốc">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Trạng thái</label>
                            <select class="form-select" name="status">
                                <option value="active" selected>Đang sử dụng</option>
                                <option value="inactive">Ngừng sử dụng</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Mô tả</label>
                            <textarea class="form-control" name="description" rows="3" maxlength="1000"></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-danger">
                        <span class="material-icons">save</span>
                        Lưu nhóm hàng
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="editCategoryModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/categories">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="categoryId" id="editCategoryId">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Cập nhật nhóm hàng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tên nhóm hàng <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" id="editCategoryName" maxlength="255" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Nhóm cha</label>
                            <input type="text" class="form-control" name="parentName" id="editCategoryParentName" placeholder="Nhập tên nhóm cha hoặc để trống = nhóm gốc">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Trạng thái</label>
                            <select class="form-select" name="status" id="editCategoryStatus">
                                <option value="active">Đang sử dụng</option>
                                <option value="inactive">Ngừng sử dụng</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Mô tả</label>
                            <textarea class="form-control" name="description" id="editCategoryDescription" rows="3" maxlength="1000"></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-danger">
                        <span class="material-icons">save</span>
                        Cập nhật nhóm hàng
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function prepareEditCategory(button) {
    const categoryId = button.dataset.categoryId || '';
    document.getElementById('editCategoryId').value = categoryId;
    document.getElementById('editCategoryName').value = button.dataset.categoryName || '';
    document.getElementById('editCategoryDescription').value = button.dataset.categoryDescription || '';
    document.getElementById('editCategoryStatus').value = button.dataset.categoryStatus || 'active';
    document.getElementById('editCategoryParentName').value = button.dataset.categoryParentName || '';
}
</script>

<style>
.category-filter-card {
    border: 1px solid var(--border-light);
    box-shadow: none;
}

.category-action-bar {
    position: fixed;
    top: 0;
    right: 24px;
    z-index: 10;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 12px 0;
    background: transparent;
}

.main-content {
    padding-top: 72px;
}

.btn-action {
    height: 44px;
    padding: 0 18px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.category-filter-card .form-control,
.category-filter-card .form-select,
.category-filter-card .btn-filter {
    height: 44px;
}

.category-filter-card .btn-filter {
    padding: 0 16px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.category-filter-card .btn-icon {
    width: 44px;
    min-width: 44px;
    padding: 0;
}

.category-filter-card .input-group-text {
    background: #ffffff;
    border-right: 0;
}

.category-filter-card .input-group .form-control {
    border-left: 0;
}
}

.category-table-card .table thead th {
    background: #f7f8fa;
}

.category-table-card .badge.bg-light {
    background-color: #eef2f6 !important;
    color: #1f2937;
}

.category-table-card td {
    vertical-align: middle;
}

.category-table-card td:nth-child(2) {
    font-weight: 600;
}

.category-table-card .btn-icon {
    border-radius: 10px;
}

.category-filter-card .input-group-text {
    background: #fff;
}

.category-filter-card .btn-danger {
    padding-left: 18px;
    padding-right: 18px;
}

@media (max-width: 768px) {
    .category-filter-card .btn-danger {
        width: 100%;
    }
}
</style>

<jsp:include page="../common/footer.jsp"/>
