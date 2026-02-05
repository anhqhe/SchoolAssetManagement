<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Yêu Cầu Chờ Xử Lý</title>
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

                    <c:if test="${not empty msg}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-check-circle"></i>
                            ${msg}
                        </div>
                    </c:if>  


                    <!-- Main Content Begin -->
                    <div class="container-fluid mt-5">

                        <div class="card shadow border-0">
                            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Yêu Cầu Chờ Phê Duyệt</h5>
                            </div>

                            <!--  Filter Begin -->
                            <div class="card-body mt-4 mr-5">
                                <form action="${pageContext.request.contextPath}/staff/allocation-list" method="get" id="filterForm" class="row gx-3 gy-2 align-items-center justify-content-end">
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
                                        <a href="allocation-list" class="btn btn-outline-secondary">Reset</a>
                                    </div>
                                </form>
                            </div>
                            <!-- Filter End -->


                            <div class="card-body">
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
                                                    <td>${req.requestCode}</td>
                                                    <td>${req.teacherName}</td>
                                                    <td>${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                                    <td>${req.roomName}</td>
                                                    <td>${req.purpose}</td>
                                                    <td><span class="badge badge-info">${req.status}</span></td>

                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/staff/request-detail?id=${req.requestId}" class="btn btn-sm btn-outline-primary">
                                                            Xem chi tiết
                                                        </a>
                                                        <c:if test="${req.status == 'APPROVED_BY_BOARD'}">
                                                            <a href="allocate-assets?requestId=${req.requestId}" class="btn btn-primary btn-sm">
                                                                <i class="fas fa-box-open"></i> Bàn giao
                                                            </a>
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
                    <!-- Main Content End -->

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <%@ include file="/views/layout/allocation/notification.jsp" %>

        <!-- Scripts -->
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
    </body>
</html>