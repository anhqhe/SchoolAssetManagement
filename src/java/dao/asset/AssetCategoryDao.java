/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.asset;
import model.asset.AssetCategory;
import util.DBUtil;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author An
 */
public class AssetCategoryDao {
    public List<AssetCategory> findAllActive() throws SQLException {
        String sql = "SELECT CategoryId, CategoryCode, CategoryName " +
                     "FROM AssetCategories WHERE IsActive = 1 ORDER BY CategoryName";
        List<AssetCategory> list = new ArrayList<>();
        try(Connection connect = DBUtil.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                AssetCategory ac = new AssetCategory();
                ac.setCategoryId(rs.getLong("CategoryId"));
                ac.setCategoryCode(rs.getString("CategoryCode"));
                ac.setCategoryName(rs.getString("CategoryName"));
                list.add(ac);
            }
            return list;
        }
}
}
