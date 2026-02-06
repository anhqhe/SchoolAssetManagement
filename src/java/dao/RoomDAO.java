package dao;

import model.Room;
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
}

