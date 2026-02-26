/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AllocationDAO;
import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dto.ApprovalDTO;
import dto.AssetDTO;
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
import model.allocation.AssetAllocation;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestDetail", urlPatterns = {"/teacher/request-detail"})
public class RequestDetailTeacher extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO itemDAO = new AssetRequestItemDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        try {
            // Check authentication
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            // Check authorization - user must have TEACHER role
            List<String> roles = currentUser.getRoles();
            if (roles == null || !roles.contains("TEACHER")) {
                //response.sendError(HttpServletResponse.SC_FORBIDDEN);
                response.sendRedirect(request.getContextPath() + "/views/common/404.jsp");
                return;
            }

            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Yêu cầu không tồn tại");
                response.sendRedirect("request-list");
                return;
            }

            long requestId = Long.parseLong(idParam);

            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Yêu cầu không tồn tại");
                response.sendRedirect("request-list");
                return;
            }

            List<AssetRequestItemDTO> itemList = itemDAO.findByRequestId(requestId);
            ApprovalDTO approval = approvalDAO.findByRef("ASSET_REQUEST", requestId);
            
            //Get asset info after allocating
            List<AssetDTO> allocatedAssets = allocationDAO.getAllocatedAssetsByRequestId(requestId);

            //Get allocation
            AssetAllocation allocation = allocationDAO.getAllocationByRequestId(requestId);
           
            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("approval", approval);
            request.setAttribute("allocation", allocation);
            request.setAttribute("allocatedAssets", allocatedAssets);

            request.getRequestDispatcher("/views/allocation/request-detail.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace(System.err);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra!!");
            response.sendRedirect("request-list");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra!!");
            response.sendRedirect("request-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
