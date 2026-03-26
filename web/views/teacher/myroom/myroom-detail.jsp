<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="model.Room" %>
<%@ page import="model.Asset" %>
<%@ page import="java.util.List" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    Room room = (Room) request.getAttribute("room");
    String error = (String) request.getAttribute("error");
    @SuppressWarnings("unchecked")
    List<Asset> assetsInRoom = (List<Asset>) request.getAttribute("assetsInRoom");

    int totalAssets  = (request.getAttribute("totalAssets")  != null) ? (int) request.getAttribute("totalAssets")  : 0;
    int totalPages   = (request.getAttribute("totalPages")   != null) ? (int) request.getAttribute("totalPages")   : 1;
    int currentPage  = (request.getAttribute("currentPage")  != null) ? (int) request.getAttribute("currentPage")  : 1;

    long roomId = (room != null) ? room.getRoomId() : 0;
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xem chi tiết phòng | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">

    <style>
        .page-link { color: #4e73df; }
        .page-item.active .page-link { background-color: #4e73df; border-color: #4e73df; }
        .page-item.disabled .page-link { color: #b7b9cc; }
        .asset-code { font-family: monospace; color: #e74a3b; font-size:.88rem; }
        .pagination-info { color: #858796; font-size: .85rem; }
    </style>
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
                        <i class="fas fa-door-open text-primary"></i> Xem chi tiết phòng
                    </h1>
                    <a href="${pageContext.request.contextPath}/teacher/myrooms" class="btn btn-secondary btn-sm">
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

                <% if (room != null) { %>

                    <!-- ======= THÔNG TIN PHÒNG ======= -->
                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">
                                Thông tin phòng #<%= room.getRoomId() %>
                            </h6>
                        </div>
                        <div class="card-body">
                            <dl class="row mb-0">
                                <dt class="col-sm-3">Tên phòng</dt>
                                <dd class="col-sm-9"><%= room.getRoomName() %></dd>

                                <dt class="col-sm-3">Vị trí</dt>
                                <dd class="col-sm-9"><%= room.getLocation() != null ? room.getLocation() : "-" %></dd>
                            </dl>
                        </div>
                    </div>

                    <!-- ======= DANH SÁCH TÀI SẢN (CÓ PHÂN TRANG) ======= -->
                    <div class="card shadow mb-4">
                        <div class="card-header py-3 d-flex align-items-center justify-content-between">
                            <h6 class="m-0 font-weight-bold text-primary">
                                <i class="fas fa-boxes"></i> Tài sản đang sử dụng trong phòng
                                <span class="badge badge-primary ml-1"><%= totalAssets %></span>
                            </h6>
                            <% if (totalPages > 1) { %>
                            <span class="pagination-info">
                                Trang <%= currentPage %> / <%= totalPages %>
                                &nbsp;|&nbsp; Hiển thị <%= assetsInRoom != null ? assetsInRoom.size() : 0 %> / <%= totalAssets %> tài sản
                            </span>
                            <% } %>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover mb-3" width="100%" cellspacing="0">
                                    <thead class="thead-light">
                                        <tr>
                                            <th style="width:40px">#</th>
                                            <th>Mã tài sản</th>
                                            <th>Tên tài sản</th>
                                            <th>Danh mục</th>
                                            <th>Serial</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            if (assetsInRoom == null || assetsInRoom.isEmpty()) {
                                        %>
                                            <tr>
                                                <td colspan="7" class="text-center text-muted py-4">
                                                    <i class="fas fa-box-open fa-2x mb-2"></i>
                                                    <p class="mb-0">Chưa có tài sản nào trong phòng này.</p>
                                                </td>
                                            </tr>
                                        <%
                                            } else {
                                                int rowNum = (currentPage - 1) * 10 + 1;
                                                for (Asset asset : assetsInRoom) {
                                        %>
                                            <tr>
                                                <td class="text-muted text-center"><%= rowNum++ %></td>
                                                <td><span class="asset-code"><%= asset.getAssetCode() %></span></td>
                                                <td><strong><%= asset.getAssetName() %></strong></td>
                                                <td><%= asset.getCategoryName() != null ? asset.getCategoryName() : "-" %></td>
                                                <td><%= asset.getSerialNumber() != null ? asset.getSerialNumber() : "-" %></td>
                                                <td>
                                                    <span class="badge <%= asset.getStatusBadgeClass() %>">
                                                        <%= asset.getStatusText() %>
                                                    </span>
                                                </td>
                                                <td>
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

                            <!-- ======= PAGINATION ======= -->
                            <% if (totalPages > 1) { %>
                            <div class="d-flex justify-content-center mt-2">
                                <nav aria-label="Phân trang tài sản">
                                    <ul class="pagination pagination-sm mb-0">

                                        <%-- Nút Đầu & Trước --%>
                                        <li class="page-item <%= (currentPage == 1) ? "disabled" : "" %>">
                                            <a class="page-link" href="?id=<%= roomId %>&page=1" title="Trang đầu">
                                                <i class="fas fa-angle-double-left"></i>
                                            </a>
                                        </li>
                                        <li class="page-item <%= (currentPage == 1) ? "disabled" : "" %>">
                                            <a class="page-link" href="?id=<%= roomId %>&page=<%= currentPage - 1 %>" title="Trang trước">
                                                <i class="fas fa-angle-left"></i>
                                            </a>
                                        </li>

                                        <%-- Các số trang (hiện tối đa 5 trang xung quanh trang hiện tại) --%>
                                        <%
                                            int startPage = Math.max(1, currentPage - 2);
                                            int endPage   = Math.min(totalPages, currentPage + 2);
                                            for (int p = startPage; p <= endPage; p++) {
                                        %>
                                        <li class="page-item <%= (p == currentPage) ? "active" : "" %>">
                                            <a class="page-link" href="?id=<%= roomId %>&page=<%= p %>"><%= p %></a>
                                        </li>
                                        <% } %>

                                        <%-- Nút Sau & Cuối --%>
                                        <li class="page-item <%= (currentPage == totalPages) ? "disabled" : "" %>">
                                            <a class="page-link" href="?id=<%= roomId %>&page=<%= currentPage + 1 %>" title="Trang sau">
                                                <i class="fas fa-angle-right"></i>
                                            </a>
                                        </li>
                                        <li class="page-item <%= (currentPage == totalPages) ? "disabled" : "" %>">
                                            <a class="page-link" href="?id=<%= roomId %>&page=<%= totalPages %>" title="Trang cuối">
                                                <i class="fas fa-angle-double-right"></i>
                                            </a>
                                        </li>

                                    </ul>
                                </nav>
                            </div>
                            <% } %>

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
