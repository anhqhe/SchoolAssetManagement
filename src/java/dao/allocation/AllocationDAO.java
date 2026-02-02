/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.AssetAllocation;

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

}
