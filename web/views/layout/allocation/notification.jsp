<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div id="notification-toast" class="toast" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
    <div class="toast-header bg-primary text-white">
        <strong class="mr-auto">Thông báo mới</strong>
        <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
    <div class="toast-body" id="noti-message"></div>
</div>

<script>
    const userId = "${sessionScope.currentUser.userId}";
    if (userId) {
        const protocol = (location.protocol === 'https:') ? 'wss://' : 'ws://';
        const socketUrl = protocol + location.host + '${pageContext.request.contextPath}/notifications/' + userId;
        let socket;
        let reconnectInterval = 3000;

        function connect() {
            console.log("Connecting to WebSocket:", socketUrl);
            socket = new WebSocket(socketUrl);

            socket.onopen = function () {
                console.log("WebSocket connected with userId:", userId);
                reconnectInterval = 3000; // reset on successful connect
            };

            socket.onmessage = function (event) {
                console.log("WebSocket message:", event.data);
                // show noti by Toast
                document.getElementById('noti-message').innerText = event.data;
                $('.toast').toast({delay: 5000});
                $('.toast').toast('show');

                // Sound
                // new Audio('notify.mp3').play();
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
    } else {
        console.log("No user in session - WebSocket notifications disabled");
    }
</script>/body>
</html>
