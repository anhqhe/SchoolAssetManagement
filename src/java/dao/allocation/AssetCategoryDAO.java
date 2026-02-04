/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import java.util.List;
import model.allocation.AssetCategory;
import util.DBUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Leo
 */
public class AssetCategoryDAO {

    public List<AssetCategory> getAllCategories() {
        List<AssetCategory> list = new ArrayList<>();
        String sql = "SELECT * FROM AssetCategories";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AssetCategory cat = new AssetCategory();
                cat.setCategoryId(rs.getLong("CategoryId"));
                cat.setCategoryCode(rs.getNString("CategoryCode"));
                cat.setCategoryName(rs.getNString("CategoryName"));

                // check null
                long parentId = rs.getLong("ParentCategoryId");
                cat.setParentCategoryId(rs.wasNull() ? null : parentId);

                cat.setIsActive(rs.getBoolean("IsActive"));
                list.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertCategory(AssetCategory cat) {
        String sql = "INSERT INTO AssetCategories (CategoryCode, CategoryName, ParentCategoryId, IsActive) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setNString(1, cat.getCategoryCode());
            ps.setNString(2, cat.getCategoryName());

            // check null
            if (cat.getParentCategoryId() != null) {
                ps.setLong(3, cat.getParentCategoryId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }

            ps.setBoolean(4, cat.isIsActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error-AssetCategoryDAO.insertCategory: " + e.getMessage());
            return false;
        }
    }

    public List<AssetCategory> getAllActiveCategories() {
        List<AssetCategory> activeList = new ArrayList<>();

        String sql = "SELECT CategoryId, CategoryCode, CategoryName, ParentCategoryId, IsActive "
                + "FROM AssetCategories "
                + "WHERE IsActive = 1";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AssetCategory cat = new AssetCategory();
                cat.setCategoryId(rs.getLong("CategoryId"));
                cat.setCategoryCode(rs.getNString("CategoryCode"));
                cat.setCategoryName(rs.getNString("CategoryName"));

                // check null
                long parentId = rs.getLong("ParentCategoryId");
                if (rs.wasNull()) {
                    cat.setParentCategoryId(null);
                } else {
                    cat.setParentCategoryId(parentId);
                }

                cat.setIsActive(rs.getBoolean("IsActive"));
                activeList.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("Error-AssetCategoryDAO.getAllActiveCategories: " + e.getMessage());
        }
        return activeList;
    }

}
