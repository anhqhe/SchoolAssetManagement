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
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestListStaff", urlPatterns = {"/staff/request-list"})
public class RequestListStaff extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check authentication
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Check authorization - user must have ASSET_STAFF hoặc ADMIN role
        List<String> roles = currentUser.getRoles();
        boolean isStaffOrAdmin = roles != null && (roles.contains("ASSET_STAFF") || roles.contains("ADMIN"));
        if (!isStaffOrAdmin) {
            response.sendRedirect(request.getContextPath() + "/views/common/403.jsp");
            return;
        }

        //Filter
        
        // Get parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");

        
        List<AssetRequestDTO> list = new ArrayList<>();
        try {
            list = requestDAO.getRequestsForStaff(keyword, status, sortBy);
        } catch (SQLException ex) {
            System.out.println("controller.allocation.staff.RequestListStaff.doGet()");
            System.out.println(ex);
        }

        request.setAttribute("requestList", list);
        request.getRequestDispatcher("/views/allocation/request-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}

