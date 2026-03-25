<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Set role flags -->
<c:set var="isTeacher" value="false"/>
<c:set var="isStaff" value="false"/>
<c:set var="isBoard" value="false"/>
<c:forEach var="role" items="${sessionScope.currentUser.roles}">
    <c:if test="${role eq 'TEACHER'}"><c:set var="isTeacher" value="true"/></c:if>
    <c:if test="${role eq 'ASSET_STAFF'}"><c:set var="isStaff" value="true"/></c:if>
    <c:if test="${role eq 'BOARD'}"><c:set var="isBoard" value="true"/></c:if>
</c:forEach>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch sử cấp phát</title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
    </head>
    <body id="page-top">
        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">

                    <%@ include file="/views/layout/allocation/topbar2.jsp" %>

                    <div class="container-fluid">

                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-history text-primary"></i>
                                Lịch Sử Cấp Phát
                            </h1>
                        </div>

                        <!-- Filter Card -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-filter"></i> Tìm Kiếm & Lọc
                                </h6>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${isTeacher}">
                                        <form action="${pageContext.request.contextPath}/teacher/allocation-history" method="get" id="filterForm">
                                        </c:when>
                                        <c:when test="${isStaff}">
                                            <form action="${pageContext.request.contextPath}/staff/allocation-history" method="get" id="filterForm">
                                            </c:when>
                                            <c:otherwise>
                                                <form action="${pageContext.request.contextPath}/board/allocation-history" method="get" id="filterForm">
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="form-row align-items-end">

                                                <!-- Keyword -->
                                                <div class="form-group col-md-3">
                                                    <label>Tìm kiếm</label>
                                                    <input type="text"
                                                           name="keyword"
                                                           class="form-control"
                                                           placeholder="Tìm kiếm tài sản..."
                                                           value="${keyword}">
                                                </div>

                                                <!-- Status -->
                                                <div class="form-group col-md-2">
                                                    <label>Trạng thái</label>
                                                    <select name="status" class="form-control">
                                                        <option value="">-- Tất cả --</option>
                                                        <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Đã cấp phát</option>
                                                        <option value="RETURNED" ${status == 'RETURNED' ? 'selected' : ''}>Đã thu hồi</option>
                                                        <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>Hoàn tất</option>
                                                    </select>
                                                </div>

                                                <!-- Date From -->
                                                <div class="form-group col-md-2">
                                                    <label>Từ ngày</label>
                                                    <input type="date" name="dateFrom" class="form-control" value="${dateFrom}">
                                                </div>

                                                <!-- Date To -->
                                                <div class="form-group col-md-2">
                                                    <label>Đến ngày</label>
                                                    <input type="date" name="dateTo" class="form-control" value="${dateTo}">
                                                </div>

                                                <!-- Buttons -->
                                                <div class="form-group col-md-3">
                                                    <button type="submit" class="btn btn-primary mr-2">
                                                        <i class="fas fa-search"></i> Tìm kiếm
                                                    </button>

                                                    <c:choose>
                                                        <c:when test="${isTeacher}">
                                                            <a href="${pageContext.request.contextPath}/teacher/allocation-history"
                                                               class="btn btn-outline-secondary">
                                                                <i class="fas fa-redo"></i> Đặt lại
                                                            </a>
                                                        </c:when>
                                                        <c:when test="${isStaff}">
                                                            <a href="${pageContext.request.contextPath}/staff/allocation-history"
                                                               class="btn btn-outline-secondary">
                                                                <i class="fas fa-redo"></i> Đặt lại
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${pageContext.request.contextPath}/board/allocation-history"
                                                               class="btn btn-outline-secondary">
                                                                <i class="fas fa-redo"></i> Đặt lại
                                                            </a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                            </div>
                                        </form>
                                        </div>
                                        </div>

                                        <!-- History Table -->
                                        <div class="card shadow mb-4">
                                            <div class="card-header py-3">
                                                <h6 class="m-0 font-weight-bold text-primary">
                                                    <i class="fas fa-list"></i> Danh sách cấp phát
                                                    <span class="badge badge-primary">${historyList != null ? historyList.size() : 0}</span>
                                                </h6>
                                            </div>
                                            <div class="card-body">
                                                <div class="table-responsive">
                                                    <table class="table table-bordered table-hover" id="dataTable" width="100%" cellspacing="0">
                                                        <thead class="thead-light">
                                                            <tr>
                                                                <th>Mã cấp phát</th>
                                                                <th>Tài sản</th>
                                                                <th>Phòng xuất</th>
                                                                <th>Phòng nhận</th>
                                                                <th>Người nhận</th>
                                                                <th>Người cấp phát</th>
                                                                <th>Ngày cấp phát</th>
                                                                <th>Trạng thái</th>
                                                                <th>Thao tác</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:choose>
                                                                <c:when test="${empty historyList}">
                                                                    <tr>
                                                                        <td colspan="9" class="text-center text-muted py-5">
                                                                            <i class="fas fa-inbox fa-3x mb-3"></i>
                                                                            <p>Chưa có dữ liệu cấp phát</p>
                                                                        </td>
                                                                    </tr>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:forEach var="h" items="${historyList}">
                                                                        <tr>
                                                                            <td><strong>${h.allocationCode}</strong></td>
                                                                            <td>
                                                                                <small class="text-muted">
                                                                                    <i class="fas fa-box text-warning"></i>
                                                                                    ${not empty h.assetNames ? h.assetNames : '-'}
                                                                                </small>
                                                                            </td>
                                                                            <td>${h.fromRoomName}</td>
                                                                            <td>${h.toRoomName}</td>
                                                                            <td>${h.receiverName}</td>
                                                                            <td>${h.allocatedByName}</td>
                                                                            <td data-sort="${h.allocatedAt}">
                                                                                <c:if test="${h.allocatedAt != null}">
                                                                                    <small>${h.allocatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</small>
                                                                                </c:if>
                                                                            </td>
                                                                            <td>
                                                                                <c:choose>
                                                                                    <c:when test="${h.status == 'ACTIVE'}">
                                                                                        <span class="badge badge-success">Đã cấp phát</span>
                                                                                    </c:when>
                                                                                    <c:when test="${h.status == 'RETURNED'}">
                                                                                        <span class="badge badge-secondary">Đã thu hồi</span>
                                                                                    </c:when>
                                                                                    <c:when test="${h.status == 'COMPLETED'}">
                                                                                        <span class="badge badge-primary">Hoàn tất</span>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <span class="badge badge-info">${h.status}</span>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </td>
                                                                            <td class="text-center">
                                                                                <c:choose>
                                                                                    <c:when test="${isTeacher}">
                                                                                        <a href="${pageContext.request.contextPath}/teacher/allocation-history?id=${h.allocationId}"
                                                                                           class="btn btn-sm btn-info" title="Chi tiết">
                                                                                            <i class="fas fa-eye"></i>
                                                                                        </a>
                                                                                    </c:when>
                                                                                    <c:when test="${isStaff}">
                                                                                        <a href="${pageContext.request.contextPath}/staff/allocation-history?id=${h.allocationId}"
                                                                                           class="btn btn-sm btn-info" title="Chi tiết">
                                                                                            <i class="fas fa-eye"></i>
                                                                                        </a>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <a href="${pageContext.request.contextPath}/board/allocation-history?id=${h.allocationId}"
                                                                                           class="btn btn-sm btn-info" title="Chi tiết">
                                                                                            <i class="fas fa-eye"></i>
                                                                                        </a>
                                                                                    </c:otherwise>
                                                                                </c:choose>
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

                                        <!-- Detail Modal (auto open when detail present) -->
                                        <c:if test="${detailNotFound}">
                                            <div class="alert alert-warning">
                                                Không tìm thấy thông tin cấp phát.
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty detail}">
                                            <div class="modal fade" id="allocationDetailModal" tabindex="-1" role="dialog" aria-hidden="true">
                                                <div class="modal-dialog modal-lg" role="document">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">
                                                                <i class="fas fa-info-circle text-primary"></i> Chi tiết cấp phát
                                                            </h5>
                                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                                <span aria-hidden="true">&times;</span>
                                                            </button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="row">
                                                                <div class="col-lg-6">
                                                                    <p><strong>Mã cấp phát:</strong> ${detail.allocationCode}</p>
                                                                    <p><strong>Mã yêu cầu:</strong> ${detail.requestCode}</p>
                                                                    <p><strong>Phòng xuất:</strong> ${detail.fromRoomName}</p>
                                                                    <p><strong>Phòng nhận:</strong> ${detail.toRoomName}</p>
                                                                </div>
                                                                <div class="col-lg-6">
                                                                    <p><strong>Người nhận:</strong> ${detail.receiverName}</p>
                                                                    <p><strong>Người cấp phát:</strong> ${detail.allocatedByName}</p>
                                                                    <p><strong>Ngày cấp phát:</strong>
                                                                        <c:if test="${detail.allocatedAt != null}">
                                                                            ${detail.allocatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                                                        </c:if>
                                                                    </p>
                                                                    <p><strong>Trạng thái:</strong>
                                                                        <c:choose>
                                                                            <c:when test="${detail.status == 'ACTIVE'}">
                                                                                <span class="badge badge-success">Đã cấp phát</span>
                                                                            </c:when>
                                                                            <c:when test="${detail.status == 'RETURNED'}">
                                                                                <span class="badge badge-secondary">Đã thu hồi</span>
                                                                            </c:when>
                                                                            <c:when test="${detail.status == 'COMPLETED'}">
                                                                                <span class="badge badge-primary">Hoàn tất</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="badge badge-info">${detail.status}</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </p>
                                                                </div>
                                                            </div>
                                                            <hr>
                                                            <h6 class="font-weight-bold mb-3">Tài sản cấp phát</h6>
                                                            <div class="table-responsive">
                                                                <table class="table table-bordered">
                                                                    <thead class="thead-light">
                                                                        <tr>
                                                                            <th>Mã tài sản</th>
                                                                            <th>Tên tài sản</th>
                                                                            <th>Serial</th>
                                                                            <th>Ghi chú</th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        <c:choose>
                                                                            <c:when test="${empty detailAssets}">
                                                                                <tr>
                                                                                    <td colspan="4" class="text-center text-muted">Không có tài sản</td>
                                                                                </tr>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <c:forEach var="a" items="${detailAssets}">
                                                                                    <tr>
                                                                                        <td>${a.assetCode}</td>
                                                                                        <td>${a.assetName}</td>
                                                                                        <td>${a.serialNumber}</td>
                                                                                        <td>${not empty a.conditionNote ? a.conditionNote : '-'}</td>
                                                                                    </tr>
                                                                                </c:forEach>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
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
                                            $(document).ready(function () {
                                                $('#dataTable').DataTable({
                                                    "searching": false,
                                                    "language": {
                                                        "lengthMenu": "Hiển thị _MENU_ lần cấp phát mỗi trang",
                                                        "zeroRecords": "Không tìm thấy dữ liệu",
                                                        "info": "Trang _PAGE_ / _PAGES_",
                                                        "infoEmpty": "Không có dữ liệu",
                                                        "infoFiltered": "(lọc từ _MAX_ dữ liệu)",
                                                        "search": "Tìm kiếm:",
                                                        "paginate": {
                                                            "first": "Đầu",
                                                            "last": "Cuối",
                                                            "next": "Sau",
                                                            "previous": "Trước"
                                                        }
                                                    },
                                                    "pageLength": 10,
                                                    "order": [[6, "desc"]]
                                                });
                                            });
                                        </script>
                                        <c:if test="${not empty detail}">
                                            <script>
                                                $(document).ready(function () {
                                                    $('#allocationDetailModal').modal('show');
                                                });
                                            </script>
                                        </c:if>
                                        </body>
                                        </html>
