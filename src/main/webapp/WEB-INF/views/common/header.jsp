<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="employeeName" value="${sessionScope.employeeName}" />
<c:if test="${empty employeeName}">
    <c:set var="employeeName" value="${sessionScope.user.fullName}" />
</c:if>
<c:if test="${empty employeeName}">
    <c:set var="employeeName" value="Nhân Viên" />
</c:if>

<c:set var="trimmedName" value="${fn:trim(employeeName)}" />
<c:set var="spaceIndex" value="${fn:indexOf(trimmedName, ' ')}" />
<c:choose>
    <c:when test="${spaceIndex > 0}">
        <c:set var="firstChar" value="${fn:toUpperCase(fn:substring(trimmedName, 0, 1))}" />
        <c:set var="lastChar" value="${fn:toUpperCase(fn:substring(trimmedName, spaceIndex + 1, spaceIndex + 2))}" />
        <c:set var="initials" value="${firstChar}${lastChar}" />
    </c:when>
    <c:when test="${fn:length(trimmedName) >= 2}">
        <c:set var="initials" value="${fn:toUpperCase(fn:substring(trimmedName, 0, 2))}" />
    </c:when>
    <c:otherwise>
        <c:set var="initials" value="${fn:toUpperCase(fn:substring(trimmedName, 0, 1))}" />
    </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${not empty pageTitle}"><c:out value="${pageTitle}" /> - KiotRetail</c:when><c:otherwise>KiotRetail</c:otherwise></c:choose></title>
    <link rel="stylesheet" href="${ctx}/assets/css/kr-common.css" />
</head>
<body>

<header class="kr-topbar">
    <a href="${ctx}/home" class="logo">
        <span class="logo-mark">K</span>
        <span>KiotRetail</span>
    </a>

    <div class="right">
        <button class="icon-btn" type="button" title="Thông báo">
            <svg class="ico" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            <span class="dot"></span>
        </button>
        <div class="avatar"><c:out value="${initials}" /></div>
        <span class="user-name"><c:out value="${employeeName}" /></span>
    </div>
</header>
