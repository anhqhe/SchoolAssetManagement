<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.asset.AssetIncreaseRecord" %>
<%@ page import="model.asset.AssetIncreaseItem" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    AssetIncreaseRecord record = (AssetIncreaseRecord) request.getAttribute("record");
    List<AssetIncreaseItem> items = (List<AssetIncreaseItem>) request.getAttribute("items");
    if (items == null) {
        items = new java.util.ArrayList<>();
    }
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết phiếu ghi tăng | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
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
                        <i class="fas fa-file-alt text-primary"></i> Chi tiết phiếu ghi tăng
                    </h1>
                    <a href="${pageContext.request.contextPath}/asset-increase"
                       class="btn btn-sm btn-secondary shadow-sm">
                        <i class="fas fa-arrow-left"></i> Quay lại danh sách
                    </a>
                </div>

                <% if (record != null) { %>

                <!-- Thông tin phiếu -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-info-circle"></i> Thông tin phiếu ghi tăng
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <table class="table table-borderless">
                                    <tr>
                                        <th width="40%">Mã phiếu:</th>
                                        <td><strong><%= record.getIncreaseCode() %></strong></td>
                                    </tr>
                                    <tr>
                                        <th>Nguồn gốc:</th>
                                        <td><span class="badge badge-info"><%= record.getSourceTypeText() %></span></td>
                                    </tr>
                                    <tr>
                                        <th>Ngày nhận:</th>
                                        <td><%= record.getReceivedDateAsDate() != null ? sdf.format(record.getReceivedDateAsDate()) : "-" %></td>
                                    </tr>
                                    <tr>
                                        <th>Chi tiết nguồn:</th>
                                        <td><%= record.getSourceDetail() != null ? record.getSourceDetail() : "-" %></td>
                                    </tr>
                                </table>
                            </div>
                            <div class="col-md-6">
                                <table class="table table-borderless">
                                    <tr>
                                        <th width="40%">Người tạo:</th>
                                        <td><%= record.getCreatedByName() != null ? record.getCreatedByName() : "-" %></td>
                                    </tr>
                                    <tr>
                                        <th>Ngày tạo:</th>
                                        <td><%= record.getCreatedAtAsDate() != null ? sdfFull.format(record.getCreatedAtAsDate()) : "-" %></td>
                                    </tr>
                                    <tr>
                                        <th>Số tài sản:</th>
                                        <td><span class="badge badge-success"><%= record.getItemCount() %></span></td>
                                    </tr>
                                    <tr>
                                        <th>Ghi chú:</th>
                                        <td><%= record.getNote() != null ? record.getNote() : "-" %></td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Danh sách tài sản trong phiếu -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-list"></i> Danh sách tài sản trong phiếu
                            <span class="badge badge-primary ml-2"><%= items.size() %> tài sản</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" width="100%" cellspacing="0">
                                <thead class="thead-light">
                                    <tr>
                                        <th width="5%">#</th>
                                        <th>Mã tài sản</th>
                                        <th>Tên tài sản</th>
                                        <th>Ghi chú</th>
                                        <th width="10%">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (!items.isEmpty()) {
                                            int idx = 1;
                                            for (AssetIncreaseItem item : items) {
                                    %>
                                    <tr>
                                        <td><%= idx++ %></td>
                                        <td>
                                            <strong><%= item.getAssetCode() != null ? item.getAssetCode() : "-" %></strong>
                                        </td>
                                        <td><%= item.getAssetName() != null ? item.getAssetName() : "-" %></td>
                                        <td><%= item.getNote() != null ? item.getNote() : "-" %></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/assets?action=detail&id=<%= item.getAssetId() %>"
                                               class="btn btn-sm btn-info" title="Xem tài sản">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                    <%
                                            }
                                        } else {
                                    %>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted">Không có tài sản trong phiếu này</td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <% } else { %>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle"></i> Không tìm thấy phiếu ghi tăng.
                </div>
                <% } %>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <!-- Scripts -->
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
    </div>
</div>

</body>
</html>
