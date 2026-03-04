<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    List<String> roles = null;
    if (currentUser != null) {
        roles = currentUser.getRoles();
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard | School Asset Management</title>

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
                        <i class="fas fa-tachometer-alt text-primary"></i> Trang tổng quan
                    </h1>
                    <span class="text-muted small">
                        Xin chào, ${sessionScope.currentUser.fullName}!
                    </span>
                </div>

                <!-- Content Row - Asset Statistics Cards -->
                <div class="row">

                    <!-- Total Assets Card -->
                    <div class="col-xl-4 col-md-6 mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                            Tổng tài sản
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${totalAssets}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-boxes fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- In Stock Card -->
                    <div class="col-xl-4 col-md-6 mb-4">
                        <div class="card border-left-success shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                            Trong kho
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${inStockAssets}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-warehouse fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- In Use Card -->
                    <div class="col-xl-4 col-md-6 mb-4">
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                            Đang sử dụng
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${inUseAssets}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-check-circle fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <!-- Content Row - Request & Workflow Statistics Cards -->
                <div class="row">

                    <!-- Total Requests Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-info shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                            Phiếu yêu cầu tài sản
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${totalRequests}
                                        </div>
                                        <div class="text-xs text-muted mt-1">
                                            Đang chờ xử lý: ${pendingRequests + waitingBoardRequests}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-clipboard-list fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Approved Requests Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                            Đã phê duyệt bởi HĐ
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${approvedRequests}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-check fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Completed Requests Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-success shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                            Phiếu yêu cầu đã hoàn thành
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${completedRequests}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-box-open fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Pending Transfers Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                            Phiếu điều chuyển đang chờ
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            ${pendingTransfers}
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-exchange-alt fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Content Row - Charts -->
                <div class="row">

                    <!-- Area / Bar Chart -->
                    <div class="col-xl-8 col-lg-7">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Thống kê phiếu yêu cầu theo trạng thái</h6>
                                <div class="dropdown no-arrow">
                                    <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink"
                                       data-toggle="dropdown">
                                        <i class="fas fa-ellipsis-v fa-sm fa-fw text-gray-400"></i>
                                    </a>
                                    <div class="dropdown-menu dropdown-menu-right shadow animated--fade-in">
                                        <div class="dropdown-header">Lựa chọn:</div>
                                        <a class="dropdown-item" href="#">Xem chi tiết</a>
                                        <a class="dropdown-item" href="#">Tải xuống</a>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="chart-area">
                                    <canvas id="myAreaChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Pie Chart -->
                    <div class="col-xl-4 col-lg-5">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Phân bố trạng thái tài sản</h6>
                            </div>
                            <div class="card-body">
                                <div class="chart-pie pt-4 pb-2">
                                    <canvas id="myPieChart"></canvas>
                                </div>
                                <div class="mt-4 text-center small">
                                    <span class="mr-2">
                                        <i class="fas fa-circle text-primary"></i> Trong kho
                                    </span>
                                    <span class="mr-2">
                                        <i class="fas fa-circle text-success"></i> Đang sử dụng
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Content Row - Tables -->
                <div class="row">

                    <!-- Recent Assets -->
                    <div class="col-xl-8 col-lg-7">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-list"></i> Tài sản mới nhập gần đây
                                </h6>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered" width="100%" cellspacing="0">
                                        <thead>
                                            <tr>
                                                <th>Mã tài sản</th>
                                                <th>Tên</th>
                                                <th>Loại</th>
                                                <th>Trạng thái</th>
                                                <th>Ngày nhập</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${empty recentAssets}">
                                                    <tr>
                                                        <td colspan="5" class="text-center text-muted">
                                                            Chưa có tài sản nào
                                                        </td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="asset" items="${recentAssets}">
                                                        <tr>
                                                            <td>${asset.assetCode}</td>
                                                            <td>${asset.assetName}</td>
                                                            <td>${asset.categoryName}</td>
                                                            <td>
                                                                <span class="badge ${asset.statusBadgeClass}">
                                                                    ${asset.statusText}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${not empty asset.receivedDate}">
                                                                        <fmt:formatDate value="${asset.receivedDate}" pattern="dd/MM/yyyy"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        -
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                                <a href="${pageContext.request.contextPath}/assets" class="btn btn-primary btn-sm">
                                    Xem tất cả <i class="fas fa-arrow-right"></i>
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Latest Notifications -->
                    <div class="col-xl-4 col-lg-5">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-bell"></i> Thông báo mới nhất
                                </h6>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${empty recentNotifications}">
                                        <div class="text-center text-muted py-3">
                                            <i class="fas fa-inbox fa-2x mb-2"></i>
                                            <div>Hiện chưa có thông báo nào</div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="noti" items="${recentNotifications}" varStatus="st">
                                            <c:if test="${st.index < 5}">
                                                <div class="mb-3">
                                                    <div class="small text-gray-500">
                                                        ${noti.createdAt}
                                                    </div>
                                                    <div class="font-weight-bold">
                                                        ${noti.title}
                                                        <c:if test="${!noti.read}">
                                                            <span class="badge badge-pill badge-warning ml-1">Mới</span>
                                                        </c:if>
                                                    </div>
                                                    <div class="text-xs text-muted">${noti.content}</div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                                <div class="mt-2 text-right text-xs text-muted">
                                    Thông báo chưa đọc: ${unreadNotificationCount}
                                </div>
                            </div>
                        </div>

                        <!-- Quick Actions -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-bolt"></i> Thao tác nhanh
                                </h6>
                            </div>
                            <div class="card-body">
                                <a href="${pageContext.request.contextPath}/assets/list" class="btn btn-primary btn-icon-split btn-sm btn-block mb-2">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-plus"></i>
                                    </span>
                                    <span class="text">Quản lý tài sản</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/staff/request-list" class="btn btn-success btn-icon-split btn-sm btn-block mb-2">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-clipboard-list"></i>
                                    </span>
                                    <span class="text">Phiếu yêu cầu tài sản</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/transfers/list" class="btn btn-info btn-icon-split btn-sm btn-block mb-2">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-file-alt"></i>
                                    </span>
                                    <span class="text">Phiếu điều chuyển tài sản</span>
                                </a>
                            </div>
                        </div>
                    </div>
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

<!-- Chart.js -->
<script src="${pageContext.request.contextPath}/assets/vendor/chart.js/Chart.min.js"></script>

<!-- Charts Scripts -->
<script>
// Area Chart
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

var ctx = document.getElementById("myAreaChart");
var myLineChart = new Chart(ctx, {
  type: 'bar',
  data: {
    labels: ["Chờ xử lý", "Chờ HĐ trường", "Đã phê duyệt", "Hoàn thành", "Thiếu tài sản"],
    datasets: [{
      label: "Số lượng phiếu yêu cầu",
      lineTension: 0.3,
      backgroundColor: "rgba(78, 115, 223, 0.5)",
      borderColor: "rgba(78, 115, 223, 1)",
      pointRadius: 3,
      pointBackgroundColor: "rgba(78, 115, 223, 1)",
      pointBorderColor: "rgba(78, 115, 223, 1)",
      pointHoverRadius: 3,
      pointHoverBackgroundColor: "rgba(78, 115, 223, 1)",
      pointHoverBorderColor: "rgba(78, 115, 223, 1)",
      pointHitRadius: 10,
      pointBorderWidth: 2,
      data: [
        '${pendingRequests}',
        '${waitingBoardRequests}',
        '${approvedRequests}',
        '${completedRequests}',
        '${outOfStockRequests}'
      ],
    }],
  },
  options: {
    maintainAspectRatio: false,
    layout: {
      padding: {
        left: 10,
        right: 25,
        top: 25,
        bottom: 0
      }
    },
    scales: {
      xAxes: [{
        time: {
          unit: 'month'
        },
        gridLines: {
          display: false,
          drawBorder: false
        },
        ticks: {
          maxTicksLimit: 12
        }
      }],
      yAxes: [{
        ticks: {
          maxTicksLimit: 5,
          padding: 10,
        },
        gridLines: {
          color: "rgb(234, 236, 244)",
          zeroLineColor: "rgb(234, 236, 244)",
          drawBorder: false,
          borderDash: [2],
          zeroLineBorderDash: [2]
        }
      }],
    },
    legend: {
      display: false
    },
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      titleMarginBottom: 10,
      titleFontColor: '#6e707e',
      titleFontSize: 14,
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      intersect: false,
      mode: 'index',
      caretPadding: 10,
    }
  }
});

// Pie Chart
var ctx2 = document.getElementById("myPieChart");
var myPieChart = new Chart(ctx2, {
  type: 'doughnut',
  data: {
    labels: ["Trong kho", "Đang sử dụng"],
    datasets: [{
      data: [
        '${inStockAssets}',
        '${inUseAssets}'
      ],
      backgroundColor: ['#4e73df', '#1cc88a'],
      hoverBackgroundColor: ['#2e59d9', '#17a673'],
      hoverBorderColor: "rgba(234, 236, 244, 1)",
    }],
  },
  options: {
    maintainAspectRatio: false,
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      caretPadding: 10,
    },
    legend: {
      display: false
    },
    cutoutPercentage: 80,
  },
});
</script>

</body>
</html>
