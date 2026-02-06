/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.util.ArrayList;
import java.util.List;
import util.DBUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

/**
 *
 * @author Leo
 */
public class UserDAO {

    public List<Long> getIdsByRole(String roleCode) {
        List<Long> userIds = new ArrayList<>();
        String sql = "SELECT ur.UserId "
                + "FROM UserRoles ur "
                + "JOIN Roles r ON ur.RoleId = r.RoleId "
                + "WHERE r.RoleCode = ?";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setString(1, roleCode); // 'BOARD', 'ASSET_STAFF', 'TEACHER'

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getLong("UserId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    public User getByUserId(long userId) {
        User user = null;
        String sql = "SELECT UserId, Username, FullName, Email, IsActive "
                + "FROM Users WHERE UserId = ?";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setActive(rs.getBoolean("IsActive"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
