<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Chỉnh sửa hàng hóa"/>
</jsp:include>

<c:set var="activeTab" value="products" scope="request"/>
<jsp:include page="../common/navbar.jsp"/>

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUẢN LÝ HÀNG HÓA</div>
            <a href="${pageContext.request.contextPath}/admin/products" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
                Danh sách hàng hóa
            </a>
            <a href="${pageContext.request.contextPath}/admin/products?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới hàng hóa
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:12px;">
                <a href="${pageContext.request.contextPath}/admin/products" style="color:#0070f4; text-decoration:none;">Hàng hóa</a>
                <span style="color:#9aa0a6;">/</span>
                <span>Chỉnh sửa hàng hóa</span>
            </div>
            <h1 class="kr-page-title" style="margin-bottom:16px;">Chỉnh sửa hàng hóa</h1>

            <jsp:include page="../common/toast.jsp"/>

            <form method="POST" action="${pageContext.request.contextPath}/admin/products" autocomplete="off">
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="productId" value="${product.productId}"/>

                <div style="background:#fff; border-radius:8px; border:1px solid #e8eaed; padding:24px; margin-bottom:16px;">
                    <div style="font-size:16px; font-weight:700; margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #e8eaed;">Thông tin cơ bản</div>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Mã hàng / SKU <span style="color:#ef4444;">*</span></label>
                            <input type="text" name="sku" required maxlength="50" value="<c:out value='${product.sku}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Tên hàng hóa <span style="color:#ef4444;">*</span></label>
                            <input type="text" name="name" required maxlength="200" value="<c:out value='${product.productName}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Nhóm hàng</label>
                            <select name="categoryId" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff;">
                                <option value="">Chọn nhóm hàng</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}" <c:if test="${category.categoryId == product.categoryId}">selected</c:if>><c:out value="${category.name}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Trạng thái</label>
                            <select name="status" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff;">
                                <option value="active" <c:if test="${product.status == 'active'}">selected</c:if>>Đang kinh doanh</option>
                                <option value="inactive" <c:if test="${product.status == 'inactive'}">selected</c:if>>Ngừng kinh doanh</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div style="background:#fff; border-radius:8px; border:1px solid #e8eaed; padding:24px; margin-bottom:16px;">
                    <div style="font-size:16px; font-weight:700; margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #e8eaed;">Giá &amp; Tồn kho</div>
                    <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:16px;">
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Giá bán <span style="color:#ef4444;">*</span></label>
                            <input type="number" name="price" required min="0" step="1000" value="<c:out value='${product.price}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Giá vốn</label>
                            <input type="number" name="costPrice" min="0" step="1000" value="<c:out value='${product.costPrice}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Tồn kho cảnh báo</label>
                            <input type="number" name="stockAlertQty" min="0" value="<c:out value='${product.stockAlertQty}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                    </div>
                </div>

                <div style="display:flex; align-items:center; gap:12px; padding-top:16px;">
                    <button type="submit" class="kr-btn kr-btn-primary">Lưu thay đổi</button>
                    <a class="kr-btn" href="${pageContext.request.contextPath}/admin/products">Hủy bỏ</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>