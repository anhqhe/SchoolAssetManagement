/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

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
@WebServlet(name = "RequestDetail", urlPatterns = {"/teacher/request-detail"})
public class RequestDetailTeacher extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO itemDAO = new AssetRequestItemDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    private static final Logger LOGGER
            = Logger.getLogger(RequestDetailTeacher.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "Yêu cầu không tồn tại");
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

            //Get allocations
            List<AssetAllocation> allocations = allocationDAO.getAllocationsByRequestId(requestId);
            FeedbackDTO teacherFeedback = feedbackDAO.findByCreatorAndTarget(
                    req.getTeacherId(), "ASSET_REQUEST", requestId);

            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("approval", approval);
            request.setAttribute("allocations", allocations);
            request.setAttribute("allocatedAssets", allocatedAssets);
            request.setAttribute("teacherFeedback", teacherFeedback);
            request.setAttribute("teacherFeedbackExists", teacherFeedback != null);

            request.getRequestDispatcher("/views/allocation/request-detail.jsp")
                    .forward(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Database error while loading request detail. ID=" + requestId, ex);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra. Vui lòng thử lại.");
            response.sendRedirect("request-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String action = request.getParameter("action");
        if (!"feedback".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        String requestIdRaw = request.getParameter("requestId");
        String contentRaw = request.getParameter("content");

        long requestId;
        try {
            requestId = Long.parseLong(requestIdRaw);
        } catch (NumberFormatException ex) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "ID yêu cầu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        String content = contentRaw == null ? "" : contentRaw.trim();
        if (content.isEmpty()) {
            session.setAttribute("type", "warning");
            session.setAttribute("message", "Vui lòng nhập nội dung đánh giá.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);
            return;
        }
        if (content.length() > 1000) {
            session.setAttribute("type", "warning");
            session.setAttribute("message", "Nội dung đánh giá tối đa 1000 ký tự.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);
            return;
        }

        try {
            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null || req.getTeacherId() == null || req.getTeacherId() != currentUser.getUserId()) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Bạn không có quyền đánh giá yêu cầu này.");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list");
                return;
            }
            if (!"COMPLETED".equals(req.getStatus())) {
                session.setAttribute("type", "warning");
                session.setAttribute("message", "Chỉ yêu cầu đã hoàn thành mới được đánh giá.");
                response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);
                return;
            }

            boolean exists = feedbackDAO.existsByCreatorAndTarget(
                    currentUser.getUserId(), "ASSET_REQUEST", requestId);
            if (exists) {
                session.setAttribute("type", "info");
                session.setAttribute("message", "Bạn đã đánh giá yêu cầu này trước đó.");
                response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);
                return;
            }

            boolean success = feedbackDAO.insert(
                    currentUser.getUserId(), "ASSET_REQUEST", requestId, content);

            session.setAttribute("type", success ? "success" : "error");
            session.setAttribute("message", success
                    ? "Gửi đánh giá thành công."
                    : "Không thể gửi đánh giá. Vui lòng thử lại.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Database error while saving teacher feedback. userId="
                    + currentUser.getUserId() + ", requestId=" + requestId, ex);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra khi gửi đánh giá.");
        }

        response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);
    }

}
