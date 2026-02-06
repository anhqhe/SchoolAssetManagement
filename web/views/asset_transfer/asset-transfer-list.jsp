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

                <!-- PAGE TITLE -->
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-exchange-alt text-primary"></i> Danh sách điều chuyển tài sản
                    </h1>
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
                                <option value="PENDING">Chờ duyệt</option>
                                <option value="APPROVED">Đã duyệt</option>
                                <option value="COMPLETED">Hoàn tất</option>
                                <option value="REJECTED">Từ chối</option>
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
                                            <td colspan="8" class="text-center text-muted">
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
                                                <td>${t.reason}</td>
                                                <td>
                                                    <span class="badge ${t.statusBadgeClass}">
                                                        ${t.statusText}
                                                    </span>
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
                                                data-status="${t.statusText}"
                                                data-date="${t.createdAt}">
                                            <i class="fas fa-eye"></i>
                                        </button>

                                        <button class="btn btn-sm btn-success approve-open-btn"
                                                data-id="${t.transferId}"
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
<!-- Transfer Detail  -->
<div class="modal fade" id="transferModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">Chi tiết phiếu điều chuyển</h5>
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
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="approveModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title">
                    <i class="fas fa-check-circle"></i> Xác nhận phê duyệt
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body text-center">
                <p>Bạn có chắc muốn phê duyệt phiếu</p>
                <h5 id="approveCode" class="font-weight-bold text-primary"></h5>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                <button id="confirmApproveBtn" class="btn btn-success">
                    <i class="fas fa-check"></i> Đồng ý
                </button>
            </div>
        </div>
    </div>
</div>


<!-- SCRIPTS -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
$(document).ready(function() {
    $('#dataTable').DataTable({
        "pageLength": 10,
        "order": [[6, "desc"]],
        "language": {
            "lengthMenu": "Hiển thị _MENU_ phiếu mỗi trang",
            "search": "Tìm kiếm:",
            "paginate": { "next": "Sau", "previous": "Trước" }
        }
    });
});

$(document).on("click", ".view-btn", function () {
    $("#mCode").text($(this).data("code"));
    $("#mFrom").text($(this).data("from"));
    $("#mTo").text($(this).data("to"));
    $("#mUser").text($(this).data("user"));
    $("#mReason").text($(this).data("reason"));
    $("#mStatus").text($(this).data("status"));
    $("#mDate").text($(this).data("date"));

    $("#transferModal").modal("show");
});

$(document).on("click", ".approve-open-btn", function () {
    approveId = $(this).data("id");
    approveRow = $(this).closest("tr");

    $("#approveCode").text($(this).data("code"));
    $("#approveModal").modal("show");
});

$("#confirmApproveBtn").click(function () {
    $.post("${pageContext.request.contextPath}/transfers/approve", { id: approveId }, function () {

        // cập nhật UI
        approveRow.find(".badge")
            .removeClass()
            .addClass("badge badge-success")
            .text("Đã duyệt");

        approveRow.find(".approve-open-btn").prop("disabled", true);

        $("#approveModal").modal("hide");
    });
});
</script>

</body>
</html>
