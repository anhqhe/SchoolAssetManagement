<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>500 - Lỗi hệ thống</title>

    <!-- Fonts -->
    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,300,400,600,700,800,900" rel="stylesheet">

    <!-- CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body class="bg-light d-flex align-items-center justify-content-center" style="min-height: 100vh;">

<div class="text-center">
    <div class="error mx-auto" data-text="500">500</div>

    <p class="lead text-gray-800 mb-4">
        Đã xảy ra lỗi hệ thống
    </p>

    <p class="text-gray-500 mb-4">
        Hệ thống đang gặp sự cố ngoài ý muốn.<br>
        Vui lòng thử lại sau hoặc liên hệ quản trị viên nếu lỗi tiếp tục xảy ra.
    </p>

    <a class="btn btn-primary"
       href="${pageContext.request.contextPath}/admin/dashboard">
        <i class="fas fa-arrow-left mr-1"></i>
        Quay lại Trang tổng quan
    </a>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

</body>
</html>