/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class AllocationItemDAO {

    public boolean insertAllocationItem(Connection conn, long allocationId, Long assetId) throws SQLException {
        String sql = "INSERT INTO AssetAllocationItems (AllocationId, AssetId) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, allocationId);
            ps.setLong(2, assetId);

            return ps.executeUpdate() > 0;
        }
    }

    //use for check quantity allocated assets
    public int countAllocatedByCategory(long requestId, long categoryId) throws Exception {
        String sql = """
                    SELECT COUNT(ai.AssetId)
                    FROM AssetAllocations a
                    JOIN AssetAllocationItems ai 
                        ON a.AllocationId = ai.AllocationId
                    JOIN Assets s 
                        ON ai.AssetId = s.AssetId
                    WHERE a.RequestId = ?
                    AND s.CategoryId = ?
                                        """;

        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, requestId);
            ps.setLong(2, categoryId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public int countAllocatedByCategory(Connection conn, long requestId, Long categoryId) throws SQLException {
        String sql = """
                    SELECT COUNT(ai.AssetId)
                    FROM AssetAllocations a
                    JOIN AssetAllocationItems ai 
                        ON a.AllocationId = ai.AllocationId
                    JOIN Assets s 
                        ON ai.AssetId = s.AssetId
                    WHERE a.RequestId = ?
                    AND s.CategoryId = ?
                                        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, requestId);
            ps.setLong(2, categoryId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

}
