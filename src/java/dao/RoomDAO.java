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
        String sql = "SELECT RoomId, RoomName, Location FROM Rooms ORDER BY RoomName";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getLong("RoomId"));
                room.setRoomName(rs.getString("RoomName"));
                room.setLocation(rs.getString("Location"));
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
     * Lấy danh sách giáo viên đang được gán vào một phòng.
     * Dữ liệu lấy từ bảng TeacherRoomAssignments (TeacherId, RoomId) join với Users.
     */
    public List<User> getTeachersByRoomId(long roomId) throws SQLException {
        List<User> teachers = new ArrayList<>();

        String sql = "SELECT u.UserId, u.Username, u.FullName, u.Email, u.IsActive " +
                     "FROM TeacherRoomAssignments tra " +
                     "JOIN Users u ON tra.TeacherId = u.UserId " +
                     "JOIN UserRoles ur ON ur.UserId = u.UserId " +
                     "JOIN Roles r ON ur.RoleId = r.RoleId " +
                     "WHERE tra.RoomId = ? AND r.RoleCode = 'TEACHER' " +
                     "ORDER BY u.FullName";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User teacher = new User();
                    teacher.setUserId(rs.getLong("UserId"));
                    teacher.setUsername(rs.getString("Username"));
                    teacher.setFullName(rs.getString("FullName"));
                    teacher.setEmail(rs.getString("Email"));
                    teacher.setActive(rs.getBoolean("IsActive"));
                    teachers.add(teacher);
                }
            }
        }

        return teachers;
    }
}
