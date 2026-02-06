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

<!-- Unify list attribute: use myRequests if teacher, otherwise pendingList -->
<c:set var="requestList" value="${isTeacher ? myRequests : pendingList}"/>

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
    </head>

    <body id="page-top">
        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">

                    <%@ include file="/views/layout/topbar.jsp" %>

                    <!-- Page Content -->

                    <!-- Message Alerts (COMMON) -->
                    <c:if test="${param.msg eq 'success'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-check-circle"></i>
                            <c:choose>
                                <c:when test="${isTeacher}">Gửi yêu cầu tài sản thành công!</c:when>
                                <c:otherwise>Xử lý thành công!</c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-exclamation-triangle"></i>
                            ${error}
                        </div>
                    </c:if>

                    <c:if test="${not empty msg}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-check-circle"></i>
                            ${msg}
                        </div>
                    </c:if>

                    <!-- Main Content -->
                    <div class="container-fluid mt-5">

                        <!-- Header: Title + Add Button (TEACHER ONLY) -->
                        <c:if test="${isTeacher}">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h3>Danh Sách Yêu Cầu Của Tôi</h3>
                                <a href="${pageContext.request.contextPath}/teacher/add-request" class="btn btn-primary">+ Tạo Yêu Cầu Mới</a>
                            </div>
                        </c:if>

                        <div class="card shadow-sm">

                            <!-- Card Header with Title (STAFF & BOARD) -->
                            <c:if test="${isStaff || isBoard}">
                                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">
                                        <c:choose>
                                            <c:when test="${isStaff}">Yêu Cầu Chờ Phê Duyệt</c:when>
                                            <c:when test="${isBoard}">Danh Sách Yêu Cầu</c:when>
                                        </c:choose>
                                    </h5>
                                </div>
                            </c:if>

                            <!-- Filter Section (COMMON) -->
                            <div class="card-body mt-4 mr-5">
                                <c:choose>
                                    <c:when test="${isTeacher}">
                                        <form action="${pageContext.request.contextPath}/teacher/request-list" method="get" id="filterForm" class="row gx-3 gy-2 align-items-center justify-content-end">
                                    </c:when>
                                    <c:when test="${isStaff}">
                                        <form action="${pageContext.request.contextPath}/staff/allocation-list" method="get" id="filterForm" class="row gx-3 gy-2 align-items-center justify-content-end">
                                    </c:when>
                                    <c:when test="${isBoard}">
                                        <form action="${pageContext.request.contextPath}/board/approval-center" method="get" id="filterForm" class="row gx-3 gy-2 align-items-center justify-content-end">
                                    </c:when>
                                </c:choose>
                                    <div class="col-sm-4">
                                        <label class="sr-only">Tìm kiếm</label>
                                        <div class="input-group">
                                            <div class="input-group-prepend">
                                                <div class="input-group-text"><i class="fas fa-search"></i></div>
                                            </div>
                                            <input type="text" name="keyword" class="form-control" placeholder="Mã phiếu, tên GV..." value="${param.keyword}">
                                        </div>
                                    </div>

                                    <div class="col-sm-3">
                                        <select name="status" class="form-control">
                                            <c:choose>
                                                <c:when test="${isTeacher}">
                                                    <option value="">-- Tất cả trạng thái --</option>
                                                    <option value="WAITING_BOARD" ${param.status == 'WAITING_BOARD' ? 'selected' : ''}>Chờ duyệt</option>
                                                    <option value="APPROVED_BY_BOARD" ${param.status == 'APPROVED_BY_BOARD' ? 'selected' : ''}>Đã duyệt</option>
                                                    <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="">-- Trạng thái --</option>
                                                    <option value="WAITING_BOARD" ${param.status == 'WAITING_BOARD' ? 'selected' : ''}>Chờ duyệt</option>
                                                    <option value="APPROVED_BY_BOARD" ${param.status == 'APPROVED_BY_BOARD' ? 'selected' : ''}>Đã duyệt</option>
                                                    <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </select>
                                    </div>

                                    <div class="col-auto">
                                        <button type="submit" class="btn btn-primary">Áp dụng</button>
                                        <c:choose>
                                            <c:when test="${isTeacher}">
                                                <a href="request-list" class="btn btn-outline-secondary">Reset</a>
                                            </c:when>
                                            <c:when test="${isStaff}">
                                                <a href="allocation-list" class="btn btn-outline-secondary">Reset</a>
                                            </c:when>
                                            <c:when test="${isBoard}">
                                                <a href="approval-center" class="btn btn-outline-secondary">Reset</a>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </form>
                            </div>

                            <!-- Table Section (COMMON) -->
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th>
                                                    <c:choose>
                                                        <c:when test="${isTeacher}">
                                                            <a href="?sortBy=${param.sortBy == 'RequestCode ASC' ? 'RequestCode DESC' : 'RequestCode ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                                Mã Phiếu <i class="fas ${param.sortBy.contains('RequestCode') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="?sortBy=${param.sortBy == 'RequestCode ASC' ? 'RequestCode DESC' : 'RequestCode ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                                Mã phiếu <i class="fas ${param.sortBy.contains('RequestCode') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                            </a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </th>

                                                <!-- STAFF & BOARD: Show Người Yêu Cầu column -->
                                                <c:if test="${isStaff || isBoard}">
                                                    <th>
                                                        <a href="?sortBy=${param.sortBy == 'TeacherName ASC' ? 'TeacherName DESC' : 'TeacherName ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                            Người Yêu Cầu <i class="fas ${param.sortBy.contains('TeacherName') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                        </a>
                                                    </th>
                                                </c:if>

                                                <!-- Ngày / CreatedAt Column -->
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'CreatedAt ASC' ? 'CreatedAt DESC' : 'CreatedAt ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        <c:choose>
                                                            <c:when test="${isTeacher}">Ngày Tạo</c:when>
                                                            <c:otherwise>Ngày Gửi</c:otherwise>
                                                        </c:choose>
                                                        <i class="fas ${param.sortBy.contains('CreatedAt') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>

                                                <!-- TEACHER: Phòng Yêu Cầu, others: Phòng Nhận -->
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'RoomName ASC' ? 'RoomName DESC' : 'RoomName ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        <c:choose>
                                                            <c:when test="${isTeacher}">Phòng Yêu Cầu</c:when>
                                                            <c:otherwise>Phòng Nhận</c:otherwise>
                                                        </c:choose>
                                                        <i class="fas ${param.sortBy.contains('RoomName') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>

                                                <!-- STAFF & BOARD: Show Purpose column -->
                                                <c:if test="${isStaff || isBoard}">
                                                    <th>Mục Đích</th>
                                                </c:if>

                                                <!-- Status Column -->
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'Status ASC' ? 'Status DESC' : 'Status ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        Trạng Thái <i class="fas ${param.sortBy.contains('Status') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>

                                                <!-- Actions Column -->
                                                <th class="text-center">Thao Tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="req" items="${requestList}">
                                                <tr>
                                                    <td><strong>${req.requestCode}</strong></td>

                                                    <!-- STAFF & BOARD: Người Yêu Cầu -->
                                                    <c:if test="${isStaff || isBoard}">
                                                        <td>${req.teacherName}</td>
                                                    </c:if>

                                                    <!-- Date -->
                                                    <td>${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>

                                                    <!-- Room -->
                                                    <td>${req.roomName}</td>

                                                    <!-- STAFF & BOARD: Purpose -->
                                                    <c:if test="${isStaff || isBoard}">
                                                        <td><span class="text-truncate" style="max-width: 200px; display: inline-block;">${req.purpose}</span></td>
                                                    </c:if>

                                                    <!-- Status -->
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${req.status == 'WAITING_BOARD'}"><span class="badge badge-info">Chờ duyệt</span></c:when>
                                                            <c:when test="${req.status == 'APPROVED_BY_BOARD'}"><span class="badge bg-success">Đã duyệt</span></c:when>
                                                            <c:when test="${req.status == 'COMPLETED'}"><span class="badge bg-success text-white">Đã hoàn thành</span></c:when>
                                                            <c:when test="${req.status == 'REJECTED'}"><span class="badge bg-danger text-white">Bị từ chối</span></c:when>
                                                            <c:otherwise><span class="badge bg-dark text-white">${req.status}</span></c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <!-- Actions (role-specific) -->
                                                    <td class="text-center">
                                                        <!-- All roles: View detail button -->
                                                        <c:choose>
                                                            <c:when test="${isTeacher}">
                                                                <a href="${pageContext.request.contextPath}/teacher/request-detail?id=${req.requestId}" class="btn btn-sm btn-outline-info">Xem chi tiết</a>
                                                            </c:when>
                                                            <c:when test="${isStaff}">
                                                                <a href="${pageContext.request.contextPath}/staff/request-detail?id=${req.requestId}" class="btn btn-sm btn-outline-primary">Xem chi tiết</a>
                                                                <c:if test="${req.status == 'APPROVED_BY_BOARD'}">
                                                                    <a href="allocate-assets?requestId=${req.requestId}" class="btn btn-primary btn-sm">
                                                                        <i class="fas fa-box-open"></i> Bàn giao
                                                                    </a>
                                                                </c:if>
                                                            </c:when>
                                                            <c:when test="${isBoard}">
                                                                <a href="${pageContext.request.contextPath}/board/request-detail?id=${req.requestId}" class="btn btn-sm btn-outline-primary">Xem chi tiết</a>
                                                                <c:if test="${req.status == 'WAITING_BOARD'}">
                                                                    <button type="button" class="btn btn-sm btn-success" 
                                                                            onclick="openApproveModal('${req.requestId}', '${req.requestCode}')">
                                                                        Duyệt / Từ chối
                                                                    </button>
                                                                </c:if>
                                                            </c:when>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty requestList}">
                                                <tr>
                                                    <td colspan="7" class="text-center text-muted py-4">
                                                        <c:choose>
                                                            <c:when test="${isTeacher}">Bạn chưa có yêu cầu nào.</c:when>
                                                            <c:otherwise>Hiện không có yêu cầu nào cần phê duyệt.</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:if>
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

        <script>
            function openApproveModal(id, code) {
                document.getElementById('modalReqId').value = id;
                document.getElementById('modalReqCode').innerText = code;
                $('#approveModal').modal('show');
            }
        </script>
    </body>
</html>
