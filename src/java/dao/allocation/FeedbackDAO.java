package dao.allocation;

import dto.FeedbackDTO;
import util.DBUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeedbackDAO {

    public boolean existsByCreatorAndTarget(long createdById, String targetType, long targetId) throws SQLException {
        String sql = """
                     SELECT 1
                     FROM Feedbacks
                     WHERE CreatedById = ?
                       AND TargetType = ?
                       AND TargetId = ?
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, createdById);
            ps.setString(2, targetType);
            ps.setLong(3, targetId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insert(long createdById, String targetType, long targetId, String content) throws SQLException {
        String sql = """
                     INSERT INTO Feedbacks (CreatedById, TargetType, TargetId, Content, CreatedAt)
                     VALUES (?, ?, ?, ?, SYSDATETIME())
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, createdById);
            ps.setString(2, targetType);
            ps.setLong(3, targetId);
            ps.setString(4, content);
            return ps.executeUpdate() > 0;
        }
    }

    public Set<Long> getTargetIdsWithFeedback(long createdById, String targetType, List<Long> targetIds) throws SQLException {
        Set<Long> result = new HashSet<>();
        if (targetIds == null || targetIds.isEmpty()) {
            return result;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT TargetId FROM Feedbacks WHERE CreatedById = ? AND TargetType = ? AND TargetId IN (");

        for (int i = 0; i < targetIds.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(")");

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, createdById);
            ps.setString(idx++, targetType);
            for (Long targetId : targetIds) {
                ps.setLong(idx++, targetId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getLong("TargetId"));
                }
            }
        }

        return result;
    }

    public FeedbackDTO findByCreatorAndTarget(long createdById, String targetType, long targetId) throws SQLException {
        String sql = """
                     SELECT TOP 1 FeedbackId, CreatedById, TargetType, TargetId, Content, CreatedAt
                     FROM Feedbacks
                     WHERE CreatedById = ?
                       AND TargetType = ?
                       AND TargetId = ?
                     ORDER BY CreatedAt DESC
                     """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, createdById);
            ps.setString(2, targetType);
            ps.setLong(3, targetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                FeedbackDTO dto = new FeedbackDTO();
                dto.setFeedbackId(rs.getLong("FeedbackId"));
                dto.setCreatedById(rs.getLong("CreatedById"));
                dto.setTargetType(rs.getString("TargetType"));
                dto.setTargetId(rs.getLong("TargetId"));
                dto.setContent(rs.getString("Content"));
                if (rs.getTimestamp("CreatedAt") != null) {
                    dto.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                }
                return dto;
            }
        }
    }
}
