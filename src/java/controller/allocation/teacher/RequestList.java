/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AssetRequestDAO;
import dao.allocation.RoomDAO;
import dto.AssetRequestDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.allocation.AssetRequest;
import model.allocation.User;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestList", urlPatterns = {"/teacher/request-list"})
public class RequestList extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get User Data from Session
//        HttpSession session = request.getSession();
//        User currentUser = (User) session.getAttribute("user");

        //DEMO
        List<String> roles = List.of("ADMIN", "TEACHER", "STAFF");
        User currentUser = new User(1, "admin", "admin", true, roles);
        //Demo end

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        //currentUser != null
        
        //Filter
        // Get parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");

        //List<AssetRequest> list = AssetRequestDAO.getRequestsByTeacher(currentUser.getUserId());

        List<AssetRequestDTO> list = new ArrayList<>();
        try {
            list = requestDAO.getRequestsByTeacher(currentUser.getUserId(), keyword, status, sortBy);
        } catch (SQLException ex) {
            Logger.getLogger(RequestList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        request.setAttribute("roomDAO", roomDAO);
        request.setAttribute("myRequests", list);
        request.getRequestDispatcher("/views/allocation/teacher/request-list.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
