/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Leo
 */
public class AssetStatusHistoryDAO {

    public boolean insertStatusHistory(Connection conn,
            long assetId,
            String oldStatus,
            String newStatus,
            String reason,
            long changedBy,
            String type,
            Long oldRoomId,
            Long newRoomId) throws SQLException {

        String sql = """
                     INSERT INTO [dbo].[AssetStatusHistory]
                                ([AssetId]
                                ,[OldStatus]
                                ,[NewStatus]
                                ,[Reason]
                                ,[ChangedByUserId]
                                ,[ChangedAt]
                                ,[Type]
                                ,[OldRoomId]
                                ,[NewRoomId])
                          VALUES
                                (?,?,?,?,?,SYSDATETIME(),?,?,?)
                     """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, assetId);
            ps.setString(2, oldStatus);
            ps.setString(3, newStatus);    
            ps.setString(4, reason);      
            ps.setLong(5, changedBy);   
            ps.setString(6, type);
            if (oldRoomId != null) {
                ps.setLong(7, oldRoomId);
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }

            if (newRoomId != null) {
                ps.setLong(8, newRoomId);
            } else {
                ps.setNull(8, java.sql.Types.BIGINT);
            }
            
            return ps.executeUpdate() > 0;
        }
    }
}
