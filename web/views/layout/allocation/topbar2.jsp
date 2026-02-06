<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.allocation.NotificationDAO, model.allocation.Notification, java.util.List" %>

<%
    model.User topbarUser = (model.User) session.getAttribute("currentUser");
    List<Notification> unreadNotis = java.util.Collections.emptyList();
    int unreadCount = 0;
    if (topbarUser != null) {
        try {
            NotificationDAO notiDao = new NotificationDAO();
            unreadNotis = notiDao.getUnreadByUserId(topbarUser.getUserId());
            unreadCount = unreadNotis != null ? unreadNotis.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>

<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

    <ul class="navbar-nav ml-auto">

        <!-- NOTIFICATION -->
        <li class="nav-item dropdown no-arrow mx-1">
            <a class="nav-link dropdown-toggle" href="#" id="alertsDropdown"
               role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-bell fa-fw"></i>
                <span class="badge badge-danger badge-counter" id="notiCount" style="<%= unreadCount > 0 ? "" : "display: none;" %>"><%= unreadCount %></span>
            </a>
            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                 aria-labelledby="alertsDropdown" style="min-width: 320px;">
                <h6 class="dropdown-header">Thông báo</h6>
                <div id="notiList">
                    <% if (unreadCount == 0) { %>
                        <div class="dropdown-item text-wrap text-gray-700" id="notiEmpty">Không có thông báo mới</div>
                    <% } else { %>
                        <% for (Notification noti : unreadNotis) { %>
                            <div class="dropdown-item text-wrap text-gray-700"><%= noti.getContent() %></div>
                        <% } %>
                    <% } %>
                </div>
            </div>
        </li>

        <!-- USER DROPDOWN -->
        <li class="nav-item dropdown no-arrow">
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown"
               role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">

                <!-- USER NAME -->
                <span class="mr-2 d-none d-lg-inline text-gray-600 small">
                    <% out.print(topbarUser != null ? topbarUser.getFullName() : ""); %>
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

<script>
    const userId = "${sessionScope.currentUser.userId}";
    let unreadCount = <%= unreadCount %>;
    if (userId) {
        const protocol = (location.protocol === 'https:') ? 'wss://' : 'ws://';
        const socketUrl = protocol + location.host + '${pageContext.request.contextPath}/notifications/' + userId;
        let socket;
        let reconnectInterval = 3000;

        function updateBadge() {
            const countBadge = document.getElementById('notiCount');
            if (!countBadge) return;
            if (unreadCount > 0) {
                countBadge.style.display = 'inline-block';
                countBadge.innerText = String(unreadCount);
            } else {
                countBadge.style.display = 'none';
                countBadge.innerText = '0';
            }
        }

        function connect() {
            console.log("Connecting to WebSocket:", socketUrl);
            socket = new WebSocket(socketUrl);

            socket.onopen = function () {
                console.log("WebSocket connected with userId:", userId);
                reconnectInterval = 3000;
            };

            socket.onmessage = function (event) {
                console.log("WebSocket message:", event.data);
                const list = document.getElementById('notiList');
                const empty = document.getElementById('notiEmpty');
                if (empty) {
                    empty.remove();
                }
                if (list) {
                    const item = document.createElement('div');
                    item.className = 'dropdown-item text-wrap text-gray-700';
                    item.textContent = event.data;
                    list.prepend(item);
                }
                unreadCount += 1;
                updateBadge();
            };

            socket.onerror = function (err) {
                console.error('WebSocket error:', err);
            };

            socket.onclose = function () {
                console.log("WebSocket closed. Reconnecting in " + reconnectInterval + "ms...");
                setTimeout(function() {
                    reconnectInterval = Math.min(60000, reconnectInterval * 2);
                    connect();
                }, reconnectInterval);
            };
        }

        connect();

        const alertsDropdown = document.getElementById('alertsDropdown');
        if (alertsDropdown) {
            alertsDropdown.addEventListener('click', function () {
                unreadCount = 0;
                updateBadge();
            });
        }
    } else {
        console.log("No user in session - WebSocket notifications disabled");
    }
</script>
