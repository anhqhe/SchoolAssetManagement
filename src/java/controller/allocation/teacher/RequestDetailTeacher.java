/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AllocationDAO;
import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestFeedbackDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.UserDAO;
import controller.allocation.notification.NotificationEndPoint;
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
import java.sql.SQLException;
import java.util.List;
import model.User;
import model.allocation.AssetAllocation;
import model.allocation.AssetRequestFeedback;
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
    private AssetRequestFeedbackDAO feedbackDAO = new AssetRequestFeedbackDAO();
    private UserDAO userDAO = new UserDAO();

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

            AssetRequestFeedback feedback = null;
            try {
                feedback = feedbackDAO.findByRequestId(requestId);
            } catch (SQLException ex) {
                // Nếu DB chưa tạo bảng feedback hoặc lỗi truy vấn feedback thì vẫn cho xem chi tiết yêu cầu
                LOGGER.log(Level.WARNING, "Cannot load feedback for requestId=" + requestId, ex);
            }

            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("approval", approval);
            request.setAttribute("allocations", allocations);
            request.setAttribute("allocatedAssets", allocatedAssets);
            request.setAttribute("teacherFeedback", feedback);

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

        String requestIdParam = request.getParameter("requestId");
        if (requestIdParam == null || requestIdParam.isBlank()) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "Thiếu mã yêu cầu để gửi feedback.");
            response.sendRedirect("request-list");
            return;
        }

        long requestId;
        try {
            requestId = Long.parseLong(requestIdParam.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "ID yêu cầu không hợp lệ.");
            response.sendRedirect("request-list");
            return;
        }

        try {
            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null || req.getTeacherId() == null || !req.getTeacherId().equals(currentUser.getUserId())) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Bạn không có quyền gửi feedback cho yêu cầu này.");
                response.sendRedirect("request-list");
                return;
            }

            String status = req.getStatus();
            boolean processed = "COMPLETED".equals(status) || "OUT_OF_STOCK".equals(status) || "INCOMPLETE".equals(status);
            if (!processed) {
                session.setAttribute("type", "warning");
                session.setAttribute("message", "Chỉ có thể gửi feedback sau khi yêu cầu đã được xử lý.");
                response.sendRedirect("request-detail?id=" + requestId);
                return;
            }

            String commentParam = request.getParameter("comment");
            String comment = (commentParam != null) ? commentParam.trim() : null;

            if (comment == null || comment.isEmpty()) {
                session.setAttribute("type", "warning");
                session.setAttribute("message", "Vui lòng nhập nội dung feedback trước khi gửi.");
                response.sendRedirect("request-detail?id=" + requestId);
                return;
            }

            try {
                feedbackDAO.upsert(requestId, currentUser.getUserId(), null, comment);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Feedback upsert failed. requestId=" + requestId, ex);
                session.setAttribute("type", "error");
                session.setAttribute("message", "Có lỗi khi lưu feedback. Kiểm tra bảng dbo.Feedbacks (TargetType='ASSET_REQUEST') trong database.");
                response.sendRedirect("request-detail?id=" + requestId);
                return;
            }

            // notify staff duy nhất: lấy user đầu tiên có role ASSET_STAFF
            try {
                List<Long> staffIds = userDAO.getIdsByRole("ASSET_STAFF");
                if (staffIds != null && !staffIds.isEmpty()) {
                    long staffId = staffIds.get(0);
                    NotificationEndPoint.sendToUser(
                            staffId,
                            "Giáo viên đã phản hồi",
                            "Giáo viên đã feedback cho phiếu: " + req.getRequestCode(),
                            "ASSET_REQUEST",
                            requestId
                    );
                }
            } catch (Exception ex) {
                // Không chặn teacher gửi feedback nếu notification gặp lỗi
                LOGGER.log(Level.WARNING, "Send staff notification failed. requestId=" + requestId, ex);
            }

            session.setAttribute("type", "success");
            session.setAttribute("message", "Đã gửi feedback cho bộ phận tài sản.");
            response.sendRedirect("request-detail?id=" + requestId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error saving feedback. requestId=" + requestIdParam, ex);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra khi lưu feedback. Vui lòng thử lại.");
            response.sendRedirect("request-detail?id=" + requestIdParam);
        }
    }

}
