/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import dto.AssetDTO;
import dto.AllocationHistoryDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.allocation.AssetAllocation;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class AllocationDAO {

    public long insertAllocation(Connection conn, AssetAllocation allocation) throws SQLException {

        String sql = """
                     INSERT INTO [dbo].[AssetAllocations]
                                ([AllocationCode]
                                ,[RequestId]
                                ,[ToRoomId]
                                ,[ReceiverId]
                                ,[AllocatedById]
                                ,[Status]
                                ,[Note]
                                ,[AllocatedAt])
                     OUTPUT INSERTED.AllocationId
                     VALUES
                          (?,?,?,?,?,?,?,SYSDATETIME())
                     """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, allocation.getAllocationCode());
            ps.setLong(2, allocation.getRequestId());
            ps.setLong(3, allocation.getToRoomId());
            ps.setLong(4, allocation.getReceiverId());
            ps.setLong(5, allocation.getAllocatedById());
            ps.setString(6, "ACTIVE");  //ACTIVE: allocate, RETURNED: deallocate
            ps.setString(7, allocation.getNote());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1;
    }

    public List<AssetDTO> getAllocatedAssetsByRequestId(long requestId) throws SQLException {
        List<AssetDTO> assets = new ArrayList<>();
        String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, c.CategoryName, a.Status, a.CurrentHolderId "
                + "FROM AssetAllocations aa "
                + "JOIN AssetAllocationItems aai ON aa.AllocationId = aai.AllocationId "
                + "JOIN Assets a ON aai.AssetId = a.AssetId "
                + "JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "WHERE aa.RequestId = ?";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssetDTO asset = new AssetDTO();
                    asset.setAssetId(rs.getLong("AssetId"));
                    asset.setAssetCode(rs.getString("AssetCode"));
                    asset.setAssetName(rs.getString("AssetName"));
                    asset.setCategoryName(rs.getString("CategoryName"));
                    asset.setStatus(rs.getString("Status"));
                    asset.setCurrentHolderId(rs.getLong("CurrentHolderId"));
                    assets.add(asset);
                }
            }
            return assets;
        }
    }

    public AssetAllocation getAllocationByRequestId(long requestId) throws SQLException {
        String sql = """
                     SELECT TOP 1 AllocationId, 
                            AllocationCode, 
                            RequestId, 
                            FromRoomId, 
                            ToRoomId, 
                            ReceiverId, 
                            AllocatedById, 
                            Status, 
                            Note, 
                            AllocatedAt 
                     FROM AssetAllocations 
                     WHERE RequestId = ? 
                     ORDER BY AllocatedAt DESC
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.allocation.AssetAllocation alloc = new model.allocation.AssetAllocation();
                    alloc.setAllocationId(rs.getLong("AllocationId"));
                    alloc.setAllocationCode(rs.getString("AllocationCode"));
                    alloc.setRequestId(rs.getLong("RequestId"));
                    alloc.setFromRoomId(rs.getLong("FromRoomId"));
                    alloc.setToRoomId(rs.getLong("ToRoomId"));
                    alloc.setReceiverId(rs.getLong("ReceiverId"));
                    alloc.setAllocatedById(rs.getLong("AllocatedById"));
                    alloc.setStatus(rs.getString("Status"));
                    alloc.setNote(rs.getString("Note"));
                    alloc.setAllocatedAt(rs.getTimestamp("AllocatedAt").toLocalDateTime());
                    return alloc;
                }
            }
        }
        return null;
    }

    public List<AssetAllocation> getAllocationsByRequestId(long requestId) throws SQLException {
        List<AssetAllocation> list = new ArrayList<>();
        String sql = """
                     SELECT AllocationId, 
                            AllocationCode, 
                            RequestId, 
                            FromRoomId, 
                            ToRoomId, 
                            ReceiverId, 
                            AllocatedById, 
                            Status, 
                            Note, 
                            AllocatedAt 
                     FROM AssetAllocations 
                     WHERE RequestId = ?
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.allocation.AssetAllocation alloc = new model.allocation.AssetAllocation();
                    alloc.setAllocationId(rs.getLong("AllocationId"));
                    alloc.setAllocationCode(rs.getString("AllocationCode"));
                    alloc.setRequestId(rs.getLong("RequestId"));
                    alloc.setFromRoomId(rs.getLong("FromRoomId"));
                    alloc.setToRoomId(rs.getLong("ToRoomId"));
                    alloc.setReceiverId(rs.getLong("ReceiverId"));
                    alloc.setAllocatedById(rs.getLong("AllocatedById"));
                    alloc.setStatus(rs.getString("Status"));
                    alloc.setNote(rs.getString("Note"));
                    alloc.setAllocatedAt(rs.getTimestamp("AllocatedAt").toLocalDateTime());

                    list.add(alloc);
                }
            }
        }
        return list;
    }

    //Get user allocate each asset
    public String getAllocatedBy(long assetId) throws Exception {

        String sql = """
                    SELECT u.FullName
                    FROM AssetAllocationItems aai
                    JOIN AssetAllocations aa ON aai.AllocationId = aa.AllocationId
                    JOIN Users u ON aa.AllocatedById = u.UserId
                    WHERE aai.AssetId = ?
                    ORDER BY aa.AllocatedAt DESC
                                                """;

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, assetId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("FullName");
            }
        }

        return null;
    }

    public List<AllocationHistoryDTO> getAllocationHistory(
            String keyword,
            Long fromRoomId,
            Long toRoomId,
            String status,
            java.time.LocalDate dateFrom,
            java.time.LocalDate dateTo,
            Long receiverIdFilter
    ) throws SQLException {
        List<AllocationHistoryDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT 
                        aa.AllocationId,
                        aa.AllocationCode,
                        ar.RequestCode,
                        fr.RoomName AS FromRoomName,
                        tr.RoomName AS ToRoomName,
                        urecv.FullName AS ReceiverName,
                        ualloc.FullName AS AllocatedByName,
                        aa.Status,
                        aa.AllocatedAt,
                        STRING_AGG(a.AssetName, ', ') AS AssetNames
                    FROM AssetAllocations aa
                    LEFT JOIN AssetRequests ar ON aa.RequestId = ar.RequestId
                    LEFT JOIN Rooms fr ON aa.FromRoomId = fr.RoomId
                    LEFT JOIN Rooms tr ON aa.ToRoomId = tr.RoomId
                    LEFT JOIN Users urecv ON aa.ReceiverId = urecv.UserId
                    LEFT JOIN Users ualloc ON aa.AllocatedById = ualloc.UserId
                    LEFT JOIN AssetAllocationItems aai ON aa.AllocationId = aai.AllocationId
                    LEFT JOIN Assets a ON aai.AssetId = a.AssetId
                    WHERE 1=1
                """);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND aa.Status = ? ");
        }
        if (fromRoomId != null && fromRoomId > 0) {
            sql.append(" AND aa.FromRoomId = ? ");
        }
        if (toRoomId != null && toRoomId > 0) {
            sql.append(" AND aa.ToRoomId = ? ");
        }
        if (dateFrom != null) {
            sql.append(" AND aa.AllocatedAt >= ? ");
        }
        if (dateTo != null) {
            sql.append(" AND aa.AllocatedAt < ? ");
        }
        if (receiverIdFilter != null && receiverIdFilter > 0) {
            sql.append(" AND aa.ReceiverId = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                       AND (
                            aa.AllocationCode LIKE ?
                            OR ar.RequestCode LIKE ?
                            OR fr.RoomName LIKE ?
                            OR tr.RoomName LIKE ?
                            OR urecv.FullName LIKE ?
                            OR urecv.Username LIKE ?
                            OR ualloc.FullName LIKE ?
                            OR ualloc.Username LIKE ?
                            OR EXISTS (
                                SELECT 1
                                FROM AssetAllocationItems aai2
                                JOIN Assets a2 ON aai2.AssetId = a2.AssetId
                                WHERE aai2.AllocationId = aa.AllocationId
                                  AND (a2.AssetName LIKE ? OR a2.AssetCode LIKE ? OR a2.SerialNumber LIKE ?)
                            )
                       )
                       """);
        }

        sql.append("""
                    GROUP BY 
                        aa.AllocationId,
                        aa.AllocationCode,
                        ar.RequestCode,
                        fr.RoomName,
                        tr.RoomName,
                        urecv.FullName,
                        ualloc.FullName,
                        aa.Status,
                        aa.AllocatedAt
                    ORDER BY aa.AllocatedAt DESC
                """);

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status.trim());
            }
            if (fromRoomId != null && fromRoomId > 0) {
                ps.setLong(idx++, fromRoomId);
            }
            if (toRoomId != null && toRoomId > 0) {
                ps.setLong(idx++, toRoomId);
            }
            if (dateFrom != null) {
                java.time.LocalDateTime fromDt = dateFrom.atStartOfDay();
                ps.setTimestamp(idx++, java.sql.Timestamp.valueOf(fromDt));
            }
            if (dateTo != null) {
                java.time.LocalDateTime toExclusive = dateTo.plusDays(1).atStartOfDay();
                ps.setTimestamp(idx++, java.sql.Timestamp.valueOf(toExclusive));
            }
            if (receiverIdFilter != null && receiverIdFilter > 0) {
                ps.setLong(idx++, receiverIdFilter);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like); // AllocationCode
                ps.setString(idx++, like); // RequestCode
                ps.setString(idx++, like); // FromRoomName
                ps.setString(idx++, like); // ToRoomName
                ps.setString(idx++, like); // Receiver FullName
                ps.setString(idx++, like); // Receiver Username
                ps.setString(idx++, like); // AllocatedBy FullName
                ps.setString(idx++, like); // AllocatedBy Username
                ps.setString(idx++, like); // AssetName
                ps.setString(idx++, like); // AssetCode
                ps.setString(idx++, like); // SerialNumber
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AllocationHistoryDTO dto = new AllocationHistoryDTO();
                    dto.setAllocationId(rs.getLong("AllocationId"));
                    dto.setAllocationCode(rs.getString("AllocationCode"));
                    dto.setRequestCode(rs.getString("RequestCode"));
                    dto.setFromRoomName(rs.getString("FromRoomName"));
                    dto.setToRoomName(rs.getString("ToRoomName"));
                    dto.setReceiverName(rs.getString("ReceiverName"));
                    dto.setAllocatedByName(rs.getString("AllocatedByName"));
                    dto.setStatus(rs.getString("Status"));
                    if (rs.getTimestamp("AllocatedAt") != null) {
                        dto.setAllocatedAt(rs.getTimestamp("AllocatedAt").toLocalDateTime());
                    }
                    dto.setAssetNames(rs.getString("AssetNames"));
                    list.add(dto);
                }
            }
        }

        return list;
    }

    public AllocationHistoryDTO getAllocationDetail(long allocationId, Long receiverIdFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                    SELECT 
                        aa.AllocationId,
                        aa.AllocationCode,
                        ar.RequestCode,
                        fr.RoomName AS FromRoomName,
                        tr.RoomName AS ToRoomName,
                        urecv.FullName AS ReceiverName,
                        ualloc.FullName AS AllocatedByName,
                        aa.Status,
                        aa.AllocatedAt
                    FROM AssetAllocations aa
                    LEFT JOIN AssetRequests ar ON aa.RequestId = ar.RequestId
                    LEFT JOIN Rooms fr ON aa.FromRoomId = fr.RoomId
                    LEFT JOIN Rooms tr ON aa.ToRoomId = tr.RoomId
                    LEFT JOIN Users urecv ON aa.ReceiverId = urecv.UserId
                    LEFT JOIN Users ualloc ON aa.AllocatedById = ualloc.UserId
                    WHERE aa.AllocationId = ?
                """);

        if (receiverIdFilter != null && receiverIdFilter > 0) {
            sql.append(" AND aa.ReceiverId = ? ");
        }

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql.toString())) {
            ps.setLong(1, allocationId);
            if (receiverIdFilter != null && receiverIdFilter > 0) {
                ps.setLong(2, receiverIdFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AllocationHistoryDTO dto = new AllocationHistoryDTO();
                    dto.setAllocationId(rs.getLong("AllocationId"));
                    dto.setAllocationCode(rs.getString("AllocationCode"));
                    dto.setRequestCode(rs.getString("RequestCode"));
                    dto.setFromRoomName(rs.getString("FromRoomName"));
                    dto.setToRoomName(rs.getString("ToRoomName"));
                    dto.setReceiverName(rs.getString("ReceiverName"));
                    dto.setAllocatedByName(rs.getString("AllocatedByName"));
                    dto.setStatus(rs.getString("Status"));
                    if (rs.getTimestamp("AllocatedAt") != null) {
                        dto.setAllocatedAt(rs.getTimestamp("AllocatedAt").toLocalDateTime());
                    }
                    return dto;
                }
            }
        }
        return null;
    }
}
