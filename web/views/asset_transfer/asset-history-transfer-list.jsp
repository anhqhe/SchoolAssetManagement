<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử điều chuyển tài sản</title>
    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
    <style>
        .asset-header {
            background: linear-gradient(135deg, #f8f9fc, #eaecf4);
            border-left: 4px solid #f6c23e;
            padding: 10px 16px;
            border-radius: 4px 4px 0 0;
        }
        .history-row:last-child { border-bottom: none !important; }
        .history-row:hover { background-color: #f8f9fc; }
        .transfer-code {
            font-family: monospace;
            font-size: 0.85rem;
            color: #4e73df;
            font-weight: 600;
        }
        .room-tag {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 2px 10px;
            border-radius: 12px;
            font-size: 0.82rem;
            font-weight: 500;
        }
        .room-from { background-color: #fde8e8; color: #c0392b; }
        .room-to   { background-color: #e8f8f0; color: #1e8449; }
        .page-link { color: #4e73df; }
        .page-item.active .page-link {
            background-color: #4e73df;
            border-color: #4e73df;
        }
    </style>
</head>

<body id="page-top">
<div id="wrapper">

    <%@ include file="/views/layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="/views/layout/topbar.jsp" %>

            <div class="container-fluid">

                <!-- TITLE -->
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-history text-primary"></i>
                        Lịch sử điều chuyển tài sản
                    </h1>
                    <span class="badge badge-primary badge-pill px-3 py-2" style="font-size:0.9rem;">
                        Tổng: ${totalItems} tài sản
                    </span>
                </div>

                <!-- FILTER -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-filter"></i> Tìm kiếm & Lọc
                        </h6>
                    </div>
                    <div class="card-body">
                        <form method="get"
                              action="${pageContext.request.contextPath}/asset-history-transfer/list">
                            <div class="row align-items-end">
                                <div class="col-md-4 mb-2">
                                    <label class="small text-muted mb-1">Tên hoặc mã tài sản</label>
                                    <input type="text" name="keyword" value="${keyword}"
                                           class="form-control"
                                           placeholder="Nhập tên hoặc mã tài sản...">
                                </div>
                                <div class="col-md-2 mb-2">
                                    <label class="small text-muted mb-1">Từ ngày</label>
                                    <input type="date" name="fromDate" value="${fromDate}"
                                           class="form-control">
                                </div>
                                <div class="col-md-2 mb-2">
                                    <label class="small text-muted mb-1">Đến ngày</label>
                                    <input type="date" name="toDate" value="${toDate}"
                                           class="form-control">
                                </div>
                                <div class="col-md-2 mb-2">
                                    <label class="small text-muted mb-1">Tài sản mỗi trang</label>
                                    <select name="pageSize" class="form-control">
                                        <option value="5"  ${pageSize == 5  ? 'selected' : ''}>5</option>
                                        <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                        <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                        <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                    </select>
                                </div>
                                <div class="col-md-2 mb-2 d-flex align-items-end">
                                    <button type="submit" class="btn btn-primary mr-2">
                                        <i class="fas fa-search"></i> Tìm
                                    </button>
                                    <a href="${pageContext.request.contextPath}/asset-history-transfer/list"
                                       class="btn btn-secondary">
                                        <i class="fas fa-redo"></i>
                                    </a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- HISTORY LIST -->
                <c:choose>
                    <c:when test="${empty historyList}">
                        <div class="card shadow">
                            <div class="card-body text-center text-muted py-5">
                                <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                Không có dữ liệu lịch sử điều chuyển
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="asset" items="${historyList}">
                            <div class="card shadow mb-3">

                                <!-- ASSET HEADER -->
                                <div class="asset-header d-flex align-items-center justify-content-between">
                                    <span class="font-weight-bold" style="color:#856404;">
                                        <i class="fas fa-box text-warning mr-1"></i>
                                        ${asset.label}
                                    </span>
                                    <span class="badge badge-warning badge-pill">
                                        ${asset.transfers.size()} lần điều chuyển
                                    </span>
                                </div>

                                <!-- HISTORY ROWS -->
                                <div class="card-body p-0">
                                    <c:forEach var="h" items="${asset.transfers}">
                                        <div class="history-row d-flex align-items-center border-bottom px-3 py-2">

                                            <!-- DATE -->
                                            <div style="min-width:160px;" class="text-muted small">
                                                <i class="fas fa-clock mr-1"></i>
                                                <fmt:formatDate value="${h.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </div>

                                            <!-- FROM -> TO -->
                                            <div class="flex-grow-1 d-flex align-items-center" style="gap:8px; flex-wrap:wrap;">
                                                <span class="room-tag room-from">
                                                    <i class="fas fa-sign-out-alt"></i> ${h.fromRoomName}
                                                </span>
                                                <i class="fas fa-long-arrow-alt-right text-muted"></i>
                                                <span class="room-tag room-to">
                                                    <i class="fas fa-sign-in-alt"></i> ${h.toRoomName}
                                                </span>
                                            </div>

                                       <!-- TRANSFER CODE -->
                                            <div class="transfer-code" style="min-width:190px; text-align:right;">
                                                <span class="view-transfer-btn"
                                                      data-id="${h.transferId}"
                                                      style="cursor:pointer;"
                                                      title="Xem chi tiết phiếu">
                                                    <i class="fas fa-file-alt mr-1 text-muted"></i>
                                                    ${h.transferCode}
                                                </span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <!-- PAGINATION -->
                <c:if test="${totalPages >= 1}">
                    <div class="card shadow mt-2 mb-4">
                        <div class="card-body py-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <small class="text-muted">
                                    Trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                                    &nbsp;|&nbsp; Tổng <strong>${totalItems}</strong> tài sản
                                </small>
                                <nav>
                                    <ul class="pagination pagination-sm mb-0">

                                        <!-- Trước -->
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="?page=${currentPage - 1}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">
                                                <i class="fas fa-chevron-left"></i>
                                            </a>
                                        </li>

                                        <!-- Trang đầu + ellipsis -->
                                        <c:set var="start" value="${currentPage - 2 > 1 ? currentPage - 2 : 1}"/>
                                        <c:set var="end"   value="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}"/>

                                        <c:if test="${start > 1}">
                                            <li class="page-item">
                                                <a class="page-link" href="?page=1&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">1</a>
                                            </li>
                                            <c:if test="${start > 2}">
                                                <li class="page-item disabled"><span class="page-link">…</span></li>
                                            </c:if>
                                        </c:if>

                                        <!-- Số trang -->
                                        <c:forEach begin="${start}" end="${end}" var="i">
                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <a class="page-link"
                                                   href="?page=${i}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">
                                                    ${i}
                                                </a>
                                            </li>
                                        </c:forEach>

                                        <!-- Trang cuối + ellipsis -->
                                        <c:if test="${end < totalPages}">
                                            <c:if test="${end < totalPages - 1}">
                                                <li class="page-item disabled"><span class="page-link">…</span></li>
                                            </c:if>
                                            <li class="page-item">
                                                <a class="page-link" href="?page=${totalPages}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">${totalPages}</a>
                                            </li>
                                        </c:if>

                                        <!-- Sau -->
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="?page=${currentPage + 1}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">
                                                <i class="fas fa-chevron-right"></i>
                                            </a>
                                        </li>

                                    </ul>
                                </nav>
                            </div>
                        </div>
                    </div>
                </c:if>

            </div>
        </div>
        <%@ include file="/views/layout/footer.jsp" %>
    </div>
</div>
    <!-- Transfer Detail Modal -->
<div class="modal fade" id="historyDetailModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">
                    <i class="fas fa-info-circle"></i> Chi tiết phiếu điều chuyển
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">

                <!-- Loading -->
                <div id="hdLoading" class="text-center py-4">
                    <i class="fas fa-spinner fa-spin fa-2x text-primary"></i>
                    <p class="mt-2 text-muted">Đang tải dữ liệu...</p>
                </div>

                <!-- Content -->
                <div id="hdContent" style="display:none;">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>Mã phiếu:</strong> <span id="hdCode" class="text-primary font-weight-bold"></span></p>
                            <p><strong>Phòng đi:</strong> <span id="hdFrom"></span></p>
                            <p><strong>Phòng đến:</strong> <span id="hdTo"></span></p>
                            <p><strong>Người yêu cầu:</strong> <span id="hdUser"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Trạng thái:</strong> <span id="hdStatus"></span></p>
                            <p><strong>Ngày tạo:</strong> <span id="hdDate"></span></p>
                            <p><strong>Lý do:</strong> <span id="hdReason"></span></p>
                        </div>
                    </div>
                    <hr>
                    <strong>Tài sản:</strong>
                    <div id="hdAssets" class="mt-2"></div>
                </div>

                <!-- Error -->
                <div id="hdError" class="alert alert-danger" style="display:none;">
                    <i class="fas fa-exclamation-circle"></i>
                    <span id="hdErrorText">Không thể tải dữ liệu. Vui lòng thử lại.</span>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>
    <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<script>
$(document).ready(function () {

    const statusMap = {
        "PENDING":   "<span class='badge badge-warning'>Chờ duyệt</span>",
        "APPROVED":  "<span class='badge badge-success'>Đã duyệt</span>",
        "COMPLETED": "<span class='badge badge-primary'>Hoàn tất</span>",
        "REJECTED":  "<span class='badge badge-danger'>Từ chối</span>"
    };

    $(document).on('click', '.view-transfer-btn', function () {
        const id = $(this).data('id');
        if (!id) return;

        // Reset modal
        $('#hdLoading').show();
        $('#hdContent').hide();
        $('#hdError').hide();
        $('#historyDetailModal').modal('show');

        $.ajax({
            url: '${pageContext.request.contextPath}/transfers/detail',
            type: 'GET',
            data: { id: id },
            dataType: 'json',
            success: function (res) {
                $('#hdCode').text(res.transferCode    || '');
                $('#hdFrom').text(res.fromRoomName    || '');
                $('#hdTo').text(res.toRoomName        || '');
                $('#hdUser').text(res.requestedByName || '');
                $('#hdReason').text(res.reason        || '');
                $('#hdDate').text(res.createdAt       || '');
                $('#hdStatus').html(statusMap[res.status]
                    || "<span class='badge badge-secondary'>" + res.status + "</span>");

                const assetList = res.assetNames
                    ? res.assetNames.split(',').filter(a => a.trim() !== '')
                    : [];
                $('#hdAssets').html(assetList.length
                    ? assetList.map(a =>
                        "<span class='badge badge-light border mr-1 mb-1'>" +
                        "<i class='fas fa-box text-warning'></i> " + a.trim() +
                        "</span>"
                      ).join('')
                    : "<em class='text-muted'>Không có tài sản</em>"
                );

                $('#hdLoading').hide();
                $('#hdContent').show();
            },
            error: function (xhr) {
                let msg = 'Không thể tải dữ liệu. Vui lòng thử lại.';
                try { msg = JSON.parse(xhr.responseText).message || msg; } catch (e) {}
                $('#hdErrorText').text(msg);
                $('#hdLoading').hide();
                $('#hdError').show();
            }
        });
    });

    $('#historyDetailModal').on('hidden.bs.modal', function () {
        $('#hdLoading').show();
        $('#hdContent').hide();
        $('#hdError').hide();
        $('#hdAssets').html('');
    });

});
$('input[name="fromDate"]').on('change', function () {
    const fromDate = $(this).val();
    $('input[name="toDate"]').attr('min', fromDate);
});
</script>
</body>
</html>