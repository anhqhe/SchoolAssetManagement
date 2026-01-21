<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    List<String> roles = null;
    if (currentUser != null) {
        roles = currentUser.getRoles();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Change Password</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">

<div id="wrapper">

    <!-- Sidebar -->
    <%@ include file="/views/layout/sidebar.jsp" %>

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

        <div id="content">

            <!-- Topbar -->
            <%@ include file="/views/layout/topbar.jsp" %>

            <!-- Page Content -->
            <div class="container-fluid">

                <h1 class="h3 mb-4 text-gray-800">Change Password</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>

                <form method="post"
                      action="${pageContext.request.contextPath}/change-password">

                    <div class="form-group">
                        <label>Current Password</label>
                        <input type="password" name="oldPassword"
                               class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label>New Password</label>
                        <input type="password" name="newPassword"
                               class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label>Confirm New Password</label>
                        <input type="password" name="confirmPassword"
                               class="form-control" required>
                    </div>

                    <button class="btn btn-primary">
                        Update Password
                    </button>
                </form>

            </div>
        </div>

        <!-- Footer -->
        <%@ include file="/views/layout/footer.jsp" %>

    </div>
</div>

<!-- JS -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

</body>
</html>
