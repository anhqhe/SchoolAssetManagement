<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Lịch Sử Yêu Cầu Tài Sản</title>
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
                    <!--   <div class="container-fluid">-->
                    <div class="container mt-5">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h3>Lịch Sử Yêu Cầu Của Tôi</h3>
                            <a href="${pageContext.request.contextPath}/teacher/add-request" class="btn btn-primary">+ Tạo Yêu Cầu Mới</a>
                        </div>

                        <div class="card shadow-sm">

                            <!--  Filter Begin -->
                            <div class="card-body mt-4 mr-5">
                                <form action="${pageContext.request.contextPath}/teacher/request-list" method="get" id="filterForm" class="row gx-3 gy-2 align-items-center justify-content-end">
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
                                        <a href="request-list" class="btn btn-outline-secondary">Reset</a>
                                    </div>
                                </form>
                            </div>
                            <!-- Filter End -->

                            <div class="card-body">
                                <table class="table table-hover">
                                    <thead class="table-light">
                                        <tr>
                                            <th>
                                                <a href="?sortBy=${param.sortBy == 'RequestCode ASC' ? 'RequestCode DESC' : 'RequestCode ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                    Mã Phiếu 
                                                    <i class="fas ${param.sortBy.contains('RequestCode') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                </a>
                                            </th>
                                            <th>
                                                <a href="?sortBy=${param.sortBy == 'CreatedAt ASC' ? 'CreatedAt DESC' : 'CreatedAt ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                    Ngày Tạo 
                                                    <i class="fas ${param.sortBy.contains('CreatedAt') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                </a>
                                            </th>
                                            <th>
                                                <a href="?sortBy=${param.sortBy == 'RoomName ASC' ? 'RoomName DESC' : 'RoomName ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                    Phòng Yêu Cầu 
                                                    <i class="fas ${param.sortBy.contains('RoomName') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                </a>
                                            </th>
                                            <th>
                                                <a href="?sortBy=${param.sortBy == 'Status ASC' ? 'Status DESC' : 'Status ASC'}&status=${param.status}&keyword=${param.keyword}">
                                                    Trạng Thái 
                                                    <i class="fas ${param.sortBy.contains('Status') ? (param.sortBy.contains('ASC') ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
                                                </a>
                                            </th>
                                            <th>Thao Tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="req" items="${myRequests}">
                                            <tr>
                                                <td><strong>${req.requestCode}</strong></td>
                                                <td> ${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                                <td>${req.roomName}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${req.status == 'PENDING'}"><span class="badge bg-secondary text-white">Chờ Staff kiểm tra</span></c:when>
                                                        <c:when test="${req.status == 'WAITING_APPROVE'}"><span class="badge bg-warning text-white">Chờ BGH duyệt</span></c:when>
                                                        <c:when test="${req.status == 'APPROVED'}"><span class="badge bg-info text-white">Đã duyệt - Chờ nhận đồ</span></c:when>
                                                        <c:when test="${req.status == 'COMPLETED'}"><span class="badge bg-success text-white">Đã hoàn thành</span></c:when>
                                                        <c:when test="${req.status == 'REJECTED'}"><span class="badge bg-danger text-white">Bị từ chối</span></c:when>
                                                        <c:otherwise><span class="badge bg-dark text-white">${req.status}</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="request-detail?id=${req.requestId}" class="btn btn-sm btn-outline-info">Xem chi tiết</a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty myRequests}">
                                            <tr><td colspan="5" class="text-center text-muted">Bạn chưa có yêu cầu nào.</td></tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

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