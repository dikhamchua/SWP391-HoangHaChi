<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="suppliers" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUẢN LÝ NHÀ CUNG CẤP</div>
            <a href="${ctx}/admin/suppliers" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 3h5v5"/><path d="M8 21H3v-5"/><path d="M21 3l-7 7"/><path d="M3 21l7-7"/></svg>
                Danh sách nhà cung cấp
            </a>
            <a href="${ctx}/admin/suppliers?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#0070f4; font-weight:600; background:#e6f1fe; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới nhà cung cấp
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; margin-bottom:16px; flex-wrap:wrap;">
                    <div>
                        <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:8px;">
                            <a href="${ctx}/admin/suppliers" style="color:#0070f4; text-decoration:none;">Nhà cung cấp</a>
                            <span style="color:#9aa0a6;">/</span>
                            <span>Thêm mới nhà cung cấp</span>
                        </div>
                        <h1 class="kr-page-title" style="margin:0;">Thêm mới nhà cung cấp</h1>
                    </div>
                    <div style="display:flex; align-items:center; gap:8px; flex-shrink:0;">
                        <button type="submit" class="kr-btn kr-btn-primary">Lưu</button>
                        <a class="kr-btn" href="${ctx}/admin/suppliers">Hủy bỏ</a>
                    </div>
                </div>

            <jsp:include page="../common/toast.jsp"/>
            <c:if test="${not empty errorMessage}">
                <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${errorMessage}" escapeXml="true"/>', 'danger'); });</script>
            </c:if>

            <form method="post" action="${ctx}/admin/suppliers" autocomplete="off">
                <input type="hidden" name="action" value="add" />

                <div style="background:#fff; border-radius:10px; border:1px solid #e8eaed; overflow:hidden; margin-bottom:16px;">
                    <div style="display:flex; align-items:flex-end; gap:8px; padding:0 24px; border-bottom:1px solid #e8eaed; background:#f8fafc;">
                        <span style="font-size:15px; font-weight:700; color:#0070f4; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#fff; border:1px solid #e8eaed; border-radius:8px 8px 0 0;">Thông tin chung</span>
                    </div>
                    <div style="padding:24px;">
                    <div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(260px, 1fr)); gap:20px 16px;">
                        <div style="display:flex; flex-direction:column; gap:8px;">
                            <label style="font-size:13px; font-weight:600;">Tên nhà cung cấp <span style="color:#ef4444;">*</span></label>
                            <input type="text" name="name" required maxlength="255" placeholder="Nhập tên nhà cung cấp" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:8px;">
                            <label style="font-size:13px; font-weight:600;">Số điện thoại</label>
                            <input type="text" name="phone" maxlength="30" placeholder="Nhập số điện thoại" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:8px;">
                            <label style="font-size:13px; font-weight:600;">Email</label>
                            <input type="email" name="email" maxlength="100" placeholder="Nhập email" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:8px;">
                            <label style="font-size:13px; font-weight:600;">Trạng thái</label>
                            <select name="status" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                <option value="active" selected>Hoạt động</option>
                                <option value="inactive">Ngừng</option>
                            </select>
                        </div>
                        <div style="display:flex; flex-direction:column; gap:8px; grid-column:1/-1;">
                            <label style="font-size:13px; font-weight:600;">Địa chỉ</label>
                            <input type="text" name="address" maxlength="255" placeholder="Nhập địa chỉ" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                        </div>
                    </div>
                    </div>
                </div>

            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
