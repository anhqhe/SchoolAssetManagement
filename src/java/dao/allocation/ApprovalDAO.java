/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import dto.ApprovalDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Approval;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class ApprovalDAO {

    public boolean insertApproval(Connection conn, String refType, long refId, long approverId, String decision, String note) throws SQLException {
        String sql = """
                     INSERT INTO Approvals (RefType, RefId, ApproverId, Decision, DecisionNote, DecidedAt)
                        VALUES (?, ?, ?, ?, ?, SYSDATETIME())
                     """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, refType);
            ps.setLong(2, refId);
            ps.setLong(3, approverId);
            ps.setString(4, decision);
            ps.setString(5, note);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; 
        }
    }

    //
    // ApprovalDTO DAO
    //
    //Find Request by
    public ApprovalDTO findByRef(String refType, long refId) throws SQLException {
        String sql = "SELECT a.*, u.FullName as ApproverName "
                + "FROM Approvals a "
                + "JOIN Users u ON a.ApproverId = u.UserId "
                + "WHERE a.RefType = ? AND a.RefId = ? "
                + "ORDER BY a.DecidedAt DESC"; // newest

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, refType);
            ps.setLong(2, refId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ApprovalDTO dto = new ApprovalDTO();
                    dto.setApprovalId(rs.getLong("ApprovalId"));
                    dto.setRefType(rs.getString("RefType"));
                    dto.setRefId(rs.getLong("RefId"));
                    dto.setApproverId(rs.getLong("ApproverId"));
                    dto.setDecision(rs.getString("Decision"));
                    dto.setDecisionNote(rs.getString("DecisionNote"));
                    dto.setDecidedAt(rs.getTimestamp("DecidedAt").toLocalDateTime());

                    dto.setApproverName(rs.getString("ApproverName"));
                    return dto;
                }
            }
        }
        return null;
    }

}
