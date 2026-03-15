/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import controller.allocation.notification.NotificationEndPoint;
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
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(AddRequest.class.getName());

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

        try {
            request.setAttribute("rooms", roomDAO.getActiveRoomsByTeacherId(currentUser.getUserId()));
            request.setAttribute("categories", assetCategoryDAO.getAllActiveCategories());
            request.getRequestDispatcher("/views/allocation/teacher/request-form.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Database error while loading request form", e);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Đã xảy ra lỗi. Vui lòng thử lại!");
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
            long roomId = validateId(request.getParameter("requestedRoomId"), "Phòng");
            String purpose = request.getParameter("purpose");

            if (!roomDAO.isTeacherAssignedToRoom(currentUser.getUserId(), roomId)) {
                throw new IllegalArgumentException("Phòng không thuộc quyền sử dụng.");
            }

            // Get list data
            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] notes = request.getParameterValues("notes");

            if (purpose == null || purpose.isBlank()) {
                throw new IllegalArgumentException("Purpose is required");
            }
            validateItems(categoryIds, quantities);

            // Save to database
            long requestId = createAssetRequest(
                    currentUser.getUserId(),
                    roomId,
                    purpose,
                    categoryIds,
                    quantities,
                    notes);

            if (requestId <= 0) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Không thể tạo yêu cầu. Vui lòng thử lại!");
                response.sendRedirect(request.getContextPath() + "/teacher/add-request");
                return;
            }

            //success
            //Send Notification to Boards
            List<Long> boardIds = userDAO.getIdsByRole("BOARD");
            try {
                NotificationEndPoint.sendToUsers(boardIds,
                        "Yêu cầu mới cần phê duyệt",
                        "Có phiếu yêu cầu mới từ: " + currentUser.getFullName(),
                        "ASSET_REQUEST",
                        requestId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error sending notification", e);
            }

            session.setAttribute("type", "success");
            session.setAttribute("message", "Gửi yêu cầu tài sản thành công!");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error", e);
            session.setAttribute("type", "error");
            session.setAttribute("message", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacher/add-request");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error in AddRequest", ex);
            response.sendRedirect(request.getContextPath() + "/views/common/500.jsp");
        }
    }

    private long validateId(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " không hợp lệ.");
        }

        try {
            long id = Long.parseLong(value);
            if (id <= 0) {
                throw new IllegalArgumentException(fieldName + " phải lớn hơn 0.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số.");
        }
    }

    private void validateItems(String[] categoryIds, String[] quantities) {

        if (categoryIds == null || quantities == null
                || categoryIds.length == 0
                || categoryIds.length != quantities.length) {
            throw new IllegalArgumentException("Danh sách tài sản không hợp lệ!");
        }

        for (int i = 0; i < categoryIds.length; i++) {

            if (categoryIds[i] == null || categoryIds[i].isBlank()) {
                throw new IllegalArgumentException("Danh mục tài sản không hợp lệ.");
            }

            try {
                long catId = Long.parseLong(categoryIds[i]);
                if (catId <= 0) {
                    throw new IllegalArgumentException("Danh mục tài sản không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Danh mục tài sản không hợp lệ.");
            }

            if (quantities[i] == null || quantities[i].isBlank()) {
                throw new IllegalArgumentException("Số lượng tài sản không được để trống.");
            }

            try {
                int qty = Integer.parseInt(quantities[i]);
                if (qty <= 0) {
                    throw new IllegalArgumentException("Số lượng tài sản phải lớn hơn 0.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Số lượng tài sản phải là số.");
            }
        }
    }

    //Save to database
    public long createAssetRequest(long userId, long roomId, String purpose,
            String[] catIds, String[] qtys, String[] notes) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            // Start Transaction
            conn.setAutoCommit(false);

            if (catIds == null || qtys == null || catIds.length == 0) {
                return -1;
            }

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

            if (requestId <= 0) {
                conn.rollback();
                return -1;
            }

            // Insert table AssetRequestItems
            for (int i = 0; i < catIds.length; i++) {
                AssetRequestItem item = new AssetRequestItem();
                item.setRequestId(requestId);
                item.setCategoryId(Long.parseLong(catIds[i]));
                item.setQuantity(Integer.parseInt(qtys[i]));

                String note = (notes != null && notes.length > i) ? notes[i] : null;
                item.setNote(note);

                // save data to table AssetRequestItem
                requestItemDAO.insert(conn, item);
            }

            conn.commit(); // End Transaction
            return requestId;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", ex);
                }
            }

            LOGGER.log(Level.SEVERE,
                    "Unexpected error while creating asset request", e);
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE,
                            "Error closing connection in AddRequest", e);
                }
            }
        }
    }
}
