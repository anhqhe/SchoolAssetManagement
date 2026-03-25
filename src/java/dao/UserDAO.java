package dao;

import model.User;
import util.DBUtil;
import util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Xác thực username/password (plain text theo seed data)
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT UserId, Username, FullName, Email, Phone, IsActive FROM Users WHERE Username = ? AND PasswordHash = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, HashUtil.hashPassword(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean isActive = rs.getBoolean("IsActive");

                    User user = new User();
                    user.setActive(isActive);
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));

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
        String sql = "SELECT UserId, Username, FullName, Email, Phone, IsActive FROM Users WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));
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
            ps.setString(2, HashUtil.hashPassword(oldPassword));
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

            ps.setString(1, HashUtil.hashPassword(newPassword));
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ========== FORGOT PASSWORD METHODS ==========
    
    /**
     * Tìm user theo email
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT UserId, Username, FullName, Email, IsActive FROM Users WHERE Email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setActive(rs.getBoolean("IsActive"));
                    user.setRoles(getRolesByUserId(conn, user.getUserId()));
                    return user;
                }
                return null;
            }
        }
    }

    /**
     * Lưu reset token và thời gian hết hạn vào database
     */
    public boolean saveResetToken(long userId, String resetToken, Timestamp expiryTime) {
        String sql = "UPDATE Users SET ResetToken=?, ResetTokenExpiry=? WHERE UserId=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, resetToken);
            ps.setTimestamp(2, expiryTime);
            ps.setLong(3, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm user theo reset token và kiểm tra xem token còn hợp lệ không
     */
    public User findByResetToken(String resetToken) throws SQLException {
        String sql = "SELECT UserId, Username, FullName, Email, IsActive, ResetToken, ResetTokenExpiry " +
                     "FROM Users WHERE ResetToken = ? AND ResetTokenExpiry > GETDATE()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resetToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("UserId"));
                    user.setUsername(rs.getString("Username"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setActive(rs.getBoolean("IsActive"));
                    user.setResetToken(rs.getString("ResetToken"));
                    user.setResetTokenExpiry(rs.getTimestamp("ResetTokenExpiry"));
                    user.setRoles(getRolesByUserId(conn, user.getUserId()));
                    return user;
                }
                return null;
            }
        }
    }

    /**
     * Reset password và xóa reset token
     */
    public boolean resetPassword(long userId, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash=?, ResetToken=NULL, ResetTokenExpiry=NULL WHERE UserId=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, HashUtil.hashPassword(newPassword));
            ps.setLong(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa reset token (sử dụng khi user cancel hoặc token expired)
     */
    public boolean clearResetToken(long userId) {
        String sql = "UPDATE Users SET ResetToken=NULL, ResetTokenExpiry=NULL WHERE UserId=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách tất cả giáo viên (role TEACHER) đang active.
     */
    public List<User> getAllTeachers() throws SQLException {
        List<User> teachers = new ArrayList<>();

        String sql = "SELECT u.UserId, u.Username, u.FullName, u.Email, u.IsActive " +
                     "FROM Users u " +
                     "JOIN UserRoles ur ON ur.UserId = u.UserId " +
                     "JOIN Roles r ON ur.RoleId = r.RoleId " +
                     "WHERE r.RoleCode = 'TEACHER' AND u.IsActive = 1 " +
                     "ORDER BY u.FullName";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User teacher = new User();
                teacher.setUserId(rs.getLong("UserId"));
                teacher.setUsername(rs.getString("Username"));
                teacher.setFullName(rs.getString("FullName"));
                teacher.setEmail(rs.getString("Email"));
                teacher.setActive(rs.getBoolean("IsActive"));
                teachers.add(teacher);
            }
        }

        return teachers;
    }

    public User findById(long userId) throws SQLException {
        String sql = "SELECT UserId, Username, FullName, Email, Phone, IsActive FROM Users WHERE UserId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User user = new User();
                user.setUserId(rs.getLong("UserId"));
                user.setUsername(rs.getString("Username"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPhone(rs.getString("Phone"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setRoles(getRolesByUserId(conn, user.getUserId()));
                return user;
            }
        }
    }

    public boolean updateProfile(long userId, String fullName, String email, String phone) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, Phone = ? WHERE UserId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setLong(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isPhoneTaken(String phone, long excludeUserId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE Phone = ? AND UserId <> ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setLong(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==================== ADMIN USER MANAGEMENT ====================

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT UserId, Username, FullName, Email, Phone, IsActive FROM Users ORDER BY UserId ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getLong("UserId"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setPhone(rs.getString("Phone"));
                u.setActive(rs.getBoolean("IsActive"));
                list.add(u);
            }
            for (User u : list) {
                u.setRoles(getRolesByUserId(conn, u.getUserId()));
            }
        }
        return list;
    }

    public List<String> getAllRoleCodes() throws SQLException {
        List<String> codes = new ArrayList<>();
        String sql = "SELECT RoleCode FROM Roles ORDER BY RoleId";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                codes.add(rs.getString("RoleCode"));
            }
        }
        return codes;
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isUsernameTaken(String username, long excludeUserId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE Username = ? AND UserId <> ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setLong(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isEmailTaken(String email, long excludeUserId) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE Email = ? AND UserId <> ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setLong(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public long createUser(String username, String password, String fullName,
                           String email, String phone, boolean active,
                           List<String> roleCodes) throws SQLException {
        String insertSql = "INSERT INTO Users (Username, PasswordHash, FullName, Email, Phone, IsActive) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long newId;
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, HashUtil.hashPassword(password));
                    ps.setString(3, fullName);
                    ps.setString(4, email);
                    ps.setString(5, phone);
                    ps.setBoolean(6, active);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Insert user failed");
                        newId = keys.getLong(1);
                    }
                }

                if (roleCodes != null && !roleCodes.isEmpty()) {
                    for (String roleCode : roleCodes) {
                        if (roleCode == null || roleCode.trim().isEmpty()) continue;
                        String roleSql = "INSERT INTO UserRoles (UserId, RoleId) "
                                       + "SELECT ?, RoleId FROM Roles WHERE RoleCode = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(roleSql)) {
                            ps2.setLong(1, newId);
                            ps2.setString(2, roleCode.trim());
                            ps2.executeUpdate();
                        }
                    }
                }

                conn.commit();
                return newId;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean setUserActive(long userId, boolean active) throws SQLException {
        String sql = "UPDATE Users SET IsActive = ? WHERE UserId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUserAdmin(long userId, String username, String fullName, String email, String phone) throws SQLException {
        String sql = "UPDATE Users SET Username = ?, FullName = ?, Email = ?, Phone = ? WHERE UserId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setLong(5, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
