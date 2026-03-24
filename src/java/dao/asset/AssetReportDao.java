/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.asset;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import model.report.InventoryDetailRow;
import model.report.InventorySummary;
import model.report.UsageDetailRow;
import model.report.UsageSummary;
import util.DBUtil;

public class AssetReportDao {

    // ============================================================
    // ===== HÀM PHỤ: Lấy danh sách tất cả Category (cho dropdown)
    // ============================================================
    public List<String[]> getAllCategories() throws SQLException {
        String sql = "SELECT CategoryId, CategoryName FROM AssetCategories ORDER BY CategoryName";
        List<String[]> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getLong("CategoryId")),
                    rs.getString("CategoryName")
                });
            }
        }
        return list;
    }

    // ============================================================
    // ===== HÀM PHỤ: Lấy danh sách tất cả Room (cho dropdown)
    // ============================================================
    public List<String[]> getAllRooms() throws SQLException {
        String sql = "SELECT RoomId, RoomName FROM Rooms ORDER BY RoomName";
        List<String[]> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getLong("RoomId")),
                    rs.getString("RoomName")
                });
            }
        }
        return list;
    }

    // ============================================================
    // ===== INVENTORY SUMMARY (có filter, search, phân trang)
    // ============================================================
    /**
     * Lấy danh sách tổng hợp inventory, hỗ trợ:
     * - categoryFilter: lọc theo CategoryId (null = tất cả)
     * - search: tìm theo CategoryName (null = không tìm)
     * - page: trang hiện tại (bắt đầu từ 1)
     * - pageSize: số dòng mỗi trang
     */
    public List<InventorySummary> getInventoryByCategoryAndStatus(
            String categoryFilter, String search, int page, int pageSize) throws SQLException {

        // Xây dựng SQL động
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.CategoryId, c.CategoryName, a.Status, COUNT(*) AS TotalAssets ");
        sql.append("FROM Assets a ");
        sql.append("JOIN AssetCategories c ON a.CategoryId = c.CategoryId ");
        sql.append("WHERE a.IsActive = 1 ");

        // Danh sách tham số sẽ truyền vào PreparedStatement
        List<Object> params = new ArrayList<>();

        // Nếu có chọn filter Category
        if (categoryFilter != null && !categoryFilter.trim().isEmpty()) {
            sql.append("AND c.CategoryId = ? ");
            params.add(Long.parseLong(categoryFilter));
        }

        // Nếu có nhập ô tìm kiếm
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND c.CategoryName LIKE ? ");
            params.add("%" + search.trim() + "%");
        }

        sql.append("GROUP BY c.CategoryId, c.CategoryName, a.Status ");
        sql.append("ORDER BY c.CategoryName, a.Status ");

        // Phân trang - cú pháp SQL Server
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);  // bỏ qua bao nhiêu dòng
        params.add(pageSize);               // lấy bao nhiêu dòng

        List<InventorySummary> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // Gán tham số vào PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Long)    ps.setLong(i + 1, (Long) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else                      ps.setString(i + 1, (String) p);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventorySummary s = new InventorySummary();
                    s.setCategoryId(rs.getLong("CategoryId"));
                    s.setCategoryName(rs.getString("CategoryName"));
                    s.setStatus(rs.getString("Status"));
                    s.setTotalAssets(rs.getInt("TotalAssets"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * Đếm tổng số dòng inventory (dùng để tính số trang)
     * Điều kiện filter/search giống hệt hàm trên, chỉ khác là COUNT thay vì SELECT dữ liệu
     */
    public int countInventoryByCategoryAndStatus(String categoryFilter, String search)
            throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) AS Total FROM ( ");
        sql.append("  SELECT c.CategoryId, a.Status ");
        sql.append("  FROM Assets a ");
        sql.append("  JOIN AssetCategories c ON a.CategoryId = c.CategoryId ");
        sql.append("  WHERE a.IsActive = 1 ");

        List<Object> params = new ArrayList<>();
        if (categoryFilter != null && !categoryFilter.trim().isEmpty()) {
            sql.append("  AND c.CategoryId = ? ");
            params.add(Long.parseLong(categoryFilter));
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("  AND c.CategoryName LIKE ? ");
            params.add("%" + search.trim() + "%");
        }
        sql.append("  GROUP BY c.CategoryId, c.CategoryName, a.Status ");
        sql.append(") AS sub");

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Long) ps.setLong(i + 1, (Long) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("Total");
            }
        }
        return 0;
    }

    /**
     * Lấy TẤT CẢ dữ liệu inventory (KHÔNG phân trang)
     * → Dùng riêng cho biểu đồ để biểu đồ luôn hiển thị toàn bộ dữ liệu
     */
    public List<InventorySummary> getAllInventoryForChart() throws SQLException {
        String sql = "SELECT c.CategoryId, c.CategoryName, a.Status, COUNT(*) AS TotalAssets " +
                     "FROM Assets a " +
                     "JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
                     "WHERE a.IsActive = 1 " +
                     "GROUP BY c.CategoryId, c.CategoryName, a.Status " +
                     "ORDER BY c.CategoryName, a.Status";
        List<InventorySummary> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                InventorySummary s = new InventorySummary();
                s.setCategoryId(rs.getLong("CategoryId"));
                s.setCategoryName(rs.getString("CategoryName"));
                s.setStatus(rs.getString("Status"));
                s.setTotalAssets(rs.getInt("TotalAssets"));
                list.add(s);
            }
        }
        return list;
    }

    // ============================================================
    // ===== INVENTORY DETAIL (giữ nguyên, không đổi)
    // ============================================================
    public List<InventoryDetailRow> getInventoryDetailByCategory(long categoryId) throws SQLException {
        String sql = "SELECT a.AssetName, a.Status, COUNT(*) AS Quantity, " +
                     "       MAX(a.UpdatedAt) AS LastUpdatedAt " +
                     "FROM Assets a " +
                     "WHERE a.IsActive = 1 AND a.CategoryId = ? " +
                     "GROUP BY a.AssetName, a.Status " +
                     "ORDER BY a.AssetName, a.Status";
        List<InventoryDetailRow> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventoryDetailRow row = new InventoryDetailRow();
                    row.setAssetName(rs.getString("AssetName"));
                    row.setStatus(rs.getString("Status"));
                    row.setQuantity(rs.getInt("Quantity"));
                    Timestamp ts = rs.getTimestamp("LastUpdatedAt");
                    if (ts != null) {
                        row.setLastUpdatedAt(ts.toLocalDateTime());
                    }
                    list.add(row);
                }
            }
        }
        return list;
    }

    // ============================================================
    // ===== USAGE SUMMARY (có filter, search, phân trang)
    // ============================================================
    /**
     * Lấy danh sách usage summary, hỗ trợ:
     * - roomFilter: lọc theo RoomId (null = tất cả)
     * - search: tìm theo RoomName hoặc CategoryName (null = không tìm)
     * - page, pageSize: phân trang
     */
    public List<UsageSummary> getCurrentUsageByRoomAndCategory(
            String roomFilter, String search, int page, int pageSize) throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.RoomId, r.RoomName, c.CategoryId, c.CategoryName, COUNT(*) AS TotalAssets ");
        sql.append("FROM Assets a ");
        sql.append("LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId ");
        sql.append("JOIN AssetCategories c ON a.CategoryId = c.CategoryId ");
        sql.append("WHERE a.IsActive = 1 AND a.Status = 'IN_USE' ");

        List<Object> params = new ArrayList<>();

        if (roomFilter != null && !roomFilter.trim().isEmpty()) {
            sql.append("AND r.RoomId = ? ");
            params.add(Long.parseLong(roomFilter));
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (r.RoomName LIKE ? OR c.CategoryName LIKE ?) ");
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }

        sql.append("GROUP BY r.RoomId, r.RoomName, c.CategoryId, c.CategoryName ");
        sql.append("ORDER BY r.RoomName, c.CategoryName ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        List<UsageSummary> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Long)    ps.setLong(i + 1, (Long) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else                      ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsageSummary s = new UsageSummary();
                    s.setRoomId(rs.getLong("RoomId"));
                    s.setRoomName(rs.getString("RoomName"));
                    s.setCategoryId(rs.getLong("CategoryId"));
                    s.setCategoryName(rs.getString("CategoryName"));
                    s.setTotalAssets(rs.getInt("TotalAssets"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * Đếm tổng số dòng usage (cho phân trang)
     */
    public int countUsageByRoomAndCategory(String roomFilter, String search) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) AS Total FROM ( ");
        sql.append("  SELECT r.RoomId, c.CategoryId ");
        sql.append("  FROM Assets a ");
        sql.append("  LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId ");
        sql.append("  JOIN AssetCategories c ON a.CategoryId = c.CategoryId ");
        sql.append("  WHERE a.IsActive = 1 AND a.Status = 'IN_USE' ");

        List<Object> params = new ArrayList<>();
        if (roomFilter != null && !roomFilter.trim().isEmpty()) {
            sql.append("  AND r.RoomId = ? ");
            params.add(Long.parseLong(roomFilter));
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("  AND (r.RoomName LIKE ? OR c.CategoryName LIKE ?) ");
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }
        sql.append("  GROUP BY r.RoomId, r.RoomName, c.CategoryId, c.CategoryName ");
        sql.append(") AS sub");

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Long) ps.setLong(i + 1, (Long) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("Total");
            }
        }
        return 0;
    }

    /**
     * Lấy TẤT CẢ dữ liệu usage (KHÔNG phân trang)
     * → Dùng riêng cho biểu đồ
     */
    public List<UsageSummary> getAllUsageForChart() throws SQLException {
        String sql = "SELECT r.RoomId, r.RoomName, c.CategoryId, c.CategoryName, COUNT(*) AS TotalAssets " +
                     "FROM Assets a " +
                     "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId " +
                     "JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
                     "WHERE a.IsActive = 1 AND a.Status = 'IN_USE' " +
                     "GROUP BY r.RoomId, r.RoomName, c.CategoryId, c.CategoryName " +
                     "ORDER BY r.RoomName, c.CategoryName";
        List<UsageSummary> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UsageSummary s = new UsageSummary();
                s.setRoomId(rs.getLong("RoomId"));
                s.setRoomName(rs.getString("RoomName"));
                s.setCategoryId(rs.getLong("CategoryId"));
                s.setCategoryName(rs.getString("CategoryName"));
                s.setTotalAssets(rs.getInt("TotalAssets"));
                list.add(s);
            }
        }
        return list;
    }

    // ============================================================
    // ===== USAGE DETAIL (giữ nguyên, không đổi)
    // ============================================================
    public List<UsageDetailRow> getUsageDetailByRoomAndCategory(long roomId, long categoryId)
            throws SQLException {
        String sql = "SELECT a.AssetCode, a.AssetName, a.SerialNumber, a.Model, a.Brand, " +
                     "       a.Status, a.UpdatedAt " +
                     "FROM Assets a " +
                     "WHERE a.IsActive = 1 " +
                     "  AND a.Status = 'IN_USE' " +
                     "  AND a.CurrentRoomId = ? " +
                     "  AND a.CategoryId = ? " +
                     "ORDER BY a.AssetName, a.AssetCode";
        List<UsageDetailRow> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            ps.setLong(2, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsageDetailRow row = new UsageDetailRow();
                    row.setAssetCode(rs.getString("AssetCode"));
                    row.setAssetName(rs.getString("AssetName"));
                    row.setSerialNumber(rs.getString("SerialNumber"));
                    row.setModel(rs.getString("Model"));
                    row.setBrand(rs.getString("Brand"));
                    row.setStatus(rs.getString("Status"));
                    Timestamp ts = rs.getTimestamp("UpdatedAt");
                    if (ts != null) {
                        row.setUpdatedAt(ts.toLocalDateTime());
                    }
                    list.add(row);
                }
            }
        }
        return list;
    }
}

