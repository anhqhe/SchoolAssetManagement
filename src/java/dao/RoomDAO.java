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
import util.DBUtil;

/**
 *
 * @author ASUS
 */
public class RoomDAO {
    
    
    public List<Room> getAllRooms() throws SQLException {
    List<Room> list = new ArrayList<>();
    // Lấy danh sách phòng; Trưởng phòng lấy từ TeacherRoomAssignments (giáo viên được gán IsPrimary=1)
    String sql = "SELECT r.RoomId, r.RoomName, r.Location, u.FullName AS HeadTeacherName "
            + "FROM Rooms r "
            + "LEFT JOIN TeacherRoomAssignments tra ON tra.RoomId = r.RoomId AND tra.IsPrimary = 1 "
            + "LEFT JOIN Users u ON tra.TeacherId = u.UserId "
            + "ORDER BY r.RoomName ASC";
    
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Room r = new Room();
            r.setRoomId(rs.getInt("RoomId"));
            r.setRoomName(rs.getString("RoomName"));
            r.setLocation(rs.getString("Location"));
            r.setHeadTeacherName(rs.getString("HeadTeacherName"));
            list.add(r);
        }
    }
    return list;
}
}
