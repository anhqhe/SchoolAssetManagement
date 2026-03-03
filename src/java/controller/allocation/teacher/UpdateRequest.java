/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AssetCategoryDAO;
import controller.allocation.notification.NotificationEndPoint;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.UserDAO;
import dao.allocation.RoomDAO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.User;
import model.allocation.AssetRequestItem;
import util.DBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leo
 */
@WebServlet(name = "UpdateRequest", urlPatterns = {"/teacher/update-request"})
public class UpdateRequest extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();
    private final AssetRequestDAO requestDAO = new AssetRequestDAO();
    private final AssetRequestItemDAO itemDAO = new AssetRequestItemDAO();
    private final UserDAO userDAO = new UserDAO();

    private static final Logger LOGGER = Logger.getLogger(UpdateRequest.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        long requestId;
        try {
            requestId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid request ID: {0}", idParam);

            session.setAttribute("type", "error");
            session.setAttribute("message", "ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        try {
            AssetRequestDTO req = requestDAO.findById(requestId);

            if (req == null) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Không tìm thấy yêu cầu");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list");
                return;
            }

            if (req.getTeacherId() != currentUser.getUserId()) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Bạn không có quyền chỉnh sửa");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list");
                return;
            }

            if (!"WAITING_BOARD".equals(req.getStatus())) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Yêu cầu không thể chỉnh sửa");
                response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);
                return;
            }

            List<AssetRequestItemDTO> itemList = itemDAO.findByRequestId(requestId);

            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("rooms", roomDAO.getAllActiveRooms());
            request.setAttribute("categories", categoryDAO.getAllActiveCategories());

            request.getRequestDispatcher("/views/allocation/teacher/request-form.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading update request page", e);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Đã xảy ra lỗi!");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
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

        try {
            long requestId = validateId(request.getParameter("requestId"),
                    "ID yêu cầu");

            AssetRequestDTO req = requestDAO.findById(requestId);

            if (req == null) {
                throw new IllegalArgumentException("Không tìm thấy yêu cầu.");
            }

            if (req.getTeacherId() != currentUser.getUserId()) {
                throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa yêu cầu này.");
            }

            if (!"WAITING_BOARD".equals(req.getStatus())) {
                throw new IllegalArgumentException("Yêu cầu không thể chỉnh sửa.");
            }

            long roomId = validateId(request.getParameter("requestedRoomId"),
                    "Phòng yêu cầu");
            String purpose = request.getParameter("purpose");
            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] notes = request.getParameterValues("notes");

            if (purpose == null || purpose.isBlank()) {
                throw new IllegalArgumentException("Mục đích sử dụng không được để trống.");
            }
            validateItems(categoryIds, quantities);

            boolean success = updateAssetRequest(requestId, roomId, purpose, categoryIds, quantities, notes);

            if (!success) {
                throw new RuntimeException("Cập nhật thất bại.");
            }

            // Notify board about updated request
            List<Long> boardIds = userDAO.getIdsByRole("BOARD");
            try {
                NotificationEndPoint.sendToUsers(boardIds,
                        "Yêu cầu đã được cập nhật",
                        currentUser.getFullName() + " đã cập nhật yêu cầu: " + req.getRequestCode(),
                        "ASSET_REQUEST",
                        requestId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error sending notification", e);
            }

            session.setAttribute("type", "success");
            session.setAttribute("message", "Cập nhật yêu cầu thành công!");
            response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId);

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error: {0}", e.getMessage());

            session.setAttribute("type", "error");
            session.setAttribute("message", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "System error while updating request", e);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Đã xảy ra lỗi hệ thống.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
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

    private boolean updateAssetRequest(long requestId, long roomId, String purpose,
            String[] categoryIds, String[] quantities, String[] notes) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            boolean updated = requestDAO.updateRequest(conn, requestId, roomId, purpose);
            if (!updated) {
                conn.rollback();
                return false;
            }

            itemDAO.deleteByRequestId(conn, requestId);

            for (int i = 0; i < categoryIds.length; i++) {
                AssetRequestItem item = new AssetRequestItem();
                item.setRequestId(requestId);
                item.setCategoryId(Long.parseLong(categoryIds[i]));
                item.setQuantity(Integer.parseInt(quantities[i]));

                String note = (notes != null && notes.length > i) ? notes[i] : null;
                item.setNote(note);
                itemDAO.insert(conn, item);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating request", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed while updating request", ex);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while updating request", e);

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", ex);
                }
            }

            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE,
                            "Error closing connection in UpdateRequest", e);
                }
            }
        }
    }
}
