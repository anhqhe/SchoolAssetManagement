<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/views/layout/system-ui-config.jspf" %>

<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

    <ul class="navbar-nav ml-auto">

        <!-- USER DROPDOWN -->
        <li class="nav-item dropdown no-arrow">
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown"
               role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">

                <!-- USER NAME -->
                <span class="mr-2 d-none d-lg-inline text-gray-600 small">
                    <% 
                        model.User topbarUser = (model.User) session.getAttribute("currentUser");
                        out.print(topbarUser != null ? topbarUser.getFullName() : "");
                    %>
                </span>

                <!-- AVATAR (SBAdmin mặc định) -->
                <img class="img-profile rounded-circle"
                     src="${pageContext.request.contextPath}/assets/img/undraw_profile.svg">
            </a>

            <!-- DROPDOWN MENU -->
            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                 aria-labelledby="userDropdown">

                <a class="dropdown-item"
                   href="${pageContext.request.contextPath}/profile">
                    <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                    Hồ sơ
                </a>

                <div class="dropdown-divider"></div>

                <a class="dropdown-item"
                   href="${pageContext.request.contextPath}/views/auth/change-password.jsp">
                    <i class="fas fa-key fa-sm fa-fw mr-2 text-gray-400"></i>
                    Thay đổi mật khẩu
                </a>

                <div class="dropdown-divider"></div>

                <a class="dropdown-item"
                   href="${pageContext.request.contextPath}/auth/logout">
                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                    Đăng xuất
                </a>
            </div>
        </li>
    </ul>
</nav>

<%
    Object enabledObj = request.getAttribute("uiBannerEnabled");
    boolean bannerEnabled = (enabledObj instanceof Boolean) ? (Boolean) enabledObj : false;
    String bannerText = String.valueOf(request.getAttribute("uiBannerText") == null ? "" : request.getAttribute("uiBannerText"));
    if (bannerEnabled && bannerText != null && !bannerText.trim().isEmpty()) {
%>
<div class="container-fluid">
    <div class="alert alert-warning shadow-sm mb-4" role="alert" style="border-left: .25rem solid var(--ui-primary);">
        <i class="fas fa-bullhorn mr-1"></i>
        <%= bannerText %>
    </div>
</div>
<%
    }
%>
