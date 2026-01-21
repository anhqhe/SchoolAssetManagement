<%@ page contentType="text/html; charset=UTF-8" %>

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
                   href="${pageContext.request.contextPath}/views/auth/change-password.jsp">
                    <i class="fas fa-key fa-sm fa-fw mr-2 text-gray-400"></i>
                    Change Password
                </a>

                <div class="dropdown-divider"></div>

                <a class="dropdown-item"
                   href="${pageContext.request.contextPath}/auth/logout">
                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                    Logout
                </a>
            </div>
        </li>
    </ul>
</nav>
