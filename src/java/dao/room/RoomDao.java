/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.room;

import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.asset.Room;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author An
 */
public class RoomDao {
    public boolean exists(long roomId) throws SQLException{
        String sql = "SELECT 1 FROM Rooms WHERE RoomId = ?";
        try(Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, roomId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }
    
    public List<Room> findAllActive() throws SQLException{
        List<Room> list = new ArrayList<>();
        String sql = "SELECT RoomId, RoomName FROM Rooms WHERE IsActive = 1 ORDER BY RoomName";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Room r = new Room();
                r.setRoomId(rs.getLong("RoomId"));
                r.setRoomName(rs.getString("RoomName"));
                list.add(r);
            }
        } 
        return list;
    }
}
