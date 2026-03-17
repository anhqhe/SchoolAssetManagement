<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>

<%
    User u = (User) request.getAttribute("viewUser");
    String error = (String) request.getAttribute("error");
    User currentUser = (User) session.getAttribute("currentUser");
    String successParam = request.getParameter("success");
    String errorParam = request.getParameter("error");

    String phoneVal = "";
    if (u != null && u.getPhone() != null) {
        phoneVal = u.getPhone().trim().replaceAll("[^0-9+]", "");
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết người dùng | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">

<div id="wrapper">

    <%@ include file="/views/layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="/views/layout/topbar.jsp" %>

            <div class="container-fluid">

                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-id-card text-primary"></i> Chi tiết người dùng
                    </h1>
                    <a href="${pageContext.request.contextPath}/admin/user" class="btn btn-secondary btn-sm">
                        <i class="fas fa-arrow-left"></i> Quay lại danh sách
                    </a>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle"></i> <%= error %>
                </div>
                <% } %>

                <% if ("updated".equals(successParam)) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> Cập nhật thông tin thành công.
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } else if ("username_empty".equals(errorParam)) { %>
                <div class="alert alert-danger">Username không được để trống.</div>
                <% } else if ("username_len".equals(errorParam)) { %>
                <div class="alert alert-danger">Username phải từ 3 đến 30 ký tự.</div>
                <% } else if ("fullname_empty".equals(errorParam)) { %>
                <div class="alert alert-danger">Họ tên không được để trống.</div>
                <% } else if ("fullname_len".equals(errorParam)) { %>
                <div class="alert alert-danger">Họ tên phải từ 2 đến 100 ký tự.</div>
                <% } else if ("email_empty".equals(errorParam)) { %>
                <div class="alert alert-danger">Email không được để trống.</div>
                <% } else if ("phone_invalid".equals(errorParam)) { %>
                <div class="alert alert-danger">Số điện thoại không hợp lệ.</div>
                <% } else if ("username_taken".equals(errorParam)) { %>
                <div class="alert alert-danger">Username đã tồn tại.</div>
                <% } else if ("email_taken".equals(errorParam)) { %>
                <div class="alert alert-danger">Email đã tồn tại.</div>
                <% } else if ("phone_taken".equals(errorParam)) { %>
                <div class="alert alert-danger">Số điện thoại đã tồn tại.</div>
                <% } else if ("update_failed".equals(errorParam)) { %>
                <div class="alert alert-danger">Không thể cập nhật thông tin. Vui lòng thử lại.</div>
                <% } %>

                <% if (u == null) { %>
                <div class="alert alert-warning">Không tìm thấy người dùng.</div>
                <% } else { %>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-user"></i> Thông tin tài khoản
                                </h6>
                                <% if (u.isActive()) { %>
                                    <span class="badge badge-success">Hoạt động</span>
                                <% } else { %>
                                    <span class="badge badge-danger">Đã ban</span>
                                <% } %>
                            </div>
                            <div class="card-body">
                                <form method="post" action="${pageContext.request.contextPath}/admin/user/update" autocomplete="off">
                                    <input type="hidden" name="id" value="<%= u.getUserId() %>">
                                    <table class="table table-borderless mb-0">
                                    <tr>
                                        <th style="width:150px" class="text-gray-600">ID người dùng</th>
                                        <td><%= u.getUserId() %></td>
                                    </tr>
                                    <tr>
                                        <th class="text-gray-600">Tài khoản</th>
                                        <td>
                                            <input type="text" name="username" class="form-control form-control-sm"
                                                   value="<%= u.getUsername() != null ? u.getUsername() : "" %>"
                                                   minlength="3" maxlength="30" required>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="text-gray-600">Họ tên</th>
                                        <td>
                                            <input type="text" name="fullName" class="form-control form-control-sm"
                                                   value="<%= u.getFullName() != null ? u.getFullName() : "" %>"
                                                   minlength="2" maxlength="100" required>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="text-gray-600">Email</th>
                                        <td>
                                            <input type="email" name="email" class="form-control form-control-sm"
                                                   value="<%= u.getEmail() != null ? u.getEmail() : "" %>"
                                                   required>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="text-gray-600">Số điện thoại</th>
                                        <td>
                                            <input type="text" name="phone" class="form-control form-control-sm"
                                                   value="<%= phoneVal %>"
                                                   inputmode="tel"
                                                   placeholder="0912345678 hoặc +84 912345678">
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="text-gray-600">Vai trò</th>
                                        <td>
                                            <%
                                                if (u.getRoles() != null && !u.getRoles().isEmpty()) {
                                                    for (String r : u.getRoles()) {
                                                        String badgeClass = "badge-secondary";
                                                        if ("ADMIN".equals(r)) badgeClass = "badge-danger";
                                                        else if ("ASSET_STAFF".equals(r)) badgeClass = "badge-primary";
                                                        else if ("TEACHER".equals(r)) badgeClass = "badge-info";
                                                        else if ("BOARD".equals(r)) badgeClass = "badge-warning";
                                            %>
                                            <span class="badge <%= badgeClass %>"><%= r %></span>
                                            <%
                                                    }
                                                } else {
                                            %>
                                            <span class="text-muted">Chưa gán vai trò</span>
                                            <%  } %>
                                        </td>
                                    </tr>
                                </table>
                                    <div class="d-flex justify-content-end mt-3">
                                        <button type="submit" class="btn btn-primary btn-sm">
                                            <i class="fas fa-save"></i> Lưu thay đổi
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-cog"></i> Thao tác
                                </h6>
                            </div>
                            <div class="card-body">
                                <%
                                    boolean isSelf = (u != null && currentUser != null && u.getUserId() == currentUser.getUserId());
                                    if (!isSelf) {
                                        if (u.isActive()) {
                                %>
                                <button type="button" class="btn btn-danger btn-block mb-2"
                                        onclick="showToggleModal(<%= u.getUserId() %>, '<%= u.getUsername() %>', false)">
                                    <i class="fas fa-user-slash"></i> Ban tài khoản
                                </button>
                                <%
                                        } else {
                                %>
                                <button type="button" class="btn btn-success btn-block mb-2"
                                        onclick="showToggleModal(<%= u.getUserId() %>, '<%= u.getUsername() %>', true)">
                                    <i class="fas fa-user-check"></i> Mở ban tài khoản
                                </button>
                                <%
                                        }
                                    }
                                %>

                                <a href="${pageContext.request.contextPath}/admin/user" class="btn btn-secondary btn-block">
                                    <i class="fas fa-arrow-left"></i> Quay lại
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <% } %>

            </div>

        </div>

        <%@ include file="/views/layout/footer.jsp" %>

    </div>
