/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.board;

import controller.allocation.notification.NotificationEndPoint;
import dao.allocation.UserDAO;
import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dto.AssetRequestDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import util.DBUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import model.User;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestListBoard", urlPatterns = {"/board/request-list"})
public class RequestListBoard extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();
    private UserDAO userDAO = new UserDAO();

    private static final Logger LOGGER
            = Logger.getLogger(RequestListBoard.class.getName());

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

        List<AssetRequestDTO> list;
        try {
            list = requestDAO.getRequestsAdvanced(keyword, status, sortBy);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Database error while loading staff asset request list. userId="
                    + currentUser.getUserId(), ex);

            response.sendRedirect(request.getContextPath() + "/views/common/500.jsp");
            return;
        }

        request.setAttribute("requestList", list);
        request.getRequestDispatcher("/views/allocation/request-list.jsp")
                .forward(request, response);
    }

    //Process Board approve or reject request
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // For Board approve or reject
        // Get data from form
        String requestIdStr = request.getParameter("requestId");
        String decision = request.getParameter("decision"); // APPROVED or REJECTED
        String note = request.getParameter("note");

        if (requestIdStr == null || requestIdStr.isEmpty()) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "Yêu cầu không tồn tại");
            response.sendRedirect(request.getContextPath() + "/board/request-list");
            return;
        }

        long requestId;
        try {
            requestId = Long.parseLong(requestIdStr);
        } catch (NumberFormatException ex) {

            LOGGER.log(Level.WARNING,
                    "Invalid requestId in board approval: {0}", requestIdStr);

            session.setAttribute("type", "error");
            session.setAttribute("message", "ID không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/board/request-list");
            return;
        }

        long approverId = currentUser.getUserId();

        if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
            LOGGER.log(Level.WARNING,
                    "Invalid decision value: {0}", decision);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Thao tác không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/board/request-list");
            return;
        }

        // Save to database
        boolean isSuccess = approveOrRejectRequest(requestId, approverId, decision, note);

        if (!isSuccess) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra trong quá trình xử lý.");
            response.sendRedirect(request.getContextPath() + "/board/request-list");
            return;
        }

        //Save to database sucessfully
        // Send Notifications to Teacher, Staff 
        AssetRequestDTO reqDTO;
        try {
            reqDTO = requestDAO.findById(requestId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Error loading request after approval. ID=" + requestId, ex);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra.");
            response.sendRedirect(request.getContextPath() + "/board/request-list");
            return;
        }

        if (reqDTO == null) {
            LOGGER.log(Level.WARNING,
                    "Request not found after approval. ID={0}", requestId);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra.");
            response.sendRedirect(request.getContextPath() + "/board/request-list");
            return;
        }

        //Board Reject
        if ("REJECTED".equals(decision)) {
            NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                    "Yêu cầu bị từ chối",
                    "Phiếu " + reqDTO.getRequestCode() + " của bạn đã bị từ chối.",
                    "ASSET_REQUEST",
                    requestId);
        }

        //Board  Approve
        if ("APPROVED".equals(decision)) {
            //Send noti to teacher
            NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                    "Yêu cầu được phê duyệt",
                    "Phiếu " + reqDTO.getRequestCode() + " đã được phê duyệt!",
                    "ASSET_REQUEST",
                    requestId);
            //send noti to staffs
            List<Long> staffIds = userDAO.getIdsByRole("ASSET_STAFF");
            NotificationEndPoint.sendToUsers(staffIds,
                    "Yêu cầu cấp phát mới",
                    "Cần cấp phát tài sản cho phiếu: " + reqDTO.getRequestCode(),
                    "ASSET_REQUEST",
                    requestId);
        }

        // Return result
        session.setAttribute("type", "success");
        session.setAttribute("message", "Đã phê duyệt phiếu thành công!");
        response.sendRedirect(request.getContextPath() + "/board/request-list");

    }

    //save data to table Approval, update Status in table AssetRequest
    public boolean approveOrRejectRequest(long requestId, long approverId, String decision, String note) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert into table Approvals
            boolean isInserted = approvalDAO.insertApproval(conn, "ASSET_REQUEST", requestId, approverId, decision, note);

            // 2. Update AssetRequest Status
            String newStatus = "APPROVED".equals(decision) ? "APPROVED_BY_BOARD" : "REJECTED";
            boolean isUpdated = requestDAO.updateStatus(conn, requestId, newStatus);

            // success
            if (isInserted && isUpdated) {
                conn.commit();  //end transaction
                return true;
            } else {
                conn.rollback();    //error -> rollback all
                return false;
            }
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Rollback failed", ex);
            }
            LOGGER.log(Level.SEVERE,
                    "Error approving/rejecting request. ID=" + requestId, e);
            return false;
        } finally {
            if (conn != null) try {
                conn.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error closing connection", ex);
            }
        }
    }
}
