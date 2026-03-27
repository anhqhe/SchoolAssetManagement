/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.teacher;

import dao.allocation.AllocationDAO;
import dto.TeacherAssignedAssetDTO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

@WebServlet(name = "AssetList", urlPatterns = {"/teacher/asset-list"})
public class AssetList extends HttpServlet {

    private final AllocationDAO allocationDAO = new AllocationDAO();
    private static final Logger LOGGER = Logger.getLogger(AssetList.class.getName());

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
        String status = request.getParameter("status");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        try {
            List<TeacherAssignedAssetDTO> assignedAssets = allocationDAO.getAssignedAssetsByTeacher(
                    currentUser.getUserId(),
                    keyword,
                    status,
                    fromDate,
                    toDate
            );

            request.setAttribute("assetList", assignedAssets);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("fromDate", fromDate);
            request.setAttribute("toDate", toDate);

            request.getRequestDispatcher("/views/allocation/teacher/asset-list.jsp")
                    .forward(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading assigned assets for teacher. userId=" + currentUser.getUserId(), ex);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Không thể tải danh sách tài sản được cấp phát.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
