<%-- 
    Document   : asset-usage
    Created on : Mar 16, 2026, 12:30:47 PM
    Author     : An
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Báo cáo sử dụng tài sản</title>
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <!-- ★ THÊM MỚI: Chart.js CDN -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
    </head>
    <body id="page-top">
        <div id="wrapper">
            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">
                    <%@ include file="/views/layout/topbar.jsp" %>

                    <div class="container-fluid">

                        <!-- ========== TIÊU ĐỀ ========== -->
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-file-alt text-primary"></i> Asset Usage Report
                            </h1>
                        </div>

                        <!-- ========== ★ BIỂU ĐỒ (MỚI) ========== -->
                        <div class="row">
                            <!-- Biểu đồ cột: số tài sản theo phòng -->
                            <div class="col-xl-6 col-lg-6">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-chart-bar"></i> Biểu đồ theo Phòng
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <canvas id="usageBarChart"></canvas>
                                    </div>
                                </div>
                            </div>
                            <!-- Biểu đồ tròn: tỷ lệ tài sản theo phòng -->
                            <div class="col-xl-6 col-lg-6">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-chart-pie"></i> Tỷ lệ theo Phòng
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <canvas id="usagePieChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- ========== ★ SEARCH + FILTER (MỚI) ========== -->
                        <div class="card shadow mb-4">
                            <div class="card-body">
                                <form method="get" action="${pageContext.request.contextPath}/asset-report"
                                      class="form-inline">
                                    <input type="hidden" name="type" value="usage"/>

                                    <!-- Dropdown lọc theo Phòng -->
                                    <div class="form-group mr-3 mb-2">
                                        <label class="mr-2 font-weight-bold">Phòng:</label>
                                        <select name="roomFilter" class="form-control form-control-sm">
                                            <option value="">-- Tất cả --</option>
                                            <c:forEach var="room" items="${rooms}">
                                                <option value="${room[0]}"
                                                        ${roomFilter == room[0] ? 'selected' : ''}>
                                                    ${room[1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- Ô tìm kiếm -->
                                    <div class="form-group mr-3 mb-2">
                                        <label class="mr-2 font-weight-bold">Tìm kiếm:</label>
                                        <input type="text" name="search" value="${search}"
                                               class="form-control form-control-sm"
                                               placeholder="Nhập tên phòng / danh mục..."/>
                                    </div>

                                    <button type="submit" class="btn btn-primary btn-sm mb-2">
                                        <i class="fas fa-search"></i> Lọc
                                    </button>
                                    <a href="${pageContext.request.contextPath}/asset-report?type=usage"
                                       class="btn btn-secondary btn-sm mb-2 ml-2">
                                        <i class="fas fa-sync"></i> Bỏ lọc
                                    </a>
                                </form>
                            </div>
                        </div>

                        <!-- ========== BẢNG DỮ LIỆU (GIỮ NGUYÊN CẤU TRÚC CŨ) ========== -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Tài sản đang sử dụng theo phòng & danh mục</h6>
                            </div>
                            <div class="card-body">
                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>Phòng</th>
                                            <th>Danh mục</th>
                                            <th>Số tài sản đang sử dụng</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="row" items="${usageData}">
                                            <tr>
                                                <td>${row.roomName}</td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/asset-report?type=usageDetail&roomId=${row.roomId}&categoryId=${row.categoryId}">
                                                        ${row.categoryName}
                                                    </a>
                                                </td>
                                                <td class="text-right">${row.totalAssets}</td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty usageData}">
                                            <tr>
                                                <td colspan="3" class="text-center">Không có dữ liệu</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>

                                <!-- ========== ★ PHÂN TRANG (MỚI) ========== -->
                                <c:if test="${totalPages > 1}">
                                    <nav aria-label="Page navigation">
                                        <ul class="pagination justify-content-center mt-3">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link"
                                                   href="${pageContext.request.contextPath}/asset-report?type=usage&roomFilter=${roomFilter}&search=${search}&page=${currentPage - 1}">
                                                    &laquo; Trước
                                                </a>
                                            </li>

                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/asset-report?type=usage&roomFilter=${roomFilter}&search=${search}&page=${i}">
                                                        ${i}
                                                    </a>
                                                </li>
                                            </c:forEach>

                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link"
                                                   href="${pageContext.request.contextPath}/asset-report?type=usage&roomFilter=${roomFilter}&search=${search}&page=${currentPage + 1}">
                                                    Sau &raquo;
                                                </a>
                                            </li>
                                        </ul>
                                    </nav>
                                    <p class="text-center text-muted small">Trang ${currentPage} / ${totalPages}</p>
                                </c:if>

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

        <!-- ========== ★ JAVASCRIPT VẼ BIỂU ĐỒ (MỚI) ========== -->
        <script>
            // ── Bước 1: Dùng JSTL đổ dữ liệu vào biến JS ──
            // roomMap sẽ có dạng: { "Phòng 101": 5, "Phòng 102": 3, ... }
            var roomMap = {};

            // chartData chứa TOÀN BỘ dữ liệu (không bị phân trang)
            <c:forEach var="row" items="${chartData}">
            if (!roomMap['${row.roomName}']) {
                roomMap['${row.roomName}'] = 0;
            }
            roomMap['${row.roomName}'] += ${row.totalAssets};
            </c:forEach>

            var rooms = Object.keys(roomMap);          // ["Phòng 101", "Phòng 102", ...]
            var roomTotals = rooms.map(function (r) {
                return roomMap[r];
            });  // [5, 3, ...]
            var colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b',
                '#858796', '#fd7e14', '#6f42c1', '#20c9a6', '#5a5c69'];

            // ── Bước 2: Vẽ Bar Chart ──
            new Chart(document.getElementById('usageBarChart'), {
                type: 'bar',
                data: {
                    labels: rooms,
                    datasets: [{
                            label: 'Số tài sản đang sử dụng',
                            data: roomTotals,
                            backgroundColor: '#4e73df'
                        }]
                },
                options: {
                    responsive: true,
                    scales: {y: {beginAtZero: true}},
                    plugins: {legend: {display: false}}
                }
            });

            // ── Bước 3: Vẽ Doughnut Chart ──
            new Chart(document.getElementById('usagePieChart'), {
                type: 'doughnut',
                data: {
                    labels: rooms,
                    datasets: [{
                            data: roomTotals,
                            backgroundColor: colors.slice(0, rooms.length)
                        }]
                },
                options: {
                    responsive: true,
                    plugins: {legend: {position: 'bottom'}}
                }
            });
        </script>

    </body>
</html>
