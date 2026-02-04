<div id="notification-toast" class="toast" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
    <div class="toast-header bg-primary text-white">
        <strong class="mr-auto">Thông báo m?i</strong>
        <button type="button" class="ml-2 mb-1 close" data-dismiss="toast">&times;</button>
    </div>
    <div class="toast-body" id="noti-message"></div>
</div>

<script>
    const userId = "${sessionScope.user.userId}";
    if (userId) {
        const socket = new WebSocket(`ws://localhost:8080/${pageContext.request.contextPath}/notifications/${userId}`);
        console.log("Connecting to WebSocket");

        
        socket.onopen = function () {
            console.log("WebSocket connected");
            reconnectInterval = 3000; // reset on successful connect
        };

        socket.onmessage = function (event) {
            // show noti by Toast
            document.getElementById('noti-message').innerText = event.data;
            $('.toast').toast({delay: 5000});
            $('.toast').toast('show');

            // thêm sound
            // new Audio('notify.mp3').play();
        };

        socket.onclose = function () {
            console.log("WebSocket closed. Attempting to reconnect...");
        };
    }
</script>