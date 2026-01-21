<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="utf-8">
    <title>Login - School Asset Management</title>

    <!-- Custom fonts -->
    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link
        href="https://fonts.googleapis.com/css?family=Nunito:200,300,400,600,700,800,900"
        rel="stylesheet">

    <!-- Custom styles -->
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">

</head>

<body class="bg-gradient-primary">

<div class="container">

    <!-- Outer Row -->
    <div class="row justify-content-center">

        <div class="col-xl-10 col-lg-12 col-md-9">

            <div class="card o-hidden border-0 shadow-lg my-5">
                <div class="card-body p-0">
                    <!-- Nested Row -->
                    <div class="row">
                        <div class="col-lg-6 d-none d-lg-block bg-login-image"></div>
                        <div class="col-lg-6">
                            <div class="p-5">

                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-4">
                                        School Asset Management
                                    </h1>
                                </div>

                                <!-- ERROR MESSAGE -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger text-center">
                                        ${error}
                                    </div>
                                </c:if>

                                <!-- LOGIN FORM -->
                                <form class="user"
                                      action="${pageContext.request.contextPath}/auth/login"
                                      method="post">

                                    <div class="form-group">
                                        <input type="text"
                                               name="username"
                                               class="form-control form-control-user"
                                               placeholder="Enter Username..."
                                               required>
                                    </div>

                                    <div class="form-group">
                                        <input type="password"
                                               name="password"
                                               class="form-control form-control-user"
                                               placeholder="Password"
                                               required>
                                    </div>

                                    <div class="form-group">
                                        <div class="custom-control custom-checkbox small">
                                            <input type="checkbox"
                                                   class="custom-control-input"
                                                   id="rememberMe">
                                            <label class="custom-control-label" for="rememberMe">
                                                Remember Me
                                            </label>
                                        </div>
                                    </div>

                                    <button type="submit"
                                            class="btn btn-primary btn-user btn-block">
                                        Login
                                    </button>

                                </form>

                                <hr>

                                <div class="text-center">
                                    <a class="small"
                                       href="${pageContext.request.contextPath}/views/auth/forgot-password.jsp">
                                        Forgot Password?
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

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

</body>
</html>
