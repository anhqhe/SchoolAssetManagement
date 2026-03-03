<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                        <form method="get" action="${pageContext.request.contextPath}/transfers/list" class="form-inline">
                            <input type="text" name="keyword" class="form-control mr-3 mb-2"
                                   placeholder="Mã phiếu, lý do..."
                                   value="${keyword}">

                      <select name="status" class="form-control mr-3 mb-2">
                        <option value="">-- Tất cả trạng thái --</option>
                        <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
                        <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                        <option value="COMPLETED" ${selectedStatus == 'COMPLETED' ? 'selected' : ''}>Hoàn tất</option>
                        <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Từ chối</option>
                    </select>

                            <button class="btn btn-primary mb-2 mr-2">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>

                            <a href="${pageContext.request.contextPath}/transfers/list" class="btn btn-secondary mb-2">
                                <i class="fas fa-redo"></i> Đặt lại
                            </a>
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
                                    <td><strong>${t.transferCode}</strong></td>
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
                                 <td>${t.createdAt}</td>
                                    <td class="text-center">
                                        <button class="btn btn-sm btn-warning view-btn"
                                                data-id="${t.transferId}"
                                                data-code="${t.transferCode}"
                                                data-from="${t.fromRoomName}"
                                                data-to="${t.toRoomName}"
                                                data-user="${t.requestedByName}"
                                                data-reason="${t.reason}"
                                                data-status="${t.status}"
                                                data-date="${t.createdAt}"
                                                data-assets="${not empty t.assetNames ? t.assetNames : ''}"> 
                                            <i class="fas fa-eye"></i>
                                        </button>

                                        <button class="btn btn-sm btn-success approve-open-btn"
                                                data-id="${t.transferId}"
                                                data-code="${t.transferCode}"
                                                ${t.status == 'APPROVED' || t.status == 'COMPLETED' ? 'disabled' : ''}>
                                            <i class="fas fa-check"></i>
                                        </button>
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

<!-- Transfer Detail Modal -->
<div class="modal fade" id="transferModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-info-circle"></i> Chi tiết phiếu điều chuyển</h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <p><strong>Mã phiếu:</strong> <span id="mCode"></span></p>
                <p><strong>Phòng đi:</strong> <span id="mFrom"></span></p>
                <p><strong>Phòng đến:</strong> <span id="mTo"></span></p>
                <p><strong>Người yêu cầu:</strong> <span id="mUser"></span></p>
                <p><strong>Lý do:</strong> <span id="mReason"></span></p>
                <p><strong>Trạng thái:</strong> <span id="mStatus"></span></p>
                <p><strong>Ngày tạo:</strong> <span id="mDate"></span></p>
                <div>
                    <strong>Tài sản:</strong>
                    <div id="mAssets" class="mt-1"></div>
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
                <h5 class="modal-title">
                    <i class="fas fa-tasks"></i> Xử lý phiếu điều chuyển
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body text-center py-4">
                <p class="mb-1 text-muted">Phiếu điều chuyển</p>
                <h5 id="approveCode" class="font-weight-bold text-primary mb-3"></h5>

                <p class="mb-0">Bạn muốn <strong>phê duyệt</strong> hay <strong>từ chối</strong> phiếu này?</p>

                <!-- Error message (ẩn mặc định) -->
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


