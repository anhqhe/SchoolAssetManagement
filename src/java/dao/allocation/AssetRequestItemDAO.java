/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import dto.AssetRequestItemDTO;
import model.allocation.AssetRequestItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class AssetRequestItemDAO {
    
    
    public void insert(Connection conn, AssetRequestItem item) throws SQLException {
        String sql = "INSERT INTO AssetRequestItems (RequestId, CategoryId, AssetNameHint, Quantity, Note) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getRequestId());
            if (item.getCategoryId() != null) ps.setLong(2, item.getCategoryId());
            else ps.setNull(2, java.sql.Types.BIGINT);
            ps.setString(3, item.getAssetNameHint());
            ps.setInt(4, item.getQuantity());
            ps.setString(5, item.getNote());

            ps.executeUpdate();
        }
    }

    //Delete RequestItem --> use for teacher update request
    public boolean deleteByRequestId(Connection conn, long requestId) throws SQLException {
        String sql = "DELETE FROM AssetRequestItems WHERE RequestId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            return ps.executeUpdate() >= 0;
        }
    }
    
    //
    // AssetRequestDTO DAO
    //
    
    //Find By RequestId --> show in teacher/request-detail.jsp, staff/allocate-asset
    public List<AssetRequestItemDTO> findByRequestId(Long requestId) throws SQLException {
        List<AssetRequestItemDTO> items = new ArrayList<>();
        String sql = """
                    SELECT i.*,c.CategoryName
                    FROM AssetRequestItems  i
                    JOIN AssetCategories c on i.CategoryId = c.CategoryId
                    WHERE RequestId = ?
                     """;
        
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, requestId);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                //check null
                Long categoryId = null;
                long tmpCatId = rs.getLong("CategoryId");
                if (!rs.wasNull()) {
                    categoryId = tmpCatId;
                }
                
                AssetRequestItemDTO dto = new AssetRequestItemDTO(
                        rs.getLong("RequestItemId"), 
                        rs.getLong("RequestId"), 
                                categoryId, 
                        rs.getString("AssetNameHint"), 
                        rs.getInt("Quantity"), 
                        rs.getString("Note"),
                rs.getString("CategoryName"));
                
                items.add(dto);
            }           
        }
        return items;
    }

 
}
