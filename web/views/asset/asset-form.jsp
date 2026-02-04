<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Asset" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Asset asset = (Asset) request.getAttribute("asset");
    String mode = (String) request.getAttribute("mode");
    boolean isEdit = "edit".equals(mode);
    
    // Format date cho input type="date"
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String purchaseDateStr = "";
    String receivedDateStr = "";
    if (asset != null) {
        if (asset.getPurchaseDate() != null) {
            purchaseDateStr = asset.getPurchaseDate().toLocalDate().format(dateFormatter);
        }
        if (asset.getReceivedDate() != null) {
            receivedDateStr = asset.getReceivedDate().toLocalDate().format(dateFormatter);
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%= isEdit ? "Cập nhật tài sản" : "Thêm tài sản mới" %> | School Asset Management</title>

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
                        <i class="fas fa-<%= isEdit ? "edit" : "plus" %> text-primary"></i> 
                        <%= isEdit ? "Cập nhật tài sản" : "Thêm tài sản mới" %>
                    </h1>
                    <a href="${pageContext.request.contextPath}/assets?action=list" 
                       class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
                        <i class="fas fa-arrow-left fa-sm text-white-50"></i> Quay lại danh sách
                    </a>
                </div>

                <!-- Form Card -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <%= isEdit ? "Thông tin tài sản" : "Nhập thông tin tài sản mới" %>
                        </h6>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/assets">
                            <input type="hidden" name="action" value="<%= mode %>"/>
                            <% if (isEdit && asset != null) { %>
                                <input type="hidden" name="assetId" value="<%= asset.getAssetId() %>"/>
                            <% } %>

                            <div class="row">
                                <!-- Cột trái -->
                                <div class="col-md-6">
                                    <!-- Asset Code -->
                                    <div class="form-group">
                                        <label for="assetCode">Mã tài sản <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="assetCode" name="assetCode" 
                                               value="<%= asset != null && asset.getAssetCode() != null ? asset.getAssetCode() : "" %>" 
                                               required>
                                    </div>

                                    <!-- Asset Name -->
                                    <div class="form-group">
                                        <label for="assetName">Tên tài sản <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="assetName" name="assetName" 
                                               value="<%= asset != null && asset.getAssetName() != null ? asset.getAssetName() : "" %>" 
                                               required>
                                    </div>

                                    <!-- Category ID -->
                                    <div class="form-group">
                                        <label for="categoryId">Danh mục (ID) <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="categoryId" name="categoryId" 
                                               value="<%= asset != null ? asset.getCategoryId() : "" %>" 
                                               required>
                                        <small class="form-text text-muted">Nhập ID danh mục từ bảng AssetCategories</small>
                                    </div>

                                    <!-- Serial Number -->
                                    <div class="form-group">
                                        <label for="serialNumber">Số seri</label>
                                        <input type="text" class="form-control" id="serialNumber" name="serialNumber" 
                                               value="<%= asset != null && asset.getSerialNumber() != null ? asset.getSerialNumber() : "" %>">
                                    </div>

                                    <!-- Model -->
                                    <div class="form-group">
                                        <label for="model">Model</label>
                                        <input type="text" class="form-control" id="model" name="model" 
                                               value="<%= asset != null && asset.getModel() != null ? asset.getModel() : "" %>">
                                    </div>

                                    <!-- Brand -->
                                    <div class="form-group">
                                        <label for="brand">Hãng</label>
                                        <input type="text" class="form-control" id="brand" name="brand" 
                                               value="<%= asset != null && asset.getBrand() != null ? asset.getBrand() : "" %>">
                                    </div>

                                    <!-- Origin Note -->
                                    <div class="form-group">
                                        <label for="originNote">Ghi chú nguồn gốc</label>
                                        <textarea class="form-control" id="originNote" name="originNote" rows="3"><%= asset != null && asset.getOriginNote() != null ? asset.getOriginNote() : "" %></textarea>
                                    </div>
                                </div>

                                <!-- Cột phải -->
                                <div class="col-md-6">
                                    <!-- Purchase Date -->
                                    <div class="form-group">
                                        <label for="purchaseDate">Ngày mua</label>
                                        <input type="date" class="form-control" id="purchaseDate" name="purchaseDate" 
                                               value="<%= purchaseDateStr %>">
                                    </div>

                                    <!-- Received Date -->
                                    <div class="form-group">
                                        <label for="receivedDate">Ngày nhận</label>
                                        <input type="date" class="form-control" id="receivedDate" name="receivedDate" 
                                               value="<%= receivedDateStr %>">
                                    </div>

                                    <!-- Condition Note -->
                                    <div class="form-group">
                                        <label for="conditionNote">Ghi chú tình trạng</label>
                                        <textarea class="form-control" id="conditionNote" name="conditionNote" rows="3"><%= asset != null && asset.getConditionNote() != null ? asset.getConditionNote() : "" %></textarea>
                                    </div>

                                    <!-- Status -->
                                    <div class="form-group">
                                        <label for="status">Trạng thái <span class="text-danger">*</span></label>
                                        <select class="form-control" id="status" name="status" required>
                                            <option value="">-- Chọn trạng thái --</option>
                                            <option value="IN_STOCK" <%= asset != null && "IN_STOCK".equals(asset.getStatus()) ? "selected" : "" %>>IN_STOCK - Trong kho</option>
                                            <option value="IN_USE" <%= asset != null && "IN_USE".equals(asset.getStatus()) ? "selected" : "" %>>IN_USE - Đang sử dụng</option>
                                            <option value="RETIRED" <%= asset != null && "RETIRED".equals(asset.getStatus()) ? "selected" : "" %>>RETIRED - Đã nghỉ hưu</option>
                                            <option value="MAINTENANCE" <%= asset != null && "MAINTENANCE".equals(asset.getStatus()) ? "selected" : "" %>>MAINTENANCE - Đang bảo trì</option>
                                        </select>
                                    </div>

                                    <!-- Current Room ID -->
                                    <div class="form-group">
                                        <label for="currentRoomId">Phòng hiện tại (ID)</label>
                                        <input type="number" class="form-control" id="currentRoomId" name="currentRoomId" 
                                               value="<%= asset != null && asset.getCurrentRoomId() != 0 ? asset.getCurrentRoomId() : "" %>">
                                        <small class="form-text text-muted">Nhập ID phòng từ bảng Rooms (để trống nếu chưa có)</small>
                                    </div>

                                    <!-- Current Holder ID -->
                                    <div class="form-group">
                                        <label for="currentHolderId">Người giữ hiện tại (ID)</label>
                                        <input type="number" class="form-control" id="currentHolderId" name="currentHolderId" 
                                               value="<%= asset != null && asset.getCurrentHolderId() != 0 ? asset.getCurrentHolderId() : "" %>">
                                        <small class="form-text text-muted">Nhập ID người dùng từ bảng Users (để trống nếu chưa có)</small>
                                    </div>

                                    <!-- Is Active -->
                                    <div class="form-group">
                                        <div class="form-check">
                                            <input type="checkbox" class="form-check-input" id="isActive" name="isActive" 
                                                   <%= asset == null || asset.isIsActive() ? "checked" : "" %>>
                                            <label class="form-check-label" for="isActive">
                                                Tài sản đang hoạt động
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Buttons -->
                            <div class="form-group mt-4">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save"></i> <%= isEdit ? "Cập nhật" : "Tạo mới" %>
                                </button>
                                <a href="${pageContext.request.contextPath}/assets?action=list" class="btn btn-secondary">
                                    <i class="fas fa-times"></i> Hủy
                                </a>
                            </div>
                        </form>
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

</body>
</html>
