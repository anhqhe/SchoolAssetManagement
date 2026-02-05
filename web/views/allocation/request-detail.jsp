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
        <title>Chi tiết yêu cầu #${req.requestCode}</title>
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

                    <!-- Message Begin -->
                    <c:if test="${param.msg eq 'success'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-check-circle"></i>
                            Thành công!
                        </div>
                    </c:if>

                    <c:if test="${param.msg eq 'error'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <i class="fas fa-exclamation-triangle"></i>
                            Có lỗi xảy ra!
                        </div>
                    </c:if>
                    <!-- Message End -->

                    <div class="container mt-4">
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">Chi tiết phiếu: ${req.requestCode}</h1>
                            <c:choose>
                                <c:when test="${isTeacher}">
                                    <a href="${pageContext.request.contextPath}/teacher/request-list" class="btn btn-sm btn-secondary shadow-sm">
                                        <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                                    </a>
                                </c:when>
                                <c:when test="${isStaff}">
                                    <a href="${pageContext.request.contextPath}/staff/allocation-list" class="btn btn-sm btn-secondary shadow-sm">
                                        <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/board/approval-center" class="btn btn-sm btn-secondary shadow-sm">
                                        <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Teacher View Begin -->
                        <c:if test="${isTeacher}">
                            <div class="row">
                                <div class="col-md-8 mx-auto">
                                    <div class="card shadow">
                                        <div class="card-header bg-white d-flex justify-content-between">
                                            <h5 class="mb-0">Thông tin phiếu: ${req.requestCode}</h5>
                                            <span class="badge text-white ${req.status == 'REJECTED' ? 'bg-danger' : 'bg-primary'}">${req.status}</span>
                                        </div>
                                        <div class="card-body">
                                            <div class="row mb-4">
                                                <div class="col-md-6">
                                                    <p><strong>Người yêu cầu:</strong> ${req.teacherName}</p>
                                                    <p><strong>Phòng nhận:</strong> ${req.roomName}</p>
                                                </div>
                                                <div class="col-md-6 text-md-end">
                                                    <p><strong>Ngày tạo:</strong>
                                                        ${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
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
                                                            <td>${item.categoryName}</td>
                                                            <td>${item.assetNameHint}</td>
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
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <!-- Teacher View End -->

                        <!-- Staff View Begin -->
                        <c:if test="${isStaff}">
                            <div class="row align-items-stretch">
                                <div class="col-lg-4 d-flex">
                                    <div class="card shadow mb-4 h-100 w-100">
                                        <div class="card-header py-3">
                                            <h6 class="m-0 font-weight-bold text-primary">Thông tin chung</h6>
                                        </div>
                                        <div class="card-body">
                                            <p><strong>Giáo viên:</strong> ${req.teacherName}</p>
                                            <p><strong>Phòng sử dụng:</strong> ${req.roomName}</p>
                                            <p><strong>Ngày tạo:</strong>
                                                ${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                            </p>
                                            <p><strong>Trạng thái:</strong>
                                                <span class="badge badge-warning">${req.status}</span>
                                            </p>
                                            <hr>
                                            <p><strong>Mục đích:</strong><br>${req.purpose}</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-lg-8 d-flex">
                                    <div class="card shadow mb-4 h-100 w-100">
                                        <div class="card-header py-3 d-flex justify-content-between align-items-center">
                                            <h6 class="m-0 font-weight-bold text-primary">Danh mục thiết bị yêu cầu</h6>
                                        </div>
                                        <div class="card-body">
                                            <div class="table-responsive">
                                                <table class="table table-bordered">
                                                    <thead class="bg-light">
                                                        <tr>
                                                            <th>Loại tài sản</th>
                                                            <th class="text-center">Số lượng</th>
                                                            <th>Ghi chú</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${itemList}">
                                                            <tr>
                                                                <td><strong>${item.categoryName}</strong></td>
                                                                <td class="text-center">${item.quantity}</td>
                                                                <td>${item.note}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <c:if test="${not empty allocatedAssets}">
                                <div class="card shadow mb-4 border-left-primary mt-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            Danh sách tài sản đã bàn giao thực tế
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="table-responsive">
                                            <table class="table table-hover">
                                                <thead class="thead-light">
                                                    <tr>
                                                        <th>Mã tài sản</th>
                                                        <th>Tên tài sản</th>
                                                        <th>Trạng thái</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="asset" items="${allocatedAssets}">
                                                        <tr>
                                                            <td>${asset.assetCode}</td>
                                                            <td>${asset.assetName}</td>
                                                            <td><span class="badge badge-success">${asset.status}</span></td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${not empty approval}">
                                <div class="alert ${approval.decision == 'APPROVED' ? 'alert-success' : 'alert-danger'} mt-4">
                                    <h6>Phản hồi từ Ban Giám Hiệu:</h6>
                                    <p class="mb-1"><strong>Quyết định:</strong> ${approval.decision}</p>
                                    <p class="mb-1"><strong>Lý do/Ghi chú:</strong> ${approval.decisionNote}</p>
                                    <small>Duyệt bởi ID: ${approval.approverId} vào lúc ${approval.decidedAt}</small>
                                </div>
                            </c:if>
                        </c:if>
                        <!-- Staff View End -->

                        <!-- Board View Begin -->
                        <c:if test="${isBoard}">
                            <div class="row align-items-stretch">
                                <div class="col-lg-4 d-flex">
                                    <div class="card shadow mb-4 h-100 w-100">
                                        <div class="card-header py-3">
                                            <h6 class="m-0 font-weight-bold text-primary">Thông tin chung</h6>
                                        </div>
                                        <div class="card-body">
                                            <p><strong>Giáo viên:</strong> ${req.teacherName}</p>
                                            <p><strong>Phòng sử dụng:</strong> ${req.roomName}</p>
                                            <p><strong>Ngày tạo:</strong>
                                                ${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                            </p>
                                            <p><strong>Trạng thái:</strong>
                                                <span class="badge badge-warning">${req.status}</span>
                                            </p>
                                            <hr>
                                            <p><strong>Mục đích:</strong><br>${req.purpose}</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-lg-8 d-flex">
                                    <div class="card shadow mb-4 h-100 w-100">
                                        <div class="card-header py-3 d-flex justify-content-between align-items-center">
                                            <h6 class="m-0 font-weight-bold text-primary">Danh mục thiết bị yêu cầu</h6>
                                        </div>
                                        <div class="card-body">
                                            <div class="table-responsive">
                                                <table class="table table-bordered">
                                                    <thead class="bg-light">
                                                        <tr>
                                                            <th>Loại tài sản</th>
                                                            <th class="text-center">Số lượng</th>
                                                            <th>Ghi chú</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${itemList}">
                                                            <tr>
                                                                <td><strong>${item.categoryName}</strong></td>
                                                                <td class="text-center">${item.quantity}</td>
                                                                <td>${item.note}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <c:if test="${not empty allocatedAssets}">
                                <div class="card shadow mb-4 border-left-primary mt-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            Danh sách tài sản đã bàn giao thực tế
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="table-responsive">
                                            <table class="table table-hover">
                                                <thead class="thead-light">
                                                    <tr>
                                                        <th>Mã tài sản</th>
                                                        <th>Tên tài sản</th>
                                                        <th>Trạng thái</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="asset" items="${allocatedAssets}">
                                                        <tr>
                                                            <td>${asset.assetCode}</td>
                                                            <td>${asset.assetName}</td>
                                                            <td><span class="badge badge-success">${asset.status}</span></td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:if>
                        <!-- Board View End -->
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
