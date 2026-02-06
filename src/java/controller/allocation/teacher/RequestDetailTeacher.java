/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dto.ApprovalDTO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestDetail", urlPatterns = {"/teacher/request-detail"})
public class RequestDetailTeacher extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO itemDAO = new AssetRequestItemDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Check authentication
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");
            
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // Check authorization - user must have TEACHER role
            List<String> roles = currentUser.getRoles();
            if (roles == null || !roles.contains("TEACHER")) {
                response.sendRedirect("request-list");
                return;
            }
            
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect("request-list?error=Invalid ID parameter");
                return;
            }

            long requestId = Long.parseLong(idParam);
            
            System.out.println("[RequestDetailTeacher] Loading request ID: " + requestId);

            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null) {
                System.out.println("[RequestDetailTeacher] Request not found for ID: " + requestId);
                response.sendRedirect("request-list?error=Request not found");
                return;
            }
            
            List<AssetRequestItemDTO> itemList = itemDAO.findByRequestId(requestId);
            ApprovalDTO approval = approvalDAO.findByRef("ASSET_REQUEST", requestId);            
            
            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("approval", approval);

            request.getRequestDispatcher("/views/allocation/request-detail.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("request-list?error=invalid_request");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            response.sendRedirect("request-list?error=not_found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
