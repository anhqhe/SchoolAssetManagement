<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="model.Room" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    Room room = (Room) request.getAttribute("room");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Room detail | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">

<div id="wrapper">

    <%@ include file="/views/layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="/views/layout/topbar.jsp" %>

            <div class="container-fluid">

                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-door-open text-primary"></i> View Detail room
                    </h1>
                    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-secondary btn-sm">
                        <i class="fas fa-arrow-left"></i> Back to list
                    </a>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-circle"></i> <%= error %>
                        <button type="button" class="close" data-dismiss="alert">
                            <span>&times;</span>
                        </button>
                    </div>
                <% } %>

                <% if (room != null) { %>
                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">
                                Thông tin phòng #<%= room.getRoomId() %>
                            </h6>
                        </div>
                        <div class="card-body">
                            <dl class="row">
                                <dt class="col-sm-3">Room name</dt>
                                <dd class="col-sm-9"><%= room.getRoomName() %></dd>

                                <dt class="col-sm-3">Location</dt>
                                <dd class="col-sm-9"><%= room.getLocation() != null ? room.getLocation() : "-" %></dd>
                            </dl>

                            <hr>
                            <a href="${pageContext.request.contextPath}/rooms/config?id=<%= room.getRoomId() %>"
                               class="btn btn-warning">
                                <i class="fas fa-cog"></i> Config Room
                            </a>
                        </div>
                    </div>
                <% } %>

            </div>

        </div>

        <%@ include file="/views/layout/footer.jsp" %>

    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

</body>
</html>

