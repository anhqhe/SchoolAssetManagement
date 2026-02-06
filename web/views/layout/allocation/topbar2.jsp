<%@ page contentType="text/html; charset=UTF-8" %>

<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

    <ul class="navbar-nav ml-auto">

        <!-- NOTIFICATION -->
        <li class="nav-item dropdown no-arrow mx-1">
            <a class="nav-link dropdown-toggle" href="#" id="alertsDropdown"
               role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-bell fa-fw"></i>
                <span class="badge badge-danger badge-counter" id="notiCount" style="display: none;">1</span>
            </a>
            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                 aria-labelledby="alertsDropdown" style="min-width: 320px;">
                <h6 class="dropdown-header">Thông báo</h6>
                <div class="dropdown-item text-wrap text-gray-700" id="notiDropdownMessage">
                    Không có thông báo mới
                </div>
            </div>
        </li>

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

<script>
    const userId = "${sessionScope.currentUser.userId}";
    let unreadCount = 0;
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
                const dropdownMsg = document.getElementById('notiDropdownMessage');
                if (dropdownMsg) {
                    dropdownMsg.innerText = event.data;
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
