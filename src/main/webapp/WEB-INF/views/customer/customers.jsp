<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="customers" scope="request" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />




<div class="kr-page">

    <jsp:include page="../common/toast.jsp" />

    <div class="kr-page-header">
        <h1 class="kr-page-title">Khách hàng</h1>
    </div>

    <form method="get" action="${ctx}/admin/customers" class="kr-toolbar">
        <div class="kr-search">
            <input type="text" name="keyword" value="<c:out value='${filter.keyword}'/>" placeholder="Tìm kiếm theo mã, tên, số điện thoại" />
            <button type="submit" title="Tìm kiếm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </button>
        </div>

        <a href="${ctx}/admin/customers?action=create" class="kr-btn kr-btn-primary">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Tạo mới
        </a>
    </form>

    <div class="kr-main">
        <aside class="kr-sidebar">
            <form method="get" action="${ctx}/admin/customers">
                <input type="hidden" name="keyword" value="<c:out value='${filter.keyword}'/>" />

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Giới tính</div>
                    <select class="kr-filter-select" name="gender" onchange="this.form.submit()">
                        <option value="">Tất cả</option>
                        <option value="Male" <c:if test="${filter.gender == 'Male'}">selected</c:if>>Nam</option>
                        <option value="Female" <c:if test="${filter.gender == 'Female'}">selected</c:if>>Nữ</option>
                    </select>
                </div>

                <div class="kr-filter-section">
                    <div class="kr-filter-label">Hạng thành viên</div>
                    <select class="kr-filter-select" name="membershipTier" onchange="this.form.submit()">
                        <option value="">Tất cả</option>
                        <option value="member" <c:if test="${filter.membershipTier == 'member'}">selected</c:if>>Member</option>
                        <option value="silver" <c:if test="${filter.membershipTier == 'silver'}">selected</c:if>>Silver</option>
                        <option value="gold" <c:if test="${filter.membershipTier == 'gold'}">selected</c:if>>Gold</option>
                        <option value="platinum" <c:if test="${filter.membershipTier == 'platinum'}">selected</c:if>>Platinum</option>
                        <option value="diamond" <c:if test="${filter.membershipTier == 'diamond'}">selected</c:if>>Diamond</option>
                    </select>
                </div>
            </form>
        </aside>

        <section class="kr-content">
        <div class="kr-table-wrap">
            <table class="kr-table">
                <thead>
                    <tr>
                        <th class="kr-col-code">Mã KH</th>
                        <th>Họ tên</th>
                        <th>Điện thoại</th>
                        <th>Email</th>
                        <th>Giới tính</th>
                        <th>Hang</th>
                        <th class="kr-col-num">Điểm</th>
                        <th class="kr-col-time">Ngày tạo</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty pageResult.items}">
                            <tr>
                                <td colspan="8" class="kr-empty">Không có khách hàng nào.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="customer" items="${pageResult.items}">
                                <tr>
                                    <td>
                                        <a class="kr-code-link" href="${ctx}/admin/customers?action=edit&id=${customer.customerId}">
                                            KH<fmt:formatNumber value="${customer.customerId}" pattern="00000" />
                                        </a>
                                    </td>
                                    <td><c:out value="${customer.fullName}" /></td>
                                    <td><c:out value="${customer.phone}" /></td>
                                    <td><c:out value="${customer.email}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${customer.gender == 'Male' || customer.gender == 'M'}">Nam</c:when>
                                            <c:when test="${customer.gender == 'Female' || customer.gender == 'F'}">Nữ</c:when>
                                            <c:otherwise><c:out value="${customer.gender}" /></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${not empty customer.membershipTier}">
                                            <c:set var="tierLower" value="${fn:toLowerCase(customer.membershipTier)}" />
                                            <span class="kr-tier ${tierLower}">
                                                <c:out value="${customer.membershipTier}" />
                                            </span>
                                        </c:if>
                                    </td>
                                    <td class="num">
                                        <fmt:formatNumber value="${customer.points}" type="number" maxFractionDigits="0" />
                                    </td>
                                    <td class="kr-col-time">
                                        <c:if test="${not empty customer.createdAt}">
                                            <fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy HH:mm" />
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
            <c:url var="baseUrl" value="/admin/customers" scope="request">
    <c:param name="keyword" value="${filter.keyword}"/>
    <c:param name="gender" value="${filter.gender}"/>
    <c:param name="membershipTier" value="${filter.membershipTier}"/>
</c:url>
            <jsp:include page="../common/pagination.jsp" />
        </div>
    </section>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
