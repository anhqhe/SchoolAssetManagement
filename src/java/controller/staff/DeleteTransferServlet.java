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

@WebServlet("/transfers/delete")
public class DeleteTransferServlet  extends HttpServlet {

 @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession();
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
         response.setContentType("application/json");

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            TransferDAO dao = new TransferDAO();
            Transfer t = dao.getTransferById(id);
            if (!"PENDING".equals(t.getStatus())) {
                response.setStatus(400);
                response.getWriter().write("{\"message\":\"Chỉ được xóa phiếu PENDING\"}");
                return;
            }
            dao.deleteTransfer(id);

            response.getWriter().write("{\"success\": true}");

        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\": false}");
        }
}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/transfers/list");
    }
}