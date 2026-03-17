package dao.allocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.allocation.AssetRequestFeedback;
import util.DBUtil;

public class AssetRequestFeedbackDAO {

    public AssetRequestFeedback findByRequestId(long requestId) throws SQLException {
        String sql = """
                     SELECT TOP 1 *
                     FROM Feedbacks
                     WHERE TargetType = 'ASSET_REQUEST' AND TargetId = ?
                     ORDER BY CreatedAt DESC
                     """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * 1 feedback / 1 request: nếu đã có thì UPDATE, chưa có thì INSERT.
     */
    public void upsert(long requestId, long teacherId, Integer rating, String comment) throws SQLException {
        String updateSql = """
                           UPDATE Feedbacks
                           SET Content = ?
                           WHERE CreatedById = ? AND TargetType = 'ASSET_REQUEST' AND TargetId = ?
                           """;
        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, comment);
                ps.setLong(2, teacherId);
                ps.setLong(3, requestId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    return;
                }
            }

            String insertSql = """
                               INSERT INTO Feedbacks (CreatedById, TargetType, TargetId, Content, CreatedAt)
                               VALUES (?, 'ASSET_REQUEST', ?, ?, SYSDATETIME())
                               """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, teacherId);
                ps.setLong(2, requestId);
                ps.setString(3, comment);
                ps.executeUpdate();
            }
        }
    }

    private AssetRequestFeedback map(ResultSet rs) throws SQLException {
        AssetRequestFeedback f = new AssetRequestFeedback();
        f.setFeedbackId(rs.getLong("FeedbackId"));
        f.setTeacherId(rs.getLong("CreatedById"));
        f.setRequestId(rs.getLong("TargetId"));
        f.setComment(rs.getString("Content"));
        if (rs.getTimestamp("CreatedAt") != null) {
            f.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        }
        return f;
    }
}

