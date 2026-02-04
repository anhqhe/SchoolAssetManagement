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
        String sql = "SELECT * FROM Assets";
        List<Asset> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asset a = new Asset();
                a.setAssetId(rs.getInt("AssetId"));
                a.setAssetCode(rs.getString("AssetCode"));
                a.setAssetName(rs.getString("AssetName"));
                a.setCategoryId(rs.getInt("CategoryId"));
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
                int roomId = rs.getInt("CurrentRoomId");
                if (rs.wasNull()) {
                    a.setCurrentRoomId(0); // 0 là chưa có phòng
                } else {
                    a.setCurrentRoomId(roomId);
                }

                int holderId = rs.getInt("CurrentHolderId");
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
            ps.setInt(3, a.getCategoryId());
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
                ps.setInt(12, a.getCurrentRoomId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            //CurrentHolderId
            if (a.getCurrentHolderId() != 0) {
                ps.setInt(13, a.getCurrentHolderId());
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
            ps.setInt(3, a.getCategoryId());
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
                ps.setInt(12, a.getCurrentRoomId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            //CurrentHolderId
            if (a.getCurrentHolderId() != 0) {
                ps.setInt(13, a.getCurrentHolderId());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.setBoolean(14, a.isIsActive());
            ps.setInt(15, a.getAssetId());
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

    public Asset findById(int id) throws SQLException {
        String sql = "SELECT AssetId, AssetCode, AssetName, CategoryId, SerialNumber, Model, Brand, "
                + "OriginNote, PurchaseDate, ReceivedDate, ConditionNote, Status, "
                + "CurrentRoomId, CurrentHolderId, IsActive, CreatedAt, UpdatedAt "
                + "FROM Assets WHERE AssetId = ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Asset a = new Asset();
                    a.setAssetId(rs.getInt("AssetId"));
                    a.setAssetCode(rs.getString("AssetCode"));
                    a.setAssetName(rs.getString("AssetName"));
                    a.setCategoryId(rs.getInt("CategoryId"));
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
                    int roomId = rs.getInt("CurrentRoomId");
                    if (rs.wasNull()) {
                        a.setCurrentRoomId(0); // 0 là chưa có phòng
                    } else {
                        a.setCurrentRoomId(roomId);
                    }

                    int holderId = rs.getInt("CurrentHolderId");
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
                    return a;
                }
            }
        }
        return null;
    }
}
