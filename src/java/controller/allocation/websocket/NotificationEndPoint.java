package controller.allocation.websocket;

import dao.allocation.NotificationDAO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import model.allocation.Notification;

@ServerEndpoint("/notifications/{userId}")
public class NotificationEndPoint {

    // Thread-safe map: UserId -> Tập hợp các Session của User đó
    private static final ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") long userId) {
        
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
    public static void sendToUser(long userId, String message) {
        sendToUser(userId, "Thông báo", message, "SYSTEM", 0);
    }

    public static void sendToUser(long userId, String title, String content, String refType, long refId) {
        insertNotification(userId, title, content, refType, refId);
        CopyOnWriteArraySet<Session> sessions = userSessions.get(userId);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    s.getAsyncRemote().sendText(content);
                }
            }
        }
    }

    public static void sendToUsers(List<Long> userIds, String message) {
        for (Long id : userIds) {
            sendToUser(id, message);
        }
    }

    public static void sendToUsers(List<Long> userIds, String title, String content, String refType, long refId) {
        for (Long id : userIds) {
            sendToUser(id, title, content, refType, refId);
        }
    }

    private static void insertNotification(long userId, String title, String content, String refType, long refId) {
        try {
            Notification noti = new Notification();
            noti.setReceiverId(userId);
            noti.setTitle(title);
            noti.setContent(content);
            noti.setRefType(refType);
            noti.setRefId(refId);

            new NotificationDAO().insertNotification(noti);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
