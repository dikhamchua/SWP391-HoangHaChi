<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="employees" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />


<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Nhân viên</h1>
    </div>

    <form method="get" action="${ctx}/admin/employees" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${filter.keyword}'/>" placeholder="Tìm theo tên, email, điện thoại" />
            <button type="submit" title="Tìm kiếm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>

        <a href="${ctx}/admin/employees?action=create" class="kr-btn kr-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Tạo mới
        </a>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <form method="get" action="${ctx}/admin/employees">
                <input type="hidden" name="keyword" value="<c:out value='${filter.keyword}'/>" />

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Vai trò</div>
                    <select class="kr-filter-select" name="roleId" onchange="this.form.submit()">
                        <option value="">Tất cả</option>
                        <c:forEach var="role" items="${roles}">
                            <option value="${role.roleId}"
                                <c:if test="${param.roleId == role.roleId}">selected</c:if>>
                                <c:out value="${role.name}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Trạng thái</div>
                    <select class="kr-filter-select" name="status" onchange="this.form.submit()">
                        <option value="">Tất cả</option>
                        <option value="active" <c:if test="${param.status == 'active'}">selected</c:if>>Đang làm việc</option>
                        <option value="inactive" <c:if test="${param.status == 'inactive'}">selected</c:if>>Nghỉ việc</option>
                    </select>
                </div>
            </form>
        </aside>

        <section class="kr-content">
        <div class="kr-table-wrap">
            <table class="kr-table">
                <thead>
                    <tr>
                        <th class="kr-col-avatar"></th>
                        <th>Họ tên</th>
                        <th>Email</th>
                        <th>Điện thoại</th>
                        <th>Vai trò</th>
                        <th>Chi nhánh</th>
                        <th>Trạng thái</th>
                        <th class="kr-col-time">Ngày tạo</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pageResult.items}">
                            <tr>
                                <td colspan="8" class="kr-empty">Không có nhân viên nào.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="emp" items="${pageResult.items}" varStatus="loop">
                                <c:set var="colorIdx" value="${(loop.index % 5) + 1}" />
                                <c:set var="initials" value="" />
                                <c:if test="${not empty emp.fullName}">
                                    <c:set var="trimmedName" value="${emp.fullName.trim()}" />
                                    <c:set var="lastSpace" value="${trimmedName.lastIndexOf(' ')}" />
                                    <c:choose>
                                        <c:when test="${lastSpace > 0}">
                                            <c:set var="initials" value="${trimmedName.substring(0, 1).toUpperCase()}${trimmedName.substring(lastSpace + 1, lastSpace + 2).toUpperCase()}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="initials" value="${trimmedName.substring(0, 1).toUpperCase()}" />
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                                <tr>
                                    <td>
                                        <span class="kr-avatar c${colorIdx}"><c:out value="${initials}" /></span>
                                    </td>
                                    <td>
                                        <a class="kr-name-link" href="${ctx}/admin/employees?action=view&id=${emp.employeeId}">
                                            <c:out value="${emp.fullName}" />
                                        </a>
                                    </td>
                                    <td><c:out value="${emp.email}" /></td>
                                    <td><c:out value="${emp.phone}" /></td>
                                    <td><c:out value="${emp.roleName}" /></td>
                                    <td><c:out value="${emp.branchName}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${emp.status == 'active'}">
                                                <span class="kr-status active">Đang làm việc</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="kr-status inactive">Đã nghỉ</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="kr-col-time">
                                        <c:if test="${not empty emp.createdAt}">
                                            <fmt:formatDate value="${emp.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <div class="kr-pagination-wrap">
            <c:url var="baseUrl" value="/admin/employees" scope="request">
    <c:param name="keyword" value="${filter.keyword}"/>
</c:url>
            <jsp:include page="../common/pagination.jsp" />
        </div>
    </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