</div>

<!-- Modal xác nhận Ban / Mở ban -->
<div class="modal fade" id="toggleModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="toggleModalTitle">Xác nhận</h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body">
                <p id="toggleModalBody"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                <form id="toggleForm" action="${pageContext.request.contextPath}/admin/user/toggle-active" method="post" class="d-inline">
                    <input type="hidden" id="toggleId" name="id">
                    <input type="hidden" id="toggleActive" name="active">
                    <button type="submit" class="btn" id="toggleConfirmBtn">Xác nhận</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

<script>
    function showToggleModal(userId, username, setActive) {
        $('#toggleId').val(userId);
        $('#toggleActive').val(setActive);
        if (setActive) {
            $('#toggleModalTitle').text('Mở ban tài khoản');
            $('#toggleModalBody').html('Bạn có chắc muốn <strong>mở ban</strong> tài khoản <strong>' + username + '</strong>?');
            $('#toggleConfirmBtn').removeClass('btn-danger').addClass('btn-success').text('Mở ban');
        } else {
            $('#toggleModalTitle').text('Ban tài khoản');
            $('#toggleModalBody').html('Bạn có chắc muốn <strong>ban</strong> tài khoản <strong>' + username + '</strong>? Người dùng sẽ không thể đăng nhập.');
            $('#toggleConfirmBtn').removeClass('btn-success').addClass('btn-danger').text('Ban');
        }
        $('#toggleModal').modal('show');
    }
</script>

</body>
</html>
