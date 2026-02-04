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

    public boolean insertStatusHistory(Connection conn, long assetId, String status, String reason, long changedBy) throws SQLException {

        String sql = """
                     INSERT INTO [dbo].[AssetStatusHistory]
                                ([AssetId]
                                ,[NewStatus]
                                ,[Reason]
                                ,[ChangedByUserId]
                                ,[ChangedAt])
                          VALUES
                                (?,?,?,?,SYSDATETIME())
                     """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, assetId);
            ps.setString(2, status);    // Ví dụ: 'IN_USE'
            ps.setString(3, reason);      // Ví dụ: 'Cấp phát theo phiếu yêu cầu'
            ps.setLong(4, changedBy);   // ID của Staff thực hiện
            return ps.executeUpdate() > 0;
        }
    }
}
