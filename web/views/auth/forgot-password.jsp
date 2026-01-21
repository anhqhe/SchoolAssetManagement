<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>SB Admin 2 - Forgot Password</title>

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
                        <div class="col-lg-6 d-none d-lg-block bg-password-image"></div>

                        <div class="col-lg-6">
                            <div class="p-5">

                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-2">Forgot Your Password?</h1>
                                    <p class="mb-4">
                                        We get it, stuff happens. Just enter your email address below
                                        and we'll send you a link to reset your password!
                                    </p>
                                </div>

                                <!-- CHƯA XỬ LÝ RESET PASSWORD -->
                                <form class="user">
                                    <div class="form-group">
                                        <input type="email"
                                               class="form-control form-control-user"
                                               placeholder="Enter Email Address...">
                                    </div>

                                    <!-- TẠM THỜI QUAY VỀ LOGIN -->
                                    <a href="${pageContext.request.contextPath}/views/auth/login.jsp"
                                       class="btn btn-primary btn-user btn-block">
                                        Reset Password
                                    </a>
                                </form>

                                <hr>

                                <div class="text-center">
                                    <a class="small"
                                       href="${pageContext.request.contextPath}/views/auth/register.jsp">
                                        Create an Account!
                                    </a>
                                </div>

                                <div class="text-center">
                                    <a class="small"
                                       href="${pageContext.request.contextPath}/views/auth/login.jsp">
                                        Already have an account? Login!
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
