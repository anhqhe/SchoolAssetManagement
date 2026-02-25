package dao;

import model.Asset;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetDAO {
    
    /**
     * Lấy tất cả assets
     */
    public List<Asset> getAllAssets() throws SQLException {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, " +
                    "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, " +
                    "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, " +
                    "a.ConditionNote, a.IsActive, a.CreatedAt " +
                    "FROM Assets a " +
                    "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
                    "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId " +
                    "ORDER BY a.CreatedAt DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
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
                asset.setPurchaseDate(rs.getDate("PurchaseDate"));
                asset.setReceivedDate(rs.getDate("ReceivedDate"));
                asset.setConditionNote(rs.getString("ConditionNote"));
                asset.setActive(rs.getBoolean("IsActive"));
                asset.setCreatedAt(rs.getTimestamp("CreatedAt"));
                
                assets.add(asset);
            }
        }
        
        return assets;
    }
    
    /**
     * Lấy asset theo ID
     */
    public Asset getAssetById(long assetId) throws SQLException {
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, " +
                    "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.OriginNote, " +
                    "a.PurchaseDate, a.ReceivedDate, a.ConditionNote, a.Status, " +
                    "a.CurrentRoomId, r.RoomName, r.Location, a.CurrentHolderId, " +
                    "u.FullName as HolderName, a.IsActive, a.CreatedAt, a.UpdatedAt " +
                    "FROM Assets a " +
                    "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
                    "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId " +
                    "LEFT JOIN Users u ON a.CurrentHolderId = u.UserId " +
                    "WHERE a.AssetId = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, assetId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Asset asset = new Asset();
                    asset.setAssetId(rs.getLong("AssetId"));
                    asset.setAssetCode(rs.getString("AssetCode"));
                    asset.setAssetName(rs.getString("AssetName"));
                    asset.setCategoryId(rs.getLong("CategoryId"));
                    asset.setCategoryName(rs.getString("CategoryName"));
                    asset.setSerialNumber(rs.getString("SerialNumber"));
                    asset.setModel(rs.getString("Model"));
                    asset.setBrand(rs.getString("Brand"));
                    asset.setOriginNote(rs.getString("OriginNote"));
                    asset.setPurchaseDate(rs.getDate("PurchaseDate"));
                    asset.setReceivedDate(rs.getDate("ReceivedDate"));
                    asset.setConditionNote(rs.getString("ConditionNote"));
                    asset.setStatus(rs.getString("Status"));
                    asset.setCurrentRoomId(rs.getLong("CurrentRoomId"));
                    asset.setRoomName(rs.getString("RoomName"));
                    asset.setRoomLocation(rs.getString("Location"));
                    asset.setCurrentHolderId(rs.getLong("CurrentHolderId"));
                    asset.setHolderName(rs.getString("HolderName"));
                    asset.setActive(rs.getBoolean("IsActive"));
                    asset.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    asset.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    
                    return asset;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Search assets
     */
    public List<Asset> searchAssets(String keyword, String status, Long categoryId) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, " +
            "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, " +
            "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, " +
            "a.ConditionNote, a.IsActive, a.CreatedAt " +
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
                    asset.setPurchaseDate(rs.getDate("PurchaseDate"));
                    asset.setReceivedDate(rs.getDate("ReceivedDate"));
                    asset.setConditionNote(rs.getString("ConditionNote"));
                    asset.setActive(rs.getBoolean("IsActive"));
                    asset.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    
                    assets.add(asset);
                }
            }
        }
        
        return assets;
    }
}

