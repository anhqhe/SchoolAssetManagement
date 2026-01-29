/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.util.List;
import model.Allocation.Room;
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
