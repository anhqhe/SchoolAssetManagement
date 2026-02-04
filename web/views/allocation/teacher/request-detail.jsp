<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Chi Tiết Yêu Cầu #${req.requestCode}</title>
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
                    <div class="container py-4">
                        <div class="card shadow">
                            <div class="card-header bg-white d-flex justify-content-between">
                                <h5 class="mb-0">Thông tin phiếu: ${req.requestCode}</h5>
                                <span class="badge text-white ${req.status == 'REJECTED' ? 'bg-danger' : 'bg-primary'}">${req.status}</span>
                            </div>
                            <div class="card-body">
                                <div class="row mb-4">
                                    <div class="col-md-6">
                                        <p><strong>Người yêu cầu:</strong> ${req.teacherName}</p>
                                        <p><strong>Phòng nhận:</strong> ${req.roomName}</p> </div>
                                    <div class="col-md-6 text-md-end">
                                        <p><strong>Ngày tạo:</strong>
                                        <td>${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</td>
                                        </p> 
                                        <p><strong>Mục đích:</strong> ${req.purpose}</p> 
                                    </div>
                                </div>

                                <h6>Danh sách tài sản yêu cầu:</h6>
                                <table class="table table-bordered table-hover">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Loại tài sản</th>
                                            <th>Gợi ý tên</th>
                                            <th>Số lượng</th>
                                            <th>Ghi chú</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${items}">
                                            <tr>
                                                <td>${item.categoryName}</td> <td>${item.assetNameHint}</td>
                                                <td>${item.quantity}</td> 
                                                <td>${item.note}</td> 
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>

                                <c:if test="${not empty approval}">
                                    <div class="alert ${approval.decision == 'APPROVED' ? 'alert-success' : 'alert-danger'} mt-4">
                                        <h6>Phản hồi từ Ban Giám Hiệu:</h6>
                                        <p class="mb-1"><strong>Quyết định:</strong> ${approval.decision}</p>
                                        <p class="mb-1"><strong>Lý do/Ghi chú:</strong> ${approval.decisionNote}</p> 
                                        <small>Duyệt bởi ID: ${approval.approverId} vào lúc ${approval.decidedAt}</small> 
                                    </div>
                                </c:if>
                            </div>
                            <div class="card-footer text-end">
                                <a href="request-list" class="btn btn-secondary">Quay lại</a>
                            </div>
                        </div>
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <%@ include file="/views/layout/allocation/notification.jsp" %>
    </body>
</html>