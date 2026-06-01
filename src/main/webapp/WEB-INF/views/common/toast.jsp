<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty sessionScope.flashMessage}">
    <c:set var="_toastMsg" value="${sessionScope.flashMessage}" />
    <c:set var="_toastType" value="${sessionScope.messageType}" />
    <c:remove var="flashMessage" scope="session" />
    <c:remove var="messageType" scope="session" />
    <script>
        (function _waitToast() {
            if (typeof showToast === 'function') {
                showToast('<c:out value="${_toastMsg}" escapeXml="true"/>', '${not empty _toastType ? _toastType : "success"}');
            } else {
                setTimeout(_waitToast, 50);
            }
        })();
    </script>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
    <c:set var="_toastErr" value="${sessionScope.flashError}" />
    <c:remove var="flashError" scope="session" />
    <script>
        (function _waitToastErr() {
            if (typeof showToast === 'function') {
                showToast('<c:out value="${_toastErr}" escapeXml="true"/>', 'danger');
            } else {
                setTimeout(_waitToastErr, 50);
            }
        })();
    </script>
</c:if>
