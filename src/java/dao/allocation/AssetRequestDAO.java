/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import dto.AssetRequestDTO;
import java.time.LocalDateTime;
import java.util.List;
import model.AssetRequest;
import util.DBUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Leo
 */
public class AssetRequestDAO {

    //save request, return requestId
    public long insert(Connection conn, AssetRequest req) throws SQLException {
        String sql = "INSERT INTO AssetRequests (RequestCode, TeacherId, RequestedRoomId, Purpose, Status, CreatedAt) "
                + "OUTPUT INSERTED.RequestId "
                + "VALUES (?, ?, ?, ?, ?, SYSDATETIME())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, req.getRequestCode());
            ps.setLong(2, req.getTeacherId());
            Long roomId = req.getRequestedRoomId();
            if (roomId == null) {
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(3, roomId);
            }
            ps.setString(4, req.getPurpose());
            ps.setString(5, req.getStatus());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    //update status
    public void updateStatus(Connection conn, Long requestId, String status) throws SQLException {
        String sql = "UPDATE AssetRequests "
                + "SET Status = ?, "
                + "UpdatedAt = SYSDATETIME() "
                + "WHERE RequestId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, requestId);
            ps.executeUpdate();
        }
    }

    public List<AssetRequest> getRequestsByTeacher(long userId) {
        List<AssetRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM AssetRequests WHERE TeacherId = ?";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AssetRequest req = mapResultSetToEntity(rs);

                list.add(req);
            }
        } catch (SQLException e) {
            System.out.println("Error: dao.allocation.AssetRequestDAO.getRequestsByTeacher(): " + e.getMessage());
        }
        return list;
    }

    public List<AssetRequest> getPendingRequests() {
        List<AssetRequest> pendingList = new ArrayList<>();

        // Select 'Pending' Request, Sort DESC CreatedAt
        String sql = "SELECT * FROM AssetRequests "
                + "WHERE Status = 'PENDING' "
                + "ORDER BY CreatedAt DESC";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AssetRequest req = mapResultSetToEntity(rs);

                pendingList.add(req);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách yêu cầu đang chờ duyệt: " + e.getMessage());
        }
        return pendingList;
    }

    // Map data from ResultSet to AssetRequest
    private AssetRequest mapResultSetToEntity(ResultSet rs) throws SQLException {
        AssetRequest req = new AssetRequest();
        req.setRequestId(rs.getLong("RequestId"));
        req.setRequestCode(rs.getNString("RequestCode"));
        req.setTeacherId(rs.getLong("TeacherId"));

        long roomId = rs.getLong("RequestedRoomId");
        req.setRequestedRoomId(rs.wasNull() ? null : roomId);

        req.setPurpose(rs.getNString("Purpose"));
        req.setStatus(rs.getNString("Status"));

        // Convert SQL Timestamp to Java LocalDateTime
        if (rs.getTimestamp("CreatedAt") != null) {
            req.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            req.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }
        return req;
    }

    //
    // AssetRequestDTO DAO
    //
    //Find Request By ID --> Show in request-detail.jsp
    public AssetRequestDTO findById(long requestId) throws SQLException {

        String sql = """
                    SELECT r.*, u.FullName as TeacherName, rm.RoomName 
                    FROM AssetRequests r 
                    JOIN Users u ON r.TeacherId = u.UserId 
                    LEFT JOIN Rooms rm ON r.RequestedRoomId = rm.RoomId
                    WHERE r.RequestId = ?
                    """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AssetRequestDTO dto = new AssetRequestDTO();
                    dto.setRequestId(rs.getLong("RequestId"));
                    dto.setRequestCode(rs.getString("RequestCode"));
                    dto.setTeacherId(rs.getLong("TeacherId"));
                    dto.setRequestedRoomId(rs.getLong("RequestedRoomId"));
                    dto.setPurpose(rs.getString("Purpose"));
                    dto.setStatus(rs.getString("Status"));
                    dto.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());

                    dto.setTeacherName(rs.getString("TeacherName"));
                    dto.setRoomName(rs.getString("RoomName"));
                    return dto;
                }
            }
        }
        return null;
    }

}
