/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;


import dto.ApprovalDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import model.Approval;
import util.DBUtil;

/**
 *
 * @author Leo
 */
public class ApprovalDAO {

    public List<Approval> getPendingList() {
        List<Approval> list = List.of(
                new Approval(1L, "ASSET_REQUEST", 1001L, 101L, "APPROVED", "Checked and approved.", LocalDateTime.of(2025, 6, 1, 10, 30)),
                new Approval(2L, "ASSET_REQUEST", 2002L, 102L, "REJECTED", "Missing invoice attachments.", LocalDateTime.of(2025, 6, 2, 14, 15)),
                new Approval(3L, "ASSET_REQUEST", 3003L, 103L, "PENDING", "Waiting for budget confirmation.", LocalDateTime.of(2025, 6, 3, 9, 0)),
                new Approval(4L, "ASSET_TRANSFER", 4004L, 104L, "APPROVED", "Expense within limit.", LocalDateTime.of(2025, 6, 4, 16, 45)),
                new Approval(5L, "ASSET_TRANSFER", 5005L, 105L, "APPROVED", "Signed by legal.", LocalDateTime.of(2025, 6, 5, 11, 20))
        );
        //DEMO end
        return list;
    }

    public boolean processDecision(long refId, String refType, long userId, String decision, String note) {
        return true;
    }

    
    //Find Request by
    public ApprovalDTO findByRef(String refType, long refId) throws SQLException {
        String sql = "SELECT a.*, u.FullName as ApproverName " +
                     "FROM Approvals a " +
                     "JOIN Users u ON a.ApproverId = u.UserId " +
                     "WHERE a.RefType = ? AND a.RefId = ? " +
                     "ORDER BY a.DecidedAt DESC"; // newest

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
