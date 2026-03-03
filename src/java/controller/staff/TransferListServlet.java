
package controller.staff;

import dao.AssetDAO;
import dao.TransferDAO;
import model.Asset;
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
import java.util.List;
import model.Room;
import model.Transfer;

@WebServlet(name = "TransferListServlet", urlPatterns = {"/transfers/list"})
public class TransferListServlet extends HttpServlet {
    
    private final AssetDAO assetDAO = new AssetDAO();
    
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String keyword = request.getParameter("keyword");
    String status = request.getParameter("status");

    try {
      
        TransferDAO transferDAO = new TransferDAO();
        List<Transfer> transfers = transferDAO.getTransfers(keyword, status);
        request.setAttribute("transfers", transfers);

        dao.RoomDAO roomDAO = new dao.RoomDAO(); 
        List<Room> rooms = roomDAO.getAllRooms();
        List<Asset> assets = assetDAO.getAvailableAssets();

   
        request.setAttribute("rooms", rooms);
        request.setAttribute("assets", assets); 
        
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedStatus", status);

    } catch (SQLException e) {
        throw new ServletException(e);
    }

    request.getRequestDispatcher("/views/asset_transfer/asset-transfer-list.jsp")
           .forward(request, response);
}
}
