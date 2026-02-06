
package controller.staff;

import dao.AssetDAO;
import dao.TransferDAO;
import model.Asset;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Transfer;

@WebServlet(name = "TransferListServlet", urlPatterns = {"/transfers/list"})
public class TransferListServlet extends HttpServlet {
    
    private final AssetDAO assetDAO = new AssetDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                TransferDAO dao = new TransferDAO();
       // List<Transfer> transfers = dao.getAllTransfers();
 List<Transfer> transfers = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
        Transfer t = new Transfer();
        t.setTransferId(i);
        t.setTransferCode("TRF-00" + i);
        t.setFromRoomName("Phòng A" + i);
        t.setToRoomName("Phòng B" + i);
        t.setRequestedByName("Nguyễn Văn " + i);
        t.setReason("Điều chuyển thiết bị phục vụ giảng dạy");
        t.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        // Fake status
        switch (i % 4) {
            case 0:
                t.setStatus("PENDING");
                t.setStatusText("Chờ duyệt");
                t.setStatusBadgeClass("badge-warning");
                break;
            case 1:
                t.setStatus("APPROVED");
                t.setStatusText("Đã duyệt");
                t.setStatusBadgeClass("badge-info");
                break;
            case 2:
                t.setStatus("COMPLETED");
                t.setStatusText("Hoàn tất");
                t.setStatusBadgeClass("badge-success");
                break;
            default:
                t.setStatus("REJECTED");
                t.setStatusText("Từ chối");
                t.setStatusBadgeClass("badge-danger");
        }

        transfers.add(t);
    }
        request.setAttribute("transfers", transfers);
        request.getRequestDispatcher("/views/asset_transfer/asset-transfer-list.jsp")
               .forward(request, response);
    
    }
}

