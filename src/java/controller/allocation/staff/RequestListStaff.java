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
import java.util.logging.Logger;
import java.util.logging.Level;
import model.User;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestListStaff", urlPatterns = {"/staff/request-list"})
public class RequestListStaff extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private static final Logger LOGGER
            = Logger.getLogger(RequestListStaff.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        //Filter

        // Get parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        List<AssetRequestDTO> list;
        try {
            list = requestDAO.getRequestsForStaff(keyword, status, sortBy, fromDate, toDate);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Database error while loading staff asset request list. userId="
                    + currentUser.getUserId(), ex);

            response.sendRedirect(request.getContextPath() + "/views/common/500.jsp");
            return;
        }

        request.setAttribute("requestList", list);
        request.getRequestDispatcher("/views/allocation/request-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
