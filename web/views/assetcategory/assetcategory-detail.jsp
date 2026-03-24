<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.AssetCategory" %>
<%@ page import="model.Asset" %>

<%
    AssetCategory category = (AssetCategory) request.getAttribute("category");
    @SuppressWarnings("unchecked")
    List<Asset> assets = (List<Asset>) request.getAttribute("assets");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết danh mục tài sản | School Asset Management</title>

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
                        <i class="fas fa-tags text-primary"></i> Chi tiết danh mục tài sản
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

                <% if (category != null) { %>
                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">
                                <i class="fas fa-info-circle"></i> Thông tin danh mục
                            </h6>
                        </div>
                        <div class="card-body">
                            <div class="row mb-3">
                                <div class="col-md-3 font-weight-bold">ID:</div>
                                <div class="col-md-9"><%= category.getCategoryId() %></div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-3 font-weight-bold">Mã danh mục:</div>
                                <div class="col-md-9"><code><%= category.getCategoryCode() %></code></div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-3 font-weight-bold">Tên danh mục:</div>
                                <div class="col-md-9"><strong><%= category.getCategoryName() %></strong></div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-3 font-weight-bold">Danh mục cha:</div>
                                <div class="col-md-9"><%= category.getParentCategoryName() != null ? category.getParentCategoryName() : "-" %></div>
                            </div>
                            <div class="row">
                                <div class="col-md-3 font-weight-bold">Trạng thái:</div>
                                <div class="col-md-9">
                                    <% if (category.isActive()) { %>
                                        <span class="badge badge-success">Đang sử dụng</span>
                                    <% } else { %>
                                        <span class="badge badge-secondary">Ngừng sử dụng</span>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">
                                <i class="fas fa-boxes"></i> Tài sản thuộc danh mục này
                                <span class="badge badge-primary">
                                    <%= (assets != null) ? assets.size() : 0 %>
                                </span>
                            </h6>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover" id="categoryAssetsTable" width="100%" cellspacing="0">
                                    <thead class="thead-light">
                                        <tr>
                                            <th>Mã tài sản</th>
                                            <th>Tên tài sản</th>
                                            <th>Serial</th>
                                            <th>Model</th>
                                            <th>Brand</th>
                                            <th>Phòng hiện tại</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            if (assets == null || assets.isEmpty()) {
                                        %>
                                            <tr>
                                                <td colspan="8" class="text-center text-muted">
                                                    Chưa có tài sản nào thuộc danh mục này.
                                                </td>
                                            </tr>
                                        <%
                                            } else {
                                                for (Asset asset : assets) {
                                        %>
                                            <tr>
                                                <td><code><%= asset.getAssetCode() %></code></td>
                                                <td><strong><%= asset.getAssetName() %></strong></td>
                                                <td><%= asset.getSerialNumber() != null ? asset.getSerialNumber() : "-" %></td>
                                                <td><%= asset.getModel() != null ? asset.getModel() : "-" %></td>
                                                <td><%= asset.getBrand() != null ? asset.getBrand() : "-" %></td>
                                                <td><%= asset.getRoomName() != null ? asset.getRoomName() : "-" %></td>
                                                <td>
                                                    <span class="badge <%= asset.getStatusBadgeClass() %>">
                                                        <%= asset.getStatusText() %>
                                                    </span>
                                                </td>
                                                <td class="text-center">
                                                    <a href="${pageContext.request.contextPath}/assets/detail?id=<%= asset.getAssetId() %>"
                                                       class="btn btn-sm btn-info" title="Xem chi tiết tài sản">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
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
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
    $(document).ready(function () {
        $('#categoryAssetsTable').DataTable({
            "language": {
                "lengthMenu": "Hiển thị _MENU_ tài sản mỗi trang",
                "zeroRecords": "Không tìm thấy tài sản nào",
                "info": "Trang _PAGE_ / _PAGES_",
                "infoEmpty": "Không có dữ liệu",
                "infoFiltered": "(lọc từ _MAX_ tài sản)",
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
