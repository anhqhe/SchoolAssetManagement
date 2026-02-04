/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.allocation;

import dto.AssetRequestDTO;
import java.util.List;
import model.allocation.AssetRequest;
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
/**
 *
 * DAO for AssetRequest and AssetRequestDTO
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
    public boolean updateStatus(Connection conn, long requestId, String status) throws SQLException {
        String sql = """
                     UPDATE AssetRequests 
                     SET Status = ?, UpdatedAt = SYSDATETIME() 
                     WHERE RequestId = ?
                     """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, requestId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

 
    /**
     *
     * AssetRequestDTO DAO
     */
    //Find Request By ID --> Show in request-detail, staff/allocate-asset
    public AssetRequestDTO findById(long requestId) {

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
                    AssetRequestDTO dto = mapResultSetToRequestDTO(rs);
                    return dto;
                }
            }
        } catch(SQLException e) {
            System.out.println("dao.allocation.AssetRequestDAO.findById()");
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    //view in teacher/request-list
    public List<AssetRequestDTO> getRequestsByTeacher(long userId, String keyword, String status, String sortBy) throws SQLException {
        
        String orderClause;
        if(sortBy ==null || sortBy.isEmpty()) {
            orderClause = "r.CreatedAt DESC"; //default
        } else {
            orderClause = switch (sortBy) {
                case "RequestCode ASC" -> "r.RequestCode ASC";
                case "RequestCode DESC" -> "r.RequestCode DESC";
                case "CreatedAt ASC" -> "r.CreatedAt ASC";
                case "CreatedAt DESC" -> "r.CreatedAt DESC";
                case "RoomName ASC" -> "rm.RoomName ASC";
                case "RoomName DESC" -> "rm.RoomName DESC";
                case "Status ASC" -> "r.Status ASC";
                case "Status DESC" -> "r.Status DESC";

                default -> "r.CreatedAt DESC";
            }; 
        }
        
        StringBuilder sql = new StringBuilder(
                """
                SELECT r.*, u.FullName AS TeacherName, rm.RoomName
                FROM AssetRequests r
                JOIN Users u ON r.TeacherId = u.UserId
                LEFT JOIN Rooms rm ON r.RequestedRoomId = rm.RoomId
                WHERE TeacherId = ? 
                                                                 """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                        AND (r.RequestCode LIKE ?
                            OR rm.RoomName LIKE ? )  
                       """);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.Status = ? ");
        }

        sql.append(" ORDER BY ").append(orderClause);
        
        List<AssetRequestDTO> list = new ArrayList<>();
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setLong(idx++, userId);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchVal = "%" + keyword.trim() + "%";
                ps.setString(idx++, searchVal);
                ps.setString(idx++, searchVal);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(idx++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssetRequestDTO dto = mapResultSetToRequestDTO(rs);

                    list.add(dto);
                }
            }
        }
        return list;
           
    }

    // not use
    public List<AssetRequestDTO> getRequestsForBoard() {
        List<AssetRequestDTO> list = new ArrayList<>();
        // query waiting_board request
        String sql = """
                 SELECT r.*, u.FullName AS TeacherName, rm.RoomName
                 FROM AssetRequests r 
                 JOIN Users u ON r.TeacherId = u.UserId 
                 LEFT JOIN Rooms rm ON r.RequestedRoomId = rm.RoomId 
                 WHERE r.Status = 'WAITING_BOARD' 
                 ORDER BY r.CreatedAt ASC
                 """;

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AssetRequestDTO dto = mapResultSetToRequestDTO(rs);

                list.add(dto);
            }
        } catch (Exception e) {
            System.err.println("dao.allocation.AssetRequestDAO.getPendingForBoard()");
            e.printStackTrace(System.err);
        }
        return list;
    }

    
    // not use
    public List<AssetRequestDTO> getRequestsForStaff() {
        List<AssetRequestDTO> list = new ArrayList<>();
        String sql = "SELECT r.*, u.FullName AS TeacherName, rm.RoomName "
                + "FROM AssetRequests r "
                + "JOIN Users u ON r.TeacherId = u.UserId "
                + "LEFT JOIN Rooms rm ON r.RequestedRoomId = rm.RoomId "
                + "WHERE r.Status = 'APPROVED_BY_BOARD' "
                + "ORDER BY r.CreatedAt ASC";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AssetRequestDTO dto = mapResultSetToRequestDTO(rs);

                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("dao.allocation.AssetRequestDAO.getRequestsForStaff()");
            System.err.println(e);
        }
        return list;
    }

    //Filter in staff/allocation-list, board/approval-center
    public List<AssetRequestDTO> getRequestsAdvanced(String keyword, String status, String sortBy) throws SQLException {
        
        String orderClause;
        if(sortBy ==null || sortBy.isEmpty()) {
            orderClause = "r.CreatedAt DESC"; //default
        } else {
            orderClause = switch (sortBy) {
                case "RequestCode ASC" -> "r.RequestCode ASC";
                case "RequestCode DESC" -> "r.RequestCode DESC";
                case "TeacherName ASC" -> "u.FullName ASC";
                case "TeacherName DESC" -> "u.FullName DESC";
                case "CreatedAt ASC" -> "r.CreatedAt ASC";
                case "CreatedAt DESC" -> "r.CreatedAt DESC";
                case "RoomName ASC" -> "rm.RoomName ASC";
                case "RoomName DESC" -> "rm.RoomName DESC";
                case "Status ASC" -> "r.Status ASC";
                case "Status DESC" -> "r.Status DESC";

                default -> "r.CreatedAt DESC";
            }; 
        }
        
        StringBuilder sql = new StringBuilder(
                """
                SELECT r.*, u.FullName AS TeacherName, rm.RoomName
                FROM AssetRequests r
                JOIN Users u ON r.TeacherId = u.UserId 
                LEFT JOIN Rooms rm ON r.RequestedRoomId = rm.RoomId
                WHERE 1=1
                                                                 """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                        AND (r.RequestCode LIKE ? 
                            OR u.FullName LIKE ? 
                            OR rm.RoomName LIKE ?
                            OR r.Purpose LIKE ? )  
                       """);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.Status = ? ");
        }

        sql.append(" ORDER BY ").append(orderClause);

        List<AssetRequestDTO> list = new ArrayList<>();
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql.toString())) {

            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchVal = "%" + keyword.trim() + "%";
                ps.setString(idx++, searchVal);
                ps.setString(idx++, searchVal);
                ps.setString(idx++, searchVal);
                ps.setString(idx++, searchVal);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(idx++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssetRequestDTO dto = mapResultSetToRequestDTO(rs);

                    list.add(dto);
                }
            }
        }
        return list;
    }

    /*
    *
    * Map data from ResultSet to AssetRequest, AssetRequestDTO
     */
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

    // Map data from ResultSet to AssetRequestDTO
    private AssetRequestDTO mapResultSetToRequestDTO(ResultSet rs) throws SQLException {
        AssetRequestDTO req = new AssetRequestDTO();
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

        req.setTeacherName(rs.getString("TeacherName"));
        req.setRoomName(rs.getString("RoomName"));

        return req;
    }

}
