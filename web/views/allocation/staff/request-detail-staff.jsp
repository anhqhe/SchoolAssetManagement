
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
                            <i class="fas fa-check-circle"></i>
                            Thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${param.msg eq 'error'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle"></i>
                            Lỗi
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    <!-- Message End -->

                    <div class="container mt-4">
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">Chi tiết phiếu: ${req.requestCode}</h1>
                            <a href="${pageContext.request.contextPath}/staff/allocation-list" class="btn btn-sm btn-secondary shadow-sm">
                                <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                            </a>
                        </div>

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
                                                    <th>Mã tài sản (Asset Code)</th>
                                                    <th>Tên tài sản</th>
                                                    <th>Loại</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="asset" items="${allocatedAssets}">
                                                    <tr>
                                                        <td class="font-weight-bold text-primary">${asset.assetCode}</td>
                                                        <td>${asset.assetName}</td>
                                                        <td><span class="badge badge-info">${asset.categoryName}</span></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <div class="card-footer text-right">
                            <!--
                            <c:if test="${sessionScope.user.role == 'BOARD' && req.status == 'WAITING_BOARD'}">
                                <button class="btn btn-danger" onclick="showRejectModal()">Từ chối</button>
                                <button class="btn btn-success" onclick="approveRequest(${req.requestId})">Duyệt yêu cầu</button>
                            </c:if>

                            <c:if test="${sessionScope.user.role == 'STAFF' && req.status == 'APPROVED_BY_BOARD'}">
                                <a href="allocate-assets?requestId=${req.requestId}" class="btn btn-success">
                                    Tiến hành cấp phát tài sản
                                </a>
                            </c:if>

                            <c:if test="${req.status == 'COMPLETED'}">
                                <div class="mt-3">
                                    <button class="btn btn-primary" onclick="window.print()">
                                        <i class="fas fa-print"></i> In biên bản bàn giao
                                    </button>
                                </div>
                            </c:if>
                            -->

                            <!--  DEMO BEGIN-->
                            <!--                            <button class="btn btn-danger" onclick="showRejectModal()">Từ chối</button>
                                                        <button class="btn btn-success" onclick="approveRequest(${req.requestId})">Duyệt yêu cầu</button>-->

                            <c:if test="${req.status == 'APPROVED_BY_BOARD'}">
                                <a href="allocate-assets?requestId=${req.requestId}" class="btn btn-success">
                                    Tiến hành cấp phát tài sản
                                </a>
                            </c:if>
                            <div class="mt-3">
                                <button class="btn btn-primary" onclick="window.print()">
                                    <i class="fas fa-print"></i> In biên bản bàn giao
                                </button>
                            </div>
                            <!--  DEMO END-->
                        </div>
                    </div>

                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <%@ include file="/views/layout/allocation/notification.jsp" %>
    </body>
</html>