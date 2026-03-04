<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    List<String> roles = null;
    if (currentUser != null) {
        roles = currentUser.getRoles();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Change Password</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">

    <style>
        .password-wrapper {
            position: relative;
        }

        .password-input {
            padding-right: 40px;
        }

        .password-toggle {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #6c757d;
        }

        .password-toggle:hover {
            color: #4e73df;
        }
    </style>
</head>

<body id="page-top">

<div id="wrapper">

    <!-- Sidebar -->
    <%@ include file="/views/layout/sidebar.jsp" %>

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

        <div id="content">

            <!-- Topbar -->
            <%@ include file="/views/layout/topbar.jsp" %>

            <!-- Page Content -->
            <div class="container-fluid">

                <h1 class="h3 mb-4 text-gray-800">Thay đổi mật khẩu</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>

                <form method="post"
                      action="${pageContext.request.contextPath}/change-password"
                      id="changePasswordForm">

                    <div class="form-group">
                        <label>Mật khẩu hiện tại</label>
                        <div class="password-wrapper">
                            <input type="password"
                                   name="oldPassword"
                                   id="oldPassword"
                                   class="form-control password-input"
                                   required>
                            <span class="password-toggle" data-target="oldPassword">
                                <i class="fas fa-eye"></i>
                            </span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Mật khẩu mới</label>
                        <div class="password-wrapper">
                            <input type="password"
                                   name="newPassword"
                                   id="newPassword"
                                   class="form-control password-input"
                                   required>
                            <span class="password-toggle" data-target="newPassword">
                                <i class="fas fa-eye"></i>
                            </span>
                        </div>
                        <small class="form-text text-danger d-none" id="passwordRequirement">
                            Mật khẩu phải có ít nhất 6 ký tự, chứa ít nhất 1 chữ hoa và 1 số.
                        </small>
                    </div>

                    <div class="form-group">
                        <label>Xác nhận lại mật khẩu mới</label>
                        <div class="password-wrapper">
                            <input type="password"
                                   name="confirmPassword"
                                   id="confirmPassword"
                                   class="form-control password-input"
                                   required>
                            <span class="password-toggle" data-target="confirmPassword">
                                <i class="fas fa-eye"></i>
                            </span>
                        </div>
                        <small class="form-text text-danger d-none" id="passwordMismatch">
                            Mật khẩu xác nhận không khớp!
                        </small>
                    </div>

                    <button class="btn btn-primary">
                        Thay mật khẩu
                    </button>
                </form>

            </div>
        </div>

        <!-- Footer -->
        <%@ include file="/views/layout/footer.jsp" %>

    </div>
</div>

<!-- JS -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
<script>
    $(document).ready(function () {
        const form = $('#changePasswordForm');
        const newPasswordInput = $('#newPassword');
        const confirmPasswordInput = $('#confirmPassword');
        const mismatchMsg = $('#passwordMismatch');
        const passwordRequirement = $('#passwordRequirement');

        form.on('submit', function (e) {
            const newPass = newPasswordInput.val();
            const confirmPass = confirmPasswordInput.val();

            const hasUppercase = /[A-Z]/.test(newPass);
            const hasDigit = /[0-9]/.test(newPass);

            if (newPass !== confirmPass) {
                e.preventDefault();
                mismatchMsg.removeClass('d-none');
                return false;
            } else {
                mismatchMsg.addClass('d-none');
            }

            if (newPass.length < 6 || !hasUppercase || !hasDigit) {
                e.preventDefault();
                passwordRequirement.removeClass('d-none');
                return false;
            } else {
                passwordRequirement.addClass('d-none');
            }
        });

        $('.password-toggle').on('click', function () {
            const targetId = $(this).data('target');
            const input = $('#' + targetId);
            const icon = $(this).find('i');

            if (input.attr('type') === 'password') {
                input.attr('type', 'text');
                icon.removeClass('fa-eye').addClass('fa-eye-slash');
            } else {
                input.attr('type', 'password');
                icon.removeClass('fa-eye-slash').addClass('fa-eye');
            }
        });
    });
</script>

</body>
</html>
