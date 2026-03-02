package dao;

import model.Room;
import model.User;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();

        String sql =
                "SELECT r.RoomId, r.RoomName, r.Location, " +
                "       u.FullName AS HeadTeacherName " +
                "FROM Rooms r " +
                "LEFT JOIN TeacherRoomAssignments tra " +
                "       ON tra.RoomId = r.RoomId AND tra.IsPrimary = 1 " +
                "LEFT JOIN Users u ON tra.TeacherId = u.UserId " +
                "ORDER BY r.RoomName";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getLong("RoomId"));
                room.setRoomName(rs.getString("RoomName"));
                room.setLocation(rs.getString("Location"));
                room.setHeadTeacherName(rs.getString("HeadTeacherName"));
                rooms.add(room);
            }
        }

        return rooms;
    }

    public Room getRoomById(long roomId) throws SQLException {
        String sql = "SELECT RoomId, RoomName, Location FROM Rooms WHERE RoomId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getLong("RoomId"));
                    room.setRoomName(rs.getString("RoomName"));
                    room.setLocation(rs.getString("Location"));
                    return room;
                }
            }
        }

        return null;
    }

    public boolean updateRoom(Room room) throws SQLException {
        String sql = "UPDATE Rooms SET RoomName = ?, Location = ? WHERE RoomId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomName());
            ps.setString(2, room.getLocation());
            ps.setLong(3, room.getRoomId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lấy trưởng phòng (giáo viên phụ trách chính) của một phòng.
     * Chọn giáo viên có IsPrimary = 1 trong TeacherRoomAssignments.
     */
    public User getRoomHeadByRoomId(long roomId) throws SQLException {
        String sql = "SELECT TOP 1 u.UserId, u.Username, u.FullName, u.Email, u.IsActive " +
                     "FROM TeacherRoomAssignments tra " +
                     "JOIN Users u ON tra.TeacherId = u.UserId " +
                     "JOIN UserRoles ur ON ur.UserId = u.UserId " +
                     "JOIN Roles r ON ur.RoleId = r.RoleId " +
                     "WHERE tra.RoomId = ? AND tra.IsPrimary = 1 AND r.RoleCode = 'TEACHER' " +
                     "ORDER BY u.FullName";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User teacher = new User();
                    teacher.setUserId(rs.getLong("UserId"));
                    teacher.setUsername(rs.getString("Username"));
                    teacher.setFullName(rs.getString("FullName"));
                    teacher.setEmail(rs.getString("Email"));
                    teacher.setActive(rs.getBoolean("IsActive"));
                    return teacher;
                }
            }
        }

        return null;
    }

    /**
     * Cập nhật trưởng phòng cho một phòng.
     * Xoá mọi gán cũ của phòng, sau đó (nếu teacherId != null) thêm bản ghi mới với IsPrimary = 1.
     */
    public void setRoomHead(long roomId, Long teacherId) throws SQLException {
        String deleteSql = "DELETE FROM TeacherRoomAssignments WHERE RoomId = ?";
        String insertSql = "INSERT INTO TeacherRoomAssignments (TeacherId, RoomId, IsPrimary) VALUES (?, ?, 1)";

        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setLong(1, roomId);
                del.executeUpdate();
            }

            if (teacherId != null) {
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    ins.setLong(1, teacherId);
                    ins.setLong(2, roomId);
                    ins.executeUpdate();
                }
            }
        }
    }
}
