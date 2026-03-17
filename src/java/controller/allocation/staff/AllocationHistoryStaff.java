/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.staff;

import dao.allocation.AllocationDAO;
import dao.allocation.AllocationItemDAO;
import dao.allocation.RoomDAO;
import dto.AllocationHistoryDTO;
import dto.AssetDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import model.allocation.Room;

/**
 *
 * @author Leo
 */
@WebServlet(name="AllocationHistoryStaff", urlPatterns={"/staff/allocation-history"})
public class AllocationHistoryStaff extends HttpServlet {
    
    private final AllocationDAO allocationDAO = new AllocationDAO();
    private final AllocationItemDAO allocationItemDAO = new AllocationItemDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    
    private static final Logger LOGGER = Logger.getLogger(AllocationHistoryStaff.class.getName());
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        String keyword = request.getParameter("keyword");
        String fromRoomParam = request.getParameter("fromRoomId");
        String toRoomParam = request.getParameter("toRoomId");
        String status = request.getParameter("status");
        String dateFromParam = request.getParameter("dateFrom");
        String dateToParam = request.getParameter("dateTo");
        String idParam = request.getParameter("id");
        
        Long fromRoomId = parseLongOrNull(fromRoomParam);
        Long toRoomId = parseLongOrNull(toRoomParam);
        LocalDate dateFrom = parseDateOrNull(dateFromParam);
        LocalDate dateTo = parseDateOrNull(dateToParam);
        
        try {
            List<AllocationHistoryDTO> historyList = allocationDAO.getAllocationHistory(
                    keyword,
                    fromRoomId,
                    toRoomId,
                    status,
                    dateFrom,
                    dateTo,
                    null
            );
            List<Room> rooms = roomDAO.getAllRooms();
            
            request.setAttribute("historyList", historyList);
            request.setAttribute("rooms", rooms);
            
            request.setAttribute("keyword", keyword);
            request.setAttribute("fromRoomId", fromRoomId);
            request.setAttribute("toRoomId", toRoomId);
            request.setAttribute("status", status);
            request.setAttribute("dateFrom", dateFromParam);
            request.setAttribute("dateTo", dateToParam);
            
            if (idParam != null && !idParam.isBlank()) {
                Long allocationId = parseLongOrNull(idParam);
                if (allocationId != null) {
                    AllocationHistoryDTO detail = allocationDAO.getAllocationDetail(allocationId, null);
                    if (detail != null) {
                        List<AssetDTO> detailAssets = allocationItemDAO.getAssetsByAllocationId(allocationId);
                        request.setAttribute("detail", detail);
                        request.setAttribute("detailAssets", detailAssets);
                    } else {
                        request.setAttribute("detailNotFound", true);
                    }
                }
            }
            
            request.getRequestDispatcher("/views/allocation/allocation-history.jsp")
                   .forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading allocation history (staff)", e);
            session.setAttribute("type", "error");
            session.setAttribute("message", "KhÃ´ng thá»ƒ táº£i lá»‹ch sá»­ cáº¥p phÃ¡t.");
            response.sendRedirect(request.getContextPath() + "/staff/request-list");
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }

    private Long parseLongOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

}
