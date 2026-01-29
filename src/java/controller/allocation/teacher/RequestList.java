/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.teacher;

import dao.allocation.AssetRequestDAO;
import dao.allocation.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.AssetRequest;
import model.Allocation.User;

/**
 *
 * @author Leo
 */
@WebServlet(name="RequestList", urlPatterns={"/teacher/request-list"})
public class RequestList extends HttpServlet {
    
    private AssetRequestDAO AssetRequestDAO = new AssetRequestDAO();
    private RoomDAO roomDAO = new RoomDAO();
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Get User Data from Session
//        HttpSession session = request.getSession();
//        User currentUser = (User) session.getAttribute("user");

        //DEMO
        List<String> roles = List.of("ADMIN", "TEACHER","STAFF");
        User currentUser = new User(1, "admin", "admin", true,roles );
        //Demo end
        
        if (currentUser != null) {
            List<AssetRequest> list = AssetRequestDAO.getRequestsByTeacher(currentUser.getUserId());
            
            request.setAttribute("roomDAO", roomDAO);
            request.setAttribute("myRequests", list);
            request.getRequestDispatcher("/views/allocation/teacher/request-list.jsp")
                   .forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

}
