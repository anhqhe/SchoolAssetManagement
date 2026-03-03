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
    // Truy vấn lấy danh sách phòng từ bảng Rooms theo thiết kế [4]
    String sql = "SELECT RoomId, RoomName FROM Rooms ORDER BY RoomName ASC";
    
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Room r = new Room();
            r.setRoomId(rs.getInt("RoomId"));
            r.setRoomName(rs.getString("RoomName"));
            list.add(r);
        }
    }
    return list;
}
}
