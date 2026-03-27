<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tài sản được cấp phát</title>
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
    </head>

    <body id="page-top">
        <div id="wrapper">
            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">
                    <%@ include file="/views/layout/allocation/topbar2.jsp" %>

                    <c:if test="${not empty sessionScope.message}">
                        <div class="alert alert-${sessionScope.type eq 'error' ? 'danger' : (sessionScope.type eq 'warning' ? 'warning' : (sessionScope.type eq 'info' ? 'info' : 'success'))} alert-dismissible fade show m-3">
                            ${sessionScope.message}
                            <button type="button" class="close" data-dismiss="alert">
                                <span>&times;</span>
                            </button>
                        </div>
                        <c:remove var="type" scope="session" />
                        <c:remove var="message" scope="session" />
                    </c:if>

                    <div class="container-fluid">
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-box-open text-primary"></i>
                                Tài sản được cấp phát
                            </h1>
                        </div>

                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-filter"></i> Tìm kiếm và lọc
                                </h6>
                            </div>
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/teacher/asset-list" method="get" class="form-row align-items-end">
                                    <div class="form-group col-md-3">
                                        <label>Tìm kiếm</label>
                                        <input type="text"
                                               name="keyword"
                                               class="form-control"
                                               placeholder="Mã tài sản, tên, loại, phòng..."
                                               value="${keyword}">
                                    </div>

                                    <div class="form-group col-md-2">
                                        <label>Trạng thái</label>
                                        <select name="status" class="form-control">
                                            <option value="">-- Tất cả --</option>
                                            <option value="IN_USE" ${status == 'IN_USE' ? 'selected' : ''}>Đang sử dụng</option>
                                        </select>
                                    </div>


                                    <div class="form-group col-md-2">
                                        <label>Từ ngày</label>
                                        <input type="date"
                                               name="fromDate"
                                               class="form-control"
                                               value="${fromDate}">
                                    </div>

                                    <div class="form-group col-md-2">
                                        <label>Đến ngày</label>
                                        <input type="date"
                                               name="toDate"
                                               class="form-control"
                                               value="${toDate}">
                                    </div>
                                    <div class="form-group col-md-2">
                                        <button type="submit" class="btn btn-primary mr-2">
                                            <i class="fas fa-search"></i> Tìm
                                        </button>
                                        <a href="${pageContext.request.contextPath}/teacher/asset-list" class="btn btn-outline-secondary">
                                            <i class="fas fa-redo"></i> Đặt lại
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-list"></i> Danh sách tài sản được cấp phát
                                    <span class="badge badge-primary">${assetList != null ? assetList.size() : 0}</span>
                                </h6>
                            </div>

                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover" id="dataTable" width="100%" cellspacing="0">
                                        <thead class="thead-light">
                                            <tr>
                                                <th>Mã Tài Sản</th>
                                                <th>Tên Tài Sản</th>
                                                <th>Loại</th>
                                                <th>Phòng hiện tại</th>
                                                <th>Thời gian cấp</th>
                                                <th>Trạng thái</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${empty assetList}">
                                                    <tr>
                                                        <td colspan="6" class="text-center text-muted py-5">
                                                            <i class="fas fa-inbox fa-3x mb-3"></i>
                                                            <p>Chưa có tài sản nào được cấp cho bạn</p>
                                                        </td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="a" items="${assetList}">
                                                        <tr>
                                                            <td><strong>${a.assetCode}</strong></td>
                                                            <td>${a.assetName}</td>
                                                            <td>${not empty a.categoryName ? a.categoryName : '-'}</td>
                                                            <td>${not empty a.currentRoomName ? a.currentRoomName : '-'}</td>
                                                            <td data-sort="${a.allocatedAt}">
                                                                <c:if test="${a.allocatedAt != null}">
                                                                    <small>${a.allocatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</small>
                                                                </c:if>
                                                                <c:if test="${a.allocatedAt == null}">-</c:if>
                                                                </td>
                                                                <td>
                                                                <c:choose>
                                                                    <c:when test="${a.status == 'IN_USE'}">
                                                                        <span class="badge badge-success">Đang sử dụng</span>
                                                                    </c:when>

                                                                    <c:otherwise>
                                                                        <span class="badge badge-info">${a.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/assets/detail?id=${a.assetId}" 
                                                                   class="btn btn-sm btn-info" title="Xem chi tiết">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
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

        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

        <script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

        <script>
            $(document).ready(function () {
                $('#dataTable').DataTable({
                    "searching": false,
                    "language": {
                        "lengthMenu": "Hiển thị _MENU_ tài sản mỗi trang",
                        "zeroRecords": "Không tìm thấy tài sản nào",
                        "info": "Trang _PAGE_ / _PAGES_",
                        "infoEmpty": "Không có dữ liệu",
                        "infoFiltered": "(lọc từ _MAX_ tài sản)",
                        "search": "Tìm kiếm:",
                        "paginate": {
                            "first": "Đầu",
                            "last": "Cuối",
                            "next": "Sau",
                            "previous": "Trước"
                        }
                    },
                    "pageLength": 10,
                    "order": [[4, "desc"]]
                });
            });
        </script>
    </body>
</html>

