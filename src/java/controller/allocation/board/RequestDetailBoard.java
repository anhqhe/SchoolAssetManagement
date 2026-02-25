/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.board;

import dao.allocation.AllocationDAO;
import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dto.ApprovalDTO;
import dto.AssetDTO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name="RequestDetailBoard", urlPatterns={"/board/request-detail"})
public class RequestDetailBoard extends HttpServlet {
    
    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO reqItemDAO = new AssetRequestItemDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();
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
            
            // Check authorization - user must have BOARD role
            List<String> roles = currentUser.getRoles();
            if (roles == null || !roles.contains("BOARD")) {
                response.sendRedirect("request-list");
                return;
            }
            
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect("request-list");
                return;
            }
            long requestId = Long.parseLong(idParam);

            // Get AssetRequest Infor
            AssetRequestDTO requestDetail = requestDAO.findById(requestId);

            // Get AssetRequestItem Infor
            List<AssetRequestItemDTO> itemList = reqItemDAO.findByRequestId(requestId);
            
            ApprovalDTO approval = approvalDAO.findByRef("ASSET_REQUEST", requestId);
            
            //Get asset info after allocating
            List<AssetDTO> allocatedAssets = allocationDAO.getAllocatedAssetsByRequestId(requestId);

            //Get allocation header to determine who allocated
            model.allocation.AssetAllocation allocHeader = allocationDAO.getAllocationByRequestId(requestId);
            String allocatedByName = null;
            if (allocHeader != null && allocHeader.getAllocatedById() != null) {
                dao.allocation.UserDAO allocUserDao = new dao.allocation.UserDAO();
                model.User allocUser = allocUserDao.getByUserId(allocHeader.getAllocatedById());
                if (allocUser != null) allocatedByName = allocUser.getFullName() != null ? allocUser.getFullName() : allocUser.getUsername();
            }

            request.setAttribute("req", requestDetail);
            request.setAttribute("itemList", itemList);
            request.setAttribute("approval", approval);
            request.setAttribute("allocatedAssets", allocatedAssets);
            request.setAttribute("allocatedByName", allocatedByName);
            
            request.getRequestDispatcher("/views/allocation/request-detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra khi tải chi tiết yêu cầu.");
            response.sendRedirect("request-list");
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }


}

