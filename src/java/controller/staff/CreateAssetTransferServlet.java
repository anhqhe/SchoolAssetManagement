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

@WebServlet(name = "CreateAssetTransferServlet", urlPatterns = {"/transfers/create"})
public class CreateAssetTransferServlet extends HttpServlet {

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
        int fromRoomId = Integer.parseInt(request.getParameter("fromRoomId"));
        int toRoomId   = Integer.parseInt(request.getParameter("toRoomId"));
        String reason  = request.getParameter("reason");

        String[] assetIdParams = request.getParameterValues("assetIds");
       
        Map<Integer, String> assetNoteMap = new LinkedHashMap<>();
        if (assetIdParams != null) {
            for (String id : assetIdParams) {
                int assetId = Integer.parseInt(id);

                String note = request.getParameter("assetNote_" + id);
                assetNoteMap.put(assetId, note != null ? note.trim() : "");
            }
        }

        if (assetNoteMap.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/transfers/list?error=noAsset");
            return;
        }

        Transfer transfer = new Transfer();
        transfer.setFromRoomId(fromRoomId);
        transfer.setToRoomId(toRoomId);
        transfer.setReason(reason);
        transfer.setRequestedById((int) currentUser.getUserId());

        TransferDAO dao = new TransferDAO();
        boolean ok = dao.insertTransferWithItems(transfer, assetNoteMap);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/transfers/list");
        } else {
            response.sendRedirect(request.getContextPath() + "/transfers/list?error=insertFail");
        }
    } catch (NumberFormatException e) {
        e.printStackTrace();
        throw new ServletException("Lỗi khi tạo điều chuyển", e);
    } catch (SQLException ex) {
         Logger.getLogger(CreateAssetTransferServlet.class.getName()).log(Level.SEVERE, null, ex);
     }
}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/transfers/list");
    }
}