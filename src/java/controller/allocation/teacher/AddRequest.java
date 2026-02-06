/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import controller.allocation.websocket.NotificationEndPoint;
import dao.allocation.UserDAO;
import dao.allocation.AssetCategoryDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;
import util.DBUtil;
import java.sql.Connection;
import model.allocation.AssetRequest;
import model.allocation.AssetRequestItem;
import java.sql.SQLException;

/**
 *
 * @author Leo
 */
@WebServlet(name = "AddRequest", urlPatterns = {"/teacher/add-request"})
public class AddRequest extends HttpServlet {

    private RoomDAO roomDAO = new RoomDAO();
    private UserDAO userDAO = new UserDAO();
    private AssetCategoryDAO assetCategoryDAO = new AssetCategoryDAO();
    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO requestItemDAO = new AssetRequestItemDAO();

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
        
        // Check authorization - user must have TEACHER role
        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains("TEACHER")) {
            response.sendRedirect("request-list");
            return;
        }

        try {
            request.setAttribute("rooms", roomDAO.getAllActiveRooms());
            request.setAttribute("categories", assetCategoryDAO.getAllActiveCategories());
            request.getRequestDispatcher("/views/allocation/teacher/add-request.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            System.err.println("Error loading add request page: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải trang. Vui lòng thử lại!");
            request.getRequestDispatcher("/views/allocation/teacher/request-list.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get user information from session
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        // Check authentication
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            // Get data from Request Form
            long roomId = Long.parseLong(request.getParameter("requestedRoomId"));
            String purpose = request.getParameter("purpose");

            // Get list data
            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] notes = request.getParameterValues("notes");

            // Save to database
            boolean success = createAssetRequest(
                    currentUser.getUserId(),
                    roomId,
                    purpose,
                    categoryIds,
                    quantities,
                    notes);

            if (success) {
                //Send Notification to Board
                List<Long> boardIds = userDAO.getIdsByRole("BOARD");
                NotificationEndPoint.sendToUsers(boardIds, "Có phiếu yêu cầu mới từ: " + currentUser.getFullName());
                
                System.out.println("[AddRequest] User " + currentUser.getUserId() + " created new request successfully");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list?msg=success");
            } else {
                request.setAttribute("error", "Không thể tạo yêu cầu. Vui lòng thử lại!");
                doGet(request, response);
            }

        } catch (Exception e) {
            System.err.println("[AddRequest] Error creating request: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Dữ liệu nhập vào không hợp lệ!");
            doGet(request, response);
        }
    }

    public boolean createAssetRequest(long userId, long roomId, String purpose,
            String[] catIds, String[] qtys, String[] notes) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            // Start Transaction
            conn.setAutoCommit(false);

            // Create AssetRequest
            AssetRequest request = new AssetRequest();
            request.setTeacherId(userId);
            request.setRequestedRoomId(roomId);
            request.setPurpose(purpose);
            request.setStatus("WAITING_BOARD"); // Default Status

            // Create RequestCode
            String requestCode = "REQ-" + System.currentTimeMillis();
            request.setRequestCode(requestCode);

            // Insert to table AssetRequest
            long requestId = requestDAO.insert(conn, request);

            // Insert table AssetRequestItems
            if (requestId > 0 && catIds != null) {
                for (int i = 0; i < catIds.length; i++) {
                    AssetRequestItem item = new AssetRequestItem();
                    item.setRequestId(requestId);
                    item.setCategoryId(Long.parseLong(catIds[i]));
                    item.setQuantity(Integer.parseInt(qtys[i]));
                    item.setNote(notes[i]);

                    // save data to table AssetRequestItem
                    requestItemDAO.insert(conn, item);
                }
            }

            conn.commit(); // End Transaction
            return true;

        } catch (SQLException e) {
            System.err.println("[AddRequest] Database error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();    //Rollback if have error
                } catch (SQLException ex) {
                    System.err.println("[AddRequest] Rollback failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("[AddRequest] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
