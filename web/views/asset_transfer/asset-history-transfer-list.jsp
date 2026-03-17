<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử điều chuyển tài sản</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">
<div id="wrapper">

    <%@ include file="/views/layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="/views/layout/topbar.jsp" %>

            <div class="container-fluid">

                <!-- TITLE -->
                <h1 class="h3 mb-4 text-gray-800">
                    <i class="fas fa-history text-primary"></i>
                    Lịch sử điều chuyển tài sản
                </h1>

                <!-- FILTER -->
                <div class="card shadow mb-4">
                    <div class="card-body">
                        <form method="get"
                              action="${pageContext.request.contextPath}/asset-history-transfer/list">

                            <div class="row align-items-end">

                                <div class="col-md-3">
                                    <input type="text" name="keyword"
                                           value="${keyword}"
                                           class="form-control"
                                           placeholder="Tên hoặc mã tài sản...">
                                </div>

                                <div class="col-md-2">
                                    <input type="date" name="fromDate"
                                           value="${fromDate}"
                                           class="form-control">
                                </div>

                                <div class="col-md-2">
                                    <input type="date" name="toDate"
                                           value="${toDate}"
                                           class="form-control">
                                </div>

                                <div class="col-md-2">
                                    <label class="small text-muted">Hiển thị</label>
                                    <select name="pageSize" class="form-control">
                                        <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                        <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                        <option value="100" ${pageSize == 100 ? 'selected' : ''}>100</option>
                                    </select>
                                </div>

                                <div class="col-md-2">
                                    <button class="btn btn-primary w-100">
                                        <i class="fas fa-search"></i> Tìm
                                    </button>
                                </div>

                            </div>
                        </form>
                    </div>
                </div>

                <!-- TIMELINE -->
                <c:forEach var="entry" items="${groupedHistory}">

                    <div class="card shadow mb-3">

                        <!-- ASSET NAME -->
                        <div class="card-header bg-light">
                            <span class="text-warning font-weight-bold">
                                <i class="fas fa-box"></i> ${entry.key}
                            </span>
                        </div>

                        <!-- HISTORY LIST -->
                        <div class="card-body p-2">

                            <c:forEach var="h" items="${entry.value}">

                                <div class="d-flex justify-content-between align-items-center border-bottom py-2">

                                    <!-- DATE -->
                                    <div style="width:180px;">
                                        <fmt:formatDate value="${h.createdAt}"
                                                        pattern="dd/MM/yyyy HH:mm"/>
                                    </div>

                                    <!-- FROM -> TO -->
                                    <div class="flex-grow-1">

                                        <span class="text-danger">
                                            <i class="fas fa-sign-out-alt"></i>
                                            ${h.fromRoomName}
                                        </span>

                                        <i class="fas fa-arrow-right mx-2 text-muted"></i>

                                        <span class="text-success">
                                            <i class="fas fa-sign-in-alt"></i>
                                            ${h.toRoomName}
                                        </span>

                                    </div>

                                    <!-- CODE -->
                                    <div style="width:200px;" class="text-primary font-weight-bold">
                                        ${h.transferCode}
                                    </div>

                                </div>

                            </c:forEach>

                        </div>

                    </div>

                </c:forEach>

                <!-- EMPTY -->
                <c:if test="${empty groupedHistory}">
                    <div class="text-center text-muted">
                        Không có dữ liệu
                    </div>
                </c:if>

                <!-- PAGINATION -->
                <div class="card shadow mt-4">
                    <div class="card-body">

                        <div class="d-flex justify-content-between align-items-center">

                            <div>
                                Tổng: <strong>${totalItems}</strong> bản ghi
                            </div>

                        </div>

                        <nav class="mt-3">
                            <ul class="pagination justify-content-center">

                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="?page=${currentPage - 1}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">
                                        Trước
                                    </a>
                                </li>

                                <c:set var="start" value="${currentPage - 2 > 1 ? currentPage - 2 : 1}" />
                                <c:set var="end" value="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}" />

                                <c:forEach begin="${start}" end="${end}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link"
                                           href="?page=${i}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">
                                            ${i}
                                        </a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="?page=${currentPage + 1}&pageSize=${pageSize}&keyword=${keyword}&fromDate=${fromDate}&toDate=${toDate}">
                                        Sau
                                    </a>
                                </li>

                            </ul>
                        </nav>

                    </div>
                </div>

            </div>
        </div>

        <%@ include file="/views/layout/footer.jsp" %>
    </div>
</div>
</body>
</html>
