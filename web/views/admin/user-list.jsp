<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>

<%
    @SuppressWarnings("unchecked")
    List<User> users = (List<User>) request.getAttribute("users");
    String error = (String) request.getAttribute("error");
    String successParam = request.getParameter("success");
    String errorParam = request.getParameter("error");

    @SuppressWarnings("unchecked")
    List<String> allRoles = (List<String>) request.getAttribute("allRoles");
    String statusFilter = (String) request.getAttribute("statusFilter");
    String roleFilter = (String) request.getAttribute("roleFilter");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Quản lý người dùng | School Asset Management</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.css" rel="stylesheet">
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
                        <i class="fas fa-users text-primary"></i> Quản lý người dùng
                    </h1>
                    <a href="${pageContext.request.contextPath}/admin/user/create" class="btn btn-primary btn-sm">
                        <i class="fas fa-user-plus"></i> Tạo tài khoản
                    </a>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-circle"></i> <%= error %>
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } %>

                <% if ("created".equals(successParam)) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> Tạo tài khoản thành công.
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } else if ("banned".equals(successParam)) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> Đã ban tài khoản thành công.
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } else if ("unbanned".equals(successParam)) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle"></i> Đã mở ban tài khoản thành công.
                    <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                </div>
                <% } %>

                <div class="card shadow mb-4">
                    <div class="card-header py-3 d-flex align-items-center justify-content-between">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-list"></i> Danh sách người dùng
                            <span class="badge badge-primary"><%= (users != null) ? users.size() : 0 %></span>
                        </h6>

                        <form class="form-inline" method="get" action="${pageContext.request.contextPath}/admin/user">
                            <div class="form-group mr-2 mb-2">
                                <label for="statusFilter" class="mr-1">Trạng thái</label>
                                <select id="statusFilter" name="status" class="form-control form-control-sm">
                                    <option value="" <%= (statusFilter == null || statusFilter.isEmpty()) ? "selected" : "" %>>Tất cả</option>
                                    <option value="active" <%= "active".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Hoạt động</option>
                                    <option value="banned" <%= "banned".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Đã ban</option>
                                </select>
                            </div>

                            <div class="form-group mr-2 mb-2">
                                <label for="roleFilter" class="mr-1">Vai trò</label>
                                <select id="roleFilter" name="role" class="form-control form-control-sm">
                                    <option value="" <%= (roleFilter == null || roleFilter.isEmpty()) ? "selected" : "" %>>Tất cả</option>
                                    <%
                                        if (allRoles != null) {
                                            for (String rc : allRoles) {
                                                boolean selected = roleFilter != null && roleFilter.equals(rc);
                                    %>
                                    <option value="<%= rc %>" <%= selected ? "selected" : "" %>><%= rc %></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <button type="submit" class="btn btn-sm btn-outline-primary mb-2">
                                <i class="fas fa-filter"></i> Lọc
                            </button>
                        </form>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" id="userTable" width="100%" cellspacing="0">
                                <thead class="thead-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Tài khoản</th>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>SĐT</th>
                                    <th>Vai trò</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (users == null || users.isEmpty()) {
                                %>
                                <tr>
                                    <td colspan="8" class="text-center text-muted">
                                        <i class="fas fa-inbox fa-3x mb-3 mt-3"></i>
                                        <p>Chưa có người dùng nào</p>
                                    </td>
                                </tr>
                                <%
                                    } else {
                                        for (User u : users) {
                                            String rolesStr = "-";
                                            if (u.getRoles() != null && !u.getRoles().isEmpty()) {
                                                rolesStr = String.join(", ", u.getRoles());
                                            }
                                %>
                                <tr>
                                    <td><%= u.getUserId() %></td>
                                    <td><%= u.getUsername() %></td>
                                    <td><%= u.getFullName() != null ? u.getFullName() : "-" %></td>
                                    <td><%= u.getEmail() != null ? u.getEmail() : "-" %></td>
                                    <td><%= u.getPhone() != null ? u.getPhone() : "-" %></td>
                                    <td>
                                        <%
                                            if (u.getRoles() != null) {
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
                                        <span class="text-muted">-</span>
                                        <%  } %>
                                    </td>
                                    <td class="text-center">
                                        <% if (u.isActive()) { %>
                                            <span class="badge badge-success">Hoạt động</span>
                                        <% } else { %>
                                            <span class="badge badge-danger">Đã ban</span>
                                        <% } %>
                                    </td>
                                    <td class="text-center text-nowrap">
                                        <a href="${pageContext.request.contextPath}/admin/user/detail?id=<%= u.getUserId() %>"
                                           class="btn btn-sm btn-info" title="Xem chi tiết">
                                            <i class="fas fa-eye"></i>
                                        </a>

                                        <% if (u.isActive()) { %>
                                        <button type="button" class="btn btn-sm btn-danger" title="Ban"
                                                onclick="showToggleModal(<%= u.getUserId() %>, '<%= u.getUsername() %>', false)">
                                            <i class="fas fa-user-slash"></i>
                                        </button>
                                        <% } else { %>
                                        <button type="button" class="btn btn-sm btn-success" title="Mở ban"
                                                onclick="showToggleModal(<%= u.getUserId() %>, '<%= u.getUsername() %>', true)">
                                            <i class="fas fa-user-check"></i>
                                        </button>
                                        <% } %>
                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

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
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
    $(document).ready(function () {
        $('#userTable').DataTable({
            "language": {
                "lengthMenu": "Hiển thị _MENU_ người dùng mỗi trang",
                "zeroRecords": "Không tìm thấy người dùng nào",
                "info": "Trang _PAGE_ / _PAGES_",
                "infoEmpty": "Không có dữ liệu",
                "infoFiltered": "(lọc từ _MAX_ người dùng)",
                "search": "Tìm kiếm:",
                "paginate": {
                    "first": "Đầu",
                    "last": "Cuối",
                    "next": "Sau",
                    "previous": "Trước"
                }
            },
            "pageLength": 10,
            "order": [[0, "desc"]]
        });
    });

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
