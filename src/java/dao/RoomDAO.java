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
import model.User;
import util.DBUtil;

/**
 *
 * @author ASUS
 */
public class RoomDAO {
    
    
    public List<Room> getAllRooms() throws SQLException {
    List<Room> list = new ArrayList<>();
    /*
     * Lấy danh sách phòng để hiển thị cho trang quản trị.
     * Ưu tiên join để lấy tên "trưởng phòng" (TeacherRoomAssignments.IsPrimary=1) nếu hệ DB có bảng này.
     *
     * Lưu ý về thiết kế:
     * - Đoạn fallback hiện dựa vào SQLException, nên có thể che mất lỗi DB thật (timeout/permission/syntax).
     * - Nếu cần "fallback khi thiếu bảng", nên bắt theo lỗi cụ thể của SQL Server thay vì bắt mọi SQLException.
     */
    String sqlWithHead = "SELECT r.RoomId, r.RoomName, r.Location, u.FullName AS HeadTeacherName "
            + "FROM Rooms r "
            + "LEFT JOIN TeacherRoomAssignments tra ON tra.RoomId = r.RoomId AND tra.IsPrimary = 1 "
            + "LEFT JOIN Users u ON tra.TeacherId = u.UserId "
            + "ORDER BY r.RoomName ASC";

    String sqlRoomsOnly = "SELECT r.RoomId, r.RoomName, r.Location "
            + "FROM Rooms r ORDER BY r.RoomName ASC";

    try (Connection conn = DBUtil.getConnection()) {
        try (PreparedStatement ps = conn.prepareStatement(sqlWithHead);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("RoomId"));
                r.setRoomName(rs.getString("RoomName"));
                r.setLocation(rs.getString("Location"));
                r.setHeadTeacherName(rs.getString("HeadTeacherName"));
                list.add(r);
            }
        } catch (SQLException ex) {
            // Fallback: không join trưởng phòng (chỉ lấy bảng Rooms)
            try (PreparedStatement ps = conn.prepareStatement(sqlRoomsOnly);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("RoomId"));
                    r.setRoomName(rs.getString("RoomName"));
                    r.setLocation(rs.getString("Location"));
                    r.setHeadTeacherName(null);
                    list.add(r);
                }
            }
        }
    }
    return list;
}

    public Room getRoomById(long roomId) throws SQLException {
        // Lấy thông tin cơ bản của phòng và tên giáo viên phụ trách (dùng PreparedStatement để tránh SQL injection)
        String sql = "SELECT r.RoomId, r.RoomName, r.Location, u.FullName AS HeadTeacherName "
                   + "FROM Rooms r "
                   + "LEFT JOIN TeacherRoomAssignments tra ON r.RoomId = tra.RoomId AND tra.IsPrimary = 1 "
                   + "LEFT JOIN Users u ON tra.TeacherId = u.UserId "
                   + "WHERE r.RoomId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Room r = new Room();
                r.setRoomId(rs.getInt("RoomId"));
                r.setRoomName(rs.getString("RoomName"));
                r.setLocation(rs.getString("Location"));
                r.setHeadTeacherName(rs.getString("HeadTeacherName"));
                return r;
            }
        }
    }

    public User getPrimaryTeacherByRoomId(long roomId) throws SQLException {
        // Lấy giáo viên được gán làm trưởng phòng (IsPrimary=1). TOP 1 để phòng trường hợp dữ liệu bị trùng.
        String sql = "SELECT TOP 1 u.UserId, u.Username, u.FullName, u.Email, u.Phone, u.IsActive "
                + "FROM TeacherRoomAssignments tra "
                + "JOIN Users u ON tra.TeacherId = u.UserId "
                + "WHERE tra.RoomId = ? AND tra.IsPrimary = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setUserId(rs.getLong("UserId"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setPhone(rs.getString("Phone"));
                u.setActive(rs.getBoolean("IsActive"));
                return u;
            }
        }
    }

    public boolean updateRoomBasic(long roomId, String roomName, String location) throws SQLException {
        // Cập nhật tên + vị trí. Validate business (độ dài, ký tự...) nên được làm ở tầng servlet/service.
        String sql = "UPDATE Rooms SET RoomName = ?, Location = ? WHERE RoomId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomName);
            ps.setString(2, location);
            ps.setLong(3, roomId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Kiểm kê tổng quát: trả về số lượng tài sản (IsActive=1) cho từng phòng.
     * Key = RoomId, Value = totalCount
     */
    public java.util.Map<Integer, Integer> getAssetCountPerRoom() throws SQLException {
        java.util.Map<Integer, Integer> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT r.RoomId, r.RoomName, COUNT(a.AssetId) AS TotalAssets "
                   + "FROM Rooms r "
                   + "LEFT JOIN Assets a ON a.CurrentRoomId = r.RoomId AND a.IsActive = 1 "
                   + "GROUP BY r.RoomId, r.RoomName "
                   + "ORDER BY r.RoomName ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("RoomId"), rs.getInt("TotalAssets"));
            }
        }
        return map;
    }

    /**
     * Đếm số tài sản chưa được phân bổ vào phòng nào (CurrentRoomId IS NULL).
     */
    public int getUnassignedAssetCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Assets WHERE CurrentRoomId IS NULL AND IsActive = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Kiểm kê chi tiết theo danh mục cho 1 phòng.
     * Trả về List map {categoryName, count} sắp xếp theo categoryName.
     */
    public List<Object[]> getAssetCountByCategoryForRoom(long roomId) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT c.CategoryName, COUNT(a.AssetId) AS Cnt "
                   + "FROM Assets a "
                   + "JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                   + "WHERE a.CurrentRoomId = ? AND a.IsActive = 1 "
                   + "GROUP BY c.CategoryName "
                   + "ORDER BY c.CategoryName ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{rs.getString("CategoryName"), rs.getInt("Cnt")});
                }
            }
        }
        return result;
    }

    public void setPrimaryTeacherForRoom(long roomId, Long teacherId) throws SQLException {
        /*
         * Gán (hoặc huỷ gán) trưởng phòng:
         * - teacherId == null: huỷ gán -> set IsPrimary=0 cho tất cả assignment của phòng
         * - teacherId != null: đảm bảo chỉ có 1 bản ghi IsPrimary=1 cho phòng này
         *
         * Dùng transaction để tránh trạng thái dở dang nếu có lỗi giữa chừng.
         */
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // clear current primary
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE TeacherRoomAssignments SET IsPrimary = 0 WHERE RoomId = ?")) {
                    ps.setLong(1, roomId);
                    ps.executeUpdate();
                }

                if (teacherId != null) {
                    // if exists, set IsPrimary=1
                    int updated;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE TeacherRoomAssignments SET IsPrimary = 1 WHERE RoomId = ? AND TeacherId = ?")) {
                        ps.setLong(1, roomId);
                        ps.setLong(2, teacherId);
                        updated = ps.executeUpdate();
                    }

                    if (updated == 0) {
                        // insert new assignment as primary
                        try (PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO TeacherRoomAssignments (TeacherId, RoomId, IsPrimary) VALUES (?, ?, 1)")) {
                            ps.setLong(1, teacherId);
                            ps.setLong(2, roomId);
                            ps.executeUpdate();
                        }
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Lấy danh sách các phòng mà giáo viên được phân công làm trưởng phòng (IsPrimary = 1)
     */
    public List<Room> getRoomsByTeacherId(long teacherId) throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.RoomId, r.RoomName, r.Location "
                   + "FROM Rooms r "
                   + "JOIN TeacherRoomAssignments tra ON r.RoomId = tra.RoomId "
                   + "WHERE tra.TeacherId = ? AND tra.IsPrimary = 1 "
                   + "ORDER BY r.RoomName ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("RoomId"));
                    r.setRoomName(rs.getString("RoomName"));
                    r.setLocation(rs.getString("Location"));
                    list.add(r);
                }
            }
        }
        return list;
    }
}
