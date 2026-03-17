<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="dao.allocation.UserDAO" %>
<%@page import="dao.allocation.AllocationDAO" %>

<!-- Set role flags -->
<c:set var="isTeacher" value="false"/>
<c:set var="isStaff" value="false"/>
<c:set var="isBoard" value="false"/>
<c:forEach var="role" items="${sessionScope.currentUser.roles}">
    <c:if test="${role eq 'TEACHER'}"><c:set var="isTeacher" value="true"/></c:if>
    <c:if test="${role eq 'ASSET_STAFF'}"><c:set var="isStaff" value="true"/></c:if>
    <c:if test="${role eq 'BOARD'}"><c:set var="isBoard" value="true"/></c:if>
</c:forEach>

<%
    UserDAO userDAO = new UserDAO();
    request.setAttribute("userDAO", userDAO);
    AllocationDAO allocationDAO = new AllocationDAO();
    request.setAttribute("allocationDAO", allocationDAO);
%>

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

                    <%@ include file="/views/layout/allocation/topbar2.jsp" %>

                    <!-- Page Content -->
                    <div class="container mt-4">

                        <!-- Header: Title + Back Button (COMMON) -->
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-info-circle text-primary"></i>
                                Chi tiết phiếu: ${req.requestCode}
                            </h1>
                            <c:choose>
                                <c:when test="${isTeacher}">
                                    <a href="${pageContext.request.contextPath}/teacher/request-list" class="btn btn-sm btn-secondary shadow-sm">
                                        <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                                    </a>
                                </c:when>
                                <c:when test="${isStaff}">
                                    <a href="${pageContext.request.contextPath}/staff/request-list" class="btn btn-sm btn-secondary shadow-sm">
                                        <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/board/request-list" class="btn btn-sm btn-secondary shadow-sm">
                                        <i class="fas fa-arrow-left fa-sm"></i> Quay lại danh sách
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Message Alert (COMMON) -->
                        <c:if test="${not empty sessionScope.message}">
                            <div class="alert alert-${sessionScope.type eq 'error' ? 'danger' : (sessionScope.type eq 'warning' ? 'warning' : (sessionScope.type eq 'info' ? 'info' : 'success'))} alert-dismissible fade show" role="alert">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                                <i class="fas fa-${sessionScope.type eq 'error' ? 'exclamation-triangle' : (sessionScope.type eq 'warning' ? 'exclamation-triangle' : (sessionScope.type eq 'info' ? 'info-circle' : 'check-circle'))}"></i>
                                ${sessionScope.message}
                            </div>
                            <c:remove var="type" scope="session" />
                            <c:remove var="message" scope="session" />
                        </c:if>

                        <!-- COMMON: General Info & Request Items  -->
                        <div class="row">
                            <div class="col-lg-6">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">Thông tin chung</h6>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Giáo viên:</strong> ${req.teacherName}</p>
                                        <p><strong>Phòng sử dụng:</strong> ${req.roomName}</p>
                                        <p><strong>Ngày tạo:</strong> ${req.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</p>
                                        <p>
                                            <strong>Trạng thái:</strong> 
                                            <c:choose>
                                                <c:when test="${req.status == 'WAITING_BOARD'}">
                                                    <span class="badge badge-warning">Chờ Phê Duyệt</span>
                                                </c:when>
                                                <c:when test="${req.status == 'APPROVED_BY_BOARD'}">
                                                    <span class="badge badge-primary">Đã Phê Duyệt</span>
                                                </c:when>
                                                <c:when test="${req.status == 'COMPLETED'}">
                                                    <span class="badge badge-success">Hoàn Thành</span>
                                                </c:when>
                                                <c:when test="${req.status == 'REJECTED'}">
                                                    <span class="badge badge-danger">Từ Chối</span>
                                                </c:when>
                                                <c:when test="${req.status == 'OUT_OF_STOCK'}">
                                                    <span class="badge badge-dark">Hết Tài Sản</span>
                                                </c:when>
                                                <c:when test="${req.status == 'INCOMPLETE'}">
                                                    <span class="badge badge-secondary">Chưa Hoàn Thành</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-info">${req.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <hr>
                                        <p><strong>Mục đích:</strong><br>${req.purpose}</p>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-6">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
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





                        <!-- All: Allocated / Distributed Assets -->
                        <c:if test="${(isTeacher || isStaff || isBoard) && not empty allocatedAssets}">
                            <div class="card shadow mb-4">
                                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                                    <h6 class="m-0 font-weight-bold text-primary">Tài sản đã được phân phối</h6>
                                </div>
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead class="thead-light">
                                                <tr>
                                                    <th>Mã tài sản</th>
                                                    <th>Tên tài sản</th>
                                                    <th>Danh mục</th>
                                                    <th>Giao cho</th>
                                                    <th>Tình trạng</th>
                                                    <th>Người phân phối</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="asset" items="${allocatedAssets}">
                                                    <tr>
                                                        <td class="font-weight-bold">${asset.assetCode}</td>
                                                        <td>${asset.assetName}</td>
                                                        <td><span class="badge badge-info">${asset.categoryName}</span></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${asset.currentHolderId != null && asset.currentHolderId > 0}">
                                                                    ${userDAO.getByUserId(asset.currentHolderId).fullName}
                                                                </c:when>
                                                                <c:otherwise>
                                                                    -
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${asset.status == 'GOOD' || asset.status == 'AVAILABLE'}">
                                                                    <span class="badge badge-success">Tốt</span>
                                                                </c:when>
                                                                <c:when test="${asset.status == 'MAINTENANCE' || asset.status == 'NEEDS_REPAIR'}">
                                                                    <span class="badge badge-warning">Cần bảo trì</span>
                                                                </c:when>
                                                                <c:when test="${asset.status == 'IN_USE'}">
                                                                    <span class="badge badge-primary">Đang sử dụng</span>
                                                                </c:when>
                                                                <c:when test="${asset.status == 'IN_STOCK'}">
                                                                    <span class="badge badge-primary">Trong kho</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge badge-secondary">${asset.status}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>${allocationDAO.getAllocatedBy(asset.assetId)}</td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- All: Approval Feedback -->
                        <c:if test="${(isTeacher || isStaff || isBoard) && not empty approval}">                           
                            <div class="card shadow-sm mb-4 border-0">

                                <div class="card-body">

                                    <!-- ===== Phản hồi BGH ===== -->
                                    <div class="d-flex align-items-start mb-3">

                                        <div class="mr-3">
                                            <i class="fas
                                               ${approval.decision == 'APPROVED' ? 'fa-check-circle text-success' : 'fa-times-circle text-danger'}
                                               fa-lg"></i>
                                        </div>

                                        <div class="flex-grow-1">

                                            <h6 class="font-weight-bold mb-2">
                                                Phản hồi từ Ban Giám Hiệu
                                            </h6>

                                            <div class="mb-2">
                                                Quyết định: 
                                                <c:choose>
                                                    <c:when test="${approval.decision == 'APPROVED'}">
                                                        <span class="badge px-3 py-2" style="background: #d4edda; color: #155724; border-radius:20px;">
                                                            Được Phê Duyệt
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${approval.decision == 'REJECTED'}">
                                                        <span class="badge px-3 py-2" style="background: #f8d7da; color: #721c24; border-radius:20px;">
                                                            Từ Chối
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-secondary">${approval.decision}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="mb-2">
                                                <div class="text-muted small">
                                                    Lý do / Ghi chú: ${approval.decisionNote}
                                                </div>
                                            </div>

                                            <div class="text-muted small">
                                                Duyệt bởi 
                                                <strong>${userDAO.getByUserId(approval.approverId).getFullName()}</strong>
                                                • 
                                                ${approval.decidedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}
                                            </div>

                                        </div>
                                    </div>

                                    <!-- ===== Phản hồi Người phân phối ===== -->
                                    <c:if test="${req.status == 'COMPLETED' || req.status == 'OUT_OF_STOCK' || req.status == 'INCOMPLETE'}">
                                        <hr class="my-3">

                                        <div class="d-flex align-items-start">

                                            <div class="mr-3">
                                                <i class="fas fa-box-open text-primary fa-lg"></i>
                                            </div>

                                            <div class="flex-grow-1">

                                                <h6 class="font-weight-bold mb-1">
                                                    Phản hồi từ Người Phân Phối
                                                </h6>

                                                <c:if test="${req.status == 'COMPLETED' || req.status == 'INCOMPLETE'}">
                                                    <ul class="list-group list-group-flush small">
                                                        <c:forEach items="${allocations}" var="allo">
                                                            <li class="list-group-item py-1">
                                                                <span class="text-muted">
                                                                    ${allo.allocatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}
                                                                </span>
                                                                - <strong>${userDAO.getByUserId(allo.allocatedById).fullName}</strong>
                                                                <c:if test="${not empty allo.note}">
                                                                    <div class="text-secondary">Ghi chú: ${allo.note}</div>
                                                                </c:if>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </c:if>

                                                <c:if test = "${req.status == 'OUT_OF_STOCK'}">
                                                    Kho đang hết tài sản. Vui lòng chờ!!
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>

                        <!-- TEACHER & STAFF: Feedback của giáo viên cho staff -->
                        <c:set var="feedback" value="${teacherFeedback}" />
                        <c:if test="${(isTeacher || isStaff) && (req.status == 'COMPLETED' || req.status == 'OUT_OF_STOCK' || req.status == 'INCOMPLETE')}">
                            <div class="card shadow-sm mb-4">
                                <div class="card-header py-3">
                                    <h6 class="m-0 font-weight-bold text-primary">
                                        <i class="fas fa-comment-dots"></i> Feedback từ giáo viên
                                    </h6>
                                </div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${not empty feedback}">
                                            <c:choose>
                                                <c:when test="${isStaff}">
                                                    <button type="button" class="btn btn-outline-info"
                                                            data-toggle="modal" data-target="#teacherFeedbackModal">
                                                        <i class="fas fa-eye"></i> Xem feedback
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <!-- Teacher: cho phép chỉnh sửa feedback đã gửi -->
                                                    <form method="post" action="${pageContext.request.contextPath}/teacher/request-detail">
                                                        <input type="hidden" name="requestId" value="${req.requestId}" />
                                                        <div class="form-group mb-2">
                                                            <label class="mb-1">Nội dung feedback gửi cho staff</label>
                                                            <textarea name="comment" class="form-control" rows="4"><c:out value="${feedback.comment}" /></textarea>
                                                        </div>
                                                        <c:if test="${feedback.createdAt != null}">
                                                            <div class="text-muted small mb-2">
                                                                Đã gửi lúc: ${feedback.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}
                                                            </div>
                                                        </c:if>
                                                        <button type="submit" class="btn btn-primary">
                                                            <i class="fas fa-save"></i> Cập nhật feedback
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${isTeacher}">
                                                <form method="post" action="${pageContext.request.contextPath}/teacher/request-detail">
                                                    <input type="hidden" name="requestId" value="${req.requestId}" />
                                                    <div class="form-group">
                                                        <label>Nội dung feedback gửi cho staff</label>
                                                        <textarea name="comment" class="form-control" rows="4"
                                                                  placeholder="Ví dụ: thời gian xử lý, bàn giao, chất lượng tài sản, hỗ trợ của nhân viên..."></textarea>
                                                    </div>
                                                    <button type="submit" class="btn btn-primary">
                                                        <i class="fas fa-paper-plane"></i> Gửi feedback
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${isStaff}">
                                                <div class="text-muted">Giáo viên chưa gửi feedback.</div>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>

                        <!-- STAFF ONLY: Popup xem feedback -->
                        <c:if test="${isStaff && not empty feedback}">
                            <div class="modal fade" id="teacherFeedbackModal" tabindex="-1" role="dialog" aria-hidden="true">
                                <div class="modal-dialog modal-lg" role="document">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">
                                                Feedback của giáo viên • ${req.requestCode}
                                            </h5>
                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                        </div>
                                        <div class="modal-body">
                                            <p class="mb-2"><strong>Giáo viên:</strong> ${req.teacherName}</p>
                                            <c:if test="${feedback.createdAt != null}">
                                                <p class="text-muted small mb-3">
                                                    Gửi lúc: ${feedback.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}
                                                </p>
                                            </c:if>
                                            <div class="border rounded p-3 bg-light">
                                                <c:out value="${feedback.comment}" />
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- TEACHER ONLY: Update Request Buttons -->
                        <c:if test="${isTeacher && req.status == 'WAITING_BOARD'}">
                            <div class="card-body">
                                <a href="${pageContext.request.contextPath}/teacher/update-request?id=${req.requestId}" class="btn btn-primary">
                                    <i class="fas fa-edit"></i> Cập nhật yêu cầu
                                </a>
                            </div>
                        </c:if>

                        <!-- BOARD ONLY: Approve/Reject Buttons -->
                        <c:if test="${isBoard && req.status == 'WAITING_BOARD'}">
                            <button type="button" class="btn btn-primary" onclick="openApproveModal(${req.requestId}, '${req.requestCode}')">
                                <i class="fas fa-check"></i> Phê duyệt/ Từ chối
                            </button>
                        </c:if>

                        <!-- STAFF ONLY: Allocate Assets Button -->
                        <c:if test="${isStaff && (req.status == 'APPROVED_BY_BOARD' 
                                      || req.status == 'OUT_OF_STOCK' 
                                      || req.status == 'INCOMPLETE')}">
                              <div class="card-body">
                                  <a href="${pageContext.request.contextPath}/staff/allocate-assets?requestId=${req.requestId}" class="btn btn-primary">
                                      <i class="fas fa-box-open"></i> Bàn giao tài sản
                                  </a>
                              </div>
                        </c:if>
                    </div>


                </div>

                <%@ include file="/views/layout/footer.jsp" %>
            </div>
        </div>

        <!-- Approval modal + helper for board actions -->
        <div class="modal fade" id="approveModal" tabindex="-1">
            <div class="modal-dialog">
                <form action="${pageContext.request.contextPath}/board/request-list" method="post" class="modal-content">
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
                                <option value="APPROVED">Phê Duyệt</option>
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


