<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="employees" scope="request" />
<fmt:formatDate value="${employee.createdAt}" pattern="dd/MM/yyyy HH:mm" var="createdAtText" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QUẢN LÝ NHÂN VIÊN</div>
            <a href="${ctx}/admin/employees" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Danh sách nhân viên
            </a>
            <a href="${ctx}/admin/employees?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Thêm mới nhân viên
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <jsp:include page="../common/toast.jsp"/>
            <c:if test="${not empty errorMessage}">
                <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${errorMessage}" escapeXml="true"/>', 'danger'); });</script>
            </c:if>

            <style>
                .kr-tab-radio { display:none; }
                .kr-tab-panel { display:none; }
                #employee-tab-general:checked ~ .kr-tab-card .kr-tab-general,
                #employee-tab-history:checked ~ .kr-tab-card .kr-tab-history { display:block; }
                #employee-tab-general:checked ~ .kr-tab-card label[for="employee-tab-general"],
                #employee-tab-history:checked ~ .kr-tab-card label[for="employee-tab-history"] {
                    color:#0070f4; background:#fff; border-bottom-color:#fff;
                }
            </style>

            <form method="post" action="${ctx}/admin/employees" autocomplete="off">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="employeeId" value="${employee.employeeId}" />

                <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; margin-bottom:16px; flex-wrap:wrap;">
                    <div>
                        <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:8px;">
                            <a href="${ctx}/admin/employees" style="color:#0070f4; text-decoration:none;">Nhân viên</a>
                            <span style="color:#9aa0a6;">/</span>
                            <span>Chi tiết nhân viên</span>
                        </div>
                        <h1 class="kr-page-title" style="margin:0;">Chi tiết nhân viên</h1>
                    </div>
                    <div style="display:flex; align-items:center; gap:8px; flex-shrink:0;">
                        <button type="submit" class="kr-btn kr-btn-primary">Lưu</button>
                        <a class="kr-btn" href="${ctx}/admin/employees">Quay lại</a>
                    </div>
                </div>

                <input class="kr-tab-radio" type="radio" id="employee-tab-general" name="employeeTab" checked />
                <input class="kr-tab-radio" type="radio" id="employee-tab-history" name="employeeTab" />

                <div class="kr-tab-card" style="background:#fff; border:1px solid #e8eaed; border-radius:10px; overflow:hidden;">
                    <div style="display:flex; align-items:flex-end; gap:8px; padding:0 24px; border-bottom:1px solid #e8eaed; background:#f8fafc;">
                        <label for="employee-tab-general" style="font-size:15px; font-weight:700; color:#5f6368; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#f8fafc; border:1px solid #e8eaed; border-radius:8px 8px 0 0; cursor:pointer;">Thông tin chung</label>
                        <label for="employee-tab-history" style="font-size:15px; font-weight:700; color:#5f6368; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#f8fafc; border:1px solid #e8eaed; border-radius:8px 8px 0 0; cursor:pointer;">Lịch sử hoạt động</label>
                    </div>
                    <div class="kr-tab-panel kr-tab-general" style="padding:24px;">
                        <div style="display:flex; flex-direction:column; gap:24px;">
                            <section style="display:flex; flex-direction:column; align-items:center; justify-content:center; gap:10px; padding:24px; border:1px solid #eef0f2; border-radius:10px; background:#f8fafc; text-align:center;">
                                <div style="width:88px; height:88px; border-radius:50%; background:#e8f1ff; color:#0070f4; display:flex; align-items:center; justify-content:center; border:3px solid #fff; box-shadow:0 2px 8px rgba(0,0,0,.08);">
                                    <svg style="width:42px;height:42px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                </div>
                                <div style="font-size:20px; font-weight:700; color:#15171a;"><c:out value="${employee.fullName}" /></div>
                                <div style="display:flex; align-items:center; justify-content:center; gap:8px; flex-wrap:wrap;">
                                    <span style="font-size:13px; color:#5f6368; background:#fff; border:1px solid #e8eaed; border-radius:999px; padding:5px 12px;">NV${employee.employeeId}</span>
                                    <span style="font-size:13px; color:#5f6368; background:#fff; border:1px solid #e8eaed; border-radius:999px; padding:5px 12px;"><c:out value="${employee.roleName}" /></span>
                                    <c:choose>
                                        <c:when test="${employee.status == 'active'}">
                                            <span class="kr-status active">Đang làm việc</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="kr-status inactive">Đã nghỉ</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </section>

                            <section style="display:grid; grid-template-columns:repeat(auto-fit, minmax(260px, 1fr)); gap:20px 16px;">
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Mã nhân viên</label>
                                    <input type="text" value="NV${employee.employeeId}" readonly style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#f8fafc; box-sizing:border-box;"/>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Họ tên <span style="color:#ef4444;">*</span></label>
                                    <input type="text" name="fullName" required maxlength="100" value="<c:out value='${employee.fullName}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Số điện thoại <span style="color:#ef4444;">*</span></label>
                                    <input type="text" name="phone" required maxlength="20" value="<c:out value='${employee.phone}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Email <span style="color:#ef4444;">*</span></label>
                                    <input type="email" name="email" required maxlength="100" value="<c:out value='${employee.email}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Chi nhánh <span style="color:#ef4444;">*</span></label>
                                    <select name="branchId" required style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                        <option value="">-- Chọn chi nhánh --</option>
                                        <c:forEach var="branch" items="${branches}">
                                            <option value="${branch.branchId}" <c:if test="${employee.branchId == branch.branchId}">selected</c:if>><c:out value="${branch.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Vai trò <span style="color:#ef4444;">*</span></label>
                                    <select name="roleId" required style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                        <option value="">-- Chọn vai trò --</option>
                                        <c:forEach var="role" items="${roles}">
                                            <option value="${role.roleId}" <c:if test="${employee.roleId == role.roleId}">selected</c:if>><c:out value="${role.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Trạng thái</label>
                                    <select name="status" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                        <option value="active" <c:if test="${employee.status == 'active'}">selected</c:if>>Đang làm việc</option>
                                        <option value="inactive" <c:if test="${employee.status != 'active'}">selected</c:if>>Đã nghỉ</option>
                                    </select>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:8px;">
                                    <label style="font-size:13px; font-weight:600;">Ngày tạo</label>
                                    <input type="text" readonly value="<c:out value='${createdAtText}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#f8fafc; box-sizing:border-box;"/>
                                </div>
                            </section>
                        </div>
                    </div>
                    <div class="kr-tab-panel kr-tab-history" style="padding:24px;">
                        <c:choose>
                            <c:when test="${empty activities}">
                                <div style="padding:24px; border:1px dashed #dfe3e8; border-radius:10px; color:#5f6368; text-align:center; background:#f8fafc;">Chưa có lịch sử hoạt động</div>
                            </c:when>
                            <c:otherwise>
                                <div style="display:flex; flex-direction:column; gap:12px;">
                                    <c:forEach var="activity" items="${activities}">
                                        <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; padding:14px 16px; border:1px solid #e8eaed; border-radius:10px; background:#fff;">
                                            <div style="display:flex; flex-direction:column; gap:6px;">
                                                <div style="font-size:14px; font-weight:700; color:#15171a;"><c:out value="${activity.description}" /></div>
                                                <div style="font-size:12px; color:#5f6368;">Người thực hiện: <c:out value="${activity.createdBy}" default="Hệ thống" /></div>
                                            </div>
                                            <span style="font-size:12px; color:#0070f4; background:#e8f1ff; border-radius:999px; padding:5px 10px; text-transform:uppercase;"><c:out value="${activity.type}" /></span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
