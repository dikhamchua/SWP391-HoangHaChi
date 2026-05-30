<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="employeeName" value="${sessionScope.employeeName}" />
<c:if test="${empty employeeName}">
    <c:set var="employeeName" value="${sessionScope.user.fullName}" />
</c:if>
<c:if test="${empty employeeName}">
    <c:set var="employeeName" value="Nhan Vien" />
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

<header style="height:48px;background:#ffffff;border-bottom:1px solid #e5e7eb;display:flex;align-items:center;justify-content:space-between;padding:0 16px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;position:sticky;top:0;z-index:100;">
    <div style="display:flex;align-items:center;gap:8px;">
        <a href="${ctx}/home" style="display:flex;align-items:center;gap:8px;text-decoration:none;">
            <span style="display:inline-flex;align-items:center;justify-content:center;width:28px;height:28px;background:#0070f4;color:#ffffff;border-radius:6px;font-weight:700;font-size:14px;letter-spacing:-0.5px;">K</span>
            <span style="color:#0070f4;font-weight:700;font-size:16px;letter-spacing:-0.3px;">KiotRetail</span>
        </a>
    </div>

    <div style="display:flex;align-items:center;gap:12px;">
        <button type="button" aria-label="Thong bao" style="position:relative;width:32px;height:32px;border:none;background:transparent;cursor:pointer;border-radius:6px;display:inline-flex;align-items:center;justify-content:center;">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#4b5563" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            <span style="position:absolute;top:4px;right:4px;width:6px;height:6px;background:#ef4444;border-radius:50%;"></span>
        </button>

        <div style="display:flex;align-items:center;gap:8px;cursor:pointer;padding:4px 8px;border-radius:6px;">
            <span style="display:inline-flex;align-items:center;justify-content:center;width:28px;height:28px;background:#0070f4;color:#ffffff;border-radius:50%;font-weight:600;font-size:12px;">
                <c:out value="${initials}" />
            </span>
            <span style="color:#1f2937;font-size:13px;font-weight:500;max-width:140px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
                <c:out value="${employeeName}" />
            </span>
        </div>
    </div>
</header>
