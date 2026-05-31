<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="branches" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUẢN LÝ CHI NHÁNH</div>
            <a href="${ctx}/admin/branches" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                Danh sách chi nhánh
            </a>
            <a href="${ctx}/admin/branches?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới chi nhánh
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:12px;">
                <a href="${ctx}/admin/branches" style="color:#0070f4; text-decoration:none;">Chi nhánh</a>
                <span style="color:#9aa0a6;">/</span>
                <span>Chỉnh sửa chi nhánh</span>
            </div>
            <h1 class="kr-page-title" style="margin-bottom:16px;">Chỉnh sửa chi nhánh</h1>

            <jsp:include page="../common/toast.jsp"/>
            <c:if test="${not empty errorMessage}">
                <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${errorMessage}" escapeXml="true"/>', 'danger'); });</script>
            </c:if>

            <form method="post" action="${ctx}/admin/branches" autocomplete="off">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="branchId" value="${branch.branchId}" />

                <div style="background:#fff; border-radius:8px; border:1px solid #e8eaed; padding:24px; margin-bottom:16px;">
                    <div style="font-size:16px; font-weight:700; margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #e8eaed;">Thông tin chi nhánh</div>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Tên chi nhánh <span style="color:#ef4444;">*</span></label>
                            <input type="text" name="name" required maxlength="255" value="<c:out value='${branch.name}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Số điện thoại</label>
                            <input type="text" name="phone" maxlength="30" value="<c:out value='${branch.phone}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px; grid-column:1/-1;">
                            <label style="font-size:13px; font-weight:600;">Địa chỉ</label>
                            <input type="text" name="address" maxlength="255" value="<c:out value='${branch.address}'/>" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label style="font-size:13px; font-weight:600;">Trạng thái</label>
                            <select name="status" style="width:100%; height:36px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff;">
                                <option value="active" <c:if test="${branch.status == 'active'}">selected</c:if>>Hoạt động</option>
                                <option value="inactive" <c:if test="${branch.status == 'inactive'}">selected</c:if>>Ngừng</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div style="display:flex; align-items:center; gap:12px; padding-top:16px;">
                    <button type="submit" class="kr-btn kr-btn-primary">Lưu thay đổi</button>
                    <a class="kr-btn" href="${ctx}/admin/branches">Hủy bỏ</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
