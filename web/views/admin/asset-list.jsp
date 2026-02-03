<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
    <title>Danh sách tài sản | School Asset Management</title>

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

            <!-- Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-boxes text-primary"></i> Quản lý tài sản
                    </h1>
                    
                    <!-- Create Asset Button (chỉ ASSET_STAFF) -->
                    <c:if test="${isAssetStaff}">
                        <button class="btn btn-primary btn-icon-split shadow-sm" onclick="alert('Chức năng Create Asset - Coming soon!')">
                            <span class="icon text-white-50">
                                <i class="fas fa-plus"></i>
                            </span>
                            <span class="text">Thêm tài sản mới</span>
                        </button>
                    </c:if>
                </div>

                <!-- Error/Success Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                        <button type="button" class="close" data-dismiss="alert">
                            <span>&times;</span>
                        </button>
                    </div>
                </c:if>

                <!-- Filter Card -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-filter"></i> Tìm kiếm & Lọc
                        </h6>
                    </div>
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/admin/assets" class="form-inline">
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
                                    <option value="MAINTENANCE" ${status == 'MAINTENANCE' ? 'selected' : ''}>Bảo trì</option>
                                    <option value="DAMAGED" ${status == 'DAMAGED' ? 'selected' : ''}>Hỏng hóc</option>
                                    <option value="DISPOSED" ${status == 'DISPOSED' ? 'selected' : ''}>Đã thanh lý</option>
                                </select>
                            </div>
                            
                            <button type="submit" class="btn btn-primary mb-2 mr-2">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>
                            
                            <a href="${pageContext.request.contextPath}/admin/assets" class="btn btn-secondary mb-2">
                                <i class="fas fa-redo"></i> Đặt lại
                            </a>
                        </form>
                    </div>
                </div>

                <!-- Assets Table Card -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-list"></i> Danh sách tài sản 
                            <span class="badge badge-primary">${assets != null ? assets.size() : 0}</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" id="dataTable" width="100%" cellspacing="0">
                                <thead class="thead-light">
                                    <tr>
                                        <th>Mã tài sản</th>
                                        <th>Tên tài sản</th>
                                        <th>Loại</th>
                                        <th>Serial Number</th>
                                        <th>Vị trí</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty assets}">
                                            <tr>
                                                <td colspan="7" class="text-center text-muted">
                                                    <i class="fas fa-inbox fa-3x mb-3 mt-3"></i>
                                                    <p>Chưa có tài sản nào</p>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="asset" items="${assets}">
                                                <tr>
                                                    <td>
                                                        <strong>${asset.assetCode}</strong>
                                                    </td>
                                                    <td>${asset.assetName}</td>
                                                    <td>
                                                        <span class="badge badge-info">${asset.categoryName}</span>
                                                    </td>
                                                    <td>
                                                        <small class="text-muted">${asset.serialNumber != null ? asset.serialNumber : '-'}</small>
                                                    </td>
                                                    <td>
                                                        <i class="fas fa-door-open text-primary"></i>
                                                        ${asset.roomName != null ? asset.roomName : '-'}
                                                    </td>
                                                    <td>
                                                        <span class="badge ${asset.statusBadgeClass}">
                                                            ${asset.statusText}
                                                        </span>
                                                    </td>
                                                    <td class="text-center">
                                                        <a href="${pageContext.request.contextPath}/admin/asset-detail?id=${asset.assetId}" 
                                                           class="btn btn-sm btn-info" title="Xem chi tiết">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        
                                                        <c:if test="${isAssetStaff}">
                                                            <button class="btn btn-sm btn-warning" 
                                                                    onclick="alert('Chức năng Edit Asset - Coming soon!')" 
                                                                    title="Sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
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

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

<!-- DataTables -->
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
$(document).ready(function() {
    $('#dataTable').DataTable({
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
        "pageLength": 10,
        "order": [[0, "desc"]]
    });
});
</script>

</body>
</html>

