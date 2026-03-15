package dao;

import java.sql.*;
import java.util.*;
import model.Transfer;
import util.DBUtil;

public class TransferDAO {


public List<Transfer> getTransfers(String keyword, String status) throws SQLException {
String sql = " SELECT \n" +
"            t.TransferId, t.TransferCode,\n" +
"            t.RequestedById, t.FromRoomId, t.ToRoomId,\n" +
"            t.Status, t.Reason,\n" +
"            t.CreatedAt,\n" +
"            u.FullName AS RequestedByName,\n" +
"            fr.RoomName AS FromRoomName,\n" +
"            tr2.RoomName AS ToRoomName,\n" +
"            STRING_AGG(\n" +
"                a.AssetName +\n" +
"                CASE \n" +
"                    WHEN ti.Note IS NOT NULL AND LTRIM(RTRIM(ti.Note)) <> ''\n" +
"                    THEN ' (' + ti.Note + ')'\n" +
"                    ELSE ''\n" +
"                END,\n" +
"                ', '\n" +
"            ) AS AssetNames\n" +
"        FROM AssetTransfers t\n" +
"        LEFT JOIN Users u ON t.RequestedById = u.UserId\n" +
"        LEFT JOIN Rooms fr ON t.FromRoomId = fr.RoomId\n" +
"        LEFT JOIN Rooms tr2 ON t.ToRoomId = tr2.RoomId\n" +
"        LEFT JOIN AssetTransferItems ti ON t.TransferId = ti.TransferId\n" +
"        LEFT JOIN Assets a ON ti.AssetId = a.AssetId\n" +
"        WHERE 1=1";

    StringBuilder sb = new StringBuilder(sql);
    List<Object> params = new ArrayList<>();

    if (keyword != null && !keyword.trim().isEmpty()) {
        sb.append(" AND (t.TransferCode LIKE ? OR t.Reason LIKE ?)");
        params.add("%" + keyword + "%");
        params.add("%" + keyword + "%");
    }
    if (status != null && !status.trim().isEmpty()) {
        sb.append(" AND t.Status = ?");
        params.add(status);
    }

    sb.append(" GROUP BY t.TransferId, t.TransferCode, t.RequestedById, t.FromRoomId, t.ToRoomId, " +
              "t.Status, t.Reason, t.CreatedAt, u.FullName, fr.RoomName, tr2.RoomName");
    sb.append(" ORDER BY t.CreatedAt DESC");

    List<Transfer> list = new ArrayList<>();
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sb.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Transfer t = new Transfer();
            t.setTransferId((int) rs.getLong("TransferId"));
            t.setTransferCode(rs.getString("TransferCode"));
            t.setFromRoomName(rs.getString("FromRoomName"));
            t.setToRoomName(rs.getString("ToRoomName"));
            t.setRequestedByName(rs.getString("RequestedByName"));
            t.setReason(rs.getString("Reason"));
            t.setStatus(rs.getString("Status"));
            t.setCreatedAt(rs.getTimestamp("CreatedAt")); 
            t.setAssetNames(rs.getString("AssetNames"));
            list.add(t);
        }
    }
    return list;
}
 public Transfer getTransferById(int id) throws SQLException {
        String sql = "SELECT t.TransferId, t.TransferCode, t.Reason, t.Status, t.CreatedAt, " +
                     "fr.RoomName AS FromRoomName, " +
                     "tr.RoomName AS ToRoomName, " +
                     "u.FullName AS RequestedByName " +
                     "FROM AssetTransfers t " +
                     "LEFT JOIN Rooms fr ON t.FromRoomId = fr.RoomId " +
                     "LEFT JOIN Rooms tr ON t.ToRoomId = tr.RoomId " +
                     "LEFT JOIN Users u ON t.RequestedById = u.UserId " +
                     "WHERE t.TransferId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransfer(rs);
                }
            }
        }
        return null;
    }

    private Transfer mapResultSetToTransfer(ResultSet rs) throws SQLException {
        Transfer t = new Transfer();
        t.setTransferId(rs.getInt("TransferId"));
        t.setTransferCode(rs.getString("TransferCode"));
        t.setFromRoomName(rs.getString("FromRoomName"));
        t.setToRoomName(rs.getString("ToRoomName"));
        t.setRequestedByName(rs.getString("RequestedByName"));
        t.setReason(rs.getString("Reason"));
        t.setStatus(rs.getString("Status"));
        t.setCreatedAt(rs.getTimestamp("CreatedAt"));


        if (t.getStatus() != null) {
            switch (t.getStatus().toUpperCase()) {
                case "PENDING":
                    t.setStatusText("Chờ duyệt");
                    t.setStatusBadgeClass("badge badge-warning");
                    break;
                case "APPROVED":
                    t.setStatusText("Đã duyệt");
                    t.setStatusBadgeClass("badge badge-success");
                    break;
                case "REJECTED":
                    t.setStatusText("Từ chối");
                    t.setStatusBadgeClass("badge badge-danger");
                    break;
                case "COMPLETED":
                    t.setStatusText("Hoàn tất");
                    t.setStatusBadgeClass("badge badge-primary");
                    break;
                default:
                    t.setStatusText(t.getStatus());
                    t.setStatusBadgeClass("badge badge-secondary");
            }
        }
        return t;
    }

