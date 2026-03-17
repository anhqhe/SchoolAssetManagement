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
import java.time.LocalDateTime;
import java.sql.Timestamp;
import model.report.InventoryDetailRow;
import model.report.InventorySummary;
import model.report.UsageDetailRow;
import model.report.UsageSummary;
import util.DBUtil;

/**
 *
 * @author An
 */
public class AssetReportDao {
       // ====== INVENTORY SUMMARY: GROUP THEO CATEGORY + STATUS ======
    public List<InventorySummary> getInventoryByCategoryAndStatus() throws SQLException {
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
    // ====== INVENTORY DETAIL: GỘP THEO TÊN TRONG 1 CATEGORY ======
   
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
    
    // ====== USAGE SUMMARY: THEO PHÒNG + CATEGORY ======
    public List<UsageSummary> getCurrentUsageByRoomAndCategory() throws SQLException {
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
    // ====== USAGE DETAIL: LIỆT KÊ THEO MÃ TỪNG TÀI SẢN ======
   
    public List<UsageDetailRow> getUsageDetailByRoomAndCategory(long roomId, long categoryId) throws SQLException {
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
