<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - School Asset Management</title>

    <!-- Custom fonts -->
    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,300,400,600,700,800,900" rel="stylesheet">

    <!-- Custom styles -->
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }

        .login-container {
            margin-top: 50px;
            margin-bottom: 50px;
        }

        .login-card {
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        }

        .login-left {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 50px;
            color: white;
        }

        .login-left-content {
            text-align: center;
        }

        .login-left-content i {
            font-size: 100px;
            margin-bottom: 30px;
            animation: float 3s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-20px); }
        }

        .login-left h2 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 15px;
        }

        .login-left p {
            font-size: 16px;
            opacity: 0.9;
        }

        .login-right {
            padding: 60px 50px;
            background: white;
        }

        .login-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .login-header h1 {
            color: #5a5c69;
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .login-header p {
            color: #858796;
            font-size: 14px;
        }

        .input-group-custom {
            position: relative;
            margin-bottom: 25px;
        }

        .input-group-custom i {
            position: absolute;
            left: 20px;
            top: 50%;
            transform: translateY(-50%);
            color: #4e73df;
            z-index: 10;
        }

        .input-group-custom input {
            padding-left: 50px;
            height: 50px;
            border-radius: 25px;
            border: 2px solid #e3e6f0;
            transition: all 0.3s;
        }

        .input-group-custom input:focus {
            border-color: #4e73df;
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
        }

        .btn-login {
            height: 50px;
            border-radius: 25px;
            font-size: 16px;
            font-weight: 600;
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            border: none;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(78, 115, 223, 0.4);
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(78, 115, 223, 0.6);
            background: linear-gradient(135deg, #2e59d9 0%, #1e3a8a 100%);
        }

        .custom-checkbox {
            margin-bottom: 20px;
        }

        .custom-checkbox label {
            color: #5a5c69;
            font-size: 14px;
        }

        .divider {
            margin: 30px 0;
            text-align: center;
            position: relative;
        }

        .divider:before {
            content: "";
            position: absolute;
            left: 0;
            top: 50%;
            width: 100%;
            height: 1px;
            background: #e3e6f0;
        }

        .divider span {
            background: white;
            padding: 0 15px;
            position: relative;
            color: #858796;
            font-size: 14px;
        }

        .forgot-link {
            color: #4e73df;
            font-size: 14px;
            text-decoration: none;
            transition: all 0.3s;
        }

        .forgot-link:hover {
            color: #224abe;
            text-decoration: none;
        }

        .alert-custom {
            border-radius: 15px;
            border: none;
            padding: 15px 20px;
            margin-bottom: 25px;
            animation: slideDown 0.5s ease;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @media (max-width: 991px) {
            .login-left {
                display: none;
            }
            .login-right {
                padding: 40px 30px;
            }
        }
    </style>
</head>

<body>

<div class="container login-container">
    <div class="row justify-content-center">
        <div class="col-xl-10 col-lg-12 col-md-9">
            <div class="card login-card border-0">
                <div class="card-body p-0">
                    <div class="row g-0">
                        
                        <!-- Left Side - Decoration -->
                        <div class="col-lg-6 login-left d-none d-lg-flex">
                            <div class="login-left-content">
                                <i class="fas fa-school"></i>
                                <h2>School Asset Management</h2>
                                <p>Quản lý tài sản trường học hiệu quả và chuyên nghiệp</p>
                            </div>
                        </div>

                        <!-- Right Side - Login Form -->
                        <div class="col-lg-6">
                            <div class="login-right">
                                
                                <!-- Header -->
                                <div class="login-header">
                                    <h1><i class="fas fa-lock text-primary"></i> Đăng nhập</h1>
                                    <p>Vui lòng nhập thông tin đăng nhập của bạn</p>
                                </div>

                                <!-- Success Message -->
                                <c:if test="${not empty sessionScope.successMessage}">
                                    <div class="alert alert-success alert-custom">
                                        <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                                    </div>
                                    <c:remove var="successMessage" scope="session" />
                                </c:if>

                                <!-- Error Message -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-custom">
                                        <i class="fas fa-exclamation-circle"></i> ${error}
                                    </div>
                                </c:if>

                                <!-- Login Form -->
                                <form action="${pageContext.request.contextPath}/auth/login" method="post">
                                    
                                    <!-- Username -->
                                    <div class="input-group-custom">
                                        <i class="fas fa-user"></i>
                                        <input type="text" 
                                               name="username" 
                                               class="form-control" 
                                               placeholder="Tên đăng nhập"
                                               required 
                                               autofocus>
                                    </div>

                                    <!-- Password -->
                                    <div class="input-group-custom">
                                        <i class="fas fa-lock"></i>
                                        <input type="password" 
                                               name="password" 
                                               class="form-control" 
                                               placeholder="Mật khẩu"
                                               required>
                                    </div>

                                    <!-- Remember Me -->
                                    <div class="custom-control custom-checkbox custom-checkbox">
                                        <input type="checkbox" 
                                               class="custom-control-input" 
                                               id="rememberMe"
                                               name="rememberMe">
                                        <label class="custom-control-label" for="rememberMe">
                                            Ghi nhớ đăng nhập
                                        </label>
                                    </div>

                                    <!-- Login Button -->
                                    <button type="submit" class="btn btn-primary btn-login btn-block">
                                        <i class="fas fa-sign-in-alt"></i> Đăng nhập
                                    </button>

                                </form>

                                <!-- Divider -->
                                <div class="divider">
                                    <span>hoặc</span>
                                </div>

                                <!-- Forgot Password -->
                                <div class="text-center">
                                    <a class="forgot-link" 
                                       href="${pageContext.request.contextPath}/forgot-password">
                                        <i class="fas fa-question-circle"></i> Quên mật khẩu?
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
