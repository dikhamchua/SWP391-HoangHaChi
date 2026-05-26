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
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}">
                                    <c:out value="${category.name}"/>
                                </option>
                            </c:forEach>
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
                        <tbody id="productTableBody">
                            <!-- JavaScript sẽ tự động vẽ các dòng TR sản phẩm vào đây -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

<!-- Pagination mới động 100% -->
<div class="d-flex justify-content-between align-items-center mt-3 p-3 bg-light rounded">
    <div class="text-muted d-flex align-items-center gap-2">
        <span>Hiển thị tối đa:</span>
        <select class="form-select form-select-sm" id="limitSelect" style="width: 80px;" onchange="loadProducts(1)">
            <option value="1">1</option>
            <option value="5">5</option>
            <option value="10" selected>10</option>
            <option value="20">20</option>
            <option value="50">50</option>
        </select>
        <span id="totalItemsInfo"></span>
    </div>
    <nav>
        <ul class="pagination mb-0" id="paginationNodes">
            <!-- Nút bấm chuyển trang sẽ tự sinh ra ở đây -->
        </ul>
    </nav>
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
                            <label class="form-label fw-semibold">SKU <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="sku" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tên hàng hóa <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Nhóm hàng</label>
                            <select class="form-select" name="categoryId" required>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}">
                                        <c:out value="${category.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Trạng thái</label>
                            <select class="form-select" name="status">
                                <option value="active" selected>Đang sử dụng</option>
                                <option value="inactive">Ngừng sử dụng</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Giá vốn</label>
                            <input type="number" class="form-control" name="costPrice" value="0" step="1000" min="0">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Giá bán <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" name="price" required step="1000" min="0">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tồn kho cảnh báo</label>
                            <input type="number" class="form-control" name="stockAlertQty" value="0" min="0">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Mô tả</label>
                            <textarea class="form-control" name="description" rows="2" placeholder="Mô tả ngắn"></textarea>
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
let currentPage = 1;

// Tự động chạy khi trang web vừa tải xong
document.addEventListener("DOMContentLoaded", function() {
    loadProducts(1);

    // Lắng nghe sự kiện gõ phím Enter trên thanh tìm kiếm
    document.getElementById('searchInput').addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            loadProducts(1); // Tìm kiếm thì reset về trang 1
        }
    });

    // Lắng nghe sự kiện thay đổi bộ lọc nhóm hàng và trạng thái
    document.getElementById('categoryFilter').addEventListener('change', function() { loadProducts(1); });
    document.getElementById('statusFilter').addEventListener('change', function() { loadProducts(1); });
});

// HÀM CHÍNH: Gọi API lấy dữ liệu và render giao diện
function loadProducts(page) {
    currentPage = page;

    // 1. Thu thập các tham số đầu vào từ giao diện
    const limit = document.getElementById('limitSelect').value;
    const keyword = document.getElementById('searchInput').value.trim();
    const categoryId = document.getElementById('categoryFilter').value;
    const status = document.getElementById('statusFilter').value;

    // 2. Tạo đường link API động (gọi đến GetProductsAction của bạn)
    let apiUrl = `${pageContext.request.contextPath}/api/products?page=\${page}&limit=\${limit}`;

    // Nếu có tìm kiếm hoặc lọc thì nối đuôi vào API (Phục vụ mở rộng sau này)
    if (keyword) apiUrl += `&keyword=\${encodeURIComponent(keyword)}`;
    if (categoryId) apiUrl += `&categoryId=\${categoryId}`;
    if (status) apiUrl += `&status=\${status}`;

    // 3. Thực hiện Fetch API ngầm
    fetch(apiUrl)
        .then(response => response.json())
        .then(apiResult => {
            if (apiResult.status === 200) {
                const products = apiResult.data;
                renderTable(products);
                renderPagination(page, limit, products.length);
            } else {
                alert("Lỗi từ máy chủ: " + apiResult.message);
            }
        })
        .catch(error => {
            console.error("Lỗi kết nối API:", error);
        });
}

