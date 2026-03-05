<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.AssetCategory" %>

<%
    @SuppressWarnings("unchecked")
    List<AssetCategory> categories = (List<AssetCategory>) request.getAttribute("categories");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Quản lý danh mục tài sản | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
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
                        <i class="fas fa-tags text-primary"></i> Quản lý danh mục tài sản
                    </h1>
                    <a href="${pageContext.request.contextPath}/admin/categories/create"
                       class="btn btn-primary btn-sm">
                        <i class="fas fa-plus"></i> Tạo danh mục tài sản
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
                            <i class="fas fa-list"></i> Danh sách danh mục tài sản
                            <span class="badge badge-primary">
                                <%= (categories != null) ? categories.size() : 0 %>
                            </span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" id="assetcategoryTable" width="100%" cellspacing="0">
                                <thead class="thead-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Mã danh mục</th>
                                    <th>Tên danh mục</th>
                                    <th>Danh mục cha</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (categories == null || categories.isEmpty()) {
                                %>
                                <tr>
                                    <td colspan="6" class="text-center text-muted">
                                        <i class="fas fa-inbox fa-3x mb-3 mt-3"></i>
                                        <p>Chưa có danh mục tài sản nào</p>
                                    </td>
                                </tr>
                                <%
                                    } else {
                                        for (AssetCategory c : categories) {
                                %>
                                <tr>
                                    <td><%= c.getCategoryId() %></td>
                                    <td><code><%= c.getCategoryCode() %></code></td>
                                    <td><strong><%= c.getCategoryName() %></strong></td>
                                    <td><%= (c.getParentCategoryName() != null ? c.getParentCategoryName() : "-") %></td>
                                    <td>
                                        <% if (c.isActive()) { %>
                                            <span class="badge badge-success">Đang sử dụng</span>
                                        <% } else { %>
                                            <span class="badge badge-secondary">Ngừng sử dụng</span>
                                        <% } %>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/admin/categories/edit?id=<%= c.getCategoryId() %>"
                                           class="btn btn-sm btn-warning" title="Cập nhật">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/categories/delete"
                                              method="post" class="d-inline"
                                              onsubmit="return confirm('Bạn có chắc muốn xóa danh mục tài sản này?');">
                                            <input type="hidden" name="id" value="<%= c.getCategoryId() %>">
                                            <button type="submit" class="btn btn-sm btn-danger" title="Xóa">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
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
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
    $(document).ready(function () {
        $('#assetcategoryTable').DataTable({
            "language": {
                "lengthMenu": "Hiển thị _MENU_ danh mục mỗi trang",
                "zeroRecords": "Không tìm thấy danh mục nào",
                "info": "Trang _PAGE_ / _PAGES_",
                "infoEmpty": "Không có dữ liệu",
                "infoFiltered": "(lọc từ _MAX_ danh mục)",
                "search": "Tìm kiếm:",
                "paginate": {
                    "first": "Đầu",
                    "last": "Cuối",
                    "next": "Sau",
                    "previous": "Trước"
                }
            },
            "pageLength": 10
        });
    });
</script>

</body>
</html>
