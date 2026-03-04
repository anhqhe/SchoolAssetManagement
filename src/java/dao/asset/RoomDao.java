/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.asset;

import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