// Hàm vẽ bảng dữ liệu sản phẩm từ mảng JSON nhận được
function renderTable(products) {
    const tbody = document.getElementById('productTableBody');
    let html = "";

    if (!products || products.length === 0) {
        html = `
            <tr>
                <td colspan="8">
                    <div class="text-center py-5 text-muted">
                        <span class="material-icons" style="font-size: 48px;">inventory_2</span>
                        <h5>Không tìm thấy sản phẩm nào phù hợp</h5>
                    </div>
                </td>
            </tr>`;
        tbody.innerHTML = html;
        return;
    }

    products.forEach(product => {
        // Xử lý ảnh sản phẩm
        const imgHtml = product.imageUrl
            ? `<img src="${pageContext.request.contextPath}\${product.imageUrl}" alt="\${product.productName}" style="width: 40px; height: 40px; object-fit: cover; border-radius: 6px;">`
            : `<div class="bg-light d-flex align-items-center justify-content-center" style="width: 40px; height: 40px; border-radius: 6px;"><span class="material-icons text-muted">image</span></div>`;

        // Định dạng tiền tệ VND kiểu JavaScript
        const priceFormatted = new Intl.NumberFormat('vi-VN').format(product.sellingPrice) + "₫";

        // Định dạng badge tồn kho
        let stockBadge = `<span class="badge bg-success" style="color:white">\${product.stockQuantity}</span>`;
        if (product.stockQuantity === 0) {
            stockBadge = `<span class="badge bg-danger" style="color:white">Hết hàng</span>`;
        } else if (product.stockQuantity < product.minStock) {
            stockBadge = `<span class="badge bg-warning text-dark">\${product.stockQuantity}</span>`;
        }

        // Định dạng badge trạng thái
        const statusBadge = product.status === 'active'
            ? `<span class="badge bg-success" style="color:white">Đang bán</span>`
            : `<span class="badge bg-danger" style="color:white">Ngừng bán</span>`;

        html += `
            <tr>
                <td><input type="checkbox" class="form-check-input row-checkbox"></td>
                <td><span class="fw-semibold">\${product.productCode}</span></td>
                <td>
                    <div class="d-flex align-items-center gap-2">
                        \${imgHtml}
                        <div>
                            <div class="fw-semibold">\${product.productName}</div>
                            <small class="text-muted">\${product.unit || 'Chiếc'}</small>
                        </div>
                    </div>
                </td>
                <td>\${product.categoryName || 'Chưa phân loại'}</td>
                <td><span class="fw-semibold text-danger">\${priceFormatted}</span></td>
                <td>\${stockBadge}</td>
                <td>\${statusBadge}</td>
                <td>
                    <div class="d-flex gap-1">
                        <button class="btn btn-sm btn-outline-primary btn-icon" onclick="editProduct(\${product.productId})" title="Sửa">
                            <span class="material-icons" style="font-size: 18px;">edit</span>
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-icon" onclick="deleteProduct(\${product.productId}, '\${product.productName}')" title="Xóa">
                            <span class="material-icons" style="font-size: 18px;">delete</span>
                        </button>
                    </div>
                </td>
            </tr>`;
    });

    tbody.innerHTML = html;
}

// Hàm vẽ các nút phân trang tiến lùi linh hoạt
function renderPagination(page, limit, currentSize) {
    document.getElementById('totalItemsInfo').innerText = `(Mục này hiển thị \${currentSize} sản phẩm)`;

    const paginUl = document.getElementById('paginationNodes');
    let html = "";

    // Nút "Trước"
    const prevDisabled = page === 1 ? 'disabled' : '';
    html += `<li class="page-item \${prevDisabled}"><a class="page-link" href="javascript:void(0)" onclick="loadProducts(\${page - 1})">Trước</a></li>`;

    // Nút số trang hiện tại linh động
    html += `<li class="page-item active"><a class="page-link" href="javascript:void(0)">\${page}</a></li>`;

    // Nút "Sau" (Nếu số lượng lấy lên bằng đúng limit thì cho bấm tiếp, nếu ít hơn limit nghĩa là đã hết sạch hàng ở trang sau)
    const nextDisabled = currentSize < limit ? 'disabled' : '';
    html += `<li class="page-item \${nextDisabled}"><a class="page-link" href="javascript:void(0)" onclick="loadProducts(\${page + 1})">Sau</a></li>`;

    paginUl.innerHTML = html;
}

// Chọn tất cả Checkbox
document.getElementById('selectAll').addEventListener('change', function() {
    const checkboxes = document.querySelectorAll('.row-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
});

// Làm mới bộ lọc
function resetFilters() {
    document.getElementById('searchInput').value = "";
    document.getElementById('categoryFilter').value = "";
    document.getElementById('statusFilter').value = "";
    document.getElementById('limitSelect').value = "10";
    loadProducts(1);
}

// Xóa sản phẩm
function deleteProduct(productId, productName) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm "' + productName + '"?')) {
        // Đoạn này giữ nguyên form submit truyền thống của bạn lên Servlet xử lý POST để xóa rất tốt!
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

function editProduct(productId) {
    alert('Chức năng chỉnh sửa sẽ được triển khai trong phiên bản tiếp theo');
}
</script>

<jsp:include page="../common/footer.jsp"/>
