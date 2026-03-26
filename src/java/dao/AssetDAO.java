package dao;

import model.Asset;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Transfer;
import model.asset.AssetTransferHistory;

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

    public List<Asset> getAssetsByRoomId(long roomId) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, " +
                    "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, " +
                    "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, " +
                    "a.ConditionNote, a.IsActive, a.CreatedAt " +
                    "FROM Assets a " +
                    "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
                    "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId " +
                    "WHERE a.CurrentRoomId = ? AND a.IsActive = 1 " +
                    "ORDER BY a.AssetName ASC, a.AssetCode ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roomId);

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

    public List<Asset> getAssetsByCategoryId(long categoryId) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, a.CategoryId, "
                + "c.CategoryName, a.SerialNumber, a.Model, a.Brand, a.Status, "
                + "a.CurrentRoomId, r.RoomName, a.PurchaseDate, a.ReceivedDate, "
                + "a.ConditionNote, a.IsActive, a.CreatedAt "
                + "FROM Assets a "
                + "LEFT JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "LEFT JOIN Rooms r ON a.CurrentRoomId = r.RoomId "
                + "WHERE a.CategoryId = ? "
                + "ORDER BY a.AssetName ASC, a.AssetCode ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);

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
public List<Asset> getAvailableAssets() throws SQLException {
    List<Asset> list = new ArrayList<>();
    // Chỉ lấy các tài sản có trạng thái cho phép điều chuyển 
   String sql = "SELECT AssetId, AssetName, AssetCode, CurrentRoomId FROM Assets WHERE Status != 'Disposed'";

    
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Asset a = new Asset();
            a.setAssetId(rs.getInt("AssetId"));
            a.setAssetName(rs.getString("AssetName"));
            a.setAssetCode(rs.getString("AssetCode"));
            a.setCurrentRoomId(rs.getLong("CurrentRoomId"));
            list.add(a);
        }
    }
    return list;
}
public List<Transfer> getAssetTransferHistoryPaging(
        String keyword, String fromDate, String toDate,
        int offset, int pageSize) throws SQLException {

    String sql = "SELECT " +
            " a.AssetId, a.AssetName, a.AssetCode, " +
            " t.TransferCode, " +
            " fr.RoomName AS FromRoomName, " +
            " tr.RoomName AS ToRoomName, " +
            " t.CreatedAt " +
            "FROM AssetTransferItems ti " +
            "JOIN Assets a ON ti.AssetId = a.AssetId " +
            "JOIN AssetTransfers t ON ti.TransferId = t.TransferId " +
            "LEFT JOIN Rooms fr ON t.FromRoomId = fr.RoomId " +
            "LEFT JOIN Rooms tr ON t.ToRoomId = tr.RoomId " +
            "WHERE 1=1 ";

 StringBuilder sb = new StringBuilder(sql);
    List<Object> params = new ArrayList<>();

    if (keyword != null && !keyword.isEmpty()) {
        sb.append(" AND (a.AssetName LIKE ? OR t.TransferCode LIKE ?)");
        params.add("%" + keyword + "%");
        params.add("%" + keyword + "%");
    }

    if (fromDate != null && !fromDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) >= ?");
        params.add(fromDate);
    }

    if (toDate != null && !toDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) <= ?");
        params.add(toDate);
    }

    sb.append(" ORDER BY t.CreatedAt DESC");
    sb.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

    params.add(offset);
    params.add(pageSize);

    List<Transfer> list = new ArrayList<>();

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sb.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Transfer t = new Transfer();

            t.setAssetNames(rs.getString("AssetName")); // reuse field
            t.setTransferCode(rs.getString("TransferCode"));
            t.setFromRoomName(rs.getString("FromRoomName"));
            t.setToRoomName(rs.getString("ToRoomName"));
            t.setCreatedAt(rs.getTimestamp("CreatedAt"));

            list.add(t);
        }
    }

    return list;
}
public int countAssetTransferHistory(
        String keyword, String fromDate, String toDate) throws SQLException {

    String sql = "SELECT COUNT(DISTINCT t.TransferId) " +
                 "FROM AssetTransfers t " +
                 "LEFT JOIN AssetTransferItems ti ON t.TransferId = ti.TransferId " +
                 "LEFT JOIN Assets a ON ti.AssetId = a.AssetId " +
                 "WHERE 1=1";

    StringBuilder sb = new StringBuilder(sql);
    List<Object> params = new ArrayList<>();

    if (keyword != null && !keyword.trim().isEmpty()) {
        sb.append(" AND (a.AssetName LIKE ? OR t.TransferCode LIKE ?)");
        params.add("%" + keyword.trim() + "%");
        params.add("%" + keyword.trim() + "%");
    }

    if (fromDate != null && !fromDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) >= ?");
        params.add(fromDate);
    }

    if (toDate != null && !toDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) <= ?");
        params.add(toDate);
    }

 
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sb.toString())) {


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
public List<AssetTransferHistory> getAssetTransferHistoryGrouped(
        String keyword, String fromDate, String toDate,
        int offset, int pageSize) throws SQLException {


    StringBuilder assetSql = new StringBuilder(
        "SELECT DISTINCT a.AssetId, a.AssetName, a.AssetCode " +
        "FROM AssetTransferItems ti " +
        "JOIN Assets a ON ti.AssetId = a.AssetId " +
        "JOIN AssetTransfers t ON ti.TransferId = t.TransferId " +
        "WHERE 1=1"
    );
    List<Object> params = new ArrayList<>();
    if (keyword != null && !keyword.isEmpty()) {
        assetSql.append(" AND (a.AssetName LIKE ? OR a.AssetCode LIKE ? OR t.TransferCode LIKE ?)");
        params.add("%" + keyword + "%");
        params.add("%" + keyword + "%");
        params.add("%" + keyword + "%");
    }
    if (fromDate != null && !fromDate.isEmpty()) {
        assetSql.append(" AND CAST(t.CreatedAt AS DATE) >= ?");
        params.add(fromDate);
    }
    if (toDate != null && !toDate.isEmpty()) {
        assetSql.append(" AND CAST(t.CreatedAt AS DATE) <= ?");
        params.add(toDate);
    }
    assetSql.append(" ORDER BY a.AssetName");
    assetSql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
    params.add(offset);
    params.add(pageSize);

    List<AssetTransferHistory> result = new ArrayList<>();
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(assetSql.toString())) {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            result.add(new AssetTransferHistory(
                rs.getInt("AssetId"),
                rs.getString("AssetName"),
                rs.getString("AssetCode"),
                new ArrayList<>()
            ));
        }
    }

    if (result.isEmpty()) return result;


    String inClause = result.stream()
            .map(a -> String.valueOf(a.getAssetId()))
            .collect(Collectors.joining(","));

    String historySql =
        "SELECT a.AssetId, t.TransferId, t.TransferCode, t.CreatedAt, " +
        "       fr.RoomName AS FromRoomName, tr2.RoomName AS ToRoomName " +
        "FROM AssetTransferItems ti " +
        "JOIN Assets a          ON ti.AssetId    = a.AssetId " +
        "JOIN AssetTransfers t  ON ti.TransferId = t.TransferId " +
        "LEFT JOIN Rooms fr     ON t.FromRoomId  = fr.RoomId " +
        "LEFT JOIN Rooms tr2    ON t.ToRoomId    = tr2.RoomId " +
        "WHERE a.AssetId IN (" + inClause + ") " +
        "ORDER BY a.AssetName, t.CreatedAt DESC";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(historySql)) {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int assetId = rs.getInt("AssetId");
            Transfer t = new Transfer();
            t.setTransferId(rs.getInt("TransferId"));   // ← THÊM
            t.setTransferCode(rs.getString("TransferCode"));
            t.setFromRoomName(rs.getString("FromRoomName"));
            t.setToRoomName(rs.getString("ToRoomName"));
            t.setCreatedAt(rs.getTimestamp("CreatedAt"));

            result.stream()
                  .filter(a -> a.getAssetId() == assetId)
                  .findFirst()
                  .ifPresent(a -> a.getTransfers().add(t));
        }
    }

    return result;
}
public int countDistinctAssets(String keyword, String fromDate, String toDate) throws SQLException {
    StringBuilder sb = new StringBuilder(
        "SELECT COUNT(DISTINCT a.AssetId) " +
        "FROM AssetTransferItems ti " +
        "JOIN Assets a ON ti.AssetId = a.AssetId " +
        "JOIN AssetTransfers t ON ti.TransferId = t.TransferId " +
        "WHERE 1=1"
    );
    List<Object> params = new ArrayList<>();
    if (keyword != null && !keyword.isEmpty()) {
        sb.append(" AND (a.AssetName LIKE ? OR a.AssetCode LIKE ? OR t.TransferCode LIKE ?)");
        params.add("%" + keyword + "%");
        params.add("%" + keyword + "%");
        params.add("%" + keyword + "%");
    }
    if (fromDate != null && !fromDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) >= ?");
        params.add(fromDate);
    }
    if (toDate != null && !toDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) <= ?");
        params.add(toDate);
    }
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sb.toString())) {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
}

}

