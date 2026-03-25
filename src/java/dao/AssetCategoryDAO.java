package dao;

import model.AssetCategory;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AssetCategoryDAO {

    public List<AssetCategory> getAllCategories() throws SQLException {
        List<AssetCategory> categories = new ArrayList<>();
        String sql = "SELECT c.CategoryId, c.CategoryCode, c.CategoryName, c.ParentCategoryId, c.IsActive, " +
                     "p.CategoryName AS ParentCategoryName " +
                     "FROM AssetCategories c " +
                     "LEFT JOIN AssetCategories p ON c.ParentCategoryId = p.CategoryId " +
                     "ORDER BY c.CategoryName";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AssetCategory category = mapRow(rs);
                categories.add(category);
            }
        }

        return categories;
    }

    public AssetCategory getCategoryById(long categoryId) throws SQLException {
        String sql = "SELECT c.CategoryId, c.CategoryCode, c.CategoryName, c.ParentCategoryId, c.IsActive, " +
                     "p.CategoryName AS ParentCategoryName " +
                     "FROM AssetCategories c " +
                     "LEFT JOIN AssetCategories p ON c.ParentCategoryId = p.CategoryId " +
                     "WHERE c.CategoryId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public boolean createCategory(AssetCategory category) throws SQLException {
        String sql = "INSERT INTO AssetCategories (CategoryCode, CategoryName, ParentCategoryId, IsActive) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());

            if (category.getParentCategoryId() != null) {
                ps.setLong(3, category.getParentCategoryId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setBoolean(4, category.isActive());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCategory(AssetCategory category) throws SQLException {
        String sql = "UPDATE AssetCategories " +
                     "SET CategoryCode = ?, CategoryName = ?, ParentCategoryId = ?, IsActive = ? " +
                     "WHERE CategoryId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());

            if (category.getParentCategoryId() != null) {
                ps.setLong(3, category.getParentCategoryId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setBoolean(4, category.isActive());
            ps.setLong(5, category.getCategoryId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Đếm số tài sản đang thuộc danh mục này (bất kể trạng thái active/inactive của danh mục).
     * Dùng để validate trước khi xoá: không được xoá nếu còn tài sản.
     *
     * @param categoryId ID danh mục cần kiểm tra
     * @return số lượng tài sản thuộc danh mục
     */
    public int countAssetsByCategoryId(long categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Assets WHERE CategoryId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Xoá danh mục tài sản theo ID.
     * Lưu ý: nên gọi {@link #countAssetsByCategoryId(long)} trước để đảm bảo không còn tài sản.
     *
     * @param categoryId ID danh mục cần xoá
     * @return {@code true} nếu xoá thành công
     */
    public boolean deleteCategory(long categoryId) throws SQLException {
        String sql = "DELETE FROM AssetCategories WHERE CategoryId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);

            return ps.executeUpdate() > 0;
        }
    }

    private AssetCategory mapRow(ResultSet rs) throws SQLException {
        AssetCategory category = new AssetCategory();
        category.setCategoryId(rs.getLong("CategoryId"));
        category.setCategoryCode(rs.getString("CategoryCode"));
        category.setCategoryName(rs.getString("CategoryName"));

        long parentId = rs.getLong("ParentCategoryId");
        if (rs.wasNull()) {
            category.setParentCategoryId(null);
        } else {
            category.setParentCategoryId(parentId);
        }

        category.setActive(rs.getBoolean("IsActive"));
        category.setParentCategoryName(rs.getString("ParentCategoryName"));
        return category;
    }
}

