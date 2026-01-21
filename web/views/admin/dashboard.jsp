<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>

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
                        <i class="fas fa-tachometer-alt text-primary"></i> Dashboard
                    </h1>
                    <a href="#" class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm">
                        <i class="fas fa-download fa-sm text-white-50"></i> Tải báo cáo
                    </a>
                </div>

                <!-- Content Row - Statistics Cards -->
                <div class="row">

                    <!-- Total Assets Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                            Tổng tài sản
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">1,247</div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-boxes fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- In Use Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-success shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                            Đang sử dụng
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">1,089</div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-check-circle fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Maintenance Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                            Đang bảo trì
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">127</div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-tools fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Damaged Card -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-danger shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
                                            Hỏng hóc
                                        </div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">31</div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-exclamation-triangle fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Content Row - Charts -->
                <div class="row">

                    <!-- Area Chart -->
                    <div class="col-xl-8 col-lg-7">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Tổng quan tài sản theo tháng</h6>
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
                                <h6 class="m-0 font-weight-bold text-primary">Phân loại tài sản</h6>
                            </div>
                            <div class="card-body">
                                <div class="chart-pie pt-4 pb-2">
                                    <canvas id="myPieChart"></canvas>
                                </div>
                                <div class="mt-4 text-center small">
                                    <span class="mr-2">
                                        <i class="fas fa-circle text-primary"></i> Thiết bị IT
                                    </span>
                                    <span class="mr-2">
                                        <i class="fas fa-circle text-success"></i> Nội thất
                                    </span>
                                    <span class="mr-2">
                                        <i class="fas fa-circle text-info"></i> Khác
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
                                            <tr>
                                                <td>IT-2024-001</td>
                                                <td>Máy tính Dell Latitude</td>
                                                <td>Thiết bị IT</td>
                                                <td><span class="badge badge-success">Đang sử dụng</span></td>
                                                <td>15/01/2024</td>
                                            </tr>
                                            <tr>
                                                <td>FU-2024-045</td>
                                                <td>Bàn giảng viên</td>
                                                <td>Nội thất</td>
                                                <td><span class="badge badge-success">Đang sử dụng</span></td>
                                                <td>14/01/2024</td>
                                            </tr>
                                            <tr>
                                                <td>IT-2024-002</td>
                                                <td>Máy chiếu Epson</td>
                                                <td>Thiết bị IT</td>
                                                <td><span class="badge badge-warning">Đang cài đặt</span></td>
                                                <td>13/01/2024</td>
                                            </tr>
                                            <tr>
                                                <td>FU-2024-046</td>
                                                <td>Ghế văn phòng</td>
                                                <td>Nội thất</td>
                                                <td><span class="badge badge-success">Đang sử dụng</span></td>
                                                <td>12/01/2024</td>
                                            </tr>
                                            <tr>
                                                <td>IT-2024-003</td>
                                                <td>Màn hình LG 27 inch</td>
                                                <td>Thiết bị IT</td>
                                                <td><span class="badge badge-success">Đang sử dụng</span></td>
                                                <td>11/01/2024</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <a href="#" class="btn btn-primary btn-sm">Xem tất cả <i class="fas fa-arrow-right"></i></a>
                            </div>
                        </div>
                    </div>

                    <!-- Upcoming Maintenance -->
                    <div class="col-xl-4 col-lg-5">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-calendar-check"></i> Bảo trì sắp tới
                                </h6>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <div class="small text-gray-500">22/01/2024</div>
                                    <div class="font-weight-bold">Máy tính phòng Lab A</div>
                                    <div class="text-xs text-muted">15 thiết bị cần bảo trì</div>
                                </div>
                                <div class="mb-3">
                                    <div class="small text-gray-500">25/01/2024</div>
                                    <div class="font-weight-bold">Máy chiếu phòng 301</div>
                                    <div class="text-xs text-muted">Bảo trì định kỳ</div>
                                </div>
                                <div class="mb-3">
                                    <div class="small text-gray-500">28/01/2024</div>
                                    <div class="font-weight-bold">Điều hòa phòng họp</div>
                                    <div class="text-xs text-muted">Vệ sinh hệ thống</div>
                                </div>
                                <div class="mb-3">
                                    <div class="small text-gray-500">01/02/2024</div>
                                    <div class="font-weight-bold">Máy in văn phòng</div>
                                    <div class="text-xs text-muted">Kiểm tra và bảo trì</div>
                                </div>
                                <a href="#" class="btn btn-warning btn-sm btn-block">
                                    <i class="fas fa-calendar"></i> Xem lịch bảo trì
                                </a>
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
                                <a href="#" class="btn btn-primary btn-icon-split btn-sm btn-block mb-2">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-plus"></i>
                                    </span>
                                    <span class="text">Thêm tài sản mới</span>
                                </a>
                                <a href="#" class="btn btn-success btn-icon-split btn-sm btn-block mb-2">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-clipboard-list"></i>
                                    </span>
                                    <span class="text">Yêu cầu tài sản</span>
                                </a>
                                <a href="#" class="btn btn-info btn-icon-split btn-sm btn-block mb-2">
                                    <span class="icon text-white-50">
                                        <i class="fas fa-file-alt"></i>
                                    </span>
                                    <span class="text">Tạo báo cáo</span>
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
  type: 'line',
  data: {
    labels: ["T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"],
    datasets: [{
      label: "Tài sản",
      lineTension: 0.3,
      backgroundColor: "rgba(78, 115, 223, 0.05)",
      borderColor: "rgba(78, 115, 223, 1)",
      pointRadius: 3,
      pointBackgroundColor: "rgba(78, 115, 223, 1)",
      pointBorderColor: "rgba(78, 115, 223, 1)",
      pointHoverRadius: 3,
      pointHoverBackgroundColor: "rgba(78, 115, 223, 1)",
      pointHoverBorderColor: "rgba(78, 115, 223, 1)",
      pointHitRadius: 10,
      pointBorderWidth: 2,
      data: [850, 920, 980, 1050, 1100, 1150, 1180, 1200, 1220, 1230, 1240, 1247],
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
    labels: ["Thiết bị IT", "Nội thất", "Khác"],
    datasets: [{
      data: [55, 30, 15],
      backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc'],
      hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf'],
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
