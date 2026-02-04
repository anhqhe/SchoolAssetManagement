/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import dto.AssetDTO;
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
    String sql = "SELECT a.AssetId, a.AssetCode, a.AssetName, c.CategoryName " +
                 "FROM AssetAllocations aa " +
                 "JOIN AssetAllocationItems aai ON aa.AllocationId = aai.AllocationId " +
                 "JOIN Assets a ON aai.AssetId = a.AssetId " +
                 "JOIN AssetCategories c ON a.CategoryId = c.CategoryId " +
                 "WHERE aa.RequestId = ?";
    
    try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
        ps.setLong(1, requestId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AssetDTO asset = new AssetDTO();
                asset.setAssetId(rs.getLong("AssetId"));
                asset.setAssetCode(rs.getString("AssetCode"));
                asset.setAssetName(rs.getString("AssetName"));
                asset.setCategoryName(rs.getString("CategoryName"));
                assets.add(asset);
            }
        }
    }
    return assets;
}
}
