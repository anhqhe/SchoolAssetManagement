<%@ page contentType="text/html; charset=UTF-8" %>

<%
    String uiPrimaryColor = (String) request.getAttribute("uiPrimaryColor");
    if (uiPrimaryColor == null || !uiPrimaryColor.matches("^#[0-9a-fA-F]{6}$")) {
        uiPrimaryColor = "#4e73df";
    }
%>

<style>
    :root {
        --primary: <%= uiPrimaryColor %>;
        --blue: <%= uiPrimaryColor %>;
        --ui-primary: <%= uiPrimaryColor %>;
    }

    /* sb-admin-2.min.css hard-codes màu #4e73df.
       Override bằng !important để theme đổi ngay lập tức. */
    .bg-gradient-primary {
        background-color: var(--primary) !important;
        background-image: linear-gradient(180deg, var(--primary) 10%, #224abe 100%) !important;
        background-size: cover !important;
    }
    .text-primary {
        color: var(--primary) !important;
    }
    .bg-primary {
        background-color: var(--primary) !important;
        border-color: var(--primary) !important;
    }
    .btn-primary {
        background-color: var(--primary) !important;
        border-color: var(--primary) !important;
        color: #fff !important;
    }
    .btn-primary:hover,
    .btn-primary:focus,
    .btn-primary.focus,
    .btn-primary:not(:disabled):not(.disabled):active,
    .btn-primary:not(:disabled):not(.disabled).active {
        background-color: var(--primary) !important;
        border-color: var(--primary) !important;
        color: #fff !important;
    }
    .border-left-primary {
        border-left-color: var(--primary) !important;
    }
    .sidebar .nav-item .collapse .collapse-inner .collapse-item.active,
    .sidebar .nav-item .collapsing .collapse-inner .collapse-item.active {
        color: var(--primary) !important;
        font-weight: 700 !important;
    }
</style>

<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- Brand -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center"
       href="${pageContext.request.contextPath}/admin/dashboard">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-school"></i>
        </div>
        <div class="sidebar-brand-text mx-3">School AM</div>
    </a>

    <hr class="sidebar-divider my-0">

    <% 
        model.User sidebarUser = (model.User) session.getAttribute("currentUser");
        java.util.List<String> sidebarRoles = (sidebarUser != null) ? sidebarUser.getRoles() : null;
    %>

    <!-- Dashboard (ADMIN ONLY) -->
    <% if (sidebarRoles != null && sidebarRoles.contains("ADMIN")) { %>
    <li class="nav-item active">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Trang tổng quan</span>
        </a>
    </li>
    <% } %>

    <hr class="sidebar-divider">

    <!-- ADMIN ONLY -->
    <% if (sidebarRoles != null && sidebarRoles.contains("ADMIN")) { %>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/rooms">
            <i class="fas fa-fw fa-door-open"></i>
            <span>Quản lý phòng</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/user">
            <i class="fas fa-fw fa-users"></i>
            <span>Quản lý người dùng</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/settings">
            <i class="fas fa-fw fa-cogs"></i>
            <span>Cài đặt</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-report?type=inventory">
            <i class="fas fa-file-alt"></i>
            <span>Báo cáo tài sản</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-report?type=usage">
            <i class="fas fa-file-alt"></i>
            <span>Báo cáo sử dụng TS</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/transfers/list">
            <i class="fas fa-fw fa-clipboard-list"></i>
            <span>Điều chuyển tài sản</span>
        </a>
    </li>

    <% } %>

    <!-- ASSET_STAFF ONLY -->
    <% if (sidebarRoles != null && sidebarRoles.contains("ASSET_STAFF") && !sidebarRoles.contains("ADMIN")) { %>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/assets">
            <i class="fas fa-fw fa-boxes"></i>
            <span>Quản lý tài sản</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">
            <i class="fas fa-fw fa-tags"></i>
            <span>Quản lý danh mục</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/staff/request-list">
            <i class="fas fa-list"></i>
            <span>Danh sách yêu cầu</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/staff/allocation-history">
            <i class="fas fa-history"></i>
            <span>Lịch sử cấp phát</span>
        </a>
    </li>
    
    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/transfers/list">
            <i class="fas fa-fw fa-clipboard-list"></i>
            <span>Điều chuyển tài sản</span>
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-history-transfer/list">
            <i class="fas fa-history"></i>
            <span>Lịch sử điều chuyển tài sản</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-increase">
            <i class="fas fa-fw fa-plus-circle"></i>
            <span>Lịch sử ghi tăng TS</span>
        </a>
    </li>
    
     <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-report?type=inventory">
            <i class="fas fa-file-alt"></i>
            <span>Báo cáo tài sản</span>
        </a>
    </li>
    
    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-report?type=usage">
            <i class="fas fa-file-alt"></i>
            <span>Báo cáo sử dụng TS</span>
        </a>
    </li>


    <% } %>


    <!-- TEACHER ONLY -->
    <% if (sidebarRoles != null && sidebarRoles.contains("TEACHER") && !sidebarRoles.contains("ADMIN")) { %>


    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/teacher/request-list">
            <i class="fas fa-list"></i>
            <span>Danh sách yêu cầu</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/teacher/allocation-history">
            <i class="fas fa-history"></i>
            <span>Lịch sử cấp phát</span>
        </a>
    </li>


    <% } %>

    <!-- BOARD ONLY -->
    <% if (sidebarRoles != null && sidebarRoles.contains("BOARD") && !sidebarRoles.contains("ADMIN")) { %>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/board/request-list">
            <i class="fas fa-list"></i>
            <span>Danh sách yêu cầu</span>
        </a>
    </li>

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/board/allocation-history">
            <i class="fas fa-history"></i>
            <span>Lịch sử cấp phát</span>
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/transfers/list">
            <i class="fas fa-fw fa-clipboard-list"></i>
            <span>Điều chuyển tài sản</span>
        </a>
    </li>

    </li>

       <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-history-transfer/list">
            <i class="fas fa-history"></i>
            <span>Lịch sử điều chuyển tài sản</span>
        </a>
    </li>
    
    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-report?type=inventory">
            <i class="fas fa-file-alt"></i>
            <span>Báo cáo tài sản</span>
        </a>
    
    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/asset-report?type=usage">
            <i class="fas fa-file-alt"></i>
            <span>Báo cáo sử dụng TS</span>
        </a>
    </li>

    <% } %>

    <!-- ALL USERS -->
    <hr class="sidebar-divider">

    <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/change-password">
            <i class="fas fa-key"></i>
            <span>Thay đổi mật khẩu</span>
        </a>
    </li>

    <hr class="sidebar-divider d-none d-md-block">

    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>
</ul>