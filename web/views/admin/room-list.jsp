<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Room" %>

<%
    User currentUser = (User) session.getAttribute("currentUser");
    List<String> roles = null;
    boolean canManageRoom = false;
    if (currentUser != null) {
        roles = currentUser.getRoles();
        // Room management chỉ dành cho ADMIN
        canManageRoom = roles != null && roles.contains("ADMIN");
    }

    @SuppressWarnings("unchecked")
    List<Room> roomList = (List<Room>) request.getAttribute("rooms");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Quản lý phòng | School Asset Management</title>

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
                        <i class="fas fa-door-open text-primary"></i> Room management
                    </h1>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-circle"></i> <%= error %>
                        <button type="button" class="close" data-dismiss="alert">
                            <span>&times;</span>
                        </button>
                    </div>
                <% } %>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-list"></i> View Room list
                            <span class="badge badge-primary">
                                <%= (roomList != null) ? roomList.size() : 0 %>
                            </span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover" id="roomTable" width="100%" cellspacing="0">
                                <thead class="thead-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Room name</th>
                                    <th>Location</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (roomList == null || roomList.isEmpty()) {
                                %>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted">
                                            <i class="fas fa-inbox fa-3x mb-3 mt-3"></i>
                                            <p>Chưa có phòng nào</p>
                                        </td>
                                    </tr>
                                <%
                                    } else {
                                        for (Room room : roomList) {
                                %>
                                    <tr>
                                        <td><%= room.getRoomId() %></td>
                                        <td><strong><%= room.getRoomName() %></strong></td>
                                        <td><%= (room.getLocation() != null ? room.getLocation() : "-") %></td>
                                        <td class="text-center">
                                            <!-- View Detail room -->
                                            <a href="<%= request.getContextPath() %>/rooms/detail?id=<%= room.getRoomId() %>"
                                               class="btn btn-sm btn-info" title="View Detail room">
                                                <i class="fas fa-eye"></i>
                                            </a>

                                            <!-- Config Room -->
                                            <% if (canManageRoom) { %>
                                                <a href="<%= request.getContextPath() %>/rooms/config?id=<%= room.getRoomId() %>"
                                                   class="btn btn-sm btn-warning" title="Config Room">
                                                    <i class="fas fa-cog"></i>
                                                </a>
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

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
    $(document).ready(function () {
        $('#roomTable').DataTable({
            "language": {
                "lengthMenu": "Hiển thị _MENU_ phòng mỗi trang",
                "zeroRecords": "Không tìm thấy phòng nào",
                "info": "Trang _PAGE_ / _PAGES_",
                "infoEmpty": "Không có dữ liệu",
                "infoFiltered": "(lọc từ _MAX_ phòng)",
                "search": "Tìm kiếm:",
                "paginate": {
                    "first": "Đầu",
                    "last": "Cuối",
                    "next": "Sau",
                    "previous": "Trước"
                }
            },
            "pageLength": 10
        });
    });
</script>

</body>
</html>

