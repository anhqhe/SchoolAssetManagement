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

/**
 *
 * @author An
 */
public class AssetDao {

    public List<Asset> findAll() throws SQLException {
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, " +
             "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.OriginNote, " +
             "a.PurchaseDate, a.ReceivedDate, a.ConditionNote, a.Status, " +
             "a.CurrentRoomId, r.RoomName, a.CurrentHolderId, " +
             "a.IsActive, a.CreatedAt, a.UpdatedAt, " +
             "(SELECT COUNT(*) FROM Assets a2 WHERE a2.AssetName = a.AssetName AND a2.CategoryId = a.CategoryId) AS Quantity " +
             "FROM Assets a " +
             "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
             "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId " +
             "ORDER BY a.CreatedAt DESC";
        List<Asset> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asset a = new Asset();
                a.setAssetId(rs.getLong("AssetId"));
                a.setAssetCode(rs.getString("AssetCode"));
                a.setAssetName(rs.getString("AssetName"));
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
                + "CurrentRoomId, CurrentHolderId, IsActive"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.executeUpdate();
        }
    }

    public void update(Asset a) throws SQLException {
        String sql = "UPDATE Assets SET "
                + "AssetCode=?, AssetName=?, CategoryId=?, SerialNumber=?, Model=?, Brand=?, OriginNote=?, "
                + "PurchaseDate=?, ReceivedDate=?, ConditionNote=?, Status=?, "
                + "CurrentRoomId=?, CurrentHolderId=?, IsActive=?, UpdatedAt=SYSDATETIME() "
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
            ps.setLong(15, a.getAssetId());
            ps.executeUpdate();
        }
    }

    public void delete(int assetId) throws SQLException {
        String sql = "DELETE FROM Assets WHERE AssetId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assetId);
            ps.executeUpdate();
        }
    }

    /** Cập nhật trạng thái tài sản và ghi lịch sử vào AssetStatusHistory */
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
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, "
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
  public List<Asset> searchAssets(String keyword, String status, Long categoryId) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, " +
            "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, " +
            "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, " +
            "a.ConditionNote, a.IsActive, a.CreatedAt, " +
            "(SELECT COUNT(*) FROM Assets a2 WHERE a2.AssetName = a.AssetName AND a2.CategoryId = a.CategoryId) AS Quantity " +
            "FROM Assets a " +
            "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
            "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId " +
            "WHERE 1=1 "
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
        
        sql.append("ORDER BY a.CreatedAt DESC");
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
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
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asset asset = new Asset();
                    asset.setAssetId(rs.getLong("AssetId"));
                    asset.setAssetCode(rs.getString("AssetCode"));
                    asset.setAssetName(rs.getString("AssetName"));
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
}
