/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Room;
import model.User;
import util.DBUtil;

/**
 *
 * @author ASUS
 */
public class RoomDAO {
    
    
    public List<Room> getAllRooms() throws SQLException {
    List<Room> list = new ArrayList<>();
    // Ưu tiên lấy trưởng phòng qua TeacherRoomAssignments (IsPrimary=1).
    // Nếu DB chưa có bảng này (hoặc khác schema), fallback về query chỉ lấy Rooms
    // để trang /rooms vẫn hiển thị được danh sách phòng.
    String sqlWithHead = "SELECT r.RoomId, r.RoomName, r.Location, u.FullName AS HeadTeacherName "
            + "FROM Rooms r "
            + "LEFT JOIN TeacherRoomAssignments tra ON tra.RoomId = r.RoomId AND tra.IsPrimary = 1 "
            + "LEFT JOIN Users u ON tra.TeacherId = u.UserId "
            + "ORDER BY r.RoomName ASC";

    String sqlRoomsOnly = "SELECT r.RoomId, r.RoomName, r.Location "
            + "FROM Rooms r ORDER BY r.RoomName ASC";

    try (Connection conn = DBUtil.getConnection()) {
        try (PreparedStatement ps = conn.prepareStatement(sqlWithHead);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("RoomId"));
                r.setRoomName(rs.getString("RoomName"));
                r.setLocation(rs.getString("Location"));
                r.setHeadTeacherName(rs.getString("HeadTeacherName"));
                list.add(r);
            }
        } catch (SQLException ex) {
            // fallback: không join trưởng phòng
            try (PreparedStatement ps = conn.prepareStatement(sqlRoomsOnly);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("RoomId"));
                    r.setRoomName(rs.getString("RoomName"));
                    r.setLocation(rs.getString("Location"));
                    r.setHeadTeacherName(null);
                    list.add(r);
                }
            }
        }
    }
    return list;
}

    public Room getRoomById(long roomId) throws SQLException {
        String sql = "SELECT RoomId, RoomName, Location FROM Rooms WHERE RoomId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Room r = new Room();
                r.setRoomId(rs.getInt("RoomId"));
                r.setRoomName(rs.getString("RoomName"));
                r.setLocation(rs.getString("Location"));
                return r;
            }
        }
    }

    public User getPrimaryTeacherByRoomId(long roomId) throws SQLException {
        String sql = "SELECT TOP 1 u.UserId, u.Username, u.FullName, u.Email, u.Phone, u.IsActive "
                + "FROM TeacherRoomAssignments tra "
                + "JOIN Users u ON tra.TeacherId = u.UserId "
                + "WHERE tra.RoomId = ? AND tra.IsPrimary = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setUserId(rs.getLong("UserId"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setPhone(rs.getString("Phone"));
                u.setActive(rs.getBoolean("IsActive"));
                return u;
            }
        }
    }

    public boolean updateRoomBasic(long roomId, String roomName, String location) throws SQLException {
        String sql = "UPDATE Rooms SET RoomName = ?, Location = ? WHERE RoomId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomName);
            ps.setString(2, location);
            ps.setLong(3, roomId);
            return ps.executeUpdate() > 0;
        }
    }

    public void setPrimaryTeacherForRoom(long roomId, Long teacherId) throws SQLException {
        // Nếu teacherId null: bỏ gán trưởng phòng (set hết IsPrimary = 0)
        // Nếu teacherId != null: đảm bảo chỉ có 1 bản ghi IsPrimary=1 cho room này
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // clear current primary
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE TeacherRoomAssignments SET IsPrimary = 0 WHERE RoomId = ?")) {
                    ps.setLong(1, roomId);
                    ps.executeUpdate();
                }

                if (teacherId != null) {
                    // if exists, set IsPrimary=1
                    int updated;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE TeacherRoomAssignments SET IsPrimary = 1 WHERE RoomId = ? AND TeacherId = ?")) {
                        ps.setLong(1, roomId);
                        ps.setLong(2, teacherId);
                        updated = ps.executeUpdate();
                    }

                    if (updated == 0) {
                        // insert new assignment as primary
                        try (PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO TeacherRoomAssignments (TeacherId, RoomId, IsPrimary) VALUES (?, ?, 1)")) {
                            ps.setLong(1, teacherId);
                            ps.setLong(2, roomId);
                            ps.executeUpdate();
                        }
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
