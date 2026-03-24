<%-- 
    Document   : asset-create
    Created on : Mar 10, 2026, 2:38:10 PM
    Author     : An
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.asset.Asset" %>
<%@ page import="model.asset.Room" %>
<%@ page import="model.asset.Teacher" %>
<%@ page import="model.User" %>
<%@ page import="model.asset.AssetCategory" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    boolean isAssetStaff = false;
    if (currentUser != null && currentUser.getRoles() != null) {
        isAssetStaff = currentUser.getRoles().contains("ASSET_STAFF")
                || currentUser.getRoles().contains("ADMIN");
    }
    if (!isAssetStaff) {
        response.sendRedirect(request.getContextPath() + "/assets?action=list");
        return;
    }
%>

<%
    Asset asset = (Asset) request.getAttribute("asset"); // dùng để giữ lại dữ liệu khi validation fail
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

    String quantityVal = request.getParameter("quantity");
    if (quantityVal == null || quantityVal.trim().isEmpty())
        quantityVal = "1";
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Nhập tài sản mới | School Asset Management</title>

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
                                <i class="fas fa-plus text-primary"></i> Nhập tài sản mới
                            </h1>
                            <a href="${pageContext.request.contextPath}/assets?action=list"
                               class="d-none d-sm-inline-block btn btn-sm btn-secondary shadow-sm">
                                <i class="fas fa-arrow-left fa-sm text-white-50"></i> Quay lại danh sách
                            </a>
                        </div>

                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Nhập thông tin tài sản mới</h6>
                            </div>
                            <div class="card-body">

                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger">${errorMessage}</div>
                                </c:if>

                                <form method="post" action="${pageContext.request.contextPath}/assets">
                                    <input type="hidden" name="action" value="create"/>

                                    <div class="row">
                                        <div class="col-md-6">
                                            <!-- Asset Name -->
                                            <div class="form-group">
                                                <label for="assetName">Tên tài sản <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="assetName" name="assetName"
                                                       value="<%= asset != null && asset.getAssetName() != null ? asset.getAssetName() : ""%>"
                                                       required>
                                            </div>

                                            <!-- Quantity -->
                                            <div class="form-group">
                                                <label for="quantity">Số lượng <span class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="quantity" name="quantity"
                                                       min="1" max="1000" value="<%= quantityVal%>" required>
                                                <small class="form-text text-muted">Hệ thống sẽ tự sinh mã cho từng tài sản</small>
                                            </div>

                                            <!-- Unit -->
                                            <div class="form-group">
                                                <label for="unit">Đơn vị tính</label>
                                                <input type="text" class="form-control" id="unit" name="unit"
                                                       value="<%= asset != null && asset.getUnit() != null ? asset.getUnit() : ""%>"
                                                       placeholder="VD: cái, hộp, chiếc, bộ">
                                            </div>

                                            <!-- Category -->
                                            <div class="form-group">
                                                <label for="categoryId">Danh mục <span class="text-danger">*</span></label>
                                                <select class="form-control" id="categoryId" name="categoryId" required>
                                                    <option value="">-- Chọn danh mục --</option>
                                                    <%
                                                        List<AssetCategory> categories = (List<AssetCategory>) request.getAttribute("categories");
                                                        Long selectedCatId = (asset != null) ? asset.getCategoryId() : null;
                                                        if (categories != null) {
                                                            for (AssetCategory c : categories) {
                                                    %>
                                                    <option value="<%= c.getCategoryId()%>"
                                                            <%= (selectedCatId != null && selectedCatId == c.getCategoryId()) ? "selected" : ""%>>
                                                        <%= c.getCategoryName()%>
                                                    </option>
                                                    <%
                                                            }
                                                        }
                                                    %>
                                                </select>
                                            </div>

                                            <!-- Serial -->
                                            <div class="form-group">
                                                <label for="serialNumber">Số seri</label>
                                                <input type="text" class="form-control" id="serialNumber" name="serialNumber"
                                                       maxlength="10"
                                                       value="<%= asset != null && asset.getSerialNumber() != null ? asset.getSerialNumber() : ""%>">
                                                <small class="form-text text-muted">Vui lòng nhập tối đa 10 ký tự.</small>
                                            </div>

                                            <div class="form-group">
                                                <label for="model">Model</label>
                                                <input type="text" class="form-control" id="model" name="model"
                                                       value="<%= asset != null && asset.getModel() != null ? asset.getModel() : ""%>">
                                            </div>

                                            <div class="form-group">
                                                <label for="brand">Hãng</label>
                                                <input type="text" class="form-control" id="brand" name="brand"
                                                       value="<%= asset != null && asset.getBrand() != null ? asset.getBrand() : ""%>">
                                            </div>

                                            <div class="form-group">
                                                <label for="originNote">Ghi chú nguồn gốc</label>
                                                <textarea class="form-control" id="originNote" name="originNote" rows="3"><%= asset != null && asset.getOriginNote() != null ? asset.getOriginNote() : ""%></textarea>
                                            </div>
                                        </div>

                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="purchaseDate">Ngày mua</label>
                                                <input type="date" class="form-control" id="purchaseDate" name="purchaseDate"
                                                       value="<%= purchaseDateStr%>">
                                            </div>

                                            <div class="form-group">
                                                <label for="receivedDate">Ngày nhận</label>
                                                <input type="date" class="form-control" id="receivedDate" name="receivedDate"
                                                       value="<%= receivedDateStr%>">
                                            </div>

                                            <div class="form-group">
                                                <label for="conditionNote">Ghi chú tình trạng</label>
                                                <textarea class="form-control" id="conditionNote" name="conditionNote" rows="3"><%= asset != null && asset.getConditionNote() != null ? asset.getConditionNote() : ""%></textarea>
                                            </div>

                                            <!-- Status: Create chỉ cho IN_STOCK -->
                                            <div class="form-group">
                                                <label for="status">Trạng thái</label>
                                                <select class="form-control" id="status" name="status" required>
                                                    <option value="IN_STOCK" selected>IN_STOCK - Trong kho</option>
                                                </select>
                                            </div>

                                            <!-- Room dropdown -->
                                            <div class="form-group">
                                                <label for="currentRoomId">Phòng hiện tại</label>
                                                <select class="form-control" id="currentRoomId" name="currentRoomId">
                                                    <option value="">-- Chọn phòng (để trống nếu chưa có) --</option>
                                                    <%
                                                        List<Room> rooms = (List<Room>) request.getAttribute("rooms");
                                                        long selectedRoomId = (asset != null) ? asset.getCurrentRoomId() : 0;
                                                        if (rooms != null) {
                                                            for (Room r : rooms) {
                                                    %>
                                                    <option value="<%= r.getRoomId()%>"
                                                            <%= (selectedRoomId != 0 && selectedRoomId == r.getRoomId()) ? "selected" : ""%>>
                                                        <%= r.getRoomName() != null ? r.getRoomName() : ""%>
                                                    </option>
                                                    <%
                                                            }
                                                        }
                                                    %>
                                                </select>
                                            </div>

                                            <!-- Teacher dropdown -->
                                            <div class="form-group">
                                                <label for="currentHolderId">Người giữ hiện tại</label>
                                                <select class="form-control" id="currentHolderId" name="currentHolderId">
                                                    <option value="">-- Chọn giáo viên (để trống nếu chưa có) --</option>
                                                    <%
                                                        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
                                                        long selectedHolderId = (asset != null) ? asset.getCurrentHolderId() : 0;
                                                        if (teachers != null) {
                                                            for (Teacher t : teachers) {
                                                    %>
                                                    <option value="<%= t.getUserId()%>"
                                                            <%= (selectedHolderId != 0 && selectedHolderId == t.getUserId()) ? "selected" : ""%>>
                                                        <%= t.getFullName() != null ? t.getFullName() : ""%>
                                                    </option>
                                                    <%
                                                            }
                                                        }
                                                    %>
                                                </select>
                                                <small class="form-text text-muted">Chỉ hiển thị giáo viên</small>
                                            </div>

                                            <div class="form-group">
                                                <div class="form-check">
                                                    <input type="checkbox" class="form-check-input" id="isActive" name="isActive"
                                                           <%= asset == null || asset.isIsActive() ? "checked" : ""%>>
                                                    <label class="form-check-label" for="isActive">Tài sản đang hoạt động</label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group mt-4">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save"></i> Tạo mới
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

        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
    </body>
</html>