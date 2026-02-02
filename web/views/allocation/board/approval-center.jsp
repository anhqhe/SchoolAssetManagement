<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Trung Tâm Phê Duyệt - BGH</title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <style>
            .btn-close{
                box-sizing:content-box;
                width:1em;
                height:1em;
                padding:.25em .25em;
                color:#000;
                background:transparent url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%23000'%3e%3cpath d='M.293.293a1 1 0 011.414 0L8 6.586 14.293.293a1 1 0 111.414 1.414L9.414 8l6.293 6.293a1 1 0 01-1.414 1.414L8 9.414l-6.293 6.293a1 1 0 01-1.414-1.414L6.586 8 .293 1.707a1 1 0 010-1.414z'/%3e%3c/svg%3e") center/1em auto no-repeat;
                border:0;
                border-radius:.25rem;
                opacity:.5
            }
            .btn-close:hover{
                color:#000;
                text-decoration:none;
                opacity:.75
            }
            .btn-close:focus{
                outline:0;
                box-shadow:0 0 0 .25rem rgba(13,110,253,.25);
                opacity:1
            }
            .btn-close.disabled,.btn-close:disabled{
                pointer-events:none;
                -webkit-user-select:none;
                -moz-user-select:none;
                user-select:none;
                opacity:.25
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
                    <div class="container-fluid mt-5">
                        <div class="card shadow border-0">
                            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Danh Sách Yêu Cầu</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty msg}">
                                    <div class="alert alert-success">${msg}</div>
                                </c:if>  

                                <!--  Filter Begin -->
                                <div class="card-body">
                                    <form action="${pageContext.request.contextPath}/board/approval-center" method="get" id="filterForm" class="row gx-3 gy-2 align-items-center justify-content-end">
                                        <div class="col-sm-4">
                                            <label class="sr-only">Tìm kiếm</label>
                                            <div class="input-group">
                                                <div class="input-group-prepend">
                                                    <div class="input-group-text"><i class="fas fa-search"></i></div>
                                                </div>
                                                <input type="text" name="keyword" class="form-control" placeholder="Mã phiếu, tên GV..." value="${param.keyword}">
                                            </div>
                                        </div>

                                        <div class="col-sm-2">
                                            <select name="status" class="form-control">
                                                <option value="">-- Trạng thái --</option>
                                                <option value="WAITING_BOARD" ${param.status == 'WAITING_BOARD' ? 'selected' : ''}>Chờ duyệt</option>
                                                <option value="APPROVED_BY_BOARD" ${param.status == 'APPROVED_BY_BOARD' ? 'selected' : ''}>Đã duyệt</option>
                                                <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                            </select>
                                        </div>

                                        <div class="col-auto">
                                            <button type="submit" class="btn btn-primary">Áp dụng</button>
                                            <a href="approval-center" class="btn btn-outline-secondary">Reset</a>
                                        </div>
                                    </form>
                                </div>
                                <!-- Filter End -->

                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'RequestCode ASC' ? 'RequestCode DESC' : 'RequestCode ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        Mã phiếu 
                                                        <i class="fas ${param.sortBy.contains('RequestCode') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'TeacherName ASC' ? 'TeacherName DESC' : 'TeacherName ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        Người Yêu Cầu 
                                                        <i class="fas ${param.sortBy.contains('TeacherName') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'CreatedAt ASC' ? 'CreatedAt DESC' : 'CreatedAt ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        Ngày Gửi 
                                                        <i class="fas ${param.sortBy.contains('CreatedAt') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'RoomName ASC' ? 'RoomName DESC' : 'RoomName ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        Phòng Nhận 
                                                        <i class="fas ${param.sortBy.contains('RoomName') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>
                                                <th>Mục Đích</th>
                                                <th>
                                                    <a href="?sortBy=${param.sortBy == 'Status ASC' ? 'Status DESC' : 'Status ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                        Trạng Thái 
                                                        <i class="fas ${param.sortBy.contains('Status') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                    </a>
                                                </th>
                                                <th class="text-center">Thao Tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="req" items="${pendingList}">
                                                <tr>
                                                    <td><span class="fw-bold">${req.requestCode}</span></td>
                                                    <td>${req.teacherName}</td>
                                                    <td> ${req.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                                    <td>${req.roomName}</td>
                                                    <td><span class="text-truncate" style="max-width: 200px; display: inline-block;">${req.purpose}</span></td>
                                                    <td><span class="badge badge-info">${req.status}</span></td>
                                                    <td>
                                                        <a href="request-detail?id=${req.requestId}" class="btn btn-sm btn-outline-primary">
                                                            Xem chi tiết
                                                        </a>
                                                        <c:if test="${req.status == 'WAITING_BOARD'}">
                                                            <button type="button" class="btn btn-sm btn-success" 
                                                                    onclick="openApproveModal('${req.requestId}', '${req.requestCode}')">
                                                                Duyệt / Từ chối
                                                            </button>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty pendingList}">
                                                <tr>
                                                    <td colspan="6" class="text-center text-muted py-4">Hiện không có yêu cầu nào cần phê duyệt.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal fade" id="approveModal" tabindex="-1">
                        <div class="modal-dialog">
                            <form action="${pageContext.request.contextPath}/board/approval-center" method="post" class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Xử lý yêu cầu: <span id="modalReqCode"></span></h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <input type="hidden" name="requestId" id="modalReqId">
                                    <div class="mb-3">
                                        <label class="form-label">Quyết định</label>
                                        <select name="decision" class="form-select" required>
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
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                    <button type="submit" class="btn btn-primary">Xác nhận</button>
                                </div>
                            </form>
                        </div>
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>



        <script src="${pageContext.request.contextPath}/assets/allocation/bootstrap.bundle.min.js"></script>
        <script>
                                                                        function openApproveModal(id, code) {
                                                                            document.getElementById('modalReqId').value = id;
                                                                            document.getElementById('modalReqCode').innerText = code;
                                                                            new bootstrap.Modal(document.getElementById('approveModal')).show();
                                                                        }
        </script>

    </body>
</html>