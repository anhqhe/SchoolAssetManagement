package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import util.DBUtil;

public class SystemConfigDAO {

    public static final String KEY_UI_PRIMARY_COLOR = "ui.primaryColor";
    public static final String KEY_UI_GLOBAL_BANNER_ENABLED = "ui.globalBanner.enabled";
    public static final String KEY_UI_GLOBAL_BANNER_TEXT = "ui.globalBanner.text";

    private static final Set<String> ALLOWED_KEYS;
    static {
        Set<String> keys = new HashSet<>();
        keys.add(KEY_UI_PRIMARY_COLOR);
        keys.add(KEY_UI_GLOBAL_BANNER_ENABLED);
        keys.add(KEY_UI_GLOBAL_BANNER_TEXT);
        ALLOWED_KEYS = Collections.unmodifiableSet(keys);
    }

    public boolean isAllowedKey(String key) {
        // Chỉ cho phép cập nhật các key đã whitelist để tránh lạm dụng bảng config lưu dữ liệu ngoài ý muốn
        return key != null && ALLOWED_KEYS.contains(key);
    }

    public Map<String, String> getAll() throws SQLException {
        // Lấy toàn bộ config (dùng cho trang settings và/hoặc UI include)
        String sql = "SELECT ConfigKey, ConfigValue FROM SystemConfigs";
        Map<String, String> map = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("ConfigKey"), rs.getString("ConfigValue"));
            }
        }
        return map;
    }

    public String get(String key) throws SQLException {
        if (!isAllowedKey(key)) {
            return null;
        }
        // Query theo key (PreparedStatement để tránh injection)
        String sql = "SELECT ConfigValue FROM SystemConfigs WHERE ConfigKey = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ConfigValue");
                }
            }
        }
        return null;
    }

    public void upsert(String key, String value, long updatedById) throws SQLException {
        if (!isAllowedKey(key)) {
            throw new SQLException("Invalid config key");
        }

        /*
         * Upsert theo 2 bước (update -> insert nếu không có):
         * - Đơn giản, dễ hiểu.
         * - Tuy nhiên hiện đang dùng 2 connection tách rời; nếu cần chắc chắn hơn dưới tải cao,
         *   nên dùng 1 transaction hoặc MERGE (SQL Server) để tránh race condition.
         */
        String updateSql = """
                           UPDATE SystemConfigs
                           SET ConfigValue = ?, UpdatedById = ?, UpdatedAt = SYSDATETIME()
                           WHERE ConfigKey = ?
                           """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, value);
            ps.setLong(2, updatedById);
            ps.setString(3, key);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                return;
            }
        }

        String insertSql = """
                           INSERT INTO SystemConfigs (ConfigKey, ConfigValue, UpdatedById, UpdatedAt)
                           VALUES (?, ?, ?, SYSDATETIME())
                           """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.setLong(3, updatedById);
            ps.executeUpdate();
        }
    }
}

