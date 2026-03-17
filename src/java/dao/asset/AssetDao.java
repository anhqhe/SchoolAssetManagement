/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.asset;

import java.util.ArrayList;
import java.util.List;
import model.asset.Asset;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import util.DBUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author An
 */
public class AssetDao {

    public List<Asset> findAll() throws SQLException {
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.Unit, a.CategoryId, "
                + "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.OriginNote, "
                + "a.PurchaseDate, a.ReceivedDate, a.ConditionNote, a.Status, "
                + "a.CurrentRoomId, r.RoomName, a.CurrentHolderId, "
                + "a.IsActive, a.CreatedAt, a.UpdatedAt, "
                + "(SELECT COUNT(*) FROM Assets a2 WHERE a2.AssetName = a.AssetName AND a2.CategoryId = a.CategoryId AND a2.IsActive = 1) AS Quantity "
                + "FROM Assets a "
                + "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId "
                + "WHERE a.IsActive = 1 "
                + "ORDER BY a.CreatedAt DESC";
        List<Asset> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asset a = new Asset();
                a.setAssetId(rs.getLong("AssetId"));
                a.setAssetCode(rs.getString("AssetCode"));
                a.setAssetName(rs.getString("AssetName"));
                a.setUnit(rs.getString("Unit"));
                a.setCategoryId(rs.getLong("CategoryId"));
                a.setSerialNumber(rs.getString("SerialNumber"));
                a.setModel(rs.getString("Model"));
                a.setBrand(rs.getString("Brand"));
                a.setOriginNote(rs.getString("OriginNote"));
                // PurchaseDate (date) -> LocalDateTime
                Date pDate = rs.getDate("PurchaseDate");
                if (pDate != null) {
                    a.setPurchaseDate(pDate.toLocalDate().atStartOfDay());
                }

                // ReceivedDate (date) -> LocalDateTime
                Date rDate = rs.getDate("ReceivedDate");
                if (rDate != null) {
                    a.setReceivedDate(rDate.toLocalDate().atStartOfDay());
                }
                a.setConditionNote(rs.getString("ConditionNote"));
                a.setStatus(rs.getString("Status"));

                // có thể null
                long roomId = rs.getLong("CurrentRoomId");
                if (rs.wasNull()) {
                    a.setCurrentRoomId(0); // 0 là chưa có phòng
                } else {
                    a.setCurrentRoomId(roomId);
                }

                long holderId = rs.getLong("CurrentHolderId");
                if (rs.wasNull()) {
                    a.setCurrentHolderId(0);
                } else {
                    a.setCurrentHolderId(holderId);
                }

                a.setIsActive(rs.getBoolean("IsActive"));

                // CreatedAt, UpdatedAt (datetime2) -> LocalDateTime
                Timestamp cAt = rs.getTimestamp("CreatedAt");
                if (cAt != null) {
                    a.setCreatedAt(cAt.toLocalDateTime());
                }
                Timestamp uAt = rs.getTimestamp("UpdatedAt");
                if (uAt != null) {
                    a.setUpdatedAt(uAt.toLocalDateTime());
                }
                a.setCategoryName(rs.getString("CategoryName"));
                a.setRoomName(rs.getString("RoomName"));
                a.setQuantity(rs.getInt("Quantity"));
                list.add(a);
            }
        }
        return list;
    }

    public void insert(Asset a) throws SQLException {
        String sql = "INSERT INTO Assets ("
                + "AssetCode, AssetName, CategoryId, SerialNumber, Model, Brand, OriginNote, "
                + "PurchaseDate, ReceivedDate, ConditionNote, Status, "
                + "CurrentRoomId, CurrentHolderId, IsActive, Unit"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAssetCode());
            ps.setString(2, a.getAssetName());
            ps.setLong(3, a.getCategoryId());
            ps.setString(4, a.getSerialNumber());
            ps.setString(5, a.getModel());
            ps.setString(6, a.getBrand());
            ps.setString(7, a.getOriginNote());
            //Purchase Date
            if (a.getPurchaseDate() != null) {
                ps.setDate(8, Date.valueOf(a.getPurchaseDate().toLocalDate()));
            } else {
                ps.setNull(8, Types.DATE);
            }
            //Received Date
            if (a.getReceivedDate() != null) {
                ps.setDate(9, Date.valueOf(a.getReceivedDate().toLocalDate()));
            } else {
                ps.setNull(9, Types.DATE);
            }
            ps.setString(10, a.getConditionNote());
            ps.setString(11, a.getStatus());
            //CurrentRoomId
            if (a.getCurrentRoomId() != 0) {
                ps.setLong(12, a.getCurrentRoomId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            //CurrentHolderId
            if (a.getCurrentHolderId() != 0) {
                ps.setLong(13, a.getCurrentHolderId());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.setBoolean(14, a.isIsActive());
            ps.setString(15, a.getUnit());
            ps.executeUpdate();
        }
    }

    public void update(Asset a) throws SQLException {
        String sql = "UPDATE Assets SET "
                + "AssetCode=?, AssetName=?, CategoryId=?, SerialNumber=?, Model=?, Brand=?, OriginNote=?, "
                + "PurchaseDate=?, ReceivedDate=?, ConditionNote=?, Status=?, "
                + "CurrentRoomId=?, CurrentHolderId=?, IsActive=?, Unit=?, UpdatedAt=SYSDATETIME() "
                + "WHERE AssetId=?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAssetCode());
            ps.setString(2, a.getAssetName());
            ps.setLong(3, a.getCategoryId());
            ps.setString(4, a.getSerialNumber());
            ps.setString(5, a.getModel());
            ps.setString(6, a.getBrand());
            ps.setString(7, a.getOriginNote());
            //Purchase Date
            if (a.getPurchaseDate() != null) {
                ps.setDate(8, Date.valueOf(a.getPurchaseDate().toLocalDate()));
            } else {
                ps.setNull(8, Types.DATE);
            }
            //Received Date
            if (a.getReceivedDate() != null) {
                ps.setDate(9, Date.valueOf(a.getReceivedDate().toLocalDate()));
            } else {
                ps.setNull(9, Types.DATE);
            }
            ps.setString(10, a.getConditionNote());
            ps.setString(11, a.getStatus());
            //CurrentRoomId
            if (a.getCurrentRoomId() != 0) {
                ps.setLong(12, a.getCurrentRoomId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            //CurrentHolderId
            if (a.getCurrentHolderId() != 0) {
                ps.setLong(13, a.getCurrentHolderId());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.setBoolean(14, a.isIsActive());
            ps.setString(15, a.getUnit());
            ps.setLong(16, a.getAssetId());
            ps.executeUpdate();
        }
    }

    public void delete(int assetId) throws SQLException {
        String sql = "UPDATE Assets SET IsActive = 0, UpdatedAt = SYSDATETIME() WHERE AssetId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assetId);
            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật trạng thái tài sản và ghi lịch sử vào AssetStatusHistory
     */
    public void updateStatus(long assetId, String newStatus, String reason, long changedByUserId) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {
            String oldStatus = null;
            String selSql = "SELECT Status FROM Assets WHERE AssetId = ?";
            try (PreparedStatement ps = con.prepareStatement(selSql)) {
                ps.setLong(1, assetId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("Status");
                    }
                }
            }
            if (oldStatus == null) {
                throw new SQLException("Asset not found: " + assetId);
            }
            String updateSql = "UPDATE Assets SET Status = ?, UpdatedAt = SYSDATETIME() WHERE AssetId = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setString(1, newStatus);
                ps.setLong(2, assetId);
                ps.executeUpdate();
            }
            String histSql = "INSERT INTO AssetStatusHistory (AssetId, OldStatus, NewStatus, Reason, ChangedByUserId, ChangedAt) VALUES (?, ?, ?, ?, ?, SYSDATETIME())";
            try (PreparedStatement ps = con.prepareStatement(histSql)) {
                ps.setLong(1, assetId);
                ps.setString(2, oldStatus);
                ps.setString(3, newStatus);
                ps.setString(4, reason);
                ps.setLong(5, changedByUserId);
                ps.executeUpdate();
            }
        }
    }

    public Asset findById(int id) throws SQLException {
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.Unit, a.CategoryId, "
                + "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.OriginNote, "
                + "a.PurchaseDate, a.ReceivedDate, a.ConditionNote, a.Status, "
                + "a.CurrentRoomId, r.RoomName, r.Location AS RoomLocation, "
                + "a.CurrentHolderId, u.FullName AS HolderName, "
                + "a.IsActive, a.CreatedAt, a.UpdatedAt "
                + "FROM Assets a "
                + "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId "
                + "LEFT JOIN Users u ON a.CurrentHolderId = u.UserId "
                + "WHERE a.AssetId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Asset a = new Asset();
                    a.setAssetId(rs.getLong("AssetId"));
                    a.setAssetCode(rs.getString("AssetCode"));
                    a.setAssetName(rs.getString("AssetName"));
                    a.setUnit(rs.getString("Unit"));
                    a.setCategoryId(rs.getLong("CategoryId"));
                    a.setSerialNumber(rs.getString("SerialNumber"));
                    a.setModel(rs.getString("Model"));
                    a.setBrand(rs.getString("Brand"));
                    a.setOriginNote(rs.getString("OriginNote"));
                    // PurchaseDate (date) -> LocalDateTime
                    Date pDate = rs.getDate("PurchaseDate");
                    if (pDate != null) {
                        a.setPurchaseDate(pDate.toLocalDate().atStartOfDay());
                    }

                    // ReceivedDate (date) -> LocalDateTime
                    Date rDate = rs.getDate("ReceivedDate");
                    if (rDate != null) {
                        a.setReceivedDate(rDate.toLocalDate().atStartOfDay());
                    }
                    a.setConditionNote(rs.getString("ConditionNote"));
                    a.setStatus(rs.getString("Status"));

                    // có thể null
                    long roomId = rs.getLong("CurrentRoomId");
                    if (rs.wasNull()) {
                        a.setCurrentRoomId(0); // 0 là chưa có phòng
                    } else {
                        a.setCurrentRoomId(roomId);
                    }

                    long holderId = rs.getLong("CurrentHolderId");
                    if (rs.wasNull()) {
                        a.setCurrentHolderId(0);
                    } else {
                        a.setCurrentHolderId(holderId);
                    }

                    a.setIsActive(rs.getBoolean("IsActive"));

                    // CreatedAt, UpdatedAt (datetime2) -> LocalDateTime
                    Timestamp cAt = rs.getTimestamp("CreatedAt");
                    if (cAt != null) {
                        a.setCreatedAt(cAt.toLocalDateTime());
                    }
                    Timestamp uAt = rs.getTimestamp("UpdatedAt");
                    if (uAt != null) {
                        a.setUpdatedAt(uAt.toLocalDateTime());
                    }
                    a.setCategoryName(rs.getString("CategoryName"));
                    a.setRoomName(rs.getString("RoomName"));
                    a.setRoomLocation(rs.getString("RoomLocation"));
                    a.setHolderName(rs.getString("HolderName"));
                    return a;
                }
            }
        }
        return null;
    }

    public List<Asset> searchAssets(String keyword, String status, Long categoryId, Boolean isActive) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.AssetId, a.AssetCode, a.AssetName, a.Unit, a.CategoryId, "
                + "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, "
                + "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, "
                + "a.ConditionNote, a.IsActive, a.CreatedAt, "
                + "(SELECT COUNT(*) FROM Assets a2 WHERE a2.AssetName = a.AssetName AND a2.CategoryId = a.CategoryId AND a2.IsActive = 1) AS Quantity "
                + "FROM Assets a "
                + "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId "
                + "WHERE 1=1 "
        );

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (a.AssetName LIKE ? OR a.AssetCode LIKE ? OR a.SerialNumber LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND a.Status = ? ");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND a.CategoryId = ? ");
        }
        if (isActive != null) {
            sql.append("AND a.IsActive = ? ");
        }
        sql.append("ORDER BY a.CreatedAt DESC");

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (categoryId != null && categoryId > 0) {
                ps.setLong(paramIndex++, categoryId);
            }
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asset asset = new Asset();
                    asset.setAssetId(rs.getLong("AssetId"));
                    asset.setAssetCode(rs.getString("AssetCode"));
                    asset.setAssetName(rs.getString("AssetName"));
                    asset.setUnit(rs.getString("Unit"));
                    asset.setCategoryId(rs.getLong("CategoryId"));
                    asset.setCategoryName(rs.getString("CategoryName"));
                    asset.setSerialNumber(rs.getString("SerialNumber"));
                    asset.setModel(rs.getString("Model"));
                    asset.setBrand(rs.getString("Brand"));
                    asset.setStatus(rs.getString("Status"));
                    asset.setCurrentRoomId(rs.getLong("CurrentRoomId"));
                    asset.setRoomName(rs.getString("RoomName"));
                    // PurchaseDate (date) -> LocalDateTime
                    Date pDate = rs.getDate("PurchaseDate");
                    if (pDate != null) {
                        asset.setPurchaseDate(pDate.toLocalDate().atStartOfDay());
                    }
                    // ReceivedDate (date) -> LocalDateTime
                    Date rDate = rs.getDate("ReceivedDate");
                    if (rDate != null) {
                        asset.setReceivedDate(rDate.toLocalDate().atStartOfDay());
                    }
                    asset.setConditionNote(rs.getString("ConditionNote"));
                    asset.setIsActive(rs.getBoolean("IsActive"));
                    // CreatedAt (datetime2) -> LocalDateTime
                    Timestamp cAt = rs.getTimestamp("CreatedAt");
                    if (cAt != null) {
                        asset.setCreatedAt(cAt.toLocalDateTime());
                    }
                    asset.setQuantity(rs.getInt("Quantity"));

                    assets.add(asset);
                }
            }
        }

        return assets;
    }

    //Generated auto mã sản phẩm 
    public List<String> generateAssetCodes(long categoryId, int count) throws SQLException {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "TS-" + datePart + "-";

        // Lấy max sequence: SELECT mã có prefix, parse số cuối, lấy max
        String sql = "SELECT AssetCode FROM Assets WHERE AssetCode LIKE ? ORDER BY AssetCode DESC";
        int nextSeq = 1;
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("AssetCode");
                    if (code != null && code.length() > prefix.length()) {
                        try {
                            int seq = Integer.parseInt(code.substring(prefix.length()));
                            if (seq >= nextSeq) {
                                nextSeq = seq + 1;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    break;// Chỉ cần 1 bản ghi (đã ORDER BY DESC)
                }
            }
        }

        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            codes.add(prefix + String.format("%04d", nextSeq + i));
        }
        return codes;
    }

