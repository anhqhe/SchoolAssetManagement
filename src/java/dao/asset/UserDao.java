/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.asset;

import model.asset.Teacher;
import java.util.ArrayList;
import java.util.List;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author An
 */
public class UserDao {

    public boolean exists(long userId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE UserId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    //Only Teacher role active for dropdown
    public List<Teacher> findAllTeachers() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT u.UserId, u.FullName "
                + "FROM Users u "
                + "JOIN UserRoles ur ON ur.UserId = u.UserId "
                + "JOIN Roles r ON ur.RoleId = r.RoleId "
                + "WHERE r.RoleCode = 'TEACHER' AND u.IsActive = 1 "
                + "ORDER BY u.FullName";
        try(Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Teacher t = new Teacher();
                t.setUserId(rs.getLong("UserId"));
                t.setFullName(rs.getNString("FullName"));
                list.add(t);
            }
        }
        return list;
    }

}
