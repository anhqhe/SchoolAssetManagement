<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>

<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");

    @SuppressWarnings("unchecked")
    List<String> allRoles = (List<String>) request.getAttribute("allRoles");

    @SuppressWarnings("unchecked")
    List<String> selectedRoles = (List<String>) request.getAttribute("selectedRoles");

    String fUsername = request.getAttribute("f_username") != null ? (String) request.getAttribute("f_username") : "";
    String fFullName = request.getAttribute("f_fullName") != null ? (String) request.getAttribute("f_fullName") : "";
    String fEmail    = request.getAttribute("f_email") != null ? (String) request.getAttribute("f_email") : "";
    String fPhone    = request.getAttribute("f_phone") != null ? (String) request.getAttribute("f_phone") : "";
    Boolean fActive  = request.getAttribute("f_active") != null ? (Boolean) request.getAttribute("f_active") : true;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tạo tài khoản | School Asset Management</title>

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
                        <i class="fas fa-user-plus text-primary"></i> Tạo tài khoản
                    </h1>
                    <a href="${pageContext.request.contextPath}/admin/user" class="btn btn-secondary btn-sm">
                        <i class="fas fa-arrow-left"></i> Quay lại danh sách
                    </a>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> <%= error %>
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } %>

                <% if (success != null && !success.isEmpty()) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> <%= success %>
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } %>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">Thông tin tài khoản mới</h6>
                    </div>
                    <div class="card-body">

                        <form method="post" action="${pageContext.request.contextPath}/admin/user/create" autocomplete="off">

                            <div class="form-group">
                                <label for="username">Tài khoản <span class="text-danger">*</span></label>
                                <input type="text" id="username" name="username" class="form-control"
                                       value="<%= fUsername %>" required autocomplete="off">
                            </div>

                            <div class="form-group">
                                <label for="password">Mật khẩu <span class="text-danger">*</span></label>
                                <input type="password" id="password" name="password" class="form-control"
                                       required autocomplete="new-password">
                            </div>

                            <div class="form-group">
                                <label for="fullName">Họ tên <span class="text-danger">*</span></label>
                                <input type="text" id="fullName" name="fullName" class="form-control"
                                       value="<%= fFullName %>" required>
                            </div>

                            <div class="form-row">
                                <div class="form-group col-md-6">
                                    <label for="email">Email <span class="text-danger">*</span></label>
                                    <input type="email" id="email" name="email" class="form-control"
                                           value="<%= fEmail %>" required>
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="phone">Số điện thoại</label>
                                    <input type="text" id="phone" name="phone" class="form-control"
                                           value="<%= fPhone %>">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="active">Trạng thái</label>
                                <select id="active" name="active" class="form-control">
                                    <option value="true" <%= fActive ? "selected" : "" %>>Hoạt động</option>
                                    <option value="false" <%= !fActive ? "selected" : "" %>>Đã ban</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Vai trò <span class="text-danger">*</span></label>
                                <div class="border rounded p-3">
                                    <%
                                        if (allRoles == null || allRoles.isEmpty()) {
                                    %>
                                    <span class="text-muted">Không có vai trò nào.</span>
                                    <%
                                        } else {
                                            for (String rc : allRoles) {
                                                boolean checked = (selectedRoles != null && selectedRoles.contains(rc));
                                    %>
                                    <div class="custom-control custom-checkbox mb-1">
                                        <input type="checkbox" class="custom-control-input"
                                               id="role_<%= rc %>" name="roles" value="<%= rc %>"
                                               <%= checked ? "checked" : "" %>>
                                        <label class="custom-control-label" for="role_<%= rc %>"><%= rc %></label>
                                    </div>
                                    <%
                                            }
                                        }
                                    %>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Tạo tài khoản
                            </button>
                        </form>

                    </div>
                </div>

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
