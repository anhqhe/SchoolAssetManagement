package dao;

import model.User;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Xác thực username/password (plain text theo seed data)
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT UserId, Username, FullName, IsActive FROM Users WHERE Username = ? AND PasswordHash = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // nếu dùng hash, truyền hash vào đây

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean isActive = rs.getBoolean("IsActive");
                    if (!isActive) return null;

                    User user = new User();
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));

                    // load roles
                    user.setRoles(getRolesByUserId(conn, user.getUserId()));
                    return user;
                } else {
                    return null;
                }
            }
        }
    }

    private List<String> getRolesByUserId(Connection conn, long userId) throws SQLException {
        String sql = "SELECT r.RoleCode FROM UserRoles ur JOIN Roles r ON ur.RoleId = r.RoleId WHERE ur.UserId = ?";
        List<String> roles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("RoleCode"));
                }
            }
        }
        return roles;
    }

    // Optional: lấy user theo username để kiểm tra tồn tại (đăng ký ...)
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT UserId, Username, FullName, IsActive FROM Users WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));
                    user.setActive(rs.getBoolean("IsActive"));
                    user.setRoles(getRolesByUserId(conn, user.getUserId()));
                    return user;
                }
                return null;
            }
        }
    }
    public boolean checkOldPassword(long userId, String oldPassword) {
        String sql = "SELECT 1 FROM Users WHERE UserId=? AND PasswordHash=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, oldPassword);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(long userId, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash=? WHERE UserId=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Long> getIdsByRole(String staff) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
