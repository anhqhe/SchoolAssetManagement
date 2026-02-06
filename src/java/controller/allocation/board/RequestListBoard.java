/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.board;

import controller.allocation.websocket.NotificationEndPoint;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author Leo
 */
@WebServlet(name = "Approval", urlPatterns = {"/board/approval-center"})
public class RequestListBoard extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        //show msg if approved
        String msg = request.getParameter("msg");
        if ("success".equals(msg)) {
            request.setAttribute("msg", "Đã phê duyệt phiếu thành công!");
        } else if ("error".equals(msg)) {
            request.setAttribute("error", "Có lỗi xảy ra trong quá trình xử lý.");
        }

        //Filter
        // Get parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");

        List<AssetRequestDTO> list = new ArrayList<>();
        try {
            list = requestDAO.getRequestsAdvanced(keyword, status, sortBy);
        } catch (SQLException ex) {
            Logger.getLogger(RequestListBoard.class.getName()).log(Level.SEVERE, null, ex);
        }

        request.setAttribute("requestList", list);
        request.getRequestDispatcher("/views/allocation/request-list.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get User data from session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        // Get data from form
        String requestIdStr = request.getParameter("requestId");
        String decision = request.getParameter("decision"); // APPROVED or REJECTED
        String note = request.getParameter("note");

        if (user == null || requestIdStr == null) {
            response.sendRedirect("approval-center?msg=error");
            return;
        }

        long requestId = Long.parseLong(requestIdStr);
        long approverId = user.getUserId();

        // Save to database
        boolean isSuccess = approveOrRejectRequest(requestId, approverId, decision, note);

        if (!isSuccess) {
            response.sendRedirect("approval-center?msg=error");
            return;
        }

        //Save to database sucessfully
        // Send Notifications to Teacher, Staff 
        
        AssetRequestDTO reqDTO = requestDAO.findById(requestId);

        //Board Reject
        if ("REJECTED".equals(decision)) {
            NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                    "Phiếu " + reqDTO.getRequestCode() + " của bạn đã bị từ chối.");
        }

        //Board  Approve
        if ("APPROVED".equals(decision)) {
            //Send noti to teacher
            NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                    "Phiếu " + reqDTO.getRequestCode() + " đã được phê duyệt!");
            //send noti to staffs
            List<Long> staffIds = userDAO.getIdsByRole("ASSET_STAFF");
            NotificationEndPoint.sendToUsers(staffIds,
                    "Cần cấp phát tài sản cho phiếu: " + reqDTO.getRequestCode());
        }

        // Return result
        response.sendRedirect("approval-center?msg=success");

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
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try {
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }
}
