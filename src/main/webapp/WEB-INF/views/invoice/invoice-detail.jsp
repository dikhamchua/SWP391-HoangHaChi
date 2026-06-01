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
        <div>
            <div style="font-size:13px;color:#6b7280;margin-bottom:4px;">
                <a href="${ctx}/admin/invoices" style="color:#2563eb;text-decoration:none;">Đơn hàng</a>
                <span style="margin:0 6px;">/</span>
                <span>Chi tiết đơn hàng</span>
            </div>
            <h1 class="kr-page-title">Chi tiết đơn hàng <c:out value="${order.orderCode}" /></h1>
        </div>
        <div style="display:flex;gap:8px;">
            <c:if test="${order.status == 'pending'}">
                <form method="post" action="${ctx}/admin/invoices" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn hủy đơn hàng này?');">
                    <input type="hidden" name="action" value="cancel" />
                    <input type="hidden" name="orderId" value="${order.orderId}" />
                    <button type="submit" class="kr-btn" style="background:#ef4444;color:#fff;border-color:#ef4444;">Hủy đơn</button>
                </form>
            </c:if>
            <a href="${ctx}/admin/invoices?action=print&id=${order.orderId}" target="_blank" class="kr-btn" style="background:#10b981;color:#fff;border-color:#10b981;">In hóa đơn</a>
            <a href="${ctx}/admin/invoices" class="kr-btn">Quay lại</a>
        </div>
    </div>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <div class="kr-filter-section">
                <div class="kr-filter-label">Điều hướng</div>
                <ul style="list-style:none;padding:0;margin:0;">
                    <li style="margin-bottom:6px;">
                        <a href="${ctx}/admin/invoices" style="display:block;padding:8px 12px;border-radius:6px;text-decoration:none;color:#374151;background:#f3f4f6;">Danh sách đơn hàng</a>
                    </li>
                    <li>
                        <a href="#" style="display:block;padding:8px 12px;border-radius:6px;text-decoration:none;color:#fff;background:#2563eb;font-weight:600;">Chi tiết đơn hàng</a>
                    </li>
                </ul>
            </div>
        </aside>

        <section class="kr-content" style="padding:24px;">

            <!-- Order Info Card -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Thông tin đơn hàng</h3>
                <table class="kr-table" style="font-size:14px;width:100%;">
                    <tbody>
                        <tr>
                            <td style="font-weight:600;width:180px;">Mã đơn hàng</td>
                            <td><c:out value="${order.orderCode}" /></td>
                            <td style="font-weight:600;width:160px;">Loại đơn</td>
                            <td><c:out value="${order.orderType}" /></td>
                        </tr>
                        <tr>
                            <td style="font-weight:600;">Trạng thái</td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 'pending'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;font-size:12px;font-weight:600;background:#fef3c7;color:#d97706;">Đang xử lý</span>
                                    </c:when>
                                    <c:when test="${order.status == 'completed'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;font-size:12px;font-weight:600;background:#d1fae5;color:#059669;">Hoàn thành</span>
                                    </c:when>
                                    <c:when test="${order.status == 'cancelled'}">
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;font-size:12px;font-weight:600;background:#fee2e2;color:#dc2626;">Đã hủy</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="display:inline-block;padding:4px 10px;border-radius:12px;font-size:12px;font-weight:600;background:#e5e7eb;color:#374151;"><c:out value="${order.status}" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td style="font-weight:600;">Ngày tạo</td>
                            <td>
                                <c:if test="${not empty order.createdAt}">
                                    <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td style="font-weight:600;">Khách hàng</td>
                            <td><c:out value="${order.customerName}" /></td>
                            <td style="font-weight:600;">Nhân viên</td>
                            <td><c:out value="${order.employeeName}" /></td>
                        </tr>
                        <tr>
                            <td style="font-weight:600;">Chi nhánh</td>
                            <td colspan="3"><c:out value="${order.branchName}" /></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Order Details Table -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Sản phẩm</h3>
                <div class="kr-table-wrap">
                    <table class="kr-table" style="width:100%;">
                        <thead>
                            <tr>
                                <th style="width:50px;text-align:center;">#</th>
                                <th style="width:140px;">SKU</th>
                                <th>Tên sản phẩm</th>
                                <th class="kr-col-num" style="width:100px;">Số lượng</th>
                                <th class="kr-col-num" style="width:140px;">Đơn giá</th>
                                <th class="kr-col-num" style="width:160px;">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty orderDetails}">
                                    <tr><td colspan="6" class="kr-empty">Không có sản phẩm nào.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${orderDetails}" varStatus="loop">
                                        <tr>
                                            <td style="text-align:center;">${loop.index + 1}</td>
                                            <td><c:out value="${item.productSku}" /></td>
                                            <td><c:out value="${item.productName}" /></td>
                                            <td class="num"><fmt:formatNumber value="${item.quantity}" type="number" groupingUsed="true" /></td>
                                            <td class="num"><fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true" /></td>
                                            <td class="num"><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true" /></td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Order Summary -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Tổng kết</h3>
                <table style="width:100%;max-width:420px;margin-left:auto;font-size:14px;">
                    <tbody>
                        <tr>
                            <td style="padding:6px 0;color:#6b7280;">Tạm tính</td>
                            <td style="padding:6px 0;text-align:right;"><fmt:formatNumber value="${order.subtotal}" type="number" groupingUsed="true" /></td>
                        </tr>
                        <tr>
                            <td style="padding:6px 0;color:#6b7280;">Giảm giá</td>
                            <td style="padding:6px 0;text-align:right;color:#dc2626;">- <fmt:formatNumber value="${order.discountAmount}" type="number" groupingUsed="true" /></td>
                        </tr>
                        <tr style="border-top:1px solid #e5e7eb;">
                            <td style="padding:10px 0 0;font-weight:700;font-size:16px;">Tổng cộng</td>
                            <td style="padding:10px 0 0;text-align:right;font-weight:700;font-size:16px;color:#111827;"><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true" /></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Payments Table -->
            <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;margin-bottom:20px;">
                <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Lịch sử thanh toán</h3>
                <div class="kr-table-wrap">
                    <table class="kr-table" style="width:100%;">
                        <thead>
                            <tr>
                                <th style="width:50px;text-align:center;">#</th>
                                <th>Phương thức</th>
                                <th class="kr-col-num" style="width:160px;">Số tiền</th>
                                <th class="kr-col-time" style="width:170px;">Thời gian</th>
                                <th style="width:140px;">Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty payments}">
                                    <tr><td colspan="5" class="kr-empty">Chưa có thanh toán nào.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="pay" items="${payments}" varStatus="loop">
                                        <tr>
                                            <td style="text-align:center;">${loop.index + 1}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${pay.paymentMethod == 'cash'}">Tiền mặt</c:when>
                                                    <c:when test="${pay.paymentMethod == 'transfer'}">Chuyển khoản</c:when>
                                                    <c:when test="${pay.paymentMethod == 'card'}">Thẻ</c:when>
                                                    <c:otherwise><c:out value="${pay.paymentMethod}" /></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="num"><fmt:formatNumber value="${pay.amount}" type="number" groupingUsed="true" /></td>
                                            <td class="kr-col-time">
                                                <c:if test="${not empty pay.paidAt}">
                                                    <fmt:formatDate value="${pay.paidAt}" pattern="dd/MM/yyyy HH:mm" />
                                                </c:if>
                                            </td>
                                            <td><c:out value="${pay.status}" /></td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Add Payment Form -->
            <c:if test="${order.status != 'cancelled'}">
                <div style="background:#fff;border:1px solid #e5e7eb;border-radius:8px;padding:20px;">
                    <h3 style="margin:0 0 16px;font-size:16px;font-weight:600;color:#111827;">Thêm thanh toán</h3>
                    <form method="post" action="${ctx}/admin/invoices" style="display:flex;gap:12px;align-items:flex-end;flex-wrap:wrap;">
                        <input type="hidden" name="action" value="addPayment" />
                        <input type="hidden" name="orderId" value="${order.orderId}" />
                        <div style="flex:1;min-width:200px;">
                            <label style="display:block;font-size:13px;font-weight:600;margin-bottom:6px;color:#374151;">Phương thức</label>
                            <select name="paymentMethod" class="kr-filter-select" style="width:100%;" required>
                                <option value="cash">Tiền mặt</option>
                                <option value="transfer">Chuyển khoản</option>
                                <option value="card">Thẻ</option>
                            </select>
                        </div>
                        <div style="flex:1;min-width:200px;">
                            <label style="display:block;font-size:13px;font-weight:600;margin-bottom:6px;color:#374151;">Số tiền</label>
                            <input type="number" name="amount" class="kr-filter-input" style="width:100%;" min="0" step="1000" required />
                        </div>
                        <button type="submit" class="kr-btn kr-btn-primary">Thêm thanh toán</button>
                    </form>
                </div>
            </c:if>

        </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