// Đếm tổng số tài sản đang active (dùng cho phân trang không filter)
    public int countAllAssets() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Assets WHERE IsActive = 1";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

// Đếm tổng bản ghi theo bộ lọc (keyword, status, categoryId, isActive)
    public int countSearchAssets(String keyword, String status, Long categoryId, Boolean isActive) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Assets a WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (a.AssetName LIKE ? OR a.AssetCode LIKE ? OR a.SerialNumber LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND a.Status = ? ");
            params.add(status.trim());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND a.CategoryId = ? ");
            params.add(categoryId);
        }
        if (isActive != null) {
            sql.append("AND a.IsActive = ? ");
            params.add(isActive);
        } else {
            // nếu không truyền trạng thái, mặc định chỉ đếm active giống findAll()
            sql.append("AND a.IsActive = 1 ");
        }

        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Lấy danh sách tài sản (active) theo trang
    public List<Asset> findAllByPage(int offset, int limit) throws SQLException {
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.Unit, a.CategoryId, "
                + "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.OriginNote, "
                + "a.PurchaseDate, a.ReceivedDate, a.ConditionNote, a.Status, "
                + "a.CurrentRoomId, r.RoomName, a.CurrentHolderId, "
                + "a.IsActive, a.CreatedAt, a.UpdatedAt, "
                + "(SELECT COUNT(*) FROM Assets a2 "
                + " WHERE a2.AssetName = a.AssetName "
                + "   AND a2.CategoryId = a.CategoryId "
                + "   AND a2.IsActive = 1) AS Quantity "
                + "FROM Assets a "
                + "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId "
                + "WHERE a.IsActive = 1 "
                + "ORDER BY a.CreatedAt DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<Asset> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asset a = new Asset();
                    a.setAssetId(rs.getLong("AssetId"));
                    a.setAssetCode(rs.getString("AssetCode"));
                    a.setAssetName(rs.getString("AssetName"));
                    a.setUnit(rs.getString("Unit"));
                    a.setCategoryId(rs.getLong("CategoryId"));
                    a.setSerialNumber(rs.getString("SerialNumber"));
                    a.setModel(rs.getString("Model"));
                    a.setBrand(rs.getString("Brand"));
                    a.setOriginNote(rs.getString("OriginNote"));

                    Date pDate = rs.getDate("PurchaseDate");
                    if (pDate != null) {
                        a.setPurchaseDate(pDate.toLocalDate().atStartOfDay());
                    }
                    Date rDate = rs.getDate("ReceivedDate");
                    if (rDate != null) {
                        a.setReceivedDate(rDate.toLocalDate().atStartOfDay());
                    }

                    a.setConditionNote(rs.getString("ConditionNote"));
                    a.setStatus(rs.getString("Status"));

                    long roomId = rs.getLong("CurrentRoomId");
                    if (rs.wasNull()) {
                        a.setCurrentRoomId(0);
                    } else {
                        a.setCurrentRoomId(roomId);
                    }

                    long holderId = rs.getLong("CurrentHolderId");
                    if (rs.wasNull()) {
                        a.setCurrentHolderId(0);
                    } else {
                        a.setCurrentHolderId(holderId);
                    }

                    a.setIsActive(rs.getBoolean("IsActive"));

                    Timestamp cAt = rs.getTimestamp("CreatedAt");
                    if (cAt != null) {
                        a.setCreatedAt(cAt.toLocalDateTime());
                    }
                    Timestamp uAt = rs.getTimestamp("UpdatedAt");
                    if (uAt != null) {
                        a.setUpdatedAt(uAt.toLocalDateTime());
                    }

                    a.setCategoryName(rs.getString("CategoryName"));
                    a.setRoomName(rs.getString("RoomName"));
                    a.setQuantity(rs.getInt("Quantity"));

                    list.add(a);
                }
            }
        }
        return list;
    }

    // Tìm kiếm tài sản theo filter + phân trang
    public List<Asset> searchAssetsByPage(String keyword, String status, Long categoryId,
            Boolean isActive, int offset, int limit) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.AssetId, a.AssetCode, a.AssetName, a.Unit, a.CategoryId, "
                + "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, "
                + "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, "
                + "a.ConditionNote, a.IsActive, a.CreatedAt, "
                + "(SELECT COUNT(*) FROM Assets a2 "
                + " WHERE a2.AssetName = a.AssetName "
                + "   AND a2.CategoryId = a.CategoryId "
                + "   AND a2.IsActive = 1) AS Quantity "
                + "FROM Assets a "
                + "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId "
                + "WHERE 1=1 "
        );

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (a.AssetName LIKE ? OR a.AssetCode LIKE ? OR a.SerialNumber LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND a.Status = ? ");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND a.CategoryId = ? ");
        }
        if (isActive != null) {
            sql.append("AND a.IsActive = ? ");
        } else {
            // giống list bình thường: chỉ lấy active
            sql.append("AND a.IsActive = 1 ");
        }

        sql.append("ORDER BY a.CreatedAt DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.trim());
            }
            if (categoryId != null && categoryId > 0) {
                ps.setLong(paramIndex++, categoryId);
            }
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }

            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asset asset = new Asset();
                    asset.setAssetId(rs.getLong("AssetId"));
                    asset.setAssetCode(rs.getString("AssetCode"));
                    asset.setAssetName(rs.getString("AssetName"));
                    asset.setUnit(rs.getString("Unit"));
                    asset.setCategoryId(rs.getLong("CategoryId"));
                    asset.setCategoryName(rs.getString("CategoryName"));
                    asset.setSerialNumber(rs.getString("SerialNumber"));
                    asset.setModel(rs.getString("Model"));
                    asset.setBrand(rs.getString("Brand"));
                    asset.setStatus(rs.getString("Status"));
                    asset.setCurrentRoomId(rs.getLong("CurrentRoomId"));
                    asset.setRoomName(rs.getString("RoomName"));

                    Date pDate = rs.getDate("PurchaseDate");
                    if (pDate != null) {
                        asset.setPurchaseDate(pDate.toLocalDate().atStartOfDay());
                    }
                    Date rDate = rs.getDate("ReceivedDate");
                    if (rDate != null) {
                        asset.setReceivedDate(rDate.toLocalDate().atStartOfDay());
                    }
                    asset.setConditionNote(rs.getString("ConditionNote"));
                    asset.setIsActive(rs.getBoolean("IsActive"));

                    Timestamp cAt = rs.getTimestamp("CreatedAt");
                    if (cAt != null) {
                        asset.setCreatedAt(cAt.toLocalDateTime());
                    }

                    asset.setQuantity(rs.getInt("Quantity"));

                    assets.add(asset);
                }
            }
        }

        return assets;
    }
}
