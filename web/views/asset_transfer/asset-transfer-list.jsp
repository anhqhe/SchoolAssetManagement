<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    List<String> roles = null;
    boolean isAssetStaff = false;
    boolean canApprove = false;
    if (currentUser != null) {
        roles = currentUser.getRoles();
        isAssetStaff = roles != null && (roles.contains("ASSET_STAFF") || roles.contains("BOARD") || roles.contains("ADMIN"));
        canApprove = roles != null && (roles.contains("BOARD") || roles.contains("ADMIN"));
    }
    request.setAttribute("isAssetStaff", isAssetStaff);
    request.setAttribute("canApprove", canApprove);
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách điều chuyển | School Asset Management</title>
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
                        <i class="fas fa-exchange-alt text-primary"></i> Danh sách điều chuyển tài sản
                    </h1>
                    <c:if test="${isAssetStaff}">
                        <button class="btn btn-primary shadow-sm"
                                data-toggle="modal"
                                data-target="#createTransferModal">
                            <i class="fas fa-plus fa-sm text-white-50"></i>
                            Tạo yêu cầu điều chuyển
                        </button>
                    </c:if>
                </div>

                <!-- FILTER -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-filter"></i> Tìm kiếm & Lọc
                        </h6>
                    </div>
                    <div class="card-body">
                        <form id="filterForm" method="get"
                              action="${pageContext.request.contextPath}/transfers/list">
                            <div class="row">
                                <div class="col-md-3 mb-2">
                                    <input type="text" name="keyword" class="form-control"
                                           placeholder="Mã phiếu, lý do..." value="${keyword}">
                                </div>
                                <div class="col-md-2 mb-2">
                                    <select name="status" class="form-control">
                                        <option value="">-- Tất cả trạng thái --</option>
                                        <option value="PENDING"   ${selectedStatus == 'PENDING'   ? 'selected' : ''}>Chờ duyệt</option>
                                        <option value="APPROVED"  ${selectedStatus == 'APPROVED'  ? 'selected' : ''}>Đã duyệt</option>
                                        <option value="COMPLETED" ${selectedStatus == 'COMPLETED' ? 'selected' : ''}>Hoàn tất</option>
                                        <option value="REJECTED"  ${selectedStatus == 'REJECTED'  ? 'selected' : ''}>Từ chối</option>
                                    </select>
                                </div>
                                <div class="col-md-2 mb-2">
                                    <select name="fromRoomId" class="form-control">
                                        <option value="">-- Phòng đi --</option>
                                        <c:forEach var="r" items="${rooms}">
                                            <option value="${r.roomId}" ${fromRoomId == r.roomId ? 'selected' : ''}>${r.roomName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-2 mb-2">
                                    <select name="toRoomId" class="form-control">
                                        <option value="">-- Phòng đến --</option>
                                        <c:forEach var="r" items="${rooms}">
                                            <option value="${r.roomId}" ${toRoomId == r.roomId ? 'selected' : ''}>${r.roomName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-1 mb-2">
                                    <input type="date" id="fromDate" name="fromDate"
                                           class="form-control" value="${fromDate}">
                                </div>
                                <div class="col-md-1 mb-2">
                                    <input type="date" id="toDate" name="toDate"
                                           class="form-control" value="${toDate}">
                                </div>
                                <div class="col-md-1 mb-2 d-flex">
                                    <button type="submit" class="btn btn-primary mr-2">
                                        <i class="fas fa-search"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/transfers/list"
                                       class="btn btn-secondary">
                                        <i class="fas fa-redo"></i>
                                    </a>
                                </div>
                            </div>
                            <div id="dateError" class="text-danger small mt-2" style="display:none;">
                                Ngày đến phải lớn hơn hoặc bằng ngày bắt đầu.
                            </div>
                        </form>
                    </div>
                </div>

                <!-- TRANSFER TABLE -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-list"></i> Danh sách phiếu điều chuyển
                            <span class="badge badge-primary">${transfers != null ? transfers.size() : 0}</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" id="dataTable">
                                <thead class="thead-light">
                                <tr>
                                    <th>Mã phiếu</th>
                                    <th>Phòng đi</th>
                                    <th>Phòng đến</th>
                                    <th>Người yêu cầu</th>
                                    <th>Tài sản</th>
                                    <th>Lý do</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${empty transfers}">
                                        <tr>
                                            <td colspan="9" class="text-center text-muted">
                                                <i class="fas fa-inbox fa-3x mb-3 mt-3"></i>
                                                <p>Chưa có phiếu điều chuyển nào</p>
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="t" items="${transfers}">
                                            <tr>
                                                <td style="cursor:pointer"
                                                    class="view-btn text-primary font-weight-bold"
                                                    data-toggle="tooltip"
                                                    data-placement="top"
                                                    title="Xem chi tiết phiếu ${t.transferCode}"
                                                    data-id="${t.transferId}">
                                                    <strong class="text-primary">${t.transferCode}</strong>
                                                </td>
                                                <td><i class="fas fa-door-open text-primary"></i> ${t.fromRoomName}</td>
                                                <td><i class="fas fa-door-open text-success"></i> ${t.toRoomName}</td>
                                                <td>${t.requestedByName}</td>
                                                <td>
                                                    <small class="text-muted">
                                                        <i class="fas fa-box text-warning"></i>
                                                        ${not empty t.assetNames ? t.assetNames : 'Không có'}
                                                    </small>
                                                </td>
                                                <td>${t.reason}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${t.status == 'PENDING'}">
                                                            <span class="badge badge-warning">Chờ duyệt</span>
                                                        </c:when>
                                                        <c:when test="${t.status == 'APPROVED'}">
                                                            <span class="badge badge-success">Đã duyệt</span>
                                                        </c:when>
                                                        <c:when test="${t.status == 'COMPLETED'}">
                                                            <span class="badge badge-primary">Hoàn tất</span>
                                                        </c:when>
                                                        <c:when test="${t.status == 'REJECTED'}">
                                                            <span class="badge badge-danger">Từ chối</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-secondary">${t.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <fmt:formatDate value="${t.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </td>
                                                <td class="text-center">
                                                    <%-- Nút View: chỉ data-id, còn lại lấy qua AJAX --%>
                                                    <button class="btn btn-sm btn-warning view-btn"
                                                            data-id="${t.transferId}"
                                                            title="Xem chi tiết">
                                                        <i class="fas fa-eye"></i>
                                                    </button>

                                                    <%-- Nút Edit --%>
                                                    <button class="btn btn-sm btn-info edit-btn"
                                                            data-id="${t.transferId}"
                                                            data-code="${t.transferCode}"
                                                            data-from="${t.fromRoomId}"
                                                            data-to="${t.toRoomId}"
                                                            data-reason="${t.reason}"
                                                            title="Chỉnh sửa"
                                                            ${t.status != 'PENDING' ? 'disabled' : ''}>
                                                        <i class="fas fa-edit"></i>
                                                    </button>

                                                    <%-- Nút Duyệt: chỉ BOARD/ADMIN, chỉ data-id — version lấy qua AJAX --%>
                                                    <c:if test="${canApprove}">
                                                        <button class="btn btn-sm btn-success approve-open-btn"
                                                                data-id="${t.transferId}"
                                                                title="Phê duyệt / Từ chối"
                                                                ${(t.status == 'APPROVED' || t.status == 'COMPLETED' || t.status == 'REJECTED') ? 'disabled' : ''}>
                                                            <i class="fas fa-check"></i>
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

<!-- ===================== MODALS ===================== -->

<!-- Transfer Detail Modal -->
<div class="modal fade" id="transferModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-info-circle"></i> Chi tiết phiếu điều chuyển</h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div id="detailLoading" class="text-center py-4">
                    <i class="fas fa-spinner fa-spin fa-2x text-primary"></i>
                    <p class="mt-2 text-muted">Đang tải dữ liệu...</p>
                </div>
                <div id="detailContent" style="display:none;">
                    <p><strong>Mã phiếu:</strong>     <span id="mCode"></span></p>
                    <p><strong>Phòng đi:</strong>      <span id="mFrom"></span></p>
                    <p><strong>Phòng đến:</strong>     <span id="mTo"></span></p>
                    <p><strong>Người yêu cầu:</strong> <span id="mUser"></span></p>
                    <p><strong>Lý do:</strong>         <span id="mReason"></span></p>
                    <p><strong>Trạng thái:</strong>    <span id="mStatus"></span></p>
                    <p><strong>Ngày tạo:</strong>      <span id="mDate"></span></p>
                    <div>
                        <strong>Tài sản:</strong>
                        <div id="mAssets" class="mt-1"></div>
                    </div>
                    <div id="assetHistory" class="mt-3"></div>
                </div>
                <div id="detailError" class="alert alert-danger" style="display:none;">
                    <i class="fas fa-exclamation-circle"></i>
                    <span id="detailErrorText">Không thể tải dữ liệu. Vui lòng thử lại.</span>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<!-- Approve/Reject Modal -->
<div class="modal fade" id="approveModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-tasks"></i> Xử lý phiếu điều chuyển</h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body text-center py-4">
                <p class="mb-1 text-muted">Phiếu điều chuyển</p>
                <h5 id="approveCode" class="font-weight-bold text-primary mb-3"></h5>
                <p class="mb-0">Bạn muốn <strong>phê duyệt</strong> hay <strong>từ chối</strong> phiếu này?</p>
                <div id="approveErrorMsg" class="alert alert-danger mt-3 mb-0 d-none">
                    <i class="fas fa-exclamation-circle"></i> <span id="approveErrorText"></span>
                </div>
            </div>
            <div class="modal-footer justify-content-between">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <i class="fas fa-times"></i> Hủy
                </button>
                <div>
                    <button id="confirmRejectBtn" class="btn btn-danger mr-2">
                        <i class="fas fa-ban"></i> Từ chối
                    </button>
                    <button id="confirmApproveBtn" class="btn btn-success">
                        <i class="fas fa-check"></i> Phê duyệt
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Create Transfer Modal -->
<div class="modal fade" id="createTransferModal" tabindex="-1">
    <div class="modal-dialog modal-xl modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-exchange-alt"></i> Tạo yêu cầu điều chuyển</h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <form id="createTransferForm" method="post"
                  action="${pageContext.request.contextPath}/transfers/create">
                <div class="modal-body">
                    <div class="form-group">
                 <label>Phòng đi <span class="text-danger">*</span></label>
                        <select id="fromRoom" name="fromRoomId" class="form-control" required>
                            <option value="">-- Chọn phòng đi --</option>
                            <c:forEach var="room" items="${rooms}">
                                <option value="${room.roomId}">${room.roomName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                    <label>Phòng đến <span class="text-danger">*</span></label>
                        <select id="toRoom" name="toRoomId" class="form-control" required>
                            <option value="">-- Chọn phòng đến --</option>
                            <c:forEach var="room" items="${rooms}">
                                <option value="${room.roomId}">${room.roomName}</option>
                            </c:forEach>
                        </select>
                        <small id="roomError" class="text-danger"></small>
                    </div>
                    <div class="form-group">
              <label>Chọn tài sản cần điều chuyển <span class="text-danger">*</span></label>
                        <div class="form-group">
                            <div class="input-group input-group-sm">
                                <input type="text" id="assetSearch" class="form-control"
                                       placeholder="Nhập mã hoặc tên tài sản...">
                                <div class="input-group-append">
                                    <button type="button" id="btnSearchAsset" class="btn btn-primary">
                                        <i class="fas fa-search"></i>
                                    </button>
                                    <button type="button" id="btnResetAsset" class="btn btn-secondary">
                                        <i class="fas fa-redo"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="table-responsive border rounded p-2"
                             style="max-height:300px; overflow-y:auto; overflow-x:hidden;">
                            <table class="table table-sm table-hover">
                                <thead class="thead-light">
                                <tr>
                                    <th width="40px"><input type="checkbox" id="checkAllAssets"></th>
                                    <th width="120px">Mã tài sản</th>
                                    <th>Tên tài sản</th>
                                    <th width="220px">Ghi chú</th>
                                </tr>
                                </thead>
                               <tbody>
                                <c:forEach var="a" items="${assets}">
                                    <tr data-room-id="${a.currentRoomId}">
                                        <td>
                                            <input type="checkbox" name="assetIds"
                                                   value="${a.assetId}" class="asset-checkbox">
                                        </td>
                                        <td>${a.assetCode}</td>
                                        <td>${a.assetName}</td>
                                        <td>
                                            <input type="text"
                                                   name="assetNote_${a.assetId}"
                                                   class="form-control form-control-sm asset-note"
                                                   placeholder="Ghi chú..." disabled>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                            <small id="assetError" class="text-danger d-block mt-1"></small>
                        </div>
                        <small class="text-muted">Có thể chọn nhiều tài sản</small>
                    </div>
                    <div class="form-group">
                     <label>Lý do điều chuyển <span class="text-danger">*</span></label>
                        <textarea id="reason" name="reason" class="form-control" rows="3"
                                  placeholder="Nhập lý do điều chuyển..."></textarea>
                        <small id="reasonError" class="text-danger"></small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Tạo yêu cầu
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Transfer Modal -->
<div class="modal fade" id="editTransferModal" tabindex="-1">
    <div class="modal-dialog modal-xl modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title"><i class="fas fa-edit"></i> Chỉnh sửa điều chuyển</h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/transfers/update">
                <input type="hidden" name="transferId" id="eId">
                <div class="modal-body">

                    <!-- Loading state -->
                    <div id="editLoading" class="text-center py-4">
                        <i class="fas fa-spinner fa-spin fa-2x text-info"></i>
                        <p class="mt-2 text-muted">Đang tải dữ liệu...</p>
                    </div>

                    <div id="editContent" style="display:none;">
                        <div class="form-group">
                         <label>Phòng đi <span class="text-danger">*</span></label>
                            <select name="fromRoomId" id="eFrom" class="form-control" required>
                                <option value="">-- Chọn phòng đi --</option>
                                <c:forEach var="room" items="${rooms}">
                                    <option value="${room.roomId}">${room.roomName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                     <label>Phòng đến <span class="text-danger">*</span></label>
                            <select name="toRoomId" id="eTo" class="form-control" required>
                                <option value="">-- Chọn phòng đến --</option>
                                <c:forEach var="room" items="${rooms}">
                                    <option value="${room.roomId}">${room.roomName}</option>
                                </c:forEach>
                            </select>
                            <small id="eRoomError" class="text-danger"></small>
                        </div>

                        <!-- Danh sách tài sản -->
                        <div class="form-group">
                        <label>Chọn tài sản cần điều chuyển <span class="text-danger">*</span></label>
                            <div class="form-group">
                                <div class="input-group input-group-sm">
                                    <input type="text" id="eAssetSearch" class="form-control"
                                           placeholder="Nhập mã hoặc tên tài sản...">
                                    <div class="input-group-append">
                                        <button type="button" id="eBtnSearchAsset" class="btn btn-info">
                                            <i class="fas fa-search"></i>
                                        </button>
                                        <button type="button" id="eBtnResetAsset" class="btn btn-secondary">
                                            <i class="fas fa-redo"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="table-responsive border rounded p-2"
                                 style="max-height:300px; overflow-y:auto; overflow-x:hidden;">
                                <table class="table table-sm table-hover">
                                    <thead class="thead-light">
                                    <tr>
                                        <th width="40px"><input type="checkbox" id="eCheckAllAssets"></th>
                                        <th width="120px">Mã tài sản</th>
                                        <th>Tên tài sản</th>
                                        <th width="220px">Ghi chú</th>
                                    </tr>
                                    </thead>
                                    <tbody id="eAssetTableBody">
                                    <c:forEach var="a" items="${assets}">
                                        <tr data-asset-name="${a.assetName}" data-asset-code="${a.assetCode}">
                                            <td>
                                                <input type="checkbox" name="assetIds"
                                                       value="${a.assetId}" class="e-asset-checkbox">
                                            </td>
                                            <td>${a.assetCode}</td>
                                            <td>${a.assetName}</td>
                                            <td>
                                                <input type="text"
                                                       name="assetNote_${a.assetId}"
                                                       class="form-control form-control-sm e-asset-note"
                                                       placeholder="Ghi chú..." disabled>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                                <small id="eAssetError" class="text-danger d-block mt-1"></small>
                            </div>
                            <small class="text-muted">Có thể chọn nhiều tài sản</small>
                        </div>

                        <div class="form-group">
                       <label>Lý do <span class="text-danger">*</span></label>
                            <textarea name="reason" id="eReason" class="form-control" rows="3"
                                      placeholder="Nhập lý do điều chuyển..."></textarea>
                            <small id="eReasonError" class="text-danger"></small>
                        </div>
                    </div>

                    <div id="editError" class="alert alert-danger" style="display:none;">
                        <i class="fas fa-exclamation-circle"></i>
                        <span id="editErrorText">Không thể tải dữ liệu. Vui lòng thử lại.</span>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" id="editSubmitBtn" class="btn btn-success" disabled>
                        <i class="fas fa-save"></i> Lưu
                    </button>
                    <button type="button" id="deleteTransferBtn" class="btn btn-danger" disabled>
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                </div>
            </form>
        </div>
    </div>
</div>
                
<!-- ===================== SCRIPTS ===================== -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
    $('input[name="fromDate"]').on('change', function () {
    const fromDate = $(this).val();
    $('input[name="toDate"]').attr('min', fromDate);
});
$(document).ready(function () {
    
        $('#fromRoom').change(function () {
            const fromRoomId = $(this).val();

            // reset checkbox + note
            $('.asset-checkbox').prop('checked', false);
            $('.asset-note').prop('disabled', true).val('');

            $('#createTransferModal tbody tr').each(function () {
                const roomId = $(this).data('room-id');

                $(this).toggle(roomId == fromRoomId);
            });
        });

    // ===================== Tooltip =====================
    $('body').tooltip({ selector: '[data-toggle="tooltip"]' });

    // ===================== DataTable =====================
    $('#dataTable').DataTable({
        pageLength: 10,
        order: [[7, "desc"]],
        language: {
            lengthMenu: "Hiển thị _MENU_ phiếu mỗi trang",
            search: "Tìm kiếm:",
            paginate: { next: "Sau", previous: "Trước" }
        }
    });

    // ===================== Filter: validate date =====================
    $('#filterForm').on('submit', function (e) {
        const fromDate = $('#fromDate').val();
        const toDate   = $('#toDate').val();
        $('#dateError').hide();
        if (fromDate && toDate && new Date(toDate) < new Date(fromDate)) {
            $('#dateError').show();
            e.preventDefault();
        }
    });

    // ===================== Shared: gọi detail API =====================
    const statusMap = {
        "PENDING":   "<span class='badge badge-warning'>Chờ duyệt</span>",
        "APPROVED":  "<span class='badge badge-success'>Đã duyệt</span>",
        "COMPLETED": "<span class='badge badge-primary'>Hoàn tất</span>",
        "REJECTED":  "<span class='badge badge-danger'>Từ chối</span>"
    };

    function fetchDetail(id, onSuccess, onError) {
        $.ajax({
            url: '${pageContext.request.contextPath}/transfers/detail',
            type: 'GET',
            data: { id: id },
            dataType: 'json',
            success: onSuccess,
            error: onError
        });
    }

    // ===================== View Detail Modal =====================
    $(document).on('click', '.view-btn', function () {
        const id = $(this).data('id');

        $('#detailLoading').show();
        $('#detailContent').hide();
        $('#detailError').hide();
        $('#transferModal').modal('show');

        fetchDetail(id,
            function (res) {
                $('#mCode').text(res.transferCode    || '');
                $('#mFrom').text(res.fromRoomName    || '');
                $('#mTo').text(res.toRoomName        || '');
                $('#mUser').text(res.requestedByName || '');
                $('#mReason').text(res.reason        || '');
                $('#mDate').text(res.createdAt       || '');
                $('#mStatus').html(statusMap[res.status]
                    || "<span class='badge badge-secondary'>" + res.status + "</span>");

                const assetList = res.assetNames
                    ? res.assetNames.split(',').filter(a => a.trim() !== '')
                    : [];
                $('#mAssets').html(assetList.length
                    ? assetList.map(a =>
                        "<span class='badge badge-light border mr-1 mb-1'>" +
                        "<i class='fas fa-box text-warning'></i> " + a.trim() +
                        "</span>"
                      ).join('')
                    : "<em class='text-muted'>Không có tài sản</em>"
                );

                $('#detailLoading').hide();
                $('#detailContent').show();
            },
            function (xhr) {
                let msg = 'Không thể tải dữ liệu. Vui lòng thử lại.';
                try { msg = JSON.parse(xhr.responseText).message || msg; } catch (e) {}
                $('#detailErrorText').text(msg);
                $('#detailLoading').hide();
                $('#detailError').show();
            }
        );
    });

    $('#transferModal').on('hidden.bs.modal', function () {
        $('#detailLoading').show();
        $('#detailContent').hide();
        $('#detailError').hide();
        $('#mAssets').html('');
        $('#assetHistory').html('');
    });

// ===================== Edit Modal =====================
let editId = null;

// Checkbox toggle note — edit modal
$(document).on('change', '.e-asset-checkbox', function () {
    const note = $(this).closest('tr').find('.e-asset-note');
    if ($(this).is(':checked')) {
        note.prop('disabled', false).focus();
    } else {
        note.prop('disabled', true).val('');
    }
});

$('#eCheckAllAssets').on('change', function () {
    $('.e-asset-checkbox').prop('checked', $(this).is(':checked')).trigger('change');
});

// Asset search trong edit modal
function filterEditAssets() {
    const val = $('#eAssetSearch').val().toLowerCase();
    $('#eAssetTableBody tr').each(function () {
        const name = $(this).data('asset-name') || '';
        const code = $(this).data('asset-code') || '';
        $(this).toggle(
            name.toLowerCase().indexOf(val) > -1 ||
            code.toLowerCase().indexOf(val) > -1
        );
    });
}

$('#eBtnSearchAsset').click(filterEditAssets);
$('#eAssetSearch').keypress(function (e) {
    if (e.which === 13) { e.preventDefault(); filterEditAssets(); }
});
$('#eBtnResetAsset').click(function () {
    $('#eAssetSearch').val('');
    $('#eAssetTableBody tr').show();
});

// Mở Edit Modal
$(document).on('click', '.edit-btn', function () {
    editId = $(this).data('id');
    $('#eId').val(editId);

    // Reset UI
    $('#editLoading').show();
    $('#editContent').hide();
    $('#editError').hide();
    $('#editSubmitBtn, #deleteTransferBtn').prop('disabled', true);
    $('#eFrom').val('');
    $('#eTo').val('');
    $('#eReason').val('');
    $('#eRoomError, #eAssetError, #eReasonError').text('');
    $('#eCheckAllAssets').prop('checked', false);
    $('.e-asset-checkbox').prop('checked', false).trigger('change');
    $('#eAssetSearch').val('');
    $('#eAssetTableBody tr').show();

    $('#editTransferModal').modal('show');

    fetchDetail(editId,
        function (res) {
            $('#eFrom').val(String(res.fromRoomId));
            $('#eTo').val(String(res.toRoomId));
            $('#eReason').val(res.reason || '');

            // Tick các tài sản hiện tại của phiếu
            // API cần trả về mảng assetIds (xem ghi chú bên dưới)
            if (res.assetIds && res.assetIds.length > 0) {
                res.assetIds.forEach(function (assetId) {
                    const cb = $('.e-asset-checkbox[value="' + assetId + '"]');
                    cb.prop('checked', true).trigger('change');
                });
            }

            $('#editLoading').hide();
            $('#editContent').show();
            $('#editSubmitBtn, #deleteTransferBtn').prop('disabled', false);
        },
        function () {
            $('#editLoading').hide();
            $('#editErrorText').text('Không thể tải dữ liệu phiếu. Vui lòng thử lại.');
            $('#editError').show();
        }
    );
});

// Validation khi submit edit form
$('#editTransferModal form').on('submit', function (e) {
    let ok = true;

    const from = $('#eFrom').val(), to = $('#eTo').val();
    if (from && to && from === to) {
        $('#eRoomError').text('Phòng đến không được trùng phòng đi!');
        ok = false;
    } else {
        $('#eRoomError').text('');
    }

    if ($('.e-asset-checkbox:checked').length === 0) {
        $('#eAssetError').text('Vui lòng chọn ít nhất 1 tài sản!');
        ok = false;
    } else {
        $('#eAssetError').text('');
    }

    if ($('#eReason').val().trim() === '') {
        $('#eReasonError').text('Vui lòng nhập lý do điều chuyển!');
        ok = false;
    } else {
        $('#eReasonError').text('');
    }

    if (!ok) e.preventDefault();
});


$('#editTransferModal').on('hidden.bs.modal', function () {
    editId = null;
    $('#editLoading').show();
    $('#editContent').hide();
    $('#editError').hide();
    $('#editSubmitBtn, #deleteTransferBtn').prop('disabled', true);
});

    // ===================== Approve / Reject Modal =====================
    let approveId      = null;
    let approveVersion = null;

    $(document).on('click', '.approve-open-btn', function () {
        approveId      = $(this).data('id');
        approveVersion = null;

        // Reset modal
        $('#approveCode').html("<i class='fas fa-spinner fa-spin text-primary'></i> Đang tải...");
        $('#approveErrorMsg').addClass('d-none');
        $('#confirmApproveBtn, #confirmRejectBtn').prop('disabled', true);

        $('#approveModal').modal('show');

        // Gọi detail API để lấy version + transferCode mới nhất
        fetchDetail(approveId,
            function (res) {
                approveVersion = res.version;
                $('#approveCode').text(res.transferCode || '');

                // Nếu status không còn PENDING → báo lỗi, giữ disabled
                if (res.status !== 'PENDING') {
                    showApproveError('Phiếu này không còn ở trạng thái chờ duyệt. Vui lòng tải lại trang.');
                    return;
                }

                resetModalButtons(); // enable nút khi data hợp lệ
            },
            function () {
                $('#approveCode').text('');
                showApproveError('Không thể tải dữ liệu phiếu. Vui lòng đóng và thử lại.');
            }
        );
    });

    $('#confirmApproveBtn').click(function () {
        handleApproveAction('${pageContext.request.contextPath}/transfers/approve', 'approve');
    });

    $('#confirmRejectBtn').click(function () {
        handleApproveAction('${pageContext.request.contextPath}/transfers/reject', 'reject');
    });

    function handleApproveAction(url, type) {
        if (!approveId || approveVersion === null) return;

        $('#confirmApproveBtn, #confirmRejectBtn').prop('disabled', true);
        if (type === 'approve') {
            $('#confirmApproveBtn').html("<i class='fas fa-spinner fa-spin'></i> Đang xử lý...");
        } else {
            $('#confirmRejectBtn').html("<i class='fas fa-spinner fa-spin'></i> Đang xử lý...");
        }

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                id:      approveId,
                version: approveVersion  // version lấy từ API, luôn mới nhất
            },
            dataType: 'json',
            success: function (res) {
                if (res.success) {
                    $('#approveModal').modal('hide');
                    window.location.href = '${pageContext.request.contextPath}/transfers/list';
                } else {
                    showApproveError(res.message || 'Thao tác thất bại. Vui lòng thử lại.');
                    resetModalButtons();
                }
            },
            error: function (xhr) {
                let msg = 'Có lỗi xảy ra. Vui lòng thử lại.';
                try { msg = JSON.parse(xhr.responseText).message || msg; } catch (e) {}
                if (xhr.status === 409) {
                    msg = 'Phiếu đã được xử lý bởi người khác. Vui lòng tải lại trang.';
                }
                showApproveError(msg);
                resetModalButtons();
            }
        });
    }

    function showApproveError(msg) {
        $('#approveErrorText').text(msg);
        $('#approveErrorMsg').removeClass('d-none');
    }

    function resetModalButtons() {
        $('#confirmApproveBtn').prop('disabled', false).html("<i class='fas fa-check'></i> Phê duyệt");
        $('#confirmRejectBtn').prop('disabled', false).html("<i class='fas fa-ban'></i> Từ chối");
    }

    $('#approveModal').on('hidden.bs.modal', function () {
        approveId      = null;
        approveVersion = null;
        $('#approveErrorMsg').addClass('d-none');
        resetModalButtons();
    });

    // ===================== Asset checkbox toggle note =====================
    $(document).on('change', '.asset-checkbox', function () {
        const note = $(this).closest('tr').find('.asset-note');
        if ($(this).is(':checked')) {
            note.prop('disabled', false).focus();
        } else {
            note.prop('disabled', true).val('');
        }
    });

    $('#checkAllAssets').on('change', function () {
        $('.asset-checkbox').prop('checked', $(this).is(':checked')).trigger('change');
    });

    // ===================== Create Transfer Form validation =====================
    function validateRooms() {
        const from = $('#fromRoom').val(), to = $('#toRoom').val();
        if (from && to && from === to) {
            $('#roomError').text('Phòng đến không được trùng phòng đi!');
            return false;
        }
        $('#roomError').text('');
        return true;
    }

    function validateAssets() {
        if ($('.asset-checkbox:checked').length === 0) {
            $('#assetError').text('Vui lòng chọn ít nhất 1 tài sản!');
            return false;
        }
        $('#assetError').text('');
        return true;
    }

    function validateReason() {
        if ($('#reason').val().trim() === '') {
            $('#reasonError').text('Vui lòng nhập lý do điều chuyển!');
            return false;
        }
        $('#reasonError').text('');
        return true;
    }

    $('#fromRoom, #toRoom').on('change', function () { validateRooms(); });

    $('#createTransferForm').on('submit', function (e) {
        const ok = validateRooms() & validateAssets() & validateReason();
        if (!ok) e.preventDefault();
    });

    // ===================== Asset Search in Create Modal =====================
    function filterAssets() {
       const val = $('#assetSearch').val().toLowerCase();
       const fromRoomId = $('#fromRoom').val();

       $('#createTransferModal tbody tr').each(function () {
           const text = $(this).text().toLowerCase();
           const roomId = $(this).data('room-id');

           const matchSearch = text.indexOf(val) > -1;
           const matchRoom = roomId == fromRoomId;

           $(this).toggle(matchSearch && matchRoom);
       });
   }

    $('#fromRoom').change(function () {
        const fromRoomId = $(this).val();

        // reset checkbox + note
        $('.asset-checkbox').prop('checked', false);
        $('.asset-note').prop('disabled', true).val('');

        $('#createTransferModal tbody tr').each(function () {
            const roomId = $(this).data('room-id');

            $(this).toggle(roomId == fromRoomId);
        });
    });

    $('#btnSearchAsset').click(filterAssets);
    $('#assetSearch').keypress(function (e) {
        if (e.which === 13) { e.preventDefault(); filterAssets(); }
    });
    $('#btnResetAsset').click(function () {
       $('#assetSearch').val('');
       $('#fromRoom').trigger('change'); // re-filter theo room
   });


    $('#createTransferModal').on('shown.bs.modal', function () {
      $('#assetSearch').val('');
      $('#fromRoom').val('');
      $('#createTransferModal tbody tr').hide();
  });
$(document).on('change', '.asset-checkbox', function () {
    const note = $(this).closest('tr').find('.asset-note');
    note.prop('disabled', !this.checked);

    if (!this.checked) note.val('');
});
$('#deleteTransferBtn').click(function () {
    if (!editId) return;

    if (!confirm('Bạn có chắc muốn xóa phiếu này không?')) return;

    $.ajax({
        url: '${pageContext.request.contextPath}/transfers/delete',
        type: 'POST',
        data: { id: editId },
        success: function () {
            $('#editTransferModal').modal('hide');
            window.location.href = '${pageContext.request.contextPath}/transfers/list';
        },
        error: function () {
            alert('Xóa thất bại!');
        }
    });
});
});

</script>
</body>
</html>