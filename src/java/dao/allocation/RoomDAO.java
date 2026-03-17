/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.util.List;
import model.allocation.Room;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getLong("RoomId"));
                room.setRoomCode(rs.getNString("RoomCode"));
                room.setRoomName(rs.getNString("RoomName"));
                room.setLocation(rs.getNString("Location"));
                room.setIsActive(rs.getBoolean("IsActive"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Error: dao.allocation.RoomDAO.getAllRooms()- "+e.getMessage());
        }
        return rooms;
    }

    public boolean insertRoom(Room room) {
        String sql = "INSERT INTO Rooms (RoomCode, RoomName, Location, IsActive) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setNString(1, room.getRoomCode());
            ps.setNString(2, room.getRoomName());
            ps.setNString(3, room.getLocation());
            ps.setBoolean(4, room.isIsActive());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error: dao.allocation.RoomDAO.insertRoom()- "+e.getMessage());
            return false;
        }
    }

    public List<Room> getAllActiveRooms() {
        List<Room> activeRooms = new ArrayList<>();
        String sql = "SELECT RoomId, RoomCode, RoomName, Location, IsActive "
                + "FROM Rooms WHERE IsActive = 1";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getLong("RoomId"));
                room.setRoomCode(rs.getNString("RoomCode"));
                room.setRoomName(rs.getNString("RoomName"));
                room.setLocation(rs.getNString("Location"));
                room.setIsActive(rs.getBoolean("IsActive"));

                activeRooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error: RoomDAO.getAllActiveRoom -" + e.getMessage());
        }
        return activeRooms;
    }

    public List<Room> getActiveRoomsByTeacherId(long teacherId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.RoomId, r.RoomCode, r.RoomName, r.Location, r.IsActive "
                + "FROM Rooms r "
                + "JOIN TeacherRoomAssignments tra ON tra.RoomId = r.RoomId "
                + "WHERE r.IsActive = 1 AND tra.TeacherId = ? "
                + "ORDER BY tra.IsPrimary DESC, r.RoomName";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomId(rs.getLong("RoomId"));
                    room.setRoomCode(rs.getNString("RoomCode"));
                    room.setRoomName(rs.getNString("RoomName"));
                    room.setLocation(rs.getNString("Location"));
                    room.setIsActive(rs.getBoolean("IsActive"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: RoomDAO.getActiveRoomsByTeacherId - " + e.getMessage());
        }
        return rooms;
    }

    public boolean isTeacherAssignedToRoom(long teacherId, long roomId) {
        String sql = "SELECT 1 "
                + "FROM TeacherRoomAssignments tra "
                + "JOIN Rooms r ON r.RoomId = tra.RoomId "
                + "WHERE tra.TeacherId = ? AND tra.RoomId = ? AND r.IsActive = 1";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ps.setLong(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error: RoomDAO.isTeacherAssignedToRoom - " + e.getMessage());
            return false;
        }
    }

    public Room getRoomById(long id) {
        Room room = null;
        String sql = "SELECT RoomId, RoomCode, RoomName, Location, IsActive "
                + "FROM Rooms WHERE RoomId = ?";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    room = new Room();
                    room.setRoomId(rs.getLong("RoomId"));
                    room.setRoomCode(rs.getNString("RoomCode"));
                    room.setRoomName(rs.getNString("RoomName"));
                    room.setLocation(rs.getNString("Location"));
                    room.setIsActive(rs.getBoolean("IsActive"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: dao.allocation.RoomDAO.getRoomById()- " + e.getMessage());
        }
        return room;
    }

}
