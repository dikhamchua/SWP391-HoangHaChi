    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- jQuery (optional, for easier DOM manipulation) -->
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>

    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

    <c:if test="${not empty param.additionalJS}">
        <script src="${pageContext.request.contextPath}/assets/js/${param.additionalJS}"></script>
    </c:if>
</body>
</html>
