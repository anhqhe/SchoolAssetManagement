package dao;

import java.sql.*;
import java.util.*;
import model.Transfer;
import util.DBUtil;

public class TransferDAO {

    public List<Transfer> getAllTransfers() {
        List<Transfer> list = new ArrayList<>();
            String sql =
              "SELECT t.TransferId, t.TransferCode, t.Reason, t.Status, t.CreatedAt, "
            + "fr.RoomName AS FromRoom, "
            + "tr.RoomName AS ToRoom, "
            + "u.FullName AS RequestedBy "
            + "FROM AssetTransfers t "
            + "JOIN Rooms fr ON t.FromRoomId = fr.RoomId "
            + "JOIN Rooms tr ON t.ToRoomId = tr.RoomId "
            + "JOIN Users u ON t.RequestedById = u.UserId "
            + "ORDER BY t.CreatedAt DESC";


        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transfer t = new Transfer();
                t.setTransferId(rs.getInt("TransferId"));
                t.setTransferCode(rs.getString("TransferCode"));
                t.setReason(rs.getString("Reason"));
                t.setStatus(rs.getString("Status"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                t.setFromRoomName(rs.getString("FromRoom"));
                t.setToRoomName(rs.getString("ToRoom"));
                t.setRequestedByName(rs.getString("RequestedBy"));
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
