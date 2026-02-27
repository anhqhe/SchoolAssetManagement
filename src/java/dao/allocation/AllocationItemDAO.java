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
public class AllocationItemDAO {

    public boolean insertAllocationItem(Connection conn, long allocationId, Long assetId) throws SQLException {
        String sql = "INSERT INTO AssetAllocationItems (AllocationId, AssetId) VALUES (?, ?)";
    
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, allocationId);
        ps.setLong(2, assetId);
        
        return ps.executeUpdate() > 0;
    }
    }
    
}
