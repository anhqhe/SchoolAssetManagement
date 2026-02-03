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

    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }

        .forgot-password-left {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 50px;
            color: white;
            text-align: center;
        }

        .forgot-password-icon {
            font-size: 120px;
            margin-bottom: 30px;
            animation: float 3s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-20px); }
        }

        .forgot-password-left h2 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 20px;
        }

        .forgot-password-left p {
            font-size: 16px;
            opacity: 0.9;
            line-height: 1.6;
        }

        .card {
            border-radius: 20px;
            overflow: hidden;
        }

        .input-email {
            height: 50px;
            border-radius: 25px;
            border: 2px solid #e3e6f0;
            padding: 0 25px;
            font-size: 15px;
            transition: all 0.3s;
        }

        .input-email:focus {
            border-color: #4e73df;
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
        }

        .btn-submit {
            height: 50px;
            border-radius: 25px;
            font-size: 16px;
            font-weight: 600;
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            border: none;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(78, 115, 223, 0.4);
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(78, 115, 223, 0.6);
        }

        .back-link {
            color: #4e73df;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
        }

        .back-link:hover {
            color: #224abe;
            text-decoration: none;
        }

        .alert {
            border-radius: 15px;
            border: none;
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
    </style>
</head>

<body>

<div class="container">

    <div class="row justify-content-center">
        <div class="col-xl-10 col-lg-12 col-md-9">

            <div class="card o-hidden border-0 shadow-lg my-5">
                <div class="card-body p-0">

                    <div class="row g-0">
                        <!-- Left Side - Illustration -->
                        <div class="col-lg-6 d-none d-lg-flex forgot-password-left">
                            <div>
                                <div class="forgot-password-icon">
                                    <i class="fas fa-unlock-alt"></i>
                                </div>
                                <h2>Khôi Phục Mật Khẩu</h2>
                                <p>
                                    Đừng lo lắng! Chúng tôi sẽ giúp bạn lấy lại quyền truy cập 
                                    vào tài khoản của mình một cách an toàn và nhanh chóng.
                                </p>
                                <p class="mt-3">
                                    <i class="fas fa-shield-alt mr-2"></i>
                                    Bảo mật và an toàn tuyệt đối
                                </p>
                            </div>
                        </div>

                        <!-- Right Side - Form -->
                        <div class="col-lg-6">
                            <div class="p-5">

                                <div class="text-center mb-4">
                                    <h1 class="h3 text-gray-900 mb-3">
                                        <i class="fas fa-key text-primary"></i> Quên Mật Khẩu?
                                    </h1>
                                    <p class="text-muted">
                                        Nhập địa chỉ email của bạn bên dưới và chúng tôi 
                                        sẽ gửi link để đặt lại mật khẩu.
                                    </p>
                                </div>

                                <%-- Hiển thị thông báo lỗi --%>
                                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                                <% if (errorMessage != null) { %>
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <i class="fas fa-exclamation-triangle"></i> <%= errorMessage %>
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                <% } %>

                                <%-- Hiển thị thông báo thành công --%>
                                <% String successMessage = (String) request.getAttribute("successMessage"); %>
                                <% if (successMessage != null) { %>
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <i class="fas fa-check-circle"></i> <%= successMessage %>
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                <% } %>

                                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                                    <div class="form-group">
                                        <input type="email"
                                               name="email"
                                               class="form-control input-email"
                                               placeholder="📧 Nhập địa chỉ email của bạn..."
                                               required
                                               autocomplete="email">
                                    </div>

                                    <button type="submit" class="btn btn-primary btn-submit btn-block">
                                        <i class="fas fa-paper-plane"></i> Gửi Link Reset Password
                                    </button>
                                </form>

                                <div class="text-center mt-4">
                                    <a class="back-link"
                                       href="${pageContext.request.contextPath}/auth/login">
                                        <i class="fas fa-arrow-left"></i> Quay lại trang đăng nhập
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
