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
    <title>Chi tiết tài sản | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
    
    <style>
        .detail-label {
            font-weight: 600;
            color: #5a5c69;
        }
        .detail-value {
            color: #6e707e;
        }
        .status-box {
            padding: 30px;
            border-radius: 10px;
            text-align: center;
            background: linear-gradient(135deg, #f8f9fc 0%, #e3e6f0 100%);
        }
        .status-icon {
            font-size: 60px;
            margin-bottom: 15px;
        }
    </style>
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
                        <i class="fas fa-box text-primary"></i> Chi tiết tài sản
                    </h1>
                    <a href="${pageContext.request.contextPath}/assets?action=list" class="btn btn-secondary btn-sm">
                        <i class="fas fa-arrow-left"></i> Quay lại danh sách
                    </a>
                </div>

                <c:choose>
                    <c:when test="${empty asset}">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i> Không tìm thấy tài sản
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            
                            <!-- Left Column - Asset Info -->
                            <div class="col-lg-8">
                                
                                <!-- Basic Info Card -->
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-info-circle"></i> Thông tin cơ bản
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Mã tài sản:</div>
                                            <div class="col-md-8 detail-value">
                                                <span class="badge badge-primary" style="font-size: 16px;">
                                                    ${asset.assetCode}
                                                </span>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Tên tài sản:</div>
                                            <div class="col-md-8 detail-value">
                                                <strong>${asset.assetName}</strong>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Loại tài sản:</div>
                                            <div class="col-md-8 detail-value">
                                                <span class="badge badge-info">${asset.categoryName}</span>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Serial Number:</div>
                                            <div class="col-md-8 detail-value">
                                                <code>${asset.serialNumber != null ? asset.serialNumber : 'N/A'}</code>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Model:</div>
                                            <div class="col-md-8 detail-value">
                                                ${asset.model != null ? asset.model : 'N/A'}
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Brand:</div>
                                            <div class="col-md-8 detail-value">
                                                ${asset.brand != null ? asset.brand : 'N/A'}
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Nguồn gốc:</div>
                                            <div class="col-md-8 detail-value">
                                                ${asset.originNote != null ? asset.originNote : 'N/A'}
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Date Info Card -->
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-calendar"></i> Thông tin ngày tháng
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Ngày mua:</div>
                                            <div class="col-md-8 detail-value">
                                                <c:choose>
                                                    <c:when test="${asset.purchaseDate != null}">
                                                        <fmt:formatDate value="${asset.purchaseDate}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>N/A</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Ngày nhập kho:</div>
                                            <div class="col-md-8 detail-value">
                                                <c:choose>
                                                    <c:when test="${asset.receivedDate != null}">
                                                        <fmt:formatDate value="${asset.receivedDate}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>N/A</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Ngày tạo:</div>
                                            <div class="col-md-8 detail-value">
                                                <c:choose>
                                                    <c:when test="${asset.createdAt != null}">
                                                        <fmt:formatDate value="${asset.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </c:when>
                                                    <c:otherwise>N/A</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Location & Holder Card -->
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-map-marker-alt"></i> Vị trí & Người giữ
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Phòng hiện tại:</div>
                                            <div class="col-md-8 detail-value">
                                                <c:choose>
                                                    <c:when test="${asset.roomName != null}">
                                                        <i class="fas fa-door-open text-primary"></i> 
                                                        <strong>${asset.roomName}</strong>
                                                        <c:if test="${asset.roomLocation != null}">
                                                            <br><small class="text-muted">${asset.roomLocation}</small>
                                                        </c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Chưa xác định</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Người giữ:</div>
                                            <div class="col-md-8 detail-value">
                                                <c:choose>
                                                    <c:when test="${asset.holderName != null}">
                                                        <i class="fas fa-user text-success"></i> ${asset.holderName}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Không có</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        
                                        <div class="row mb-3">
                                            <div class="col-md-4 detail-label">Tình trạng:</div>
                                            <div class="col-md-8 detail-value">
                                                ${asset.conditionNote != null ? asset.conditionNote : 'Bình thường'}
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </div>

                            <!-- Right Column - Status & Actions -->
                            <div class="col-lg-4">
                                
                                <!-- Status Card -->
                                <div class="card shadow mb-4">
                                    <div class="card-body">
                                        <div class="status-box">
                                            <c:choose>
                                                <c:when test="${asset.status == 'IN_STOCK'}">
                                                    <i class="fas fa-warehouse status-icon text-info"></i>
                                                </c:when>
                                                <c:when test="${asset.status == 'IN_USE'}">
                                                    <i class="fas fa-check-circle status-icon text-success"></i>
                                                </c:when>
                                                <c:when test="${asset.status == 'MAINTENANCE'}">
                                                    <i class="fas fa-tools status-icon text-warning"></i>
                                                </c:when>
                                                <c:when test="${asset.status == 'DAMAGED'}">
                                                    <i class="fas fa-exclamation-triangle status-icon text-danger"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-question-circle status-icon text-secondary"></i>
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <h5 class="text-gray-800">Trạng thái</h5>
                                            <h3>
                                                <span class="badge ${asset.statusBadgeClass}" style="font-size: 18px;">
                                                    ${asset.statusText}
                                                </span>
                                            </h3>
                                        </div>
                                        
                                        <!-- Change Status Button (ASSET_STAFF only) -->
                                        <c:if test="${isAssetStaff}">
                                            <hr>
                                            <button class="btn btn-warning btn-block" onclick="showChangeStatusModal()">
                                                <i class="fas fa-exchange-alt"></i> Thay đổi trạng thái
                                            </button>
                                        </c:if>
                                    </div>
                                </div>

                                <!-- Action Buttons Card (ASSET_STAFF only) -->
                                <c:if test="${isAssetStaff}">
                                    <div class="card shadow mb-4">
                                        <div class="card-header py-3">
                                            <h6 class="m-0 font-weight-bold text-primary">
                                                <i class="fas fa-bolt"></i> Thao tác
                                            </h6>
                                        </div>
                                        <div class="card-body">
                                            <button class="btn btn-primary btn-block mb-2" onclick="alert('Chức năng Update Asset - Coming soon!')">
                                                <i class="fas fa-edit"></i> Cập nhật thông tin
                                            </button>
                                            
                                            <button class="btn btn-info btn-block mb-2" onclick="alert('Chức năng Transfer - Coming soon!')">
                                                <i class="fas fa-exchange-alt"></i> Chuyển phòng
                                            </button>
                                            
                                            <button class="btn btn-success btn-block mb-2" onclick="alert('Chức năng Allocate - Coming soon!')">
                                                <i class="fas fa-hand-holding"></i> Cấp phát
                                            </button>
                                            
                                            <button class="btn btn-warning btn-block mb-2" onclick="alert('Chức năng Maintenance - Coming soon!')">
                                                <i class="fas fa-tools"></i> Bảo trì
                                            </button>
                                            
                                            <hr>
                                            
                                            <button class="btn btn-danger btn-block" onclick="confirmDelete()">
                                                <i class="fas fa-trash"></i> Xóa tài sản
                                            </button>
                                        </div>
                                    </div>
                                </c:if>

                                <!-- Quick Info Card -->
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-chart-pie"></i> Thông tin nhanh
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="text-center mb-3">
                                            <small class="text-muted">Trạng thái hoạt động</small>
                                            <h4>
                                                <c:choose>
                                                    <c:when test="${asset.active}">
                                                        <span class="badge badge-success">
                                                            <i class="fas fa-check-circle"></i> Đang hoạt động
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-secondary">
                                                            <i class="fas fa-times-circle"></i> Ngừng hoạt động
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </h4>
                                        </div>
                                        
                                        <hr>
                                        
                                        <div class="row text-center">
                                            <div class="col-6">
                                                <small class="text-muted">Asset ID</small>
                                                <h5 class="text-gray-800">#${asset.assetId}</h5>
                                            </div>
                                            <div class="col-6">
                                                <small class="text-muted">Category ID</small>
                                                <h5 class="text-gray-800">#${asset.categoryId}</h5>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </div>

                        </div>
                    </c:otherwise>
                </c:choose>

            </div>

        </div>

        <%@ include file="/views/layout/footer.jsp" %>

    </div>
</div>

<!-- Change Status Modal (ASSET_STAFF only) -->
<c:if test="${isAssetStaff && not empty asset}">
    <div class="modal fade" id="changeStatusModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-warning text-white">
                    <h5 class="modal-title">
                        <i class="fas fa-exchange-alt"></i> Thay đổi trạng thái tài sản
                    </h5>
                    <button type="button" class="close text-white" data-dismiss="modal">
                        <span>&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="changeStatusForm">
                        <input type="hidden" name="assetId" value="${asset.assetId}">
                        
                        <div class="alert alert-info">
                            <strong>Tài sản:</strong> ${asset.assetCode} - ${asset.assetName}<br>
                            <strong>Trạng thái hiện tại:</strong> 
                            <span class="badge ${asset.statusBadgeClass}">${asset.statusText}</span>
                        </div>
                        
                        <div class="form-group">
                            <label>Trạng thái mới:</label>
                            <select name="newStatus" class="form-control" required>
                                <option value="">-- Chọn trạng thái --</option>
                                <option value="IN_STOCK" ${asset.status == 'IN_STOCK' ? 'selected' : ''}>Trong kho</option>
                                <option value="IN_USE" ${asset.status == 'IN_USE' ? 'selected' : ''}>Đang sử dụng</option>
                                <option value="MAINTENANCE" ${asset.status == 'MAINTENANCE' ? 'selected' : ''}>Bảo trì</option>
                                <option value="DAMAGED" ${asset.status == 'DAMAGED' ? 'selected' : ''}>Hỏng hóc</option>
                                <option value="DISPOSED" ${asset.status == 'DISPOSED' ? 'selected' : ''}>Đã thanh lý</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label>Lý do thay đổi:</label>
                            <textarea name="reason" class="form-control" rows="3" required></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">
                        <i class="fas fa-times"></i> Hủy
                    </button>
                    <button type="button" class="btn btn-warning" onclick="submitChangeStatus()">
                        <i class="fas fa-check"></i> Xác nhận thay đổi
                    </button>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

<script>
function showChangeStatusModal() {
    $('#changeStatusModal').modal('show');
}

function submitChangeStatus() {
    var assetId = $('input[name="assetId"]').val();
    var newStatus = $('select[name="newStatus"]').val();
    var reason = $('textarea[name="reason"]').val();
    
    alert('Chức năng thay đổi trạng thái - Coming soon!\n\n' + 
          'Asset ID: ' + assetId + '\n' +
          'New Status: ' + newStatus + '\n' +
          'Reason: ' + reason);
    
    $('#changeStatusModal').modal('hide');
}

function confirmDelete() {
    if (confirm('Bạn có chắc chắn muốn xóa tài sản "${asset.assetCode} - ${asset.assetName}"?\n\nHành động này không thể hoàn tác!')) {
        alert('Chức năng Delete Asset - Coming soon!\n\nAsset ID: ${asset.assetId}');
    }
}
</script>

</body>
</html>

