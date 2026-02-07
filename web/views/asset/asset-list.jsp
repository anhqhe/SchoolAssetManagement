<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.asset.Asset" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    List<Asset> assets = (List<Asset>) request.getAttribute("assets");
    if (assets == null) {
        assets = new java.util.ArrayList<>();
    }
%>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    List<String> roles = null;
    boolean isAssetStaff = false;
    if (currentUser != null) {
        roles = currentUser.getRoles();
        isAssetStaff = roles != null && (roles.contains("ASSET_STAFF") || roles.contains("ADMIN"));
    }
    request.setAttribute("isAssetStaff", isAssetStaff);
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý tài sản | School Asset Management</title>

        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
    </head>

    <body id="page-top">

        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">

                    <%@ include file="/views/layout/topbar.jsp" %>

                    <!-- Page Content -->
                    <div class="container-fluid">

                        <!-- Page Heading -->
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-boxes text-primary"></i> Quản lý tài sản
                            </h1>
                            <c:if test="${isAssetStaff}">
                                <a href="${pageContext.request.contextPath}/assets?action=create" 
                                   class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm">
                                    <i class="fas fa-plus fa-sm text-white-50"></i> Thêm tài sản mới
                                </a>
                            </c:if>
                        </div>
                        <!-- Filter Card -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-filter"></i> Tìm kiếm & Lọc
                                </h6>
                            </div>
                            <div class="card-body">
                                <form method="get" action="${pageContext.request.contextPath}/assets" class="form-inline">
                                    <input type="hidden" name="action" value="list">
                                    <div class="form-group mr-3 mb-2">
                                        <input type="text" 
                                               name="keyword" 
                                               class="form-control" 
                                               placeholder="Tìm kiếm tên, mã, serial..."
                                               value="${keyword}">
                                    </div>
                                    <div class="form-group mr-3 mb-2">
                                        <select name="status" class="form-control">
                                            <option value="">-- Tất cả trạng thái --</option>
                                            <option value="IN_STOCK" ${status == 'IN_STOCK' ? 'selected' : ''}>Trong kho</option>
                                            <option value="IN_USE" ${status == 'IN_USE' ? 'selected' : ''}>Đang sử dụng</option>
                                        </select>
                                    </div>
                                    <button type="submit" class="btn btn-primary mb-2 mr-2">
                                        <i class="fas fa-search"></i> Tìm kiếm
                                    </button>
                                    <a href="${pageContext.request.contextPath}/assets?action=list" class="btn btn-secondary mb-2">
                                        <i class="fas fa-redo"></i> Đặt lại
                                    </a>
                                </form>
                            </div>
                        </div>

                        <!-- Data Table Card -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Danh sách tài sản</h6>
                            </div>
                            <div class="card-body">
                                            <div class="table-responsive">
                                                <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                                    <thead>
                                                        <tr>
                                                            <th>Mã tài sản</th>
                                                            <th>Tên tài sản</th>
                                                            <th>Danh mục</th>
                                                            <th>Số lượng</th>
                                                            <th>Số seri</th>
                                                            <th>Model</th>
                                                            <th>Hãng</th>
                                                            <th>Trạng thái</th>
                                                            <th>Phòng hiện tại</th>
                                                            <th>Hoạt động</th>
                                                            <th>Thao tác</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <%
                                                            if (assets != null && !assets.isEmpty()) {
                                                                for (Asset a : assets) {
                                                        %>
                                                        <tr>
                                                            <td><%= a.getAssetCode() != null ? a.getAssetCode() : "" %></td>
                                                            <td><%= a.getAssetName() != null ? a.getAssetName() : "" %></td>
                                                            <td><%= a.getCategoryName() != null ? a.getCategoryName() : "-" %></td>
                                                            <td class="text-center"><span class="badge badge-primary"><%= a.getQuantity() %></span></td>
                                                            <td><%= a.getSerialNumber() != null ? a.getSerialNumber() : "-" %></td>
                                                            <td><%= a.getModel() != null ? a.getModel() : "-" %></td>
                                                            <td><%= a.getBrand() != null ? a.getBrand() : "-" %></td>
                                                            <td>
                                                                <%
                                                                    String itemStatus = a.getStatus() != null ? a.getStatus() : "";
                                                                    String badgeClass = "badge-secondary";
                                                                    if ("IN_STOCK".equals(itemStatus)) badgeClass = "badge-info";
                                                                    else if ("IN_USE".equals(itemStatus)) badgeClass = "badge-success";
                                                                    else if ("RETIRED".equals(itemStatus)) badgeClass = "badge-danger";
                                                                %>
                                                                <span class="badge <%= badgeClass %>"><%= itemStatus %></span>
                                                            </td>
                                                            <td><%= a.getRoomName() != null ? a.getRoomName() : "-" %></td>
                                                            <td>
                                                                <% if (a.isIsActive()) { %>
                                                                <span class="badge badge-success">Hoạt động</span>
                                                                <% } else { %>
                                                                <span class="badge badge-secondary">Không hoạt động</span>
                                                                <% } %>
                                                            </td>
                                                            <td>
                                                                <% if (isAssetStaff) { %>
                                                                <a href="${pageContext.request.contextPath}/assets?action=edit&id=<%= a.getAssetId() %>" 
                                                                   class="btn btn-sm btn-warning">
                                                                    <i class="fas fa-edit"></i> Sửa
                                                                </a>
                                                                <a href="${pageContext.request.contextPath}/assets?action=delete&id=<%= a.getAssetId() %>" 
                                                                   class="btn btn-sm btn-danger"
                                                                   onclick="return confirm('Bạn chắc chắn muốn xóa tài sản này?');">
                                                                    <i class="fas fa-trash"></i> Xóa
                                                                </a>
                                                                <% } %>
                                                                <a href="${pageContext.request.contextPath}/assets?action=detail&id=<%= a.getAssetId() %>" 
                                                                   class="btn btn-sm btn-info" title="Chi tiết">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
                                                            </td>

                                                        </tr>
                                                        <%
                                                                }
                                                            } else {
                                                        %>
                                                        <tr>
                                                            <td colspan="11" class="text-center">Chưa có dữ liệu</td>
                                                        </tr>
                                                        <%
                                                            }
                                                        %>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>

                        <%@ include file="/views/layout/footer.jsp" %>

                    </div>
                </div>

                <!-- Scripts -->
                <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

                <!-- DataTables -->
                <script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>
                </body>
                </html>
