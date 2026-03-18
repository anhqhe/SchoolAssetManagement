package controller.staff;

import dao.TransferDAO;
import model.Transfer;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/transfers/update")
public class UpdateTransferServlet  extends HttpServlet {

 @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession();
    User currentUser = (User) session.getAttribute("currentUser");

    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    try {
        int id = Integer.parseInt(request.getParameter("transferId"));
        int fromRoomId = Integer.parseInt(request.getParameter("fromRoomId"));
        int toRoomId = Integer.parseInt(request.getParameter("toRoomId"));
        String reason = request.getParameter("reason");

        // === LẤY ASSET + NOTE ===
        String[] assetIdParams = request.getParameterValues("assetIds");

        Map<Integer, String> assetNoteMap = new LinkedHashMap<>();

        if (assetIdParams != null) {
            for (String aId : assetIdParams) {
                int assetId = Integer.parseInt(aId);
                String note = request.getParameter("assetNote_" + aId);
                assetNoteMap.put(assetId, note != null ? note.trim() : "");
            }
        }

        if (assetNoteMap.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/transfers/list?error=noAsset");
            return;
        }

        // === SET OBJECT ===
        Transfer t = new Transfer();
        t.setTransferId(id);
        t.setFromRoomId(fromRoomId);
        t.setToRoomId(toRoomId);
        t.setReason(reason);

        TransferDAO dao = new TransferDAO();
        boolean ok = dao.updateTransferWithItems(t, assetNoteMap);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/transfers/list");
        } else {
            response.sendRedirect(request.getContextPath() + "/transfers/list?error=updateFail");
        }

    } catch (Exception e) {
        e.printStackTrace();
        throw new ServletException("Lỗi khi cập nhật điều chuyển", e);
    }
}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/transfers/list");
    }
}