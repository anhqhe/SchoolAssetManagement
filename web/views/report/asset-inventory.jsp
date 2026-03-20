<%-- 
    Document   : asset-inventory
    Created on : Mar 16, 2026, 10:54:29 AM
    Author     : An
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Báo cáo tài sản tồn kho</title>
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
                                <i class="fas fa-file-alt text-primary"></i> Asset Inventory Report
                            </h1>
                        </div>

                        <!-- ========== ★ BIỂU ĐỒ (MỚI) ========== -->
                        <div class="row">
                            <!-- Biểu đồ cột: số tài sản theo danh mục -->
                            <div class="col-xl-6 col-lg-6">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-chart-bar"></i> Biểu đồ theo Danh mục
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <canvas id="inventoryBarChart"></canvas>
                                    </div>
                                </div>
                            </div>
                            <!-- Biểu đồ tròn: tỷ lệ theo trạng thái -->
                            <div class="col-xl-6 col-lg-6">
                                <div class="card shadow mb-4">
                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            <i class="fas fa-chart-pie"></i> Biểu đồ theo Trạng thái
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <canvas id="inventoryPieChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- ========== ★ SEARCH + FILTER (MỚI) ========== -->
                        <div class="card shadow mb-4">
                            <div class="card-body">
                                <form method="get" action="${pageContext.request.contextPath}/asset-report"
                                      class="form-inline">
                                    <!-- Giữ type=inventory khi submit form -->
                                    <input type="hidden" name="type" value="inventory"/>

                                    <!-- Dropdown lọc theo Category -->
                                    <div class="form-group mr-3 mb-2">
                                        <label class="mr-2 font-weight-bold">Danh mục:</label>
                                        <select name="categoryFilter" class="form-control form-control-sm">
                                            <option value="">-- Tất cả --</option>
                                            <c:forEach var="cat" items="${categories}">
                                                <option value="${cat[0]}"
                                                        ${categoryFilter == cat[0] ? 'selected' : ''}>
                                                    ${cat[1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- Ô tìm kiếm -->
                                    <div class="form-group mr-3 mb-2">
                                        <label class="mr-2 font-weight-bold">Tìm kiếm:</label>
                                        <input type="text" name="search" value="${search}"
                                               class="form-control form-control-sm"
                                               placeholder="Nhập tên danh mục..."/>
                                    </div>

                                    <!-- Nút Lọc -->
                                    <button type="submit" class="btn btn-primary btn-sm mb-2">
                                        <i class="fas fa-search"></i> Lọc
                                    </button>

                                    <!-- Nút Bỏ lọc (reset về trang gốc) -->
                                    <a href="${pageContext.request.contextPath}/asset-report?type=inventory"
                                       class="btn btn-secondary btn-sm mb-2 ml-2">
                                        <i class="fas fa-sync"></i> Bỏ lọc
                                    </a>
                                </form>
                            </div>
                        </div>

                        <!-- ========== BẢNG DỮ LIỆU (GIỮ NGUYÊN CẤU TRÚC CŨ) ========== -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Tổng hợp theo danh mục & trạng thái</h6>
                            </div>
                            <div class="card-body">
                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>Danh mục</th>
                                            <th>Trạng thái</th>
                                            <th>Tổng số tài sản</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="row" items="${inventoryData}">
                                            <tr>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/asset-report?type=inventoryDetail&categoryId=${row.categoryId}">
                                                        ${row.categoryName}
                                                    </a>
                                                </td>
                                                <td>${row.status}</td>
                                                <td class="text-right">${row.totalAssets}</td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty inventoryData}">
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
                                            <!-- Nút Trước -->
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link"
                                                   href="${pageContext.request.contextPath}/asset-report?type=inventory&categoryFilter=${categoryFilter}&search=${search}&page=${currentPage - 1}">
                                                    &laquo; Trước
                                                </a>
                                            </li>

                                            <!-- Các số trang: 1, 2, 3, ... -->
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/asset-report?type=inventory&categoryFilter=${categoryFilter}&search=${search}&page=${i}">
                                                        ${i}
                                                    </a>
                                                </li>
                                            </c:forEach>

                                            <!-- Nút Sau -->
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link"
                                                   href="${pageContext.request.contextPath}/asset-report?type=inventory&categoryFilter=${categoryFilter}&search=${search}&page=${currentPage + 1}">
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
        // ── Bước 1: Dùng JSTL đổ dữ liệu từ server vào biến JavaScript ──
        // categoryMap sẽ có dạng: { "Máy tính": { "IN_USE": 5, "AVAILABLE": 3 }, "Bàn ghế": { ... } }
            var categoryMap = {};
            var statusSet = new Set();

        // chartData là attribute chứa TOÀN BỘ dữ liệu (không bị phân trang)
            <c:forEach var="row" items="${chartData}">
            if (!categoryMap['${row.categoryName}']) {
                categoryMap['${row.categoryName}'] = {};
            }
            categoryMap['${row.categoryName}']['${row.status}'] = ${row.totalAssets};
            statusSet.add('${row.status}');
            </c:forEach>

            var categories = Object.keys(categoryMap);   // ["Máy tính", "Bàn ghế", ...]
            var statuses = Array.from(statusSet);         // ["IN_USE", "AVAILABLE", ...]
            var colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', '#858796'];

        // ── Bước 2: Vẽ Stacked Bar Chart ──
            var barDatasets = statuses.map(function (status, i) {
                return {
                    label: status,
                    data: categories.map(function (cat) {
                        return categoryMap[cat][status] || 0;
                    }),
                    backgroundColor: colors[i % colors.length]
                };
            });

            new Chart(document.getElementById('inventoryBarChart'), {
                type: 'bar',
                data: {labels: categories, datasets: barDatasets},
                options: {
                    responsive: true,
                    plugins: {legend: {position: 'top'}},
                    scales: {
                        x: {stacked: true},
                        y: {stacked: true, beginAtZero: true}
                    }
                }
            });

        // ── Bước 3: Vẽ Doughnut Chart (tỷ lệ theo trạng thái) ──
            var statusTotals = {};
            statuses.forEach(function (s) {
                statusTotals[s] = 0;
            });
            <c:forEach var="row" items="${chartData}">
            statusTotals['${row.status}'] += ${row.totalAssets};
            </c:forEach>

            new Chart(document.getElementById('inventoryPieChart'), {
                type: 'doughnut',
                data: {
                    labels: statuses,
                    datasets: [{
                            data: statuses.map(function (s) {
                                return statusTotals[s];
                            }),
                            backgroundColor: colors.slice(0, statuses.length)
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
