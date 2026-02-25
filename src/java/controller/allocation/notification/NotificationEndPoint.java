package controller.allocation.notification;

import dao.allocation.NotificationDAO;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import model.User;
import model.allocation.Notification;

@ServerEndpoint(
        value = "/notifications/{userId}",
        configurator = HttpSessionConfigurator.class
)
public class NotificationEndPoint {
    
    private static NotificationDAO notiDAO = new NotificationDAO();

    // Thread-safe map: UserId -> Tập hợp các Session của User đó
    private static final ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") long userId) {
        HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
        User currentUser = null;
        if (httpSession != null) {
            currentUser = (User) httpSession.getAttribute("currentUser");
        }

        if (currentUser == null || currentUser.getUserId() != userId) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
            } catch (Exception e) {
                // ignore close failures
            }
            return;
        }

        System.out.println("WebSocket onOpen: userId=" + userId + ", sessionId=" + session.getId());
        // Nếu User chưa có trong Map, tạo mới một CopyOnWriteArraySet
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") long userId) {
        //
        System.out.println("WebSocket onClose: userId=" + userId + ", sessionId=" + session.getId());
        CopyOnWriteArraySet<Session> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    @OnError
    public void onError(Throwable throwable) {
        // Log lỗi 
    }

    // Send to 1 user (all tab)
    public static void sendToUser(long userId, String title, String content, String refType, long refId) {
        long notiId = insertNotificationAndGetId(userId, title, content, refType, refId);
        CopyOnWriteArraySet<Session> sessions = userSessions.get(userId);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    String payload = "{\"id\":" + notiId + ",\"content\":\"" + escapeJson(content) + "\"}";
                    s.getAsyncRemote().sendText(payload);
                }
            }
        }
    }

    public static void sendToUsers(List<Long> userIds, String title, String content, String refType, long refId) {
        for (Long id : userIds) {
            sendToUser(id, title, content, refType, refId);
        }
    }

    private static long insertNotificationAndGetId(long userId, String title, String content, String refType, long refId) {
        try {
            Notification noti = new Notification();
            noti.setReceiverId(userId);
            noti.setTitle(title);
            noti.setContent(content);
            noti.setRefType(refType);
            noti.setRefId(refId);

            return notiDAO.insert(noti);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