<!-- CREATE TRANSFER MODAL -->
<div class="modal fade" id="createTransferModal" tabindex="-1">
    <div class="modal-dialog modal-xl modal-dialog-centered custom-transfer-modal">
        <div class="modal-content">

            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">
                    <i class="fas fa-exchange-alt"></i>
                    Tạo yêu cầu điều chuyển
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>

            <form id="createTransferForm"
                  method="post"
                  action="${pageContext.request.contextPath}/transfers/create">
                
                <div class="modal-body">

                    <div class="form-group">
                        <label>Phòng đi</label>
                        <select id="fromRoom" name="fromRoomId" class="form-control" required>
                            <option value="">-- Chọn phòng đi --</option>
                            <c:forEach var="room" items="${rooms}">
                                <option value="${room.roomId}">${room.roomName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Phòng đến</label>
                        <select id="toRoom" name="toRoomId" class="form-control" required>
                            <option value="">-- Chọn phòng đến --</option>
                            <c:forEach var="room" items="${rooms}">
                                <option value="${room.roomId}">${room.roomName}</option>
                            </c:forEach>
                        </select>
                        <small id="roomError" class="text-danger"></small>
                    </div>

                    <div class="form-group">
                        <label>Chọn tài sản cần điều chuyển</label>

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
                             style="max-height:250px; overflow-y:auto; overflow-x:hidden;">
                            <table class="table table-sm table-hover">
                                <thead class="thead-light">
                                    <tr>
                                        <th width="40px">
                                            <input type="checkbox" id="checkAllAssets">
                                        </th>
                                        <th>Mã tài sản</th>
                                        <th>Tên tài sản</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="a" items="${assets}">
                                        <tr>
                                            <td>
                                                <input type="checkbox" name="assetIds"
                                                       value="${a.assetId}" class="asset-checkbox">
                                            </td>
                                            <td>${a.assetCode}</td>
                                            <td>${a.assetName}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <small id="assetError" class="text-danger d-block mt-1"></small>
                        </div>

                        <small class="text-muted">Có thể chọn nhiều tài sản</small>
                    </div>

                    <div class="form-group">
                        <label>Lý do điều chuyển</label>
                        <textarea name="reason" class="form-control" rows="3"
                                  placeholder="Nhập lý do điều chuyển..." required></textarea>
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

<!-- SCRIPTS -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
$(document).ready(function () {

    // === DataTable ===
    $('#dataTable').DataTable({
        "pageLength": 10,
        "order": [[7, "desc"]],
        "language": {
            "lengthMenu": "Hiển thị _MENU_ phiếu mỗi trang",
            "search": "Tìm kiếm:",
            "paginate": { "next": "Sau", "previous": "Trước" }
        }
    });

    // === View Detail Modal ===
    let statusMap = {
        "PENDING":   "<span class='badge badge-warning'>Chờ duyệt</span>",
        "APPROVED":  "<span class='badge badge-success'>Đã duyệt</span>",
        "COMPLETED": "<span class='badge badge-primary'>Hoàn tất</span>",
        "REJECTED":  "<span class='badge badge-danger'>Từ chối</span>"
    };

    $(document).on("click", ".view-btn", function () {
        $("#mCode").text($(this).data("code"));
        $("#mFrom").text($(this).data("from"));
        $("#mTo").text($(this).data("to"));
        $("#mUser").text($(this).data("user"));
        $("#mReason").text($(this).data("reason"));
        $("#mDate").text($(this).data("date"));

        let rawStatus = $(this).data("status");
        $("#mStatus").html(statusMap[rawStatus] || "<span class='badge badge-secondary'>" + rawStatus + "</span>");

        let assets = $(this).data("assets") || "";
        let assetList = assets ? assets.split(",") : [];
        let html = assetList
            .filter(a => a.trim() !== "")
            .map(a =>
                "<span class='badge badge-light border mr-1 mb-1'>" +
                "<i class='fas fa-box text-warning'></i> " + a.trim() +
                "</span>"
            ).join("");
        $("#mAssets").html(html || "<em class='text-muted'>Không có tài sản</em>");

        $("#transferModal").modal("show");
    });

// === Approve / Reject Modal ===
let approveId = null;

$(document).on("click", ".approve-open-btn", function () {
    approveId = $(this).data("id");
    $("#approveCode").text($(this).data("code"));

    // Reset trạng thái modal
    $("#approveErrorMsg").addClass("d-none");
    $("#approveErrorText").text("");
    $("#confirmApproveBtn").prop("disabled", false).html("<i class='fas fa-check'></i> Phê duyệt");
    $("#confirmRejectBtn").prop("disabled", false).html("<i class='fas fa-ban'></i> Từ chối");

    $("#approveModal").modal("show");
});

// Phê duyệt: PENDING → APPROVED
$("#confirmApproveBtn").click(function () {
    handleAction("${pageContext.request.contextPath}/transfers/approve", "approve");
});

// Từ chối: PENDING → REJECTED
$("#confirmRejectBtn").click(function () {
    handleAction("${pageContext.request.contextPath}/transfers/reject", "reject");
});

function handleAction(url, type) {
    if (!approveId) return;

    let $approveBtn = $("#confirmApproveBtn");
    let $rejectBtn  = $("#confirmRejectBtn");

    // Disable cả 2 nút, hiện spinner trên nút đang click
    $approveBtn.prop("disabled", true);
    $rejectBtn.prop("disabled", true);

    if (type === "approve") {
        $approveBtn.html("<i class='fas fa-spinner fa-spin'></i> Đang xử lý...");
    } else {
        $rejectBtn.html("<i class='fas fa-spinner fa-spin'></i> Đang xử lý...");
    }

    $.ajax({
        url: url,
        type: "POST",
        data: { id: approveId },
        dataType: "json",
        success: function (res) {
            if (res.success) {
                $("#approveModal").modal("hide");
                window.location.href = "${pageContext.request.contextPath}/transfers/list";
            } else {
                showModalError(res.message || "Thao tác thất bại. Vui lòng thử lại.");
                resetModalButtons();
            }
        },
        error: function (xhr) {
            let msg = "Có lỗi xảy ra. Vui lòng thử lại.";
            try { msg = JSON.parse(xhr.responseText).message || msg; } catch (e) {}
            showModalError(msg);
            resetModalButtons();
        }
    });
}

function showModalError(msg) {
    $("#approveErrorText").text(msg);
    $("#approveErrorMsg").removeClass("d-none");
}

function resetModalButtons() {
    $("#confirmApproveBtn").prop("disabled", false).html("<i class='fas fa-check'></i> Phê duyệt");
    $("#confirmRejectBtn").prop("disabled", false).html("<i class='fas fa-ban'></i> Từ chối");
}

$("#approveModal").on("hidden.bs.modal", function () {
    approveId = null;
    $("#approveErrorMsg").addClass("d-none");
    resetModalButtons();
});

    // === Create Transfer Form ===
    function validateRooms() {
        let from = $("#fromRoom").val();
        let to = $("#toRoom").val();
        if (from && to && from === to) {
            $("#roomError").text("Phòng đến không được trùng phòng đi!");
            return false;
        }
        $("#roomError").text("");
        return true;
    }

    function validateAssets() {
        if ($(".asset-checkbox:checked").length === 0) {
            $("#assetError").text("Vui lòng chọn ít nhất 1 tài sản!");
            return false;
        }
        $("#assetError").text("");
        return true;
    }

    $("#fromRoom, #toRoom").change(function () {
        validateRooms();
    });

    $("#checkAllAssets").click(function () {
        $(".asset-checkbox").prop("checked", this.checked);
    });

    $("#createTransferForm").submit(function (e) {
        let isRoomValid = validateRooms();
        let isAssetValid = validateAssets();
        if (!isRoomValid || !isAssetValid) {
            e.preventDefault();
            return false;
        }
    });

    // === Asset Search ===
    function filterAssets() {
        let value = $("#assetSearch").val().toLowerCase();
        $("#createTransferModal table tbody tr").each(function () {
            let rowText = $(this).text().toLowerCase();
            $(this).toggle(rowText.indexOf(value) > -1);
        });
    }

    $("#btnSearchAsset").click(function () {
        filterAssets();
    });

    $("#assetSearch").keypress(function (e) {
        if (e.which === 13) {
            e.preventDefault();
            filterAssets();
        }
    });

    $("#btnResetAsset").click(function () {
        $("#assetSearch").val("");
        $("#createTransferModal table tbody tr").show();
    });

    // Reset modal khi mở lại
    $('#createTransferModal').on('shown.bs.modal', function () {
        $("#assetSearch").val("");
        $("#createTransferModal tbody tr").show();
    });

});
</script>

</body>
</html>
