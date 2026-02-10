/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.allocation.Notification;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class NotificationDAO {

    //not use
    public boolean insertNotification(Notification noti) throws SQLException {
        String sql = """
                     INSERT INTO Notifications (ReceiverId, Title, Content, RefType, RefId, IsRead, CreatedAt)
                     VALUES (?, ?, ?, ?, ?, 0, SYSDATETIME())
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, noti.getReceiverId());
            ps.setString(2, noti.getTitle());
            ps.setString(3, noti.getContent());
            String refType = noti.getRefType();
            if (refType == null || refType.isBlank()) {
                refType = "SYSTEM";
            }
            ps.setString(4, refType);
            ps.setLong(5, noti.getRefId());

            return ps.executeUpdate() > 0;
        }
    }

    public long insert(Notification noti) {
        String sql = """
                     INSERT INTO Notifications (ReceiverId, Title, Content, RefType, RefId, IsRead, CreatedAt)
                     OUTPUT INSERTED.NotificationId
                     VALUES (?, ?, ?, ?, ?, 0, SYSDATETIME())
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, noti.getReceiverId());
            ps.setString(2, noti.getTitle());
            ps.setString(3, noti.getContent());
            String refType = noti.getRefType();
            if (refType == null || refType.isBlank()) {
                refType = "SYSTEM";
            }
            ps.setString(4, refType);
            ps.setLong(5, noti.getRefId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch(SQLException e) {
            System.out.println("dao.allocation.NotificationDAO.insert()");
            System.out.println(e);
        } catch(Exception e) {
            System.out.println("dao.allocation.NotificationDAO.insert()");
            System.out.println(e);
        }
        return -1;
    }
    
    public List<Notification> getTop10ByUserId(long userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = """
                 SELECT TOP 10 * FROM Notifications 
                 WHERE ReceiverId = ? 
                 ORDER BY CreatedAt DESC
                 """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification noti = new Notification();
                    noti.setNotificationId(rs.getLong("NotificationId"));
                    noti.setReceiverId(rs.getLong("ReceiverId"));
                    noti.setTitle(rs.getString("Title"));
                    noti.setContent(rs.getString("Content"));
                    noti.setRefType(rs.getString("RefType"));
                    noti.setRefId(rs.getLong("RefId"));
                    noti.setRead(rs.getBoolean("IsRead"));
                    noti.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());

                    list.add(noti);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Notification> getUnreadByUserId(long userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = """
                 SELECT TOP 10 * FROM Notifications 
                 WHERE ReceiverId = ? AND IsRead = 0 ORDER BY CreatedAt DESC
                 """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification noti = new Notification();
                    noti.setNotificationId(rs.getLong("NotificationId"));
                    noti.setReceiverId(rs.getLong("ReceiverId"));
                    noti.setTitle(rs.getString("Title"));
                    noti.setContent(rs.getString("Content"));
                    noti.setRefType(rs.getString("RefType"));
                    noti.setRefId(rs.getLong("RefId"));
                    noti.setRead(rs.getBoolean("IsRead"));
                    noti.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());

                    list.add(noti);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean markAsRead(long notificationId) throws SQLException {
        String sql = """
                 UPDATE Notifications
                 SET IsRead = 1
                 WHERE NotificationId = ? AND IsRead = 0
                 """;
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            return ps.executeUpdate() > 0;
        }
    }
}
