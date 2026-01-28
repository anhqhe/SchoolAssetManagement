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
                    <div class="container mt-5">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h3>Lịch Sử Yêu Cầu Của Tôi</h3>
                            <a href="${pageContext.request.contextPath}/teacher/add-request" class="btn btn-primary">+ Tạo Yêu Cầu Mới</a>
                        </div>

                        <div class="card shadow-sm">
                            <div class="card-body">
                                <table class="table table-hover">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Mã Phiếu</th>
                                            <th>Ngày Tạo</th>
                                            <th>Phòng Yêu Cầu</th>
                                            <th>Trạng Thái</th>
                                            <th>Thao Tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="req" items="${myRequests}">
                                            <tr>
                                                <td><strong>${req.requestCode}</strong></td>
                                                <td> ${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                                <!--<td>{req.roomName}</td>-->

                                                <td>${req.requestedRoomId}</td>
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

    </body>
</html>