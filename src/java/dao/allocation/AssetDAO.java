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
import model.allocation.Asset;
import util.DBUtil;

/**
 *
 * @author Leo
 */

/*
*
* Asset & AssetDTO DAO
*/

public class AssetDAO {
    
    public boolean updateAssetStatus(Connection conn, long assetId, String status) throws SQLException {
        String sql = "UPDATE Assets SET Status = ? WHERE AssetId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, assetId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /*
    *
    * AssetDTO DAO
    */
    public List<AssetDTO> findAvailableAssets() {
        List<AssetDTO> list = new ArrayList<>();

        String sql = "SELECT a.*, c.CategoryName "
                + "FROM Assets a "
                + "JOIN AssetCategories c ON a.CategoryId = c.CategoryId "
                + "WHERE a.Status = 'IN_STOCK'";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AssetDTO a = new AssetDTO();
                a.setAssetId(rs.getLong("AssetId"));
                a.setAssetCode(rs.getString("AssetCode"));
                a.setAssetName(rs.getString("AssetName"));
                a.setCategoryId(rs.getLong("CategoryId"));
                a.setCategoryName(rs.getString("CategoryName"));
                a.setStatus(rs.getString("Status"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("dao.allocation.AssetDAO.findAvailableAssets()");
            System.err.println(e);
        }
        return list;
    }

    

}
