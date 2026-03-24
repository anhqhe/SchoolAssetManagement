<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.asset.AssetIncreaseRecord" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    List<AssetIncreaseRecord> records = (List<AssetIncreaseRecord>) request.getAttribute("records");
    if (records == null) {
        records = new java.util.ArrayList<>();
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử ghi tăng tài sản | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">

<div id="wrapper">

    <%@ include file="/views/layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="/views/layout/topbar.jsp" %>

            <!-- Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-plus-circle text-success"></i> Lịch sử ghi tăng tài sản
                    </h1>
                </div>

                <!-- Filter Card -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-filter"></i> Tìm kiếm & Lọc
                        </h6>
                    </div>
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/asset-increase" class="form-inline">
                            <div class="form-group mr-3 mb-2">
                                <input type="text"
                                       name="keyword"
                                       class="form-control"
                                       placeholder="Tìm mã phiếu, nguồn gốc..."
                                       value="${keyword}">
                            </div>
                            <div class="form-group mr-3 mb-2">
                                <label class="mr-2">Từ ngày</label>
                                <input type="date" name="fromDate" class="form-control" value="${fromDate}">
                            </div>
                            <div class="form-group mr-3 mb-2">
                                <label class="mr-2">Đến ngày</label>
                                <input type="date" name="toDate" class="form-control" value="${toDate}">
                            </div>
                            <button type="submit" class="btn btn-primary mb-2 mr-2">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>
                            <a href="${pageContext.request.contextPath}/asset-increase" class="btn btn-secondary mb-2">
                                <i class="fas fa-redo"></i> Đặt lại
                            </a>
                        </form>
                    </div>
                </div>

                <!-- Data Table Card -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3 d-flex justify-content-between align-items-center">
                        <h6 class="m-0 font-weight-bold text-primary">
                            Danh sách phiếu ghi tăng
                            <span class="badge badge-primary ml-2">${totalRecords} phiếu</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" width="100%" cellspacing="0">
                                <thead class="thead-light">
                                    <tr>
                                        <th>Mã phiếu</th>
                                        <th>Nguồn gốc</th>
                                        <th>Ngày nhận</th>
                                        <th>Người tạo</th>
                                        <th class="text-center">Số tài sản</th>
                                        <th>Ghi chú</th>
                                        <th>Ngày tạo</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (records != null && !records.isEmpty()) {
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                            SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            for (AssetIncreaseRecord rec : records) {
                                    %>
                                    <tr>
                                        <td>
                                            <strong><%= rec.getIncreaseCode() != null ? rec.getIncreaseCode() : "" %></strong>
                                        </td>
                                        <td>
                                            <span class="badge badge-info"><%= rec.getSourceTypeText() %></span>
                                        </td>
                                        <td>
                                            <%= rec.getReceivedDateAsDate() != null ? sdf.format(rec.getReceivedDateAsDate()) : "-" %>
                                        </td>
                                        <td><%= rec.getCreatedByName() != null ? rec.getCreatedByName() : "-" %></td>
                                        <td class="text-center">
                                            <span class="badge badge-success"><%= rec.getItemCount() %></span>
                                        </td>
                                        <td><%= rec.getNote() != null ? rec.getNote() : "-" %></td>
                                        <td>
                                            <%= rec.getCreatedAtAsDate() != null ? sdfFull.format(rec.getCreatedAtAsDate()) : "-" %>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/asset-increase?action=detail&id=<%= rec.getIncreaseId() %>"
                                               class="btn btn-sm btn-info" title="Xem chi tiết">
                                                <i class="fas fa-eye"></i> Chi tiết
                                            </a>
                                        </td>
                                    </tr>
                                    <%
                                            }
                                        } else {
                                    %>
                                    <tr>
                                        <td colspan="8" class="text-center text-muted">Chưa có phiếu ghi tăng nào</td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>

                            <!-- Phân trang -->
                            <c:if test="${totalPages != null && totalPages > 1}">
                                <nav aria-label="Page navigation">
                                    <ul class="pagination justify-content-center mt-3">
                                        <!-- Previous -->
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/asset-increase?page=${currentPage - 1}&keyword=${fn:escapeXml(keyword)}&fromDate=${fromDate}&toDate=${toDate}">
                                                Trước
                                            </a>
                                        </li>
                                        <!-- Page numbers -->
                                        <c:forEach var="i" begin="1" end="${totalPages}">
                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <a class="page-link"
                                                   href="${pageContext.request.contextPath}/asset-increase?page=${i}&keyword=${fn:escapeXml(keyword)}&fromDate=${fromDate}&toDate=${toDate}">
                                                    ${i}
                                                </a>
                                            </li>
                                        </c:forEach>
                                        <!-- Next -->
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/asset-increase?page=${currentPage + 1}&keyword=${fn:escapeXml(keyword)}&fromDate=${fromDate}&toDate=${toDate}">
                                                Sau
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>

                        </div>
                    </div>
                </div>

                <%@ include file="/views/layout/footer.jsp" %>

            </div>
        </div>

        <!-- Scripts -->
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
    </div>
</div>

</body>
</html>
