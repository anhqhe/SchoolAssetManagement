<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>SB Admin 2 - Login</title>

    <!-- Fonts -->
    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css"
          rel="stylesheet" type="text/css">

    <link href="https://fonts.googleapis.com/css?family=Nunito:200,300,400,600,700,800,900"
          rel="stylesheet">

    <!-- CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css"
          rel="stylesheet">
</head>

<body class="bg-gradient-primary">

<div class="container">

    <div class="row justify-content-center">
        <div class="col-xl-10 col-lg-12 col-md-9">

            <div class="card o-hidden border-0 shadow-lg my-5">
                <div class="card-body p-0">

                    <div class="row">
                        <div class="col-lg-6 d-none d-lg-block bg-login-image"></div>

                        <div class="col-lg-6">
                            <div class="p-5">
                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-4">Welcome Back!</h1>
                                </div>

                                <!-- CHƯA XỬ LÝ LOGIN -->
                                <form class="user">
                                    <div class="form-group">
                                        <input type="email"
                                               class="form-control form-control-user"
                                               placeholder="Enter Email Address...">
                                    </div>

                                    <div class="form-group">
                                        <input type="password"
                                               class="form-control form-control-user"
                                               placeholder="Password">
                                    </div>

                                    <div class="form-group">
                                        <div class="custom-control custom-checkbox small">
                                            <input type="checkbox"
                                                   class="custom-control-input"
                                                   id="customCheck">
                                            <label class="custom-control-label"
                                                   for="customCheck">Remember Me</label>
                                        </div>
                                    </div>

                                    <!-- TẠM THỜI CHUYỂN DASHBOARD -->
                                    <a href="${pageContext.request.contextPath}/views/admin/dashboard.jsp"
                                       class="btn btn-primary btn-user btn-block">
                                        Login
                                    </a>
                                </form>

                                <hr>

                                <div class="text-center">
                                    <a class="small"
                                       href="${pageContext.request.contextPath}/views/auth/forgot-password.jsp">
                                        Forgot Password?
                                    </a>
                                </div>

                                <div class="text-center">
                                    <a class="small"
                                       href="${pageContext.request.contextPath}/views/auth/register.jsp">
                                        Create an Account!
                                    </a>
                                </div>

                            </div>
                        </div>
                    </div>

                </div>
            </div>

        </div>
    </div>

</div>

<!-- JS -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

</body>
</html>
