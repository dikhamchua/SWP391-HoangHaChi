<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="customers" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUẢN LÝ KHÁCH HÀNG</div>
            <a href="${ctx}/admin/customers" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Danh sách khách hàng
            </a>
            <a href="${ctx}/admin/customers?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới khách hàng
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:12px;">
                <a href="${ctx}/admin/customers" style="color:#0070f4; text-decoration:none;">Khách hàng</a>
                <span style="color:#9aa0a6;">/</span>
                <span>Chỉnh sửa khách hàng</span>
            </div>
            <h1 class="kr-page-title" style="margin-bottom:16px;">Chỉnh sửa khách hàng</h1>

            <jsp:include page="../common/toast.jsp"/>
            <c:if test="${not empty errorMessage}">
                <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${errorMessage}" escapeXml="true"/>', 'danger'); });</script>
            </c:if>

            <form method="post" action="${ctx}/admin/customers" autocomplete="off">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="customerId" value="${customer.customerId}" />

                <div style="background:#fff; border-radius:8px; border:1px solid #e8eaed; padding:24px; margin-bottom:16px;">
                    <div style="font-size:16px; font-weight:700; margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #e8eaed;">Thông tin cơ bản</div>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Họ tên <span style="color:#ef4444;">*</span></label>
                            <input type="text" name="fullName" required maxlength="100" value="<c:out value='${customer.fullName}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Số điện thoại <span style="color:#ef4444;">*</span></label>
                            <input type="text" name="phone" required maxlength="20" value="<c:out value='${customer.phone}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Email</label>
                            <input type="email" name="email" maxlength="100" value="<c:out value='${customer.email}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Giới tính</label>
                            <select name="gender" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff;">
                                <option value="">-- Chọn --</option>
                                <option value="Male" <c:if test="${customer.gender == 'Male'}">selected</c:if>>Nam</option>
                                <option value="Female" <c:if test="${customer.gender == 'Female'}">selected</c:if>>Nữ</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div style="background:#fff; border-radius:8px; border:1px solid #e8eaed; padding:24px; margin-bottom:16px;">
                    <div style="font-size:16px; font-weight:700; margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #e8eaed;">Thông tin bổ sung</div>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <div style="display:flex; flex-direction:column; gap:6px; grid-column:1/-1;">
                            <label style="font-size:13px; font-weight:600;">Địa chỉ</label>
                            <input type="text" name="address" maxlength="255" value="<c:out value='${customer.address}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Ngày sinh</label>
                            <input type="date" name="dateOfBirth" value="<fmt:formatDate value='${customer.dateOfBirth}' pattern='yyyy-MM-dd' />" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Hạng thành viên</label>
                            <select name="membershipTier" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff;">
                                <option value="member" <c:if test="${customer.membershipTier == 'member'}">selected</c:if>>Member</option>
                                <option value="silver" <c:if test="${customer.membershipTier == 'silver'}">selected</c:if>>Silver</option>
                                <option value="gold" <c:if test="${customer.membershipTier == 'gold'}">selected</c:if>>Gold</option>
                                <option value="platinum" <c:if test="${customer.membershipTier == 'platinum'}">selected</c:if>>Platinum</option>
                                <option value="diamond" <c:if test="${customer.membershipTier == 'diamond'}">selected</c:if>>Diamond</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div style="display:flex; align-items:center; gap:12px; padding-top:16px;">
                    <button type="submit" class="kr-btn kr-btn-primary">Lưu thay đổi</button>
                    <a class="kr-btn" href="${ctx}/admin/customers">Hủy bỏ</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />