package dao;

import java.sql.*;
import java.util.*;
import model.Transfer;
import util.DBUtil;

public class TransferDAO {


public List<Transfer> getTransfers(
        String keyword,
        String status,
        String fromDate,
        String toDate,
        String fromRoomId,
        String toRoomId) throws SQLException {
String sql = " SELECT " +
    " t.TransferId, t.TransferCode," +
    " t.RequestedById, t.FromRoomId, t.ToRoomId," +
    " t.Status, t.Reason," +
    " t.CreatedAt," +
    " u.FullName AS RequestedByName," +
    " fr.RoomName AS FromRoomName," +
    " tr2.RoomName AS ToRoomName," +
    " STRING_AGG(" +
    " a.AssetName +" +
    " CASE " +
    " WHEN ti.Note IS NOT NULL AND LTRIM(RTRIM(ti.Note)) <> '' " +
    " THEN ' (' + ti.Note + ')' " +
    " ELSE '' " +
    " END," +
    " ', '" +
    " ) AS AssetNames" +
    " FROM AssetTransfers t" +
    " LEFT JOIN Users u ON t.RequestedById = u.UserId" +
    " LEFT JOIN Rooms fr ON t.FromRoomId = fr.RoomId" +
    " LEFT JOIN Rooms tr2 ON t.ToRoomId = tr2.RoomId" +
    " LEFT JOIN AssetTransferItems ti ON t.TransferId = ti.TransferId" +
    " LEFT JOIN Assets a ON ti.AssetId = a.AssetId" +
    " WHERE 1=1";

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

    if (fromDate != null && !fromDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) >= ?");
        params.add(fromDate);
    }

    if (toDate != null && !toDate.isEmpty()) {
        sb.append(" AND CAST(t.CreatedAt AS DATE) <= ?");
        params.add(toDate);
    }
    
    if (fromRoomId != null && !fromRoomId.isEmpty()) {
    sb.append(" AND t.FromRoomId = ?");
    params.add(fromRoomId);
    }

    if (toRoomId != null && !toRoomId.isEmpty()) {
        sb.append(" AND t.ToRoomId = ?");
        params.add(toRoomId);
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
public List<String> getAssetHistory(String assetName) throws SQLException {

    String sql =
        "SELECT " +
        " r.RoomName, " +
        " t.CreatedAt " +
        "FROM AssetTransferItems ti " +
        "JOIN Assets a ON ti.AssetId = a.AssetId " +
        "JOIN AssetTransfers t ON ti.TransferId = t.TransferId " +
        "JOIN Rooms r ON t.ToRoomId = r.RoomId " +
        "WHERE a.AssetName = ? " +
        "ORDER BY t.CreatedAt DESC";

    List<String> history = new ArrayList<>();

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, assetName);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Timestamp date = rs.getTimestamp("CreatedAt");
            String room = rs.getString("RoomName");

            history.add(date + " → " + room);
        }
    }

    return history;
}

public boolean updateTransferWithItems(Transfer t, Map<Integer, String> assetNoteMap) throws SQLException {

    String sqlUpdateTransfer =
            "UPDATE AssetTransfers SET FromRoomId=?, ToRoomId=?, Reason=?, UpdatedAt=GETDATE() " +
            "WHERE TransferId=? AND Status='PENDING'";

    String sqlDeleteItems =
            "DELETE FROM AssetTransferItems WHERE TransferId=?";

    String sqlInsertItem =
            "INSERT INTO AssetTransferItems (TransferId, AssetId, Note) VALUES (?, ?, ?)";

    Connection conn = null;

    try {
        conn = DBUtil.getConnection();
        conn.setAutoCommit(false);

        // 1. Update transfer
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdateTransfer)) {
            ps.setInt(1, t.getFromRoomId());
            ps.setInt(2, t.getToRoomId());
            ps.setString(3, t.getReason());
            ps.setInt(4, t.getTransferId());

            if (ps.executeUpdate() == 0) {
                conn.rollback();
                return false;
            }
        }

        // 2. Delete old items
        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteItems)) {
            ps.setInt(1, t.getTransferId());
            ps.executeUpdate();
        }

        // 3. Insert new items
        try (PreparedStatement ps = conn.prepareStatement(sqlInsertItem)) {
            for (Map.Entry<Integer, String> entry : assetNoteMap.entrySet()) {
                ps.setInt(1, t.getTransferId());
                ps.setInt(2, entry.getKey());
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

public boolean deleteTransfer(int transferId) throws SQLException {

    String sqlDeleteItems =
            "DELETE FROM AssetTransferItems WHERE TransferId=?";

    String sqlDeleteTransfer =
            "DELETE FROM AssetTransfers WHERE TransferId=? AND Status='PENDING'";

    Connection conn = null;

    try {
        conn = DBUtil.getConnection();
        conn.setAutoCommit(false);

        // 1. delete items
        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteItems)) {
            ps.setInt(1, transferId);
            ps.executeUpdate();
        }

        // 2. delete transfer
        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteTransfer)) {
            ps.setInt(1, transferId);

            if (ps.executeUpdate() == 0) {
                conn.rollback();
                return false;
            }
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