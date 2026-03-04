<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reset Password - School Asset Management</title>

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
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .reset-password-left {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 50px;
            color: white;
            text-align: center;
        }

        .reset-password-icon {
            font-size: 120px;
            margin-bottom: 30px;
            animation: pulse 2s ease-in-out infinite;
        }

        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.1); }
        }

        .reset-password-left h2 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 20px;
        }

        .reset-password-left p {
            font-size: 16px;
            opacity: 0.9;
            line-height: 1.6;
        }

        .card {
            border-radius: 20px;
            overflow: hidden;
        }

        .input-password {
            height: 50px;
            border-radius: 25px;
            border: 2px solid #e3e6f0;
            padding: 0 25px;
            font-size: 15px;
            transition: all 0.3s;
        }

        .input-password:focus {
            border-color: #4e73df;
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
        }

        .btn-reset {
            height: 50px;
            border-radius: 25px;
            font-size: 16px;
            font-weight: 600;
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            border: none;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(78, 115, 223, 0.4);
        }

        .btn-reset:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(78, 115, 223, 0.6);
        }

        .password-strength {
            height: 5px;
            margin-top: 10px;
            border-radius: 3px;
            transition: all 0.3s;
        }
        .strength-weak { background: #dc3545; width: 33%; }
        .strength-medium { background: #ffc107; width: 66%; }
        .strength-strong { background: #28a745; width: 100%; }
        .password-requirements {
            font-size: 0.85rem;
            margin-top: 10px;
        }
        .requirement {
            color: #6c757d;
        }
        .requirement.met {
            color: #28a745;
        }
        .requirement i {
            width: 20px;
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
                        <div class="col-lg-6 d-none d-lg-flex reset-password-left">
                            <div>
                                <div class="reset-password-icon">
                                    <i class="fas fa-lock-open"></i>
                                </div>
                                <h2>Tạo Mật Khẩu Mới</h2>
                                <p>
                                    Bạn đang ở bước cuối cùng! Hãy tạo một mật khẩu mạnh 
                                    để bảo vệ tài khoản của mình.
                                </p>
                                <p class="mt-3">
                                    <i class="fas fa-check-circle mr-2"></i>
                                    Mật khẩu sẽ được mã hóa an toàn
                                </p>
                            </div>
                        </div>

                        <!-- Right Side - Form -->
                        <div class="col-lg-6">
                            <div class="p-5">

                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-2">
                                        <i class="fas fa-key text-primary"></i> Đặt Lại Mật Khẩu
                                    </h1>
                                    <% String email = (String) request.getAttribute("email"); %>
                                    <% if (email != null) { %>
                                        <p class="mb-4 text-muted">
                                            <i class="fas fa-envelope"></i> <%= email %>
                                        </p>
                                    <% } %>
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

                                <form method="post" action="${pageContext.request.contextPath}/reset-password" id="resetForm">
                                    <input type="hidden" name="token" value="<%= request.getAttribute("token") %>">

                                    <div class="form-group">
                                        <input type="password"
                                               name="newPassword"
                                               id="newPassword"
                                               class="form-control input-password"
                                               placeholder="🔒 Mật khẩu mới"
                                               required
                                               minlength="6">
                                        <div class="password-strength" id="strengthBar"></div>
                                        <div class="password-requirements mt-2">
                                            <div class="requirement" id="req-length">
                                                <i class="fas fa-circle"></i> Ít nhất 6 ký tự
                                            </div>
                                            <div class="requirement" id="req-uppercase">
                                                <i class="fas fa-circle"></i> Ít nhất 1 chữ hoa
                                            </div>
                                           
                                            <div class="requirement" id="req-number">
                                                <i class="fas fa-circle"></i> Ít nhất 1 số
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <input type="password"
                                               name="confirmPassword"
                                               id="confirmPassword"
                                               class="form-control input-password"
                                               placeholder="🔑 Xác nhận mật khẩu mới"
                                               required
                                               minlength="6">
                                        <small class="form-text text-danger d-none" id="passwordMismatch">
                                            Mật khẩu không khớp!
                                        </small>
                                    </div>

                                    <button type="submit" class="btn btn-success btn-reset btn-block" id="submitBtn">
                                        <i class="fas fa-check-circle"></i> Đặt Lại Mật Khẩu
                                    </button>
                                </form>

                                <div class="text-center mt-4">
                                    <a class="back-link" href="${pageContext.request.contextPath}/auth/login">
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

<script>
    $(document).ready(function() {
        const newPasswordInput = $('#newPassword');
        const confirmPasswordInput = $('#confirmPassword');
        const strengthBar = $('#strengthBar');
        const submitBtn = $('#submitBtn');
        const form = $('#resetForm');
        const mismatchMsg = $('#passwordMismatch');

        // Check password strength
        newPasswordInput.on('input', function() {
            const password = $(this).val();
            let strength = 0;

            // Check length
            if (password.length >= 6) {
                strength++;
                $('#req-length').addClass('met');
            } else {
                $('#req-length').removeClass('met');
            }

            // Check uppercase
            if (/[A-Z]/.test(password)) {
                strength++;
                $('#req-uppercase').addClass('met');
            } else {
                $('#req-uppercase').removeClass('met');
            }

            // Check lowercase
            if (/[a-z]/.test(password)) {
                strength++;
                $('#req-lowercase').addClass('met');
            } else {
                $('#req-lowercase').removeClass('met');
            }

            // Check number
            if (/[0-9]/.test(password)) {
                strength++;
                $('#req-number').addClass('met');
            } else {
                $('#req-number').removeClass('met');
            }

            // Update strength bar
            strengthBar.removeClass('strength-weak strength-medium strength-strong');
            if (strength <= 1) {
                strengthBar.addClass('strength-weak');
            } else if (strength <= 3) {
                strengthBar.addClass('strength-medium');
            } else {
                strengthBar.addClass('strength-strong');
            }
        });

        // Check password match
        function checkPasswordMatch() {
            const newPass = newPasswordInput.val();
            const confirmPass = confirmPasswordInput.val();

            if (confirmPass.length > 0) {
                if (newPass !== confirmPass) {
                    mismatchMsg.removeClass('d-none');
                    submitBtn.prop('disabled', true);
                } else {
                    mismatchMsg.addClass('d-none');
                    submitBtn.prop('disabled', false);
                }
            } else {
                mismatchMsg.addClass('d-none');
                submitBtn.prop('disabled', false);
            }
        }

        confirmPasswordInput.on('input', checkPasswordMatch);
        newPasswordInput.on('input', checkPasswordMatch);

        // Form validation
        form.on('submit', function(e) {
            const newPass = newPasswordInput.val();
            const confirmPass = confirmPasswordInput.val();

            const hasUppercase = /[A-Z]/.test(newPass);
            const hasDigit = /[0-9]/.test(newPass);

            if (newPass !== confirmPass) {
                e.preventDefault();
                mismatchMsg.removeClass('d-none');
                return false;
            }

            if (newPass.length < 6 || !hasUppercase || !hasDigit) {
                e.preventDefault();
                alert('Mật khẩu phải có ít nhất 6 ký tự, chứa ít nhất 1 chữ hoa và 1 số');
                return false;
            }

            submitBtn.html('<i class="fas fa-spinner fa-spin"></i> Đang xử lý...');
            submitBtn.prop('disabled', true);
        });
    });
</script>

</body>
</html>

