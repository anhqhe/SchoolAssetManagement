<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
<html>
    <head>
        <meta charset="UTF-8">
        <title>
            <c:choose>
                <c:when test="${isTeacher}">Lịch Sử Yêu Cầu Tài Sản</c:when>
                <c:when test="${isStaff}">Danh Sách Yêu Cầu Chờ Xử Lý</c:when>
                <c:when test="${isBoard}">Trung Tâm Phê Duyệt</c:when>
                <c:otherwise>Danh Sách Yêu Cầu</c:otherwise>
            </c:choose>
        </title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
    </head>

    <body id="page-top">
        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">

                    <%@ include file="/views/layout/topbar.jsp" %>

                    <!-- Error/Success Messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="fas fa-exclamation-circle"></i> ${error}
                            <button type="button" class="close" data-dismiss="alert">
                                <span>&times;</span>
                            </button>
                        </div>
                    </c:if>

                    <c:if test="${not empty msg}">
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="fas fa-check-circle"></i> ${msg}
                            <button type="button" class="close" data-dismiss="alert">
                                <span>&times;</span>
                            </button>
                        </div>
                    </c:if>

                    <!-- Main Content -->
                    <div class="container-fluid">

                        <!-- Page Heading -->
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-list text-primary"></i>
                                <c:choose>
                                    <c:when test="${isTeacher}">Lịch Sử Yêu Cầu Tài Sản</c:when>
                                    <c:when test="${isStaff}">Danh Sách Yêu Cầu Chờ Xử Lý</c:when>
                                    <c:when test="${isBoard}">Trung Tâm Phê Duyệt</c:when>
                                    <c:otherwise>Danh Sách Yêu Cầu</c:otherwise>
                                </c:choose>
                            </h1>
                            
                            <!-- Add Request Button (TEACHER ONLY) -->
                            <c:if test="${isTeacher}">
                                <a href="${pageContext.request.contextPath}/teacher/add-request" class="btn btn-primary btn-icon-split shadow-sm">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-plus"></i>
                                    </span>
                                    <span class="text">Tạo Yêu Cầu Mới</span>
                                </a>
                            </c:if>
                        </div>

                        <!-- Filter Card -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-filter"></i> Tìm kiếm & Lọc
                                </h6>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${isTeacher}">
                                        <form action="${pageContext.request.contextPath}/teacher/request-list" method="get" id="filterForm" class="form-inline">
                                    </c:when>
                                    <c:when test="${isStaff}">
                                        <form action="${pageContext.request.contextPath}/staff/allocation-list" method="get" id="filterForm" class="form-inline">
                                    </c:when>
                                    <c:when test="${isBoard}">
                                        <form action="${pageContext.request.contextPath}/board/approval-center" method="get" id="filterForm" class="form-inline">
                                    </c:when>
                                </c:choose>
                                    <div class="form-group mr-3 mb-2">
                                        <input type="text" 
                                               name="keyword" 
                                               class="form-control" 
                                               placeholder="Tìm kiếm ..."
                                               value="${param.keyword}">
                                    </div>
                                    
                                    <div class="form-group mr-3 mb-2">
                                        <select name="status" class="form-control">
                                            <option value="">-- Tất cả trạng thái --</option>
                                            <option value="WAITING_BOARD" ${param.status == 'WAITING_BOARD' ? 'selected' : ''}>Chờ duyệt</option>
                                            <option value="APPROVED_BY_BOARD" ${param.status == 'APPROVED_BY_BOARD' ? 'selected' : ''}>Đã duyệt</option>
                                            <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                        </select>
                                    </div>

                                    <button type="submit" class="btn btn-primary mb-2 mr-2">
                                        <i class="fas fa-search"></i> Tìm kiếm
                                    </button>
                                    
                                    <c:choose>
                                        <c:when test="${isTeacher}">
                                            <a href="${pageContext.request.contextPath}/teacher/request-list" class="btn btn-secondary mb-2">
                                                <i class="fas fa-redo"></i> Đặt lại
                                            </a>
                                        </c:when>
                                        <c:when test="${isStaff}">
                                            <a href="${pageContext.request.contextPath}/staff/allocation-list" class="btn btn-secondary mb-2">
                                                <i class="fas fa-redo"></i> Đặt lại
                                            </a>
                                        </c:when>
                                        <c:when test="${isBoard}">
                                            <a href="${pageContext.request.contextPath}/board/approval-center" class="btn btn-secondary mb-2">
                                                <i class="fas fa-redo"></i> Đặt lại
                                            </a>
                                        </c:when>
                                    </c:choose>
                                </form>
                            </div>
                        </div>

                        <!-- Requests Table Card -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-tasks"></i> Danh sách yêu cầu
                                    <span class="badge badge-primary">${requestList != null ? requestList.size() : 0}</span>
                                </h6>
                            </div>

                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover" id="dataTable" width="100%" cellspacing="0">
                                        <thead class="thead-light">
                                            <tr>
                                                <th>Mã Phiếu</th>
                                                <!-- STAFF & BOARD: Show Người Yêu Cầu column -->
                                                <c:if test="${isStaff || isBoard}">
                                                    <th>Người Yêu Cầu</th>
                                                </c:if>
                                                <th>Ngày Gửi</th>
                                                <th>
                                                    <c:choose>
                                                        <c:when test="${isTeacher}">Phòng Yêu Cầu</c:when>
                                                        <c:otherwise>Phòng Nhận</c:otherwise>
                                                    </c:choose>
                                                </th>
                                                <!-- STAFF & BOARD: Show Purpose column -->
                                                <c:if test="${isStaff || isBoard}">
                                                    <th>Mục Đích</th>
                                                </c:if>
                                                <th>Trạng Thái</th>
                                                <th class="text-center">Thao Tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${empty requestList}">
                                                    <tr>
                                                        <td colspan="${(isStaff || isBoard) ? 7 : 6}" class="text-center text-muted py-5">
                                                            <i class="fas fa-inbox fa-3x mb-3"></i>
                                                            <p>
                                                                <c:choose>
                                                                    <c:when test="${isTeacher}">Bạn chưa có yêu cầu nào</c:when>
                                                                    <c:otherwise>Hiện không có yêu cầu nào cần phê duyệt</c:otherwise>
                                                                </c:choose>
                                                            </p>
                                                        </td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="req" items="${requestList}">
                                                        <tr>
                                                            <td>
                                                                <strong>${req.requestCode}</strong>
                                                            </td>
                                                            <!-- STAFF & BOARD: Người Yêu Cầu -->
                                                            <c:if test="${isStaff || isBoard}">
                                                                <td>${req.teacherName}</td>
                                                            </c:if>
                                                            <!-- Date -->
                                                            <td>
                                                                <small>${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</small>
                                                            </td>
                                                            <!-- Room -->
                                                            <td>
                                                                ${req.roomName}
                                                            </td>
                                                            <!-- STAFF & BOARD: Purpose -->
                                                            <c:if test="${isStaff || isBoard}">
                                                                <td>
                                                                    <span class="text-truncate" style="max-width: 150px; display: inline-block;" title="${req.purpose}">
                                                                        <small>${req.purpose}</small>
                                                                    </span>
                                                                </td>
                                                            </c:if>
                                                            <!-- Status -->
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${req.status == 'WAITING_BOARD'}"><span class="badge badge-warning">Chờ duyệt</span></c:when>
                                                                    <c:when test="${req.status == 'APPROVED_BY_BOARD'}"><span class="badge badge-success">Đã duyệt</span></c:when>
                                                                    <c:when test="${req.status == 'COMPLETED'}"><span class="badge badge-success">Đã hoàn thành</span></c:when>
                                                                    <c:when test="${req.status == 'REJECTED'}"><span class="badge badge-danger">Bị từ chối</span></c:when>
                                                                    <c:otherwise><span class="badge badge-secondary">${req.status}</span></c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <!-- Actions (role-specific) -->
                                                            <td class="text-left">
                                                                <c:choose>
                                                                    <c:when test="${isTeacher}">
                                                                        <a href="${pageContext.request.contextPath}/teacher/request-detail?id=${req.requestId}" 
                                                                           class="btn btn-sm btn-info" title="Xem chi tiết">
                                                                            <i class="fas fa-eye"></i>
                                                                        </a>
                                                                    </c:when>
                                                                    <c:when test="${isStaff}">
                                                                        <a href="${pageContext.request.contextPath}/staff/request-detail?id=${req.requestId}" 
                                                                           class="btn btn-sm btn-info" title="Xem chi tiết">
                                                                            <i class="fas fa-eye"></i>
                                                                        </a>
                                                                        <c:if test="${req.status == 'APPROVED_BY_BOARD'}">
                                                                            <a href="allocate-assets?requestId=${req.requestId}" 
                                                                               class="btn btn-sm btn-success" title="Bàn giao tài sản">
                                                                                <i class="fas fa-box-open"></i>
                                                                            </a>
                                                                        </c:if>
                                                                    </c:when>
                                                                    <c:when test="${isBoard}">
                                                                        <a href="${pageContext.request.contextPath}/board/request-detail?id=${req.requestId}" 
                                                                           class="btn btn-sm btn-info" title="Xem chi tiết">
                                                                            <i class="fas fa-eye"></i>
                                                                        </a>
                                                                        <c:if test="${req.status == 'WAITING_BOARD'}">
                                                                            <button type="button" class="btn btn-sm btn-warning" 
                                                                                    onclick="openApproveModal('${req.requestId}', '${req.requestCode}')"
                                                                                    title="Phê duyệt / Từ chối">
                                                                                <i class="fas fa-check"></i>
                                                                            </button>
                                                                        </c:if>
                                                                    </c:when>
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
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <%@ include file="/views/layout/allocation/notification.jsp" %>

        <!-- Approval Modal (BOARD ONLY) -->
        <c:if test="${isBoard}">
            <div class="modal fade" id="approveModal" tabindex="-1">
                <div class="modal-dialog">
                    <form action="${pageContext.request.contextPath}/board/approval-center" method="post" class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Xử lý yêu cầu: <span id="modalReqCode"></span></h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="requestId" id="modalReqId">
                            <div class="mb-3">
                                <label class="form-label">Quyết định</label>
                                <select name="decision" class="form-control" required>
                                    <option value="APPROVED">Phê Duyệt (Chuyển Staff cấp phát)</option>
                                    <option value="REJECTED">Từ Chối Yêu Cầu</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Ghi chú phản hồi</label>
                                <textarea name="note" class="form-control" rows="3" placeholder="Lý do duyệt hoặc từ chối..."></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                            <button type="submit" class="btn btn-primary">Xác nhận</button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>

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
                        "lengthMenu": "Hiển thị _MENU_ yêu cầu mỗi trang",
                        "zeroRecords": "Không tìm thấy yêu cầu nào",
                        "info": "Trang _PAGE_ / _PAGES_",
                        "infoEmpty": "Không có dữ liệu",
                        "infoFiltered": "(lọc từ _MAX_ yêu cầu)",
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
        <script>
            function openApproveModal(id, code) {
                document.getElementById('modalReqId').value = id;
                document.getElementById('modalReqCode').innerText = code;
                $('#approveModal').modal('show');
            }
        </script>
    </body>
</html>
