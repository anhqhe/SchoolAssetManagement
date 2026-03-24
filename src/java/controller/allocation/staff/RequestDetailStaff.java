/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.staff;

import dao.allocation.AllocationDAO;
import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.FeedbackDAO;
import dto.ApprovalDTO;
import dto.AssetDTO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import dto.FeedbackDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import model.User;
import model.allocation.AssetAllocation;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestDetailStaff", urlPatterns = {"/staff/request-detail"})
public class RequestDetailStaff extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO reqItemDAO = new AssetRequestItemDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    private static final Logger LOGGER
            = Logger.getLogger(RequestDetailStaff.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "Yêu cầu không tồn tại.");
            response.sendRedirect("request-list");
            return;
        }

        long requestId;
        try {
            requestId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid request ID format: {0}", idParam);

            session.setAttribute("type", "error");
            session.setAttribute("message", "ID yêu cầu không hợp lệ.");
            response.sendRedirect("request-list");
            return;
        }

        try {
            // Get AssetRequest Infor
            AssetRequestDTO req = requestDAO.findById(requestId);

            if (req == null) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Yêu cầu không tồn tại.");
                response.sendRedirect("request-list");
                return;
            }

            // Get AssetRequestItem Infor
            List<AssetRequestItemDTO> itemList = reqItemDAO.findByRequestId(requestId);

            ApprovalDTO approval = approvalDAO.findByRef("ASSET_REQUEST", requestId);

            //Get asset info after allocating
            List<AssetDTO> allocatedAssets = allocationDAO.getAllocatedAssetsByRequestId(requestId);

            //Get allocations
            List<AssetAllocation> allocations = allocationDAO.getAllocationsByRequestId(requestId);

            FeedbackDTO teacherFeedback = feedbackDAO.findByCreatorAndTarget(
                    req.getTeacherId(), "ASSET_REQUEST", requestId);
            
            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("approval", approval);
            request.setAttribute("allocatedAssets", allocatedAssets);
            request.setAttribute("allocations", allocations);
            request.setAttribute("teacherFeedback", teacherFeedback);
            request.setAttribute("teacherFeedbackExists", teacherFeedback != null);
            request.getRequestDispatcher("/views/allocation/request-detail.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Database error while loading request detail. ID=" + requestId, e);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra. Vui lòng thử lại.");
            response.sendRedirect("request-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
