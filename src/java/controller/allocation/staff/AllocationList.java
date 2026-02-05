/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.staff;

import dao.allocation.AssetRequestDAO;
import dto.AssetRequestDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
@WebServlet(name = "AllocationList", urlPatterns = {"/staff/allocation-list"})
public class AllocationList extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //Handle message from redirect
        String msg = request.getParameter("msg");
        if ("success".equals(msg)) {
            request.setAttribute("msg", "Cấp phát tài sản thành công!");
        } else if ("error".equals(msg)) {
            request.setAttribute("msg", "Có lỗi xảy ra.");
        }

        //Filter
        
        // Get parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");

        
        List<AssetRequestDTO> list = new ArrayList<>();
        try {
            list = requestDAO.getRequestsAdvanced(keyword, status, sortBy);
        } catch (SQLException ex) {
            Logger.getLogger(AllocationList.class.getName()).log(Level.SEVERE, null, ex);
        }

        request.setAttribute("pendingList", list);
        request.getRequestDispatcher("/views/allocation/staff/allocation-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
