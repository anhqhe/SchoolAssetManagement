<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.allocation.NotificationDAO, model.allocation.Notification, java.util.List" %>
<%@ include file="/views/layout/system-ui-config.jspf" %>

<!-- Set role flags -->
<c:set var="isTeacher" value="false"/>
<c:set var="isStaff" value="false"/>
<c:set var="isBoard" value="false"/>
<c:forEach var="role" items="${sessionScope.currentUser.roles}">
    <c:if test="${role eq 'TEACHER'}"><c:set var="isTeacher" value="true"/></c:if>
    <c:if test="${role eq 'ASSET_STAFF'}"><c:set var="isStaff" value="true"/></c:if>
    <c:if test="${role eq 'BOARD'}"><c:set var="isBoard" value="true"/></c:if>
</c:forEach>


<%
    model.User topbarUser = (model.User) session.getAttribute("currentUser");
    List<Notification> recentNotis = java.util.Collections.emptyList();
    int unreadCount = 0;
    if (topbarUser != null) {
        try {
            NotificationDAO notiDao = new NotificationDAO();
            recentNotis = notiDao.getTop10ByUserId(topbarUser.getUserId());
            if (recentNotis != null) {
                for (Notification noti : recentNotis) {
                    if (!noti.isRead()) {
                        unreadCount++;
                    }
                }
            }
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
                    <% if (recentNotis == null || recentNotis.isEmpty()) { %>
                    <div class="dropdown-item text-wrap text-gray-700" id="notiEmpty">Không có thông báo</div>
                    <% } else { %>
                    <% for (Notification noti : recentNotis) { %>
                    <div class="dropdown-item text-wrap noti-item <%= noti.isRead() ? "text-gray-500" : "text-gray-900 font-weight-bold" %>"
                         data-noti-id="<%= noti.getNotificationId() %>"
                         data-is-read="<%= noti.isRead() ? "1" : "0" %>">

                        <c:choose>
                            <c:when test="${isTeacher}">
                                <a href="${pageContext.request.contextPath}/teacher/request-detail?id=<%= noti.getRefId() %>" class="text-decoration-none text-reset">
                                    <%= noti.getContent() %>
                                </a>
                            </c:when>
                            <c:when test="${isStaff}">
                                <a href="${pageContext.request.contextPath}/staff/request-detail?id=<%= noti.getRefId() %>" class="text-decoration-none text-reset">
                                    <%= noti.getContent() %>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/board/request-detail?id=<%= noti.getRefId() %>" class="text-decoration-none text-reset">
                                    <%= noti.getContent() %>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
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
    Object enabledObj2 = request.getAttribute("uiBannerEnabled");
    boolean bannerEnabled2 = (enabledObj2 instanceof Boolean) ? (Boolean) enabledObj2 : false;
    String bannerText2 = String.valueOf(request.getAttribute("uiBannerText") == null ? "" : request.getAttribute("uiBannerText"));
    if (bannerEnabled2 && bannerText2 != null && !bannerText2.trim().isEmpty()) {
%>
<div class="container-fluid">
    <div class="alert alert-warning shadow-sm mb-4" role="alert" style="border-left: .25rem solid var(--ui-primary);">
        <i class="fas fa-bullhorn mr-1"></i>
        <%= bannerText2 %>
    </div>
</div>
<%
    }
%>

<script>
    const userId = "${sessionScope.currentUser.userId}";
    const contextPath = "${pageContext.request.contextPath}";
    let unreadCount = <%= unreadCount %>;
    if (userId) {
        const protocol = (location.protocol === 'https:') ? 'wss://' : 'ws://';
        const socketUrl = protocol + location.host + contextPath + '/notifications/' + userId;
        let socket;
        let reconnectInterval = 3000;

        function updateBadge() {
            const countBadge = document.getElementById('notiCount');
            if (!countBadge)
                return;
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
                    let data;
                    try {
                        data = JSON.parse(event.data);
                    } catch (e) {
                        data = {content: event.data, id: -1};
                    }
                    const item = document.createElement('div');
                    item.className = 'dropdown-item text-wrap text-gray-900 font-weight-bold noti-item';
                    item.textContent = data.content || '';
                    if (data.id && data.id > 0) {
                        item.setAttribute('data-noti-id', String(data.id));
                    }
                    item.setAttribute('data-is-read', '0');
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
                setTimeout(function () {
                    reconnectInterval = Math.min(60000, reconnectInterval * 2);
                    connect();
                }, reconnectInterval);
            };
        }

        connect();

        const notiList = document.getElementById('notiList');
        if (notiList) {
            notiList.addEventListener('click', function (e) {
                const target = e.target.closest('.noti-item');
                if (!target)
                    return;
                const notiId = target.getAttribute('data-noti-id');
                if (!notiId)
                    return;

                fetch(contextPath + '/notifications/mark-read', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: 'notificationId=' + encodeURIComponent(notiId)
                }).then(function () {
                    const isRead = target.getAttribute('data-is-read');
                    if (isRead !== '1') {
                        target.setAttribute('data-is-read', '1');
                        target.classList.remove('text-gray-900', 'font-weight-bold');
                        target.classList.add('text-gray-500');
                        if (unreadCount > 0) {
                            unreadCount -= 1;
                            updateBadge();
                        }
                    }
                }).catch(function (err) {
                    console.error('Mark read failed:', err);
                });
            });
        }
    } else {
        console.log("No user in session - WebSocket notifications disabled");
    }
</script>