public boolean insertTransferWithItems(Transfer t, Map<Integer, String> assetNoteMap) throws SQLException {
    String sqlTransfer = "INSERT INTO AssetTransfers (TransferCode, RequestedById, FromRoomId, ToRoomId, " +
                         "Status, Reason, CreatedAt) " +
                         "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    String sqlItem = "INSERT INTO AssetTransferItems (TransferId, AssetId, Note) VALUES (?, ?, ?)";

    Connection conn = null;
    try {
        conn = DBUtil.getConnection();
        conn.setAutoCommit(false);

        long transferId;
        try (PreparedStatement ps = conn.prepareStatement(sqlTransfer, Statement.RETURN_GENERATED_KEYS)) {
            String code = "TRF" + System.currentTimeMillis();
            ps.setString(1, code);
            ps.setLong(2, t.getRequestedById());
            ps.setLong(3, t.getFromRoomId());
            ps.setLong(4, t.getToRoomId());
            ps.setString(5, "PENDING");
            ps.setString(6, t.getReason());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                transferId = rs.getLong(1);
            } else {
                conn.rollback();
                return false;
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
            for (Map.Entry<Integer, String> entry : assetNoteMap.entrySet()) {
                ps.setLong(1, transferId);
                ps.setLong(2, entry.getKey());       
                ps.setString(3, entry.getValue());   
                ps.addBatch();
            }
            ps.executeBatch();
        }

        conn.commit();
        return true;
    } catch (SQLException e) {
        if (conn != null) conn.rollback();
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}


    public boolean approveTransfer(int transferId) throws SQLException {
        String sql = "UPDATE AssetTransfers " +
                     "SET Status = 'APPROVED', UpdatedAt = GETDATE() " +
                     "WHERE TransferId = ? AND Status = 'PENDING'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean rejectTransfer(int transferId) throws SQLException {
        String sql = "UPDATE AssetTransfers " +
                     "SET Status = 'REJECTED', UpdatedAt = GETDATE() " +
                     "WHERE TransferId = ? AND Status = 'PENDING'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean completeTransfer(int transferId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int toRoomId = -1;
            String getSql = "SELECT ToRoomId FROM AssetTransfers " +
                            "WHERE TransferId = ? AND Status = 'APPROVED'";
            try (PreparedStatement ps = conn.prepareStatement(getSql)) {
                ps.setInt(1, transferId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    toRoomId = rs.getInt("ToRoomId");
                } else {
              
                    conn.rollback();
                    return false;
                }
            }

         
            String updateAssetSql =
                "UPDATE a " +
                "SET a.RoomId = ?, a.UpdatedAt = GETDATE() " +
                "FROM Assets a " +
                "INNER JOIN AssetTransferItems ati ON a.AssetId = ati.AssetId " +
                "WHERE ati.TransferId = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateAssetSql)) {
                ps.setInt(1, toRoomId);
                ps.setInt(2, transferId);
                ps.executeUpdate();
            }

      
            String completeSql = "UPDATE AssetTransfers " +
                                 "SET Status = 'COMPLETED', UpdatedAt = GETDATE() " +
                                 "WHERE TransferId = ? AND Status = 'APPROVED'";
            try (PreparedStatement ps = conn.prepareStatement(completeSql)) {
                ps.setInt(1, transferId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


}