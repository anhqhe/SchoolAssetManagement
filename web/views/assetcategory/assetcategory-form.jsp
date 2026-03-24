<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.AssetCategory" %>

<%
    AssetCategory category = (AssetCategory) request.getAttribute("category");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");

    boolean isEdit = (category != null && category.getCategoryId() > 0);
    String contextPath = request.getContextPath();
    String formAction = isEdit
            ? (contextPath + "/admin/categories/edit")
            : (contextPath + "/admin/categories/create");
    String pageTitle = isEdit ? "Cập nhật danh mục tài sản" : "Tạo danh mục tài sản";
    String submitLabel = isEdit ? "Cập nhật" : "Tạo mới";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%= pageTitle %> | School Asset Management</title>

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
                        <i class="fas fa-tags text-primary"></i> <%= pageTitle %>
                    </h1>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary btn-sm">
                        <i class="fas fa-arrow-left"></i> Quay lại danh sách
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

                <% if (success != null && !success.isEmpty()) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> <%= success %>
                    <button type="button" class="close" data-dismiss="alert">
                        <span>&times;</span>
                    </button>
                </div>
                <% } %>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <%= pageTitle %>
                        </h6>
                    </div>
                    <div class="card-body">
                        <form method="post" action="<%= formAction %>">
                            <% if (isEdit) { %>
                            <input type="hidden" name="id" value="<%= category.getCategoryId() %>">
                            <% } %>

                            <div class="form-group">
                                <label for="categoryCode">Mã danh mục</label>
                                <input type="text"
                                       id="categoryCode"
                                       name="categoryCode"
                                       class="form-control"
                                       value="<%= (category != null && category.getCategoryCode() != null) ? category.getCategoryCode() : "" %>"
                                       required>
                                <small class="form-text text-muted">
                                    Ví dụ: IT, FURN, LAPTOP. Mã không được trùng.
                                </small>
                            </div>

                            <div class="form-group">
                                <label for="categoryName">Tên danh mục tài sản</label>
                                <input type="text"
                                       id="categoryName"
                                       name="categoryName"
                                       class="form-control"
                                       value="<%= (category != null && category.getCategoryName() != null) ? category.getCategoryName() : "" %>"
                                       required>
                            </div>

                            <div class="form-group">
                                <label for="active">Trạng thái sử dụng</label>
                                <select id="active" name="active" class="form-control">
                                    <option value="true" <%= (category == null || category.isActive()) ? "selected" : "" %>>
                                        Đang sử dụng
                                    </option>
                                    <option value="false" <%= (category != null && !category.isActive()) ? "selected" : "" %>>
                                        Ngừng sử dụng
                                    </option>
                                </select>
                            </div>

                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> <%= submitLabel %>
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
