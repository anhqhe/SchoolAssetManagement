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

    <style>
        /* ===== INVENTORY MODAL STYLES ===== */
        .inventory-modal .modal-header {
            background: linear-gradient(135deg, #4e73df 0%, #224abe 100%);
            color: #fff;
        }
        .inventory-modal .modal-header .close { color: #fff; opacity: 0.9; }

        /* Nút kiểm kê tổng quan ở header */
        .btn-inventory-all {
            background: linear-gradient(135deg, #1cc88a, #13855c);
            color: #fff;
            border: none;
            font-weight: 600;
            letter-spacing: 0.02em;
            transition: opacity .2s;
        }
        .btn-inventory-all:hover { opacity: .85; color: #fff; }

        /* Bảng trong modal tổng quan */
        .inv-all-table thead th { background: #f8f9fc; font-size:.78rem; text-transform:uppercase; color:#4e73df; }
        .inv-all-table .badge-count {
            background: #4e73df;
            color: #fff;
            border-radius: 20px;
            padding: 3px 10px;
            font-size: .82rem;
            font-weight: 600;
        }
        .inv-all-table .badge-zero { background: #e2e6ea; color: #858796; }

        /* Bảng/card trong modal chi tiết */
        .inv-detail-header { background: linear-gradient(135deg, #4e73df 0%, #224abe 100%); color:#fff; border-radius:8px; padding:14px 18px; margin-bottom:14px; }
        .inv-cat-card { border-left: 4px solid #4e73df; border-radius:6px; background:#f8f9fc; margin-bottom:8px; padding:10px 16px; display:flex; justify-content:space-between; align-items:center; }
        .inv-cat-card .cat-name { font-weight:600; color:#3a3b45; }
        .inv-cat-card .cat-count { background:#4e73df; color:#fff; border-radius:20px; padding:3px 14px; font-size:.85rem; font-weight:700; min-width:38px; text-align:center; }
        .inv-cat-card.empty .cat-count { background:#e2e6ea; color:#858796; }

        /* Spinner */
        .inv-loading { text-align:center; padding:30px 0; color:#858796; }
        .inv-error   { text-align:center; padding:20px 0; color:#e74a3b; }

        /* Thanh tổng */
        .inv-total-bar { background:#eaecf4; border-radius:8px; padding:10px 16px; font-weight:700; color:#4e73df; font-size:1rem; display:flex; justify-content:space-between; margin-bottom:14px; }
        .inv-total-bar span:last-child { background:#4e73df; color:#fff; border-radius:20px; padding:2px 16px; }
    </style>
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
                        <i class="fas fa-door-open text-primary"></i> Quản lý phòng
                    </h1>
                    <% if (canManageRoom) { %>
                    <!-- NÚT 1: Kiểm kê tổng quan tất cả phòng -->
                    <button class="btn btn-inventory-all btn-sm shadow-sm"
                            id="btnInventoryAll" title="Xem số tài sản của tất cả các phòng">
                        <i class="fas fa-clipboard-list mr-1"></i> Kiểm kê tổng quan
                    </button>
                    <% } %>
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
                                    <th>Tên phòng</th>
                                    <th>Vị trí</th>
                                    <th>Giáo viên phụ trách</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    if (roomList == null || roomList.isEmpty()) {
                                %>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted">
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
                                        <td>
                                            <% if (room.getHeadTeacherName() != null) { %>
                                                <span class="badge badge-success"><i class="fas fa-user-tie"></i> <%= room.getHeadTeacherName() %></span>
                                            <% } else { %>
                                                <span class="text-muted font-italic">Chưa phân công</span>
                                            <% } %>
                                        </td>
                                        <td class="text-center" style="white-space:nowrap;">
                                            <!-- View Detail room -->
                                            <a href="<%= request.getContextPath() %>/rooms/detail?id=<%= room.getRoomId() %>"
                                               class="btn btn-sm btn-info" title="View Detail room">
                                                <i class="fas fa-eye"></i>
                                            </a>

                                            <!-- NÚT 2: Kiểm kê chi tiết theo danh mục của phòng này -->
                                            <button class="btn btn-sm btn-primary btn-inventory-detail"
                                                    data-room-id="<%= room.getRoomId() %>"
                                                    data-room-name="<%= room.getRoomName() %>"
                                                    title="Kiểm kê tài sản theo danh mục">
                                                <i class="fas fa-chart-pie"></i>
                                            </button>

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

<!-- =========================================================
     MODAL 1: KIỂM KÊ TỔNG QUAN (tất cả phòng)
     ========================================================= -->
<div class="modal fade inventory-modal" id="modalInventoryAll" tabindex="-1" role="dialog"
     aria-labelledby="modalInventoryAllLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalInventoryAllLabel">
                    <i class="fas fa-clipboard-list mr-2"></i>Kiểm kê tổng quan theo phòng
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="inventoryAllBody">
                <div class="inv-loading">
                    <i class="fas fa-spinner fa-spin fa-2x mb-2"></i><br>Đang tải dữ liệu...
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<!-- =========================================================
     MODAL 2: KIỂM KÊ CHI TIẾT (1 phòng, theo danh mục)
     ========================================================= -->
<div class="modal fade inventory-modal" id="modalInventoryDetail" tabindex="-1" role="dialog"
     aria-labelledby="modalInventoryDetailLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalInventoryDetailLabel">
                    <i class="fas fa-chart-pie mr-2"></i>Kiểm kê chi tiết phòng
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="inventoryDetailBody">
                <div class="inv-loading">
                    <i class="fas fa-spinner fa-spin fa-2x mb-2"></i><br>Đang tải dữ liệu...
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<!-- ===================== SCRIPTS ===================== -->
<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>

<script>
    var CTX = '${pageContext.request.contextPath}';

    /* ---- DataTables ---- */
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
                    "first": "Đầu", "last": "Cuối",
                    "next": "Sau", "previous": "Trước"
                }
            },
            "pageLength": 10
        });
    });

    /* ====================================================
       NÚT 1: Kiểm kê TỔNG QUAN – tất cả phòng
       ==================================================== */
    $('#btnInventoryAll').on('click', function () {
        // Hiện modal, reset nội dung
        $('#inventoryAllBody').html(
            '<div class="inv-loading"><i class="fas fa-spinner fa-spin fa-2x mb-2"></i><br>Đang tải dữ liệu...</div>'
        );
        $('#modalInventoryAll').modal('show');

        $.ajax({
            url: CTX + '/rooms/inventory/all',
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                if (!data || data.length === 0) {
                    $('#inventoryAllBody').html('<div class="inv-error"><i class="fas fa-info-circle"></i> Không có dữ liệu phòng.</div>');
                    return;
                }

                var totalAll = data.reduce(function(s, r){ return s + r.totalAssets; }, 0);

                var html = '<div class="inv-total-bar">'
                         + '<span><i class="fas fa-warehouse mr-1"></i> Tổng tài sản toàn trường</span>'
                         + '<span>' + totalAll + '</span>'
                         + '</div>';

                html += '<table class="table table-sm table-hover inv-all-table mb-0">'
                      + '<thead><tr>'
                      + '<th style="width:50px">#</th>'
                      + '<th>Tên phòng</th>'
                      + '<th>Vị trí</th>'
                      + '<th class="text-center">Số lượng tài sản</th>'
                      + '</tr></thead><tbody>';

                $.each(data, function (i, room) {
                    var badgeClass = room.totalAssets > 0 ? 'badge-count' : 'badge-count badge-zero';
                    html += '<tr>'
                          + '<td class="text-muted">' + (i + 1) + '</td>'
                          + '<td><strong>' + escHtml(room.roomName) + '</strong></td>'
                          + '<td class="text-muted">' + (room.location || '-') + '</td>'
                          + '<td class="text-center"><span class="' + badgeClass + '">' + room.totalAssets + '</span></td>'
                          + '</tr>';
                });

                html += '</tbody></table>';
                $('#inventoryAllBody').html(html);
            },
            error: function () {
                $('#inventoryAllBody').html('<div class="inv-error"><i class="fas fa-exclamation-triangle mr-1"></i>Không thể tải dữ liệu. Vui lòng thử lại.</div>');
            }
        });
    });

    /* ====================================================
       NÚT 2: Kiểm kê CHI TIẾT – 1 phòng theo danh mục
       ==================================================== */
    $(document).on('click', '.btn-inventory-detail', function () {
        var roomId   = $(this).data('room-id');
        var roomName = $(this).data('room-name');

        $('#modalInventoryDetailLabel').html('<i class="fas fa-chart-pie mr-2"></i>Kiểm kê: ' + escHtml(roomName));
        $('#inventoryDetailBody').html(
            '<div class="inv-loading"><i class="fas fa-spinner fa-spin fa-2x mb-2"></i><br>Đang tải dữ liệu...</div>'
        );
        $('#modalInventoryDetail').modal('show');

        $.ajax({
            url: CTX + '/rooms/inventory/detail',
            method: 'GET',
            data: { roomId: roomId },
            dataType: 'json',
            success: function (data) {
                var html = '<div class="inv-detail-header">'
                         + '<div class="font-weight-bold"><i class="fas fa-door-open mr-1"></i> ' + escHtml(data.roomName) + '</div>'
                         + (data.location ? '<small class="opacity-75"><i class="fas fa-map-marker-alt mr-1"></i>' + escHtml(data.location) + '</small>' : '')
                         + '</div>';

                if (!data.categories || data.categories.length === 0) {
                    html += '<div class="text-center text-muted py-3">'
                          + '<i class="fas fa-box-open fa-2x mb-2"></i><br>Phòng này chưa có tài sản nào.</div>';
                } else {
                    html += '<div class="inv-total-bar">'
                          + '<span><i class="fas fa-cubes mr-1"></i> Tổng tài sản trong phòng</span>'
                          + '<span>' + data.totalAssets + '</span>'
                          + '</div>';

                    $.each(data.categories, function (i, cat) {
                        html += '<div class="inv-cat-card' + (cat.count === 0 ? ' empty' : '') + '">'
                              + '<span class="cat-name"><i class="fas fa-tag mr-1 text-primary"></i>' + escHtml(cat.name) + '</span>'
                              + '<span class="cat-count">' + cat.count + '</span>'
                              + '</div>';
                    });
                }

                $('#inventoryDetailBody').html(html);
            },
            error: function () {
                $('#inventoryDetailBody').html('<div class="inv-error"><i class="fas fa-exclamation-triangle mr-1"></i>Không thể tải dữ liệu. Vui lòng thử lại.</div>');
            }
        });
    });

    /* Escape HTML để tránh XSS */
    function escHtml(str) {
        if (!str) return '';
        return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }
</script>

</body>
</html>
